package com.eviltwo.alloutwar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

public class AOWAssistGhast implements Runnable{
	
	AOW plugin;
	Ghast ghast;
	Team team;
	float runInterval = 5.0f;
	BukkitTask task;
	double distMin = 16;
	double distMax = 128;
	double dist = 0;
	double distAdd = 4;
	
	public AOWAssistGhast(AOW plugin, Ghast ghast) {
		this.plugin = plugin;
		this.ghast = ghast;
		this.team = plugin.manager.getTeam(ghast);
		long interval = 20L * (long)runInterval;
		task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, 0L, interval);
		dist = distMin;
	}
	
	@Override
	public void run() {
		if(ghast == null || ghast.isDead()){
			remove();
		}
		List<Entity> findEntities = ghast.getNearbyEntities(dist, dist, dist);
		List<Entity> enemies = new ArrayList<Entity>();
		for (Entity entity : findEntities) {
			Team findTeam = plugin.manager.getTeam(entity);
			if(findTeam == null)
				continue;
			if(team.equals(findTeam) == false){
				enemies.add(entity);
				break;
			}
		}
		if(enemies.size() == 0){
			dist += distAdd;
			if(dist > distMax){
				dist = distMax;
			}
		}else{
			int r = new Random().nextInt(enemies.size());
			Entity target = enemies.get(r);
			// set target
		}
	}
	
	
	
	public  void  remove() {
		plugin.getServer().getScheduler().cancelTask(task.getTaskId());
	}

}
