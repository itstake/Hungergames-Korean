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
		Util.msg(player, "이 미니게임에서 나가셨습니다.");
		return true;
	}
}
