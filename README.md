# API de vídeos com Spring Boot e Angular

API para o gerenciamento de uma plataforma de vídeos.

Projeto desenvolvido para a minha evolução com as tecnologias do Spring Boot.

## 💻 Tecnologias

- Java 17
- Spring Boot
- Maven
- JPA/Hibernate
- MySQL
- JUnit 5/Mockito
- Angular 17
- Spring Security
- Swagger
- JACOCO

## ⌨️ IDE

- Visual Studio Code (Front-end)
- Intellij (Back-end)

## Funcionalidades da API

- ✅ Classes para model
- ✅ JPA repository
- ✅ Banco de dados MySQL
- ✅ Camadas de Controller, Service e Repository
- ✅ DTOs
- ✅ Testes unitários e de integração 
- ✅ Hibernate
- ✅ JACOCO
- ✅ Spring doc com Swagger [api-documentation.pdf](https://github.com/user-attachments/files/17576625/api-documentation.pdf)

## ❗️Executando o projeto

### Para executar o projeto back-end

Para executar este back-end, você precisa instalar o Java 17 e o banco de dados MySQL.

Baixe a pasta chamada "back-end" e abra com a sua ferramente de desenvolvimento (Visual Studio, IntelliJ, Eclipse , etc).

Após baixar o back-end, você tera que fazer algumas alterações no arquivo "back-end/src/main/resources/application.properties", para o funcionamento do MySQL, é preciso alterar as seguintes propriedades:
- "spring.datasource.password={senha root}" alterar para a sua senha root, definida no MySQL
- "spring.datasource.username={usuário root}" alterar para o nome de usuário root definido por você, por padrão o MySQL define o username como "root".
- Para a conexão com o banco de dados, utilizamos a prorpiedade: spring.datasource.url=jdbc:mysql://localhost:3306/video_platform_v2, antes de executar a aplicação, você deve criar um banco de dados chamado "video_platform_v2".
- Como esta API realiza o upload de vídeos, temos que definir um local para salvar os vídeos, por padrão, o projeto esta definido para salvar todos os vídeos neste caminho: "C:\videos", então é necessario criar uma pasta chamada vídeos no caminho "C:\", para alterar o local e preciso alterar a propriedade "file.upload-dir={DIRETÓRIO SELECIONADO}" dentro do "application.properties".
- Execute o projeto através da classe VideoPlatformV2Application.

### Para executar o projeto front-end

Para executar este front-end, você precisa instalar o npm, node.js 20.13.1 e o Angular CLI 17.3.6.

Baixe a pasta chamada "front-end" e abra com a sua ferramenta de desenvolvimento.

Abra o terminal e digite "ng s" 

por padrão, o projeto é localizado na url: http://localhost:4200/

### Referências

- Para a construção do front-end: Curso do @wellingtonfoz (https://github.com/wellingtonfoz)
- Elaboração do ReadMe: loiane (https://github.com/loiane)

