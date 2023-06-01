resource "aws_ecs_cluster" "ems-ecs-cluster" {
  name = "event-management-service-cluster"

  tags = {
    name = "event-management-service"
  }
}

resource "aws_ecs_task_definition" "task_definition" {
  family = var.ecs_task_definition_name
  execution_role_arn = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn = aws_iam_role.ecs_task_role.arn
  requires_compatibilities = ["FARGATE"]
  network_mode = "awsvpc"
  cpu = 1024
  memory = 2048
  container_definitions = data.template_file.task_definition_template.rendered
}

data "template_file" "task_definition_template" {
  template = file("task_definition.json.tpl")
  vars = {
    REPOSITORY_URL = var.event_management_service_image
    DB_USER = aws_db_instance.rds_instance.username
    DB_PASSWORD = aws_db_instance.rds_instance.password
    POSTGRES_ENDPOINT = aws_db_instance.rds_instance.endpoint
    POSTGRES_DATABASE = aws_db_instance.rds_instance.db_name
    REGION = var.region,
    JWT_GENERATOR_SIGNATURE_SECRET = var.jwt_generation_signature_secret
    CLOUDWATCH_GROUP = aws_cloudwatch_log_group.logs.name,
    QUEUE_URL = aws_sqs_queue.email_queue.id
  }
}

resource "aws_cloudwatch_log_group" "logs" {
  name              = "/fargate/service/${var.cloudwatch_group}"
  retention_in_days = var.logs_retention_in_days
  tags = {
    service = var.cloudwatch_group
  }
}

resource "aws_ecs_service" "ems-service" {
  name = "event-management-service"
  cluster = aws_ecs_cluster.ems-ecs-cluster.id
  task_definition = aws_ecs_task_definition.task_definition.arn
  desired_count = 2
  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent = 200
  launch_type = "FARGATE"
  scheduling_strategy = "REPLICA"
  enable_ecs_managed_tags = true


  network_configuration {
    security_groups = [
      aws_security_group.ecs_sg.id]
    subnets = aws_subnet.public_subnets.*.id
    assign_public_ip = true
  }

  load_balancer {
    container_name = "event-management-service"
    container_port = var.ems_service_port
    target_group_arn = aws_alb_target_group.target_group.id
  }

  depends_on = [
    aws_alb_listener.fp-alb-listener
  ]

  lifecycle {
    ignore_changes = [
      task_definition, desired_count]
  }
}
