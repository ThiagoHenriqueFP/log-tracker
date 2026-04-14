package br.edu.ufersa.distributed_logging.usecase.async

import br.edu.ufersa.distributed_logging.kafka.producer.KafkaAsyncProducer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AsyncUseCaseImpl(
    private val kafkaAsyncProducer: KafkaAsyncProducer,
) : AsyncUseCase {
    private val logger = LoggerFactory.getLogger(AsyncUseCaseImpl::class.java)
    override fun sendToKafka(value: Int) {
        logger.info("method=AsyncUseCaseImpl.sendToKafka, message=Enviando para o kafka")
        kafkaAsyncProducer.produce(value)
        logger.info("method=AsyncUseCaseImpl.sendToKafka, message=Enviando para o kafka com sucesso")
    }

    override fun processFromKafka(value: Int) {
        logger.info("method=AsyncUseCaseImpl.processFromKafka, message=Recebendo via kafka")
        logger.info("method=AsyncUseCaseImpl.processFromKafka, message=Valor vindo do kafka $value")

    }
}