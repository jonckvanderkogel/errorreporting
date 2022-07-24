package com.bullet.errorreporting.entity

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("errors")
data class ErrorEntity (
    @PrimaryKey
    val key: ErrorEntityPrimaryKey,
    val description: String
)