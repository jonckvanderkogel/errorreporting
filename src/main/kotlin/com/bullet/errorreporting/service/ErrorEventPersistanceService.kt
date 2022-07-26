package com.bullet.errorreporting.service

import com.bullet.errorreporting.entity.ErrorEntity
import com.bullet.errorreporting.entity.ErrorEntityPrimaryKey
import com.bullet.errorreporting.kafka.ErrorEvent
import com.bullet.errorreporting.repository.ErrorEntityRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ErrorEventPersistanceService (
    private val errorEventFlux: Flux<ErrorEvent>,
    private val errorEntityRepository: ErrorEntityRepository
) {
    init {
        handleErrorEvents().subscribe();
    }

    final fun handleErrorEvents(): Flux<ErrorEntity> = errorEventFlux
        .flatMap { persistErrorEvent(it) }

    private fun persistErrorEvent(errorEvent: ErrorEvent) = errorEntityRepository.save(
        ErrorEntity(
            ErrorEntityPrimaryKey(user = errorEvent.user, application = errorEvent.application),
            errorEvent.description))

}