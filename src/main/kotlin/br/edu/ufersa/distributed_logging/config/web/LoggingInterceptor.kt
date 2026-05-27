package br.edu.ufersa.distributed_logging.config.web

import br.edu.ufersa.distributed_logging.config.LoggingConfig
import br.edu.ufersa.distributed_logging.config.MdcInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

/**
 * Interceptor que inicializa o contexto de logging para cada requisição HTTP
 */
@Component
class LoggingInterceptor : HandlerInterceptor, MdcInterceptor() {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val correlationId = request.getHeader("X-Correlation-ID") ?: UUID.randomUUID().toString()
        if (correlationId.contains(":")) {
            val ids = getContextIds(correlationId)

            LoggingConfig.initializeLoggingContext(ids[LoggingConfig.CORRELATION_ID].toString())

            ids.forEach { (key, value) ->
                LoggingConfig.setProperty(key, value)
            }

        } else {
            LoggingConfig.initializeLoggingContext(correlationId)
        }

        logger.debug("Iniciando requisição: {} {}", request.method, request.requestURI)

        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        logger.debug(
            "Requisição finalizada: {} {} - Status: {}",
            request.method, request.requestURI, response.status
        )

        // Limpa o contexto ao finalizar
        LoggingConfig.clearLoggingContext()
    }
}


