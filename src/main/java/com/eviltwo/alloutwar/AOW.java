package com.eviltwo.alloutwar;

import org.bukkit.plugin.java.JavaPlugin;

public class AOW extends JavaPlugin {
	
	public AOWConfigLoader configLoader;
	public AOWManager manager;
	public AOWCommandSender commandSender;
	public AOWTitleSender titleSender;
	
	@Override
	public void onEnable() {
		configLoader = new AOWConfigLoader(this);
		manager = new AOWManager(this);
		commandSender = new AOWCommandSender();
		titleSender = new AOWTitleSender();
		getCommand("team").setExecutor(new AOWCommandExcutor(this));
		getServer().getPluginManager().registerEvents(new AOWEventListener(this),this);
	}
	
	@Override
	public void onDisable() {
		
	}
}
