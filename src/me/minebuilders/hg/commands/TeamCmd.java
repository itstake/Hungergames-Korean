package me.minebuilders.hg.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.minebuilders.hg.Config;
import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.PlayerData;
import me.minebuilders.hg.Team;
import me.minebuilders.hg.Util;

public class TeamCmd extends BaseCmd {

	public TeamCmd() {
		forcePlayer = true;
		cmdName = "team";
		forceInGame = true;
		argLength = 2;
		usage = "<&cinvite&b/&caccept&b>";
	}

	@Override
	public boolean run() {
		PlayerData pd = HG.plugin.players.get(player.getName());
		Game g = pd.getGame();

		if (args[1].equalsIgnoreCase("invite")) {

			if (args.length >= 3) {

				Player p = Bukkit.getPlayer(args[2]);

				if (p == null || !g.getPlayers().contains(p.getName())) {
					Util.msg(player, "&c" + args[2] + " Is not available!");
					return true;
				}

				if (pd.getTeam() != null) {

					Team t = pd.getTeam();

					if (!t.getLeader().equalsIgnoreCase(player.getName())) {
						Util.msg(player, "&cOnly the leader can invite other players!");
						return true;
					}
					if (t.isOnTeam(p.getName())) {
						Util.msg(player, "&c" + args[2] + " &3is already on a team!");
						return true;
					}

					if ((t.getPlayers().size() + t.getPenders().size()) >= Config.maxTeam) {
						Util.msg(player, "&cYou've hit the max team limit!");
						return true;
					}

					HG.plugin.players.get(p.getName()).setTeam(t);
					t.invite(p);
					Util.msg(player, "&c" + p.getName() + " &3Has been invited!");
					return true;
				}

				if (HG.plugin.players.get(p.getName()).isOnTeam(p.getName())) {
					Util.msg(player, "&c" + args[2] + " &3is already on a team!");
					return true;
				}

				Team t = new Team(player.getName());
				HG.plugin.players.get(p.getName()).setTeam(t);
				pd.setTeam(t);
				t.invite(p);
				Util.msg(player, "&c" + p.getName() + " &3Has been invited!");
				return true;
			} else {
				Util.msg(player, "&cWrong Usage: &3/hg &bteam invite <&cname&b>");
			}
		} else if (args[1].equalsIgnoreCase("accept")) {

			Team t = HG.plugin.players.get(player.getName()).getTeam();

			if (t == null) {
				Util.msg(player, "&cYou have no pending invites...");
				return true;
			}
			if (t.getPenders().contains(player.getName())) {

				t.acceptInvite(player);
				for (String s : t.getPlayers()) {
					Player p = Bukkit.getPlayer(s);

					if (p != null) {
						Util.scm(p, "&6*&b&m                                                                             &6*");
						Util.scm(p, ChatColor.WHITE + player.getName() + " &3Just joined your team!");
						Util.scm(p, "&6*&b&m                                                                             &6*");
					}
					return true;
				}

				return true;
			}
		} else {
			Util.scm(player, "&c" + args[1] + " is not a valid command!");
			Util.scm(player, "&cValid arguments: &6invite&c, &6accept ");
		}
		return true;
	}
}