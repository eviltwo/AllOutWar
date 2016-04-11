package com.eviltwo.alloutwar;

import org.bukkit.Bukkit;

public class AOWCommandSender {
	
	public AOWCommandSender() {
	}
	
	public void sendCommand(String commandText){
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandText);
	}
}
