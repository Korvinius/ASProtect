package net.wealth_mc.asprotect;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class ASPRunnRclick implements Runnable {

	private ASPLocation loc;
	private Player player;
	private String name;
	private Thread thread;
	
	public ASPRunnRclick(PlayerInteractEvent e) {
		int x = e.getClickedBlock().getLocation().getBlockX();
		int y = e.getClickedBlock().getLocation().getBlockY() + 1;
		int z = e.getClickedBlock().getLocation().getBlockZ();
		this.loc = new ASPLocation(e.getClickedBlock().getLocation().getWorld(), x,y,z);
		this.player = e.getPlayer();
		this.name = loc.toString();
		thread = new Thread(this, "Thread-" + player.getName() + ": " + name.hashCode());
		thread.start();
	}

	@Override
	public void run() {
		Material hand = player.getItemInHand().getType();
		if (hand != Material.AIR && hand != Material.ARMOR_STAND) return;
		ASProtect.isPlayerStand(name, player, true);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ASProtect.isPlayerStand(name, null, false);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

}
