locals {
  public_security_group_name = "public-security-group-default"
  alb_security_group_name    = "alb-security-group"
  ecs_security_group_name    = "ecs-security-group"
  rds_security_group_name    = "rds-security-group"
  lambda_security_group_name = "lambda-security-group"
}

resource "aws_security_group" "public_security_group" {
  name   = local.public_security_group_name
  vpc_id = aws_vpc.vpc.id
}

resource "aws_security_group" "alb_security_group" {
  name   = local.alb_security_group_name
  vpc_id = aws_vpc.vpc.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "ecs_security_group" {
  name   = local.ecs_security_group_name
  vpc_id = aws_vpc.vpc.id

  ingress {
    from_port       = var.ems_service_port
    to_port         = var.ems_service_port
    protocol        = "tcp"
    cidr_blocks     = ["0.0.0.0/0"]
    security_groups = [aws_security_group.alb_security_group.id]
  }

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "rds_security_group" {
  name   = local.rds_security_group_name
  vpc_id = aws_vpc.vpc.id

  ingress {
    protocol        = "tcp"
    from_port       = var.rds_port
    to_port         = var.rds_port
    cidr_blocks     = ["0.0.0.0/0"]
    security_groups = [
      aws_security_group.alb_security_group.id,
      aws_security_group.lambda_security_group.id
    ]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "lambda_security_group" {
  name   = local.lambda_security_group_name
  vpc_id = aws_vpc.vpc.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
