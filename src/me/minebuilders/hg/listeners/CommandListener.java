package me.minebuilders.hg.listeners;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;
import me.minebuilders.hg.commands.BaseCmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandListener implements CommandExecutor {
	
	private final HG p;

	public CommandListener(HG plugin) {
		this.p = plugin;
	}

	public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
		if (args.length == 0 || !p.cmds.containsKey(args[0])) {
			 Util.scm(s, "&6*&1&m                           &6*( &3Hungergames &6)*&1&m                           &6*");
			for (BaseCmd cmd : p.cmds.values().toArray(new BaseCmd[0])) {
				if (s.hasPermission("hg." + cmd.cmdName)) Util.scm(s, "  &4- " + cmd.sendHelpLine());
			}
			Util.scm(s, "&6*&1&m                                                                             &6*");
		} else p.cmds.get(args[0]).processCmd(p, s, args);
		return true;
	}
}