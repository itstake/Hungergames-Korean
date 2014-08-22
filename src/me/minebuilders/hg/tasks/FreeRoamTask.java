package me.minebuilders.hg.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

public class FreeRoamTask implements Runnable {

	private Game game;
	private int id;

	public FreeRoamTask(Game g) {
		this.game = g;
		for (String s : g.getPlayers()) {
			Player p = Bukkit.getPlayer(s);
			if (p != null) {
				Util.scm(p,"게임이 시작되었습니다!"); 
				Util.scm(p,g.getRoamTime() + " 초후에 다른 플레이어에게 데미지를 줄 수 있습니다!"); 
				p.setHealth(20);
				p.setFoodLevel(20);
				g.unFreeze(p);
			}
		}
		this.id = Bukkit.getScheduler().scheduleSyncDelayedTask(HG.plugin, this, g.getRoamTime() * 20L);
	}

	@Override
	public void run() {
		game.msgAll("이제 다른 플레이어에게 데미지를 줄 수 있습니다!");
		game.startGame();
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
