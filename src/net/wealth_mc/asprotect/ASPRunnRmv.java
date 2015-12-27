package net.wealth_mc.asprotect;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

public class ASPRunnRmv implements Runnable {

	private Player player;
	private ASPLocation loc;
	private Thread thread;
	
	public ASPRunnRmv(Player p, ASPLocation loc) {
		this.player = p;
		this.setLoc(loc);
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		for (int delay = ASProtect.delayunprotect; delay>0; delay--) {
			Map<Player, ASPLocation> asrmv = ASProtect.getIsplayerasrmv();
			for(Entry<Player, ASPLocation> entry : asrmv.entrySet()) {
				if (entry.getKey().equals(player) 
						&& entry.getValue() != null) {
					loc = entry.getValue();
				}
			}
			if (loc != null) {
				//*******здесь будет удаление защиты из стойки
				return;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public ASPLocation getLoc() {
		return loc;
	}

	public void setLoc(ASPLocation loc) {
		this.loc = loc;
	}

}
