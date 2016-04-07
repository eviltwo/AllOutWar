package com.eviltwo.alloutwar;

import org.bukkit.plugin.java.JavaPlugin;

public class AOW extends JavaPlugin {
	
	public AOWConfigLoader configLoader;
	
	@Override
	public void onEnable() {
		// getLogger().info("Enable plugin");
		configLoader = new AOWConfigLoader(this);
		getCommand("team").setExecutor(new AOWCommandExcutor(this));
		getServer().getPluginManager().registerEvents(new AOWEventLestener(this),this);
	}
	
	@Override
	public void onDisable() {
		// getLogger().info("Disable plugin");
	}
}
