package br.edu.ufersa.distributed_logging.usecase.simple

interface SimpleUseCase {
    fun execute(): String
    fun execute(str: String): String
}