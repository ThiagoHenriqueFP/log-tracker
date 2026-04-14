package br.edu.ufersa.distributed_logging.kafka.consumer

import br.edu.ufersa.distributed_logging.usecase.async.AsyncUseCase
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaAsyncConsumerImpl(
    private val useCase: AsyncUseCase,
) : KafkaAsyncConsumer {

    companion object {
        private val logger = LoggerFactory.getLogger(KafkaAsyncConsumerImpl::class.java)
    }

    @KafkaListener(
        topics = [$$"${kafka.topicos.async}"],
        autoStartup = "true",
    )
    override fun listen(message: String) {
        logger.info("method=KafkaAsyncConsumerImpl.listen, message=Recebendo mensagem do kafka: $message")
        useCase.processFromKafka(message.toInt())
    }
}