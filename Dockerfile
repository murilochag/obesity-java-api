FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom.xml para baixar dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código-fonte
COPY src ./src

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação em modo desenvolvimento
CMD ["mvn", "spring-boot:run"]