# Configuração de Logging JSON com Campos Customizados

Este projeto está configurado para gerar logs em formato JSON com os campos customizados: **correlationId**, **depth** e **thread**.

## 📋 O que foi configurado

### 1. **Dependência Logstash Logback Encoder**
- Adicionada ao `build.gradle.kts` para suporte a JSON estruturado

### 2. **Arquivo `logback-spring.xml`**
- Configuração de appenders JSON para console e arquivos
- Inclui rotação automática de logs
- Campos customizados no JSON: `correlationId`, `depth`, `thread`
- Diferentes níveis de log por ambiente (dev, prod, test)

### 3. **LoggingConfig.kt**
Classe utilitária que gerencia o contexto de logging usando MDC (Mapped Diagnostic Context):
```kotlin
// Inicializar contexto
LoggingConfig.initializeLoggingContext(
    correlationId = "uuid-da-requisição",
    depth = 0
)

// Atualizar profundidade
LoggingConfig.setDepth(2)

// Obter correlationId
val id = LoggingConfig.getCorrelationId()

// Limpar contexto
LoggingConfig.clearLoggingContext()
```

### 4. **LoggingInterceptor.kt**
Interceptor HTTP que:
- Lê ou gera um `correlationId` para cada requisição
- Inicializa automaticamente o contexto de logging
- Limpa o contexto ao final da requisição
- Suporta header customizado `X-Correlation-ID`

### 5. **WebMvcConfig.kt**
Registra o interceptor de logging na aplicação

### 6. **LoggingExampleController.kt**
Controller de exemplo que demonstra o uso do LoggingConfig

## 🚀 Como usar

### Exemplo básico:
```kotlin
import org.slf4j.LoggerFactory
import br.edu.ufersa.distributed_logging.config.LoggingConfig

class MinhaClasse {
    private val logger = LoggerFactory.getLogger(javaClass)
    
    fun minhaFuncao() {
        // O contexto já é inicializado automaticamente pelo interceptor
        logger.info("Meu log com correlationId automático")
        
        // Atualizar profundidade se necessário
        LoggingConfig.setDepth(2)
        logger.debug("Log com profundidade 2")
    }
}
```

### Requisição HTTP com correlationId customizado:
```bash
curl -H "X-Correlation-ID: my-custom-id-123" http://localhost:8080/api/logs/test
```

## 📊 Exemplo de saída JSON

Cada log será um JSON estruturado como:
```json
{
  "@timestamp": "2026-04-13T10:30:45.123Z",
  "@version": 1,
  "message": "Meu log com correlationId automático",
  "logger": "br.edu.ufersa.distributed_logging.MinhaClasse",
  "threadName": "http-nio-8080-exec-1",
  "level": "INFO",
  "levelValue": 20000,
  "application": "distributed-logging",
  "mdc": {
    "correlationId": "550e8400-e29b-41d4-a716-446655440000",
    "depth": "0",
    "thread": "http-nio-8080-exec-1"
  }
}
```

## 📁 Arquivos criados/modificados

- ✅ `build.gradle.kts` - Adicionada dependência logstash-logback-encoder
- ✅ `src/main/resources/logback-spring.xml` - Configuração de logging JSON
- ✅ `src/main/kotlin/.../config/LoggingConfig.kt` - Gerenciador de contexto
- ✅ `src/main/kotlin/.../config/LoggingInterceptor.kt` - Interceptor HTTP
- ✅ `src/main/kotlin/.../config/WebMvcConfig.kt` - Configuração MVC
- ✅ `src/main/kotlin/.../web/LoggingExampleController.kt` - Controller exemplo

## 🔧 Configurações por Ambiente

### Desenvolvimento (dev, default)
- Nível: DEBUG
- Saída: Console + Arquivo + Arquivo de Erros
- Mais verboso para debugging

### Produção (prod)
- Nível: INFO
- Saída: Arquivo + Arquivo de Erros
- Sem console

### Testes (test)
- Nível: INFO
- Saída: Console apenas

## 📝 Campos inclusos no JSON

- `@timestamp` - Data/hora do evento
- `@version` - Versão do formato
- `message` - Mensagem de log
- `logger` - Nome da classe que gerou o log
- `threadName` - Nome da thread
- `level` - Nível de severidade (INFO, DEBUG, ERROR, etc)
- `levelValue` - Valor numérico do nível
- `application` - Nome da aplicação
- `mdc` - Contexto customizado com:
  - `correlationId` - ID para correlacionar logs relacionados
  - `depth` - Profundidade/nível de aninhamento
  - `thread` - Nome da thread (redundante com threadName)

## 🔍 Endpoints de teste

- `GET /api/logs/test` - Log básico
- `GET /api/logs/test-depth/{level}` - Log com profundidade customizada

