FROM gradle:8.3-jdk17 AS build

WORKDIR /app

# Копируем файлы сборки
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Загружаем зависимости
RUN gradle dependencies --no-daemon

# Копируем исходный код
COPY src ./src

# Собираем проект
RUN gradle build --no-daemon -x test

# Финальный образ
FROM eclipse-temurin:17-jre

WORKDIR /app

# Копируем JAR из этапа сборки
COPY --from=build /app/build/libs/*.jar app.jar

# Переменные среды
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]