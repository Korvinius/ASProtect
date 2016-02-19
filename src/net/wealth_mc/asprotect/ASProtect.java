package net.wealth_mc.asprotect;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ASProtect extends JavaPlugin {

	public static ASProtect instance;
	public FileConfiguration config;
	private ASPCommand asprotect;
	
	public static Logger log;
	public static String tag;
	public static Integer defaultgroup;
	public static File fileAS;
	private static Map<String, Object> stands = new HashMap<String, Object>();
	private static Map<String, Player> isplayerstand = new HashMap<String, Player>();
	private static Map<Player, ASPLocation> isplayerasrmv = new HashMap<Player, ASPLocation>();
	public static Map<String, Object> groups = new HashMap<String, Object>();
	public static final String PERM_admin     = "asprotect.admin";
	public static final String PERM_protect   = "asprotect.protect";
	public static final String PERM_unprotect = "asprotect.unprotect";
	
	@Override
	public void onEnable() {
		instance = this;
		this.getConfig().options().copyDefaults(true).copyHeader(true);
		this.saveDefaultConfig();
		log = this.getLogger();
		fileAS = new File (instance.getDataFolder()+File.separator + "ArmorStand.yml");
		log.info(fileAS.toString());
		
		config = this.getConfig();
		groups = config.getConfigurationSection("group").getValues(false);
		defaultgroup = stringToInt(config.getString("group.default"));
		tag = toStringColor(config.getString("PluginName"));
		
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
		groups = config.getConfigurationSection("group").getValues(false);
		defaultgroup = stringToInt(config.getString("group.default"));
		loadProtectArmorStand();
	}
	public static boolean checkPlayerRemoveAS(Player player) {
		for (Entry<Player, ASPLocation> entry : isplayerasrmv.entrySet()){
			if (entry.getKey().equals(player)) return true;
		}
		return false;
	}
	public static boolean checkArmorStandProtect(ASPLocation asloc, Player player) {
		for (Entry<String, Object> entry : stands.entrySet()){
			if(entry.getKey().equals(asloc.toString())) {
				if (player == null) return true;
				String owner = (String) entry.getValue();
				if (owner.equals(player.getName().toLowerCase())) {
					if (player.getItemInHand().getType() != Material.BONE) {
						player.sendMessage(tag + ChatColor.AQUA
								+ " Эта стойка защищена вами, чтобы снять защиту ударьте по стойке костью");
					}
					return true;
				}
				player.sendMessage(tag + ChatColor.DARK_RED
						+ " Эта стойка защищена, владелец стойки: " + ChatColor.AQUA + owner);
				return true;
				
			}
		}
		return false;
	}

	public static boolean checkArmorStandInteract(ASPLocation asloc, Player player) {
//		printProtectArmorStand();
//		log.info("Interact: " + asloc);
		for (Entry<String, Object> entry : stands.entrySet()){
			if(entry.getKey().equals(asloc.toString())) {
				String owner = (String) entry.getValue();
				if (owner.equals(player.getName().toLowerCase())) {
					player.sendMessage(tag + ChatColor.YELLOW + " Это ваша стойка");
					return false;
				}
				if (player.hasPermission(PERM_admin)) {
					player.sendMessage(tag + ChatColor.YELLOW + " Эта стойка принадлежит: "
							 + ChatColor.GOLD + owner);
					return false;
				}
				player.sendMessage(tag + ChatColor.DARK_RED + " Это не Ваша стойка");
				return true;
			}
		}
		player.sendMessage(tag + ChatColor.AQUA + " Эта стойка не защищена");
		return false;
	}
	
	public static boolean checkCroupStands(Player player) {
		String group = ASPVault.getPrimaryGroup(player);
		Integer i = 0;
		Integer x = 0;
		
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

	public static void isPlayerStand(String name, Player player, boolean put) {
		if (put) {
			isplayerstand.put(name, player);
			return;
		}else {
			for(Iterator<Map.Entry<String, Player>> it = isplayerstand.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, Player> entry = it.next();
				if(entry.getKey().equals(name)) {
					it.remove();
				}
			}
		}
	}

	public synchronized static void addOrRemoveProtectArmorStand(ASPLocation loc, Player player, boolean add) {
		if (add) {
			stands.put(loc.toString(), player.getName().toLowerCase());
		}else {
			for(Iterator<Map.Entry<String, Object>> it = stands.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, Object> entry = it.next();
				if(entry.getKey().equals(loc.toString())) {
					it.remove();
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

	public static String toStringColor(String input) {
		    return ChatColor.translateAlternateColorCodes('&', input);
	}

	public static void printProtectArmorStand() {
		for (Entry<String, Object> entry : stands.entrySet()){
			String strloc = entry.getKey();
			Object player = entry.getValue();
			log.info(strloc +": "+ player);
		}
	}
}