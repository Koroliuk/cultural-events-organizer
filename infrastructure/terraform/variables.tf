variable "infrastructure_version" {
  type    = number
  default = 1
}

variable "region" {
  default = "eu-central-1"
}

variable "vpc_cidr" {
  default = "10.0.0.0/16"
}

variable "rt_wide_route" {
  default = "0.0.0.0/0"
}

variable "subnet_count" {
  default = 2
}

variable "rds_port" {
  default = 5432
}

variable "rds_instance_class" {
  default = "db.t2.micro"
}

variable "rds_identifier" {
  default     = "postgres"
}

variable "rds_storage_type" {
  default = "gp2"
}

variable "rds_allocated_storage" {
  default = 5
}

variable "rds_engine" {
  default = "postgres"
}
variable "rds_engine_version" {
  default = "12"
}

variable "rds_db_name" {
  default = "cultural_events_organizer_db"
}

variable "rds_username" {
  default = "db_user"
}

variable "rds_password" {
  default = "paSSw0rd"
}

variable "rds_final_snapshot_identifier" {
  default = "cultural-events-organizer-final"
}

variable "container_name" {
  default = "event-management-service"
}

variable "ems_service_port" {
  default = 80
}

variable "jwt_generation_signature_secret" {
  default = "349thuhfeirugherultglsdjgl4i7rgwefgsjhgfw76dfkadgayug"
}

variable "event_management_service_image_url" {
  default = "docker.io/koroliuk/event-management-service:latest"
}

variable "ecs_task_definition_name" {
  type    = string
  default = "event-management-service"
}

variable "health_check_path" {
  default = "/health"
}

variable "cloudwatch_group" {
  type    = string
  default = "event-management-service"
}

variable "monitor_schedule_expression" {
  default = "rate(15 minutes)"
}

variable "monitor_lambda_source_code_path" {
  default = "../../event-monitoring-service/build/libs/event-monitoring-service-0.1-all.jar"
}

variable "java_runtime" {
  default = "java17"
}

variable "cleanup_lambda_schedule_expression" {
  default = "rate(1 day)"
}

variable "cleanup_lambda_source_code_path" {
  default = "../../notification-cleanup-service/build/libs/notification-cleanup-service-0.1-all.jar"
}

variable "email_sender_lambda_source_code_path" {
  default = "../../email-sender-service/build/libs/email-sender-service-0.1-all.jar"
}

variable "retention_in_days_logs" {
  type    = number
  default = 30
}

variable "email_send_from" {
  default = "tt6761826@gmail.com"
}

variable "smtp_server" {
  default = "smtp.elasticemail.com"
}

variable "smtp_port" {
  default = "2525"
}

variable "smtp_username" {
  default = "tt6761826@gmail.com"
}

variable "smtp_password" {
  default = "94033C90C1FD8685A14087CE4E8A49927E16"
}