package br.edu.ufersa.distributed_logging.usecase.simple

import org.springframework.http.ResponseEntity

fun interface SimpleUseCase {
    fun execute(): String
}