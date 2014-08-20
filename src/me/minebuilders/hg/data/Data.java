package me.minebuilders.hg.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import me.minebuilders.hg.Bound;
import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;
import me.minebuilders.hg.tasks.CompassTask;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Data {

	private FileConfiguration arenadat = null;
	private File customConfigFile = null;
	private final HG plugin;

	public Data(HG plugin) {
		this.plugin = plugin;
		reloadCustomConfig();
		load();
	}

	public FileConfiguration getConfig() {
		return arenadat;
	}

	public void reloadCustomConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(plugin.getDataFolder(), "arenas.yml");
		}
		arenadat = YamlConfiguration.loadConfiguration(customConfigFile);

		// Look for defaults in the jar
		InputStream defConfigStream = plugin.getResource("arenas.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			arenadat.setDefaults(defConfig);
		}
	}

	public FileConfiguration getCustomConfig() {
		if (arenadat == null) {
			this.reloadCustomConfig();
		}
		return arenadat;
	}

	public void saveCustomConfig() {
		if (arenadat == null || customConfigFile == null) {
			return;
		}
		try {
			getCustomConfig().save(customConfigFile);
		} catch (IOException ex) {
			Util.log("Could not save config to " + customConfigFile);
		}
	}

	public void load() {
		int freeroam = plugin.getConfig().getInt("settings.free-roam");
		if (new File(plugin.getDataFolder(), "arenas.yml").exists()) {
			
			new CompassTask(plugin);
			
			for (String s : arenadat.getConfigurationSection("arenas").getKeys(false)) {
				boolean isReady = true;
				List<Location> spawns = new ArrayList<Location>();
				Sign lobbysign = null;
				int timer = 0;
				int minplayers = 0;
				int maxplayers = 0;
				Bound b = null;

				try {
					timer = arenadat.getInt("arenas." + s + ".info." + "timer");
					minplayers = arenadat.getInt("arenas." + s + ".info." + "min-players");
					maxplayers = arenadat.getInt("arenas." + s + ".info." + "max-players");
				} catch (Exception e) { 
					Util.warning("Unable to load infomation for arena " + s + "!"); 
					isReady = false;
				}

				try {
					lobbysign = (Sign) getSLoc(arenadat.getString("arenas." + s + "." + "lobbysign")).getBlock().getState();
				} catch (Exception e) { 
					Util.warning("Unable to load lobbysign for arena " + s + "!"); 
					isReady = false;
				}

				try {
					for (String l : arenadat.getStringList("arenas." + s + "." + "spawns")) {
						spawns.add(getLocFromString(l));
					}
				} catch (Exception e) { 
					Util.warning("Unable to load random spawns for arena " + s + "!"); 
					isReady = false;
				}

				try {
					b = new Bound(arenadat.getString("arenas." + s + ".bound." + "world"), BC(s, "x"), BC(s, "y"), BC(s, "z"), BC(s, "x2"), BC(s, "y2"), BC(s, "z2"));
				} catch (Exception e) { 
					Util.warning("Unable to load region bounds for arena " + s + "!"); 
					isReady = false;
				}
				plugin.games.add(new Game(s, b, spawns, lobbysign, timer, minplayers, maxplayers, freeroam, isReady));
			}
		}
	}

	public int BC(String s, String st) {
		return arenadat.getInt("arenas." + s + ".bound." + st);
	}

	public Location getLocFromString(String s) {
		String[] h = s.split(":");
		return new Location(Bukkit.getServer().getWorld(h[0]), Integer.parseInt(h[1]) + 0.5, Integer.parseInt(h[2]), Integer.parseInt(h[3]) + 0.5, Float.parseFloat(h[4]), Float.parseFloat(h[5]));
	}

	public Location getSLoc(String s) {
		String[] h = s.split(":");
		return new Location(Bukkit.getServer().getWorld(h[0]), Integer.parseInt(h[1]), Integer.parseInt(h[2]), Integer.parseInt(h[3]));
	}
}
