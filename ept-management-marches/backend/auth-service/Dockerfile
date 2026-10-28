# ==============================================================================
# Dockerfile multi-stage pour le service d'authentification
# ==============================================================================

# --- Étape 1 : Construction avec Maven ---
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copier les fichiers POM pour le cache des dépendances
COPY ../../pom.xml /app/parent-pom.xml
COPY pom.xml .

# Copier le module commun
COPY ../common /app/common

# Copier le code source
COPY src ./src

# Construire le projet (sans les tests pour accélérer le build Docker)
RUN mvn clean package -DskipTests -f pom.xml

# --- Étape 2 : Image d'exécution légère ---
FROM eclipse-temurin:21-jre

WORKDIR /app

# Créer un utilisateur non-root pour la sécurité
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Changer le propriétaire du fichier
RUN chown appuser:appuser app.jar

# Utiliser l'utilisateur non-root
USER appuser

# Exposer le port du service
EXPOSE 8081

# Point d'entrée avec options JVM optimisées pour les conteneurs
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
