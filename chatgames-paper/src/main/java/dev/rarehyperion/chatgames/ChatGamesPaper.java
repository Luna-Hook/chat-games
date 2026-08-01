package dev.rarehyperion.chatgames;

import dev.rarehyperion.chatgames.platform.impl.PaperPlatform;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChatGamesPaper extends JavaPlugin {
  private final ChatGamesCore core = new ChatGamesCore(new PaperPlatform(this));

  public void onLoad() {
    this.core.load();
  }

  public void onEnable() {
    this.core.enable();
  }

  public void onDisable() {
    this.core.disable();
  }
}
