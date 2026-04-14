package br.edu.ufersa.distributed_logging.usecase.nested

import org.springframework.http.ResponseEntity

fun interface NestedUseCase {
    fun execute(limit: Int): ResponseEntity<Any>
}