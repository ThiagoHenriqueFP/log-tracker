package br.edu.ufersa.distributed_logging.kafka.producer

fun interface KafkaAsyncProducer {
    fun produce(message: Int)
}