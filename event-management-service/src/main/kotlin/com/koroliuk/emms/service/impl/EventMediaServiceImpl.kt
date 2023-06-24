package com.koroliuk.emms.service.impl

import com.koroliuk.emms.controller.response.EventMediaPresignedUrl
import com.koroliuk.emms.controller.response.GetEventMediaResponse
import com.koroliuk.emms.model.event.EventMedia
import com.koroliuk.emms.repository.event.EventMediaRepository
import com.koroliuk.emms.service.EventMediaService
import com.koroliuk.emms.service.EventService
import io.micronaut.data.model.Pageable
import io.micronaut.http.multipart.CompletedFileUpload
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.io.File
import java.io.FileOutputStream

@Singleton
class EventMediaServiceImpl(
    @Inject private val eventMediaRepository: EventMediaRepository,
    @Inject private val eventService: EventService,
    @Inject private val s3Service: S3Service
) : EventMediaService {

    override fun addMediaToEvent(eventId: Long, file: CompletedFileUpload) {
        val event = eventService.findById(eventId)
        val tempFile = File.createTempFile(file.filename, ".tmp")
        tempFile.deleteOnExit()
        FileOutputStream(tempFile).use { output ->
            output.write(file.bytes)
        }
        val s3Key = "events/$eventId/media/${file.filename}"
        s3Service.uploadFile(tempFile, s3Key)
        val eventMedia = EventMedia(key = s3Key, event = event!!)
        eventMediaRepository.save(eventMedia)
    }

    override fun findByEventId(id: Long): List<EventMedia> {
        return eventMediaRepository.findAllByEventId(id)
    }

    override fun findByEventId(id: Long, page: Int, size: Int): GetEventMediaResponse {
        val pageable = Pageable.from(page, size)
        val media = eventMediaRepository.findAllByEventId(id, pageable)
        return GetEventMediaResponse(
            page = media.pageable.number,
            size = media.size,
            totalSize = media.totalSize,
            totalPage = media.totalPages,
            content = media.content
        )
    }

    override fun findById(id: Long): EventMedia? {
        return eventMediaRepository.findById(id).orElse(null)
    }

    override fun delete(media: EventMedia) {
        s3Service.deleteFile(media.key)
        eventMediaRepository.delete(media)
    }

    override fun getPresignedUrl(media: EventMedia): EventMediaPresignedUrl {
        val presignedUrl = "https://emms-events-media-storage.s3.eu-central-1.amazonaws.com/test.jpg?response-content-disposition=inline&X-Amz-Security-Token=IQoJb3JpZ2luX2VjECgaCmV1LW5vcnRoLTEiRzBFAiEA7dGkl1Owctq2JD8TzzjUNd6HmhTj8MI2vOHoRklUPf4CICKRLqhs%2FUFfoxqpp%2FljpzgnhNOqm4iAgb8bbxBdjx8kKuQCCHEQARoMNDUxMTk3MjY5MDk3IgyTvkdbu9eslG0fyycqwQJFHvvubFcnGF5Qj%2FEOesNoDo2k3QysvOMdmSLOccQ5cw0s02hozm32wTt%2F0SxZnii8gZiM5dXRMiKJzv4ZsNoKZiUZOWpu4mR8ixndbjCW23jQHQNT759LdVcO6ulYtjHPRsTGTq%2BhJDSjSQLJE%2Bbpbh%2BTXHRqvETPt9Ouv%2Bz5ls0V870xXYXVnx4NGMcyit7HXH1kIsJ%2Fugl9xWzXN1%2BMXv6U4tBJ3F1ldZw0h2iu36D94I9bH2iaNGEGF76ZsWfZ2ZqdkRs71vaA5qJBDkrReO13DzhMOyENZxpCOWSSiXSuidKATg2lIbxIy1R6wNnqB71uZNMd9SQwN5e9v9oCzIsIrcuuyToC71jZZz8W4dqq8cL2LttyHfVuSXWdTLdOD7NTJ%2F6DFIu767kvLSRelZPJI0jrecHPza6AuoMn4NIwo5GGpAY6swJiV13UXg5EjhDmA4Oq3y%2FIFhwbgdtL24BEl5WMsA8NRPfb42O6JFMkqE1DW8I890HBYienZG2VxKsJ1%2F%2BEv4faWwMvBjVkDm5wH9WEvkF%2FiKSo55ymDluNyMsNHNfaFm2jhEkgCfCkwHg%2FDsLwBtCgGrM7uyGyLOZ4OBLZaXJ8iKwzaKQx%2BrSPtJ5z4czkGrX8DsRU%2F51jZrmdgWFqqRjSvn9PEtNbq7CU%2BWTA9RniBL7AbfIot8nZ%2FnAe0tWCKkH5yfjCgPSnO5u8zEB1jdWu1NDVslNSoA9%2Fa2U2imo7U8%2B9Vqvq%2FeswePSK%2BIYCHi2io%2BpDAJ1EB8NbjBwqCpscoG%2FjOQnQfLLGolpdXR6dlDoG0jruw3JEzSFjZnYXD0ZfDyrAhyTWaYEz4Dnh4ld6lbfY&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20230608T080824Z&X-Amz-SignedHeaders=host&X-Amz-Expires=17999&X-Amz-Credential=ASIAWSDLT6RU3OP2RO4R%2F20230608%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=d70efdb35b54e8675aca561069cb036812ddc7d1bff647e3b6548e860093affd"
        return EventMediaPresignedUrl(presignedUrl)
    }


}
