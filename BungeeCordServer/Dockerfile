FROM itzg/bungeecord:latest

# Set environment variables
ENV TYPE=BUNGEECORD \
    MEMORY=1G

# Copy the custom BungeeCord plugin from build context
# Build context is set to project root in docker-compose.yml
COPY BungeeCordServer/build/libs/BungeeCordServer.jar /server/plugins/

# Copy the pre-configured config.yml to /config directory
# The itzg/bungeecord image copies from /config/config.yml to /server/config.yml on startup
COPY BungeeCordServer/config.yml /config/config.yml

# Expose BungeeCord default port
EXPOSE 25577

# The base image handles the startup
