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

  ## Screenshots

- CADASTRO

![cadastro](https://github.com/user-attachments/assets/34a92b4b-8066-462a-8dbb-18bfeac7a2b0)


- LOGIN

![login](https://github.com/user-attachments/assets/b4bd5591-ec63-4867-8af1-7fc499407f1c)

- HOME
  
![home](https://github.com/user-attachments/assets/c02fc9de-30ae-437d-8cda-b9b3a2871980)


- CANAIS DA COMUNIDADE

![canais](https://github.com/user-attachments/assets/b50eb93b-c376-4cac-87c5-1c3b95328063)

- VÍDEOS DO USUÁRIO
  
![seus vídeos](https://github.com/user-attachments/assets/c344748c-63d6-4140-aa32-200d0cc6ec64)

- PAGINA DO VÍDEO
  
 ![image](https://github.com/user-attachments/assets/11fba9f4-948a-47d3-a6ab-0ca13ac680db)

- PERFIL
  
![perfil](https://github.com/user-attachments/assets/deefe46d-c6ed-4dfc-9adb-e58a2da9d600)

GERENCIAMENTO DE VÍDEOS

![gerenciador de vídeos](https://github.com/user-attachments/assets/fc809a5c-fca0-4d55-bd30-37767a75d91e)


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

Abra o integrated terminal e digite "npm install" e depois "ng s" 

![executando o front end](https://github.com/user-attachments/assets/f0b6bfac-b2b3-4183-8cd0-6d07c54da9d9)

por padrão, o projeto é localizado na url: http://localhost:4200/

### Possíveis erros

Ao abrir o projeto back-end através do IntelliJ, o projeto pode apresentar o erro: "IntelliJ: Cannot resolve symbol 'springframework'".
Para corrigir este erro, abra as configurações do Maven, selecione o nome do projeto "video-platform-v2" e selecione "Reload All Maven Projects".

![image](https://github.com/user-attachments/assets/81a69512-0a4c-489c-a3fc-99d691a06a82)


### Referências

- Para a construção do front-end: Curso do @wellingtonfoz (https://github.com/wellingtonfoz)
- Elaboração do ReadMe: loiane (https://github.com/loiane)

