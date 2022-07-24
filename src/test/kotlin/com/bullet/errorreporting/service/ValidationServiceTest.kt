package com.bullet.errorreporting.service

import com.bullet.errorreporting.kafka.ErrorEvent
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier

class ValidationServiceTest {
    @Test
    fun shouldErrorWhenInvalidObject() {
        val faultyErrorEvent = ErrorEvent("", "user", "description")

        StepVerifier.create(ValidationService.validate(faultyErrorEvent))
            .expectErrorMatches { it is InputValidationException }
            .verify()
    }

    @Test
    fun shouldReturnIdentityWhenValid() {
        val correctErrorEvent = ErrorEvent("application", "user", "description")

        StepVerifier.create(ValidationService.validate(correctErrorEvent))
            .expectNext(correctErrorEvent)
            .expectComplete()
            .verify()
    }
}