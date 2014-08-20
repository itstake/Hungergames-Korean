package me.minebuilders.hg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import me.minebuilders.hg.mobhandler.Spawner;
import me.minebuilders.hg.tasks.ChestDropTask;
import me.minebuilders.hg.tasks.FreeRoamTask;
import me.minebuilders.hg.tasks.StartingTask;
import me.minebuilders.hg.tasks.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Game {

	private String name;
	private List<Location> spawns;
	private Bound b;
	private List<String> players = new ArrayList<String>();
	private ArrayList<Location> chests = new ArrayList<Location>();
	private List<BlockState> blocks = new ArrayList<BlockState>();
	private Location exit;
	private Status status;
	private int minplayers;
	private int maxplayers;
	private int time;
	private Sign s;
	private Sign s1;
	private Sign s2;
	private int roamtime;
	private SBDisplay sb;

	// Task ID's here!
	private Spawner spawner;
	private FreeRoamTask freeroam;
	private StartingTask starting;
	private TimerTask timer;
	private ChestDropTask chestdrop;

	public Game(String s, Bound bo, List<Location> spawns, Sign lobbysign, int timer, int minplayers, int maxplayers, int roam, boolean isready) {
		this.name = s;
		this.b = bo;
		this.spawns = spawns;
		this.s = lobbysign;
		this.time = timer;
		this.minplayers = minplayers;
		this.maxplayers = maxplayers;
		this.roamtime = roam;

		if (isready) status = Status.STOPPED;
		else status = Status.BROKEN;

		setChests();
		setLobbyBlock(lobbysign);

		sb = new SBDisplay(this);
	}

	public Game(String s, Bound c, int timer, int minplayers, int maxplayers, int roam) {
		this.name = s;
		this.time = timer;
		this.minplayers = minplayers;
		this.maxplayers = maxplayers;
		this.roamtime = roam;
		this.spawns = new ArrayList<Location>();
		this.b = c;
		status = Status.NOTREADY;
		setChests();
		sb = new SBDisplay(this);
	}

	public Bound getRegion() {
		return b;
	}

	public void forceRollback() {
		Collections.reverse(blocks);
		for (BlockState st : blocks) {
			st.update(true);
		}
	}

	public void setStatus(Status st) {
		this.status = st; 
		updateLobbyBlock();
	}

	public void addState(BlockState s) {
		if (s.getType() != Material.AIR) {
			blocks.add(s);
		}
	}
	
	public void recordBlockBreak(Block bl) {
		Block top = bl.getRelative(BlockFace.UP);

		if (!top.getType().isSolid() || !top.getType().isBlock()) {
			addState(bl.getRelative(BlockFace.UP).getState());
		}

		for (BlockFace bf : Util.faces) {
			Block rel = bl.getRelative(bf);

			if (Util.isAttached(bl, rel)) {
				addState(rel.getState());
			}
		}
		addState(bl.getState());
	}

	public void recordBlockPlace(BlockState bs) {
		blocks.add(bs);
	}

	public Status getStatus() {
		return this.status;
	}

	public List<BlockState> getBlocks() {
		Collections.reverse(blocks);
		return blocks;
	}

	public void resetBlocks() {
		this.blocks.clear();
	}

	public void setChests() {
		chests.clear();
		for (Location bl : b.getBlocks(Material.CHEST)) {
			chests.add(bl);
		}
	}

	public void msgAllMulti(String[] sta) {
		for (String s : sta) {
			for (String st : players) {
				Player p = Bukkit.getPlayer(st);
				if (p != null)
					Util.msg(p, s);
			}
		}
	}

	public List<String> getPlayers() {
		return players;
	}

	public String getName() {
		return this.name;
	}

	public boolean isInRegion(Location l) {
		return b.isInRegion(l);
	}

	public List<Location> getSpawns() {
		return spawns;
	}

	public int getRoamTime() {
		return this.roamtime;
	}

	public void join(Player p) {
		if (status != Status.WAITING && status != Status.STOPPED && status != Status.COUNTDOWN) {
			p.sendMessage(ChatColor.RED + "�� �Ʒ����� ���� �غ���� �ʾҽ��ϴ�! ���߿� �ٽ� ���� �ּ���!");
		} else if (maxplayers <= players.size()) {
			p.sendMessage(ChatColor.RED + name + " �� �� á���ϴ�!");
		} else {
			if (p.isInsideVehicle()) {
				p.leaveVehicle();
			}
			players.add(p.getName());
			HG.plugin.players.put(p.getName(), new PlayerData(p, this));
			p.teleport(pickSpawn());
			heal(p);
			freeze(p);
			if (players.size() >= minplayers && status.equals(Status.WAITING)) {
				startPreGame();
			} else if (status == Status.WAITING) {
				msgDef("&4(&3"+p.getName() + "&b Has joined the game"+(minplayers-players.size()<= 0?"!":": "+(minplayers-players.size())+" players to start!")+"&4)");
			}
			kitHelp(p);
			if (players.size() == 1)
				status = Status.WAITING;
			updateLobbyBlock();
			sb.setSB(p);
			sb.setAlive();
		}
	}

	public void kitHelp(Player p) {
		String kit = HG.plugin.kit.getKitList();
		Util.scm(p, "&8     ");
		Util.scm(p, "&9&l>----------[&b&lWelcome to HungerGames&9&l]----------<");
		Util.scm(p, "&9&l - &bPick a kit using &c/hg kit <kit-name>");
		Util.scm(p, "&9&lKits:&b" + kit);
		Util.scm(p, "&9&l>------------------------------------------<");
	}

	public void respawnAll() {
		for (String st : players) {
			Player p = Bukkit.getPlayer(st);
			if (p != null)
				p.teleport(pickSpawn());
		}
	}

	public void startPreGame() {
		setStatus(Status.COUNTDOWN);
		starting = new StartingTask(this);
		updateLobbyBlock();
	}

	public void startFreeRoam() {
		status = Status.BEGINNING;
		HG.manager.restoreChests(this);
		b.removeEntities();
		freeroam = new FreeRoamTask(this);
	}

	public void startGame() {
		status = Status.RUNNING;
		if (Config.spawnmobs) spawner = new Spawner(this, Config.spawnmobsinterval);
		if (Config.randomChest) chestdrop = new ChestDropTask(this);
		timer = new TimerTask(this, time);
		updateLobbyBlock();
	}


	public void addSpawn(Location l) {
		this.spawns.add(l);
	}

	public Location pickSpawn() {
		int spawn = players.size() - 1;
		if (containsPlayer(spawns.get(spawn))) {
			for (Location l : spawns) {
				if (!containsPlayer(l)) {
					return l;
				}
			}
		}
		return spawns.get(spawn);
	}

	public boolean containsPlayer(Location l) {
		if (l == null) return false;

		for (String s : players) {
			Player p = Bukkit.getPlayer(s);
			if (p != null && p.getLocation().getBlock().equals(l.getBlock()))
				return true;
		}
		return false;
	}

	public void msgAll(String s) {
		for (String st : players) {
			Player p = Bukkit.getPlayer(st);
			if (p != null)
				Util.msg(p, s);
		}
	}

	public void msgDef(String s) {
		for (String st : players) {
			Player p = Bukkit.getPlayer(st);
			if (p != null)
				Util.scm(p, s);
		}
	}

	public void updateLobbyBlock() {
		s1.setLine(1, status.getName());
		s2.setLine(1, ChatColor.BOLD + "" + players.size() + "/" + maxplayers);
		s1.update(true);
		s2.update(true);
	}

	public void heal(Player p) {
		for (PotionEffect ef : p.getActivePotionEffects()) {
			p.removePotionEffect(ef.getType());
		}
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setFireTicks(0);
	}

	public void freeze(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 23423525, -10));
		p.setWalkSpeed(0.0001F);
		p.setFoodLevel(1);
		p.setAllowFlight(false);
		p.setFlying(false);
		p.setGameMode(GameMode.SURVIVAL);
	}

	public void unFreeze(Player p) {
		p.removePotionEffect(PotionEffectType.JUMP);
		p.setWalkSpeed(0.2F);
	}

	public boolean setLobbyBlock(Sign sign) {
		try {
			this.s = sign;
			Block c = s.getBlock();
			@SuppressWarnings("deprecation")
			BlockFace face = Util.getSignFace(c.getData());
			this.s1 = (Sign) c.getRelative(face).getState();
			this.s2 = (Sign) s1.getBlock().getRelative(face).getState();

			s.setLine(0, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "HungerGames");
			s.setLine(1, ChatColor.BOLD + name);
			s.setLine(2, ChatColor.BOLD + "Click To Join");
			s1.setLine(0, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Game Status");
			s1.setLine(1, status.getName());
			s2.setLine(0, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Alive");
			s2.setLine(1, ChatColor.BOLD + "" + 0 + "/" + maxplayers);
			s.update(true);
			s1.update(true);
			s2.update(true);
		} catch (Exception e) { return false; }
		try {
			String[] h = HG.plugin.getConfig().getString("settings.globalexit").split(":");
			this.exit = new Location(Bukkit.getServer().getWorld(h[0]), Integer.parseInt(h[1]) + 0.5, Integer.parseInt(h[2]) + 0.1, Integer.parseInt(h[3]) + 0.5, Float.parseFloat(h[4]), Float.parseFloat(h[5]));
		} catch (Exception e) {
			this.exit = s.getWorld().getSpawnLocation();
		}
		return true;
	}

	public void setExit(Location l) {
		this.exit = l;
	}

	public void cancelTasks() {
		if (spawner != null) spawner.stop();
		if (timer != null) timer.stop();
		if (starting != null) starting.stop();
		if (freeroam != null) freeroam.stop();
		if (chestdrop != null) chestdrop.shutdown();
	}

	public void stop() {
		List<String> win = new ArrayList<String>();
		cancelTasks();
		for (String s : players) {
			Player p = Bukkit.getPlayer(s);
			if (p != null) {
				heal(p);
				exit(p);
				HG.plugin.players.get(p.getName()).restore(p);
				HG.plugin.players.remove(p.getName());
				win.add(p.getName());
				sb.restoreSB(p);
			}
		}
		players.clear();

		if (!win.isEmpty() && Config.giveReward) {
			double db = Config.cash / win.size();

			for (String s : win) {
				Vault.economy.depositPlayer(s, db);
				Player p = Bukkit.getPlayer(s);
				if (p != null)
				Util.msg(p, "&aYou won " + db + " for winning HungerGames!");
			}
		}

		Util.broadcast("&l&3" + Util.translateStop(win) + " &l&bWon HungerGames at arena " + name + "!");
		if (!blocks.isEmpty()) {
			new Rollback(this);
		} else {
			status = Status.STOPPED;
			updateLobbyBlock();
		}
		b.removeEntities();
		sb.resetAlive();
	}

	public void leave(Player p) {
		players.remove(p.getName());
		unFreeze(p);
		heal(p);
		exit(p);
		HG.plugin.players.get(p.getName()).restore(p);
		HG.plugin.players.remove(p.getName());
		if (status == Status.RUNNING || status == Status.BEGINNING) {
			if (isGameOver()) {
				stop();
			}
		} else if (status == Status.WAITING) {
			msgDef("&3&l"+p.getName() + "&l&c Has left the game"+(minplayers-players.size()<= 0?"!":": "+(minplayers-players.size())+" players to start!"));
		}
		updateLobbyBlock();
		sb.restoreSB(p);
		sb.setAlive();
	}

	public boolean isGameOver() {
		if (players.size() <= 1) return true; 
		for (Entry<String, PlayerData> f : HG.plugin.players.entrySet()) {

			Team t = f.getValue().getTeam();

			if (t != null && (t.getPlayers().size() >= players.size())) {
				List<String> ps = t.getPlayers();
				for (String s : players) {
					if (!ps.contains(s)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	public void addChests(Location b) {
		chests.add(b);
	}

	public ArrayList<Location> getChests() {
		return chests;
	}

	public void exit(Player p) {
		Util.clearInv(p);
		if (this.exit == null) {
			p.teleport(s.getWorld().getSpawnLocation());
		} else {
			p.teleport(this.exit);
		}
	}

	public int getMaxPlayers() {
		return maxplayers;
	}

	public boolean isLobbyValid() {
		try {
			if (s instanceof Sign && s1 instanceof Sign && s2 instanceof Sign) {
				return true;
			}
		} catch (Exception e) { 
			return false;
		}
		return false;
	}
}
