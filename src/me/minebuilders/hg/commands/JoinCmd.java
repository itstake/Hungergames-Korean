package me.minebuilders.hg.commands;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

public class JoinCmd extends BaseCmd {

	public JoinCmd() {
		forcePlayer = true;
		cmdName = "join";
		forceInGame = false;
		argLength = 2;
		usage = "<아레나 이름>";
	}

	@Override
	public boolean run() {

		if (HG.plugin.players.containsKey(player.getName())) {
			Util.msg(player, "&c당신은 이미 게임중입니다!");
		} else {
			Game g = HG.manager.getGame(args[1]);
			if (g != null && !g.getPlayers().contains(player.getName())) {
				g.join(player);
			} else {
				Util.msg(player, "&c그 미니게임은 존재하지 않습니다!");
			}
		}
		return true;
	}
}
