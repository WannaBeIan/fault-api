package com.upfault.fault.api;

import com.upfault.fault.api.types.NamespacedId;
import com.upfault.fault.api.types.OperationResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing HTTP webhooks with delivery tracking and retry logic.
 * 
 * <p>This service provides reliable webhook delivery with automatic retries,
 * delivery tracking, and failure handling for external service integration.
 * 
 * <p>Example usage:
 * <pre>{@code
 * WebhookService webhooks = Fault.service(WebhookService.class);
 * if (webhooks != null) {
 *     // Register a webhook
 *     Webhook discordHook = Webhook.builder()
 *         .id(new NamespacedId("myplugin", "discord"))
 *         .url(URI.create("https://discord.com/api/webhooks/..."))
 *         .contentType("application/json")
 *         .maxRetries(3)
 *         .timeout(Duration.ofSeconds(10))
 *         .build();
 *     
 *     webhooks.registerWebhook(discordHook);
 *     
 *     // Send a webhook
 *     Map<String, Object> payload = Map.of(
 *         "content", "Player Steve joined the server!",
 *         "username", "Minecraft Server"
 *     );
 *     
 *     webhooks.sendWebhook(discordHook.id(), payload).thenAccept(result -> {
 *         switch (result) {
 *             case DeliveryResult.Success success -> 
 *                 logger.info("Webhook delivered successfully");
 *             case DeliveryResult.Failure failure -> 
 *                 logger.warning("Webhook delivery failed: " + failure.reason());
 *         }
 *     });
 * }
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All operations are thread-safe. Webhook delivery
 * is performed asynchronously on background threads with configurable timeouts.
 * 
 * @since 0.0.1
 * @apiNote Webhooks support automatic retries and delivery tracking for reliability
 */
public interface WebhookService {

    /**
     * Registers a webhook for delivery.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during registration.
     * 
     * @param webhook the webhook configuration
     * @return future that completes when registration is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> registerWebhook(@NotNull Webhook webhook);

    /**
     * Unregisters a webhook.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param webhookId the webhook identifier
     * @return future containing true if the webhook was removed
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> unregisterWebhook(@NotNull NamespacedId webhookId);

    /**
     * Gets a registered webhook by identifier.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param webhookId the webhook identifier
     * @return future containing the webhook, or null if not found
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Webhook> getWebhook(@NotNull NamespacedId webhookId);

    /**
     * Gets all registered webhook identifiers.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return future containing set of webhook identifiers
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Set<NamespacedId>> getRegisteredWebhooks();

    /**
     * Sends a webhook with JSON payload.
     * 
     * <p><strong>Threading:</strong> Thread-safe, executes delivery on background thread.
     * 
     * @param webhookId the webhook identifier
     * @param payload the JSON payload data
     * @return future containing delivery result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<DeliveryResult> sendWebhook(@NotNull NamespacedId webhookId, @NotNull Map<String, Object> payload);

    /**
     * Sends a webhook with raw payload data.
     * 
     * <p><strong>Threading:</strong> Thread-safe, executes delivery on background thread.
     * 
     * @param webhookId the webhook identifier
     * @param payload the raw payload data
     * @param contentType the content type header
     * @return future containing delivery result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<DeliveryResult> sendWebhook(@NotNull NamespacedId webhookId, @NotNull String payload, @NotNull String contentType);

    /**
     * Sends a webhook with custom headers.
     * 
     * <p><strong>Threading:</strong> Thread-safe, executes delivery on background thread.
     * 
     * @param webhookId the webhook identifier
     * @param payload the raw payload data
     * @param contentType the content type header
     * @param headers additional HTTP headers
     * @return future containing delivery result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<DeliveryResult> sendWebhook(@NotNull NamespacedId webhookId, @NotNull String payload, @NotNull String contentType, @NotNull Map<String, String> headers);

    /**
     * Gets the delivery history for a webhook.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param webhookId the webhook identifier
     * @param limit maximum number of deliveries to return
     * @return future containing list of delivery attempts
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<DeliveryAttempt>> getDeliveryHistory(@NotNull NamespacedId webhookId, int limit);

    /**
     * Gets recent failed deliveries across all webhooks.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param limit maximum number of failures to return
     * @return future containing list of failed delivery attempts
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<List<DeliveryAttempt>> getFailedDeliveries(int limit);

    /**
     * Retries a failed webhook delivery.
     * 
     * <p><strong>Threading:</strong> Thread-safe, executes retry on background thread.
     * 
     * @param deliveryId the delivery attempt identifier
     * @return future containing retry result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<DeliveryResult> retryDelivery(@NotNull String deliveryId);

    /**
     * Retries all failed deliveries for a webhook.
     * 
     * <p><strong>Threading:</strong> Thread-safe, executes retries on background thread.
     * 
     * @param webhookId the webhook identifier
     * @return future containing number of retries attempted
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Integer> retryFailedDeliveries(@NotNull NamespacedId webhookId);

    /**
     * Tests webhook connectivity without sending actual data.
     * 
     * <p><strong>Threading:</strong> Thread-safe, executes test on background thread.
     * 
     * @param webhookId the webhook identifier
     * @return future containing test result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<DeliveryResult> testWebhook(@NotNull NamespacedId webhookId);

    /**
     * Gets delivery statistics for a webhook.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during calculation.
     * 
     * @param webhookId the webhook identifier
     * @return future containing webhook statistics
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<WebhookStatistics> getWebhookStatistics(@NotNull NamespacedId webhookId);

    /**
     * Clears delivery history for a webhook.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during cleanup.
     * 
     * @param webhookId the webhook identifier
     * @return future containing number of records cleared
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Integer> clearDeliveryHistory(@NotNull NamespacedId webhookId);

    /**
     * Webhook configuration with delivery settings.
     * 
     * @param id unique identifier for this webhook
     * @param url the webhook URL endpoint
     * @param contentType default content type for payloads
     * @param headers default HTTP headers to send
     * @param timeout request timeout duration
     * @param maxRetries maximum number of retry attempts
     * @param retryDelay delay between retry attempts
     * @param enabled whether webhook delivery is enabled
     * 
     * @since 0.0.1
     */
    record Webhook(
        @NotNull NamespacedId id,
        @NotNull URI url,
        @NotNull String contentType,
        @NotNull Map<String, String> headers,
        @NotNull Duration timeout,
        int maxRetries,
        @NotNull Duration retryDelay,
        boolean enabled
    ) {

        /**
         * Creates a new webhook builder.
         * 
         * @return new webhook builder
         */
        public static @NotNull WebhookBuilder builder() {
            return new WebhookBuilder();
        }

        /**
         * Creates a simple webhook with default settings.
         * 
         * @param id the webhook identifier
         * @param url the webhook URL
         * @return webhook with default settings
         */
        public static @NotNull Webhook simple(@NotNull NamespacedId id, @NotNull URI url) {
            return new Webhook(
                id, url, "application/json", Map.of(),
                Duration.ofSeconds(10), 3, Duration.ofSeconds(1), true
            );
        }

        /**
         * Creates a copy of this webhook with a different URL.
         * 
         * @param newUrl the new webhook URL
         * @return webhook with updated URL
         */
        public @NotNull Webhook withUrl(@NotNull URI newUrl) {
            return new Webhook(id, newUrl, contentType, headers, timeout, maxRetries, retryDelay, enabled);
        }

        /**
         * Creates a copy of this webhook with different enabled status.
         * 
         * @param newEnabled the new enabled status
         * @return webhook with updated status
         */
        public @NotNull Webhook withEnabled(boolean newEnabled) {
            return new Webhook(id, url, contentType, headers, timeout, maxRetries, retryDelay, newEnabled);
        }

        /**
         * Checks if this webhook is enabled for delivery.
         * 
         * @return true if enabled
         */
        public boolean isEnabled() {
            return enabled;
        }
    }

    /**
     * Result of webhook delivery attempt.
     * 
     * @since 0.0.1
     */
    sealed interface DeliveryResult permits DeliveryResult.Success, DeliveryResult.Failure {

        /**
         * Successful delivery result.
         * 
         * @param statusCode HTTP response status code
         * @param responseBody response body from webhook endpoint
         * @param duration delivery duration
         */
        record Success(int statusCode, @NotNull String responseBody, @NotNull Duration duration) implements DeliveryResult {

            /**
             * Checks if the response indicates success.
             * 
             * @return true if status code indicates success (200-299)
             */
            public boolean isHttpSuccess() {
                return statusCode >= 200 && statusCode < 300;
            }
        }

        /**
         * Failed delivery result.
         * 
         * @param reason failure reason
         * @param statusCode HTTP status code (if available)
         * @param cause underlying exception
         */
        record Failure(@NotNull String reason, int statusCode, @Nullable Throwable cause) implements DeliveryResult {

            /**
             * Creates a failure result from exception.
             * 
             * @param cause the exception that caused the failure
             * @return failure delivery result
             */
            public static @NotNull Failure from(@NotNull Throwable cause) {
                return new Failure(cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName(), 0, cause);
            }

            /**
             * Creates a failure result from HTTP status.
             * 
             * @param statusCode the HTTP status code
             * @param reason the failure reason
             * @return failure delivery result
             */
            public static @NotNull Failure http(int statusCode, @NotNull String reason) {
                return new Failure(reason, statusCode, null);
            }

            /**
             * Checks if this was an HTTP error.
             * 
             * @return true if status code indicates HTTP error
             */
            public boolean isHttpError() {
                return statusCode >= 400;
            }
        }

        /**
         * Checks if delivery was successful.
         * 
         * @return true if delivery succeeded
         */
        default boolean isSuccess() {
            return this instanceof Success;
        }

        /**
         * Checks if delivery failed.
         * 
         * @return true if delivery failed
         */
        default boolean isFailure() {
            return this instanceof Failure;
        }
    }

    /**
     * Record of a webhook delivery attempt.
     * 
     * @param id unique identifier for this delivery attempt
     * @param webhookId the webhook identifier
     * @param timestamp when the delivery was attempted
     * @param result the delivery result
     * @param attempt the attempt number (1-based)
     * @param payload the payload that was sent
     * 
     * @since 0.0.1
     */
    record DeliveryAttempt(
        @NotNull String id,
        @NotNull NamespacedId webhookId,
        @NotNull Instant timestamp,
        @NotNull DeliveryResult result,
        int attempt,
        @NotNull String payload
    ) {

        /**
         * Checks if this delivery attempt was successful.
         * 
         * @return true if delivery succeeded
         */
        public boolean isSuccessful() {
            return result.isSuccess();
        }

        /**
         * Checks if this was a retry attempt.
         * 
         * @return true if attempt number is greater than 1
         */
        public boolean isRetry() {
            return attempt > 1;
        }

        /**
         * Gets the failure reason if delivery failed.
         * 
         * @return failure reason, or null if delivery succeeded
         */
        public @Nullable String getFailureReason() {
            return result instanceof DeliveryResult.Failure failure ? failure.reason() : null;
        }
    }

    /**
     * Statistics for webhook delivery performance.
     * 
     * @param webhookId the webhook identifier
     * @param totalDeliveries total number of delivery attempts
     * @param successfulDeliveries number of successful deliveries
     * @param failedDeliveries number of failed deliveries
     * @param averageDeliveryTime average delivery time in milliseconds
     * @param lastDelivery timestamp of last delivery attempt
     * @param lastSuccess timestamp of last successful delivery
     * 
     * @since 0.0.1
     */
    record WebhookStatistics(
        @NotNull NamespacedId webhookId,
        long totalDeliveries,
        long successfulDeliveries,
        long failedDeliveries,
        long averageDeliveryTime,
        @Nullable Instant lastDelivery,
        @Nullable Instant lastSuccess
    ) {

        /**
         * Gets the success rate as a percentage.
         * 
         * @return success rate (0.0 to 1.0)
         */
        public double getSuccessRate() {
            return totalDeliveries > 0 ? (double) successfulDeliveries / totalDeliveries : 0.0;
        }

        /**
         * Gets the failure rate as a percentage.
         * 
         * @return failure rate (0.0 to 1.0)
         */
        public double getFailureRate() {
            return totalDeliveries > 0 ? (double) failedDeliveries / totalDeliveries : 0.0;
        }

        /**
         * Gets the average delivery time as a duration.
         * 
         * @return average delivery duration
         */
        public @NotNull Duration getAverageDeliveryDuration() {
            return Duration.ofMillis(averageDeliveryTime);
        }
    }

    /**
     * Builder for creating webhook configurations.
     * 
     * @since 0.0.1
     */
    class WebhookBuilder {
        private NamespacedId id;
        private URI url;
        private String contentType = "application/json";
        private Map<String, String> headers = Map.of();
        private Duration timeout = Duration.ofSeconds(10);
        private int maxRetries = 3;
        private Duration retryDelay = Duration.ofSeconds(1);
        private boolean enabled = true;

        private WebhookBuilder() {}

        /**
         * Sets the webhook identifier.
         * 
         * @param id the webhook identifier
         * @return this builder
         */
        public @NotNull WebhookBuilder id(@NotNull NamespacedId id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the webhook URL.
         * 
         * @param url the webhook URL
         * @return this builder
         */
        public @NotNull WebhookBuilder url(@NotNull URI url) {
            this.url = url;
            return this;
        }

        /**
         * Sets the default content type.
         * 
         * @param contentType the content type
         * @return this builder
         */
        public @NotNull WebhookBuilder contentType(@NotNull String contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * Sets the default headers.
         * 
         * @param headers the HTTP headers
         * @return this builder
         */
        public @NotNull WebhookBuilder headers(@NotNull Map<String, String> headers) {
            this.headers = Map.copyOf(headers);
            return this;
        }

        /**
         * Adds a header.
         * 
         * @param name the header name
         * @param value the header value
         * @return this builder
         */
        public @NotNull WebhookBuilder header(@NotNull String name, @NotNull String value) {
            Map<String, String> newHeaders = new java.util.HashMap<>(this.headers);
            newHeaders.put(name, value);
            this.headers = Map.copyOf(newHeaders);
            return this;
        }

        /**
         * Sets the request timeout.
         * 
         * @param timeout the timeout duration
         * @return this builder
         */
        public @NotNull WebhookBuilder timeout(@NotNull Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Sets the maximum retry attempts.
         * 
         * @param maxRetries the max retries
         * @return this builder
         */
        public @NotNull WebhookBuilder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        /**
         * Sets the delay between retries.
         * 
         * @param retryDelay the retry delay
         * @return this builder
         */
        public @NotNull WebhookBuilder retryDelay(@NotNull Duration retryDelay) {
            this.retryDelay = retryDelay;
            return this;
        }

        /**
         * Sets the enabled status.
         * 
         * @param enabled whether webhook is enabled
         * @return this builder
         */
        public @NotNull WebhookBuilder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * Builds the webhook configuration.
         * 
         * @return the completed webhook
         * @throws IllegalStateException if required fields are not set
         */
        public @NotNull Webhook build() {
            if (id == null) throw new IllegalStateException("Webhook ID is required");
            if (url == null) throw new IllegalStateException("Webhook URL is required");
            return new Webhook(id, url, contentType, headers, timeout, maxRetries, retryDelay, enabled);
        }
    }
}