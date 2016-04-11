package com.eviltwo.alloutwar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
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
			if(args.length >= 1){
				if(args[0].equals("create")){
					if(args.length != 4){
						sender.sendMessage("/team create <TeamName> <Player> <TeamColor>");
						return true;
					}
					Team team = board.getTeam(args[1]);
					if(team == null){
						Player target = Bukkit.getPlayerExact(args[2]);
					    if ( target == null ) {
					        sender.sendMessage("Player name " + args[2] + " is not exists.");
					        return true;
					    }
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
					}else{
						sender.sendMessage("Team <" + args[1] + "> is already exists.");
					}
				}
				if(args[0].equals("remove")){
					if (args.length != 2) {
						sender.sendMessage("/team remove <TeamName>");
						return true;
					}
					Team team = board.getTeam(args[1]);
					if(team == null){
						sender.sendMessage("Team <" + args[1] + "> is not exists.");
						return true;
					}
					team.unregister();
					sender.sendMessage("Removed team " + args[1]);
					return true;
				}
				if(args[0].equals("join")){
					if (args.length < 2 || args.length > 3) {
						sender.sendMessage("/team join <TeamName>");
						return true;
					}
					Team team = board.getTeam(args[1]);
					if(team == null){
						sender.sendMessage("Team <" + args[1] + "> is not exists.");
						return true;
					}
					if(args.length == 2){
						if(sender instanceof Player){
							team.addEntry(((Player)sender).getName());
							sender.sendMessage("You join \""+team.getName()+"\" team.");
						}else{
							sender.sendMessage("This command is player only.");
							sender.sendMessage("/team join <Player>");
						}
					}else{
						Player target = Bukkit.getPlayerExact(args[2]);
					    if ( target == null ) {
					        sender.sendMessage("Player name " + args[2] + " is not exists.");
					        return true;
					    }
					    team.addEntry(target.getName());
					    target.sendMessage("You join \""+team.getName()+"\" team.");
					    sender.sendMessage(target.getName()+" join \""+team.getName()+"\" team.");
					}
					return true;
				}
				if(args[0].equals("debug")){
					if (args.length != 1) {
						sender.sendMessage("/team debug");
						return true;
					}
					sender.sendMessage("Debug command");
					if(sender instanceof Player){
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
