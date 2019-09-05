package dev.anullihate.envyskyblockcore.listeners;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import dev.anullihate.envyskyblockcore.Core;

public class PlayerEventListener implements Listener {

    private Core core;

    public PlayerEventListener(Core core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setMovementSpeed(0.1f);
    }
}
