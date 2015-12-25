package net.wealth_mc.asprotect;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ASProtect extends JavaPlugin {

	public static ASProtect instance;
	public FileConfiguration config;
	private ASPCommand asprotect;
	
	public static Logger log;
	public static String protectmessage;
	public static Integer defaultgroup;
	public static File fileAS;
	private static Map<String, Object> stands = new HashMap<String, Object>();
	private static Map<String, Player> isplayerstand = new HashMap<String, Player>();
	public static Map<String, Object> groups = new HashMap<String, Object>();
	public static final String PERM_admin     = "asprotect.admin";
	public static final String PERM_ignore    = "asprotect.ignore";
	public static final String PERM_user      = "asprotect.user";
	public static final String PERM_unlimite  = "asprotect.unlimite";
	public static final String PERM_protect   = "asprotect.cmd.protect";
	public static final String PERM_unprotect = "asprotect.cmd.unprotect";
	public static final String PERM_info      = "asprotect.cmd.info";
	public static final String TAG = ChatColor.DARK_RED + "[" + ChatColor.GOLD + "ASprotect" 
			+ ChatColor.DARK_RED + "] " + ChatColor.RESET;
	
	@Override
	public void onEnable() {
		instance = this;
		this.getConfig().options().copyDefaults(true).copyHeader(true);
		this.saveDefaultConfig();
		log = this.getLogger();
		fileAS = new File (instance.getDataFolder()+File.separator + "ArmorStand.yml");
		log.info(fileAS.toString());
		
		config = this.getConfig();
		protectmessage = config.getString("protect.message");
		groups = config.getConfigurationSection("group").getValues(false);
		defaultgroup = stringToInt(config.getString("group.default"));
		
		saveDefaultProtectArmorStand();
		loadProtectArmorStand();
		
		getServer().getPluginManager().registerEvents(new ASPListener(this), this);
		
		asprotect = new ASPCommand(this);
		getCommand("asprotect").setExecutor(asprotect);
		ASPVault.init();
	}
	@Override
	public void onDisable() {
		saveProtectArmorStand();
	}
	
	public static int stringToInt(String str) {
		Integer i = 0;
		try { 
	        i = new Integer(str);
	    }catch (NumberFormatException e) {  
	        System.err.println("Неверный формат строки в конфиге!");  
	    }
		return i;
	}
	
	public void reloadConfiguration() {
		this.reloadConfig();
		config = this.getConfig();
		protectmessage = config.getString("protect.message");
		groups = config.getConfigurationSection("group").getValues(false);
		loadProtectArmorStand();
	}
	public static boolean checkArmorStandProtect(ASPLocation asloc, Player player) {
		for (Entry<String, Object> entry : stands.entrySet()){
			if(entry.getKey().equals(asloc.toString())) {
				if (player == null) return true;
				String owner = (String) entry.getValue();
				String message1 = protectmessage.replace("%owner%", owner);
				if (owner.equals(player.getName().toLowerCase())) {
					player.sendMessage(TAG + ChatColor.DARK_RED + message1);
					return true;
				}
				if (player.hasPermission(PERM_ignore)) {
					player.sendMessage(TAG + ChatColor.DARK_RED + message1);
					return true;
				}
				player.sendMessage(TAG + ChatColor.DARK_RED + message1);
				return true;
				
			}
		}
		return false;
	}

	public static boolean checkCroupStands(Player player) {
		String group = ASPVault.getPrimaryGroup(player);
		int i = 0;
		int x = 0;
		for (Entry<String, Object> entry : groups.entrySet()){
			if (entry.getKey().equals(group)) {
				i = stringToInt(entry.getValue().toString());
			}
		}
		if (i == 0) i = defaultgroup;
		for (Entry<String, Object> entry : stands.entrySet()){
			if (entry.getValue().equals(player.getName().toLowerCase())) {
				x++;
			}
		}
		if (x >= i) {
			return false;
		}
		return true;
	}
	
	public static Map<String, Player> getIsPlayerStand() {
		return isplayerstand;
	}

	public synchronized static void isPlayerStand(String name, Player player, boolean put) {
		if (put) {
			isplayerstand.put(name, player);
			return;
		}else {
			
			isplayerstand.clear();
		}
	}

	public synchronized static void addOrRemoveProtectArmorStand(ASPLocation loc, Player player, boolean add) {
		if (add) {
			stands.put(loc.toString(), player.getName().toLowerCase());
		}else {
			for(Entry<String, Object> entry : stands.entrySet()) {
				if(entry.getKey().equals(loc.toString())) {
					stands.remove(entry);
				}
			}
		}
		YamlConfiguration cfg = new YamlConfiguration();
		for (Entry<String, Object> entry : stands.entrySet()){
			String strloc = entry.getKey();
			String pname = (String) entry.getValue();
			cfg.set(strloc, pname);
		}
		if (fileAS.exists()) fileAS.delete(); 
		try {
			cfg.save(fileAS);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<String, Object> getStands() {
		return stands;
	}
	
	private void loadProtectArmorStand(){
        try {
        	YamlConfiguration cfg = new YamlConfiguration();
        	stands.clear();
            if (fileAS.exists()) {
            	cfg.load(fileAS);
                if (cfg != null) {
                	stands = cfg.getValues(false);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

	private void saveProtectArmorStand() {
		YamlConfiguration cfg = new YamlConfiguration();
		for (Entry<String, Object> entry : stands.entrySet()){
			String strloc = entry.getKey();
			String pname = (String) entry.getValue();
			cfg.set(strloc, pname);
		}
		if (fileAS.exists()) fileAS.delete(); 
		try {
			cfg.save(fileAS);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveDefaultProtectArmorStand() {
		if (fileAS.exists()) return;
		YamlConfiguration cfg = new YamlConfiguration();
		cfg.addDefaults(stands);
		try {
			cfg.save(fileAS);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
// ****************
	public static void printProtectArmorStand() {
		for (Entry<String, Object> entry : stands.entrySet()){
			String strloc = entry.getKey();
			Object player = entry.getValue();
			log.info(strloc +": "+ player);
		}
	}

}