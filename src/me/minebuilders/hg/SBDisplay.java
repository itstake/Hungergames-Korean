package me.minebuilders.hg;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class SBDisplay {

	private ScoreboardManager manager;
	private Scoreboard board;
	private Objective ob;
	private HashMap<String, Scoreboard> score = new HashMap<String, Scoreboard>();
	private Game g;

	public SBDisplay(Game g) {
		this.manager = Bukkit.getScoreboardManager();
		this.board = manager.getNewScoreboard();
		this.ob = board.registerNewObjective(ChatColor.GREEN + "살아있는 플레이어:", "dummy");
		this.ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.ob.setDisplayName("헝거게임");
		this.g = g;
	}

	public void setAlive() {
		Score score = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "살아있는 플레이어:")); 
		score.setScore(g.getPlayers().size());
	}

	public void resetAlive() {
		board.resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + "살아있는 플레이어:"));
		score.clear();
	}

	public void setSB(Player p) {
		score.put(p.getName(), p.getScoreboard());
		p.setScoreboard(board);
	}

	public void restoreSB(Player p) {
		if (score.get(p.getName()) == null) {
			p.setScoreboard(manager.getNewScoreboard());
		} else {
			p.setScoreboard(score.get(p.getName()));
			score.remove(p.getName());
		}
	}
}
