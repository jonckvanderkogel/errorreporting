package com.bullet.errorreporting.service

import com.bullet.errorreporting.entity.ErrorEntity
import com.bullet.errorreporting.entity.ErrorEntityPrimaryKey
import com.bullet.errorreporting.kafka.ErrorEvent
import com.bullet.errorreporting.repository.ErrorEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.annotation.PostConstruct

@Service
class ErrorEventPersistanceService(
    @Autowired val errorEventFlux: Flux<ErrorEvent>,
    @Autowired val errorEntityRepository: ErrorEntityRepository
) {

    @PostConstruct
    fun postConstruct() {
        handleErrorEvents().subscribe();
    }

    fun handleErrorEvents(): Flux<ErrorEntity> {
        return errorEventFlux
            .flatMap {
                persistErrorEvent(it)
            }
    }

    private fun persistErrorEvent(errorEvent: ErrorEvent): Mono<ErrorEntity> {
        return errorEntityRepository.save(
            ErrorEntity(
                ErrorEntityPrimaryKey(user = errorEvent.user, application = errorEvent.application),
                errorEvent.description))
    }
}