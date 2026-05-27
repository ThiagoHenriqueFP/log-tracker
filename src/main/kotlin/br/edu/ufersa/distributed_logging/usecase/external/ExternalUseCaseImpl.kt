package br.edu.ufersa.distributed_logging.usecase.external

import br.edu.ufersa.distributed_logging.client.SimpleWebClient
import org.springframework.stereotype.Component

@Component
class ExternalUseCaseImpl(
    private val client: SimpleWebClient
) : ExternalUseCase {
    override fun execute(str: String): String? {
        return client.sendEvent(str)
    }
}