# Documentação

# Sistema de Gestão de Equipamentos

## 1. Visão geral

O projeto é uma API REST desenvolvida com Java e Spring Boot para o controle de equipamentos, colaboradores e empréstimos de patrimônio. O sistema permite manter o cadastro de equipamentos, cadastrar colaboradores, registrar empréstimos, registrar devoluções, consultar registros e aplicar regras de acesso por perfil.

O sistema foi organizado em pacotes de controller, service, repository, model, DTO, mapping, security, client e exceptions.

### Objetivos implementados

- centralizar o cadastro de equipamentos;
- centralizar o cadastro de colaboradores;
- consultar a disponibilidade dos equipamentos;
- registrar empréstimos;
- registrar devoluções;
- impedir empréstimo enquanto houver um empréstimo ativo;
- calcular o prazo de devolução;
- consultar empréstimos atrasados;
- autenticar usuários com JWT;
- restringir operações por perfil;
- integrar o cadastro de colaboradores à API ViaCEP;
- preservar os dados em banco H2 em arquivo.

---

## 2. Tecnologias utilizadas

| Tecnologia | Utilização |
| --- | --- |
| Java 17 | Linguagem principal |
| Spring Boot | Configuração da aplicação e execução da API |
| Spring MVC | Exposição dos endpoints REST |
| Spring Data JPA | Persistência de entidades |
| Hibernate | Mapeamento objeto-relacional |
| H2 Database | Banco de dados em arquivo |
| Spring Security | Autenticação e autorização |
| JWT — `java-jwt` | Emissão e validação de tokens |
| BCrypt | Criptografia de senhas |
| Bean Validation | Validação dos dados de entrada |
| Spring Cloud OpenFeign | Integração HTTP com ViaCEP |
| MapStruct | Conversão entre DTO e entidade |
| Lombok | Redução de código repetitivo |
| Springdoc OpenAPI | Dependência para documentação da API |
| Maven | Gerenciamento de dependências e build |

---

## 3. Organização da aplicação

### Pacote principal

`br.com.projeto.gestao_equipamentos`

```
src/
├── main/
│   ├── java/
│   │   └── br/com/projeto/gestao_equipamentos/
│   │       ├── client/
│   │       ├── controller/
│   │       ├── dtos/
│   │       ├── exceptions/
│   │       ├── mapping/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── security/
│   │       └── service/
│   └── resources/
│       └── application.yaml
└── test/
    └── java/
```

### Responsabilidade das principais camadas

| Camada | Responsabilidade |
| --- | --- |
| `controller` | Receber requisições HTTP e retornar respostas |
| `service` | Implementar casos de uso e regras de negócio |
| `repository` | Executar operações de persistência |
| `model` | Representar entidades JPA |
| `dtos` | Representar dados de entrada e saída da API |
| `mapping` | Converter entidades em DTO e DTO em entidades |
| `security` | Autenticar usuários, emitir JWT e controlar acesso |
| `client` | Consumir serviços externos |
| `exceptions` | Representar erros de domínio e padronizar respostas |

---

## 4. Arquitetura

A aplicação usa uma arquitetura em camadas:

```
Cliente HTTP
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
Banco H2
```

### Fluxo de cadastro de colaborador

```
Requisição
    ↓
Validação do DTO
    ↓
ColaboradorService
    ↓
CepService
    ↓
ViaCEP
    ↓
Criação do usuário
    ↓
Criação do endereço
    ↓
Persistência do colaborador
    ↓
Resposta HTTP
```

### Fluxo de criação de empréstimo

```
Requisição
    ↓
Busca do equipamento
    ↓
Busca do colaborador
    ↓
Verificação de empréstimo ativo
    ↓
Marcação do equipamento como emprestado
    ↓
Cálculo das datas
    ↓
Persistência do empréstimo
    ↓
Resposta HTTP
```

### Fluxo de devolução

```
Requisição
    ↓
Busca do empréstimo
    ↓
Verificação da devolução anterior
    ↓
Registro da data de devolução
    ↓
Marcação do equipamento como disponível
    ↓
Persistência
    ↓
Resposta HTTP
```

---

---

## 5. Regras de empréstimo

## 5.1 Criação de empréstimo

O payload de criação contém:

```json
{
  "equipamentoId": 3,
  "colaboradorId": 1
}
```

Ao criar um empréstimo, o sistema:

1. busca o equipamento pelo identificador;
2. busca o colaborador pelo identificador;
3. verifica se já existe empréstimo ativo para o equipamento;
4. marca o equipamento como emprestado;
5. cria o empréstimo;
6. salva o registro.

Caso o equipamento ou colaborador não exista, uma exceção específica é lançada.

Caso o equipamento já esteja emprestado, o sistema lança `EquipamentoIndisponivelException`.

---

## 5.2 Cálculo das datas

A entidade `Emprestimo` utiliza `@PrePersist` para calcular suas datas.

Comportamento:

- Se `dataEmprestimo` estiver vazia, ela recebe `LocalDate.now()`;
- `dataPrevistaDevolucao` recebe a data do empréstimo acrescida de 14 dias;
- `dataDevolucao` permanece vazia até o registro da devolução.

Exemplo:

- empréstimo em `17/11/2026`;
- data prevista em `01/12/2026`.

---

## 5.3 Devolução

O método `registrarDevolucao()` realiza a devolução de um empréstimo.

O método:

- verifica se o empréstimo já possui uma data de devolução;
- impede uma devolução repetida;
- registra `LocalDate.now()` como data de devolução;
- permite que o equipamento seja marcado como disponível.

No serviço de empréstimo:

1. o empréstimo é buscado pelo identificador;
2. a devolução é registrada;
3. o equipamento associado é marcado como disponível;
4. o empréstimo atualizado é salvo.

---

## 5.4 Atraso

A entidade `Emprestimo` implementa os métodos:

- `isAtrasado()`;
- `getDiasAtraso()`.

O método `isAtrasado()` considera:

- empréstimos não devolvidos cuja data prevista já passou;
- empréstimos devolvidos depois da data prevista.

O método `getDiasAtraso()` calcula a quantidade de dias entre a data prevista e:

- a data de devolução, quando houver;
- a data atual, quando o empréstimo continuar aberto.

---

## 5.5 Consulta de atrasados

A `EmprestimoRepository` possui uma consulta que recebe:

- `atrasados`;
- `hoje`.

A consulta pode retornar:

- todos os empréstimos;
- empréstimos considerados atrasados;
- empréstimos não considerados atrasados.

O filtro usa as datas de previsão e devolução.

---

## 6. Equipamentos

## 61 Cadastro

Endpoint:

```
POST /equipamento/criar
```

Campos:

- `patrimonio`;
- `nome`;
- `tipo`;
- `disponivel`.

Validações:

- patrimônio não pode estar vazio;
- nome não pode estar vazio;
- tipo não pode estar vazio.

Comportamento:

- verifica se o patrimônio já existe;
- lança `PatrimonioJaCadastradoException` em caso de duplicidade;
- salva o equipamento;
- retorna o equipamento cadastrado;
- responde com `201 Created`.

---

## 6.2 Atualização

Endpoint:

```
PUT /equipamento/atualizar/{id}
```

Campos atualizáveis pelo serviço:

- nome;
- tipo.

Comportamento:

- o request passa por validação;
- o repository realiza a atualização;
- caso nenhuma linha seja alterada, lança `EquipamentoNaoEncontradoException`;
- retorna o equipamento atualizado.

---

## 6.3 Busca por identificador

Endpoint:

```
GET /equipamento/buscar/{id}
```

Caso o identificador não exista, o sistema lança `EquipamentoNaoEncontradoException`.

---

## 6.4 Listagem

Endpoint:

```
GET /equipamentos
```

Filtros opcionais:

- `tipo`;
- `disponivel`.

Exemplo:

```
GET /equipamentos?tipo=NOTEBOOK&disponivel=true
```

A consulta é realizada pelo `EquipamentoRepository` e aceita filtros nulos.

Também existe a rota:

```
GET /buscar
```

que lista todos os equipamentos sem aplicar filtros.

---

## 6.5 Exclusão

Endpoint:

```
DELETE /equpamento/delete/{id}
```

A palavra `equpamento` está escrita dessa forma na implementação atual.

Comportamento:

- busca o equipamento pelo identificador;
- lança `EquipamentoNaoEncontradoException` caso não exista;
- remove o equipamento;
- não executa uma consulta de empréstimos históricos antes da exclusão.

---

## 7. Colaboradores

## 7.1 Cadastro

Endpoint:

```
POST /colaborador/criar
```

Campos:

- `nome`;
- `email`;
- `cpf`;
- `cep`;
- `numero`;
- `senha`;
- `perfil`.

Validações:

- nome não pode ser nulo ou vazio;
- nome deve ter entre 3 e 50 caracteres;
- email não pode estar vazio;
- email deve estar em formato válido;
- CPF não pode estar vazio;
- CPF deve estar em formato válido.

### Processo de cadastro

1. verifica se o email já está cadastrado;
2. cria o usuário associado;
3. criptografa a senha com BCrypt;
4. consulta o ViaCEP;
5. cria o endereço com o resultado da consulta;
6. cria o colaborador;
7. salva o colaborador;
8. retorna o colaborador cadastrado.

Caso o email já exista, lança `EmailJaCadastradoException`.

---

## 7.2 Busca por identificador

Endpoint:

```
GET /colaborador/buscar/{id}
```

Caso o colaborador não exista, lança `ColaboradorNaoEncontradoException`.

---

## 7.3 Listagem

Endpoint:

```
GET /colaboradores/buscar
```

Retorna:

- todos os colaboradores cadastrados;
- uma lista vazia, caso não existam colaboradores.

---

## 8. Empréstimos

## 8.1 Criação

Endpoint:

```
POST /emprestimo
```

Request:

```json
{
  "equipamentoId": 3,
  "colaboradorId": 1
}
```

Comportamento:

- busca o equipamento;
- busca o colaborador;
- verifica empréstimo ativo;
- marca o equipamento como emprestado;
- cria o empréstimo;
- calcula as datas;
- persiste o registro;
- retorna `201 Created`.

---

## 8.2 Devolução

Endpoint:

```
PUT /emprestimo/{id}/devolucao
```

Comportamento:

- busca o empréstimo;
- verifica se já possui devolução;
- registra a data atual;
- libera o equipamento;
- salva as alterações;
- retorna o empréstimo atualizado.

---

## 8.3 Listagem

Endpoint:

```
GET /emprestimos
```

Retorna todos os empréstimos cadastrados.

---

## 8.4 Filtro de atrasados

Endpoint:

```
GET /emprestimos/filtrar
```

Parâmetro opcional:

```
GET /emprestimos/filtrar?atrasados=true
```

A consulta suporta:

- `atrasados=true`;
- `atrasados=false`;
- `atrasados` nulo.

Com `atrasados=true`, a consulta considera:

- empréstimos em aberto cuja data prevista seja anterior à data atual;
- empréstimos devolvidos após a data prevista.

---

## 9. Login e autenticação

## 9.1 Login

Endpoint:

```
POST /auth/login
```

Body:

```json
{
  "email": "usuario@empresa.com",
  "senha": "senha"
}
```

Comportamento:

1. cria um token de autenticação;
2. usa o `AuthenticationManager`;
3. busca o usuário autenticado;
4. gera um JWT;
5. retorna o token.

Resposta:

```json
{
  "token": "JWT"
}
```

---

## 9.2 Token JWT

O `TokenService` gera o token com:

- emissor: `API Equipamentos`;
- subject: email do usuário;
- claim: perfil;
- validade: duas horas;
- assinatura: HMAC256.

O token é enviado no cabeçalho:

```
Authorization: Bearer <token>
```

---

## 9.3 Segurança

A aplicação usa Spring Security em modo stateless.

A classe `SecurityFilter`:

- recupera o cabeçalho `Authorization`;
- remove o prefixo `Bearer`;
- valida o token;
- busca o usuário pelo email;
- cria a autenticação no contexto do Spring.

Rotas públicas:

- `POST /auth/login`;
- `/h2-console/**`.

A configuração impede a criação de sessão HTTP pelo Spring Security.

---

## 10. Perfis e autorização

Perfis implementados:

| Perfil | Permissões observadas |
| --- | --- |
| `ADMIN` | Consultar e cadastrar, atualizar, remover e realizar empréstimos/devoluções |
| `COLABORADOR` | Consultar equipamentos e empréstimos |

### Regras configuradas

Rotas públicas:

- `/auth/login`;
- `/h2-console/**`.

Endpoints de consulta de equipamentos e empréstimos:

- `ADMIN`;
- `COLABORADOR`.

Endpoints de colaboradores:

- a consulta foi restrita ao `ADMIN`.

Operações administrativas:

- criação;
- atualização;
- exclusão;
- empréstimo;
- devolução.

Qualquer outra requisição não configurada exige autenticação.

---

## 12. Integração ViaCEP

A integração é feita por Spring Cloud OpenFeign.

### Cliente

`CepClient` usa:

```
<https://viacep.com.br/ws>
```

URL chamada:

```
GET <https://viacep.com.br/ws/{cep}/json/>
```

### Resposta esperada

```json
{
  "cep": "01310-100",
  "logradouro": "Avenida Paulista",
  "bairro": "Bela Vista",
  "localidade": "São Paulo",
  "uf": "SP"
}
```

### Tratamento atual

O `CepService` trata:

- `FeignException.BadRequest`;
- outras exceções do Feign;
- falhas de integração.

No cadastro do colaborador:

- o CEP é enviado ao serviço;
- a resposta é convertida em `EnderecoRequest`;
- o endereço é associado ao colaborador.

---

## 13. Tratamento de erros

A aplicação possui `GlobalExceptionHandler` implementado com `@RestControllerAdvice`.

Exceções e respostas implementadas:

| Exceção | Status |
| --- | --- |
| `ColaboradorNaoEncontradoException` | `404 Not Found` |
| `EmprestimoNaoEncontradoException` | `404 Not Found` |
| `EquipamentoNaoEncontradoException` | `404 Not Found` |
| `EmailJaCadastradoException` | `409 Conflict` |
| `PatrimonioJaCadastradoException` | `409 Conflict` |
| `EquipamentoIndisponivelException` | `409 Conflict` |
| `CepInvalidoException` | `400 Bad Request` |
| `IntegracaoCepException` | `502 Bad Gateway` |

A estrutura da resposta contém:

- timestamp;
- status;
- error;
- message;
- path.

---

## 14. Banco de dados

A aplicação utiliza H2 em arquivo.

Arquivos de banco:

- `data/project.mv.db`;
- `data/project.trace.db`.

### Hibernate

Configuração:

- `ddl-auto: update`;
- `show-sql: true`.

### Console H2

A console está habilitada em:

```
/h2-console
```

---

## 15. Configuração da aplicação

Arquivo:

`src/main/resources/application.yaml`

Configurações presentes:

- nome da aplicação;
- datasource;
- Hibernate;
- console H2;
- porta `8081`;
- segredo JWT;
- dados do administrador;
- logging do Spring Security.

Variáveis de ambiente:

- `JWT_SECRET`;
- `ADMIN_EMAIL`;
- `ADMIN_PASSWORD`.

---

## 16. Inicialização do administrador

A classe `AdminInitializer` implementa `CommandLineRunner`.

Durante a inicialização:

1. verifica se existe usuário com o email configurado;
2. cria o usuário caso ele não exista;
3. criptografa a senha;
4. atribui o perfil `ADMIN`;
5. salva o usuário no banco.

---

## 17. Execução da aplicação

### Requisitos

- JDK 17;
- acesso às dependências Maven;
- variável `JWT_SECRET`;
- variável `ADMIN_EMAIL`;
- variável `ADMIN_PASSWORD`.

### Comando no Linux/macOS

```bash
export JWT_SECRET="segredo-jwt"
export ADMIN_EMAIL="admin@empresa.com"
export ADMIN_PASSWORD="senha-administrador"
./mvnw spring-boot:run
```

### Comando no Windows

```bash
mvnw.cmd spring-boot:run
```

A aplicação fica configurada na porta:

```
<http://localhost:8081>
```

---

## 18. Testes implementados

Os testes presentes estão organizados em testes unitários de serviços.

### Equipamentos

O teste de equipamento cobre:

- criação com patrimônio novo;
- criação com patrimônio duplicado;
- atualização;
- atualização com identificador inexistente;
- busca por identificador;
- busca com filtros;
- lista vazia;
- exclusão;
- exclusão com identificador inexistente.

### Empréstimos

O teste de empréstimo cobre:

- criação com sucesso;
- equipamento inexistente;
- colaborador inexistente;
- equipamento indisponível;
- listagem;
- lista vazia;
- devolução;
- empréstimo inexistente durante devolução.

### Colaboradores

O teste de colaborador cobre:

- criação com dados válidos;
- email duplicado;
- CEP inválido;
- falha de integração;
- busca por identificador;
- identificador inexistente;
- listagem;
- lista vazia.

### ViaCEP

O teste de CEP cobre:

- busca com sucesso;
- resposta HTTP `400`;
- falha genérica do Feign.

---

## 19. Tabela de endpoints

| Método | Rota | Perfil | Resposta |
| --- | --- | --- | --- |
| `POST` | `/auth/login` | Público | `200` |
| `POST` | `/equipamento/criar` | `ADMIN` | `201` |
| `PUT` | `/equipamento/atualizar/{id}` | `ADMIN` | `200` |
| `GET` | `/equipamento/buscar/{id}` | `ADMIN` ou `COLABORADOR` | `200` |
| `GET` | `/equipamentos` | `ADMIN` ou `COLABORADOR` | `200` |
| `GET` | `/buscar` | `ADMIN` ou `COLABORADOR` | `200` |
| `DELETE` | `/equpamento/delete/{id}` | Configuração administrativa | `200` |
| `POST` | `/colaborador/criar` | `ADMIN` | `201` |
| `GET` | `/colaborador/buscar/{id}` | `ADMIN` | `200` |
| `GET` | `/colaboradores/buscar` | `ADMIN` | `200` |
| `POST` | `/emprestimo` | `ADMIN` | `201` |
| `PUT` | `/emprestimo/{id}/devolucao` | `ADMIN` | `200` |
| `GET` | `/emprestimos` | `ADMIN` ou `COLABORADOR` | `200` |
| `GET` | `/emprestimos/filtrar` | `ADMIN` ou `COLABORADOR` | `200` |
| `GET` | `/cep/{cep}` | Autenticado pelas regras gerais | `200` |
| `GET` | `/h2-console/**` | Público | `200` |

---

## 20. Resultado implementado

A branch implementa:

- backend em Java/Spring Boot;
- arquitetura em camadas;
- persistência em H2;
- cadastro de equipamentos;
- cadastro de colaboradores;
- integração ViaCEP;
- cadastro de usuários;
- autenticação JWT;
- perfis `ADMIN` e `COLABORADOR`;
- empréstimos;
- devoluções;
- cálculo de prazo de 14 dias;
- consulta de atrasados;
- validação de patrimônio e email;
- tratamento de principais exceções de domínio;
- testes unitários de serviços;
- documentação técnica via Springdoc como dependência.

---

## 21. Conclusão

O projeto apresenta a implementação da API de gestão de equipamentos e empréstimos com persistência, segurança, integração externa e regras de domínio relacionadas ao ciclo de empréstimo e devolução.

O projeto contém uma estrutura modular baseada em controllers, services e repositories, com DTOs e mappers separando dados da API das entidades persistidas.