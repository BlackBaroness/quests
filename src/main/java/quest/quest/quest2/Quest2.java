package quest.quest.quest2;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import quest.quest.Manager;
import quest.quest.Progress;

import java.util.ArrayList;
import java.util.List;

public class Quest2 implements Listener {

    private final Manager data;
    private final JavaPlugin core;
    private final List<String> about = new ArrayList<>();
    private final Progress progress;

    public Quest2(Manager data, JavaPlugin core, Progress progress) {
        this.data = data;
        this.core = core;
        this.progress = progress;

        about.add("Начало - найти местных");
        about.add("- " + ChatColor.GRAY + "Дойти до деревни (X: 125, Z: -208).");
        about.add("- " + ChatColor.GRAY + "Поговорить со старейшиной.");
        about.add("Награда: " + ChatColor.GRAY + "неизвестно" + ChatColor.GOLD + ".");
    }

    @EventHandler
    private void clickNpc(PlayerInteractAtEntityEvent e) {
        Entity npc = e.getRightClicked();
        Player p = e.getPlayer();

        if (npc.getLocation().getChunk().toString().equals("CraftChunk{x=-7z=0}")) {
            if (progress.isFinished(p, about.get(0))) {
                sayAsUnknown(p, "Здравствуй.");
                return;
            }
            if (data.hasQuest(p) && !data.getQuest(p).get(0).equals(about.get(0))) {
                p.sendMessage(ChatColor.GOLD + "Вы уже имеете активный квест!");
                p.sendMessage(ChatColor.GOLD + "Вы можете узнать информацию о нём, введя " + ChatColor.GRAY + "/quest" + ChatColor.GOLD + ".");
                return;
            }

            if (data.hasQuest(p)) {
                sayAsUnknown(p, "Казалось, всё так хорошо, и вдруг...");
                return;
            }

            sayAsUnknown(p, "Ты не похож на местного. Чужеземцы всё еще прибывают?");
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.sendMessage(ChatColor.GOLD + "Вы заговорили с местным. Попробуйте ответить ему, нажимая на варианты диалога.");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            level1(p);
                        }
                    }.runTaskLater(core, 10);
                }
            }.runTaskLater(core, 40);
            return;
        }
        if (npc.getLocation().getChunk().toString().equals("CraftChunk{x=7z=-14}")) {
            if (data.hasQuest(p)) {
                if (data.getQuest(p).get(0).equals(about.get(0))) {
                    data.finishQuest(p);
                    progress.saveQuest(p, about.get(0));
                    return;
                }
                p.sendMessage(ChatColor.RED + "У вас уже есть квест.");
                return;
            }
            if (!progress.isFinished(p, about.get(0))) {
                p.sendMessage(ChatColor.GOLD + "Старейшина: " + ChatColor.RESET + "Мы знакомы?");
            }
        }
    }

    private void level1(Player p) {
        data.setWaiting(p, about.get(0));
        clickable(p, ChatColor.AQUA + "> " + ChatColor.WHITE + "Чужеземцы? Кто это?", "/quest2_1");
        clickable(p, ChatColor.AQUA + "> " + ChatColor.WHITE + "Не подскажешь, куда стоит держать путь?", "/quest2_2");
    }

    private void level2(Player p) {
        p.sendMessage(ChatColor.GOLD + "Этот выбор является решающим. Готовы ли вы принять квест?");
        data.setWaiting(p, about.get(0));
        clickable(p, ChatColor.AQUA + "> " + ChatColor.WHITE + "Я хочу отправиться в деревню.", "/quest2_3");
        clickable(p, ChatColor.AQUA + "> " + ChatColor.WHITE + "Пожалуй, в другой раз.", "/quest2_4");
    }


    @EventHandler
    private void onCmd(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage();
        Player p = e.getPlayer();

        if (cmd.equals("/chunk")) {
            p.sendMessage(p.getLocation().getChunk().toString());
            e.setCancelled(true);
            return;
        }

        if (cmd.equals("/quest2_1")) {
            e.setCancelled(true);
            if (data.checkAndPrevent(p, about.get(0), e)) return;
            data.removeWaiting(p);
            sayAsUnknown(p, "Странно, что вы все даже не знаете, кто вы такие.");
            new BukkitRunnable() {
                @Override
                public void run() {
                    sayAsUnknown(p, "Чужеземцы бессмертны, в отличие от нас.");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            sayAsUnknown(p, "Не нравится мне эта история с Источником...");
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    level1(p);
                                }
                            }.runTaskLater(core, 20);
                        }
                    }.runTaskLater(core, 40);
                }
            }.runTaskLater(core, 40);
            return;
        }

        if (cmd.equals("/quest2_2")) {
            e.setCancelled(true);
            if (data.checkAndPrevent(p, about.get(0), e)) return;
            data.removeWaiting(p);
            sayAsUnknown(p, "Хм...");
            new BukkitRunnable() {
                @Override
                public void run() {
                    sayAsUnknown(p, "На северо-востоке стоит деревушка, там ты можешь найти себе занятие.");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            sayAsUnknown(p, "Тебе бы приодеться... Думаю, они помогут с этим.");
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    level2(p);
                                }
                            }.runTaskLater(core, 20);
                        }
                    }.runTaskLater(core, 40);
                }
            }.runTaskLater(core, 40);
            return;
        }

        if (cmd.equals("/quest2_3")) {
            e.setCancelled(true);
            if (data.checkAndPrevent(p, about.get(0), e)) return;
            data.removeWaiting(p);
            data.startQuest(p, about);
            sayAsUnknown(p, "Вот и славно. Когда будешь на месте, поищи старейшину.");
            new BukkitRunnable() {
                @Override
                public void run() {
                    sayAsUnknown(p, "Альбион довольно безопасный, но без оружия лучше не ходить. Держи.");
                    p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.STONE_SWORD));
                }
            }.runTaskLater(core, 40);
            return;
        }

        if (cmd.equals("/quest2_4")) {
            e.setCancelled(true);
            if (data.checkAndPrevent(p, about.get(0), e)) return;
            data.removeWaiting(p);
            sayAsUnknown(p, "Ладно, бывай.");
        }
    }

    private void clickable(Player p, String message, String command) {
        TextComponent component = new TextComponent(message);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        p.spigot().sendMessage(component);
    }

    private void sayAsUnknown(Player p, String msg) {
        p.sendMessage(ChatColor.GOLD + "Незнакомец: " + ChatColor.RESET + msg);
    }
}
