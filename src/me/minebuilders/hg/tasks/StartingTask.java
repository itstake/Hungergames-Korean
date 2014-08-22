package me.minebuilders.hg.tasks;

import org.bukkit.Bukkit;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

public class StartingTask implements Runnable {

	private int timer;
	private int id;
	private Game game;

	public StartingTask(Game g) {
		this.timer = 30;
		this.game = g;

		this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.plugin, this, 5 * 20L, 5 * 20L);
	}

	@Override
	public void run() {
		timer = (timer - 5);

		if (timer <= 0) {
			stop();
			game.startFreeRoam();
		} else {
			game.msgAll("&7" + timer + " 초후에 시작합니다..");
		}
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
