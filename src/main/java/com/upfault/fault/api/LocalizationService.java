package com.upfault.fault.api;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

/**
 * Service for runtime internationalization with plurals and gender support.
 * 
 * <p>This service provides comprehensive i18n functionality for translating
 * text content based on player locale preferences. It supports complex
 * formatting including pluralization rules, gender agreements, and
 * parameter substitution with type-aware formatting.
 * 
 * <p>The service integrates with Adventure Components to provide rich text
 * formatting while maintaining translation capabilities across different
 * languages and locales.
 * 
 * @since 0.0.1
 * @apiNote All methods are thread-safe and may be called from any thread
 */
public interface LocalizationService {
    
    /**
     * Translates a localization key to a Component for the specified locale.
     * 
     * <p>The args map can contain various types of objects which will be
     * formatted appropriately based on the target locale's conventions
     * for numbers, dates, currency, etc.
     * 
     * @param key the localization key to translate
     * @param locale the target locale for translation
     * @param args named parameters for the translation template
     * @return the translated and formatted component
     * @throws IllegalArgumentException if key or locale is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Missing keys return the key itself as fallback text.
     */
    @NotNull Component translate(@NotNull String key, @NotNull Locale locale, @NotNull Map<String, Object> args);
    
    /**
     * Translates a key with no parameters.
     * 
     * @param key the localization key to translate
     * @param locale the target locale for translation
     * @return the translated component
     * @throws IllegalArgumentException if key or locale is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull Component translate(@NotNull String key, @NotNull Locale locale);
    
    /**
     * Checks if a translation exists for a key in the specified locale.
     * 
     * <p>This can be used to determine if a fallback strategy should be
     * employed or if the key should be displayed as-is.
     * 
     * @param key the localization key to check
     * @param locale the locale to check in
     * @return true if a translation exists for the key and locale
     * @throws IllegalArgumentException if key or locale is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    boolean has(@NotNull String key, @NotNull Locale locale);
    
    /**
     * Translates a key with plural handling.
     * 
     * <p>The count parameter determines which plural form to use based on
     * the target locale's pluralization rules. The count is also available
     * as a parameter in the translation template.
     * 
     * @param key the localization key to translate
     * @param locale the target locale for translation
     * @param count the count for plural determination
     * @param args additional named parameters for the translation template
     * @return the translated component with appropriate plural form
     * @throws IllegalArgumentException if key or locale is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread.
     *          Plural rules are locale-specific and handled automatically.
     */
    @NotNull Component translatePlural(@NotNull String key, @NotNull Locale locale, int count, @NotNull Map<String, Object> args);
    
    /**
     * Translates a key with gender agreement support.
     * 
     * <p>The gender parameter affects grammatical agreement in languages
     * that have gendered nouns, adjectives, or verb forms.
     * 
     * @param key the localization key to translate
     * @param locale the target locale for translation
     * @param gender the gender context for grammatical agreement
     * @param args named parameters for the translation template
     * @return the translated component with appropriate gender agreement
     * @throws IllegalArgumentException if key, locale, or gender is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull Component translateGendered(@NotNull String key, @NotNull Locale locale, @NotNull Gender gender, @NotNull Map<String, Object> args);
    
    /**
     * Gets all available locales supported by the system.
     * 
     * @return set of supported locales
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull java.util.Set<Locale> getSupportedLocales();
    
    /**
     * Gets the default fallback locale used when translations are missing.
     * 
     * @return the default locale
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull Locale getDefaultLocale();
    
    /**
     * Loads translations from a resource bundle or file.
     * 
     * <p>This allows dynamic loading of translation resources at runtime,
     * useful for plugins that want to add their own localizations.
     * 
     * @param bundleName the name of the resource bundle to load
     * @param locale the locale to load translations for
     * @return future containing the operation result
     * @throws IllegalArgumentException if any parameter is null
     * 
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull java.util.concurrent.CompletableFuture<com.upfault.fault.api.types.OperationResult> loadTranslations(@NotNull String bundleName, @NotNull Locale locale);
    
    /**
     * Reloads all translation data from sources.
     * 
     * <p>This is useful for development or when translation files are
     * updated and need to be reloaded without server restart.
     * 
     * @return future containing the operation result
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull java.util.concurrent.CompletableFuture<com.upfault.fault.api.types.OperationResult> reloadTranslations();
    
    /**
     * Gets statistics about loaded translations.
     * 
     * @return current localization statistics
     * @apiNote This method is thread-safe and may be called from any thread
     */
    @NotNull LocalizationStats getStats();
    
    /**
     * Gender contexts for grammatical agreement.
     */
    enum Gender {
        /** Masculine gender */
        MASCULINE,
        /** Feminine gender */
        FEMININE,
        /** Neuter gender */
        NEUTER,
        /** Unknown or non-applicable gender */
        UNKNOWN
    }
    
    /**
     * Statistics about the localization system.
     * 
     * @param supportedLocales number of supported locales
     * @param totalKeys total number of translation keys across all locales
     * @param averageKeysPerLocale average number of keys per locale
     * @param missingTranslations number of missing translations (fallbacks used)
     */
    record LocalizationStats(
        int supportedLocales,
        int totalKeys,
        double averageKeysPerLocale,
        int missingTranslations
    ) {
        public LocalizationStats {
            if (supportedLocales < 0) throw new IllegalArgumentException("Supported locales cannot be negative");
            if (totalKeys < 0) throw new IllegalArgumentException("Total keys cannot be negative");
            if (averageKeysPerLocale < 0) throw new IllegalArgumentException("Average keys cannot be negative");
            if (missingTranslations < 0) throw new IllegalArgumentException("Missing translations cannot be negative");
        }
        
        /**
         * Gets the translation completeness as a percentage.
         * 
         * @return completeness from 0.0 to 1.0
         */
        public double getCompleteness() {
            if (totalKeys == 0) return 1.0;
            return 1.0 - ((double) missingTranslations / totalKeys);
        }
    }
}