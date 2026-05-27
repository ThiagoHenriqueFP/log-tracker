package br.edu.ufersa.distributed_logging.config

import org.slf4j.MDC
import java.util.UUID

abstract class MdcInterceptor {
    fun createCorrelationId(): String {
        val correlationId: String? = MDC.get(LoggingConfig.CORRELATION_ID)
        val subContext: String? = MDC.get(LoggingConfig.SUB_CONTEXT_NAME)
        val subContextName = MDC.get(LoggingConfig.SUB_CONTEXT_ID)

        return if (subContext != null) {
            if (subContextName.contains(subContext))
                "$correlationId:$subContext"
            else
                "$correlationId:$subContext:$subContextName"
        } else {
            correlationId ?: UUID.randomUUID().toString()
        }
    }

    fun getContextIds(headerValue: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        headerValue.split(":").forEachIndexed { index, value ->
            when (index) {
                0 -> map[LoggingConfig.CORRELATION_ID] = value
                1 -> map[LoggingConfig.SUB_CONTEXT_NAME] = value
                2 -> map[LoggingConfig.SUB_CONTEXT_ID] = value
            }
        }

        return map
    }
}