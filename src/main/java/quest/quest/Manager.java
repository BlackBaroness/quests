package quest.quest;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Manager implements Serializable {

    private HashMap<UUID, List<String>> data = new HashMap<>();
    transient private final JavaPlugin core;
    final transient private HashMap<UUID, String> waiting = new HashMap<>();

    Manager(HashMap<UUID, List<String>> hashMap, JavaPlugin core) {
        if (hashMap != null) data = hashMap;
        this.core = core;
    }

    public void startQuest(Player p, List<String> about) {
        // старт нового квеста
        data.put(p.getUniqueId(), about);
        p.sendMessage(ChatColor.GOLD + "Вы получили новое задание! Введите " + ChatColor.GRAY + "/quest" + ChatColor.GOLD + ", чтобы ознакомиться с ним.");
        save();
    }

    public void finishQuest(Player p) {
        // финиш квеста
        String str = "'" + getQuest(p).get(0) + "'";
        p.sendMessage(ChatColor.GOLD + "Вы выполнили квест " + ChatColor.GRAY + str + ChatColor.GOLD + "!");
        data.remove(p.getUniqueId());
        save();
    }

    public boolean hasQuest(Player p) {
        return data.containsKey(p.getUniqueId());
    }

    public List<String> getQuest(Player p) {
        return data.get(p.getUniqueId());
    }

    public void setWaiting(Player p, String name) {
        waiting.put(p.getUniqueId(), name);

        new BukkitRunnable() {
            @Override
            public void run() {
                waiting.remove(p.getUniqueId());
            }
        }.runTaskLater(core, 100 * 20);
    }

    public boolean checkAndPrevent(Player p, String name, PlayerCommandPreprocessEvent e) {
        return !isWaiting(p, name);
    }

    public void removeWaiting(Player p) {
        waiting.remove(p.getUniqueId());
    }

    public boolean isWaiting(Player p, String name) {
        if (!waiting.containsKey(p.getUniqueId())) return false;
        return waiting.get(p.getUniqueId()).equals(name);
    }

    private void save() {
        Save save = new Save(this);
        save.start();
    }

    public HashMap<UUID, List<String>> getData() {
        return data;
    }

    public JavaPlugin getCore() {
        return core;
    }
}
