package com.upfault.fault.api;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Declarative command tree model for mapping to Brigadier or other command systems.
 * 
 * <p>This interface defines the structure for building command trees that can be
 * mapped to various command frameworks including Paper's Brigadier integration.
 * 
 * <p>Example usage:
 * <pre>{@code
 * CommandModel commands = Fault.service(CommandModel.class);
 * 
 * CommandNode rootNode = commands.createNode("mycommand")
 *     .permission("myplugin.command.use")
 *     .description("My plugin's main command")
 *     .child(
 *         commands.createNode("reload")
 *             .permission("myplugin.command.reload")
 *             .executor((sender, args) -> {
 *                 // Handle reload command
 *                 return CompletableFuture.completedFuture(true);
 *             })
 *     )
 *     .build();
 * 
 * commands.registerCommand(rootNode);
 * }</pre>
 * 
 * <p><strong>Threading:</strong> Command registration is typically done during
 * plugin initialization. Command execution may occur on the main thread or
 * asynchronously depending on the implementation.
 * 
 * @since 0.0.1
 * @apiNote Implementations should handle async command execution gracefully
 */
public interface CommandModel {

    /**
     * Creates a new command node builder.
     * 
     * @param name the command name or argument name
     * @return a new node builder
     * @since 0.0.1
     */
    @NotNull
    CommandNodeBuilder createNode(@NotNull String name);

    /**
     * Registers a command tree with the server.
     * 
     * @param rootNode the root command node
     * @return future that completes when registration is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> registerCommand(@NotNull CommandNode rootNode);

    /**
     * Unregisters a command from the server.
     * 
     * @param commandName the name of the command to unregister
     * @return future that completes when unregistration is done
     * @since 0.0.1
     */
    @NotNull
    CompletableFuture<Void> unregisterCommand(@NotNull String commandName);

    /**
     * Builder for creating command nodes.
     * 
     * @since 0.0.1
     */
    interface CommandNodeBuilder {

        /**
         * Sets the permission required to execute this command.
         * 
         * @param permission the permission string
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        CommandNodeBuilder permission(@Nullable String permission);

        /**
         * Sets the command description.
         * 
         * @param description the command description
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        CommandNodeBuilder description(@Nullable String description);

        /**
         * Sets the command executor.
         * 
         * @param executor the command executor
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        CommandNodeBuilder executor(@NotNull CommandExecutor executor);

        /**
         * Sets the tab completion provider.
         * 
         * @param completer the tab completer
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        CommandNodeBuilder completer(@NotNull TabCompleter completer);

        /**
         * Adds a child command node.
         * 
         * @param child the child node
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        CommandNodeBuilder child(@NotNull CommandNode child);

        /**
         * Adds a child command node using a builder.
         * 
         * @param childBuilder the child node builder
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        CommandNodeBuilder child(@NotNull CommandNodeBuilder childBuilder);

        /**
         * Sets this node as an argument with a specific type.
         * 
         * @param argumentType the type of argument (e.g., "string", "integer", "player")
         * @return this builder
         * @since 0.0.1
         */
        @NotNull
        CommandNodeBuilder argument(@NotNull String argumentType);

        /**
         * Builds the command node.
         * 
         * @return the built command node
         * @since 0.0.1
         */
        @NotNull
        CommandNode build();
    }

    /**
     * Represents a node in the command tree.
     * 
     * @since 0.0.1
     */
    interface CommandNode {

        /**
         * Gets the name of this command node.
         * 
         * @return the node name
         * @since 0.0.1
         */
        @NotNull
        String getName();

        /**
         * Gets the permission required for this command.
         * 
         * @return the permission string, or null if no permission required
         * @since 0.0.1
         */
        @Nullable
        String getPermission();

        /**
         * Gets the description of this command.
         * 
         * @return the description, or null if none set
         * @since 0.0.1
         */
        @Nullable
        String getDescription();

        /**
         * Gets the executor for this command.
         * 
         * @return the executor, or null if this is not an executable node
         * @since 0.0.1
         */
        @Nullable
        CommandExecutor getExecutor();

        /**
         * Gets the tab completer for this command.
         * 
         * @return the tab completer, or null if none set
         * @since 0.0.1
         */
        @Nullable
        TabCompleter getCompleter();

        /**
         * Gets the child nodes of this command.
         * 
         * @return list of child nodes
         * @since 0.0.1
         */
        @NotNull
        List<CommandNode> getChildren();

        /**
         * Checks if this is an argument node.
         * 
         * @return true if this node represents an argument
         * @since 0.0.1
         */
        boolean isArgument();

        /**
         * Gets the argument type if this is an argument node.
         * 
         * @return the argument type, or null if not an argument node
         * @since 0.0.1
         */
        @Nullable
        String getArgumentType();
    }

    /**
     * Functional interface for command execution.
     * 
     * @since 0.0.1
     */
    @FunctionalInterface
    interface CommandExecutor {

        /**
         * Executes the command.
         * 
         * @param sender the command sender
         * @param args the command arguments
         * @return future containing true if successful, false otherwise
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<Boolean> execute(@NotNull CommandSender sender, @NotNull String[] args);
    }

    /**
     * Functional interface for tab completion.
     * 
     * @since 0.0.1
     */
    @FunctionalInterface
    interface TabCompleter {

        /**
         * Provides tab completion suggestions.
         * 
         * @param sender the command sender
         * @param args the current command arguments
         * @return future containing list of suggestions
         * @since 0.0.1
         */
        @NotNull
        CompletableFuture<List<String>> complete(@NotNull CommandSender sender, @NotNull String[] args);
    }
}