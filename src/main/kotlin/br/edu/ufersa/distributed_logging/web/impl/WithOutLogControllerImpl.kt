package br.edu.ufersa.distributed_logging.web.impl

import br.edu.ufersa.distributed_logging.usecase.simple.SimpleUseCase
import br.edu.ufersa.distributed_logging.web.WithOutLogController
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class WithOutLogControllerImpl(
    val simpleUseCase: SimpleUseCase
) : WithOutLogController {
    companion object {
        private val logger = LoggerFactory.getLogger(WithOutLogControllerImpl::class.java)
    }

    override fun simple(): ResponseEntity<Any> {
        logger.info("method=ApiControllerImpl, message=Iniciando fluxo de use case simples")
        return try {
            ResponseEntity.ok(simpleUseCase.execute())
        } catch (ex: Exception) {
            logger.error("method=ApiControllerImpl, message=Erro ao executar usecase simples. Erro: ${ex.message}", ex)
            throw ex
        }
    }

    override fun nested(): ResponseEntity<Unit> {
        TODO("Not yet implemented")
    }

    override fun async(): ResponseEntity<Unit> {
        TODO("Not yet implemented")
    }

    override fun coroutine(): ResponseEntity<Unit> {
        TODO("Not yet implemented")
    }
}