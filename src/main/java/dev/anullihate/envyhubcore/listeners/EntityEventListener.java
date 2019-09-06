package dev.anullihate.envyhubcore.listeners;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntitySpawnEvent;
import cn.nukkit.event.entity.EntityVehicleEnterEvent;
import cn.nukkit.nbt.tag.StringTag;
import dev.anullihate.envyhubcore.Core;
import dev.anullihate.envyhubcore.entities.NpcEntity;
import dev.anullihate.envyhubcore.entities.NpcHuman;

import java.util.List;

public class EntityEventListener implements Listener {

    private Core core;

    public EntityEventListener(Core core) {
        this.core = core;
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof NpcHuman || entity instanceof NpcEntity || entity.namedTag.getBoolean("npc")) {
            if (!"%k".equals(entity.namedTag.getString("NameTag"))) {
                entity.setNameTag(entity.namedTag.getString("NameTag"));
                entity.setNameTagVisible(true);
                entity.setNameTagAlwaysVisible(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof NpcHuman || entity instanceof NpcEntity || entity.namedTag.getBoolean("npc")) {
            event.setCancelled();

            if (event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                String name = player.getName();

                if (Core.id.contains(name)) {
                    player.sendMessage("\u00A7aThe ID from that entity is " + entity.getId());
                    Core.id.remove(name);

                } else if (Core.kill.contains(name)) {
                    entity.close();
                    player.sendMessage("\u00A7aEntity removed");
                    Core.kill.remove(name);

                } else {
                    List<StringTag> cmddd = entity.namedTag.getList("Commands", StringTag.class).getAll();
                    for (StringTag cmdd : cmddd) {
                        String cmd = cmdd.data;
                        cmd = cmd.replaceAll("%p", player.getName());
                        Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), cmd);
                    }

                    List<StringTag> cmdddd = entity.namedTag.getList("PlayerCommands", StringTag.class).getAll();
                    for (StringTag cmdd : cmdddd) {
                        String cmd = cmdd.data;
                        Server.getInstance().dispatchCommand(player, cmd);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityVehicleEnter(EntityVehicleEnterEvent e) {
        if (e.getEntity() instanceof NpcHuman || e.getEntity() instanceof NpcEntity) {
            e.setCancelled(true);
        }
    }
}
