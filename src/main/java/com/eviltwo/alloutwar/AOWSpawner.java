package com.eviltwo.alloutwar;
import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.scoreboard.Team;

import com.eviltwo.alloutwar.AOWConfigLoader.SpecialMob;

public class AOWSpawner {
	
	private AOW plugin;
	private int villagerNumber = 0;
	
	public AOWSpawner(AOW plugin){
		this.plugin = plugin;
	}
	
	public void spawnVillager(Team team, Location location){
		LivingEntity villager = null;
		// send summon command (set store recipe)
		String cmdText = "summon Villager "+location.getX()+" "+location.getY()+" "+location.getZ()+" {CustomName:\"CoreVillager"+villagerNumber+"\",Offers:{Recipes:[";
		int max = plugin.configLoader.getMobListSize();
		for(int i=0; i<max; i++){
			String itemText = "";
			SpecialMob spawnMob = plugin.configLoader.getMob(i);
			if(spawnMob.isTrade == false){
				continue;
			}
			int price = spawnMob.price;
			String entityId = spawnMob.id;
			String entityName = spawnMob.name;
			itemText += "{buy:{id:\"emerald\",Count:"+price+"},maxUses:9999999,sell:{id:\"spawn_egg\",Count:1,Damage:0,tag:{EntityTag:{id:"+entityId+"},display:{Name:"+entityId+", Lore:[\"Team monster spawn egg\",\""+entityName+"\"]},CustomNameVisible:1}}}";
			if(i<max-1){
				itemText += ",";
			}
			cmdText += itemText;
		}
		cmdText += "]}}";
		plugin.commandSender.sendCommand(cmdText);
		Collection<Entity> villagers = location.getWorld().getEntitiesByClasses(Villager.class);
		for (Entity entity : villagers) {
			if(entity.getCustomName().equals("CoreVillager"+villagerNumber) == false){
				continue;
			}
			Team t = plugin.manager.getTeam(entity);
			if(t!=null){
				continue;
			}
			villager = (LivingEntity)entity;
			break;
		}
		villagerNumber++;
		if(villager == null){
			plugin.getLogger().warning("CoreVillager is not exist.");
			return;
		}
		villager.setCustomName("CoreVillager");
		villager.setCustomNameVisible(true);
		villager.setAI(false);
		team.addEntry(villager.getUniqueId().toString());
	}
}
