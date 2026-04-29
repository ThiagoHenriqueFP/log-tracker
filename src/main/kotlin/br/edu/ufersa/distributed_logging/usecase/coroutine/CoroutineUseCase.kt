package br.edu.ufersa.distributed_logging.usecase.coroutine

interface CoroutineUseCase {
    fun execute()
    fun executeWithOutContext()
}