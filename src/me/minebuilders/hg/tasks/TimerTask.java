package me.minebuilders.hg.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Status;

public class TimerTask implements Runnable {

	private int remainingtime;
	private int id;
	private Game game;

	public TimerTask(Game g, int time) {
		this.remainingtime = time;
		this.game = g;
		
		this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.plugin, this, 30 * 20L, 30 * 20L);
	}
	
	@Override
	public void run() {
		if (game == null || game.getStatus() != Status.RUNNING) stop(); //A quick null check!
		
		remainingtime = (remainingtime - 30);

		if (remainingtime == 30 && HG.plugin.getConfig().getBoolean("settings.teleport-at-end")) {
			game.msgAll("게임이 거의 끝나갑니다. 싸우세요!!");
			game.respawnAll();
		} else if (this.remainingtime < 10) {
			stop();
			game.stop();
		} else {
			int minutes = this.remainingtime / 60;
			int asd = Integer.valueOf(this.remainingtime % 60);
			if (minutes != 0) game.msgAll("이 게임이 " + minutes + (asd == 0?" 분후에 끝납니다!":" 분, " + asd+" 초후에 끝납니다!"));
			else game.msgAll("이 게임이 " + this.remainingtime +" 초후에 끝납니다!");
		}
	}
	
	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
