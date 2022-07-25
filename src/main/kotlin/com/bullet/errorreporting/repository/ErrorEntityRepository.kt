package com.bullet.errorreporting.repository

import com.bullet.errorreporting.entity.ErrorEntity
import com.bullet.errorreporting.entity.ErrorEntityPrimaryKey
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

interface ErrorEntityRepository : ReactiveCassandraRepository<ErrorEntity, ErrorEntityPrimaryKey> {
    fun findByKeyErrorDateTimeAfterAndKeyUser(errorDateTime: LocalDateTime, user: String) : Flux<ErrorEntity>
}
