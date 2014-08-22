package me.minebuilders.hg.listeners;

import me.minebuilders.hg.Config;
import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.PlayerData;
import me.minebuilders.hg.Status;
import me.minebuilders.hg.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class GameListener implements Listener {

	private HG plugin;
	private String tsn = ChatColor.GOLD + "플레이어 추적 막대기 " + ChatColor.GREEN + "사용법: ";
	private ItemStack trackingStick;

	public GameListener(HG plugin) {
		this.plugin = plugin;
		ItemStack it = new ItemStack(Material.STICK, 1);
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(tsn + Config.trackingstickuses);
		it.setItemMeta(im);
		trackingStick = it;
	}

	public void dropInv(Player p) {
		PlayerInventory inv = p.getInventory();
		Location l = p.getLocation();
		for (ItemStack i : inv.getContents()) {
			if (i != null && i.getType() != Material.AIR)
				l.getWorld().dropItemNaturally(l, i);
		}
		for (ItemStack i : inv.getArmorContents()) {
			if (i != null && i.getType() != Material.AIR)
				l.getWorld().dropItemNaturally(l, i);
		}
	}

	public void checkStick(Game g) {
		if (Config.playersfortrackingstick == g.getPlayers().size()) {
			for (String r : g.getPlayers()) {
				Player p = Bukkit.getPlayer(r);
				if (p != null) {
					Util.scm(p,"&a&l[]------------------------------------------[]");
					Util.scm(p, "&a&l |&3&l   플레이어-추적 막대를 얻으셨습니다! &a&l |");
					Util.scm(p, "&a&l |&3&l   스틱을 클릭해 플레이어들을 추적하세요!                &a&l |");
					Util.scm(p,"&a&l[]------------------------------------------[]");
					p.getInventory().addItem(trackingStick);
				}
			}
		}
	}
	
	@EventHandler
	public void onDIe(PlayerDeathEvent event) {
		final Player p = event.getEntity();

		PlayerData pd = plugin.players.get(p.getName());

		if (pd != null) {
			final Game g = pd.getGame();

			p.setHealth(20);

			LivingEntity killer = p.getKiller();

			if (killer != null) {
				g.msgDef(HG.killmanager.getKillString(p.getName(), killer));
			} else {
				g.msgDef(HG.killmanager.getDeathString(p.getLastDamageCause().getCause(), p.getName()));
			}
			event.setDeathMessage(null);
			
			event.getDrops().clear();
			
			dropInv(p);
			g.exit(p);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

				@Override
				public void run() {
					g.leave(p);
					checkStick(g);
				}
			}, 10L);
		}
	}

	@EventHandler
	public void onSprint(FoodLevelChangeEvent event) {
		Player p = (Player)event.getEntity();
		if (plugin.players.containsKey(p.getName())) {
			Status st = plugin.players.get(p.getName()).getGame().getStatus();
			if (st == Status.WAITING || st == Status.COUNTDOWN) {
			event.setFoodLevel(1);
			event.setCancelled(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void useTrackStick(Player p) {
		ItemStack i = p.getItemInHand();
		ItemMeta im = i.getItemMeta();
		if (im.getDisplayName() != null && im.getDisplayName().startsWith(tsn)) {
			int uses = 0;
			uses = Integer.parseInt(im.getDisplayName().replace(tsn, ""));
			if (uses == 0) {
				p.sendMessage(ChatColor.RED + "이 추적 막대기는 더이상 사용할 수 없습니다");
			} else {
				boolean foundno = true;
				for (Entity e : p.getNearbyEntities(120, 50, 120)) {
					if (e instanceof Player) {
						im.setDisplayName(tsn + (uses -1));
						foundno = false;
						Location l = e.getLocation();
						int range = (int) p.getLocation().distance(l);
						Util.msg(p, (((Player)e).getName()) + " 님이 당신으로부터 " + range + " 블록 떨어져 있습니다: " + getDirection(p.getLocation().getBlock(), l.getBlock()));
						i.setItemMeta(im);
						p.updateInventory();
						return;
					} 
				}
				if (foundno)
					Util.msg(p,"가까운 곳에 있는 플레이어를 찾을 수 없습니다!");

			}
		}
	}

	public String getDirection(Block block, Block block1) {
		Vector bv = block.getLocation().toVector();
		Vector bv2 = block1.getLocation().toVector();
		float y = (float) angle(bv.getX(), bv.getZ(), bv2.getX(), bv2.getZ());
		float cal = (y * 10);
		int c = (int) cal;
		if (c<=1 && c>=-1) {
			return "남쪽";
		} else if (c>-14 && c<-1) {
			return "남서쪽";
		} else if (c>=-17 && c<=-14) {
			return "서쪽";
		} else if (c>-29 && c<-17) {
			return "북서쪽";
		} else if (c>17 && c<29) {
			return "북동쪽";
		} else if (c<=17 && c>=14) {
			return "동쪽";
		} else if (c>1 && c<14) {
			return "남동쪽";
		}  else if (c<=29 && c>=-29) {
			return "북쪽";
		} else {
			return "알수없음";
		}
	}


	public double angle(double d, double e, double f, double g) {
		//Vector differences
		int x = (int) (f - d);
		int z = (int) (g - e);

		double yaw = Math.atan2(x, z);
		return yaw;
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = false)
	public void onAttack(EntityDamageByEntityEvent event) {
		Entity defender = event.getEntity();
		Entity damager = event.getDamager();
		
		if (damager instanceof Projectile) {
			damager = (Entity) ((Projectile)damager).getShooter();
		}
		
		if (defender instanceof Player && damager != null) {
			Player p = (Player)defender;
			PlayerData pd = plugin.players.get(p.getName());

			if (pd != null) {
				Game g = pd.getGame();

				if (g.getStatus() != Status.RUNNING) {
					event.setCancelled(true);
				} else if (pd.isOnTeam(p.getName()) && damager instanceof Player && pd.getTeam().isOnTeam(((Player)damager).getName())) {
					Util.scm(((Player)damager),p.getName() + " 님은 당신의 팀입니다!");
					event.setCancelled(true);
				} else if (event.isCancelled()) event.setCancelled(false);
			}
		}
	}

	@EventHandler
	public void onItemUseAttempt(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction() != Action.PHYSICAL && plugin.players.containsKey(p.getName())) {
			Status st = plugin.players.get(p.getName()).getGame().getStatus();
			if (st == Status.WAITING || st == Status.COUNTDOWN) {
				event.setCancelled(true);
				p.sendMessage("You cannot interact until the game has started!");
			}
		}
	}

	@EventHandler
	public void onPlayerClickLobby(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block b = event.getClickedBlock();
			if (b.getType().equals(Material.WALL_SIGN)) {
				Sign sign = (Sign) b.getState();
				if (sign.getLine(0).equals(ChatColor.AQUA + "[미니게임]")) {
					Game game = HG.manager.getGame(sign.getLine(2).substring(2));
					if (game == null) {
						Util.msg(p, ChatColor.RED + "그 미니게임은 존재하지 않습니다!");
						return;
					} else {
						if (p.getItemInHand().getType() == Material.AIR) {
							game.join(p);
						} else {
							Util.msg(p, ChatColor.RED + "손으로 표지판을 클릭하세요!");
						}
					}
				} 
			}
		} else if (event.getAction().equals(Action.LEFT_CLICK_AIR)) {
			if (p.getItemInHand().getType().equals(Material.STICK) && plugin.players.containsKey(p.getName())) {
				useTrackStick(p);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void blockPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		
		if (HG.manager.isInRegion(b.getLocation())) {
			
			if (Config.breakblocks && plugin.players.containsKey(p.getName())) {
				
				Game g = plugin.players.get(p.getName()).getGame();
				
				if (g.getStatus() == Status.RUNNING || g.getStatus() == Status.BEGINNING) {
					if (!Config.blocks.contains(b.getType().getId())) {
						p.sendMessage(ChatColor.RED + "당신은 이 블록을 부술 수 없습니다!");
						event.setCancelled(true);
						return;
					} else {
						g.recordBlockPlace(event.getBlockReplacedState());
						return;
					}
				} else {
					p.sendMessage(ChatColor.RED + "이 게임은 진행중이 아닙니다!");
					event.setCancelled(true);
					return;
				}
			} else if (p.hasPermission("hg.create") && HG.manager.getGame(b.getLocation()).getStatus() != Status.RUNNING) {
				if (b.getType() == Material.CHEST) {
					HG.manager.getGame(b.getLocation()).addChests(b.getLocation());
				}
			} else {
				event.setCancelled(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if (HG.manager.isInRegion(b.getLocation())) {
			if (Config.breakblocks && plugin.players.containsKey(p.getName())) {
				Game g = plugin.players.get(p.getName()).getGame();
				if (g.getStatus() == Status.RUNNING) {
					if (!Config.blocks.contains(b.getType().getId())) {
						p.sendMessage(ChatColor.RED + "당신은 이 블록을 부술 수 없습니다!");
						event.setCancelled(true);
						return;
					} else {
						g.recordBlockBreak(b);
						return;
					}
				} else {
					p.sendMessage(ChatColor.RED + "이 게임은 진행중이 아닙니다!");
					event.setCancelled(true);
					return;
				}
			} else if (p.hasPermission("hg.create") && HG.manager.getGame(b.getLocation()).getStatus() != Status.RUNNING) {
				return;
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		if (plugin.players.containsKey(p.getName()) && plugin.players.get(p.getName()).getGame().getStatus() == Status.WAITING) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onlogout(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (plugin.players.containsKey(player.getName())) {
			plugin.players.get(player.getName()).getGame().leave(player);
		}
	}
}
