package net.wealth_mc.asprotect;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ASPRunnRmv implements Runnable {

	private Player player;
	private ASPLocation loc;
	private Thread thread;
	
	public ASPRunnRmv(Player p, ASPLocation loc) {
		this.player = p;
		this.loc = loc;
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		if (!player.hasPermission(ASProtect.PERM_unprotect)
				&& !player.hasPermission(ASProtect.PERM_admin)) {
			player.sendMessage(ASProtect.tag + ChatColor.DARK_RED + " вы не можете снимать защиту из стоек,"
					+ "нет прав.");
			return;
		}
		
		Map<String, Object> stands = ASProtect.getStands();
		for (Entry<String, Object> entry : stands.entrySet()) {
			if (entry.getKey().equals(loc.toString())) {
				if (entry.getValue().equals(player.getName().toLowerCase()) 
						|| player.hasPermission(ASProtect.PERM_admin)) {

					new ASPRunnProtect(loc, null, false);
					player.sendMessage(ASProtect.tag + ChatColor.DARK_RED + " вы сняли защиту с этой стойки");
                	return;
				}else{
					player.sendMessage(ASProtect.tag + ChatColor.DARK_RED 
            				+ " у вас нет прав на снятие защиты с этой стойки");
					return;
				}
			}
		}
		player.sendMessage(ASProtect.tag + ChatColor.DARK_RED 
				+ " эта стойка не защищена");
	}
}
