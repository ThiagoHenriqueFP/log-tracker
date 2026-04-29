package br.edu.ufersa.distributed_logging.usecase.nested

import org.springframework.http.ResponseEntity

interface NestedUseCase {
    fun execute(limit: Int = 0): ResponseEntity<Any>
}