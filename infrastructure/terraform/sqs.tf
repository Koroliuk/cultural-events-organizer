resource "aws_sqs_queue" "email_queue" {
  name = "emailQueue.fifo"
  fifo_queue = true
  visibility_timeout_seconds = 900
}

resource "aws_lambda_function" "sqs_trigger_lambda" {
  filename      = "../../email-sender-service/build/libs/email-sender-service-0.1-all.jar"
  function_name = "sqsTriggerLambda"
  role          = aws_iam_role.lambda_sqs_role.arn
  handler       = "com.ess.FunctionRequestHandler::execute"
  source_code_hash = filebase64sha256("../../email-sender-service/build/libs/email-sender-service-0.1-all.jar")
  runtime          = "java17"
  timeout          = 900
  memory_size      = 1024
}

resource "aws_lambda_event_source_mapping" "sqs_source_mapping" {
  event_source_arn = aws_sqs_queue.email_queue.arn
  function_name    = aws_lambda_function.sqs_trigger_lambda.function_name
}

resource "aws_iam_role" "lambda_sqs_role" {
  name = "lambda-sqs-role"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy" "lambda_sqs_policy" {
  name = "lambda-sqs-policy"
  role = aws_iam_role.lambda_sqs_role.id

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "arn:aws:logs:*:*:*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "sqs:DeleteMessage",
        "sqs:GetQueueAttributes",
        "sqs:ReceiveMessage"
      ],
      "Resource": "${aws_sqs_queue.email_queue.arn}"
    }
  ]
}
EOF
}
