MDC para Corrotinas - Guia de Uso
==================================

## Visão Geral

Esta configuração gerencia o **MDC (Mapped Diagnostic Context)** através de limites de thread em corrotinas Kotlin, 
garantindo que correlationIds e outros contextos de logging sejam preservados em execuções assíncronas.

## Componentes Principais

### 1. **MdcContextElement**
Elemento de contexto de corrotina que implements `ThreadContextElement`. 
Ele captura e restaura o MDC ao mudar entre threads.

### 2. **CoroutineMdcExtensions**
Extensões Kotlin que facilitam o uso de corrotinas com MDC:
- `launchWithMdc()` - Lança corrotina preservando MDC
- `asyncWithMdc()` - Executa assincronamente preservando MDC

### 3. **MdcScopeManager**
Componente Spring Bean que oferece uma interface simplificada para criar escopos 
de corrotinas que preservam o MDC.

## Como Usar

### Opção 1: Usar o MdcScopeManager (Recomendado)

```kotlin
import br.edu.ufersa.distributed_logging.config.mdc.MdcScopeManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MinhaProcessadora(
    @Autowired private val mdcScopeManager: MdcScopeManager
) {
    
    private val logger = LoggerFactory.getLogger(javaClass)
    
    fun processarDados() {
        logger.info("Iniciando processamento")
        
        // Lança corrotina com MDC preservado (Despachador padrão)
        mdcScopeManager.launchWithDefaultDispatcher {
            logger.info("Processando com correlationId preservado")
            // Seu código aqui
        }
    }
    
    fun buscarDadosExterno() {
        // Usar Dispatchers.IO para operações de rede/banco de dados
        mdcScopeManager.launchWithIODispatcher {
            logger.info("Buscando dados externamente com correlationId")
            // Chamada HTTP ou banco de dados
        }
    }
}
```

### Opção 2: Usar as Extensões Diretamente

```kotlin
import br.edu.ufersa.distributed_logging.config.mdc.launchWithMdc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MeuServico {
    fun executar() {
        val scope = CoroutineScope(Dispatchers.Default)
        
        scope.launchWithMdc {
            // correlationId será preservado
            logger.info("Executando com MDC")
        }
        
        scope.asyncWithMdc {
            // Executa e retorna resultado
            "resultado"
        }
    }
}
```

## Métodos do MdcScopeManager

### Métodos sem retorno (launch)

```kotlin
// Despachador padrão
mdcScopeManager.launchWithDefaultDispatcher {
    // código executado em thread padrão
}

// Despachador IO
mdcScopeManager.launchWithIODispatcher {
    // ideal para operações de I/O
}

// Despachador customizado
val meuDispatcher = Dispatchers.Default
mdcScopeManager.launchWithDispatcher(meuDispatcher) {
    // código com despachador escolhido
}
```

### Métodos com retorno (async)

```kotlin
// Despachador padrão
suspend fun exemplo() {
    val resultado = mdcScopeManager.asyncWithDefaultDispatcher {
        "processado"
    }
}

// Despachador IO
suspend fun buscarDados() {
    val dados = mdcScopeManager.asyncWithIODispatcher {
        // chamada HTTP/DB
        listOf("item1", "item2")
    }
}
```

## Fluxo de MDC em Corrotinas

```
┌─────────────────────────────────────────┐
│  Requisição HTTP                        │
│  LoggingInterceptor inicializa MDC      │
│  - correlationId: "abc-123"             │
└────────────────┬────────────────────────┘
                 │
                 ▼
         ┌───────────────────┐
         │  Controller/Service│
         │  currentThread: T1 │
         └────────┬──────────┘
                  │ launchWithMdc()
                  │ MDC capturado
                  │
        ┌─────────────────────────┐
        │ MdcContextElement       │
        │ Armazena:              │
        │ - correlationId       │
        │ - depth              │
        │ - thread             │
        └──────────┬──────────────┘
                   │
        ┌──────────▼──────────────┐
        │ Execução em Thread T2   │
        │ MDC restaurado          │
        │ correlationId: "abc-123"│
        └────────────────────────┘
```

## Campos MDC Propagados

A configuração propaga automaticamente:

- `correlationId` - ID para correlacionar logs relacionados
- `correlationIdExternal` - ID externo (opcional)
- `depth` - Profundidade de aninhamento
- `thread` - Nome da thread
- `stack` - Informação de stack (se configurado)

## Exemplo de Log Resultante

```json
{
  "@timestamp": "2024-04-23T10:30:45.123Z",
  "message": "Executando com correlationId preservado",
  "logger": "com.example.MinhaProcessadora",
  "thread": "DefaultDispatcher-worker-1",
  "mdc": {
    "correlationId": "abc-123-def-456",
    "depth": "0",
    "thread": "http-nio-8080-exec-1"
  },
  "level": "INFO"
}
```

## Tratamento de Exceções

O `MdcScopeManager` inclui um `CoroutineExceptionHandler` que:

1. Registra exceções não tratadas em corrotinas
2. Preserva o contexto MDC ao registrar o erro
3. Evita travamento da aplicação

```kotlin
try {
    mdcScopeManager.launchWithDefaultDispatcher {
        throw RuntimeException("Erro na corrotina")
    }
} catch (e: Exception) {
    // Erro será registrado com correlationId preservado
}
```

## Boas Práticas

1. **Use o MdcScopeManager**: Oferece interface padronizada
2. **Injetar o Bean**: Avoid criar instâncias manuais
3. **Escolha o Dispatcher certo**:
   - `Dispatchers.Default`: CPU-bound operations
   - `Dispatchers.IO`: I/O operations (HTTP, BD)
   - `Dispatchers.Main`: UI operations (se aplicável)

4. **Monitore as Corrotinas**:
   ```kotlin
   val job = mdcScopeManager.launchWithIODispatcher {
       // fazer algo
   }
   job.join() // aguardar conclusão
   ```

5. **Teste com MDC**:
   ```kotlin
   @Test
   fun testComMdc() {
       LoggingConfig.initializeLoggingContext()
       try {
           // seu teste
       } finally {
           LoggingConfig.clearLoggingContext()
       }
   }
   ```

## Integração com Logback

A configuração já está integrada no `logback-spring.xml`:

```xml
<includeContext>true</includeContext>
```

Isso inclui automaticamente o MDC nos logs JSON.

## Troubleshooting

### MDC não está sendo propagado

1. Verificar se está usando `launchWithMdc()` ou `MdcScopeManager`
2. Certificar que `includeContext>true` no logback
3. Verificar logs para `[correlationId=abc-123]`

### Performance

- O MDC é thread-local e a propagação é eficiente
- O overhead é mínimo: cópia de Map quando launch/async
- Use `asyncWithDispatcher` apenas se realmente precisar do resultado

## Sub-contextos MDC com IDs Internos Únicos

Para cenários onde você precisa de **múltiplos fluxos dentro de uma coroutine**, cada um com seu próprio correlation ID interno único, utilize o `MdcSubContextManager`.

### Por que usar sub-contextos?

- **Rastreabilidade granular**: Cada operação específica tem seu próprio ID
- **Debugging facilitado**: Identifique exatamente qual operação falhou
- **Correlação de logs**: Mantenha o correlationId principal + IDs internos únicos
- **Aninhamento estruturado**: Organize operações em contextos hierárquicos

### Exemplo Básico - Lista de Tarefas

```kotlin
@Service
class ProcessadorService(
    @Autowired private val mdcScopeManager: MdcScopeManager,
    @Autowired private val mdcSubContextManager: MdcSubContextManager
) {
    
    fun processarPedidos() {
        mdcScopeManager.launchWithDefaultDispatcher {
            val pedidos = listOf("pedido-1", "pedido-2", "pedido-3")
            
            pedidos.forEach { pedido ->
                mdcSubContextManager.executeWithNewInternalId {
                    logger.info("Processando pedido: $pedido")
                    processarPedido(pedido)
                }
            }
        }
    }
}
```

**Resultado no log:**
```json
{
  "correlationId": "abc-123-def",
  "correlationIdInternal": "uuid-1",
  "message": "Processando pedido: pedido-1"
}
{
  "correlationId": "abc-123-def", 
  "correlationIdInternal": "uuid-2",
  "message": "Processando pedido: pedido-2"
}
```

### DSL de Sub-contextos Estruturados

Para operações mais complexas com aninhamento:

```kotlin
mdcScopeManager.launchWithDefaultDispatcher {
    withMdcContextScope(mdcSubContextManager) {
        subContext("validacao") {
            logger.info("Iniciando validação")
            
            subContext("dados-entrada") {
                validarDadosEntrada()
            }
            
            subContext("regras-negocio") {
                validarRegrasNegocio()
            }
        }
        
        subContext("processamento") {
            executarProcessamento()
        }
    }
}
```

### Execução Paralela com IDs Únicos

```kotlin
fun processarEmParalelo() {
    val tarefas = listOf("tarefa-A", "tarefa-B", "tarefa-C")
    
    CoroutineScope(Dispatchers.Default).launchParallelWithInternalIds(
        mdcSubContextManager, 
        tarefas
    ) { tarefa ->
        logger.info("Executando $tarefa")
        executarTarefa(tarefa)
    }
}
```

### Campos MDC Disponíveis

- `correlationId` - ID principal da requisição (preservado)
- `correlationIdExternal` - ID externo (opcional)
- `correlationIdInternal` - ID único para sub-fluxos
- `depth` - Profundidade de aninhamento
- `thread` - Nome da thread atual

### Métodos do MdcSubContextManager

#### Execução Básica
```kotlin
// Novo ID interno automático
mdcSubContextManager.executeWithNewInternalId {
    // código aqui
}

// ID interno específico
mdcSubContextManager.executeWithInternalId("meu-id-personalizado") {
    // código aqui
}
```

#### Com Profundidade
```kotlin
// Incrementa depth automaticamente
mdcSubContextManager.executeWithIncreasedDepth {
    // depth será incrementado
}
```

#### Utilitários
```kotlin
// Verificar se existe ID interno
val temIdInterno = mdcSubContextManager.hasInternalId()

// Obter ID interno atual
val idInterno = mdcSubContextManager.getCurrentInternalId()
```

### Extensões Kotlin

```kotlin
// Extensões para CoroutineScope
CoroutineScope.launchWithNewInternalId(mdcSubContextManager) {
    // código com novo ID interno
}

CoroutineScope.launchWithInternalId(mdcSubContextManager, "id-especifico") {
    // código com ID específico
}
```

### Exemplo Completo de Uso

```kotlin
@Component
class ExemploCompletoService(
    @Autowired private val mdcScopeManager: MdcScopeManager,
    @Autowired private val mdcSubContextManager: MdcSubContextManager
) {
    
    fun executarWorkflowCompleto() {
        mdcScopeManager.launchWithDefaultDispatcher {
            logger.info("Iniciando workflow completo")
            
            // Fase 1: Preparação
            withMdcContextScope(mdcSubContextManager) {
                subContext("preparacao") {
                    prepararAmbiente()
                    carregarConfiguracoes()
                }
            }
            
            // Fase 2: Processamento paralelo
            val dados = carregarDados()
            val jobs = CoroutineScope(Dispatchers.Default).launchParallelWithInternalIds(
                mdcSubContextManager, 
                dados
            ) { item ->
                processarItem(item)
            }
            
            // Aguardar conclusão
            jobs.forEach { it.join() }
            
            // Fase 3: Finalização
            mdcSubContextManager.executeWithNewInternalId {
                finalizarWorkflow()
            }
        }
    }
}
```

### Benefícios dos Sub-contextos

✅ **Isolamento**: Cada sub-fluxo é independente  
✅ **Rastreabilidade**: IDs únicos facilitam debugging  
✅ **Hierarquia**: Estrutura clara de operações  
✅ **Performance**: Overhead mínimo  
✅ **Flexibilidade**: IDs automáticos ou customizados  

### Boas Práticas

1. **Use IDs descritivos** quando precisar controlar especificamente
2. **Prefira IDs automáticos** para casos gerais
3. **Aninhe com DSL** para operações estruturadas
4. **Monitore paralelismo** com `Job.join()`
5. **Log com contexto** para facilitar troubleshooting
