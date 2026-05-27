package br.edu.ufersa.distributed_logging.usecase.external

fun interface ExternalUseCase {
    fun execute(str: String): String?
}