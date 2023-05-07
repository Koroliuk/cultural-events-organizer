resource "aws_lambda_function" "event_monitor" {
  function_name    = "event_monitor"
  filename         = "../../event-monitoring-service/build/libs/event-monitoring-service-0.1-all.jar"
  source_code_hash = filebase64sha256("../../event-monitoring-service/build/libs/event-monitoring-service-0.1-all.jar")
  handler          = "com.koroliuk.ems.FunctionRequestHandler"
  role             = aws_iam_role.lambda_execution_role.arn
  runtime          = "java17"
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
    security_group_ids = [aws_security_group.lambda_sg.id]
  }
}

resource "aws_lambda_permission" "allow_cloudwatch_to_invoke1" {
  function_name = aws_lambda_function.event_monitor.function_name
  statement_id  = "CloudWatchInvoke"
  action        = "lambda:InvokeFunction"

  source_arn = aws_cloudwatch_event_rule.every_day.arn
  principal  = "events.amazonaws.com"
}

resource "aws_cloudwatch_event_target" "invoke_lambda1" {
  rule = aws_cloudwatch_event_rule.every_day.name
  arn  = aws_lambda_function.event_monitor.arn
}
