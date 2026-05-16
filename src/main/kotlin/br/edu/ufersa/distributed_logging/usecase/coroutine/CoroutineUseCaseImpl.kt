package br.edu.ufersa.distributed_logging.usecase.coroutine

import br.edu.ufersa.distributed_logging.config.mdc.MdcContextManager
import br.edu.ufersa.distributed_logging.config.mdc.MdcSubContextManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Component
class CoroutineUseCaseImpl(
    private val mdcContextManager: MdcContextManager,
    private val mdcSubContextManager: MdcSubContextManager
) : CoroutineUseCase {

    companion object {
        private val logger = LoggerFactory.getLogger(CoroutineUseCaseImpl::class.java)
    }

    override fun execute() {
        logger.info("method=CoroutineUseCaseImpl.execute, message=Iniciando execução com coroutines")

        mdcContextManager.launchWithDefaultDispatcher {
            logger.info("method=CoroutineUseCaseImpl.execute, message=Executando corrotine principal com MDC preservado")
            processarListaDeTarefas()
        }

        logger.info("method=CoroutineUseCaseImpl.execute, message=Coroutines lançadas com sucesso")
    }

    override fun executeWithOutContext() {
        logger.info("method=CoroutineUseCaseImpl.execute, message=Iniciando execução com coroutines sem tratamento mdc")

        runBlocking {
            Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher().use {
                val tarefas = listOf("validar-dados", "processar-pagamento", "enviar-notificacao")
                tarefas.forEach { tarefa ->
                    launch(Dispatchers.IO) {
                        logger.info("method=processarListaDeTarefas, message=Executando tarefa: $tarefa")

                        // Simula processamento
                        kotlinx.coroutines.delay(100.milliseconds)

                        when (tarefa) {
                            "validar-dados" -> validarDados()
                            "processar-pagamento" -> processarPagamento()
                            "enviar-notificacao" -> enviarNotificacao()
                        }

                        logger.info("method=processarListaDeTarefas, message=Tarefa $tarefa concluída")
                    }
                }
            }
        }

        logger.info("method=CoroutineUseCaseImpl.execute, message=Coroutines lançadas com sucesso sem preservar contexto mdc")
    }

    /**
     * Exemplo de processamento de lista onde cada item tem seu próprio correlation ID interno
     */
    private suspend fun processarListaDeTarefas() {
        val tarefas = listOf("validar-dados", "processar-pagamento", "enviar-notificacao")

        logger.info("method=processarListaDeTarefas, message=Iniciando processamento de ${tarefas.size} tarefas")

        // Cada tarefa será executada com seu próprio correlationIdInternal único
        tarefas.forEach { tarefa ->
            mdcSubContextManager.executeSubContext(tarefa) {
                logger.info("method=processarListaDeTarefas, message=Executando tarefa: $tarefa")

                // Simula processamento
                kotlinx.coroutines.delay(100.milliseconds)

                when (tarefa) {
                    "validar-dados" -> validarDados()
                    "processar-pagamento" -> processarPagamento()
                    "enviar-notificacao" -> enviarNotificacao()
                }

                logger.info("method=processarListaDeTarefas, message=Tarefa $tarefa concluída")
            }
        }
    }

    /**
     * Exemplo usando DSL de sub-contextos para aninhamento estruturado
     */
//    private suspend fun executarComSubContextos() {
//        withMdcContextScope(mdcSubContextManager) {
//            logger.info("method=executarComSubContextos, message=Entrando no contexto principal")
//
//            subContext("etapa-preparacao") {
//                logger.info("method=executarComSubContextos, message=Executando preparação")
//                prepararDados()
//            }
//
//            subContext("etapa-processamento") {
//                logger.info("method=executarComSubContextos, message=Executando processamento")
//
//                // Sub-sub-contextos
//                subContext("sub-etapa-calculo") {
//                    logger.info("method=executarComSubContextos, message=Executando cálculos")
//                    executarCalculos()
//                }
//
//                subContext("sub-etapa-validacao") {
//                    logger.info("method=executarComSubContextos, message=Executando validação")
//                    validarResultados()
//                }
//            }
//
//            subContext("etapa-finalizacao") {
//                logger.info("method=executarComSubContextos, message=Executando finalização")
//                finalizarProcessamento()
//            }
//        }
//    }

    private suspend fun validarDados() {
        logger.info("method=validarDados, message=Validando dados de entrada")
        prepararDados()
        executarCalculos()
        validarResultados()
        registrarEvento("validar-dados")
        kotlinx.coroutines.delay(50.milliseconds)
    }

    private suspend fun processarPagamento() {
        logger.info("method=processarPagamento, message=Processando pagamento")
        atualizarEstado("processar-pagamento")
        registrarEvento("processar-pagamento")
        kotlinx.coroutines.delay(150.milliseconds)
    }

    private suspend fun enviarNotificacao() {
        logger.info("method=enviarNotificacao, message=Enviando notificação")
        limparRecursos()
        finalizarProcessamento()
        registrarEvento("enviar-notificacao")
        kotlinx.coroutines.delay(75.milliseconds)
    }

    // Funções adicionais simples apenas com logs para aumentar o tamanho do exemplo
    private suspend fun prepararDados() {
        logger.info("method=prepararDados, message=Preparando dados para processamento")
        val resultados = listOf(1..3).map {
            mdcSubContextManager.executeSubContext("preparacao-dados") {
                logger.info("method=prepararDados, message=Processando dado: $it")
                kotlinx.coroutines.delay(10.milliseconds)
            }
        }

        resultados.joinAll()
        logger.info("method=prepararDados, message=Preparação de dados concluída")
    }

    private suspend fun executarCalculos() {
        logger.info("method=executarCalculos, message=Executando cálculos auxiliares")
    }

    private suspend fun validarResultados() {
        logger.info("method=validarResultados, message=Validando resultados obtidos")
    }

    private suspend fun finalizarProcessamento() {
        logger.info("method=finalizarProcessamento, message=Finalizando processamento")
    }

    private suspend fun limparRecursos() {
        logger.info("method=limparRecursos, message=Limpeza de recursos iniciada")
    }

    private suspend fun registrarEvento(nomeEvento: String) {
        logger.info("method=registrarEvento, event=$nomeEvento, message=Registrando evento")
    }

    private suspend fun atualizarEstado(estado: String) {
        logger.info("method=atualizarEstado, state=$estado, message=Atualizando estado interno")
    }
}