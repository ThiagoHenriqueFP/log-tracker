package br.edu.ufersa.distributed_logging.usecase.simple

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.time.LocalTime

@Component
class SimpleUseCaseImpl : SimpleUseCase {

    companion object {
        private val logger = LoggerFactory.getLogger(SimpleUseCaseImpl::class.java)
    }

    override fun execute(): String {
        logger.info("method=SimpleUseCaseImpl.execute, message=Executando caso de uso simples")
        val time = LocalTime.now()

        if (time.second % 2 != 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Segundo impar detectado")
        }

        return time.toString()
    }
}