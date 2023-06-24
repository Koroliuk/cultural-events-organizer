locals {
  db_subnet_group = "postgres-db-subnet-group"
}

resource "aws_db_subnet_group" "db_subnet_group" {
  name       = local.db_subnet_group
  subnet_ids = aws_subnet.private_subnets.*.id
  tags       = {
    name : local.db_subnet_group,
    service-name : local.service_name
  }
}

resource "aws_db_instance" "rds_instance" {
  identifier                  = var.rds_identifier
  allocated_storage           = var.rds_allocated_storage
  storage_type                = var.rds_storage_type
  multi_az                    = false
  engine                      = var.rds_engine
  engine_version              = var.rds_engine_version
  instance_class              = var.rds_instance_class
  db_name                     = var.rds_db_name
  username                    = var.rds_username
  password                    = var.rds_password
  port                        = var.rds_port
  parameter_group_name        = "default.postgres12"
  publicly_accessible         = false
  allow_major_version_upgrade = false
  auto_minor_version_upgrade  = false
  apply_immediately           = true
  storage_encrypted           = false
  skip_final_snapshot         = true
  final_snapshot_identifier   = var.rds_final_snapshot_identifier
  vpc_security_group_ids      = [
    aws_security_group.rds_security_group.id, aws_security_group.ecs_security_group.id
  ]
  db_subnet_group_name = aws_db_subnet_group.db_subnet_group.name

  tags = {
    service-name : local.service_name
  }
}
