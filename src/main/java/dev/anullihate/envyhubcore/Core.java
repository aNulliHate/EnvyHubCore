package dev.anullihate.envyhubcore;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import dev.anullihate.envyhubcore.entities.NpcEntity;
import dev.anullihate.envyhubcore.entities.NpcHuman;
import dev.anullihate.envyhubcore.listeners.PlayerEventListener;
import org.itxtech.synapseapi.SynapseAPI;
import org.itxtech.synapseapi.SynapseEntry;
import org.itxtech.synapseapi.utils.ClientData;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Core extends PluginBase {

    public static final List<String> entities = Arrays.asList("Human");
    public static List<String> id = new ArrayList<>();
    public static List<String> kill = new ArrayList<>();

    private SynapseAPI synapseAPI = SynapseAPI.getInstance();
    private Map entries = synapseAPI.getSynapseEntries();

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
        Entity.registerEntity(NpcHuman.class.getSimpleName(), NpcHuman.class);
    }

    public CompoundTag nbt(Player sender, String[] args, String name) {
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<>("Pos")
                        .add(new DoubleTag("", sender.x))
                        .add(new DoubleTag("", sender.y))
                        .add(new DoubleTag("", sender.z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) sender.getYaw()))
                        .add(new FloatTag("", (float) sender.getPitch())))
                .putBoolean("Invulnerable", true)
                .putString("NameTag", name)
                .putList(new ListTag<StringTag>("Commands"))
                .putList(new ListTag<StringTag>("PlayerCommands"))
                .putBoolean("npc", true)
                .putFloat("scale", 1);
        if ("Human".equals(args[1])) {
            nbt.putCompound("Skin", new CompoundTag()
                    .putString("ModelId", sender.getSkin().getGeometryName())
                    .putByteArray("Data", sender.getSkin().getSkinData())
                    .putString("ModelId", sender.getSkin().getSkinId())
                    .putByteArray("CapeData", sender.getSkin().getCapeData())
                    .putString("GeometryName", sender.getSkin().getGeometryName())
                    .putByteArray("GeometryData", sender.getSkin().getGeometryData().getBytes(StandardCharsets.UTF_8))
            );
            nbt.putBoolean("ishuman", true);
            nbt.putString("Item", sender.getInventory().getItemInHand().getName());
            nbt.putString("Helmet", sender.getInventory().getHelmet().getName());
            nbt.putString("Chestplate", sender.getInventory().getChestplate().getName());
            nbt.putString("Leggings", sender.getInventory().getLeggings().getName());
            nbt.putString("Boots", sender.getInventory().getBoots().getName());
        }
        return nbt;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == this.getServer().getConsoleSender()) {
            sender.sendMessage("\u00A7cThis command only works in game");
            return true;
        }
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("enpc")) {
            if (args.length < 1) {
                sender.sendMessage("\u00A7l\u00A7a--- NPC HELP ---");
                sender.sendMessage("\u00A73Spawn NPC: \u00A7e/enpc spawn <entity> <name>");
                sender.sendMessage("\u00A73Add console command: \u00A7e/enpc addcmd <ID> <cmd>");
                sender.sendMessage("\u00A73Add player command: \u00A7e/enpc addplayercmd <ID> <cmd>");
                sender.sendMessage("\u00A73Delete console command: \u00A7e/enpc delcmd <ID> <cmd>");
                sender.sendMessage("\u00A73Delete player command: \u00A7e/enpc delplayercmd <ID> <cmd>");
                sender.sendMessage("\u00A73Delete all commands: \u00A7e/enpc delallcmd <ID>");
                sender.sendMessage("\u00A73See all commands: \u00A7e/enpc listcmd <ID>");
                sender.sendMessage("\u00A73Edit NPC: \u00A7e/enpc edit <ID> <item|armor|scale|name|tphere>");
                sender.sendMessage("\u00A73Get NPCs id: \u00A7e/enpc getid");
                sender.sendMessage("\u00A73Get list of all available entities: \u00A7e/enpc entities");
                sender.sendMessage("\u00A73Remove NPC: \u00A7e/enpc remove");
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "spawn":
                    if (args.length < 2) {
                        sender.sendMessage("\u00A7cUsage: /npc spawn <entity> <name>");
                        return true;
                    }
                    if (!entities.contains(args[1])) {
                        sender.sendMessage("\u00A7cEntity \u00A74" + args[1] + "\u00A7c is not supported, with the command \u00A7e/npc list\u00A7c you see all supported entities");
                        return true;
                    }
                    String name;
                    if (args.length < 2) {
                        name = "%k";
                    } else {
                        name = String.join(" ", args);
                        name = name.replaceFirst("spawn", "");
                        name = name.replaceFirst(args[1], "");
                        name = name.replaceFirst(" ", "");
                        name = name.replaceFirst(" ", "");
                    }
                    name = name.replaceAll("%n", "\n");
                    CompoundTag nbt = this.nbt(player, args, name);
                    Entity ent = Entity.createEntity("NPC_" + args[1], player.chunk, nbt);
                    ent.setNameTag(name);
                    if (!"%k".equals(name)) {
                        ent.setNameTagAlwaysVisible();
                    }
                    ent.spawnToAll();
                    sender.sendMessage("\u00A7aNPC spawned with the ID: " + ent.getId() + " and the name: " + ent.getName());
                    return true;
                case "getid":
                case "id":
                    id.add(player.getName());
                    player.sendMessage("\u00A7aID MODE - click an entity to get it's ID");
                    return true;
                case "addcmd":
                    if (args.length < 3) {
                        sender.sendMessage("\u00A7cUsage: /npc addcmd <ID> <cmd>");
                        return true;
                    }
                    if (!isInteger(args[1])) {
                        player.sendMessage("\u00A7cUsage: /npc addcmd <ID> <cmd>");
                        return true;
                    }
                    Entity enti = player.getLevel().getEntity(Integer.parseInt(args[1]));
                    if (enti instanceof NpcHuman || enti instanceof NpcEntity || enti.namedTag.getBoolean("npc")) {
                        String cmd;
                        cmd = String.join(" ", args);
                        cmd = cmd.replaceFirst("addcmd", "");
                        cmd = cmd.replaceFirst(args[1], "");
                        StringTag st = new StringTag(cmd, cmd);
                        if (enti.namedTag.getList("Commands", StringTag.class).getAll().contains(st)) {
                            player.sendMessage("\u00A7aCommand already added");
                            return true;
                        }
                        enti.namedTag.getList("Commands", StringTag.class).add(st);
                        player.sendMessage("\u00A7aCommand added");
                        return true;
                    } else {
                        player.sendMessage("\u00A7cNo NPC found with that ID");
                        return true;
                    }
                case "addplayercmd":
                    if (args.length < 3) {
                        sender.sendMessage("\u00A7cUsage: /npc addplayercmd <ID> <cmd>");
                        return true;
                    }
                    if (!isInteger(args[1])) {
                        player.sendMessage("\u00A7cUsage: /npc addplayercmd <ID> <cmd>");
                        return true;
                    }
                    Entity enti2 = player.getLevel().getEntity(Integer.parseInt(args[1]));
                    if (enti2 instanceof NpcHuman || enti2 instanceof NpcEntity || enti2.namedTag.getBoolean("npc")) {
                        String cmd;
                        cmd = String.join(" ", args);
                        cmd = cmd.replaceFirst("addplayercmd", "");
                        cmd = cmd.replaceFirst(args[1], "");
                        StringTag st = new StringTag(cmd, cmd);
                        if (enti2.namedTag.getList("PlayerCommands", StringTag.class).getAll().contains(st)) {
                            player.sendMessage("\u00A7aCommand already added");
                            return true;
                        }
                        enti2.namedTag.getList("PlayerCommands", StringTag.class).add(st);
                        player.sendMessage("\u00A7aCommand added");
                        return true;
                    } else {
                        player.sendMessage("\u00A7cNo NPC found with that ID");
                        return true;
                    }
                case "listcmd":
                    if (args.length < 2) {
                        sender.sendMessage("\u00A7cUsage: /npc listcmd <ID>");
                        return true;
                    }
                    if (!isInteger(args[1])) {
                        sender.sendMessage("\u00A7cUsage: /npc listcmdd <ID>");
                        return true;
                    }
                    Entity entity = player.getLevel().getEntity(Integer.parseInt(args[1]));
                    if (entity instanceof NpcHuman || entity instanceof NpcEntity || entity.namedTag.getBoolean("npc")) {
                        List<StringTag> cmddd = entity.namedTag.getList("Commands", StringTag.class).getAll();
                        player.sendMessage("\u00A7aCommands of \u00A7e" + entity.getName() + " (" + entity.getId() + ")\u00A7a:");
                        for (StringTag cmdd : cmddd) {
                            player.sendMessage(cmdd.data);
                        }
                        List<StringTag> cmdddd = entity.namedTag.getList("PlayerCommands", StringTag.class).getAll();
                        player.sendMessage("\u00A7aPlayer commands of \u00A7e" + entity.getName() + " (" + entity.getId() + ")\u00A7a:");
                        for (StringTag cmdd : cmdddd) {
                            player.sendMessage(cmdd.data);
                        }
                        return true;
                    } else {
                        player.sendMessage("\u00A7cNo NPC found with that ID");
                        return true;
                    }
                case "delcmd":
                    if (args.length < 3) {
                        sender.sendMessage("\u00A7cUsage: /npc delcmd <ID> <cmd>");
                        return true;
                    }
                    if (!isInteger(args[1])) {
                        player.sendMessage("\u00A7cUsage: /npc delcmd <ID> <cmd>");
                        return true;
                    }
                    Entity en = player.getLevel().getEntity(Integer.parseInt(args[1]));
                    if (en instanceof NpcHuman || en instanceof NpcEntity || en.namedTag.getBoolean("npc")) {
                        String cmd;
                        cmd = String.join(" ", args);
                        cmd = cmd.replaceFirst("delcmd", "");
                        cmd = cmd.replaceFirst(args[1], "");
                        StringTag st = new StringTag(cmd, cmd);
                        if (en.namedTag.getList("Commands", StringTag.class).getAll().contains(st)) {
                            en.namedTag.getList("Commands", StringTag.class).remove(st);
                            player.sendMessage("\u00A7aCommand \u00A7e" + cmd + "\u00A7a removed");
                            return true;
                        } else {
                            player.sendMessage("\u00A7cCommand \u00A7e" + cmd + "\u00A7c not found");
                            return true;
                        }
                    } else {
                        player.sendMessage("\u00A7cNo NPC found with that ID");
                        return true;
                    }
                case "delplayercmd":
                    if (args.length < 3) {
                        sender.sendMessage("\u00A7cUsage: /npc delplayercmd <ID> <cmd>");
                        return true;
                    }
                    if (!isInteger(args[1])) {
                        player.sendMessage("\u00A7cUsage: /npc delplayercmd <ID> <cmd>");
                        return true;
                    }
                    Entity en2 = player.getLevel().getEntity(Integer.parseInt(args[1]));
                    if (en2 instanceof NpcHuman || en2 instanceof NpcEntity || en2.namedTag.getBoolean("npc")) {
                        String cmd;
                        cmd = String.join(" ", args);
                        cmd = cmd.replaceFirst("delplayercmd", "");
                        cmd = cmd.replaceFirst(args[1], "");
                        StringTag st = new StringTag(cmd, cmd);
                        if (en2.namedTag.getList("PlayerCommands", StringTag.class).getAll().contains(st)) {
                            en2.namedTag.getList("PlayerCommands", StringTag.class).remove(st);
                            player.sendMessage("\u00A7aCommand \u00A7e" + cmd + "\u00A7a removed");
                            return true;
                        } else {
                            player.sendMessage("\u00A7cCommand \u00A7e" + cmd + "\u00A7c not found");
                            return true;
                        }
                    } else {
                        player.sendMessage("\u00A7cNo NPC found with that ID");
                        return true;
                    }
                case "delallcmd":
                    if (args.length < 2) {
                        sender.sendMessage("\u00A7cUsage: /npc delallcmd <ID>");
                        return true;
                    }
                    if (!isInteger(args[1])) {
                        player.sendMessage("\u00A7cUsage: /npc delallcmd <ID>");
                        return true;
                    }
                    Entity en3 = player.getLevel().getEntity(Integer.parseInt(args[1]));
                    if (en3 instanceof NpcHuman || en3 instanceof NpcEntity || en3.namedTag.getBoolean("npc")) {
                        en3.namedTag.putList(new ListTag<StringTag>("Commands")).putList(new ListTag<StringTag>("PlayerCommands"));
                        sender.sendMessage("\u00A7aCommands removed");
                    } else {
                        player.sendMessage("\u00A7cNo NPC found with that ID");
                        return true;
                    }
                case "edit":
                    if (args.length < 3) {
                        player.sendMessage("\u00A7cUsage: /npc edit <ID> <item|armor|scale|name|tphere>");
                        return true;
                    }
                    if (!isInteger(args[1])) {
                        sender.sendMessage("\u00A7cUsage: /npc edit <ID> <item|armor|scale|name|tphere>");
                        return true;
                    }
                    Entity e = player.getLevel().getEntity(Integer.parseInt(args[1]));
                    if (e == null) {
                        player.sendMessage("\u00A7cno entity found with that ID");
                        return true;
                    }
                    PlayerInventory pl = player.getInventory();
                    switch (args[2].toLowerCase()) {
                        case "handitem":
                        case "item":
                        case "hand":
                            if (e instanceof NpcHuman || e.namedTag.getBoolean("ishuman")) {
                                NpcHuman nh = (NpcHuman) e;
                                nh.getInventory().setItemInHand(pl.getItemInHand());
                                player.sendMessage("\u00A7aItem changed to \u00A7e" + pl.getItemInHand().getName());
                                nh.namedTag.putString("Item", pl.getItemInHand().getName());
                                return true;
                            } else {
                                player.sendMessage("\u00A7cThat entity can't have item");
                                return true;
                            }
                        case "armor":
                            if (e instanceof NpcHuman || e.namedTag.getBoolean("ishuman")) {
                                NpcHuman nh = (NpcHuman) e;
                                nh.getInventory().setHelmet(pl.getHelmet());
                                player.sendMessage("\u00A7aHelmet changed to \u00A7e" + pl.getHelmet().getName());
                                nh.namedTag.putString("Helmet", pl.getHelmet().getName());
                                nh.getInventory().setChestplate(pl.getChestplate());
                                player.sendMessage("\u00A7aChestplate changed to \u00A7e" + pl.getChestplate().getName());
                                nh.namedTag.putString("Chestplate", pl.getChestplate().getName());
                                nh.getInventory().setLeggings(pl.getLeggings());
                                player.sendMessage("\u00A7aLeggings changed to \u00A7e" + pl.getLeggings().getName());
                                nh.namedTag.putString("Leggings", pl.getLeggings().getName());
                                nh.getInventory().setBoots(pl.getBoots());
                                player.sendMessage("\u00A7aBoots changed to \u00A7e" + pl.getBoots().getName());
                                nh.namedTag.putString("Boots", pl.getBoots().getName());
                                return true;
                            } else {
                                player.sendMessage("\u00A7cNo Human NPC found with that ID");
                                return true;
                            }
                        case "scale":
                        case "size":
                            if (args.length < 4) {
                                player.sendMessage("\u00A7cUsage: /npc edit <ID> scale <int> \u00A7eDefault is 1.");
                                return true;
                            }
                            if (!isFloat(args[3])) {
                                player.sendMessage("\u00A7cUsage: /npc edit <ID> scale <int>  \u00A7eDefault is 1.");
                                return true;
                            }
                            if (Float.parseFloat(args[3]) > 25) {
                                player.sendMessage("\u00A7cMax scale is 25");
                                return true;
                            }
                            if (e instanceof NpcHuman || e instanceof NpcEntity || e.namedTag.getBoolean("npc")) {
                                e.setScale(Float.parseFloat(args[3]));
                                e.namedTag.putFloat("scale", Float.parseFloat(args[3]));
                                player.sendMessage("\u00A7aScale changed to \u00A7e" + args[3]);
                                return true;
                            } else {
                                player.sendMessage("\u00A7cNo NPC found with that ID");
                                return true;
                            }
                        case "name":
                            if (args.length < 3) {
                                player.sendMessage("\u00A7cUsage: /npc edit <ID> name <name>");
                                return true;
                            }
                            if (e instanceof NpcHuman || e instanceof NpcEntity || e.namedTag.getBoolean("npc")) {
                                if (args.length != 3) {
                                    name = String.join(" ", args);
                                    name = name.replaceFirst("edit", "");
                                    name = name.replaceFirst("name", "");
                                    name = name.replaceFirst(args[1], "");
                                    name = name.replaceFirst(" ", "");
                                    name = name.replaceFirst(" ", "");
                                    name = name.replaceFirst(" ", "");
                                } else {
                                    name = "%k";
                                    e.setNameTagVisible(false);
                                    e.setNameTagAlwaysVisible(false);
                                    player.sendMessage("\u00A7aName removed");
                                }
                                name = name.replaceAll("%n", "\n");
                                if (!name.equals("%k")) {
                                    e.setNameTag(name);
                                    e.setNameTagVisible();
                                    player.sendMessage("\u00A7aName changed to \u00A7e" + name);
                                }
                                e.namedTag.putString("NameTag", name);
                                return true;
                            } else {
                                player.sendMessage("\u00A7cNo NPC found with that ID");
                                return true;
                            }
                        case "gohere":
                        case "tphere":
                        case "tp":
                        case "teleport":
                            if (args.length < 2) {
                                player.sendMessage("\u00A7cUsage: /npc edit <ID> tphere");
                                return true;
                            }
                            if (e instanceof NpcHuman || e instanceof NpcEntity || e.namedTag.getBoolean("npc")) {
                                e.teleport(player);
                                e.respawnToAll();
                                player.sendMessage("\u00A7aEntity teleported");
                                return true;
                            }
                    }
                case "remove":
                case "kill":
                    if (kill.contains(player.getName())) {
                        kill.remove(player.getName());
                        player.sendMessage("\u00A7cKill mode deactivated");
                    } else {
                        kill.add(player.getName());
                        player.sendMessage("\u00A7aKILL MODE - click an entity to remove it");
                    }
                    return true;
                case "entities":
                case "list":
                    sender.sendMessage("\u00A7aAvailable entities: \u00A73" + entities.toString());
                    return true;
                default:
                    sender.sendMessage("\u00A7l\u00A7a--- NPC HELP ---");
                    sender.sendMessage("\u00A73Spawn NPC: \u00A7e/npc spawn <entity> <name>");
                    sender.sendMessage("\u00A73Add console command: \u00A7e/npc addcmd <ID> <cmd>");
                    sender.sendMessage("\u00A73Add player command: \u00A7e/npc addplayercmd <ID> <cmd>");
                    sender.sendMessage("\u00A73Delete console command: \u00A7e/npc delcmd <ID> <cmd>");
                    sender.sendMessage("\u00A73Delete player command: \u00A7e/npc delplayercmd <ID> <cmd>");
                    sender.sendMessage("\u00A73Delete all commands: \u00A7e/npc delallcmd <ID>");
                    sender.sendMessage("\u00A73See all commands: \u00A7e/npc listcmd <ID>");
                    sender.sendMessage("\u00A73Edit NPC: \u00A7e/npc edit <ID> <item|armor|scale|name|tphere>");
                    sender.sendMessage("\u00A73Get NPCs id: \u00A7e/npc getid");
                    sender.sendMessage("\u00A73Get list of all available entities: \u00A7e/npc entities");
                    sender.sendMessage("\u00A73Remove NPC: \u00A7e/npc remove");
                    return true;
            }
        }

        return true;
    }



    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
