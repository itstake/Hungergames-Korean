package me.minebuilders.hg.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

public class SetLobbyWallCmd extends BaseCmd {

	public SetLobbyWallCmd() {
		forcePlayer = true;
		cmdName = "setlobbywall";
		forceInGame = false;
		argLength = 2;
		usage = "<&carena-name&b>";
	}

	@Override
	public boolean run() {
		Game g = HG.manager.getGame(args[1]);
		if (g != null) {
			@SuppressWarnings("deprecation")
			Block b = player.getTargetBlock(null, 6);
			if (b.getType() == Material.WALL_SIGN && g.setLobbyBlock((Sign)b.getState())) {
				Location l = b.getLocation();
				HG.arenaconfig.getCustomConfig().set(("arenas." + args[1] + "." + "lobbysign"), (l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ()));
				HG.arenaconfig.saveCustomConfig();
				Util.msg(player, "&aThe lobbyWallSign has been set!");
				HG.manager.checkGame(g, player);
			} else {
				Util.msg(player, "&cThese signs aren't in correct format!");
				Util.msg(player, "&cformat: &6[sign] &c[sign] [sign]");
			}
		} else {
			player.sendMessage("This arena does not exist!");
		}
		return true;
	}
}