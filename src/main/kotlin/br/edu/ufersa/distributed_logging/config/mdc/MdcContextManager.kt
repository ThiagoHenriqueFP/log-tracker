package br.edu.ufersa.distributed_logging.config.mdc

import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.coroutines.CoroutineContext

/**
 * Gerenciador centralizado de escopos de corrotina com suporte a MDC
 *
 * Este componente oferece uma forma padronizada de criar escopos de corrotinas
 * que mantêm o contexto de MDC através de execuções assíncronas.
 *
 * Exemplo de uso:
 * ```
 * @Autowired
 * private lateinit var mdcScopeManager: MdcScopeManager
 *
 * fun procesarDados() {
 *     mdcScopeManager.launchWithDefaultDispatcher {
 *         // correlationId será preservado aqui mesmo em thread diferente
 *         logger.info("Processando com correlationId propagado")
 *     }
 * }
 * ```
 */
@Component
class MdcContextManager {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MdcContextManager::class.java)
    }

    /**
     * Handler de exceção que registra erros e preserva o contexto MDC
     */
    private fun createExceptionHandler(): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            logger.error("Erro não tratado em corrotina MDC-aware", exception)
        }
    }

    /**
     * Cria um contexto de corrotina com MDC e tratamento de exceção
     */
    private fun createContext(dispatcher: CoroutineDispatcher): CoroutineContext {
//        this.internalCorrelationId()
        return dispatcher + mdcContextElement() + createExceptionHandler()
    }

    /**
     * Lança uma corrotina com o despachador padrão preservando o MDC
     *
     * @param block Bloco de código a executar
     * @return Job para monitorar a corrotina
     */
    fun launchWithDefaultDispatcher(block: suspend () -> Unit): Job {
        val scope = CoroutineScope(createContext(Dispatchers.Default))
        return scope.launchWithMdc(block = block)
    }

//    /**
//     * Lança uma corrotina com o despachador IO preservando o MDC
//     *
//     * Ideal para operações de I/O como banco de dados ou chamadas HTTP
//     *
//     * @param block Bloco de código a executar
//     * @return Job para monitorar a corrotina
//     */
//    fun launchWithIODispatcher(block: suspend () -> Unit): Job {
//        val scope = CoroutineScope(createContext(Dispatchers.IO))
//        return scope.launchWithMdc(block = block)
//    }
//
//    /**
//     * Lança uma corrotina com despachador customizado preservando o MDC
//     *
//     * @param dispatcher Despachador a utilizar
//     * @param block Bloco de código a executar
//     * @return Job para monitorar a corrotina
//     */
//    fun launchWithDispatcher(dispatcher: CoroutineDispatcher, block: suspend () -> Unit): Job {
//        val scope = CoroutineScope(createContext(dispatcher))
//        return scope.launchWithMdc(block = block)
//    }
//
//    /**
//     * Executa uma operação assíncrona com o despachador padrão preservando o MDC
//     *
//     * @param block Bloco de código a executar
//     * @return Deferred contendo o resultado
//     */
//    suspend fun <T> asyncWithDefaultDispatcher(block: suspend () -> T): T {
//
//        val scope = CoroutineScope(createContext(Dispatchers.Default))
//        return scope.asyncWithMdc(block = block).await()
//    }
//
//    /**
//     * Executa uma operação assíncrona com o despachador IO preservando o MDC
//     *
//     * @param block Bloco de código a executar
//     * @return Deferred contendo o resultado
//     */
//    suspend fun <T> asyncWithIODispatcher(block: suspend () -> T): T {
//        val scope = CoroutineScope(createContext(Dispatchers.IO))
//        return scope.asyncWithMdc(block = block).await()
//    }
//
//    /**
//     * Executa uma operação assíncrona com despachador customizado preservando o MDC
//     *
//     * @param dispatcher Despachador a utilizar
//     * @param block Bloco de código a executar
//     * @return Deferred contendo o resultado
//     */
//    suspend fun <T> asyncWithDispatcher(
//        dispatcher: CoroutineDispatcher,
//        block: suspend () -> T
//    ): T {
//        val scope = CoroutineScope(createContext(dispatcher))
//        return scope.asyncWithMdc(block = block).await()
//    }
//
//    /**
//     * Cria um escopo de corrotina que preserva o MDC
//     *
//     * Útil para casos onde você precisa de controle fino sobre a execução
//     *
//     * @param dispatcher Despachador a utilizar (padrão: Default)
//     * @return CoroutineScope com MDC configurado
//     */
//    fun createMdcScope(dispatcher: CoroutineDispatcher = Dispatchers.Default): CoroutineScope {
//        return CoroutineScope(createContext(dispatcher))
//    }
//
//    private fun internalCorrelationId() {
//        MDC.put(loggingConfig.CORRELATION_ID_INT, UUID.randomUUID().toString())
//    }
}


