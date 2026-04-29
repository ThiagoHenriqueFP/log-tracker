package br.edu.ufersa.distributed_logging.usecase.coroutine

import br.edu.ufersa.distributed_logging.config.mdc.MdcScopeManager
import br.edu.ufersa.distributed_logging.config.mdc.MdcSubContextManager
import br.edu.ufersa.distributed_logging.config.mdc.withMdcContextScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
class CoroutineUseCaseImpl(
    private val mdcScopeManager: MdcScopeManager,
    private val mdcSubContextManager: MdcSubContextManager
) : CoroutineUseCase {

    companion object {
        private val logger = LoggerFactory.getLogger(CoroutineUseCaseImpl::class.java)
    }

    override fun execute() {
        logger.info("method=CoroutineUseCaseImpl.execute, message=Iniciando execução com coroutines")

        // Exemplo 1: Usando o gerenciador de escopos com despachador padrão
        mdcScopeManager.launchWithDefaultDispatcher {
            logger.info("method=CoroutineUseCaseImpl.execute, message=Executando corrotine principal com MDC preservado")

            // Exemplo 2: Cada tarefa em um loop tem seu próprio correlationIdInternal
            processarListaDeTarefas()

//            // Exemplo 3: Usando DSL de sub-contextos
//            executarComSubContextos()
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
                        kotlinx.coroutines.delay(100)

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
            mdcSubContextManager.executeWithNewInternalId {
                logger.info("method=processarListaDeTarefas, message=Executando tarefa: $tarefa")

                // Simula processamento
                kotlinx.coroutines.delay(100)

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
        kotlinx.coroutines.delay(50)
    }

    private suspend fun processarPagamento() {
        logger.info("method=processarPagamento, message=Processando pagamento")
        kotlinx.coroutines.delay(150)
    }

    private suspend fun enviarNotificacao() {
        logger.info("method=enviarNotificacao, message=Enviando notificação")
        kotlinx.coroutines.delay(75)
    }
}