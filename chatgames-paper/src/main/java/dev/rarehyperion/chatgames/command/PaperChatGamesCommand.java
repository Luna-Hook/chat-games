package dev.rarehyperion.chatgames.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.rarehyperion.chatgames.ChatGamesCore;
import dev.rarehyperion.chatgames.game.GameConfig;
import dev.rarehyperion.chatgames.platform.PlatformSender;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.concurrent.CompletableFuture;

public class PaperChatGamesCommand extends ChatGamesCommand {
  public PaperChatGamesCommand(ChatGamesCore plugin) {
    super(plugin);
  }

  public LiteralCommandNode<CommandSourceStack> build() {
    LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("chatgames")
      .executes(ctx -> {
        PlatformSender sender = this.plugin.platform().wrapSender(ctx.getSource().getSender());
        this.handleCommand(sender, new String[0]);
        return 1;
      });

    String[][] subCommands = {
      { "reload", "chatgames.reload" },
      { "start", "chatgames.start" },
      { "stop", "chatgames.stop" },
      { "list", "chatgames.list" },
      { "toggle", "chatgames.toggle" },
      { "help", "chatgames.help" },
      { "answer", null },
      { "info", null },
    };

    for (String[] cmd : subCommands) {
      String name = cmd[0];
      String permission = cmd[1];
      LiteralArgumentBuilder<CommandSourceStack> node = Commands.literal(name);
      if (permission != null)
        node.requires(ctx -> ctx.getSender().hasPermission(permission));

      if ("start".equals(name)) {
        node.then(this.createArgumentNode(name, "game", StringArgumentType.greedyString()));
      } else if ("answer".equals(name)) {
        node.then(this.createArgumentNode(name, "token", StringArgumentType.string()));
      } else {
        node.executes(ctx -> {
          PlatformSender sender = this.plugin.platform().wrapSender(ctx.getSource().getSender());
          this.handleCommand(sender, new String[]{ name });
          return 1;
        });
      }
      root.then(node);
    }

    return root.build();
  }

  private ArgumentBuilder<CommandSourceStack, ?> createArgumentNode(String command, String argName, ArgumentType<?> argType) {
    return Commands.argument(argName, argType)
      .suggests("start".equals(command) ? this::suggestGameNames : (ctx, builder) -> builder.buildFuture())
      .executes(ctx -> {
        PlatformSender sender = this.plugin.platform().wrapSender(ctx.getSource().getSender());
        String arg = StringArgumentType.getString(ctx, argName);
        this.handleCommand(sender, new String[]{ command, arg });
        return 1;
      });
  }

  private CompletableFuture<Suggestions> suggestGameNames(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
    for (GameConfig config : this.plugin.gameRegistry().getAllConfigs())
      builder.suggest(config.getName());
    return builder.buildFuture();
  }
}
