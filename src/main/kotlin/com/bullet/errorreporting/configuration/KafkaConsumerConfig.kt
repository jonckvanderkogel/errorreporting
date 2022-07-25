package com.bullet.errorreporting.configuration

import com.bullet.errorreporting.kafka.ErrorEvent
import com.bullet.errorreporting.service.InputValidationException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.receiver.ReceiverOptions
import java.util.stream.Collectors
import javax.validation.Validation
import javax.validation.Validator


@Configuration
open class KafkaConsumerConfig {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private val VALIDATOR = Validation.buildDefaultValidatorFactory().validator
    }

    @Bean
    open fun kafkaReceiverOptions(
        @Value(value = "\${kafka.topic}") topic: String,
        kafkaProperties: KafkaProperties
    ): ReceiverOptions<String, ErrorEvent> {
        val basicReceiverOptions: ReceiverOptions<String, ErrorEvent> =
            ReceiverOptions.create(kafkaProperties.buildConsumerProperties())
        return basicReceiverOptions.subscription(listOf(topic))
    }

    @Bean
    open fun reactiveKafkaConsumerTemplate(
        kafkaReceiverOptions: ReceiverOptions<String, ErrorEvent>
    ): ReactiveKafkaConsumerTemplate<String, ErrorEvent> = ReactiveKafkaConsumerTemplate(kafkaReceiverOptions)

    @Bean
    open fun errorEventFlux(
        kafkaConsumerTemplate: ReactiveKafkaConsumerTemplate<String, ErrorEvent>
    ): Flux<ErrorEvent> = kafkaConsumerTemplate
        .receive()
        .share()
        .map { it.value() }
        .flatMap { VALIDATOR validates it }
        .onErrorContinue { t, o ->
            logger.error("Error in payment stream.", t);
            logger.error("Error happened on object: {}", o);
        }
}

infix fun <T : Any> Validator.validates(arg: T): Mono<T> = validate(arg).run {
    if (isEmpty()) Mono.just(arg) else Mono.error(
        InputValidationException(
            stream()
                .map { "Field ${it.propertyPath}, constraint: ${it.message}, actual value: ${it.invalidValue}." }
                .collect(Collectors.joining(" "))
        )
    )
}