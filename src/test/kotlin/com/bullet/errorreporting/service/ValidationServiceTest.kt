package com.bullet.errorreporting.service

import com.bullet.errorreporting.configuration.validates
import com.bullet.errorreporting.kafka.ErrorEvent
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import javax.validation.Validation

class ValidationServiceTest {
    @Test
    fun shouldErrorWhenInvalidObject() {
        val faultyErrorEvent = ErrorEvent("", "user", "description")

        val validator = Validation.buildDefaultValidatorFactory().validator

        StepVerifier.create(validator validates faultyErrorEvent)
            .expectErrorMatches { it is InputValidationException }
            .verify()
    }

    @Test
    fun shouldReturnIdentityWhenValid() {
        val correctErrorEvent = ErrorEvent("application", "user", "description")

        val validator = Validation.buildDefaultValidatorFactory().validator

        StepVerifier.create(validator validates correctErrorEvent)
            .expectNext(correctErrorEvent)
            .expectComplete()
            .verify()
    }
}