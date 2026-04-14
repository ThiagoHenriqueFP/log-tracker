package br.edu.ufersa.distributed_logging.usecase.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CoroutineUseCaseImpl : CoroutineUseCase {

    companion object {
        private val logger = LoggerFactory.getLogger(CoroutineUseCaseImpl::class.java)
    }

    override fun execute() {
        listOf(1..10).forEach {
            CoroutineScope(Dispatchers.Default).launch {
                logger.info("method=CoroutineUseCaseImpl.execute, message=Executando coroutine $it")
            }
        }

        logger.info("method=CoroutineUseCaseImpl.execute, message=Coroutine lancada")
    }
}