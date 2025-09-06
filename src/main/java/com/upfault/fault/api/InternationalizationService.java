package com.upfault.fault.api;

import com.upfault.fault.api.types.NamespacedId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing internationalization (i18n) and localization (l10n) of plugin content.
 * 
 * <p>This service provides centralized translation management with support for
 * multiple locales, pluralization rules, and message formatting with parameters.
 * 
 * <p>Example usage:
 * <pre>{@code
 * InternationalizationService i18n = Fault.service(InternationalizationService.class);
 * if (i18n != null) {
 *     NamespacedId messageKey = new NamespacedId("myplugin", "welcome_message");
 *     
 *     // Register translations
 *     i18n.setTranslation(messageKey, Locale.ENGLISH, "Welcome to the server, {player}!");
 *     i18n.setTranslation(messageKey, Locale.FRENCH, "Bienvenue sur le serveur, {player} !");
 *     i18n.setTranslation(messageKey, new Locale("es"), "¡Bienvenido al servidor, {player}!");
 *     
 *     // Get localized message
 *     String message = i18n.translate(messageKey, Locale.FRENCH, Map.of("player", "Steve"));
 *     // Returns: "Bienvenue sur le serveur, Steve !"
 *     
 *     // Handle pluralization
 *     NamespacedId playersOnline = new NamespacedId("myplugin", "players_online");
 *     i18n.setTranslation(playersOnline, Locale.ENGLISH, "{count} player online|{count} players online");
 *     
 *     String pluralized = i18n.translatePlural(playersOnline, Locale.ENGLISH, 5, Map.of("count", 5));
 *     // Returns: "5 players online"
 * }
 * }</pre>
 * 
 * <p><strong>Threading:</strong> All operations are thread-safe. Translation lookups
 * are optimized for high-frequency access with caching.
 * 
 * @since 0.0.1
 * @apiNote Supports ICU MessageFormat syntax for advanced formatting features
 */
public interface InternationalizationService {

    /**
     * Sets a translation for a message key and locale.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during storage.
     * 
     * @param key the message key
     * @param locale the target locale
     * @param translation the translated text
     * @return future that completes when translation is stored
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> setTranslation(@NotNull NamespacedId key, @NotNull Locale locale, @NotNull String translation);

    /**
     * Gets a translation for a message key and locale.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking (cached).
     * 
     * @param key the message key
     * @param locale the target locale
     * @return future containing the translation, or null if not found
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<String> getTranslation(@NotNull NamespacedId key, @NotNull Locale locale);

    /**
     * Translates a message with parameter substitution.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the message key
     * @param locale the target locale
     * @param parameters parameters for message formatting
     * @return the formatted translated message
     * @since 0.0.1
     */
    @NotNull
    String translate(@NotNull NamespacedId key, @NotNull Locale locale, @NotNull Map<String, Object> parameters);

    /**
     * Translates a message without parameters.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the message key
     * @param locale the target locale
     * @return the translated message
     * @since 0.0.1
     */
    @NotNull
    String translate(@NotNull NamespacedId key, @NotNull Locale locale);

    /**
     * Translates a message with plural form selection.
     * 
     * <p>Translation should contain plural forms separated by '|':
     * "one item|many items" for simple plurals, or full ICU syntax for complex rules.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the message key
     * @param locale the target locale
     * @param count the count for plural selection
     * @param parameters parameters for message formatting
     * @return the formatted translated message with correct plural form
     * @since 0.0.1
     */
    @NotNull
    String translatePlural(@NotNull NamespacedId key, @NotNull Locale locale, long count, @NotNull Map<String, Object> parameters);

    /**
     * Translates a message with fallback to default locale.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the message key
     * @param locale the preferred locale
     * @param fallback the fallback locale
     * @param parameters parameters for message formatting
     * @return the formatted translated message
     * @since 0.0.1
     */
    @NotNull
    String translateWithFallback(@NotNull NamespacedId key, @NotNull Locale locale, @NotNull Locale fallback, @NotNull Map<String, Object> parameters);

    /**
     * Removes a translation for a specific locale.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the message key
     * @param locale the target locale
     * @return future containing true if translation was removed
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Boolean> removeTranslation(@NotNull NamespacedId key, @NotNull Locale locale);

    /**
     * Removes all translations for a message key.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during removal.
     * 
     * @param key the message key
     * @return future containing the number of translations removed
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Integer> removeAllTranslations(@NotNull NamespacedId key);

    /**
     * Gets all supported locales for a message key.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the message key
     * @return future containing set of supported locales
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Set<Locale>> getSupportedLocales(@NotNull NamespacedId key);

    /**
     * Gets all message keys for a specific namespace.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param namespace the namespace to filter by
     * @return future containing set of message keys
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Set<NamespacedId>> getMessageKeys(@NotNull String namespace);

    /**
     * Gets all message keys that have translations.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return future containing set of all message keys
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Set<NamespacedId>> getAllMessageKeys();

    /**
     * Gets all translations for a namespace and locale.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param namespace the namespace to filter by
     * @param locale the target locale
     * @return future containing map of message keys to translations
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Map<NamespacedId, String>> getNamespaceTranslations(@NotNull String namespace, @NotNull Locale locale);

    /**
     * Bulk sets multiple translations for a locale.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during storage.
     * 
     * @param locale the target locale
     * @param translations map of message keys to translations
     * @return future that completes when all translations are stored
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> setTranslations(@NotNull Locale locale, @NotNull Map<NamespacedId, String> translations);

    /**
     * Loads translations from a resource file.
     * 
     * <p><strong>Threading:</strong> Thread-safe, executes loading on background thread.
     * 
     * @param namespace the namespace for loaded translations
     * @param locale the target locale
     * @param resourcePath path to the translation resource
     * @param format the file format (properties, json, yaml)
     * @return future containing the number of translations loaded
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Integer> loadTranslations(@NotNull String namespace, @NotNull Locale locale, @NotNull String resourcePath, @NotNull TranslationFormat format);

    /**
     * Exports translations to a resource file.
     * 
     * <p><strong>Threading:</strong> Thread-safe, executes export on background thread.
     * 
     * @param namespace the namespace to export
     * @param locale the target locale
     * @param outputPath path for the output file
     * @param format the file format (properties, json, yaml)
     * @return future containing the number of translations exported
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Integer> exportTranslations(@NotNull String namespace, @NotNull Locale locale, @NotNull String outputPath, @NotNull TranslationFormat format);

    /**
     * Sets the default locale for fallback translations.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param locale the default locale
     * @return future that completes when default is set
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> setDefaultLocale(@NotNull Locale locale);

    /**
     * Gets the current default locale.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @return future containing the default locale
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Locale> getDefaultLocale();

    /**
     * Finds the best matching locale from available translations.
     * 
     * <p><strong>Threading:</strong> Thread-safe, non-blocking.
     * 
     * @param key the message key
     * @param preferredLocales list of preferred locales in order
     * @return future containing the best matching locale, or null if no matches
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Locale> findBestLocale(@NotNull NamespacedId key, @NotNull List<Locale> preferredLocales);

    /**
     * Validates that all required translations exist for a locale.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during validation.
     * 
     * @param namespace the namespace to validate
     * @param locale the target locale
     * @param requiredKeys set of required message keys
     * @return future containing validation result
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<TranslationValidation> validateTranslations(@NotNull String namespace, @NotNull Locale locale, @NotNull Set<NamespacedId> requiredKeys);

    /**
     * Clears all cached translations and reloads from storage.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during reload.
     * 
     * @return future that completes when cache is cleared
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> clearCache();

    /**
     * Gets translation statistics for analysis.
     * 
     * <p><strong>Threading:</strong> Thread-safe, may block briefly during calculation.
     * 
     * @return future containing translation statistics
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<TranslationStatistics> getStatistics();

    /**
     * Supported translation file formats.
     * 
     * @since 0.0.1
     */
    enum TranslationFormat {
        /**
         * Java properties format (key=value).
         */
        PROPERTIES,

        /**
         * JSON format with nested objects.
         */
        JSON,

        /**
         * YAML format with hierarchical structure.
         */
        YAML
    }

    /**
     * Result of translation validation.
     * 
     * @param namespace the validated namespace
     * @param locale the validated locale
     * @param totalRequired total number of required translations
     * @param found number of translations found
     * @param missing set of missing message keys
     * @param valid whether all required translations are present
     * 
     * @since 0.0.1
     */
    record TranslationValidation(
        @NotNull String namespace,
        @NotNull Locale locale,
        int totalRequired,
        int found,
        @NotNull Set<NamespacedId> missing,
        boolean valid
    ) {

        /**
         * Gets the completion percentage.
         * 
         * @return completion percentage (0.0 to 1.0)
         */
        public double getCompletionPercentage() {
            return totalRequired > 0 ? (double) found / totalRequired : 1.0;
        }

        /**
         * Gets the number of missing translations.
         * 
         * @return count of missing translations
         */
        public int getMissingCount() {
            return missing.size();
        }

        /**
         * Checks if validation passed completely.
         * 
         * @return true if all required translations are present
         */
        public boolean isComplete() {
            return valid;
        }
    }

    /**
     * Statistics about translation coverage and usage.
     * 
     * @param totalMessages total number of message keys
     * @param totalTranslations total number of translations across all locales
     * @param supportedLocales number of supported locales
     * @param namespacesWithTranslations number of namespaces with translations
     * @param averageTranslationsPerLocale average translations per locale
     * @param mostTranslatedLocale locale with the most translations
     * 
     * @since 0.0.1
     */
    record TranslationStatistics(
        int totalMessages,
        int totalTranslations,
        int supportedLocales,
        int namespacesWithTranslations,
        double averageTranslationsPerLocale,
        @Nullable Locale mostTranslatedLocale
    ) {

        /**
         * Gets the overall translation coverage.
         * 
         * @return coverage percentage (0.0 to 1.0)
         */
        public double getCoveragePercentage() {
            if (totalMessages == 0 || supportedLocales == 0) {
                return 0.0;
            }
            int maxPossibleTranslations = totalMessages * supportedLocales;
            return (double) totalTranslations / maxPossibleTranslations;
        }

        /**
         * Checks if translation coverage is considered complete.
         * 
         * @return true if coverage is above 95%
         */
        public boolean isComplete() {
            return getCoveragePercentage() >= 0.95;
        }
    }
}