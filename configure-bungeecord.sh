#!/bin/bash

# Script to configure BungeeCord to connect to the Survival server

set -e

echo "🔧 Configuring BungeeCord..."
echo ""

# Check if BungeeCord container is running
if ! docker ps | grep -q minecraft-bungeecord; then
    echo "❌ BungeeCord container is not running. Start it first with:"
    echo "   docker-compose up -d"
    exit 1
fi

echo "⏳ Waiting for BungeeCord to generate config.yml..."
sleep 5

# Copy the pre-configured config to the container
echo "📝 Updating BungeeCord configuration..."
docker cp BungeeCordServer/config.yml minecraft-bungeecord:/server/config.yml

echo "🔄 Restarting BungeeCord to apply configuration..."
docker-compose restart bungeecord-server

echo ""
echo "✅ BungeeCord configured successfully!"
echo ""
echo "Configuration changes:"
echo "  - Added 'survival' server pointing to survival-server:25565"
echo "  - Set default server to 'survival'"
echo "  - Enabled IP forwarding for BungeeCord"
echo ""
echo "📊 View logs to confirm:"
echo "   docker-compose logs -f bungeecord-server"
