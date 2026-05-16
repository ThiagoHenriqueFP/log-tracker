package br.edu.ufersa.distributed_logging.config.mdc

import br.edu.ufersa.distributed_logging.config.LoggingConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
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
    suspend fun executeSubContext(
        block: suspend () -> Unit,
        subContext: String,
        uuid: String,
        depth: Int
    ): Job {
        val originalMdc = MDC.getCopyOfContextMap() ?: emptyMap()
        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        try {
            // Adiciona correlation ID interno único
            MDC.put(loggingConfig.SUB_CONTEXT_ID, uuid)
            MDC.put(loggingConfig.DEPTH, depth.toString())
            MDC.put(loggingConfig.SUB_CONTEXT_NAME, subContext)

            logger.debug("Criado novo sub-contexto MDC com correlationIdInternal")

            return scope.launchWithMdc(block = block)
        } finally {
            // Restaura o MDC original
            logger.debug("Restaurado contexto MDC original")
            MDC.clear()
            originalMdc.forEach { (key, value) ->
                MDC.put(key, value)
            }

        }
    }

    suspend fun executeSubContext(
        subContext: String? = null,
        block: suspend () -> Unit,
    ): Job {
        val uuid = UUID.randomUUID().toString()
        val parentId = loggingConfig.getParentId()
        MDC.put(loggingConfig.PARENT_ID, parentId)
        val newDepth = MDC.get(loggingConfig.DEPTH)?.toIntOrNull()?.plus(1) ?: 1
        return executeSubContext(
            block,
            subContext ?: "$uuid-$newDepth",
            uuid,
            newDepth
        )
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
                executeSubContext {
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
    fun getCurrentSubContextId(): String? {
        return MDC.get(loggingConfig.SUB_CONTEXT_ID)
    }

    /**
     * Verifica se existe um correlation ID interno definido
     *
     * @return true se existe correlation ID interno, false caso contrário
     */
    fun hasSubContextId(): Boolean {
        return getCurrentSubContextId() != null
    }
}
