#!/bin/bash

# Configuration
IMAGE_NAME="blog-api"
IMAGE_TAG="latest"
COMPOSE_FILE="docker-compose-dev.yml"

echo "==============================="
echo "👉🗑️ Remove old Docker image"
echo "==============================="
docker rmi -f ${IMAGE_NAME}:${IMAGE_TAG} 2>/dev/null || echo "Image not found, skipping removal."


echo "==============================="
echo "🧹 Clean and build Spring Boot project"
echo "==============================="

mvn clean package


echo "==============================="r
echo "🐳 Step 3: Rebuild and start Docker Compose"
echo "==============================="

docker compose -f ${COMPOSE_FILE} up --build
