package net.wealth_mc.asprotect;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ASPListener implements Listener {

	public ASPListener(ASProtect aSProtect) {
	}

	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event){
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getBlockFace() != BlockFace.UP) return;
		new ASPRunnRclick(event);
	}
	
	@EventHandler
	public void onCreatureSpawnEvent(CreatureSpawnEvent event){
		if (event.getEntity() instanceof ArmorStand) {
			new ASPRunnSpawn(event, null);
		}
	}
	
	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof ArmorStand)) return;
		checkDamageArmorStand(event);
	}
	
	@EventHandler
	public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
		checkArmorStandManipulate(event);
	}

	private void checkArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
		Location loc = event.getRightClicked().getEyeLocation();
		Player player = event.getPlayer();
		String owner = null;
		ASPLocation asloc = new ASPLocation(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		
		Map<String, Object> stands = ASProtect.getStands();
		for (Entry<String, Object> entry : stands.entrySet()){
			if(entry.getKey().equals(asloc.toString())) {
				
				owner = (String) entry.getValue();
				if (owner.equals(player.getName().toLowerCase())) return;
				if (player.hasPermission(ASProtect.PERM_admin)) return;
			}
		}
		event.setCancelled(true);
	}

	private void checkDamageArmorStand(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		Player player = null;
		Location loc = entity.getLocation();
		if (event.getDamager() != null && event.getDamager() instanceof Player) {
			player = (Player) event.getDamager();
		}
		ASPLocation asloc = new ASPLocation(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if (ASProtect.checkArmorStandProtect(asloc, player)) {
			event.setCancelled(true);
			return;
		}	
	}
}
