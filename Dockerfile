FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia os arquivos de configuração e baixa as dependências
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copia o código-fonte e gera o arquivo .jar
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Estágio 2: Execução da aplicação (imagem leve)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia apenas o .jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão e inicia a aplicação
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]