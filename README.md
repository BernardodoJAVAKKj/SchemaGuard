SchemaGuard 🛡️

API SaaS multi-tenant em Java com Spring Boot e PostgreSQL. Cada empresa (tenant) tem seus dados completamente isolados em schemas separados no banco de dados.

Tecnologias


Java 21
Spring Boot 3.2.5 — Web, Security, Data JPA
PostgreSQL — isolamento por schema
Hibernate — multi-tenancy via MultiTenantConnectionProvider
Flyway — migrations por tenant
JWT (jjwt) — autenticação stateless com tenant embutido no token
Lombok


Como funciona

Cada empresa se cadastra e recebe um schema próprio no PostgreSQL:

public/
├── tenants       ← empresas cadastradas
└── users         ← usuários (vinculados ao tenant)

empresa_a/
└── products      ← dados isolados da Empresa A

empresa_b/
└── products      ← dados isolados da Empresa B

O fluxo de uma requisição autenticada:

Request → JwtAuthFilter → extrai tenantId do token
        → TenantContext (ThreadLocal)
        → Hibernate TenantIdentifierResolver
        → SET search_path TO empresa_a
        → query vai pro schema certo

Pré-requisitos


Java 21+
Maven 3.9+
PostgreSQL rodando localmente


Configuração


Crie o banco de dados:


sqlCREATE DATABASE schemaguard;


Configure o src/main/resources/application.properties:


propertiesspring.datasource.url=jdbc:postgresql://localhost:5432/schemaguard
spring.datasource.username=postgres
spring.datasource.password=sua_senha

spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=false

jwt.secret=Sch3m@Gu4rd#S3cr3t!K3y$2024%Ultra$Secure&Token@Key!


Rode a aplicação:


bashmvn spring-boot:run

Endpoints

Tenants

MétodoEndpointDescriçãoAuthPOST/tenantsCria um novo tenant e seu schema❌

json// Body
{ "name": "empresa_a" }

Auth

MétodoEndpointDescriçãoAuthPOST/auth/registerRegistra usuário no tenant❌POST/auth/loginAutentica e retorna JWT❌

json// POST /auth/register
{
  "username": "joao",
  "password": "123456",
  "tenantId": "uuid-do-tenant"
}

// POST /auth/login
{
  "username": "joao",
  "password": "123456"
}

Products

MétodoEndpointDescriçãoAuthGET/productsLista produtos do tenant✅POST/productsCria produto no tenant✅

json// POST /products
{
  "name": "Produto A",
  "price": 99.90
}


Todas as requisições autenticadas precisam do header:
Authorization: Bearer <token>



Estrutura do projeto

src/main/java/com/SchemaGuard/
├── auth/
│   ├── controller/     AuthController
│   ├── dto/            LoginRequest, RegisterRequest
│   ├── model/          User
│   ├── repository/     UserRepository
│   ├── security/       JwtService, JwtAuthFilter
│   └── service/        AuthService
├── tenant/
│   ├── controller/     TenantController
│   ├── dto/            TenantRequest
│   ├── model/          Tenant
│   ├── repository/     TenantRepository
│   └── service/        TenantProvisioningService
├── product/
│   ├── controller/     ProductController
│   ├── model/          Product
│   ├── repository/     ProductRepository
│   └── service/        ProductService
├── config/
│   ├── HibernateConfig
│   └── SecurityConfig
└── infra/
    ├── TenantContext
    ├── TenantIdentifierResolver
    └── SchemaMultiTenantConnectionProvider

Conceitos aplicados


Multi-tenancy por schema — isolamento real de dados no PostgreSQL
ThreadLocal — armazena o tenant atual por thread de forma segura
JWT com claims customizados — tenant embutido no token
Monolito modular — separação por domínio sem complexidade de microsserviços
Flyway programático — migrations executadas dinamicamente por tenant
Spring Security stateless — sem sessão, autenticação via filtro JWT
