locals {
  cleanup_lambda_name = "ncs-notification-cleanup"
}

resource "aws_lambda_function" "notification_cleanup_lambda" {
  function_name    = local.cleanup_lambda_name
  filename         = var.cleanup_lambda_source_code_path
  source_code_hash = filebase64sha256(var.cleanup_lambda_source_code_path)
  handler          = "com.koroliuk.ncs.CleanupLambdaHandler::execute"
  role             = aws_iam_role.lambda_execution_role.arn
  runtime          = var.java_runtime
  timeout          = 900
  memory_size      = 1024
  environment {
    variables = {
      DB_USER           = aws_db_instance.rds_instance.username
      DB_PASSWORD       = aws_db_instance.rds_instance.password
      POSTGRES_ENDPOINT = aws_db_instance.rds_instance.endpoint
      POSTGRES_DATABASE = aws_db_instance.rds_instance.db_name
    }
  }
  vpc_config {
    subnet_ids         = aws_subnet.private_subnets.*.id
    security_group_ids = [aws_security_group.lambda_security_group.id]
  }
  tags = {
    name : local.cleanup_lambda_name,
    service-name : local.service_name
  }
}

resource "aws_lambda_permission" "allow_cloudwatch_to_invoke" {
  statement_id  = "CloudWatchInvoke"
  action        = "lambda:InvokeFunction"
  principal  = "events.amazonaws.com"
  function_name = aws_lambda_function.notification_cleanup_lambda.function_name
  source_arn = aws_cloudwatch_event_rule.every_day.arn
}

resource "aws_cloudwatch_event_rule" "every_day" {
  name                = "daily"
  schedule_expression = var.cleanup_lambda_schedule_expression
}

resource "aws_cloudwatch_event_target" "cleanup_lambda_every_day" {
  target_id = aws_lambda_function.notification_cleanup_lambda.id
  rule = aws_cloudwatch_event_rule.every_day.name
  arn  = aws_lambda_function.notification_cleanup_lambda.arn
}
