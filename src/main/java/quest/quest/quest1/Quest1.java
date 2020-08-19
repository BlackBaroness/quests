package quest.quest.quest1;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import quest.quest.Manager;

import java.util.ArrayList;
import java.util.List;

public class Quest1 implements Listener {

    private final Manager data;
    private final JavaPlugin core;
    private final List<String> about = new ArrayList<>();

    public Quest1(Manager data, JavaPlugin core) {
        this.data = data;
        this.core = core;
        about.add("Помочь отшельнику");
        about.add("- " + ChatColor.GRAY + "Добыть 20 блоков любого дерева.");
        about.add("- " + ChatColor.GRAY + "Передать древесину старику.");
        about.add("Награда: " + ChatColor.GRAY + "неизвестно" + ChatColor.GOLD + ".");
    }

    @EventHandler
    public void i(PlayerInteractAtEntityEvent e) {
        Entity npc = e.getRightClicked();
        if (!npc.getLocation().getChunk().toString().equals("CraftChunk{x=-1z=20}")) return;
        Player p = e.getPlayer();

        if (data.hasQuest(p) && !data.getQuest(p).get(0).equals(about.get(0))) {
            p.sendMessage(ChatColor.GOLD + "Вы уже имеете активный квест!");
            p.sendMessage(ChatColor.GOLD + "Вы можете узнать информацию о нём, введя " + ChatColor.GRAY + "/quest" + ChatColor.GOLD + ".");
            return;
        }
        if (data.hasQuest(p)) {
            if (data.getQuest(p).get(0).equals(about.get(0))) {
                if (howMuchLog(p) >= 20) {
                    removeItems(p.getInventory());
                    p.sendMessage(ChatColor.GOLD + "Незнакомец: " + ChatColor.RESET + "Спасибо тебе! Вовек твою доброту не забуду!");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            p.sendMessage(ChatColor.GOLD + "Незнакомец: " + ChatColor.RESET + "Жаль, отблагодарить дедушке тебя нечем... Береги себя.");
                            data.finishQuest(p);
                        }
                    }.runTaskLater(core, 20);
                    return;
                }
                p.sendMessage(ChatColor.GOLD + "Незнакомец: " + ChatColor.RESET + "Как же сурова бывает жизнь...");
                return;
            }
        }
        p.sendMessage(ChatColor.GOLD + "Незнакомец: " + ChatColor.RESET + "Здравствуй, чужеземец.");
        new BukkitRunnable() {
            @Override
            public void run() {
                data.setWaiting(p, about.get(0));
                p.sendMessage(ChatColor.GOLD + "Незнакомец: " + ChatColor.RESET + "Не мог бы ты помочь мне? Стар я уже, дров в доме нет, а холода наступают.");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        clickable(p, ChatColor.AQUA + "> " + ChatColor.WHITE + "Конечно.", "/questYes1");
                        clickable(p, ChatColor.AQUA + "> " + ChatColor.WHITE + "Не сейчас.", "/questNo1");
                    }
                }.runTaskLater(core, 20);

            }
        }.runTaskLater(core, 30);
    }

    @EventHandler
    void cmd(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage();
        Player p = e.getPlayer();
        if (cmd.equals("/questYes1")) {
            e.setCancelled(true);
            if (data.checkAndPrevent(p, about.get(0), e)) return;
            data.removeWaiting(p);
            agree(p);
        }
        if (cmd.equals("/questNo1")) {
            e.setCancelled(true);
            if (data.checkAndPrevent(p, about.get(0), e)) return;
            data.removeWaiting(p);
            disagree(p);
        }
    }

    private void agree(Player p) {
        data.startQuest(p, about);
        p.sendMessage(ChatColor.GOLD + "Незнакомец: " + ChatColor.RESET + "Только на таких, как ты, наш мир и держится...");
        new BukkitRunnable() {
            @Override
            public void run() {
                p.sendMessage(ChatColor.GOLD + "Незнакомец: " + ChatColor.RESET + "Вот тебе мой старый топор. Надеюсь, его хватит для таких нужд.");
                p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.STONE_AXE));
            }
        }.runTaskLater(core, 40);
    }

    private void disagree(Player p) {
        p.sendMessage(ChatColor.GOLD + "Незнакомец: " + ChatColor.RESET + "Ладно, путник, иди своей дорогой.");
    }

    private void clickable(Player p, String message, String command) {
        TextComponent component = new TextComponent(message);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        p.spigot().sendMessage(component);
    }

    int howMuchLog(Player p) {
        Inventory inv = p.getInventory();
        int number = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null) {
                if (item.getType().equals(Material.LOG)) {
                    number = number + item.getAmount();
                }
            }
        }
        return number;
    }

    void removeItems(Inventory inventory) {
        int amount = 20;
        if (amount <= 0) return;
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) continue;
            if (Material.LOG == is.getType()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) break;
                }
            }
        }
    }
}