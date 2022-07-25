package com.bullet.errorreporting.service

import com.bullet.errorreporting.entity.ErrorEntity
import com.bullet.errorreporting.repository.ErrorEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.LocalDateTime

@Service
class ErrorEntityFetchingService(
    @Autowired val errorEntityRepository: ErrorEntityRepository
) {
    fun findErrorsForLastDay(user: String) : Flux<ErrorEntity> {
        return errorEntityRepository.findByKeyErrorDateTimeAfterAndKeyUser(LocalDateTime.now().minusDays(1L), user)
    }
}