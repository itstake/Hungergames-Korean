package me.minebuilders.hg;

import me.minebuilders.hg.Util;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerData {

	//Pregame data
	private ItemStack[] inv;
	private ItemStack[] equip;
	private int exp;
	private GameMode mode;
	
	//Ingame data
	private Team team;
	private Game game;
	
	public PlayerData(Player p, Game game) {
		this.game = game;
		inv = p.getInventory().getContents();
		equip = p.getInventory().getArmorContents();
		exp = (int) p.getExp();
		mode = p.getGameMode();
		Util.clearInv(p);
	}

	@SuppressWarnings("deprecation")
	public void restore(Player p) {
		Util.clearInv(p);
		p.setExp(0);
		p.giveExp(exp);
		p.getInventory().setContents(inv);
		p.getInventory().setArmorContents(equip);
		p.setGameMode(mode);
		p.updateInventory();
	}
	
	public boolean isOnTeam(String s) {
		return (team != null && team.isOnTeam(s));
	}

	public Game getGame() {
		return game;
	}

	public Team getTeam() {
		return team;
	}
	
	public void setTeam(Team team) {
		this.team = team;
	}
}
