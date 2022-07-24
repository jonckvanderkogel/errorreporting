package com.bullet.errorreporting.simulation

import com.bullet.errorreporting.kafka.ErrorEvent
import com.bullet.errorreporting.service.MessageProducerService
import com.github.javafaker.Faker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.*
import javax.annotation.PostConstruct

@Service
class ErrorEventSimulation(
    @Autowired private val messageProducerService: MessageProducerService
) {
    companion object {
        private val RANDOM: SplittableRandom = SplittableRandom()
        private val FAKER: Faker = Faker()
    }

    @PostConstruct
    fun init() {
        runSimulation()
    }

    private fun runSimulation() {
        generateErrorEvents()
            .flatMap<Any>(messageProducerService::sendMessage)
            .subscribe()
    }

    fun generateErrorEvents(): Flux<ErrorEvent> {
        return Flux.interval(Duration.ofSeconds(5))
            .map {
                ErrorEvent(
                    "Application${RANDOM.nextInt(1, 10)}",
                    "User${RANDOM.nextInt(1, 10)}",
                    FAKER.dune().quote().take(1000)
                )
            }
    }
}