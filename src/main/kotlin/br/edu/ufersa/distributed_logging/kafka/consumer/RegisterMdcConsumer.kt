package br.edu.ufersa.distributed_logging.kafka.consumer

import br.edu.ufersa.distributed_logging.config.LoggingConfig
import br.edu.ufersa.distributed_logging.config.MdcInterceptor
import org.springframework.stereotype.Component

@Component
open class RegisterMdcConsumer(
    private val mdcInterceptor: MdcInterceptor
) {
    fun registerMdcConsumer(kafkaCorrelationId: String) {
        val context = mdcInterceptor.getContextIds(kafkaCorrelationId)

        context.forEach { (key, value) ->
            LoggingConfig.setProperty(key, value)
        }

        LoggingConfig.initializeLoggingContext(context[LoggingConfig.CORRELATION_ID].toString())
    }
}