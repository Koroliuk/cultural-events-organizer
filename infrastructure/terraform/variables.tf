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

variable "postgres_db_port" {
  default = 5432
}

variable "rds_instance_type" {
  default = "db.t2.micro"
}

variable "rds_identifier" {
  description = "db identifier"
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

variable "rds_database_name" {
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

variable "ems_service_port" {
  default = 8080
}

variable "jwt_generation_signature_secret" {
  default = "349thuhfeirugherultglsdjgl4i7rgwefgsjhgfw76dfkadgayug"
}

variable "event_management_service_image" {
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

variable "logs_retention_in_days" {
  type    = number
  default = 90
}
