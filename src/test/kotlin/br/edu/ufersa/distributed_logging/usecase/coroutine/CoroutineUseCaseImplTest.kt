package br.edu.ufersa.distributed_logging.usecase.coroutine

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.slf4j.LoggerFactory

class CoroutineUseCaseImplTest {

    @Test
    fun `execute should log message using coroutine`() {
        val logger = LoggerFactory.getLogger(CoroutineUseCaseImpl::class.java) as Logger
        val appender = ListAppender<ILoggingEvent>()
        appender.start()
        logger.addAppender(appender)

        try {
            CoroutineUseCaseImpl().execute()

            val events = appender.list.filter {
                it.formattedMessage.contains("method=CoroutineUseCaseImpl.execute")
            }

            assertTrue(events.isNotEmpty(), "Expected execute() to log a coroutine message")
            assertEquals(
                "method=CoroutineUseCaseImpl.execute, message=Executando log simples com coroutine",
                events.last().formattedMessage,
            )
        } finally {
            logger.detachAppender(appender)
            appender.stop()
        }
    }
}

