package br.edu.ufersa.distributed_logging.kafka.consumer

import br.edu.ufersa.distributed_logging.config.LoggingConfig
import br.edu.ufersa.distributed_logging.config.MdcInterceptor

open class RegisterMdcConsumer : MdcInterceptor() {
    fun registerMdcConsumer(kafkaCorrelationId: String) {
        val context = getContextIds(kafkaCorrelationId)

        context.forEach { (key, value) ->
            LoggingConfig.setProperty(key, value)
        }

        LoggingConfig.initializeLoggingContext(context[LoggingConfig.CORRELATION_ID].toString())
    }
}