package com.bullet.errorreporting.configuration

import org.springframework.beans.factory.BeanClassLoaderAware
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption
import org.springframework.data.cassandra.core.cql.session.init.KeyspacePopulator
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator


@Configuration
class CreateKeyspaceConfiguration(@Value(value = "\${spring.data.cassandra.keyspace-name}") private val keyspace: String) : AbstractCassandraConfiguration(), BeanClassLoaderAware {
    override fun getKeyspaceName(): String {
        return keyspace
    }

    override fun getKeyspaceCreations(): List<CreateKeyspaceSpecification> {
        val specification = CreateKeyspaceSpecification
            .createKeyspace(keyspace)
            .ifNotExists()
            .with(KeyspaceOption.DURABLE_WRITES, true)
        return listOf(specification)
    }

    override fun keyspacePopulator(): KeyspacePopulator? {
        return ResourceKeyspacePopulator(scriptOf("""
            CREATE TABLE IF NOT EXISTS $keyspace.errors (
              id UUID,
              user VARCHAR,
              application VARCHAR,
              description VARCHAR,
              error_datetime TIMESTAMP,
              PRIMARY KEY ((application), error_datetime, id, user)
            )
            WITH CLUSTERING ORDER BY (error_datetime DESC, id DESC, user DESC);
        """.trimIndent()))
    }
}