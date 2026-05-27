package br.edu.ufersa.distributed_logging.kafka.consumer

import br.edu.ufersa.distributed_logging.usecase.async.AsyncUseCase
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class KafkaAsyncConsumerImpl(
    private val useCase: AsyncUseCase
) : KafkaAsyncConsumer, RegisterMdcConsumer() {

    companion object {
        private val logger = LoggerFactory.getLogger(KafkaAsyncConsumerImpl::class.java)
    }

    @KafkaListener(
        topics = [$$"${kafka.topicos.async}"],
        autoStartup = "true",
    )
    override fun listen(
        @Payload message: String,
        @Header(KafkaHeaders.CORRELATION_ID) correlationId: String?,
    ) {
        if (correlationId == null) logger.warn("Correlation id $correlationId nao encontrado no header do kafka")
        else this.registerMdcConsumer(correlationId)
        logger.info("method=KafkaAsyncConsumerImpl.listen, message=Recebendo mensagem do kafka: $message")
        useCase.processFromKafka(message.toInt())
    }
}