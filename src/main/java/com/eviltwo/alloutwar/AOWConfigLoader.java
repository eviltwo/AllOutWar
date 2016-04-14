package com.eviltwo.alloutwar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

public class AOWConfigLoader {
	
	private Plugin plugin;
	private List<SpecialMob> loadedMobs;
	private int coreVillagerValue;
	private int coreVillagerHealth;
	private int startEmeraldValue;
	
	public AOWConfigLoader(Plugin plugin){
		this.plugin = plugin;
		LoadConfig();
	}
	
	private void LoadConfig(){
		plugin.saveDefaultConfig();
		
		List<Map<?,?>> mobs = plugin.getConfig().getMapList("mobs");
		loadedMobs = new ArrayList<>();
		for(int i=0; i<mobs.size(); i++){
			String id = (String)mobs.get(i).get("name");
			String name = EntityIdToTypeName(id);
			int price = -1;
			if(mobs.get(i).containsKey("price")){
			 price = (int)mobs.get(i).get("price");
			}
			int reward = -1;
			if(mobs.get(i).containsKey("reward")){
				reward = (int)mobs.get(i).get("reward");
			}
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
			SpecialMob item = new SpecialMob(id, name, price, reward, type);
			loadedMobs.add(item);
		}
		plugin.getLogger().info("Load : "+loadedMobs.size()+" mobs");
		
		coreVillagerValue = plugin.getConfig().getInt("coreVillagerValue");
		coreVillagerHealth = plugin.getConfig().getInt("coreVillagerHealth");
		startEmeraldValue = plugin.getConfig().getInt("startEmeraldValue");
		
		plugin.saveConfig();
	}
	
	public int getMobListSize(){
		return loadedMobs.size();
	}
	
	public SpecialMob getMob(int index){
		return loadedMobs.get(index);
	}
	
	public SpecialMob getMobFromType(EntityType entityType) {
		for (SpecialMob mob : loadedMobs) {
			if(mob.entityType.equals(entityType)){
				return mob;
			}
		}
		return null;
	}
	
	public class SpecialMob {
		public String id;
		public String name;
		public int price = 0;
		public int reward = 0;
		public EntityType entityType;
		public boolean isTrade = false;
		public boolean isReward = false;;
		public SpecialMob(String id, String name, int price, int reward,  EntityType entityType){
			this.id = id;
			this.name = name;
			this.price = price;
			this.reward = reward;
			this.entityType = entityType;
			this.isTrade = price > 0;
			this.isReward = reward > 0;
		}
	}
	
	public int getCoreVillagerValue(){
		return coreVillagerValue;
	}
	
	public int getCoreVillagerHealth(){
		return coreVillagerHealth;
	}
	
	public int getEmeraldValue() {
		return startEmeraldValue;
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
