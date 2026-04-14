package br.edu.ufersa.distributed_logging.usecase.async

interface AsyncUseCase {
    fun sendToKafka(value: Int)
    fun processFromKafka(value: Int)
}