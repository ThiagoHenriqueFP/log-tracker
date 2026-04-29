package br.edu.ufersa.distributed_logging.config.mdc

import kotlinx.coroutines.ThreadContextElement
import org.slf4j.MDC
import kotlin.coroutines.CoroutineContext

/**
 * Element de contexto de corrotina que gerencia o MDC (Mapped Diagnostic Context)
 *
 * Esta classe assegura que o MDC seja propagado corretamente através de
 * limits de thread em corrotinas, mantendo a correlação de logs em execuções assíncronas.
 *
 * @property context Mapa com os valores do MDC a serem propagados
 */
class CoroutineMdcConfig(
    private val context: Map<String, String> = MDC.getCopyOfContextMap() ?: emptyMap()
) : ThreadContextElement<Map<String, String>?> {

    companion object Key : CoroutineContext.Key<CoroutineMdcConfig>

    override val key: CoroutineContext.Key<CoroutineMdcConfig>
        get() = Key

    /**
     * Restaura o MDC com os valores capturados quando a corrotina é executada em uma thread
     */
    override fun restoreThreadContext(context: CoroutineContext, oldState: Map<String, String>?) {
        // Limpa o MDC anterior
        MDC.clear()

        // Restaura o estado anterior se houver
        oldState?.forEach { (key, value) ->
            MDC.put(key, value)
        }
    }

    /**
     * Captura o estado do MDC antes da execução em uma nova thread
     */
    override fun updateThreadContext(context: CoroutineContext): Map<String, String>? {
        val oldMdc = MDC.getCopyOfContextMap()

        // Limpa e insere os valores propagados
        MDC.clear()
        this.context.forEach { (key, value) ->
            MDC.put(key, value)
        }

        return oldMdc
    }
}

/**
 * Factory para criar um elemento de contexto MDC
 *
 * @return MdcContextElement contendo uma cópia do MDC atual
 */
fun mdcContextElement(): CoroutineMdcConfig = CoroutineMdcConfig()

/**
 * Extende CoroutineContext com o elemento MDC
 *
 * @return CoroutineContext com o elemento MDC adicionado
 */
fun CoroutineContext.withMdc(): CoroutineContext = this + mdcContextElement()
