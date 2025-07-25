# Obesity Predict AI API

Este projeto é uma API Spring Boot que utiliza MariaDB como banco de dados, configurada para rodar em contêineres Docker. A aplicação expõe endpoints para predições, incluindo rotas GET e POST em `/predicao`. Este README explica como configurar e rodar o ambiente, além de como testar as rotas especificadas.

## Pré-requisitos

Antes de executar ou contribuir com este projeto, certifique-se de ter as seguintes ferramentas instaladas:

- **[Docker](https://docs.docker.com/get-docker/)** e **[Docker Compose](https://docs.docker.com/compose/install/)**: Para criar e gerenciar os containers da aplicação.
- **Java 17**: Versão necessária para compilar e executar a aplicação.
- **[IntelliJ IDEA Community](https://www.jetbrains.com/idea/download/)**: Recomendado para o desenvolvimento. A versão *Community* é gratuita.
- **[Maven](https://maven.apache.org/)**: Para o gerenciamento de dependências e automação de builds.
- **Ferramentas para testes de API**: Como [Postman](https://www.postman.com/) ou [Insomnia](https://insomnia.rest/).
- **[DBeaver](https://dbeaver.io/)**: Opcional, mas útil para visualizar e interagir com o banco de dados.


# Instalação do MariaDB no Docker e Configuração do Banco de Dados

## Pré-requisitos
- Docker instalado na máquina.

## Passos para Instalação

1. **Puxar e executar o container MariaDB**
   Execute o seguinte comando no terminal para iniciar o MariaDB no Docker na porta 3307, com usuário `root` e senha `123`:

   ```bash
   docker run --name mariadb -e MYSQL_ROOT_PASSWORD=123 -p 3307:3306 -d mariadb:latest
   ```

## Conexão com DBeaver

1. **Instalar o DBeaver**
    - Baixe e instale o DBeaver (ferramenta gráfica para gerenciamento de bancos de dados) em: [https://dbeaver.io/download/](https://dbeaver.io/download/).
    - Siga as instruções de instalação de acordo com o seu sistema operacional.

2. **Configurar conexão no DBeaver**
    - Abra o DBeaver e crie uma nova conexão.
    - Selecione **MariaDB** como tipo de banco de dados.
    - Preencha os seguintes parâmetros:
        - **Host**: `localhost`
        - **Porta**: `3307`
        - **Usuário**: `root`
        - **Senha**: `123`
    - Clique em **Test Connection** para testar e, em seguida, **Finish** para salvar.

3. **Criar o banco de dados**
    - A conexão aparecerá no painel à esquerda do DBeaver.
    - Clique com o botão direito sobre ela e vá em **Create > Database**.
    - Defina o nome do banco como `dev` e clique em **OK**.


# Clonando e abrindo o projeto no IntelliJ IDEA

Siga os passos abaixo para clonar o projeto e abrir no IntelliJ IDEA Community:

## Clonar o repositório

Abra o terminal e execute o seguinte comando:

  ```bash
  git clone https://github.com/repositorio.git
  ```

Acesse a pasta do projeto:

  ```bash
  cd repositorio
  ```

## Abrir o projeto no IntelliJ

- Abra o **IntelliJ IDEA Community**.
- Clique em **"Open"** (ou "Abrir") e selecione a pasta onde o projeto foi clonado.
- O IntelliJ pode perguntar se deseja abrir como um projeto Maven — aceite.

## Importar e carregar as dependências do Maven
- Ao abrir o projeto, o IntelliJ deve detectar automaticamente o arquivo `pom.xml`.
- Se as dependências não forem carregadas automaticamente:
    - Vá até o painel lateral direito onde está a aba **Maven**.
    - Clique no botão de **refresh** (ícone de duas setas em círculo) ou com o botão direito no nome do projeto e selecione **"Reimport"**.
    - Alternativamente, vá no menu: **File > Sync Project with Maven**.

## Executar o projeto 
- Aguarde o IntelliJ terminar a indexação e carregar todas as dependências.
- Abra a classe `ApiApplication` elá está em `src/main/java/com/obesityPredictAi/api`.
- Clique no botão verde de execução ao lado do método `main` ou na barra superior que terá a opção de run.

> 💡 Se o projeto não compilar ou houver erros de dependência, verifique se o Maven está instalado corretamente e se há conexão com a internet para baixar os pacotes.


A API expõe endpoints em `http://localhost:8080`. Abaixo estão as instruções para testar as rotas GET e POST em `/predicao`.

## Testando as rotas

### 1. Rota GET: `/predicao`
Esta rota retorna as predições disponíveis.

**Comando com `curl`**:
```bash
curl http://localhost:8080/predicao
```

**Resposta esperada**:
Uma lista de predições (dependendo da implementação). Exemplo:
```json
[
  {
    "userId": 1,
    "resultado": "Positivo",
    "dataDoResultado": "2025-07-24"
  }
]
```

Obs.: Se não houver nenhum registro no banco de dados, a aplicação retornará uma lista vazia.

**Teste com Postman/Insomnia**:
- Método: `GET`
- URL: `http://localhost:8080/predicao`
- Clique em "Send" e verifique a resposta.

### 2. Rota POST: `/predicao`
Esta rota cria uma nova predição enviando um JSON no corpo da requisição.

**JSON de exemplo**:
```json
{
  "userId": 1,
  "resultado": "Positivo",
  "dataDoResultado": "2025-07-24"
}
```

**Comando com `curl`**:
```bash
curl -X POST http://localhost:8080/predicao \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "resultado": "Positivo", "dataDoResultado": "2025-07-24"}'
```

**Resposta esperada**:
Uma confirmação da criação da predição (dependendo da implementação). Exemplo:
```json
{
  "id": 1,
  "userId": 1,
  "resultado": "Positivo",
  "dataDoResultado": "2025-07-24",
  "status": "Created"
}
```

**Teste com Postman/Insomnia**:
- Método: `POST`
- URL: `http://localhost:8080/predicao`
- Headers: `Content-Type: application/json`
- Body: Cole o JSON acima no formato `raw` (JSON).
- Clique em "Send" e verifique a resposta.

## Notas
- **Conflitos de porta**: O MariaDB do projeto usa a porta 3307 no host para evitar conflitos com outro MariaDB na porta 3306. Se a porta 3307 estiver em uso, edite o `docker-compose.yml` para usar outra porta (ex.: `3308:3306`).
- **Persistência de dados**: Os dados do banco são salvos no volume `mariadb-data`. Para recriar o banco do zero, use `docker-compose down -v`.
- **Flyway**: Se você usa Flyway para migrações, certifique-se de que os scripts de migração em `src/main/resources/db/migration` estão corretos.
- **Problemas?** Verifique os logs com `docker-compose logs` e confirme que o `init.sql` e as configurações do banco estão corretas.