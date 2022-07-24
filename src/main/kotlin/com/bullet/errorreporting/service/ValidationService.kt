package com.bullet.errorreporting.service

import reactor.core.publisher.Mono
import java.util.stream.Collectors
import javax.validation.ConstraintViolation
import javax.validation.Validation


object ValidationService {
    private val VALIDATOR = Validation.buildDefaultValidatorFactory().validator
    fun <T : Any> validate(arg: T): Mono<T> {
        val validationResult = VALIDATOR.validate(arg)
        return if (validationResult.isEmpty()) Mono.just(arg) else Mono.error(
            InputValidationException(
                validationResult
                    .stream()
                    .map { r: ConstraintViolation<T> ->
                        String.format(
                            "Field %s, constraint: %s, actual value: %s.",
                            r.propertyPath.toString(),
                            r.message,
                            r.invalidValue
                        )
                    }
                    .collect(Collectors.joining(" "))
            )
        )
    }
}