package com.eviltwo.alloutwar;

import org.bukkit.plugin.java.JavaPlugin;

public class AOW extends JavaPlugin {
	
	public AOWConfigLoader configLoader;
	public AOWCommandSender commandSender;
	
	@Override
	public void onEnable() {
		configLoader = new AOWConfigLoader(this);
		commandSender = new AOWCommandSender();
		getCommand("team").setExecutor(new AOWCommandExcutor(this));
		getServer().getPluginManager().registerEvents(new AOWEventListener(this),this);
	}
	
	@Override
	public void onDisable() {
		
	}
}
