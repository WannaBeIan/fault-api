# Fault API

**API-only interfaces, DTOs, and events for the Fault plugin ecosystem**

## Overview

Fault API is a comprehensive API library designed for Paper 1.21.8 plugins. It provides a clean, well-documented interface layer that promotes loose coupling between plugins and implementations.

**🚀 Recently Expanded**: This API has been dramatically expanded with 25+ new services covering interactive systems, spatial operations, economic systems, multimedia effects, scripted sequences, and much more!

### Key Features

- **Pure API Design**: Contains only interfaces, DTOs, events, and a lightweight service locator
- **Modern Java**: Built for Java 21 with records, sealed interfaces, and modern language features
- **Async-First**: All blocking operations return `CompletableFuture` for non-blocking execution
- **Type Safety**: Extensive use of type-safe patterns and null-safety annotations
- **Paper Integration**: Designed specifically for Paper with Adventure API support
- **No Implementation Details**: Free of database code, schedulers, or static singletons
- **Comprehensive Coverage**: Over 50 services spanning all aspects of Minecraft server development
- **Production Ready**: Enterprise-grade error handling, thread safety, and defensive programming

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
- `TracingService` - Distributed tracing with span correlation across async operations
- `DataRetentionService` - Policy-driven automatic data expiry and cleanup

### Player Management & Session
- `SessionService` - Player session lifecycle tracking with login/logout events
- `AfkService` - Idle detection with configurable activity thresholds
- `PresenceService` - Cross-server player presence tracking in network environments
- `PlayerSnapshotService` - Complete player state capture and restoration

### Interactive Systems & UI
- `FormsService` - Interactive text prompts and selection menus with timeout support
- `ScoreboardService` - Clean scoreboard API without Bukkit leakage
- `BossBarService` - Progress bars and status displays with dynamic updates
- `NotificationService` - Multi-channel notifications (chat, actionbar, title, webhook)

### Messaging & Communication
- `MessageService` - Component-based messaging with MiniMessage support
- `CommandModel` - Declarative command trees for Brigadier integration
- `EventBus` - Lightweight pub/sub system with sync/async channels
- `LocalizationService` - Runtime i18n with plurals and gender support
- `WebhookService` - HTTP webhook delivery with retry logic and tracking
- `MailService` - Offline messaging system with item attachments

### World & Spatial Systems
- `TeleportService` - Safe teleportation with cause tracking and validation
- `RegionIndex` - Efficient spatial indexing for region queries
- `SelectionService` - Player region selection tools and management
- `PathfindingService` - Server-side path calculation to waypoints
- `MapCanvasService` - Pixel-level drawing API for filled maps

### Economic Systems
- `EconomyFacade` - Economy operations with result types
- `AuctionHouseService` - Player-to-player auction system with bidding
- `LedgerService` - Double-entry bookkeeping with transaction tracking
- `TradeService` - Atomic player-to-player item trades
- `CatalogService` - Item catalogs with pricing and availability

### Infrastructure & Reliability
- `RollbackService` - Reversible block operations with transaction management
- `LockService` - Distributed locking with TTL and deadlock prevention
- `CircuitBreakerService` - Circuit breaker pattern for fault tolerance
- `InvalidationBus` - Cache coherence across distributed systems
- `CronScheduler` - Cron-like task scheduling with timezone support
- `SecretsService` - Secure key-value storage for sensitive data

### Data & Storage
- `ProfileService` - Per-player profile management with attributes and versioning
- `CacheFacade` - Builder-style cache configuration and monitoring
- `StorageFacade` - Repository pattern for data persistence
- `SchemaRegistry` - Versioned codec management for data serialization
- `SchemaService` - Data schema validation and migration management
- `HeatmapService` - Positional data aggregation and activity tracking
- `DependencyGraphService` - Plugin dependency tracking and analysis

### Multimedia & Effects
- `ParticleService` - Declarative particle effect system
- `SoundService` - Centralized audio playback with volume and pitch control
- `EmoteService` - Abstract animation and emote trigger system

### Game Mechanics
- `CombatMath` - Damage calculations with resistances and attributes
- `RecipeService` - Custom crafting, stonecutter, and smelting recipes
- `InventoryService` - Virtual inventory management with persistence
- `TimelineService` - Scripted sequences for cutscenes and events
- `AchievementService` - Player achievement and badge system

### Utilities & Tools
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

### New Service Examples

#### Interactive Forms and Prompts
```java
FormsService forms = Fault.require(FormsService.class);

// Text input prompt
CompletableFuture<Optional<String>> response = forms.promptText(
    playerId,
    Component.text("Enter your clan name:"),
    Duration.ofMinutes(2)
);

// Selection menu
CompletableFuture<Optional<Integer>> choice = forms.promptSelect(
    playerId,
    Component.text("Choose your class:"),
    List.of(
        Component.text("Warrior"),
        Component.text("Mage"),
        Component.text("Archer")
    ),
    Duration.ofMinutes(1)
);
```

#### Regional Systems and Spatial Queries
```java
RegionIndex regions = Fault.require(RegionIndex.class);
SelectionService selection = Fault.require(SelectionService.class);

// Define a protection area
RegionBox spawn = RegionBox.of(
    Coordinates.overworld(-100, 60, -100),
    Coordinates.overworld(100, 80, 100)
);
regions.add(NamespacedId.of("server", "spawn"), spawn);

// Query regions at a location
Set<NamespacedId> containing = regions.query(Coordinates.overworld(0, 64, 0));

// Request player selection
CompletableFuture<OperationResult> result = selection.requestSelection(
    playerId,
    Component.text("Select the area to protect")
);
```

#### Auction House and Economy
```java
AuctionHouseService auctions = Fault.require(AuctionHouseService.class);
LedgerService ledger = Fault.require(LedgerService.class);

// Create an auction listing
CompletableFuture<OperationResult> listResult = auctions.list(
    sellerId,
    ItemModel.of(NamespacedId.minecraft("diamond_sword"), 1),
    new Money(1000, "USD"), // $10.00 starting price
    Duration.ofDays(7)
);

// Record economic activity in ledger
List<Entry> transaction = List.of(
    Entry.debit(NamespacedId.of("economy", "player:" + playerId), new Money(1000, "USD"), "Auction purchase"),
    Entry.credit(NamespacedId.of("economy", "auction_revenue"), new Money(950, "USD"), "Auction sale (5% fee)"),
    Entry.credit(NamespacedId.of("economy", "auction_fees"), new Money(50, "USD"), "Auction house fee")
);
CompletableFuture<OperationResult> ledgerResult = ledger.post(transaction);
```

#### Timeline Sequences and Scripted Events
```java
TimelineService timeline = Fault.require(TimelineService.class);

// Create a welcome sequence
List<Step> welcomeSequence = List.of(
    Step.ShowTitle.of(Component.text("Welcome!")),
    Step.Wait.seconds(2),
    Step.PlaySound.of(NamespacedId.minecraft("entity.experience_orb.pickup")),
    Step.ShowTitle.of(Component.text("Enjoy your stay"), Component.text("Type /help for commands")),
    Step.Wait.seconds(3),
    Step.RunAction.of(NamespacedId.of("myplugin", "give_starter_kit"))
);

// Execute for a player
CompletableFuture<OperationResult> result = timeline.play(playerId, welcomeSequence);
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
- `NamespacedId` - Namespaced identifiers for resource keys
- `Subscription` - Cancellable subscription interface

### Player & Session Types
- `Session` - Player login/logout session tracking
- `Achievement` - Achievement definitions with rich text descriptions
- `Mail` - Offline mail messages with item attachments

### Spatial & Region Types
- `Region` - Sealed interface for different region shapes
- `RegionBox` - Axis-aligned bounding box regions
- `RegionSphere` - Spherical regions with center and radius
- `RegionPolygon` - Polygonal regions with Y-bounds support
- `Waypoint` - 3D coordinate points for pathfinding

### Economic Types
- `Listing` - Auction house listings with bidding state
- `Entry` - Double-entry bookkeeping entries
- `Offer` - Catalog item offers with expiration

### Combat & Game Mechanics
- `DamageType` - Damage type enumeration (melee, ranged, magic, etc.)
- `Resistance` - Damage resistance with percentage values
- `TeleportCause` - Teleportation reason categorization

### Multimedia & Effects
- `ParticleSpec` - Particle effect specifications with positioning
- `SoundSpec` - Sound effect specifications with volume and pitch
- `HeatSample` - Positional data points for heatmap generation

### Timeline & Scripting
- `Step` - Sealed interface for timeline step implementations
  - `Step.Wait` - Delay steps with duration
  - `Step.PlaySound` - Sound effect steps
  - `Step.ShowTitle` - Title display steps
  - `Step.RunAction` - Custom action execution steps

### System & Infrastructure
- `BreakerState` - Circuit breaker states (CLOSED, OPEN, HALF_OPEN)
- `Channel` - Notification channel enumeration
- `ListingStatus` - Auction listing status tracking
- `AudienceSelector` - Sealed interface for targeting players
- `ExecContext` - Execution context for tracing
- `Codec<T>` - Serialization codec interface

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
- `ItemModel` - Complete item stack representation with NBT data
- `RecipeModel` - Custom recipe definitions
- `InventoryModel` - Virtual inventory representation

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
