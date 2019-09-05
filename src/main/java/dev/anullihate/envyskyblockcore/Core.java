package dev.anullihate.envyskyblockcore;

import cn.nukkit.plugin.PluginBase;
import dev.anullihate.envyskyblockcore.listeners.PlayerEventListener;

public class Core extends PluginBase {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
    }
}
