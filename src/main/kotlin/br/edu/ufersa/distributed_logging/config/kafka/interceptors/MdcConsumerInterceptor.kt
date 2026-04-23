package br.edu.ufersa.distributed_logging.config.kafka.interceptors

import br.edu.ufersa.distributed_logging.config.LoggingConfig
import org.apache.kafka.clients.consumer.ConsumerInterceptor
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.springframework.context.annotation.Configuration

@Configuration
class MdcConsumerInterceptor(
    private val loggingConfig: LoggingConfig,
) : ConsumerInterceptor<String, String> {
    override fun onConsume(p0: ConsumerRecords<String?, String?>?): ConsumerRecords<String?, String?>? {
        val records = p0 ?: return null


        records.forEach {
            val header = it.headers().lastHeader(loggingConfig.CORRELATION_ID)
        }
    }

    override fun onCommit(offsets: Map<TopicPartition?, OffsetAndMetadata?>?) {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun configure(configs: Map<String?, *>?) {
        TODO("Not yet implemented")
    }
}