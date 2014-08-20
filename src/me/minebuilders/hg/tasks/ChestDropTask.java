package me.minebuilders.hg.tasks;

import java.util.ArrayList;
import java.util.List;

import me.minebuilders.hg.ChestDrop;
import me.minebuilders.hg.Config;
import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

public class ChestDropTask implements Runnable {

	private Game g;
	private int timerID;
	private List<ChestDrop> chests = new ArrayList<ChestDrop>();

	public ChestDropTask(Game g) {
		this.g = g;
		timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(HG.plugin, this, Config.randomChestInterval, Config.randomChestInterval);
	}

	@SuppressWarnings("deprecation")
	public void run() {
		Integer[] i = g.getRegion().getRandomLocs();

		int x = i[0];
		int y = i[1];
		int z = i[2];
		World w = g.getRegion().getWorld();

		while (w.getBlockTypeIdAt(x, y, z) == 0) {
			y--;
			
			if (y <= 0) {
				i = g.getRegion().getRandomLocs();

				x = i[0];
				y = i[1];
				z = i[2];
			}
		}

		y = y + 10;

		Location l = new Location(w, x, y, z);

		FallingBlock fb = l.getWorld().spawnFallingBlock(l, 33, ((byte)6));

		chests.add(new ChestDrop(fb));

		for (String s : g.getPlayers()) {
			Player p = Bukkit.getPlayer(s);
			if (p != null) {
			Util.scm(p, "&6*&b&m                                                                             &6*");
			Util.scm(p, "&b| &3A Care-Package was just dropped near: &f"+x+"&3, &f"+y+"&3, &f"+z);
			Util.scm(p, "&6*&b&m                                                                             &6*");
			}
		}
	}

	public void shutdown() {
		Bukkit.getScheduler().cancelTask(timerID);
		for (ChestDrop cd : chests) {
			if (cd != null) cd.remove();
		}
	}
}
