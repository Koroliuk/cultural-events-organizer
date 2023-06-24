package com.koroliuk.ncs.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("event-monitoring-service")
class MonitorCleanupServiceProperties {

    var batchSize: Int? = null

}
