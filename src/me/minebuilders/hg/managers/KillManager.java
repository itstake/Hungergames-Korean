package me.minebuilders.hg.managers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class KillManager {

	public String getDeathString(DamageCause dc, String name) {
		switch (dc) {
		case ENTITY_EXPLOSION: return (name + " 님이 터져 죽었습니다!");
		case BLOCK_EXPLOSION: return (name + " 님이 터져 죽었습니다!");
		case CUSTOM: return (name + " 알수없는 이유로 죽었습니다!");
		case FALL: return (name + " 땅에 떨어져 죽었습니다!");
		case FALLING_BLOCK: return (name + " 님이 떨어지는 블록에 맞아 죽었습니다!");
		case FIRE: return (name + " 님이 타 죽었습니다!");
		case FIRE_TICK: return (name + " 님이 타 죽었습니다!");
		case PROJECTILE: return (name + " 님이 던진것에 맞아 죽었습니다!");
		case LAVA: return (name + " 용암에 빠져 죽었습니다!");
		case MAGIC: return (name + " 마법으로 인해 죽었습니다!");
		case SUICIDE: return (name + " 자살했습니다!");
		default: return (name + " 님이 " + dc.toString().toLowerCase() + "님에게 죽었습니다!");
		}
	}

	public String getKillString(String name, Entity e) {
		switch (e.getType()) {
		case PLAYER: return (name + " 님이 " + ((Player)e).getName() + " 님에게 " + ((Player)e).getItemInHand().getType().name().toLowerCase() + " 으로 죽었습니다!");
		case ZOMBIE: return (name + " 님이 좀비에게 죽었습니다!");
		case SKELETON: return (name + " 님이 스켈레톤에게 죽었습니다");
		case ARROW: return (name + " 님이 스켈레톤에게 맞아 죽었습니다");
		case SPIDER: return (name + " 님이 거미에게 맞아 죽었습니다!");
		default: return (name + " 님이 죽었습니다!");
		}
	}
}
