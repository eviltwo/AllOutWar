package com.eviltwo.alloutwar;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class AOWSoundPlayer {
	
	public AOWSoundPlayer(){
	}
	
	public void playSound(Location location, Sound sound){
		location.getWorld().playSound(location, sound, 1.0f, 1.0f);
	}
	
	public void playSound(Player player, Sound sound){
		player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
	}
	
	public void playSound(Team team, Sound sound){
		
	}
	
	public void playSoundAllPlayer(Sound sound){
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for (Player player : players) {
			playSound(player, sound);
		}
	}
	
}
