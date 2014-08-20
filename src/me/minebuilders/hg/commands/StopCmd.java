package me.minebuilders.hg.commands;

import org.bukkit.ChatColor;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

public class StopCmd extends BaseCmd {

	public StopCmd() {
		forcePlayer = false;
		cmdName = "stop";
		forceInGame = false;
		argLength = 2;
		usage = "<&cgame&b>";
	}

	@Override
	public boolean run() {
		Game g = HG.manager.getGame(args[1]);
		if (g != null) {
			g.stop();
			Util.scm(sender, "&3" + args[1] + "&b Has been stopped!");
		} else {
			sender.sendMessage(ChatColor.RED + "This game does not exist!");
		}
		return true;
	}
}