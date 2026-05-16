package br.edu.ufersa.distributed_logging.config

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
class LoggingInterceptor : HandlerInterceptor {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        // Gera um correlationId único para essa requisição
        val correlationId = request.getHeader("X-Correlation-ID") ?: UUID.randomUUID().toString()
        LoggingConfig.initializeLoggingContext(correlationId, 0)

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


