package br.edu.ufersa.distributed_logging.config.kafka

import br.edu.ufersa.distributed_logging.config.kafka.interceptors.MdcProducerInterceptor
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.kafka.autoconfigure.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaConfiguration(
    private val kafkaProperties: KafkaProperties,
) {
    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val properties: MutableMap<String, Any> = kafkaProperties.buildProducerProperties()
        properties[ProducerConfig.INTERCEPTOR_CLASSES_CONFIG] = MdcProducerInterceptor::class.java.name
        return DefaultKafkaProducerFactory(properties)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }
}