package quest.quest;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Command implements CommandExecutor {

    private final Manager manager;

    Command(Manager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Player p = (Player) sender;
        if (!manager.hasQuest(p)) {
            p.sendMessage(ChatColor.GOLD + "У вас нет активного квеста.");
            return true;
        }
        List<String> var = manager.getQuest(p);
        String first = var.get(0);
        first = "==== " + ChatColor.GRAY + first + ChatColor.GOLD + " ====";
        for (String str : var) {
            if (str.equals(var.get(0))) {
                p.sendMessage(ChatColor.GOLD + first);
                continue;
            }
            p.sendMessage(ChatColor.GOLD + str);
        }
        StringBuilder son = new StringBuilder();
        for (int i = 0; i < first.length() - 4; i++) {
            son.append("=");
        }
        p.sendMessage(ChatColor.GOLD + String.valueOf(son));
        return true;
    }
}
