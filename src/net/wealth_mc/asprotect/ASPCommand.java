package net.wealth_mc.asprotect;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ASPCommand implements CommandExecutor {

	private ASProtect plg;

	public ASPCommand(ASProtect plg) {
		this.setPlg(plg);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("reload")) {
				return execCmdReload(sender, args);
			}
		}
		return false;
	}

	private boolean execCmdReload(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!player.hasPermission(ASProtect.PERM_admin)) {
				player.sendMessage(ASProtect.tag + ChatColor.DARK_RED + " нет прав на эту команду");
				return true;
			}
			plg.reloadConfiguration();
			player.sendMessage(ASProtect.tag + ChatColor.DARK_RED + " конфигурация перезагружена");
			ASProtect.log.info("конфигурация перезагружена");
			return true;
		}else {
			plg.reloadConfiguration();
			ASProtect.log.info("конфигурация перезагружена");
			return true;
		}
	}

	public void setPlg(ASProtect plg) {
		this.plg = plg;
	}
}
