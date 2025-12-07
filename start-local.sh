#!/bin/bash

# Script to build and start the Minecraft network locally with Docker

set -e

echo "🎮 Starting Nerds @ ATX Minecraft Network..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running. Please start Docker Desktop."
    exit 1
fi

# Build the plugins
echo "📦 Building plugins..."
./gradlew build -q

if [ $? -ne 0 ]; then
    echo "❌ Build failed. Please fix the errors and try again."
    exit 1
fi

echo "✅ Plugins built successfully"
echo ""

# Start Docker containers
echo "🐳 Starting Docker containers..."
docker-compose up -d

if [ $? -ne 0 ]; then
    echo "❌ Failed to start containers. Check the error messages above."
    exit 1
fi

echo ""
echo "✅ Servers started!"
echo ""

# Configure BungeeCord
echo "🔧 Configuring BungeeCord to connect to Survival server..."
sleep 10  # Wait for BungeeCord to initialize

if docker ps | grep -q minecraft-bungeecord; then
    docker cp BungeeCordServer/config.yml minecraft-bungeecord:/server/config.yml 2>/dev/null || true
    docker-compose restart bungeecord-server > /dev/null 2>&1
    echo "✅ BungeeCord configured!"
fi

echo ""
echo "📊 View logs:"
echo "   docker-compose logs -f"
echo ""
echo "🎮 Connect to the server:"
echo "   Server Address: localhost:25577"
echo ""
echo "🛑 Stop servers:"
echo "   docker-compose down"
echo ""
echo "📖 For more info, see DOCKER.md"
