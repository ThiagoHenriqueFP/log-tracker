package br.edu.ufersa.distributed_logging.kafka.consumer

import br.edu.ufersa.distributed_logging.config.LoggingConfig

open class RegisterMdcConsumer(
    private val loggingConfig: LoggingConfig,
) {
    fun registerMdcConsumer(kafkaCorrelationId: String) {
        loggingConfig.setProperty(loggingConfig.CORRELATION_ID, kafkaCorrelationId)
    }
}