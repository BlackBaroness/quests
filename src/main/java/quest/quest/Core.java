package quest.quest;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import quest.quest.quest1.Quest1;
import quest.quest.quest2.Quest2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Core extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) if (getDataFolder().mkdir()) this.getLogger().info("dir created");
        Manager manager;
        if (getManager() == null) {
            HashMap<UUID, List<String>> var = new HashMap<>();
            manager = new Manager(var, this);
        } else {
            manager = new Manager(getManager(), this);
        }

        Progress progress = new Progress(this);

        Quest1 quest1 = new Quest1(manager, this);
        Bukkit.getPluginManager().registerEvents(quest1, this);

        Quest2 quest2 = new Quest2(manager, this, progress);
        Bukkit.getPluginManager().registerEvents(quest2, this);

        Command command = new Command(manager);
        getCommand("quest").setExecutor(command);
    }

    private HashMap<UUID, List<String>> getManager() {
        HashMap<UUID, List<String>> var = null;
        try {
            File file = new File(getDataFolder() + File.separator + "quests");
            if (!file.exists()) return var;
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            var = (HashMap<UUID, List<String>>) ois.readObject();
            ois.close();
        } catch (
                IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return var;
    }
}
