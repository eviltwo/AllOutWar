package com.eviltwo.alloutwar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.MonsterEggs;
import org.bukkit.material.SpawnEgg;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public class AOWCommandExcutor implements CommandExecutor {

	private final AOW plugin;
	private ScoreboardManager manager;
	private Scoreboard board;
	 
	public AOWCommandExcutor(AOW plugin) {
		this.plugin = plugin;
		manager = Bukkit.getScoreboardManager();
		board = manager.getMainScoreboard();
	}
 
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("team")) {
			if (args.length == 4) {
				if(args[0].equals("create")){
					Team team = board.getTeam(args[1]);
					if(team == null){
						Player target = Bukkit.getPlayerExact(args[2]);
					    if ( target == null ) {
					        sender.sendMessage("Player name " + args[2] + " is not found!");
					        return true;
					    } else {
					    	ChatColor c = NameToColor(args[3]);
	                        if (c == null) {
	                            sender.sendMessage("Valid values for option color are: " + args[3]);
	                            return true;
	                        }
					    	Team newTeam = board.registerNewTeam(args[1]);
					    	newTeam.addEntry(target.getName());
					    	newTeam.setDisplayName(args[1]);
					    	newTeam.setAllowFriendlyFire(false);
					    	newTeam.setPrefix(c.toString());
					    	newTeam.setSuffix(ChatColor.RESET.toString());
					    	newTeam.setOption(Option.NAME_TAG_VISIBILITY,OptionStatus.ALWAYS);
					    	sender.sendMessage(args[2] + " is leader!");
					    	// give item
					    	ItemStack is = new ItemStack(Material.MONSTER_EGG, plugin.configLoader.getCoreVillagerValue());
					    	ItemMeta meta = is.getItemMeta();
					    	meta.setDisplayName("TEAM CORE");
					    	List<String> lores = new ArrayList<>(Arrays.asList(args[1], "King villager spawn egg"));
					    	meta.setLore(lores);
					    	is.setItemMeta(meta);
					    	// change color hint https://bukkit.org/threads/giving-players-colored-wool.61779/
					    	PlayerInventory inventory = plugin.getServer().getPlayer(args[2]).getInventory();
					        inventory.addItem(is);
					        return true;
					    }
					}else{
						sender.sendMessage("Team <" + args[1] + "> is already exists.");
					}
				}
			}
			if (args.length == 2) {
				if(args[0].equals("remove")){
					Team team = board.getTeam(args[1]);
					if(team == null){
						sender.sendMessage("Team <" + args[1] + "> is not exists.");
						return false;
					}
					team.unregister();
					sender.sendMessage("Removed team " + args[1]);
					return true;
				}
			}
			if (args.length == 1) {
				if(args[0].equals("debug")){
					plugin.getLogger().info("Debug command");
					if(sender instanceof Player){
						Player player = (Player)sender;
						Team team = board.getEntryTeam(player.getName());
						if(team == null){
							plugin.getLogger().warning("You are alone. Please join team!");
							return true;
						}
						// give item
				    	ItemStack is = new ItemStack(Material.MONSTER_EGG, 32, (short)61);
				    	ItemMeta meta = is.getItemMeta();
				    	meta.setDisplayName("Zombie egg");
				    	List<String> lores = new ArrayList<>(Arrays.asList("Team monster spawn egg", "ZOMBIE"));
				    	meta.setLore(lores);
				    	is.setItemMeta(meta);
				    	//SpawnEgg egg = new SpawnEgg(EntityType.BLAZE);
				    	//is.setData(egg);
				    	//is.setDurability(durability);
				    	PlayerInventory inventory = player.getInventory();
				        inventory.addItem(is);
				        return true;
					}
				}
			}
		}
		return false;
	}
	
	private ChatColor NameToColor(String name){
		String input = name.toUpperCase();
		for(ChatColor c:ChatColor.values()){
			if(c.name().equals(input)){
				return c;
			}
		}
		return null;
	}
}
