package me.minebuilders.hg.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.PlayerData;

public class CompassTask implements Runnable {

	private HG plugin;

	public CompassTask(HG plugin) {
		this.plugin = plugin;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.plugin, this, 25L, 25L);
	}

	@Override
	public void run() {
		for (Player p : Bukkit.getOnlinePlayers()) {

			if (p.getInventory().contains(Material.COMPASS)) {
				PlayerData pd = plugin.players.get(p.getName());

				if (pd != null) {

					String[] st = getNearestPlayer(p, pd);
					String info = ChatColor.WHITE + "" + ChatColor.BOLD + "Nearest Player: " + ChatColor.RED + st[0] + "    "  + ChatColor.WHITE + "" + ChatColor.BOLD + "Distance: " + ChatColor.RED + st[1]; 

					for (ItemStack it : p.getInventory()) {
						if (it != null && it.getType() == Material.COMPASS) {
							ItemMeta im = it.getItemMeta();
							im.setDisplayName(info);
							it.setItemMeta(im);
						}
					}
				}

			}
		}
	}

	private int cal(int i) {
		if (i < 0) {
			return -i;
		}
		return i;
	}

	public String[] getNearestPlayer(Player p, PlayerData pd) {

		Game g = pd.getGame();

		int x = p.getLocation().getBlockX();
		int y = p.getLocation().getBlockY();
		int z = p.getLocation().getBlockZ();

		int i = 200000;

		Player player = null;

		for (String s : g.getPlayers()) {

			Player p2 = Bukkit.getPlayer(s);

			if (p2 != null && !p2.equals(p) && !pd.isOnTeam(s)) {

				Location l = p2.getLocation();

				int c = (int) (cal((int) (x - l.getX())) + cal((int) (y - l.getY())) + cal((int) (z - l.getZ())));

				if (i > c) {
					player = p2;
					i = c;
				}
			}
		}
		if (player != null) p.setCompassTarget(player.getLocation());

		return new String[] {(player==null?"none":player.getName()), String.valueOf(i)};
	}
}
