package me.minebuilders.hg.commands;

import me.minebuilders.hg.HG;

public class DebugCmd extends BaseCmd {

	public DebugCmd() {
		forcePlayer = false;
		cmdName = "debug";
		forceInGame = false;
		argLength = 2;
		usage = "<&cgame&b>";
	}

	@Override
	public boolean run() {
			HG.manager.runDebugger(sender, args[1]);
		return true;
	}
}