resource "aws_s3_bucket" "data_bucket" {
  bucket = "emms-events-media-storage"
}

resource "aws_s3_bucket_public_access_block" "app" {
  bucket = aws_s3_bucket.data_bucket.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}
