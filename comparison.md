### Sem metodologia

simples

```json
{"message":"method=ApiControllerImpl, message=Iniciando fluxo de use case simples","level":"INFO","traceId":"69f510669ef72955dcfccdb5a1789976","spanId":"dcfccdb5a1789976"}
{"message":"method=SimpleUseCaseImpl.execute, message=Executando caso de uso simples","level":"INFO","traceId":"69f510669ef72955dcfccdb5a1789976","spanId":"dcfccdb5a1789976"}
```

coroutine

```json
{"message":"method=CoroutineUseCaseImpl.execute, message=Iniciando execução com coroutines sem tratamento mdc","level":"INFO","levelValue":20000,"traceId":"69f512f3a1680f9ecfb89f34a5b6d8ed","spanId":"cfb89f34a5b6d8ed",}
{"message":"method=processarListaDeTarefas, message=Executando tarefa: validar-dados","level":"INFO",}
{"message":"method=processarListaDeTarefas, message=Executando tarefa: processar-pagamento","level":"INFO",}
{"message":"method=processarListaDeTarefas, message=Executando tarefa: enviar-notificacao",,"level":"INFO",}
{"message":"method=validarDados, message=Validando dados de entrada","level":"INFO",}
{"message":"method=enviarNotificacao, message=Enviando notificação","level":"INFO",}
{"message":"method=processarPagamento, message=Processando pagamento",,"level":"INFO",}
{"message":"method=processarListaDeTarefas, message=Tarefa validar-dados concluída","level":"INFO",}
{"message":"method=processarListaDeTarefas, message=Tarefa enviar-notificacao concluída","level":"INFO",}
{"message":"method=processarListaDeTarefas, message=Tarefa processar-pagamento concluída","level":"INFO",}
{"message":"method=CoroutineUseCaseImpl.execute, message=Coroutines lançadas com sucesso sem preservar contexto mdc","level":"INFO","levelValue":20000,"traceId":"69f512f3a1680f9ecfb89f34a5b6d8ed","spanId":"cfb89f34a5b6d8ed",}
```

Async (kafka)

```json
{"message":"method=AsyncUseCaseImpl.sendToKafka, message=Enviando para o kafka","level":"INFO"}
{"message":"method=AsyncProducerImpl.execute, message=Enviando para o kafka","level":"INFO"}
{"message":"method=AsyncProducerImpl.execute, message=Enviado com sucesso","level":"INFO"}
{"message":"method=AsyncUseCaseImpl.sendToKafka, message=Enviando para o kafka com sucesso","level":"INFO"}
{"message":"method=KafkaAsyncConsumerImpl.listen, message=Correlation id null nao encontrado no header do kafka","level":"WARN"}
{"message":"method=KafkaAsyncConsumerImpl.listen, message=Recebendo mensagem do kafka: 1","level":"INFO"}
{"message":"method=AsyncUseCaseImpl.processFromKafka, message=Recebendo via kafka","level":"INFO"}
{"message":"method=AsyncUseCaseImpl.processFromKafka, message=Valor vindo do kafka 1","level":"INFO"}
```

### com metodologia

Simples

```json
{"message":"Iniciando requisição: GET /simples","level":"DEBUG","correlationId":"c9f251c1-c96c-4703-a19f-0ba384358172"}
{"message":"method=ApiControllerImpl, message=Iniciando fluxo de use case simples","level":"INFO","correlationId":"c9f251c1-c96c-4703-a19f-0ba384358172"}
{"message":"method=SimpleUseCaseImpl.execute, message=Executando caso de uso simples","level":"INFO","correlationId":"c9f251c1-c96c-4703-a19f-0ba384358172"}
{"message":"Requisição finalizada: GET /simples - Status: 200","level":"DEBUG","correlationId":"c9f251c1-c96c-4703-a19f-0ba384358172"}
```

Coroutine

```json
{"message":"Iniciando requisição: GET /coroutine","level":"DEBUG","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
{"message":"method=CoroutineUseCaseImpl.execute, message=Iniciando execução com coroutines","level":"INFO","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
{"message":"method=CoroutineUseCaseImpl.execute, message=Coroutines lançadas com sucesso","level":"INFO","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
{"message":"Requisição finalizada: GET /coroutine - Status: 200","level":"DEBUG","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
{"message":"method=CoroutineUseCaseImpl.execute, message=Executando corrotine principal com MDC preservado","level":"INFO","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
{"message":"method=processarListaDeTarefas, message=Iniciando processamento de 3 tarefas","level":"INFO","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
{"message":"Criado novo sub-contexto MDC com correlationIdInternal","level":"DEBUG","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738","correlationIdInternal":"82b8018c-de66-4f8f-970f-fc7b2d8f90a2"}
{"message":"method=processarListaDeTarefas, message=Executando tarefa: validar-dados","level":"INFO","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738","correlationIdInternal":"82b8018c-de66-4f8f-970f-fc7b2d8f90a2"}
{"message":"method=validarDados, message=Validando dados de entrada","level":"INFO","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
{"message":"method=processarListaDeTarefas, message=Tarefa validar-dados concluída","level":"INFO","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
{"message":"Restaurado contexto MDC original","level":"DEBUG","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
{"message":"Criado novo sub-contexto MDC com correlationIdInternal","level":"DEBUG","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738","correlationIdInternal":"0697f872-fc57-4e86-b5af-cae37c36e4af"}
{"message":"method=processarListaDeTarefas, message=Executando tarefa: processar-pagamento","level":"INFO","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738","correlationIdInternal":"0697f872-fc57-4e86-b5af-cae37c36e4af"}
{"message":"method=processarPagamento, message=Processando pagamento","level":"INFO","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
{"message":"method=processarListaDeTarefas, message=Tarefa processar-pagamento concluída","level":"INFO","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
{"message":"Restaurado contexto MDC original","level":"DEBUG","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
{"message":"Criado novo sub-contexto MDC com correlationIdInternal","level":"DEBUG","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738","correlationIdInternal":"45522abf-00e6-4ca6-8c3e-74486ee89ce5"}
{"message":"method=processarListaDeTarefas, message=Executando tarefa: enviar-notificacao","level":"INFO","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738","correlationIdInternal":"45522abf-00e6-4ca6-8c3e-74486ee89ce5"}
{"message":"method=enviarNotificacao, message=Enviando notificação","level":"INFO","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
{"message":"method=processarListaDeTarefas, message=Tarefa enviar-notificacao concluída","level":"INFO","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
{"message":"Restaurado contexto MDC original","level":"DEBUG","correlationId":"baa0eba0-875a-4a14-90be-78c73f85c738"}
```

Async (Kafka)

```json
{"message":"Iniciando requisição: GET /async","level":"DEBUG","correlationId":"52be85d2-75de-4332-8642-d233789461ba"}
{"message":"method=AsyncUseCaseImpl.sendToKafka, message=Enviando para o kafka","level":"INFO","correlationId":"52be85d2-75de-4332-8642-d233789461ba"}
{"message":"method=AsyncProducerImpl.execute, message=Enviando para o kafka","level":"INFO","correlationId":"52be85d2-75de-4332-8642-d233789461ba"}
{"message":"method=AsyncProducerImpl.execute, message=Enviado com sucesso","level":"INFO","correlationId":"52be85d2-75de-4332-8642-d233789461ba"}
{"message":"method=AsyncUseCaseImpl.sendToKafka, message=Enviando para o kafka com sucesso","level":"INFO","correlationId":"52be85d2-75de-4332-8642-d233789461ba"}
{"message":"Requisição finalizada: GET /async - Status: 200","level":"DEBUG","correlationId":"52be85d2-75de-4332-8642-d233789461ba"}
{"message":"method=KafkaAsyncConsumerImpl.listen, message=Recebendo mensagem do kafka: 1","level":"INFO","correlationId":"52be85d2-75de-4332-8642-d233789461ba"}
{"message":"method=AsyncUseCaseImpl.processFromKafka, message=Recebendo via kafka","level":"INFO","correlationId":"52be85d2-75de-4332-8642-d233789461ba"}
{"message":"method=AsyncUseCaseImpl.processFromKafka, message=Valor vindo do kafka 1","level":"INFO","correlationId":"52be85d2-75de-4332-8642-d233789461ba"}
```