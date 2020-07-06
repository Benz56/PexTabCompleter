package com.benzoft.pextabcompleter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.IntStream;

public class UpdateChecker implements Listener {

    private final int ID = 69539;
    private final Plugin plugin;
    private final String localPluginVersion;
    private String spigotPluginVersion;

    UpdateChecker(final Plugin plugin) {
        this.plugin = plugin;
        localPluginVersion = plugin.getDescription().getVersion();
    }

    void checkForUpdate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        final HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + ID).openConnection();
                        connection.setRequestMethod("GET");
                        spigotPluginVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                    } catch (final IOException e) {
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUpdate checker failed!"));
                        e.printStackTrace();
                        cancel();
                        return;
                    }
                    if (!isLatestVersion()) {
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&aPermissionsEx Tab Completer&7] &fA new update is available at:"));
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bhttps://www.spigotmc.org/resources/" + ID + "/updates"));
                        cancel();
                    }
                });
            }
        }.runTaskTimer(plugin, 0, 12_000);
    }

    private boolean isLatestVersion() {
        try {
            final int[] local = Arrays.stream(localPluginVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
            final int[] spigot = Arrays.stream(spigotPluginVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
            return IntStream.range(0, local.length).noneMatch(i -> spigot[i] > local[i]);
        } catch (final NumberFormatException ignored) {
            return localPluginVersion.equals(spigotPluginVersion);
        }
    }
}
