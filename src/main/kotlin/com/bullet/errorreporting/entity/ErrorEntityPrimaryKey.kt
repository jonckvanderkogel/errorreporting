package com.bullet.errorreporting.entity

import com.datastax.oss.driver.api.core.uuid.Uuids
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

@PrimaryKeyClass
data class ErrorEntityPrimaryKey(
    @PrimaryKeyColumn(name = "id", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    val id: UUID = Uuids.timeBased(),

    @PrimaryKeyColumn(name = "user", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    val user: String,

    @PrimaryKeyColumn(name = "application", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val application: String,

    @PrimaryKeyColumn(name = "error_datetime", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    val errorDateTime: LocalDateTime = LocalDateTime.now()
) : Serializable