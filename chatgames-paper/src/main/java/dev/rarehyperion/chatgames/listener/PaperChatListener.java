package dev.rarehyperion.chatgames.listener;

import dev.rarehyperion.chatgames.game.GameManager;
import dev.rarehyperion.chatgames.platform.impl.PaperPlatformPlayer;
import dev.rarehyperion.chatgames.util.MessageUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PaperChatListener implements Listener {
  private final GameManager gameManager;

  public PaperChatListener(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerChat(AsyncChatEvent event) {
    if (this.gameManager.getActiveGame() != null) {
      String message = MessageUtil.plainText(event.message());
      if (this.gameManager.processAnswer(new PaperPlatformPlayer(event.getPlayer()), message))
        event.setCancelled(true);
    }
  }
}
