# BANK API

## Install

`mvn clean -D skipTests && mvn install -D skipTests`

## Database

Para criar o banco de dados, execute o comando abaixo - lembrando de ajustar o caminho do volume, ou removê-lo:

`docker run --name mariadb -p 3306:3306 -v C:\Users\layon\Documents\projects\bankapi\mysql:/var/lib/mysql -e MYSQL_DATABASE=bankapi -e MYSQL_USER=bankapi -e MYSQL_PASSWORD=NdbM8AaMFAHXwC -e MYSQL_ROOT_PASSWORD=Q7dhMwcKEmEX7p -d mariadb:10.6.4`

## Running

Via linha de comandos, execute a instrução abaixo:

`mvn spring-boot:run`

Caso deseje executar pelo Intellij IDEA, basta abrir o projeto que as variáveis de ambientes já serão carregadas.

A API está disponível na porta *8089* (verifique a propriedade `server.port` no arquivo `src/main/resources/application.yml`).

## Docs

A documentação da API pode ser acessada pelo endereço abaixo:

`http://localhost:8089/swagger-ui/index.html`

## Tests

`mvn test`