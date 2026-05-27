package br.edu.ufersa.distributed_logging.web.impl

import br.edu.ufersa.distributed_logging.usecase.async.AsyncUseCase
import br.edu.ufersa.distributed_logging.usecase.coroutine.CoroutineUseCase
import br.edu.ufersa.distributed_logging.usecase.nested.NestedUseCase
import br.edu.ufersa.distributed_logging.usecase.simple.SimpleUseCase
import br.edu.ufersa.distributed_logging.web.WithLogController
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class WithLogControllerImpl(
    private val simpleUseCase: SimpleUseCase,
    private val coroutineUseCase: CoroutineUseCase,
    private val asyncUseCase: AsyncUseCase,
    private val nestedUseCase: NestedUseCase
) : WithLogController {
    companion object {
        private val logger = LoggerFactory.getLogger(WithLogControllerImpl::class.java)
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

    override fun simple(str: String): ResponseEntity<Any> {
        return ResponseEntity.ok(simpleUseCase.execute(str))
    }

    override fun nested(): ResponseEntity<Any> {
        return nestedUseCase.execute()
    }

    override fun async(): ResponseEntity<Any> {
        asyncUseCase.sendToKafka(1)
        return ResponseEntity.ok().build()
    }

    override fun coroutine(): ResponseEntity<String> {
        coroutineUseCase.execute()
        return ResponseEntity.ok().build()
    }
}