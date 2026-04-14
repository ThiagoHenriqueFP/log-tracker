package br.edu.ufersa.distributed_logging.kafka.producer

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaAsyncProducerImpl(
    @param:Value("\${kafka.topicos.async}")
    private val topico: String,
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : KafkaAsyncProducer {
    companion object {
        private val logger = LoggerFactory.getLogger(KafkaAsyncProducerImpl::class.java)
    }

    override fun produce(message: Int) {
        logger.info("method=AsyncProducerImpl.execute, message=Enviando para o kafka")
        kafkaTemplate.send(topico, message.toString())
        logger.info("method=AsyncProducerImpl.execute, message=Enviado com sucesso")
    }
}