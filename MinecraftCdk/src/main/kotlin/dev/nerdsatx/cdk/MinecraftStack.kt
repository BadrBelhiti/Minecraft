package dev.nerdsatx.cdk

import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.ec2.*
import software.amazon.awscdk.services.ecs.*
import software.amazon.awscdk.services.ecs.Protocol
import software.amazon.awscdk.services.logs.LogGroup
import software.amazon.awscdk.services.logs.RetentionDays
import software.amazon.awscdk.services.s3.Bucket
import software.amazon.awscdk.services.s3.BucketEncryption
import software.constructs.Construct

/**
 * Main stack for Minecraft network infrastructure
 */
class MinecraftStack(scope: Construct, id: String, props: StackProps) : Stack(scope, id, props) {

    init {
        // Create VPC for the Minecraft network
        val vpc = Vpc.Builder.create(this, "MinecraftVpc")
            .maxAzs(2)
            .natGateways(1)
            .subnetConfiguration(
                listOf(
                    SubnetConfiguration.builder()
                        .name("Public")
                        .subnetType(SubnetType.PUBLIC)
                        .cidrMask(24)
                        .build(),
                    SubnetConfiguration.builder()
                        .name("Private")
                        .subnetType(SubnetType.PRIVATE_WITH_EGRESS)
                        .cidrMask(24)
                        .build()
                )
            )
            .build()

        // Create ECS Cluster
        val cluster = Cluster.Builder.create(this, "MinecraftCluster")
            .vpc(vpc)
            .clusterName("minecraft-network-cluster")
            .containerInsights(true)
            .build()

        // Create S3 bucket for backups
        val backupBucket = Bucket.Builder.create(this, "BackupBucket")
            .encryption(BucketEncryption.S3_MANAGED)
            .versioned(true)
            .build()

        // Create BungeeCord Proxy service
        createBungeeCordService(cluster, backupBucket)

        // Create Survival Server service
        createSurvivalServerService(cluster, backupBucket)
    }

    private fun createBungeeCordService(cluster: Cluster, backupBucket: Bucket) {
        // Task Definition for BungeeCord
        val taskDef = FargateTaskDefinition.Builder.create(this, "BungeeTaskDef")
            .memoryLimitMiB(2048)
            .cpu(1024)
            .build()

        // Grant S3 access for backups
        backupBucket.grantReadWrite(taskDef.taskRole)

        // Log group
        val logGroup = LogGroup.Builder.create(this, "BungeeLogGroup")
            .logGroupName("/ecs/bungee-proxy")
            .retention(RetentionDays.ONE_WEEK)
            .build()

        // Container for BungeeCord
        val container = taskDef.addContainer(
            "BungeeContainer",
            ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry("itzg/bungeecord:latest"))
                .logging(LogDrivers.awsLogs(AwsLogDriverProps.builder()
                    .streamPrefix("bungee")
                    .logGroup(logGroup)
                    .build()))
                .environment(
                    mapOf(
                        "EULA" to "TRUE",
                        "TYPE" to "BUNGEECORD",
                        "MEMORY" to "1G"
                    )
                )
                .build()
        )

        // Port mappings for BungeeCord
        container.addPortMappings(
            PortMapping.builder()
                .containerPort(25577)
                .protocol(Protocol.TCP)
                .build()
        )

        // Security Group for BungeeCord
        val bungeeSecurityGroup = SecurityGroup.Builder.create(this, "BungeeSecurityGroup")
            .vpc(cluster.vpc)
            .description("Security group for BungeeCord proxy")
            .allowAllOutbound(true)
            .build()

        bungeeSecurityGroup.addIngressRule(
            Peer.anyIpv4(),
            Port.tcp(25577),
            "Allow Minecraft connections"
        )

        // Fargate Service for BungeeCord
        FargateService.Builder.create(this, "BungeeService")
            .cluster(cluster)
            .taskDefinition(taskDef)
            .desiredCount(1)
            .assignPublicIp(true)
            .securityGroups(listOf(bungeeSecurityGroup))
            .serviceName("bungee-proxy")
            .build()
    }

    private fun createSurvivalServerService(cluster: Cluster, backupBucket: Bucket) {
        // Task Definition for Survival Server
        val taskDef = FargateTaskDefinition.Builder.create(this, "SurvivalTaskDef")
            .memoryLimitMiB(4096)
            .cpu(2048)
            .build()

        // Grant S3 access for backups
        backupBucket.grantReadWrite(taskDef.taskRole)

        // Log group
        val logGroup = LogGroup.Builder.create(this, "SurvivalLogGroup")
            .logGroupName("/ecs/survival-server")
            .retention(RetentionDays.ONE_WEEK)
            .build()

        // Container for Survival Server
        val container = taskDef.addContainer(
            "SurvivalContainer",
            ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry("itzg/minecraft-server:latest"))
                .logging(LogDrivers.awsLogs(AwsLogDriverProps.builder()
                    .streamPrefix("survival")
                    .logGroup(logGroup)
                    .build()))
                .environment(
                    mapOf(
                        "EULA" to "TRUE",
                        "TYPE" to "PAPER",
                        "VERSION" to "1.20.4",
                        "MEMORY" to "3G",
                        "SERVER_NAME" to "Survival",
                        "DIFFICULTY" to "normal",
                        "MODE" to "survival",
                        "ONLINE_MODE" to "FALSE", // BungeeCord handles authentication
                        "ENABLE_RCON" to "true"
                    )
                )
                .build()
        )

        // Port mappings for Survival Server
        container.addPortMappings(
            PortMapping.builder()
                .containerPort(25565)
                .protocol(Protocol.TCP)
                .build()
        )

        // Security Group for Survival Server
        val survivalSecurityGroup = SecurityGroup.Builder.create(this, "SurvivalSecurityGroup")
            .vpc(cluster.vpc)
            .description("Security group for Survival server")
            .allowAllOutbound(true)
            .build()

        // Only allow connections from within the VPC (from BungeeCord)
        survivalSecurityGroup.addIngressRule(
            Peer.ipv4(cluster.vpc.vpcCidrBlock),
            Port.tcp(25565),
            "Allow connections from BungeeCord"
        )

        // Fargate Service for Survival Server
        FargateService.Builder.create(this, "SurvivalService")
            .cluster(cluster)
            .taskDefinition(taskDef)
            .desiredCount(1)
            .assignPublicIp(false) // In private subnet
            .securityGroups(listOf(survivalSecurityGroup))
            .serviceName("survival-server")
            .build()
    }
}
