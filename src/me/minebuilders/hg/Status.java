package me.minebuilders.hg;


import org.bukkit.ChatColor;

public enum Status {
	RUNNING(ChatColor.GREEN  + "" + ChatColor.BOLD +  "게임중"), STOPPED(ChatColor.DARK_RED  + "" + ChatColor.BOLD +  "시작하지 않음"),
	WAITING(ChatColor.AQUA  + "" + ChatColor.BOLD +  "사람을 기다리는 중..."), BROKEN(ChatColor.DARK_RED  + "" + ChatColor.BOLD +  "오류"), 
	ROLLBACK(ChatColor.RED  + "" + ChatColor.BOLD +  "롤백중..."), NOTREADY(ChatColor.DARK_BLUE  + "" + ChatColor.BOLD +  "준비되지 않음"), 
	BEGINNING(ChatColor.GREEN  + "" + ChatColor.BOLD +  "게임중"), COUNTDOWN(ChatColor.AQUA  + "" + ChatColor.BOLD +  "시작중...");

	private String name;

	Status(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
