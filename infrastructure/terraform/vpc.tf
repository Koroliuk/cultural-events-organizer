locals {
  subnets = flatten([aws_subnet.public_subnets.*.id])
}

data "aws_availability_zones" "azs" {}

resource "aws_vpc" "vpc" {
  cidr_block            = var.vpc_cidr
  enable_dns_hostnames  = true
  enable_dns_support    = true

  tags = {
    service-name : local.service_name
  }
}

resource "aws_internet_gateway" "internet_gateway" {
  vpc_id = aws_vpc.vpc.id
  tags = {
    service-name : local.service_name
  }
}

resource "aws_route_table" "public_route_table" {
  vpc_id = aws_vpc.vpc.id
  route {
    cidr_block = var.rt_wide_route
    gateway_id = aws_internet_gateway.internet_gateway.id
  }
  tags = {
    service-name : local.service_name
  }
}

resource "aws_default_route_table" "private_route_table" {
  default_route_table_id = aws_vpc.vpc.default_route_table_id
  tags = {
    service-name : local.service_name
  }
}

resource "aws_subnet" "public_subnets" {
  count = var.subnet_count
  cidr_block = "10.0.${var.subnet_count * (var.infrastructure_version - 1) + count.index + 1}.0/24"
  vpc_id = aws_vpc.vpc.id
  availability_zone = data.aws_availability_zones.azs.names[count.index]
  map_public_ip_on_launch = true
  tags = {
    service-name : local.service_name
  }
}

resource "aws_subnet" "private_subnets" {
  count             = var.subnet_count
  cidr_block        = "10.0.${var.subnet_count * (var.infrastructure_version - 1) + count.index + 1 + var.subnet_count}.0/24"
  vpc_id            = aws_vpc.vpc.id
  availability_zone = data.aws_availability_zones.azs.names[count.index]
  tags = {
    service-name : local.service_name
  }
}

resource "aws_route_table_association" "public_route_table_association" {
  count = var.subnet_count
  route_table_id = aws_route_table.public_route_table.id
  subnet_id = aws_subnet.public_subnets.*.id[count.index]
}

resource "aws_route_table_association" "private_route_table_association" {
  count = var.subnet_count
  route_table_id = aws_route_table.public_route_table.id
  subnet_id = aws_subnet.private_subnets.*.id[count.index]
}
