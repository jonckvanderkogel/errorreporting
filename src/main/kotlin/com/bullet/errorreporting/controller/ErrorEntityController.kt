package com.bullet.errorreporting.controller

import com.bullet.errorreporting.entity.ErrorEntity
import com.bullet.errorreporting.service.ErrorEntityFetchingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/errors")
class ErrorEntityController(
    @Autowired val errorEntityFetchingService: ErrorEntityFetchingService
) {

    @GetMapping("/today")
    fun findErrorsForLastDay(user: String) : Flux<ErrorEntity> {
        return errorEntityFetchingService.findErrorsForLastDay(user)
    }
}