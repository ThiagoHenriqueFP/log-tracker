package br.edu.ufersa.distributed_logging.web.impl

import br.edu.ufersa.distributed_logging.usecase.async.AsyncUseCase
import br.edu.ufersa.distributed_logging.usecase.coroutine.CoroutineUseCase
import br.edu.ufersa.distributed_logging.usecase.nested.NestedUseCase
import br.edu.ufersa.distributed_logging.usecase.simple.SimpleUseCase
import br.edu.ufersa.distributed_logging.web.WithOutLogController
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class WithOutLogControllerImpl(
    private val simpleUseCase: SimpleUseCase,
    private val coroutineUseCase: CoroutineUseCase,
    private val asyncUseCase: AsyncUseCase,
    private val nestedUseCase: NestedUseCase
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

    override fun nested(): ResponseEntity<Any> {
        return nestedUseCase.execute()
    }

    override fun async(): ResponseEntity<Any> {
        asyncUseCase.sendToKafka(1)
        return ResponseEntity.ok().build()
    }

    override fun coroutine(): ResponseEntity<String> {
        coroutineUseCase.executeWithOutContext()
        return ResponseEntity.ok().build()
    }
}