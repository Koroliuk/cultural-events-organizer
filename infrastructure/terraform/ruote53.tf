resource "aws_route53_zone" "primary" {
  name = "cultural-events-organizer.com"
}

resource "aws_route53_record" "A" {
  name    = "cultural-events-organizer.com"
  type    = "A"
  zone_id = aws_route53_zone.primary.id

  alias {
    evaluate_target_health = true
    name                   = aws_alb.alb.dns_name
    zone_id                = aws_alb.alb.zone_id
  }
}
