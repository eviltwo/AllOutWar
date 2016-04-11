package com.eviltwo.alloutwar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

public class AOWConfigLoader {
	
	private Plugin plugin;
	private List<SpawnMob> spawnMobs;
	private int coreVillagerValue;
	private int coreVillagerHealth;
	
	public AOWConfigLoader(Plugin plugin){
		this.plugin = plugin;
		LoadConfig();
	}
	
	private void LoadConfig(){
		plugin.saveDefaultConfig();
		
		List<Map<?,?>> mobs = plugin.getConfig().getMapList("mobs");
		spawnMobs = new ArrayList<>();
		for(int i=0; i<mobs.size(); i++){
			String id = (String)mobs.get(i).get("name");
			String name = EntityIdToTypeName(id);
			int price = (int)mobs.get(i).get("price");
			EntityType type;
			try{
				type = EntityType.valueOf(name);
			}catch(IllegalArgumentException e){
				type = null;
				plugin.getLogger().warning("Mob \""+name+"\" in Config.yml is not exists!");
			}
			if(type == null){
				continue;
			}
			SpawnMob item = new SpawnMob(id, name, price, type);
			spawnMobs.add(item);
		}
		plugin.getLogger().info("Load : "+spawnMobs.size()+" mobs");
		
		coreVillagerValue = plugin.getConfig().getInt("coreVillagerValue");
		coreVillagerHealth = plugin.getConfig().getInt("coreVillagerHealth");
		
		plugin.saveConfig();
	}
	
	public int getSpawnMobSize(){
		return spawnMobs.size();
	}
	
	public SpawnMob getSpawnMob(int index){
		return spawnMobs.get(index);
	}
	
	public class SpawnMob {
		public String id;
		public String name;
		public int price;
		public EntityType entityType;
		public SpawnMob(String id, String name, int price, EntityType entityType){
			this.id = id;
			this.name = name;
			this.price = price;
			this.entityType = entityType;
		}
	}
	
	public int getCoreVillagerValue(){
		return coreVillagerValue;
	}
	
	public int getCoreVillagerHealth(){
		return coreVillagerHealth;
	}
	
	public String EntityIdToTypeName(String id){
		String typeName = "";
		typeName += Character.toUpperCase(id.charAt(0));
		for(int i=1; i<id.length(); i++){
			if(Character.isUpperCase(id.charAt(i))){
				typeName += "_";
			}
			typeName += Character.toUpperCase(id.charAt(i));
		}
		return typeName;
	}
}
