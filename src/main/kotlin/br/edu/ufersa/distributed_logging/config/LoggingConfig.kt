package br.edu.ufersa.distributed_logging.config

import org.slf4j.MDC
import java.util.*

/**
 * Classe utilitária para gerenciar campos customizados de logging
 * Utiliza MDC (Mapped Diagnostic Context) do SLF4J para armazenar contexto
 */
object LoggingConfig {

    const val CORRELATION_ID = "correlationId"
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

    /**
     * Atualiza a profundidade de logging
     */
    fun setDepth(depth: Int) {
        MDC.put(DEPTH, depth.toString())
    }

    /**
     * Obtém o correlationId atual
     */
    fun getCorrelationId(): String? = MDC.get(CORRELATION_ID)

    /**
     * Limpa o contexto de logging
     */
    fun clearLoggingContext() {
        MDC.clear()
    }
}

