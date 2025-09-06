package com.upfault.fault.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Static helper class for accessing Fault API services through Bukkit's ServicesManager.
 * 
 * <p>This class provides convenience methods for retrieving service implementations
 * that have been registered with Bukkit's service registry.
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Get a service (returns null if not available)
 * PlatformInfoService platformInfo = Fault.service(PlatformInfoService.class);
 * if (platformInfo != null) {
 *     TpsSnapshot tps = platformInfo.getCurrentTps();
 *     System.out.println("Current TPS: " + tps.tps1m());
 * }
 * 
 * // Require a service (throws exception if not available)
 * try {
 *     MessageService messages = Fault.require(MessageService.class);
 *     messages.broadcast(Component.text("Hello, world!"));
 * } catch (IllegalStateException e) {
 *     System.err.println("MessageService not available: " + e.getMessage());
 * }
 * }</pre>
 * 
 * <p><strong>Threading:</strong> Service lookups are thread-safe but the returned
 * services may have their own threading requirements.
 * 
 * @since 0.0.1
 * @apiNote This class does not cache services - each call queries ServicesManager
 */
public final class Fault {
    
    private Fault() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Gets a service implementation from Bukkit's ServicesManager.
     * 
     * <p>Returns the highest priority registration of the requested service type,
     * or null if no implementation is registered.
     * 
     * @param type the service interface class
     * @param <T> the service type
     * @return the service implementation, or null if not available
     * @throws IllegalArgumentException if type is null
     */
    public static <T> @Nullable T service(@NotNull Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }
        
        RegisteredServiceProvider<T> provider = Bukkit.getServicesManager().getRegistration(type);
        return provider != null ? provider.getProvider() : null;
    }
    
    /**
     * Gets a required service implementation from Bukkit's ServicesManager.
     * 
     * <p>Returns the highest priority registration of the requested service type.
     * Throws an exception if no implementation is registered.
     * 
     * @param type the service interface class
     * @param <T> the service type
     * @return the service implementation (never null)
     * @throws IllegalArgumentException if type is null
     * @throws IllegalStateException if no service implementation is registered
     */
    public static <T> @NotNull T require(@NotNull Class<T> type) {
        T service = service(type);
        if (service == null) {
            throw new IllegalStateException("Required service not available: " + type.getSimpleName() + 
                ". Make sure a Fault implementation plugin is installed and loaded.");
        }
        return service;
    }
    
    /**
     * Checks if a service implementation is available.
     * 
     * @param type the service interface class
     * @return true if a service implementation is registered
     * @throws IllegalArgumentException if type is null
     */
    public static boolean isAvailable(@NotNull Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }
        
        return Bukkit.getServicesManager().isProvidedFor(type);
    }
    
    /**
     * Gets all registered service providers for a service type.
     * 
     * <p>Returns all registered implementations of the service, ordered by priority
     * (highest priority first).
     * 
     * @param type the service interface class
     * @param <T> the service type
     * @return list of all registered service providers (may be empty)
     * @throws IllegalArgumentException if type is null
     */
    public static <T> @NotNull java.util.List<RegisteredServiceProvider<T>> getProviders(@NotNull Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }
        
        return new java.util.ArrayList<>(Bukkit.getServicesManager().getRegistrations(type));
    }
    
    /**
     * Gets the number of registered providers for a service type.
     * 
     * @param type the service interface class
     * @return the number of registered providers
     * @throws IllegalArgumentException if type is null
     */
    public static int getProviderCount(@NotNull Class<?> type) {
        return getProviders(type).size();
    }
    
    /**
     * Attempts to get multiple services at once.
     * 
     * <p>This is a convenience method for batch service lookups.
     * Services that are not available will be null in the returned array.
     * 
     * @param types the service types to look up
     * @return array of services in the same order as the types (some may be null)
     * @throws IllegalArgumentException if types array is null or empty
     */
    @SafeVarargs
    public static @NotNull Object[] services(@NotNull Class<?>... types) {
        if (types == null || types.length == 0) {
            throw new IllegalArgumentException("Service types cannot be null or empty");
        }
        
        Object[] services = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            services[i] = service(types[i]);
        }
        return services;
    }
    
    /**
     * Checks if all specified services are available.
     * 
     * @param types the service types to check
     * @return true if all services are available
     * @throws IllegalArgumentException if types array is null or empty
     */
    public static boolean allAvailable(@NotNull Class<?>... types) {
        if (types == null || types.length == 0) {
            throw new IllegalArgumentException("Service types cannot be null or empty");
        }
        
        for (Class<?> type : types) {
            if (!isAvailable(type)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if any of the specified services are available.
     * 
     * @param types the service types to check
     * @return true if at least one service is available
     * @throws IllegalArgumentException if types array is null or empty
     */
    public static boolean anyAvailable(@NotNull Class<?>... types) {
        if (types == null || types.length == 0) {
            throw new IllegalArgumentException("Service types cannot be null or empty");
        }
        
        for (Class<?> type : types) {
            if (isAvailable(type)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets a list of all available Fault API service types.
     * 
     * <p>This method scans the registered services and returns only those
     * that are part of the Fault API (in the com.upfault.fault.api package).
     * 
     * @return list of available Fault service types
     */
    public static @NotNull java.util.List<Class<?>> getAvailableFaultServices() {
        return Bukkit.getServicesManager().getKnownServices().stream()
                     .filter(clazz -> clazz.getName().startsWith("com.upfault.fault.api."))
                     .filter(clazz -> clazz.isInterface())
                     .filter(Fault::isAvailable)
                     .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Gets diagnostic information about service availability.
     * 
     * <p>Returns a formatted string with information about which Fault services
     * are available and which are missing.
     * 
     * @return diagnostic information string
     */
    public static @NotNull String getDiagnosticInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Fault API Service Status:\n");
        
        // Core service types to check
        Class<?>[] coreServices = {
            PlatformInfoService.class,
            TimeService.class,
            SchedulerFacade.class,
            MessageService.class,
            CommandModel.class,
            EventBus.class,
            ProfileService.class,
            CacheFacade.class,
            StorageFacade.class,
            EconomyFacade.class,
            RateLimitService.class,
            ItemFacade.class,
            GuiModel.class,
            RegionModel.class,
            AuditFacade.class,
            CapabilityService.class,
            ResourcePackService.class,
            RngService.class,
            HealthCheckService.class,
            DiagnosticsService.class,
            FlagService.class,
            SchemaService.class,
            WebhookService.class,
            InternationalizationService.class,
            AuditService.class
        };
        
        int available = 0;
        for (Class<?> service : coreServices) {
            boolean isAvailable = isAvailable(service);
            sb.append("  ").append(service.getSimpleName()).append(": ")
              .append(isAvailable ? "✓ Available" : "✗ Missing").append("\n");
            if (isAvailable) available++;
        }
        
        sb.append("\nSummary: ").append(available).append("/").append(coreServices.length)
          .append(" services available");
        
        return sb.toString();
    }
}
