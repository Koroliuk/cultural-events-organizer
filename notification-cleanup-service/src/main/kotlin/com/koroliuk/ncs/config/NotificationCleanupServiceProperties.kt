package com.koroliuk.ncs.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("notification-cleanup-service")
class NotificationCleanupServiceProperties {

    var expirationInDays: Int? = null

    var batchSize: Int? = null

}
