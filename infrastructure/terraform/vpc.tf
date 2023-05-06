locals {
  subnets = flatten([aws_subnet.public_subnets.*.id])
}

data "aws_availability_zones" "azs" {}

resource "aws_vpc" "vpc" {
  cidr_block            = var.vpc_cidr
  enable_dns_hostnames  = true
  enable_dns_support    = true

  tags = {
    Name = "cultural-events-organizer-vpc"
  }
}

resource "aws_internet_gateway" "internet_gateway" {
  vpc_id = aws_vpc.vpc.id
  tags = {
    Name = "cultural-events-organizer-igw"
  }
}

resource "aws_route_table" "rt_public" {
  vpc_id = aws_vpc.vpc.id

  route {
    cidr_block = var.rt_wide_route
    gateway_id = aws_internet_gateway.internet_gateway.id
  }

  tags = {
    Name = "cultural-events-organizer-rt-public"
  }
}

resource "aws_default_route_table" "rt_private_default" {
  default_route_table_id = aws_vpc.vpc.default_route_table_id

  tags = {
    Name = "cultural-events-organizer-rt-private-default"
  }
}

resource "aws_subnet" "public_subnets" {
  count = var.subnet_count
  cidr_block = "10.0.${var.subnet_count * (var.infrastructure_version - 1) + count.index + 1}.0/24"
  vpc_id = aws_vpc.vpc.id
  availability_zone = data.aws_availability_zones.azs.names[count.index]
  map_public_ip_on_launch = true

  tags = {
    Name = "cultural-events-organizer-tf-public-${count.index + 1}"
  }
}

resource "aws_subnet" "private_subnets" {
  count             = var.subnet_count
  cidr_block        = "10.0.${var.subnet_count * (var.infrastructure_version - 1) + count.index + 1 + var.subnet_count}.0/24"
  vpc_id            = aws_vpc.vpc.id
  availability_zone = data.aws_availability_zones.azs.names[count.index]

  tags = {
    Name = "cultural-events-organizer-tf-private-${count.index + 1}"
  }
}

resource "aws_route_table_association" "public-rt-association" {
  count = var.subnet_count
  route_table_id = aws_route_table.rt_public.id
  subnet_id = aws_subnet.public_subnets.*.id[count.index]
}

resource "aws_route_table_association" "private-rt-association" {
  count = var.subnet_count
  route_table_id = aws_route_table.rt_public.id
  subnet_id = aws_subnet.private_subnets.*.id[count.index]
}
