package quest.quest.quest3;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import quest.quest.Manager;
import quest.quest.Progress;

import java.util.ArrayList;
import java.util.List;

public class Quest3 implements Listener {

    private final Manager data;
    private final JavaPlugin core;
    private final List<String> about = new ArrayList<>();
    private final Progress progress;
    private final String NPC2 = "CraftChunk{x=8z=-14}";
    final int delay = 70;

    public Quest3(Manager data, JavaPlugin core, Progress progress) {
        this.data = data;
        this.core = core;
        this.progress = progress;

        about.add("Начало - помочь пекарю");
        about.add("- " + ChatColor.GRAY + "Найти пекаря.");
        about.add("- " + ChatColor.GRAY + "Сделать тесто.");
        about.add("- " + ChatColor.GRAY + "Испечь хлеб.");
        about.add("- " + ChatColor.GRAY + "Вернуть хлеб пекарю.");
        about.add("Награда: " + ChatColor.GRAY + "неизвестный инструмент" + ChatColor.GOLD + ".");
    }

    @EventHandler
    private void onClickNPC(PlayerInteractAtEntityEvent e) {
        Entity en = e.getRightClicked();
        String NPC1 = "CraftChunk{x=7z=-14}";
        if (!en.getLocation().getChunk().toString().equals(NPC1)) return;

        Player p = e.getPlayer();
        if (!progress.isFinished(p, "Начало - найти местных")) return;
        if (progress.isFinished(p, about.get(0))) {
            say1(p, "У меня пока что нет для тебя заданий. Приходи позже.");
            return;
        }
        if (data.hasQuest(p)) {
            if (data.getQuest(p).equals(about)) {
                say1(p, "Тебе не нравится печь хлеб?");
                return;
            }
        }

        say1(p, "Чем я могу быть полезен? Подожди-ка...");
        final int delay = 45;
        new BukkitRunnable() {
            @Override
            public void run() {
                say1(p, "Ты чужеземец, полагаю?");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        say1(p, "Если тебе нужна работа, могу посоветовать того, кому нужна помощь.");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                say1(p, "Как насчёт помочь пекарю?");
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        dialog1(p);
                                    }
                                }.runTaskLater(core, 15);
                            }
                        }.runTaskLater(core, delay);
                    }
                }.runTaskLater(core, delay);
            }
        }.runTaskLater(core, delay);
    }

    @EventHandler
    private void onClickNPCBaker(PlayerInteractAtEntityEvent e) {
        Entity en = e.getRightClicked();
        if (!en.getLocation().getChunk().toString().equals(NPC2)) return;

        Player p = e.getPlayer();
        if (!data.hasQuest(p) || !data.getQuest(p).equals(about)) {
            say2(p, "Здравствуйте.");
            return;
        }

        if (progress.isFinished(p, "bread_given_quest3") && !p.getInventory().getItemInMainHand().getType().equals(Material.BREAD)) {
            say2(p, "Просто сделай тесто и сунь в печь.");
            return;
        }


        if (p.getInventory().getItemInMainHand().getType().equals(Material.BREAD)) {
            say2(p, "Так держать! Буханку можешь себе оставить, у меня таких целые склады.");
            new BukkitRunnable() {
                @Override
                public void run() {
                    say2(p, "Вижу, ты человек приличный. Держи, заслужил.");
                    p.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
                    data.finishQuest(p);
                    progress.saveQuest(p, about.get(0));
                }
            }.runTaskLater(core, delay);
            return;
        }

        say2(p, "Старейшина послал, верно?");
        new BukkitRunnable() {
            @Override
            public void run() {
                say2(p, "С чего он вообще взял, что мне нужна помощь?..");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        say2(p, "Ладно, раз уж ты пришёл, стоит чему-то научиться.");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                say2(p, "Хлеб это целое искусство. Правда, сейчас процесс упрощён донельзя.");
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        say2(p, "Люд не особо-то сыто живёт. Потому и тесто можно делать из чего попало.");
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                say2(p, "Говорят, что некий Mraxean всё ещё печёт хлеб по древним традициям.");
                                                new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        say2(p, "Но сейчас не об этом. Держи пшено и сахар, попробуй сделать из этого тесто.");
                                                        p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.WHEAT, 6));
                                                        p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.SUGAR, 3));
                                                        progress.saveQuest(p, "bread_given_quest3");
                                                    }
                                                }.runTaskLater(core, delay);
                                            }
                                        }.runTaskLater(core, delay);
                                    }
                                }.runTaskLater(core, delay);
                            }
                        }.runTaskLater(core, delay);
                    }
                }.runTaskLater(core, delay);
            }
        }.runTaskLater(core, delay);
    }

    private void dialog1(Player p) {
        data.setWaiting(p, about.get(0));
        clickable(p, ChatColor.AQUA + "> " + ChatColor.WHITE + "Где я могу его найти?", "/quest3_1");
        clickable(p, ChatColor.AQUA + "> " + ChatColor.WHITE + "Пожалуй, не сейчас.", "/quest3_2");
    }

    @EventHandler
    private void craft(CraftItemEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (data.hasQuest(p)) {
            if (e.getCurrentItem().getType().toString().equals("SAPPHIREBREAD_TIESTO") && data.getQuest(p).equals(about) && p.getLocation().getChunk().toString().equals(NPC2)) {
                say2(p, "Хм, неплохое тесто получилось. ");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        say2(p, "Вот эта электропечь работает быстрее обычной каменной.");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                say2(p, "Попробуй испечь хлеб в ней.");
                            }
                        }.runTaskLater(core, delay);
                    }
                }.runTaskLater(core, delay);
            }
        }
    }

    @EventHandler
    private void onCmd(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage();
        Player p = e.getPlayer();

        if (cmd.equals("/quest3_1")) {
            e.setCancelled(true);
            if (data.checkAndPrevent(p, about.get(0), e)) return;
            data.removeWaiting(p);
            data.startQuest(p, about);
            say1(p, "В доме слева. Постарайся не пачкать пол, он тот ещё чистюля.");
        }

        if (cmd.equals("/quest3_2")) {
            e.setCancelled(true);
            if (data.checkAndPrevent(p, about.get(0), e)) return;
            data.removeWaiting(p);
            say1(p, "На нет и суда нет.");
        }
    }

    private void clickable(Player p, String message, String command) {
        TextComponent component = new TextComponent(message);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        p.spigot().sendMessage(component);
    }

    private void say1(Player p, String msg) {
        p.sendMessage(ChatColor.GOLD + "Старейшина: " + ChatColor.RESET + msg);
    }

    private void say2(Player p, String msg) {
        p.sendMessage(ChatColor.GOLD + "Пекарь: " + ChatColor.RESET + msg);
    }


}
