package quest.quest;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Progress {

    private final File dir;

    public Progress(JavaPlugin core) {
        dir = new File(core.getDataFolder() + File.separator + "progress");
        if (!dir.exists()) if (dir.mkdir()) log("Main progress dir created");
    }

    public boolean isFinished(Player p, String quest) {
        // прошёл ли игрок квест
        UUID uuid = p.getUniqueId();
        String id = uuid.toString();

        File var = new File(dir + File.separator + id + File.separator + quest);
        return var.exists();
    }

    public void saveQuest(Player p, String quest) {
        try {
            // добавляет квест в список пройденных
            UUID uuid = p.getUniqueId();
            String id = uuid.toString();

            // папка игрока по uuid
            File var = new File(dir + File.separator + id);
            if (!var.exists()) if (var.mkdir()) log(p.getName() + "'s progress dir created");

            // файл квеста
            File var1 = new File(dir + File.separator + id + File.separator + quest);
            if (!var1.exists())
                if (var1.createNewFile()) log(p.getName() +  " | Finished quest " + quest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String msg) {
        // отчеты
        System.out.println(ChatColor.YELLOW + "[LOG] " + ChatColor.WHITE + msg);
    }
}
