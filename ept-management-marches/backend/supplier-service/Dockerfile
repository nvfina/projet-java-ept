# ============================================================
# Dockerfile - Service Gestion des Fournisseurs
# ============================================================
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="EPT Gestion des Marchés"
LABEL description="Microservice de gestion des fournisseurs"

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copier le JAR construit
COPY target/*.jar app.jar

# Exposer le port du service
EXPOSE 8083

# Utiliser l'utilisateur non-root
USER appuser

# Point d'entrée avec options JVM optimisées pour les conteneurs
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
