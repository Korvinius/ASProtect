package net.wealth_mc.asprotect;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

public class ASPVault {
	private static boolean vault_perm = false;
	private static Permission permission = null;
	

	public static void init() {
        if (checkVault()){
            vault_perm = setupPermissions();            
        }else{
        	ASProtect.log.info("Плагин Vault не обнаружен!");
        }
    }
    private static boolean checkVault(){
        Plugin vplg = Bukkit.getServer().getPluginManager().getPlugin("Vault");
        return  ((vplg != null)&&(vplg instanceof Vault));
    }
    private static boolean setupPermissions(){
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
	
	public static boolean isPermissionConected(){
        return vault_perm;
    }
	
	public static String getPrimaryGroup (Player p){
		if (!isPermissionConected()) return "player";
		return permission.getPrimaryGroup(p);
	}

}
