package com.bullet.errorreporting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties
@SpringBootApplication
class ErrorreportingApplication

fun main(args: Array<String>) {
	runApplication<ErrorreportingApplication>(*args)
}
