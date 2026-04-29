package br.edu.ufersa.distributed_logging.config.mdc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Extensão para [CoroutineScope.launch] que propaga automaticamente o MDC
 *
 * Exemplo de uso:
 * ```
 * CoroutineScope(Dispatchers.Default).launchWithMdc {
 *     logger.info("Este log manterá o correlationId do contexto pai")
 * }
 * ```
 *
 * @param context Contexto adicional da corrotina (default + MDC)
 * @param start Modo de início da corrotina
 * @param block Função a ser executada na corrotina
 * @return Job da corrotina lançada
 */
inline fun CoroutineScope.launchWithMdc(
    context: CoroutineContext = this.coroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend () -> Unit
): Job = launch(context.withMdc(), start) {
    block()
}

/**
 * Extensão para [CoroutineScope.async] que propaga automaticamente o MDC
 *
 * Exemplo de uso:
 * ```
 * val result = CoroutineScope(Dispatchers.Default).asyncWithMdc {
 *     logger.info("Operação assíncrona com correlationId preservado")
 *     "resultado"
 * }.await()
 * ```
 *
 * @param context Contexto adicional da corrotina (default + MDC)
 * @param start Modo de início da corrotina
 * @param block Função a ser executada na corrotina
 * @return Deferred<T> que pode ser aguardado para obter o resultado
 */
inline fun <T> CoroutineScope.asyncWithMdc(
    context: CoroutineContext = this.coroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: suspend () -> T
): Deferred<T> = async(context.withMdc(), start) {
    block()
}


