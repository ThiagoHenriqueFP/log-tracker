package br.edu.ufersa.distributed_logging.kafka.consumer

fun interface KafkaAsyncConsumer {
    fun listen(message: String, correlationId: String)
}