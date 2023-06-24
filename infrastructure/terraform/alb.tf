locals {
  aws_alb_name              = "application-load-balancer"
  aws_alb_target_group_name = "alb-target-group"
  health_check_path         = "/health"
}

resource "aws_alb" "application_load_balancer" {
  load_balancer_type = "application"
  name               = local.aws_alb_name
  subnets            = aws_subnet.public_subnets.*.id
  security_groups    = [aws_security_group.alb_security_group.id]
  tags               = {
    name : local.aws_alb_name,
    service-name : local.service_name
  }
}

resource "aws_alb_target_group" "apl_target_group" {
  name        = local.aws_alb_target_group_name
  port        = 80
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = aws_vpc.vpc.id

  health_check {
    healthy_threshold   = "3"
    interval            = "30"
    protocol            = "HTTP"
    matcher             = "200"
    timeout             = "6"
    path                = local.health_check_path
    unhealthy_threshold = "2"
  }
  tags = {
    name : local.aws_alb_target_group_name,
    service-name : local.service_name
  }
}

resource "aws_alb_listener" "alb_listener" {
  load_balancer_arn = aws_alb.application_load_balancer.arn
  port              = 80
  protocol          = "HTTP"
  default_action {
    target_group_arn = aws_alb_target_group.apl_target_group.arn
    type             = "forward"
  }
  tags = {
    service-name : local.service_name
  }
}
