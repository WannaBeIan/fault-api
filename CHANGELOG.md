# Changelog

All notable changes to the Fault API will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.0.1] - 2025-01-XX

### Added
- Initial API design and structure
- Core service interfaces:
  - `PlatformInfoService` for server metrics and information
  - `TimeService` for tick/duration conversions
  - `SchedulerFacade` for Folia-friendly task scheduling
  - `MessageService` for Component-based messaging with MiniMessage
  - `CommandModel` for declarative command tree definitions
  - `EventBus` for lightweight pub/sub messaging
  - `ProfileService` for per-player profile management
  - `CacheFacade` for cache configuration and monitoring
  - `StorageFacade` for repository-pattern data access
  - `EconomyFacade` for economy operations with result types
  - `RateLimitService` for token-bucket rate limiting
  - `ItemFacade` for immutable item descriptions
  - `GuiModel` for declarative inventory GUIs
  - `RegionModel` for world coordinate abstractions
  - `AuditFacade` for append-only audit logging
- Comprehensive DTO record types:
  - `TpsSnapshot` for server performance metrics
  - `DurationTicks` and `TickTime` for server time handling
  - `Uptime` for server uptime information
  - `AudienceSelector` for message targeting
  - `Money` for currency with minor unit precision
  - `NamespacedId` for conflict-free identifiers
  - `Coordinates` for world positions
  - `Page<T>` for paginated results
  - `OperationResult` sealed interface for success/failure handling
  - `AttributeKey<T>` for type-safe profile attributes
  - `ProfileId` and `ProfileSnapshot` for player profile management
  - `RateLimitResult` for rate limiting responses
  - `ItemDescription` for platform-agnostic item representation
  - `CacheStats` for cache performance monitoring
  - `AuditEvent` for audit logging
- Paper event classes:
  - `ProfileLoadEvent` for profile loading notifications
  - `ProfileSaveEvent` for profile save operations (cancellable)
  - `ProfileUnloadEvent` for profile unloading notifications
  - `EconomyChangeEvent` for balance change notifications
  - `RateLimitTriggeredEvent` for rate limit violations
  - `AuditEventFired` for audit event notifications
- `Fault` service locator utility class with ServicesManager integration
- Complete Javadoc documentation with examples and threading notes
- Maven configuration for Java 21 with Paper 1.21.8 and Adventure dependencies

### Technical Details
- Java 21 language features including records and sealed interfaces
- All blocking operations return `CompletableFuture` for async execution
- Null-safety with JetBrains annotations
- No implementation details - pure API design
- Bukkit ServicesManager integration for loose coupling
- Adventure Component API for rich text
- Comprehensive validation and error handling

### Documentation
- README with usage examples and integration guide
- MIT license
- This changelog

[0.0.1]: https://github.com/your-org/fault-api/releases/tag/v0.0.1
