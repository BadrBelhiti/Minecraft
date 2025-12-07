# Docker Setup for Local Development

This guide explains how to run the Minecraft network locally using Docker.

## Prerequisites

- Docker Desktop installed and running
- Docker Compose (comes with Docker Desktop)
- At least 4GB of RAM allocated to Docker

## Quick Start

### 1. Build the Plugins

First, build all the plugins with Gradle:

```bash
./gradlew build
```

This creates:
- `SurvivalPlugin/build/libs/SurvivalPlugin.jar`
- `BungeeCordServer/build/libs/BungeeCordServer.jar`

### 2. Start the Infrastructure

Start both the BungeeCord server and Survival server:

```bash
docker-compose up -d
```

This will:
- Pull the necessary Docker images (first time only)
- Build custom images with your plugins
- Start the BungeeCord server on port 25577
- Start the Survival server (internal network only)
- Set up networking between the servers

### 3. Connect to the Server

In your Minecraft client (1.20.4):
1. Add Server
2. Server Address: `localhost:25577`
3. Connect!

You'll be automatically routed to the Survival server via BungeeCord.

## Useful Commands

### View Logs

```bash
# View all logs
docker-compose logs -f

# View BungeeCord logs only
docker-compose logs -f bungeecord-server

# View Survival server logs only
docker-compose logs -f survival-server
```

### Stop the Servers

```bash
docker-compose down
```

### Stop and Remove Data

```bash
# WARNING: This deletes all world data!
docker-compose down -v
```

### Restart a Specific Server

```bash
# Restart BungeeCord
docker-compose restart bungeecord-server

# Restart Survival server
docker-compose restart survival-server
```

### Rebuild After Code Changes

After making changes to your plugins:

```bash
# Build the plugins
./gradlew build

# Rebuild and restart the containers
docker-compose up -d --build
```

## Architecture

```
┌─────────────────┐
│  Your Client    │
│  localhost:25577│
└────────┬────────┘
         │
         ▼
┌─────────────────────┐
│  BungeeCord Server  │
│  Port: 25577        │
│  Network: minecraft │
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│  Survival Server    │
│  Port: 25565        │
│  Network: minecraft │
│  (internal only)    │
└─────────────────────┘
```

## Configuration

### BungeeCord Configuration

Edit `BungeeCordServer/config.yml` to:
- Change MOTD (message of the day)
- Add more servers
- Configure permissions
- Adjust network settings

Changes require a rebuild:
```bash
docker-compose up -d --build bungeecord-server
```

### Survival Server Settings

Modify environment variables in `docker-compose.yml`:
- `DIFFICULTY`: peaceful, easy, normal, hard
- `MODE`: survival, creative, adventure
- `MEMORY`: RAM allocation (e.g., "2G", "4G")
- `ONLINE_MODE`: Set to FALSE for BungeeCord setups

### Data Persistence

World data is persisted in Docker volumes:
- `survival-data`: Contains the Survival world
- `bungeecord-data`: Contains BungeeCord configuration

To backup world data:
```bash
docker run --rm -v minecraft_survival-data:/data -v $(pwd):/backup alpine \
  tar czf /backup/survival-backup.tar.gz -C /data .
```

To restore world data:
```bash
docker run --rm -v minecraft_survival-data:/data -v $(pwd):/backup alpine \
  tar xzf /backup/survival-backup.tar.gz -C /data
```

## Troubleshooting

### Plugins Not Loading

1. Ensure plugins are built:
   ```bash
   ls -lh SurvivalPlugin/build/libs/
   ls -lh BungeeCordServer/build/libs/
   ```

2. Rebuild containers:
   ```bash
   docker-compose up -d --build
   ```

### Can't Connect to Server

1. Check if containers are running:
   ```bash
   docker-compose ps
   ```

2. Check BungeeCord logs:
   ```bash
   docker-compose logs bungeecord-server
   ```

3. Verify port 25577 is not in use:
   ```bash
   lsof -i :25577
   ```

### Memory Issues

If servers crash due to memory:

1. Increase Docker Desktop memory allocation (Preferences → Resources)
2. Reduce memory in `docker-compose.yml`:
   ```yaml
   environment:
     MEMORY: "1G"  # Reduce from 2G
   ```

### Server Won't Start

Check logs for errors:
```bash
docker-compose logs
```

Common issues:
- EULA not accepted (shouldn't happen with our setup)
- Port already in use
- Insufficient memory
- Invalid configuration

## Development Workflow

1. Make code changes in your IDE
2. Build plugins: `./gradlew build`
3. Rebuild containers: `docker-compose up -d --build`
4. Test in Minecraft client
5. View logs: `docker-compose logs -f`
6. Repeat!

## Network Details

Both servers run on a custom Docker network called `minecraft-network`. This allows:
- BungeeCord to connect to Survival server using hostname `survival-server`
- Internal communication without exposing Survival server to host
- Easy addition of more game servers

To add another server, simply add it to `docker-compose.yml` and update BungeeCord's `config.yml`.

## Clean Start

To completely reset everything:

```bash
# Stop and remove containers and volumes
docker-compose down -v

# Remove built images
docker-compose build --no-cache

# Start fresh
docker-compose up -d
```
