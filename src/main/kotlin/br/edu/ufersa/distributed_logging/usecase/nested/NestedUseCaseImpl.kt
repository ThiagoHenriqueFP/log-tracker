package br.edu.ufersa.distributed_logging.usecase.nested

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class NestedUseCaseImpl : NestedUseCase {
    companion object {
        private val log = LoggerFactory.getLogger(NestedUseCaseImpl::class.java)
    }
    override fun execute(limit: Int): ResponseEntity<Any> {
        log.info("method=NestedUseCaseImpl.execute, message=Executando limit=$limit")
        return if (limit >= 5) ResponseEntity.ok().build() else this.execute(limit + 1)
    }
}