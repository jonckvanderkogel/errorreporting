package com.bullet.errorreporting.service

import com.bullet.errorreporting.kafka.ErrorEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kafka.sender.SenderResult


@Service
class MessageProducerService(
    @Autowired val kafkaTemplate: ReactiveKafkaProducerTemplate<String, ErrorEvent>,
    @Value("\${kafka.topic}") val topic: String
) {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    fun sendMessage(message: ErrorEvent): Mono<SenderResult<Void>> {
        logger.info("Producing $message")
        return kafkaTemplate.send(topic, message)
    }
}