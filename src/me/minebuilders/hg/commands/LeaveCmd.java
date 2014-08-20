package me.minebuilders.hg.commands;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

public class LeaveCmd extends BaseCmd {

	public LeaveCmd() {
		forcePlayer = true;
		cmdName = "leave";
		forceInGame = true;
		argLength = 1;
	}

	@Override
	public boolean run() {
		HG.plugin.players.get(player.getName()).getGame().leave(player);
		Util.msg(player, "&cYou left Hungergames!");
		return true;
	}
}