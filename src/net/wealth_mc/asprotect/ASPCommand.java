package net.wealth_mc.asprotect;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ASPCommand implements CommandExecutor {

	private ASProtect plg;

	public ASPCommand(ASProtect plg) {
		this.setPlg(plg);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(cmd.getName().equalsIgnoreCase("asprotect")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("protect") || args[0].equalsIgnoreCase("add")) {
					return execCmdAdd(sender, args);
				}else if (args[0].equalsIgnoreCase("unprotect") || args[0].equalsIgnoreCase("remove")) {
					return execCmdRemove(sender, args);
				}else if (args[0].equalsIgnoreCase("reload")) {
					return execCmdReload(sender, args);
				}
			}
		}
		return false;
	}

	private boolean execCmdReload(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!player.hasPermission(ASProtect.PERM_admin)) {
				player.sendMessage(ASProtect.TAG + ChatColor.DARK_RED + " нет прав на эту команду");
				return true;
			}
			plg.reloadConfiguration();
			player.sendMessage(ASProtect.TAG + ChatColor.DARK_RED + " конфигурация перезагружена");
			ASProtect.log.info("конфигурация перезагружена");
			return true;
		}else {
			plg.reloadConfiguration();
			ASProtect.log.info("конфигурация перезагружена");
			return true;
		}
	}
		
	private boolean execCmdAdd(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!player.hasPermission(ASProtect.PERM_protect) 
					|| !player.hasPermission(ASProtect.PERM_admin)) {
				player.sendMessage(ASProtect.TAG + ChatColor.DARK_RED + " нет прав на эту команду");
				return true;
			}
			Location location = player.getEyeLocation();
			ASPLocation loc = new ASPLocation(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
			Map<String, Object> stands = ASProtect.getStands();
			for (Entry<String, Object> entry : stands.entrySet()) {
				if (entry.getKey().equals(loc.toString())) {
					player.sendMessage(ASProtect.TAG + ChatColor.DARK_RED + " эта стойка уже защищена на имя: "
				+ entry.getValue());
					return true;
				}
			}
			List<Entity> nearEntities = player.getNearbyEntities(5, 5, 5);

			for (Entity entity : nearEntities) {
                if (entity instanceof ArmorStand) {
                	Location location2 = entity.getLocation();
                	ASPLocation loc2 = new ASPLocation(location2.getWorld(), location2.getBlockX(), location2.getBlockY(), location2.getBlockZ());
                	if (loc.equals(loc2));
                	if (!ASProtect.checkCroupStands(player)
            				&& (!player.hasPermission(ASProtect.PERM_admin) 
            						|| !player.hasPermission(ASProtect.PERM_ignore))) {
            			player.sendMessage(ASProtect.TAG + ChatColor.DARK_RED 
            					+ " стойка не защищена, "
            					+ "вы уже установили максимально-возможное, для вашего статуса, "
            					+ "количество защищенных стоек");
            			return true;
            		}
                	new ASPRunnProtect(loc, player, true);
                	return true;
                }

			}
		}
		return false;
	}

	private boolean execCmdRemove(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!player.hasPermission(ASProtect.PERM_unprotect)
					|| !player.hasPermission(ASProtect.PERM_admin)) {
				player.sendMessage(ASProtect.TAG + ChatColor.DARK_RED + " нет прав на эту команду");
				return true;
			}
			Location location = player.getEyeLocation();
			ASPLocation loc = new ASPLocation(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
			ASProtect.log.info(loc.toString());
			ASProtect.printProtectArmorStand();
			ASPLocation loc2 = new ASPLocation(location.getWorld(),0,0,0);
			Map<String, Object> stands = ASProtect.getStands();
			for (Entry<String, Object> entry : stands.entrySet()) {
				if (entry.getKey().equals(loc.toString())) {
					ASProtect.log.info(entry.getKey() + " " + entry.getValue());
					if (entry.getValue().equals(player.getName().toLowerCase()) 
							|| player.hasPermission(ASProtect.PERM_admin)) {
						ASProtect.log.info(player.getName() + " entry.getValue().equals(player.getName().toLowerCase(): " 
							+ entry.getValue().equals(player.getName().toLowerCase())
								+ " player.hasPermission(ASProtect.PERM_admin: " 
								+ player.hasPermission(ASProtect.PERM_admin));

						List<Entity> nearEntities = player.getNearbyEntities(5, 5, 5);
						for (Entity entity : nearEntities) {
							ASProtect.log.info(nearEntities.toString());
							if (entity instanceof ArmorStand) {
			                	Location location2 = entity.getLocation();
			                	loc2 = new ASPLocation(location2.getWorld(), location2.getBlockX(), location2.getBlockY(), location2.getBlockZ());
			                	if (loc.equals(loc2)) {
			                		new ASPRunnProtect(loc, null, false);
									player.sendMessage(ASProtect.TAG + ChatColor.DARK_RED + " вы сняли защиту с этой стойки");
				                	return true;
			                	}else {
			                		player.sendMessage(ASProtect.TAG + ChatColor.DARK_RED 
			                				+ " вы не смотрите на стойку для брони, или расстояние до нее слишком большое");
				                	return true;
			                	}
							}else {
		                		player.sendMessage(ASProtect.TAG + ChatColor.DARK_RED 
		                				+ " вы не смотрите на стойку для брони, или расстояние до нее слишком большое");
			                	return true;
							}
						}
					}else{
						player.sendMessage(ASProtect.TAG + ChatColor.DARK_RED 
	            				+ " у вас нет прав на снятие защиты с этой стойки");
	                	return true;
					}
				}
			}
			player.sendMessage(ASProtect.TAG + ChatColor.DARK_RED
					+ " эта стойка не защищена");
			player.sendMessage(ASProtect.TAG + ChatColor.DARK_RED + loc.toString());
			player.sendMessage(ASProtect.TAG + ChatColor.DARK_RED + loc2.toString());
			return true;
		}
		return false;
	}

	public void setPlg(ASProtect plg) {
		this.plg = plg;
	}
}
