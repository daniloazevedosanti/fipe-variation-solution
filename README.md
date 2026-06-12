# Solução — Teste Técnico FIPE

Solução full stack para o teste técnico:

- **Backend:** Java 17 + Spring Boot 3, microsserviço REST.
- **Frontend:** Angular 18 standalone.
- **Integração externa:** API FIPE v2 `https://fipe.parallelum.com.br/api/v2`.
- **Objetivo:** selecionar tipo/marca/modelo e retornar a evolução de preço entre anos de fabricação.

## Decisão técnica principal

O teste pede o valor e o percentual de alteração do valor do veículo ao longo dos anos em que foi fabricado.
A API FIPE v2 possui o fluxo adequado:

1. Buscar marcas: `/{vehicleType}/brands`.
2. Buscar modelos por marca: `/{vehicleType}/brands/{brandId}/models`.
3. Buscar anos do modelo: `/{vehicleType}/brands/{brandId}/models/{modelId}/years`.
4. Para cada ano, buscar o detalhe/preço: `/{vehicleType}/brands/{brandId}/models/{modelId}/years/{yearId}`.

A solução ignora o código `32000-*` quando aparecer, porque representa **Zero KM** e não um ano de fabricação efetivo.

## Estrutura

```text
fipe-variation-solution/
├── backend-fipe-variation/
│   ├── src/main/java/br/com/danilo/fipevariation
│   │   ├── client       # Cliente da API externa FIPE
│   │   ├── config       # CORS, RestClient e properties
│   │   ├── controller   # Endpoints REST da aplicação
│   │   ├── dto          # Records de entrada/saída
│   │   ├── exception    # Tratamento centralizado de erros
│   │   ├── service      # Regra de cálculo da variação
│   │   └── util         # Conversão monetária pt-BR
│   └── src/test/java    # Testes unitários
└── frontend-fipe-variation/
    └── src/app          # Tela Angular 18
```

## Como executar o backend

Pré-requisitos:

- JDK 17+
- Maven 3.9+

```bash
cd backend-fipe-variation
mvn spring-boot:run
```

O serviço sobe em:

```text
http://localhost:8080
```

Swagger/OpenAPI local:

```text
http://localhost:8080/swagger-ui/index.html
```

Health check:

```text
http://localhost:8080/actuator/health
```

### Token FIPE opcional

A documentação da FIPE informa limite para requisições não autenticadas. Se tiver token:

```bash
export FIPE_SUBSCRIPTION_TOKEN="seu-token"
mvn spring-boot:run
```

## Endpoints do microsserviço

### Tipos de veículo

```http
GET /api/v1/fipe/vehicle-types
```

### Marcas

```http
GET /api/v1/fipe/{vehicleType}/brands?reference={reference}
```

Exemplo:

```bash
curl "http://localhost:8080/api/v1/fipe/cars/brands"
```

### Modelos

```http
GET /api/v1/fipe/{vehicleType}/brands/{brandId}/models?reference={reference}
```

Exemplo:

```bash
curl "http://localhost:8080/api/v1/fipe/cars/brands/59/models"
```

### Variação de preços por ano

```http
GET /api/v1/fipe/{vehicleType}/brands/{brandId}/models/{modelId}/variations?reference={reference}
```

Exemplo:

```bash
curl "http://localhost:8080/api/v1/fipe/cars/brands/59/models/5940/variations"
```

### Exemplo de resposta

```json
{
  "vehicleType": "cars",
  "brandId": 59,
  "modelId": 5940,
  "reference": null,
  "generatedAt": "2026-06-12T15:30:00-03:00",
  "items": [
    {
      "yearId": "2014-3",
      "modelYear": 2014,
      "brand": "VW - VolksWagen",
      "model": "AMAROK High.CD 2.0 16V TDI 4x4 Dies. Aut",
      "fuel": "Diesel",
      "fuelAcronym": "D",
      "codeFipe": "005340-6",
      "referenceMonth": "junho de 2026",
      "price": "R$ 100.000,00",
      "priceValue": 100000.00,
      "changeValue": 2500.00,
      "changeValueFormatted": "R$ 2.500,00",
      "changePercent": 2.56,
      "comparedWithYear": 2013
    }
  ]
}
```

## Regra de cálculo

Para cada ano, exceto o mais antigo:

```text
alteracaoValor = valorAnoAtual - valorAnoAnterior
alteracaoPercentual = (alteracaoValor / valorAnoAnterior) * 100
```

A lista é retornada do ano mais novo para o mais antigo, mas o cálculo é feito em ordem cronológica crescente para manter a comparação correta.

## Como executar o frontend

Pré-requisitos:

- Node.js compatível com Angular 18
- npm

```bash
cd frontend-fipe-variation
npm install
npm start
```

A aplicação abre em:

```text
http://localhost:4200
```

O `proxy.conf.json` direciona `/api` para `http://localhost:8080`, evitando problema de CORS durante o desenvolvimento.

## Boas práticas aplicadas

- Separação clara entre controller, service e client externo.
- DTOs imutáveis com `record`.
- Tratamento centralizado de erros com `ProblemDetail`.
- Cache com Caffeine para reduzir chamadas repetidas à API FIPE.
- CORS configurável por ambiente.
- Conversão monetária isolada em utilitário testável.
- Teste unitário da regra principal de variação.
- Tela Angular com componentes standalone, reactive forms e sinais (`signal`).

## Autor

**Danilo Azevedo**

Desenvolvedor / Engenheiro de Software

- GitHub: [@seu-usuario](https://github.com/daniloazevedosanti)
- LinkedIn: [Danilo Azevedo](https://www.linkedin.com/in/dansantosaz)
- E-mail: danilo.azevedosanti@gmail.com

