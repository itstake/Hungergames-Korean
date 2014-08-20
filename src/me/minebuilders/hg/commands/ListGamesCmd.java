package me.minebuilders.hg.commands;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

public class ListGamesCmd extends BaseCmd {

	public ListGamesCmd() {
		forcePlayer = false;
		cmdName = "listgames";
		forceInGame = false;
		argLength = 1;
	}

	@Override
	public boolean run() {
		Util.scm(sender, "&3&l Games:");
		for (Game g : HG.plugin.games) {
			Util.scm(sender, " &4 - &3" + g.getName() + "&4:&3" + g.getStatus().getName());
		}
		return true;
	}
}