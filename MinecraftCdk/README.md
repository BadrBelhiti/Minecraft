# MinecraftCdk

AWS CDK infrastructure code for the Minecraft network, written in Kotlin.

## Overview

This module defines the AWS infrastructure for hosting:
- **BungeeCord Proxy**: Public-facing proxy server (ECS Fargate)
- **Survival Server**: Paper server in private subnet (ECS Fargate)
- **S3 Backup Bucket**: For world backups and data persistence

## Architecture

```
┌─────────────────────────────────────────┐
│              Internet                    │
└──────────────┬──────────────────────────┘
               │ Port 25577
       ┌───────▼────────┐
       │  BungeeCord    │
       │  (Public)      │
       └───────┬────────┘
               │
    ┌──────────▼──────────┐
    │      VPC            │
    │  ┌──────────────┐   │
    │  │  Survival    │   │
    │  │  Server      │   │
    │  │  (Private)   │   │
    │  └──────┬───────┘   │
    └─────────┼───────────┘
              │
      ┌───────▼────────┐
      │  S3 Backups    │
      └────────────────┘
```

## Infrastructure Components

### VPC
- 2 Availability Zones
- Public and Private subnets
- NAT Gateway for private subnet egress

### ECS Cluster
- Fargate launch type (serverless containers)
- Container Insights enabled for monitoring

### BungeeCord Proxy
- 1 vCPU, 2GB RAM
- Public IP assigned
- Accessible on port 25577
- Docker image: `itzg/bungeecord:latest`

### Survival Server
- 2 vCPU, 4GB RAM
- Private subnet (no public IP)
- Paper 1.20.4
- Docker image: `itzg/minecraft-server:latest`

### S3 Backup Bucket
- Versioning enabled
- S3-managed encryption

## Prerequisites

1. AWS CLI configured with credentials
2. AWS CDK CLI installed: `npm install -g aws-cdk`
3. Java 17+ (for Gradle)

## Deployment

```bash
# Build the CDK app
./gradlew :MinecraftCdk:build

# Bootstrap CDK (first time only)
cdk bootstrap

# Synthesize CloudFormation template
cdk synth

# Deploy the stack
cdk deploy

# View differences before deploying
cdk diff

# Destroy the stack (deletes all resources)
cdk destroy
```

## Configuration

Environment variables:
- `CDK_DEFAULT_ACCOUNT`: Your AWS account ID
- `CDK_DEFAULT_REGION`: AWS region (e.g., `us-east-1`)

## Cost Optimization

- Fargate pricing is based on vCPU and memory per second
- NAT Gateway has hourly charges + data transfer costs
- S3 storage costs vary by usage
- Consider stopping services when not in use to reduce costs

## Monitoring

- CloudWatch Logs: `/ecs/bungee-proxy` and `/ecs/survival-server`
- Container Insights for detailed metrics
- Log retention: 7 days

## Next Steps

1. Configure BungeeCord to point to the Survival server
2. Upload custom plugins to the containers
3. Set up automated backups to S3
4. Add CloudWatch alarms for monitoring
5. Consider adding an Application Load Balancer for high availability
