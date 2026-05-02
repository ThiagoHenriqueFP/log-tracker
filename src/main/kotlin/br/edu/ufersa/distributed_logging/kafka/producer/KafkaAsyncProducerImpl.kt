package br.edu.ufersa.distributed_logging.kafka.producer

import br.edu.ufersa.distributed_logging.config.LoggingConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

@Component
class KafkaAsyncProducerImpl(
    @param:Value("\${kafka.topicos.async}")
    private val topico: String,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val loggingConfig: LoggingConfig,
) : KafkaAsyncProducer {
    companion object {
        private val logger = LoggerFactory.getLogger(KafkaAsyncProducerImpl::class.java)
    }

    override fun produce(message: Int) {
        logger.info("method=AsyncProducerImpl.execute, message=Enviando para o kafka")
        val correlationId: String? = loggingConfig.getCorrelationPair().second
        val message = MessageBuilder<String>.withPayload(message.toString())
            .setHeader(KafkaHeaders.TOPIC, topico)

        if (correlationId != null)
            message.setHeader(KafkaHeaders.CORRELATION_ID, correlationId)

        kafkaTemplate.send(message.build())
        logger.info("method=AsyncProducerImpl.execute, message=Enviado com sucesso")
    }
}