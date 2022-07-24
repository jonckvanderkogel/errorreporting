package com.bullet.errorreporting.service

import com.bullet.errorreporting.entity.ErrorEntity
import com.bullet.errorreporting.kafka.ErrorEvent
import com.bullet.errorreporting.repository.ErrorEntityRepository
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class ErrorEventPersistanceServiceTest {
    @Test
    fun errorEventShouldBePersistedAsErrorEntity() {
        val errorEntityRepository = Mockito.mock(ErrorEntityRepository::class.java)

        Mockito
            .`when`(errorEntityRepository.save(ArgumentMatchers.any(ErrorEntity::class.java)))
            .thenAnswer { invocation: InvocationOnMock ->
                Mono.just(
                    invocation.arguments[0]
                )
            }

        val errorEventFlux = Flux.just(
            ErrorEvent(
                "application",
                "user",
                "description"
            )
        )

        val errorEventPersistanceService = ErrorEventPersistanceService(errorEventFlux, errorEntityRepository)

        StepVerifier.create(errorEventPersistanceService.handleErrorEvents())
            .expectNextMatches{ it.key.application == "application" && it.key.user == "user" && it.description == "description" }
            .expectComplete()
            .verify()
    }
}