package themasterkitty.lesspearls;

import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class LessPearls extends JavaPlugin implements Listener {
    Map<UUID, Integer> kills = new HashMap<>();
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    @EventHandler
    public void onPlayerTrade(PlayerTradeEvent e) {
        if (e.getTrade().getResult().getType() == Material.ENDER_PEARL) e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerChat(PlayerChatEvent e) { // requested by LilyLikess
        if (e.getMessage().toLowerCase().contains("cookie")) e.getPlayer().getInventory().addItem(new ItemStack(Material.COOKIE, new Random().nextInt(1, 16)));
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntityType() == EntityType.ENDERMAN && Objects.nonNull(e.getEntity().getKiller())) {
            kills.put(e.getEntity().getKiller().getUniqueId(), kills.getOrDefault(e.getEntity().getKiller().getUniqueId(), 0) + 1);

            new BukkitRunnable() {
                @Override
                public void run() {
                    kills.put(e.getEntity().getKiller().getUniqueId(), kills.getOrDefault(e.getEntity().getKiller().getUniqueId(), 1) - 1);
                }
            }.runTaskLater(this, 3600);
            e.getDrops().forEach(item -> {
                if (item.getType() == Material.ENDER_PEARL) {
                    e.getDrops().remove(item);
                    if (kills.getOrDefault(e.getEntity().getKiller().getUniqueId(), 0) <= 5)
                        e.getDrops().add(new ItemStack(Material.ENDER_PEARL, 1));
                }
            });
        }
    }
    @EventHandler
    public void onPiglinBarter(PiglinBarterEvent e) {
        e.getOutcome().forEach(item -> {
            if (item.getType() == Material.ENDER_PEARL && item.getAmount() > 1) {
                e.getOutcome().remove(item);
                e.getOutcome().add(new ItemStack(Material.ENDER_PEARL, 1));
            }
        });
    }
}
