package br.edu.ufersa.distributed_logging.config.kafka.interceptors

import br.edu.ufersa.distributed_logging.config.LoggingConfig
import org.apache.kafka.clients.producer.ProducerInterceptor
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.KafkaHeaders

@Configuration
class MdcProducerInterceptor(
    private val loggingConfig: LoggingConfig,
) : ProducerInterceptor<String, String> {
    override fun onSend(p0: ProducerRecord<String?, String?>?): ProducerRecord<String?, String?>? {
        val record = p0 ?: return null

        val correlationId = loggingConfig.getCorrelationPair()
        record.headers().add(KafkaHeaders.CORRELATION_ID, correlationId.second.toByteArray(Charsets.UTF_8))

        return record
    }

    override fun close() {
    }

    override fun configure(configs: Map<String?, *>?) {
    }
}