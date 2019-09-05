package dev.anullihate.envyhubcore;

import cn.nukkit.plugin.PluginBase;
import dev.anullihate.envyhubcore.listeners.PlayerEventListener;

public class Core extends PluginBase {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
    }
}
