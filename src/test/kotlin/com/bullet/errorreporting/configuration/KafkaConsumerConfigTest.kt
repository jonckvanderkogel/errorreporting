package com.bullet.errorreporting.configuration

import com.bullet.errorreporting.kafka.ErrorEvent
import com.github.javafaker.Faker
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import reactor.core.publisher.Flux
import reactor.kafka.receiver.ReceiverRecord
import reactor.test.StepVerifier
import java.util.*

class KafkaConsumerConfigTest {
    companion object {
        private val RANDOM: SplittableRandom = SplittableRandom()
        private val FAKER: Faker = Faker()
    }

    @Test
    fun errorEventFluxWithValidElementsShouldBeFine() {
        val mockedConsumerTemplate: ReactiveKafkaConsumerTemplate<String, ErrorEvent> = Mockito.mock(
            ReactiveKafkaConsumerTemplate::class.java
        ) as ReactiveKafkaConsumerTemplate<String, ErrorEvent>
        val receiverRecordFlux =
            Flux.fromStream(
                (1 .. 10 )
                    .map {
                        generateReceiverRecord(
                            generateConsumerRecord(generateErrorEvent())
                        )
                    }.stream()
            )

        Mockito.`when`(mockedConsumerTemplate.receive())
            .thenReturn(receiverRecordFlux)

        StepVerifier.create(KafkaConsumerConfig().errorEventFlux(mockedConsumerTemplate))
            .expectNextCount(10)
            .expectComplete()
            .verify()
    }

    @Test
    fun errorEventFluxShouldDropInvalidElements() {
        val mockedConsumerTemplate: ReactiveKafkaConsumerTemplate<String, ErrorEvent> = Mockito.mock(
            ReactiveKafkaConsumerTemplate::class.java
        ) as ReactiveKafkaConsumerTemplate<String, ErrorEvent>

        val receiverRecordFlux = Flux.just(
            generateReceiverRecord(generateConsumerRecord(generateErrorEvent())),
            generateReceiverRecord(generateConsumerRecord(generateFaultyErrorEvent())),
            generateReceiverRecord(generateConsumerRecord(generateErrorEvent()))
        )

        Mockito.`when`(mockedConsumerTemplate.receive())
            .thenReturn(receiverRecordFlux)

        StepVerifier.create(KafkaConsumerConfig().errorEventFlux(mockedConsumerTemplate))
            .expectNextCount(2)
            .expectComplete()
            .verify()
    }

    private fun generateErrorEvent() : ErrorEvent {
        return ErrorEvent("Application${RANDOM.nextInt(1, 10)}",
            "User${RANDOM.nextInt(1, 10)}",
            FAKER.chuckNorris().fact().take(1000)
        )
    }

    private fun generateFaultyErrorEvent() : ErrorEvent {
        return ErrorEvent(
            "Application${RANDOM.nextInt(1, 10)}",
            "User${RANDOM.nextInt(1, 10)}",
            "foo".repeat(1000)
        )
    }

    private fun generateReceiverRecord(consumerRecord: ConsumerRecord<String, ErrorEvent>): ReceiverRecord<String, ErrorEvent> {
        return ReceiverRecord(consumerRecord, null)
    }

    private fun generateConsumerRecord(errorEvent: ErrorEvent): ConsumerRecord<String, ErrorEvent> {
        return ConsumerRecord<String, ErrorEvent>("topic", 0, 123L, "key", errorEvent)
    }
}