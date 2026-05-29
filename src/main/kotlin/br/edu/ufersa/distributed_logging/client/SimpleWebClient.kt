package br.edu.ufersa.distributed_logging.client

import br.edu.ufersa.distributed_logging.config.MdcInterceptor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class SimpleWebClient(
    @Value($$"${simple.service.base-url}")
    private val baseUrl: String,
    private val mdcInterceptor: MdcInterceptor,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(SimpleWebClient::class.java)
    }

    private val webClient: HttpClient = HttpClient.newBuilder()
        .build()

    fun sendEvent(str: String): String? {
        return try {
            val request = HttpRequest.newBuilder()
                .uri(
                    URI(
                        baseUrl
                            .plus("/simples/")
                            .plus(encode(str))
                    )
                )
                .header("Content-Type", "text/plain")
                .header("X-Correlation-ID", mdcInterceptor.createCorrelationId())
                .GET()
                .build()
            val response = webClient.send(request, HttpResponse.BodyHandlers.ofString())

            if (response.statusCode() in 200..299) {
                response.body()
            } else {
                logger.error(
                    "Resposta inesperada ao chamar POST {}/simples: {} - {}",
                    baseUrl,
                    response.statusCode(),
                    response.body()
                )
                throw RuntimeException("Resposta inesperada: ${response.statusCode()}")
            }
        } catch (e: Exception) {
            logger.error("Erro ao chamar POST {}/simples: {}", baseUrl, e.message)
            null
        }
    }

    private fun encode(str: String): String {
        return URLEncoder.encode(str, "UTF-8")
    }
}


