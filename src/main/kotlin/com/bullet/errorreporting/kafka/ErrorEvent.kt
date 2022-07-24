package com.bullet.errorreporting.kafka

import javax.validation.constraints.Size

data class ErrorEvent(
    @field:Size(min = 1, max = 255)
    val application: String,

    @field:Size(min = 1, max = 255)
    val user: String,

    @field:Size(min = 1, max = 1000)
    val description: String)