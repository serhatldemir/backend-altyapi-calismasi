# 1. Temel imaj olarak güncel Eclipse Temurin Java 17 kullan
FROM eclipse-temurin:17-jdk-jammy

# 2. Çalışma dizinini belirle
WORKDIR /app

# 3. Oluşturduğumuz jar dosyasını konteyner içine kopyala
COPY target/*.jar app.jar

# 4. Uygulamanın çalışacağı port
EXPOSE 9090

# 5. Uygulamayı başlat
ENTRYPOINT ["java", "-jar", "app.jar"]