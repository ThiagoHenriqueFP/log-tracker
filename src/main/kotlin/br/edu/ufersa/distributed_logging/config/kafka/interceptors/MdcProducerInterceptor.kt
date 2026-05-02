package br.edu.ufersa.distributed_logging.config.kafka.interceptors

import org.apache.kafka.clients.producer.ProducerInterceptor
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.MDC

/**
 * Interceptor de Producer que propaga MDC (correlationId) para headers Kafka
 *
 * Este interceptor:
 * - É instanciado por Kafka via reflexão (sem parâmetros Spring)
 * - Acessa MDC diretamente para obter correlationId
 * - Adiciona correlationId aos headers de cada mensagem
 * - Preserva rastreabilidade distribuída
 */
class MdcProducerInterceptor : ProducerInterceptor<String, String> {

    companion object {
        private const val CORRELATION_ID_KEY = "correlationId"
        private const val CORRELATION_ID_INTERNAL_KEY = "correlationIdInternal"
    }

    /**
     * Chamado por Kafka antes de enviar a mensagem
     * Adiciona o correlationId do MDC aos headers
     */
    override fun onSend(record: ProducerRecord<String?, String?>?): ProducerRecord<String?, String?>? {
        if (record == null) return null

        try {
            // Obtém o correlationId do MDC
            val correlationId = MDC.get(CORRELATION_ID_KEY)
            if (correlationId != null) {
                record.headers().add(
                    CORRELATION_ID_KEY,
                    correlationId.toByteArray(Charsets.UTF_8)
                )
            }

            // Obtém o correlationIdInternal do MDC (opcional)
            val correlationIdInternal = MDC.get(CORRELATION_ID_INTERNAL_KEY)
            if (correlationIdInternal != null) {
                record.headers().add(
                    CORRELATION_ID_INTERNAL_KEY,
                    correlationIdInternal.toByteArray(Charsets.UTF_8)
                )
            }
        } catch (e: Exception) {
            // Log silencioso para não quebrar o envio
            System.err.println("Erro ao adicionar MDC aos headers Kafka: ${e.message}")
        }

        return record
    }

    /**
     * Chamado quando o producer é fechado
     */
    override fun close() {
        // Cleanup se necessário
    }

    /**
     * Configuração do interceptor
     */
    override fun configure(configs: Map<String?, *>?) {
        // Não precisa de configuração adicional
    }
}