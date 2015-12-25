package net.wealth_mc.asprotect;

import org.bukkit.entity.Player;

public class ASPRunnProtect implements Runnable {

	private ASPLocation loc;
	private Player player;
	private boolean add;
	private Thread thread;

	public ASPRunnProtect(ASPLocation loc, Player player, boolean add) {
		super();
		this.loc = loc;
		this.player = player;
		this.add = add;
		this.thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		ASProtect.addOrRemoveProtectArmorStand(loc, player, add);
	}

}
