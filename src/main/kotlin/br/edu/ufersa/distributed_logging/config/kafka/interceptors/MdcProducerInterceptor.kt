package br.edu.ufersa.distributed_logging.config.kafka.interceptors

import br.edu.ufersa.distributed_logging.config.MdcInterceptor
import org.apache.kafka.clients.producer.ProducerInterceptor
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.KafkaHeaders

/**
 * Interceptor de Producer que propaga MDC (correlationId) para headers Kafka
 *
 * Este interceptor:
 * - É instanciado por Kafka via reflexão (sem parâmetros Spring)
 * - Acessa MDC diretamente para obter correlationId
 * - Adiciona correlationId aos headers de cada mensagem
 * - Preserva rastreabilidade distribuída
 */
class MdcProducerInterceptor : ProducerInterceptor<String, String>, MdcInterceptor() {

    companion object {
            private val log = LoggerFactory.getLogger(MdcProducerInterceptor::class.java)
    }

    /**
     * Chamado por Kafka antes de enviar a mensagem
     * Adiciona o correlationId do MDC aos headers
     */
    override fun onSend(record: ProducerRecord<String?, String?>?): ProducerRecord<String?, String?>? {
        if (record == null) return null

        try {
            // Obtém o correlationId do MDC
            val correlationId = createCorrelationId()

                record.headers().add(
                    KafkaHeaders.CORRELATION_ID,
                    correlationId.toByteArray(Charsets.UTF_8)
                )
        } catch (e: Exception) {
            // Log silencioso para não quebrar o envio
            log.error("Erro ao adicionar MDC aos headers Kafka: ${e.message}", e)
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