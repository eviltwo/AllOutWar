package com.eviltwo.alloutwar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public class AOWManager {
	
	private final AOW plugin;
	private Scoreboard board;
	private boolean isCoreProtect = false;
	private StartTimer startTimer;
	
	public AOWManager(AOW plugin){
		this.plugin = plugin;
		board = Bukkit.getScoreboardManager().getMainScoreboard();
	}
	
	public void setReady(int time){
		// give egg
		Set<Team> teams = board.getTeams();
		for (Team team : teams) {
			Set<String> entries = team.getEntries();
			List<Player> players = new ArrayList<Player>();
			for (String entry : entries) {
				Player player = plugin.getServer().getPlayer(entry);
				if(player != null){
					players.add(player);
				}
			}
			if(players.size() == 0){
				continue;
			}
			Random random = new Random();
			int r = random.nextInt(players.size());
			giveCoreEgg(players.get(r), plugin.configLoader.getCoreVillagerValue());
		}
		// start count down
		if(time <= 0){
			setBegin();
			return;
		}
		isCoreProtect = true;
		
		plugin.titleSender.sendTitleAllPlayer(time+" seconds to start!", "Get ready for war.", 0.0, 5.0, 1.0);
		Bukkit.broadcastMessage(time+" seconds to start!");
		plugin.soundPlayer.playSoundAllPlayer(Sound.ENTITY_FIREWORK_LAUNCH);
		if(startTimer != null){
			startTimer.remove();
		}
		startTimer = new StartTimer(plugin, time);
	}
	
	public void setBegin() {
		// give Emerald
		Set<Team> teams = board.getTeams();
		for (Team team : teams) {
			Set<String> entries = team.getEntries();
			List<Player> players = new ArrayList<Player>();
			for (String entry : entries) {
				Player player = plugin.getServer().getPlayer(entry);
				if(player != null){
					players.add(player);
				}
			}
			if(players.size() == 0){
				continue;
			}
			int emeraldMax = plugin.configLoader.getEmeraldValue();
			int giveAll = emeraldMax / players.size();
			int giveAny = emeraldMax % players.size();
			for(int i=0; i<players.size(); i++){
				players.get(i).getInventory().remove(Material.MONSTER_EGG);
				giveEmerald(players.get(i), giveAll);
			}
			for(int i=0; i<giveAny; i++){
				giveEmerald(players.get(i), 1);
			}
		}
		isCoreProtect = false;
		plugin.titleSender.sendTitleAllPlayer("WAR START", "Destroy the enemy CORE", 0.0, 5.0, 1.0);
		
		Bukkit.broadcastMessage("WAR START");
		plugin.soundPlayer.playSoundAllPlayer(Sound.ENTITY_GENERIC_EXPLODE);
		if(startTimer != null){
			startTimer.remove();
		}
	}
	
	public Team createTeam(String teamName, ChatColor teamColor){
		if(teamColor == null){
			Random random = new Random();
			int r = random.nextInt(15);
			teamColor = ChatColor.values()[r];
		}
		Team team = board.registerNewTeam(teamName);
    	team.setDisplayName(teamName);
    	team.setAllowFriendlyFire(false);
    	team.setPrefix(teamColor.toString());
    	team.setSuffix(ChatColor.RESET.toString());
    	team.setOption(Option.NAME_TAG_VISIBILITY,OptionStatus.ALWAYS);
    	return team;
	}
	
	private void giveCoreEgg(Player player, int value){
		ItemStack is = new ItemStack(Material.MONSTER_EGG, value);
    	ItemMeta meta = is.getItemMeta();
    	meta.setDisplayName("TEAM CORE");
    	List<String> lores = new ArrayList<>(Arrays.asList("Core villager spawn egg"));
    	meta.setLore(lores);
    	is.setItemMeta(meta);
    	PlayerInventory inventory = player.getInventory();
        inventory.addItem(is);
	}
	
	private void giveEmerald(Player player, int value){
		ItemStack is = new ItemStack(Material.EMERALD, value);
    	PlayerInventory inventory = player.getInventory();
        inventory.addItem(is);
	}
	
	public boolean isCoreProtect(){
		return isCoreProtect;
	}
	
	public Team getTeam(Entity entity){
		if(entity == null){
			return null;
		}
		Team team = null;
		if(entity instanceof Player){
			team = board.getEntryTeam(((Player)entity).getName());
		}else{
			team = board.getEntryTeam(entity.getUniqueId().toString());
		}
		return team;
	}
	
	public String teamText(String text, Team team) {
		if(team == null){
			return text;
		}
		return team.getPrefix() + text + team.getSuffix();
	}
	
	public class StartTimer implements Runnable{
		int time;
		int m;
		AOW plugin;
		BukkitTask task;
		public StartTimer(AOW plugin, int timeMax) {
			this.plugin = plugin;
			this.time = timeMax;
			task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, 0L, 20L);
			m = time / 60;
		}
		@Override
		public void run() {
			time -= 1;
			if(time % 60 == 0){
				m = time / 60;
				if(m > 0){
					plugin.titleSender.sendTitleAllPlayer(null, m+" minutes to start!", 0.0, 5.0, 1.0);
					Bukkit.broadcastMessage(m+" minutes to start!");
					plugin.soundPlayer.playSoundAllPlayer(Sound.BLOCK_NOTE_HAT);
				}else{
					plugin.manager.setBegin();
					plugin.getServer().getScheduler().cancelTask(task.getTaskId());
				}
			}else{
				if(time < 30 && time > 0){
					plugin.titleSender.sendTitleAllPlayer(null, Integer.toString(time), 0.0, 0.5, 1.0);
					plugin.soundPlayer.playSoundAllPlayer(Sound.BLOCK_NOTE_HAT);
				}
			}
		}
		public  void  remove() {
			plugin.getServer().getScheduler().cancelTask(task.getTaskId());
		}
	}
}
