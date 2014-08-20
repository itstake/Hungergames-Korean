package me.minebuilders.hg;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class Team {

	private String leader;
	private List<String> players = new ArrayList<String>();
	private List<String> pending = new ArrayList<String>();
	
	public Team(String leader) {
		this.leader = leader;
		players.add(leader);
	}
	
	
	public void invite(Player p) {
		Util.scm(p, "&6*&b&m                                                                             &6*");
		Util.scm(p, "| &f" + leader + " &3Just invited you to a &fTeam&3!");
		Util.scm(p, "| &3Type &f/hg team accept &3To join!");
		Util.scm(p, "&6*&b&m                                                                             &6*");
		pending.add(p.getName());
	}
	
	public void acceptInvite(Player p) {
		pending.remove(p.getName());
		players.add(p.getName());
		Util.msg(p, "&3You successfully joined this team!");
	}
	
	public boolean isOnTeam(String p) {
		return (players.contains(p));
	}
	
	public boolean isPending(String p) {
		return (pending.contains(p));
	}
	
	public List<String> getPlayers() {
		return players;
	}
	
	public List<String> getPenders() {
		return pending;
	}
	
	public String getLeader() {
		return leader;
	}
}
