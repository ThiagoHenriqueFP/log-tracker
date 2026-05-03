package br.edu.ufersa.distributed_logging.config.mdc

import br.edu.ufersa.distributed_logging.config.LoggingConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.*

/**
 * Utilitário para criar sub-contextos MDC com correlation IDs internos únicos
 *
 * Permite que cada fluxo/sub-tarefa dentro de uma coroutine tenha seu próprio
 * correlation ID interno, mantendo a rastreabilidade enquanto permite
 * identificar operações específicas.
 *
 * Exemplo de uso:
 * ```
 * mdcScopeManager.launchWithDefaultDispatcher {
 *     listOf("task1", "task2", "task3").forEach { task ->
 *         mdcSubContextManager.executeWithNewInternalId {
 *             logger.info("Executando tarefa: $task")
 *             // Cada tarefa terá seu próprio correlationIdInternal único
 *         }
 *     }
 * }
 * ```
 */
@Component
class MdcSubContextManager(
    private val loggingConfig: LoggingConfig
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MdcSubContextManager::class.java)
    }

    /**
     * Executa um bloco de código com um novo correlation ID interno único
     *
     * Preserva o MDC atual e adiciona um correlationIdInternal único para este sub-contexto.
     * Após a execução, restaura o MDC anterior.
     *
     * @param block Bloco de código a executar com novo ID interno
     */
    suspend fun executeWithNewInternalId(block: suspend () -> Unit) {
        val originalMdc = MDC.getCopyOfContextMap() ?: emptyMap()

        try {
            val parentId: String? = MDC.get(LoggingConfig.CORRELATION_ID_INT)
            if (parentId != null) {
                logger.info("parentId=$parentId")
                MDC.put(LoggingConfig.CORRELATION_ID_PARENT, parentId)
            }

            // Adiciona correlation ID interno único
            MDC.put(loggingConfig.CORRELATION_ID_INT, UUID.randomUUID().toString())

            logger.debug("Criado novo sub-contexto MDC com correlationIdInternal")

            block()
        } finally {
            // Restaura o MDC original
            MDC.clear()
            originalMdc.forEach { (key, value) ->
                MDC.put(key, value)
            }

            logger.debug("Restaurado contexto MDC original")
        }
    }

    /**
     * Executa um bloco de código com um correlation ID interno específico
     *
     * Útil quando você quer controlar o ID interno ou reutilizar um ID existente.
     *
     * @param internalId ID interno a ser usado
     * @param block Bloco de código a executar
     */
    suspend fun executeWithInternalId(internalId: String, block: suspend () -> Unit) {
        val originalMdc = MDC.getCopyOfContextMap() ?: emptyMap()

        try {
            val parentId: String? = MDC.get(LoggingConfig.CORRELATION_ID_INT)
            if (parentId != null) {
                MDC.put(LoggingConfig.CORRELATION_ID_PARENT, parentId)
            }

            MDC.put(loggingConfig.CORRELATION_ID_INT, internalId)
            logger.debug("Definido correlationIdInternal: $internalId")

            block()
        } finally {
            MDC.clear()
            originalMdc.forEach { (key, value) ->
                MDC.put(key, value)
            }
        }
    }

    /**
     * Cria um novo contexto MDC com ID interno único mantendo o contexto pai
     *
     * @param parentContext Contexto MDC pai (opcional, usa atual se não informado)
     * @return Novo contexto MDC com correlationIdInternal único
     */
    fun createSubContext(
        parentContext: Map<String, String> = MDC.getCopyOfContextMap() ?: emptyMap()
    ): Map<String, String> {
        val newContext = parentContext.toMutableMap()
        newContext[loggingConfig.CORRELATION_ID_INT] = UUID.randomUUID().toString()
        return newContext
    }

    /**
     * Incrementa a profundidade do MDC para indicar aninhamento
     *
     * @param block Bloco a executar com profundidade incrementada
     */
    suspend fun executeWithIncreasedDepth(block: suspend () -> Unit) {
        val originalMdc = MDC.getCopyOfContextMap() ?: emptyMap()
        val currentDepth = originalMdc[loggingConfig.DEPTH]?.toIntOrNull() ?: 0

        try {
            MDC.put(loggingConfig.DEPTH, (currentDepth + 1).toString())
            MDC.put(loggingConfig.CORRELATION_ID_INT, UUID.randomUUID().toString())

            logger.debug("Incrementada profundidade para ${currentDepth + 1} com novo correlationIdInternal")

            block()
        } finally {
            MDC.clear()
            originalMdc.forEach { (key, value) ->
                MDC.put(key, value)
            }
        }
    }

    /**
     * Executa múltiplas tarefas em paralelo, cada uma com seu próprio correlation ID interno
     *
     * @param tasks Lista de tarefas a executar
     * @param taskExecutor Função que executa cada tarefa com seu ID interno
     * @return Lista de Jobs para monitorar as execuções
     */
    fun <T> executeParallelWithInternalIds(
        tasks: List<T>,
        taskExecutor: suspend (T) -> Unit
    ): List<Job> {
        return tasks.map { task ->
            val scope = CoroutineScope(Dispatchers.Default)
            scope.launchWithMdc {
                executeWithNewInternalId {
                    taskExecutor(task)
                }
            }
        }
    }

    /**
     * Obtém o correlation ID interno atual
     *
     * @return Correlation ID interno atual ou null se não existir
     */
    fun getCurrentInternalId(): String? {
        return MDC.get(loggingConfig.CORRELATION_ID_INT)
    }

    /**
     * Verifica se existe um correlation ID interno definido
     *
     * @return true se existe correlation ID interno, false caso contrário
     */
    fun hasInternalId(): Boolean {
        return getCurrentInternalId() != null
    }
}
