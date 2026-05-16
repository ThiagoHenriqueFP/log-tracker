//package br.edu.ufersa.distributed_logging.config.mdc
//
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Job
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//
///**
// * Extensões para facilitar o uso de sub-contextos MDC em coroutines
// */
//
///**
// * Extensão para CoroutineScope que executa um bloco com novo correlation ID interno
// *
// * @param subContextManager Instância do MdcSubContextManager
// * @param block Bloco a executar com novo ID interno
// * @return Job da coroutine
// */
//fun CoroutineScope.launchWithNewInternalId(
//    subContextManager: MdcSubContextManager,
//    block: suspend () -> Unit
//): Job = this.launchWithMdc {
//    subContextManager.executeSubContext(block)
//}
//
///**
// * Extensão para executar uma lista de tarefas, cada uma com seu próprio correlation ID interno
// *
// * @param subContextManager Instância do MdcSubContextManager
// * @param tasks Lista de tarefas a executar
// * @param taskExecutor Função que executa cada tarefa
// * @return Lista de Jobs para monitorar as execuções
// */
//fun <T> CoroutineScope.launchParallelWithInternalIds(
//    subContextManager: MdcSubContextManager,
//    tasks: List<T>,
//    taskExecutor: suspend (T) -> Unit
//): List<Job> = subContextManager.executeParallelWithInternalIds(tasks, taskExecutor)
//
///**
// * DSL para criar blocos MDC aninhados com IDs internos únicos
// *
// * Exemplo de uso:
// * ```
// * mdcScopeManager.launchWithDefaultDispatcher {
// *     logger.info("Inicio do processamento")
// *
// *     subContext("processamento-dados") {
// *         logger.info("Processando dados")
// *
// *         subContext("validacao") {
// *             logger.info("Validando dados")
// *         }
// *
// *         subContext("persistencia") {
// *             logger.info("Persistindo dados")
// *         }
// *     }
// * }
// * ```
// */
//class MdcContextScope(private val subContextManager: MdcSubContextManager) {
//
//    companion object {
//        private val logger: Logger = LoggerFactory.getLogger(MdcContextScope::class.java)
//    }
//
//    /**
//     * Executa um bloco dentro de um sub-contexto MDC
//     *
//     * @param contextName Nome descritivo do contexto (opcional)
//     * @param block Bloco a executar
//     */
//    suspend fun subContext(contextName: String? = null, block: suspend MdcContextScope.() -> Unit) {
//        subContextManager.executeSubContext {
//            if (contextName != null) {
//                logger.debug("Entrando no sub-contexto: $contextName")
//            }
//            this.block()
//            if (contextName != null) {
//                logger.debug("Saindo do sub-contexto: $contextName")
//            }
//        }
//    }
//
//    /**
//     * Executa um bloco com um ID interno específico
//     *
//     * @param internalId ID interno a ser usado
//     * @param contextName Nome descritivo do contexto (opcional)
//     * @param block Bloco a executar
//     */
//    suspend fun subContextWithId(
//        internalId: String,
//        contextName: String? = null,
//        block: suspend MdcContextScope.() -> Unit
//    ) {
//        subContextManager.executeWithInternalId(internalId) {
//            if (contextName != null) {
//                logger.debug("Entrando no sub-contexto: $contextName (ID: $internalId)")
//            }
//            this.block()
//            if (contextName != null) {
//                logger.debug("Saindo do sub-contexto: $contextName")
//            }
//        }
//    }
//}
//
///**
// * Cria um escopo MDC para uso com DSL de sub-contextos
// *
// * @param subContextManager Instância do MdcSubContextManager
// * @param block Bloco DSL a executar
// */
//suspend fun withMdcContextScope(
//    subContextManager: MdcSubContextManager,
//    block: suspend MdcContextScope.() -> Unit
//) {
//    val scope = MdcContextScope(subContextManager)
//    scope.block()
//}
