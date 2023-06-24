locals {
  component_name        = "event-management-service"
  ecs_emms_cluster_name = "${local.component_name}-cluster"
  file_task_path        = "./ecs_emms_task.tmpl"
}

resource "aws_ecs_cluster" "emms_ecs_cluster" {
  name = local.ecs_emms_cluster_name
  tags = {
    name : local.ecs_emms_cluster_name,
    service-name : local.service_name
  }
}

data "template_file" "emms_task_template" {
  template = file(local.file_task_path)
  vars     = {
    REPOSITORY_URL                 = var.event_management_service_image_url
    REGION                         = var.region,
    JWT_GENERATOR_SIGNATURE_SECRET = var.jwt_generation_signature_secret
    QUEUE_URL                      = aws_sqs_queue.email_queue.id
    DB_USER                        = aws_db_instance.rds_instance.username
    DB_PASSWORD                    = aws_db_instance.rds_instance.password
    POSTGRES_ENDPOINT              = aws_db_instance.rds_instance.endpoint
    POSTGRES_DATABASE              = aws_db_instance.rds_instance.db_name
    CLOUDWATCH_GROUP               = aws_cloudwatch_log_group.emms_task_log_group.name,
  }
}

resource "aws_cloudwatch_log_group" "emms_task_log_group" {
  name              = "/fargate/service/${local.ecs_emms_cluster_name}"
  retention_in_days = var.retention_in_days_logs
}

resource "aws_ecs_task_definition" "emms_task" {
  family                   = local.component_name
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = 1024
  memory                   = 2048
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn
  container_definitions    = data.template_file.emms_task_template.rendered
  tags                     = {
    name : local.component_name,
    service-name : local.service_name
  }
}

resource "aws_ecs_service" "emms_ecs_service" {
  name                               = local.component_name
  cluster                            = aws_ecs_cluster.emms_ecs_cluster.id
  task_definition                    = aws_ecs_task_definition.emms_task.arn
  desired_count                      = 5
  deployment_minimum_healthy_percent = 45
  deployment_maximum_percent         = 210
  launch_type                        = "FARGATE"
  scheduling_strategy                = "REPLICA"
  enable_ecs_managed_tags            = true
  network_configuration {
    subnets          = aws_subnet.public_subnets.*.id
    assign_public_ip = true
    security_groups  = [
      aws_security_group.ecs_security_group.id
    ]
  }
  load_balancer {
    container_name   = var.container_name
    container_port   = var.ems_service_port
    target_group_arn = aws_alb_target_group.apl_target_group.id
  }
  lifecycle {
    ignore_changes = [task_definition, desired_count]
  }
}
