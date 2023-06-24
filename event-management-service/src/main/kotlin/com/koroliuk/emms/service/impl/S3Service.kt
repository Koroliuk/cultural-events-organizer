package com.koroliuk.emms.service.impl

import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest
import java.io.File
import java.nio.file.Paths
import java.time.Duration

@Singleton
class S3Service {

    @Value("\${aws.access-key}")
    lateinit var accessKey: String

    @Value("\${aws.secret-key}")
    lateinit var secretKey: String

    @Value("\${aws.region}")
    lateinit var region: String

    @Value("\${aws.s3.bucket-name}")
    lateinit var bucketName: String

    private val s3Client: S3Client
        get() {
            val credentials = AwsBasicCredentials.create(accessKey, secretKey)
            return S3Client.builder()
                .credentialsProvider { credentials }
                .region(Region.of(region))
                .build()
        }

    private val s3Presigner: S3Presigner
        get() {
            val credentials = AwsBasicCredentials.create(accessKey, secretKey)
            return S3Presigner.builder()
                .credentialsProvider { credentials }
                .region(Region.of(region))
                .build()
        }

    fun uploadFile(file: File, key: String) {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()
        s3Client.putObject(putObjectRequest, Paths.get(file.absolutePath))
    }

    fun deleteFile(s3Key: String) {
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .build()

        s3Client.deleteObject(deleteObjectRequest)
    }

    fun generatePresignedUrl(s3Key: String): String {
        val getObjectRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(60)) // Set the duration for how long the URL is valid
            .getObjectRequest { b -> b.bucket(bucketName).key(s3Key) }
            .build()

        val presignedGetObjectRequest: PresignedGetObjectRequest = s3Presigner.presignGetObject(getObjectRequest)

        return presignedGetObjectRequest.url().toString()
    }

}
