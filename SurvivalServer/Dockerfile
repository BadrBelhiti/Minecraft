FROM itzg/minecraft-server:latest

# Set environment variables for Paper server
ENV EULA=TRUE \
    TYPE=PAPER \
    VERSION=1.20.4 \
    MEMORY=2G \
    SERVER_NAME="Survival Server" \
    DIFFICULTY=normal \
    MODE=survival \
    ONLINE_MODE=FALSE \
    ENABLE_RCON=true \
    RCON_PASSWORD=minecraft \
    SPIGOT_BUNGEECORD=true

# Copy the custom Survival plugin from build context
# Build context is set to project root in docker-compose.yml
COPY SurvivalPlugin/build/libs/SurvivalPlugin.jar /plugins/

# Expose Minecraft server port
EXPOSE 25565

# The base image handles the startup
