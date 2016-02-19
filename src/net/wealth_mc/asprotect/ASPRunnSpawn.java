package net.wealth_mc.asprotect;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class ASPRunnSpawn implements Runnable {

	private String name;
	private CreatureSpawnEvent event;
	private Player player;
	private Thread thread;
	private ASPLocation loc;
	
	public ASPRunnSpawn(CreatureSpawnEvent e, Player p) {
		this.event = e;
		this.player = p;
		Location location = event.getEntity().getLocation();
		this.loc = new ASPLocation(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
		this.name = loc.toString(); 
		thread = new Thread(this, "Thread AS:" + name.hashCode());
		thread.start();
	}



	@Override
	public void run() {
		player = checkPlayerIsNull(name);
		if (player == null) {
			try {
				for (int i = 5; i>0; i--) {
					Thread.sleep(10);
					player = checkPlayerIsNull(name);
					if (player != null) break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (player == null) return;
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<Entity> nearEntities = player.getNearbyEntities(5, 5, 5);

		for (Entity entity : nearEntities) {
            if (entity instanceof ArmorStand) {
            	Location location = entity.getLocation();
            	ASPLocation loc2 = new ASPLocation(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
            	if (loc.equals(loc2)) {
            		if (!ASProtect.checkCroupStands(player)
            				&& !player.hasPermission(ASProtect.PERM_admin)) {
            			player.sendMessage(ASProtect.tag + ChatColor.DARK_AQUA 
            					+ " стойка не защищена, "
            					+ "вы уже установили максимально-возможное, для вашего статуса, "
            					+ "количество защищенных стоек");
            			return;
            		}
            		if (!player.hasPermission(ASProtect.PERM_protect)) {
            			player.sendMessage(ASProtect.tag + ChatColor.DARK_AQUA 
            					+ " стойка не защищена, "
            					+ "у вас нет прав на защиту стоек");
            			return;
            		}
            		ASProtect.addOrRemoveProtectArmorStand(loc, player, true);
            		player.sendMessage(ASProtect.tag + ChatColor.DARK_AQUA 
            				+ " вы установили защищенную стойку для брони");
            	}
            }
		}
		
	}
	private Player checkPlayerIsNull(String n) {
		Map<String, Player> map = ASProtect.getIsPlayerStand();
		Player p = null;
		for (Entry<String, Player> entry : map.entrySet()) {
			if (entry.getKey().equals(n)) {
				p = entry.getValue();
				break;
			}
		}
		return p;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
}
