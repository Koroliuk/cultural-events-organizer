locals {
  sqs_name                      = "email-queue"
  email_sender_lambda_name      = "ess-email-sender"
  lambda_sqs_policy             = "lambda-sqs-policy"
}

resource "aws_sqs_queue" "email_queue" {
  name                       = "${local.sqs_name}.fifo"
  fifo_queue                 = true
  visibility_timeout_seconds = 900
  tags                       = {
    name : local.sqs_name,
    service-name : local.service_name
  }
}

resource "aws_lambda_function" "email_sender_lambda" {
  function_name    = local.email_sender_lambda_name
  filename         = var.email_sender_lambda_source_code_path
  role             = aws_iam_role.email_sender_role_name.arn
  handler          = "com.ess.FunctionRequestHandler::execute"
  source_code_hash = filebase64sha256(var.email_sender_lambda_source_code_path)
  runtime          = var.java_runtime
  timeout          = 900
  memory_size      = 1024
  environment {
    variables = {
      EMAIL_SEND_FROM = var.email_send_from
      SMTP_SERVER = var.smtp_server
      SMTP_PORT = var.smtp_port
      SMTP_USERNAME = var.smtp_username
      SMTP_PASSWORD = var.smtp_password
    }
  }
  tags             = {
    name : local.email_sender_lambda_name,
    service-name : local.service_name
  }
}

resource "aws_lambda_event_source_mapping" "sqs_source_mapping" {
  event_source_arn = aws_sqs_queue.email_queue.arn
  function_name    = aws_lambda_function.email_sender_lambda.function_name
}

resource "aws_cloudwatch_log_group" "ess_log_group" {
  name              = "/fargate/service/${local.email_sender_lambda_name}"
  retention_in_days = var.retention_in_days_logs
}

