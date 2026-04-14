package br.edu.ufersa.distributed_logging.usecase.simple

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalTime

@Component
class SimpleUseCaseImpl : SimpleUseCase {

    companion object {
        private val logger = LoggerFactory.getLogger(SimpleUseCaseImpl::class.java)
    }

    override fun execute(): String {
        logger.info("method=SimpleUseCaseImpl.execute, message=Executando caso de uso simples")
        val time = LocalTime.now()

        if (time.minute % 2 != 0) {
            throw RuntimeException("Minuto impar detectado")
        }

        return time.toString()
    }
}