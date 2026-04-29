package br.edu.ufersa.distributed_logging.config

import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.*

/**
 * Classe utilitária para gerenciar campos customizados de logging
 * Utiliza MDC (Mapped Diagnostic Context) do SLF4J para armazenar contexto
 */
@Component
object LoggingConfig {

    const val CORRELATION_ID = "correlationId"
    const val CORRELATION_ID_EXT = "correlationIdExternal"
    const val CORRELATION_ID_INT = "correlationIdInternal"
    const val DEPTH = "depth"
    const val THREAD_NAME = "thread"

    /**
     * Inicializa o contexto de logging com os campos customizados
     * @param correlationId ID único para correlacionar logs relacionados
     * @param depth Profundidade da chamada ou nível de aninhamento
     */
    fun initializeLoggingContext(
        correlationId: String = UUID.randomUUID().toString(),
        depth: Int = 0
    ) {
        MDC.put(CORRELATION_ID, correlationId)
        MDC.put(DEPTH, depth.toString())
        MDC.put(THREAD_NAME, Thread.currentThread().name)
    }

    fun initializeLoggingContext(
        correlationIdExt: String,
        correlationId: String = UUID.randomUUID().toString(),
        depth: Int = 0
    ) {
        MDC.put(CORRELATION_ID, correlationId)
        MDC.put(CORRELATION_ID_EXT, correlationIdExt)
        MDC.put(DEPTH, depth.toString())
        MDC.put(THREAD_NAME, Thread.currentThread().name)
    }

    fun setProperty(key: String, value: String) {
        MDC.put(key, value)
    }

    fun getCorrelationPair():Pair<String, String> {
        return CORRELATION_ID to MDC.get(this.CORRELATION_ID)
    }
    /**
     * Limpa o contexto de logging
     */
    fun clearLoggingContext() {
        MDC.clear()
    }
}

