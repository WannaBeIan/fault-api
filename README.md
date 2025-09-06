# Fault API

**API-only interfaces, DTOs, and events for the Fault plugin ecosystem**

## Overview

Fault API is a comprehensive API library designed for Paper 1.21.8 plugins. It provides a clean, well-documented interface layer that promotes loose coupling between plugins and implementations.

### Key Features

- **Pure API Design**: Contains only interfaces, DTOs, events, and a lightweight service locator
- **Modern Java**: Built for Java 21 with records, sealed interfaces, and modern language features
- **Async-First**: All blocking operations return `CompletableFuture` for non-blocking execution
- **Type Safety**: Extensive use of type-safe patterns and null-safety annotations
- **Paper Integration**: Designed specifically for Paper with Adventure API support
- **No Implementation Details**: Free of database code, schedulers, or static singletons

## Maven Coordinates

```xml
<dependency>
    <groupId>com.upfault</groupId>
    <artifactId>fault-api</artifactId>
    <version>0.0.1</version>
    <scope>compileOnly</scope>
</dependency>
```

## Core Services

The API provides the following service interfaces:

### Platform & System
- `PlatformInfoService` - Server metrics, TPS, uptime, player counts
- `TimeService` - Tick/duration conversions and server time utilities  
- `SchedulerFacade` - Folia-friendly async and sync task scheduling
- `CapabilityService` - Runtime capability detection and API versioning
- `HealthCheckService` - Plugin health monitoring with configurable checks
- `DiagnosticsService` - System metrics collection and performance monitoring

### Messaging & Communication
- `MessageService` - Component-based messaging with MiniMessage support
- `CommandModel` - Declarative command trees for Brigadier integration
- `EventBus` - Lightweight pub/sub system with sync/async channels
- `InternationalizationService` - Multi-language support with ICU formatting
- `WebhookService` - HTTP webhook delivery with retry logic and tracking

### Data & Storage
- `ProfileService` - Per-player profile management with attributes and versioning
- `CacheFacade` - Builder-style cache configuration and monitoring
- `StorageFacade` - Repository pattern for data persistence
- `SchemaService` - Data schema validation and migration management

### Game Systems
- `EconomyFacade` - Economy operations with result types
- `RateLimitService` - Token bucket rate limiting for any operation
- `ItemFacade` - Immutable item descriptions without ItemStack coupling
- `GuiModel` - Declarative inventory GUI system
- `RegionModel` - World coordinate and region abstractions
- `ResourcePackService` - Resource pack distribution and status tracking
- `RngService` - Deterministic random number generation with sessions
- `FlagService` - Temporary and permanent feature flags for players and worlds

### Auditing & Monitoring
- `AuditFacade` - Append-only audit events with configurable sinks
- `AuditService` - Comprehensive audit logging with structured metadata

## Service Usage

Services are accessed through Bukkit's ServicesManager using the `Fault` helper class:

```java
// Optional service access
PlatformInfoService platform = Fault.service(PlatformInfoService.class);
if (platform != null) {
    TpsSnapshot tps = platform.getCurrentTps();
    getLogger().info("Current TPS: " + tps.tps1m());
}

// Required service access (throws if unavailable)
try {
    MessageService messages = Fault.require(MessageService.class);
    messages.broadcast(Component.text("Server announcement!"));
} catch (IllegalStateException e) {
    getLogger().warning("MessageService not available: " + e.getMessage());
}

// Check service availability
if (Fault.isAvailable(EconomyFacade.class)) {
    // Economy operations available
}
```

## Threading Model

- **Service Lookups**: Thread-safe
- **Async Operations**: All potentially blocking operations return `CompletableFuture`
- **Main Thread**: Sync operations run on the main server thread
- **Background Threads**: Async operations use background thread pools

## Error Handling

The API uses result types instead of exceptions for expected failures:

```java
EconomyFacade economy = Fault.require(EconomyFacade.class);
Money cost = new Money(5000, "USD"); // $50.00

CompletableFuture<OperationResult> result = economy.withdraw(playerId, cost);
result.thenAccept(res -> {
    switch (res) {
        case OperationResult.Success success -> {
            player.sendMessage("Purchase successful!");
        }
        case OperationResult.Failure failure -> {
            player.sendMessage("Purchase failed: " + failure.reason());
        }
    }
});
```

## Plugin Integration

### For API Consumers

1. Add `fault-api` as `compileOnly` dependency
2. Access services through `Fault.service()` or `Fault.require()`
3. Handle service unavailability gracefully
4. Use async patterns with CompletableFuture

```xml
<dependencies>
    <dependency>
        <groupId>com.upfault</groupId>
        <artifactId>fault-api</artifactId>
        <version>0.0.1</version>
        <scope>compileOnly</scope>
    </dependency>
</dependencies>
```

### For Implementation Providers

1. Depend on `fault-api` with `api` scope
2. Implement service interfaces
3. Register implementations with Bukkit's ServicesManager
4. Handle service lifecycle (enable/disable)

```java
public class MyFaultPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // Create service implementations
        MyPlatformInfoService platformService = new MyPlatformInfoService();
        MyMessageService messageService = new MyMessageService();
        
        // Register with ServicesManager
        getServer().getServicesManager().register(
            PlatformInfoService.class, 
            platformService, 
            this, 
            ServicePriority.Normal
        );
        
        getServer().getServicesManager().register(
            MessageService.class,
            messageService,
            this,
            ServicePriority.Normal
        );
    }
}
```

## Event System

The API includes Bukkit events for key operations:

```java
@EventHandler
public void onProfileLoad(ProfileLoadEvent event) {
    UUID playerId = event.getPlayerId();
    ProfileSnapshot profile = event.getProfile();
    
    if (event.wasCreated()) {
        getLogger().info("Created new profile for " + playerId);
    } else {
        getLogger().info("Loaded existing profile for " + playerId);
    }
}

@EventHandler  
public void onEconomyChange(EconomyChangeEvent event) {
    Money newBalance = event.getNewBalance();
    getLogger().info("Player " + event.getPlayerId() + " now has " + newBalance);
}
```

## Data Types

The API includes comprehensive DTO records and type-safe constructs:

### Core Types
- `TpsSnapshot` - Server performance metrics
- `Money` - Currency amounts with minor unit precision
- `Coordinates` - World positions with NamespacedId worlds
- `Page<T>` - Paginated results with navigation
- `OperationResult` - Success/failure result types with fault codes
- `ProfileSnapshot` - Immutable player profile data with versioning
- `ItemDescription` - Platform-agnostic item representation

### Advanced Types
- `AttributeKey<T>` - Type-safe keys for attribute storage
- `AttributeMap` - Thread-safe attribute modifier collections
- `DataKey<T>` - Type-safe keys for persistent data access
- `DataView` - Hierarchical data access with type safety
- `CooldownState` - Immutable cooldown/warmup state tracking
- `ResourcePack` - Resource pack definitions with metadata
- `Modifier` - Attribute modifiers with operation ordering
- `FaultCode` - Standardized error classification
- `ApiVersion` - Semantic versioning with compatibility checking
- `Capability` - System capability enumeration

## Version Compatibility

- **Java**: 21+
- **Paper**: 1.21.8+
- **API Stability**: Semantic versioning with careful deprecation cycles

## Building

```bash
mvn clean compile
mvn package
```

## License

MIT License - see LICENSE file for details.
