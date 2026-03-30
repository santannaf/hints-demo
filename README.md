# Hints Demo

Projeto de demonstração para evidenciar quando e necessário utilizar o **native-image agent** (`runCustomJar`) para coletar hints de reflection antes de compilar uma native image com GraalVM, e quando o `nativeCompile` funciona diretamente.

## Stack

- Java 25 (GraalVM)
- Spring Boot 4.0.5
- PostgreSQL 17
- Redis 7
- Apache Kafka (Confluent 7.4) + Avro (GenericRecord)
- OpenTelemetry (Collector, Prometheus, Grafana, Tempo, Loki, Jaeger, Zipkin)
- Testcontainers (testes de integração)

## Como rodar

### 1. Subir a infraestrutura

```bash
./start.sh
```

Sobe tres docker-compose separados:

| Arquivo                             | Servicos                                                    |
|-------------------------------------|-------------------------------------------------------------|
| `docker-compose.database.yaml`      | PostgreSQL, Redis, Oracle                                   |
| `docker-compose.kafka.yaml`         | Zookeeper, Kafka, Schema Registry, Control Center           |
| `docker-compose.opentelemetry.yaml` | Collector, Tempo, Loki, Prometheus, Grafana, Jaeger, Zipkin |

### 2. Rodar com JVM

```bash
./gradlew bootRun
```

### 3. Rodar os testes

```bash
./gradlew test
```

### 4. Compilar native image

```bash
./gradlew nativeCompile
```

O binario sera gerado em `build/native/nativeCompile/hints`.

## GraalVM Native Image e Hints

Este e o objetivo principal do projeto: demonstrar quando o `nativeCompile` funciona sozinho e quando e necessário coletar hints primeiro.

### Funciona sem hints

Tecnologias que o Spring Boot ja trata via AOT processing e hints built-in:

- Spring Web (RestController, RestClient)
- Spring Data JDBC (NamedParameterJdbcTemplate)
- Spring Data Redis (@Cacheable, RedisCacheManager)
- Driver PostgreSQL
- Spring Actuator / OpenTelemetry

### Precisa de hints (runCustomJar)

Libraries que usam reflection sem fornecer metadata para GraalVM:

- **Kafka com libs customizadas** (`com.tanna.spring:kafka`) — classes record como `KafkaArchCommonProperties` usam reflection para acessar record components
- **Avro GenericRecord** — serializacao/deserializacao depende de reflection
- **Qualquer lib terceira** que use reflection, proxies ou carregamento dinâmico de classes sem fornecer reachability metadata

### Erro tipico sem hints

```
Caused by: com.oracle.svm.core.jdk.UnsupportedFeatureError: Record components not available
for record class com.tanna.configuration.properties.KafkaArchCommonProperties.
All record component accessor methods of this record class must be included in the
reflection configuration at image build time, then this method can be called.
```

### Workflow para coletar hints

```bash
# 1. Gerar o fat JAR
./gradlew bootJar

# 2. Rodar com o agent (exercitar os endpoints enquanto a app esta rodando)
./gradlew runCustomJar

# 3. Fazer requests para exercitar os code paths
curl http://localhost:8080/posts

# 4. Parar a app (Ctrl+C) — os hints sao salvos em:
#    src/main/resources/META-INF/native-image/

# 5. Agora o nativeCompile vai incluir os hints coletados
./gradlew nativeCompile
```

## Arquitetura

```
src/main/java/santannaf/hints/demo/
├── config/                          # Configuracoes (RestClient, Redis)
├── entity/                          # Records (Post)
├── provider/                        # Interfaces dos providers
├── usecase/                         # Casos de uso (orquestracao)
├── entrypoint/
│   ├── sync/controller/             # REST controllers
│   └── async/consumer/              # Kafka consumers
└── dataprovider/
    ├── http/                        # Integracao HTTP (jsonplaceholder)
    ├── repository/postgres/         # Persistencia PostgreSQL
    └── producer/                    # Kafka producer (Avro GenericRecord)
```

### Fluxo do GET /posts

1. `PostsController` recebe a requisição
2. `GetAllPostsUseCase` orquestra:
   - Busca posts via HTTP (com cache Redis de 30s via `@Cacheable`)
   - Insere em batch no PostgreSQL (upsert com `ON CONFLICT`)
   - Envia cada post como evento Kafka (Avro GenericRecord)
3. `PostEventConsumer` consome os eventos do tópico

## Testes

- **Unitários**: Mockito para UseCase, Providers e Controller
- **Integração**: Testcontainers (PostgreSQL + Redis) com `@SpringBootTest`
- Beans de Kafka sao mockados nos testes de integração via `@MockitoBean`

```bash
./gradlew test                                           # Todos os testes
./gradlew test --tests "*.integration.*"                 # Somente integração
./gradlew test --tests "*.usecase.GetAllPostsUseCaseTest" # Teste especifico
```
