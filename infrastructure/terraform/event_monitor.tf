locals {
  monitor_lambda_name     = "ems-monitor"
  monitor_event_rule_name = "monitor_rule_every_fifteen_minutes"
}

resource "aws_lambda_function" "monitor_lambda" {
  function_name    = local.monitor_lambda_name
  filename         = var.monitor_lambda_source_code_path
  source_code_hash = filebase64sha256(var.monitor_lambda_source_code_path)
  handler          = "com.koroliuk.ems.MonitorLambdaHandler"
  role             = aws_iam_role.lambda_execution_role.arn
  runtime          = var.java_runtime
  timeout          = 900
  memory_size      = 1024
  environment {
    variables = {
      POSTGRES_ENDPOINT = aws_db_instance.rds_instance.endpoint
      POSTGRES_DATABASE = aws_db_instance.rds_instance.db_name
      DB_USER           = aws_db_instance.rds_instance.username
      DB_PASSWORD       = aws_db_instance.rds_instance.password
    }
  }
  vpc_config {
    subnet_ids         = aws_subnet.private_subnets.*.id
    security_group_ids = [aws_security_group.lambda_security_group.id]
  }
  tags = {
    name : local.monitor_lambda_name,
    service-name : local.service_name
  }
}

resource "aws_lambda_permission" "allow_cloudwatch_to_invoke1" {
  statement_id  = "CloudWatchInvoke"
  action        = "lambda:InvokeFunction"
  principal     = "events.amazonaws.com"
  function_name = aws_lambda_function.monitor_lambda.function_name
  source_arn    = aws_cloudwatch_event_rule.every_day.arn
}

resource "aws_cloudwatch_event_rule" "monitor_event_rule" {
  name                = local.monitor_event_rule_name
  schedule_expression = var.monitor_schedule_expression
}

resource "aws_cloudwatch_event_target" "monitor_lambda_every_fifteen_minutes" {
  target_id = aws_lambda_function.monitor_lambda.id
  rule      = aws_cloudwatch_event_rule.every_day.name
  arn       = aws_lambda_function.monitor_lambda.arn
}

resource "aws_cloudwatch_log_group" "ems_log_group" {
  name              = "/fargate/service/${local.monitor_lambda_name}"
  retention_in_days = var.retention_in_days_logs
}
