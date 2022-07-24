package com.bullet.errorreporting.configuration

import com.bullet.errorreporting.kafka.ErrorEvent
import com.bullet.errorreporting.service.ValidationService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import reactor.core.publisher.Flux
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.receiver.ReceiverRecord


@Configuration
class KafkaConsumerConfig {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    @Bean
    fun kafkaReceiverOptions(
        @Value(value = "\${kafka.topic}") topic: String,
        kafkaProperties: KafkaProperties
    ): ReceiverOptions<String, ErrorEvent> {
        val basicReceiverOptions: ReceiverOptions<String, ErrorEvent> =
            ReceiverOptions.create(kafkaProperties.buildConsumerProperties())
        return basicReceiverOptions.subscription(listOf(topic))
    }

    @Bean
    fun reactiveKafkaConsumerTemplate(
        kafkaReceiverOptions: ReceiverOptions<String, ErrorEvent>
    ): ReactiveKafkaConsumerTemplate<String, ErrorEvent> {
        return ReactiveKafkaConsumerTemplate(kafkaReceiverOptions)
    }

    @Bean
    fun errorEventFlux(
        @Autowired kafkaConsumerTemplate: ReactiveKafkaConsumerTemplate<String, ErrorEvent>
    ): Flux<ErrorEvent> {
        return kafkaConsumerTemplate
            .receive()
            .share()
            .map { obj: ReceiverRecord<String, ErrorEvent> -> obj.value() }
            .flatMap(ValidationService::validate)
            .onErrorContinue { t, o ->
                logger.error("Error in payment stream.", t);
                logger.error("Error happened on object: {}", o);
            }
    }
}