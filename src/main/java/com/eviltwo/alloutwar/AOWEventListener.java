package com.eviltwo.alloutwar;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.eviltwo.alloutwar.AOWConfigLoader.SpawnMob;

public class AOWEventListener implements Listener {
	
	private final AOW plugin;
	private ScoreboardManager manager;
	private Scoreboard board;
	private AOWTitleSender titleSender;
	private int villagerNumber = 0;
	
	public AOWEventListener(AOW plugin){
		this.plugin = plugin;
		manager = Bukkit.getScoreboardManager();
		board = manager.getMainScoreboard();
		titleSender = new AOWTitleSender();
	}
	
	@EventHandler
	void onRightClickTool(PlayerInteractEvent e){
		if(e == null){
			return;
		}
		Player player = e.getPlayer();
		ItemStack mainIS = player.getInventory().getItemInMainHand();
		ItemMeta mainMeta = mainIS.getItemMeta();
		// Villager
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(mainMeta.getDisplayName().equals("TEAM CORE")){
				// get team
				Team team = board.getEntryTeam(player.getName());
				if(team == null){
					plugin.getLogger().warning("You must join team.");
					return;
				}
				// villager
				Location blockLocation = player.getTargetBlock((Set<Material>)null, 100).getLocation();
				BlockFace bf = e.getBlockFace();
				double x = blockLocation.getX()+0.5+bf.getModX();
				double y = blockLocation.getY()+0.5+bf.getModY();
				double z = blockLocation.getZ()+0.5+bf.getModZ();
				Location location = new Location(player.getWorld(),x,y,z);
				location.setYaw(player.getLocation().getYaw());
				location.setDirection(player.getLocation().getDirection());
				LivingEntity villager = null;
				// send summon command (set store recipe)
				String cmdText = "summon Villager "+location.getX()+" "+location.getY()+" "+location.getZ()+" {CustomName:\"CoreVillager"+villagerNumber+"\",Offers:{Recipes:[";
				int max = plugin.configLoader.getSpawnMobSize();
				for(int i=0; i<max; i++){
					String itemText = "";
					SpawnMob spawnMob = plugin.configLoader.getSpawnMob(i);
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
					Team t = board.getEntryTeam(entity.getUniqueId().toString());
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
				// less item
				int haveAmount = player.getInventory().getItemInMainHand().getAmount();
				ItemStack oldItem = player.getInventory().getItemInMainHand();
				ItemStack newItem = new ItemStack(oldItem.getType(),haveAmount-1);
				newItem.setItemMeta(oldItem.getItemMeta());
				player.getInventory().setItemInMainHand(newItem);
				// particle
				player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, location, 1);
				return;
			}
		}
		// Monster
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
			if(mainMeta.getLore() != null && mainMeta.getLore().get(0).equals("Team monster spawn egg")){
				e.setCancelled(true);
				// get team
				Team team = board.getEntryTeam(player.getName());
				if(team == null){
					plugin.getLogger().warning("You must join team.");
					return;
				}
				// throw egg
				Location eyeLocation = player.getLocation();
				eyeLocation.setY(eyeLocation.getY()+player.getEyeHeight());
				Entity ball = player.getWorld().spawnEntity(eyeLocation,EntityType.SNOWBALL);
				ball.setVelocity(player.getLocation().getDirection());
				ball.setCustomName("SpecialSpawnEgg");
				MetadataValue meta = new FixedMetadataValue(plugin, mainMeta.getLore().get(1));
				ball.setMetadata("SpawnName", meta);
				MetadataValue meta2 = new FixedMetadataValue(plugin, player);
				ball.setMetadata("Shooter", meta2);
				// less item
				int haveAmount = player.getInventory().getItemInMainHand().getAmount();
				haveAmount -= 1;
				if(haveAmount==0){
					ItemStack newItem = new ItemStack(Material.AIR,0);
					player.getInventory().setItemInMainHand(newItem);
				}else{
					ItemStack oldItem = player.getInventory().getItemInMainHand();
					ItemStack newItem = new ItemStack(oldItem.getType(),haveAmount);
					newItem.setItemMeta(oldItem.getItemMeta());
					player.getInventory().setItemInMainHand(newItem);
				}
				return;
			}
		}
	}
	
	@EventHandler
	public void onProjectileThrownEvent(ProjectileLaunchEvent event) {
		ProjectileSource projectile = event.getEntity().getShooter();
		if(projectile == null){
			return;
		}
		LivingEntity lEntity = (LivingEntity)projectile;
		Team team = board.getEntryTeam(lEntity.getUniqueId().toString());
		if(team == null){
			return;
		}
		plugin.getLogger().info("set team " + event.getEntity().getName() + " -> " + team.getName());
		team.addEntry(event.getEntity().getUniqueId().toString());
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e){
		Entity atkEntity = e.getDamager();
		Entity dmgEntity = e.getEntity();
		Team atkTeam = board.getEntryTeam(atkEntity.getUniqueId().toString());
		Team dmgTeam = board.getEntryTeam(dmgEntity.getUniqueId().toString());
		if(dmgEntity instanceof Player){
			dmgTeam = board.getEntryTeam(((Player)dmgEntity).getName());
		}
		if(atkTeam == null || dmgTeam == null){
			return;
		}
		if(atkTeam.equals(dmgTeam)){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (e.getEntity().getType().equals(EntityType.SNOWBALL)) {
        	if(e.getEntity().getCustomName().equals("SpecialSpawnEgg")){
        		Player shotPlayer = (Player)e.getEntity().getMetadata("Shooter").get(0).value();
            	Team team = board.getEntryTeam(shotPlayer.getName());
				if(team == null){
					plugin.getLogger().warning("Shot snowball by no team player.");
					return;
				}
				// spawn
				Location hitLocation = e.getEntity().getLocation();
				String spawnName = e.getEntity().getMetadata("SpawnName").get(0).asString();
				Entity monster = shotPlayer.getWorld().spawnEntity(hitLocation,EntityType.valueOf(spawnName));
				monster.setCustomName(spawnName);
				monster.setCustomNameVisible(true);
				team.addEntry(monster.getUniqueId().toString());
        	}
        }
    }
	
	@EventHandler
	public void onVillagerTrade(VillagerAcquireTradeEvent e){
		/*
		if(e.getEntity().getCustomName().equals("CoreVillager")){
			List<MerchantRecipe> recipes = new ArrayList<>();
			int max = plugin.configLoader.getSpawnMobSize();
			for(int i=0; i<max; i++){
				SpawnMob spawnMob = plugin.configLoader.getSpawnMob(i);
				ItemStack isAdd = new ItemStack(Material.EMERALD, spawnMob.price);
				SpawnEgg sEgg = new SpawnEgg(EntityType.ZOMBIE);
				ItemStack isResult = sEgg.toItemStack();
				isResult.setAmount(1);
				//ItemStack isResult = new ItemStack(Material.MONSTER_EGG, 1);
				//isResult.setData(sEgg);
				ItemMeta meta = isResult.getItemMeta();
				// /give @p spawn_egg 1 0 {EntityTag:{id:EntityHorse,Type:4,SaddleItem:{id:saddle},Tame:1b,Age:0},display:{Name:Tom},CustomNameVisible:1b}
		    	meta.setDisplayName(spawnMob.name + " Egg");
		    	//List<String> lores = new ArrayList<>(Arrays.asList("Team monster spawn egg", spawnMob.name));
		    	//meta.setLore(lores);
		    	isResult.setItemMeta(meta);
				MerchantRecipe recipe = new MerchantRecipe(isResult, 999999);
				recipe.addIngredient(isAdd);
				if(i<max-1){
					recipes.add(recipe);
				}else{
					e.setRecipe(recipe);
				}
			}
			e.getEntity().setRecipes(recipes);
		}
		*/
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e){
		if(e == null){
			return;
		}
		Entity entity = e.getEntity();
		if(entity.getCustomName().equals("CoreVillager")){
			Team team = board.getEntryTeam(entity.getUniqueId().toString());
			List<Entity> entities = entity.getWorld().getEntities();
			int villagerCount = 0;
			for(Entity searchEntity : entities){
				if(searchEntity.getType().equals(EntityType.VILLAGER)){
					if(team.hasEntry(searchEntity.getUniqueId().toString())){
						if(searchEntity.getCustomName().equals("CoreVillager")){
							if(searchEntity.isDead() == false){
								villagerCount++;
								plugin.getLogger().info("player:"+searchEntity.getCustomName());
							}
						}
					}
				}
			}
			if(villagerCount == 0){
				String title = team.getPrefix() + team.getName() + team.getSuffix() + " defeat!";
				for(Entity searchEntity : entities){
					if(team.hasEntry(searchEntity.getUniqueId().toString())){
						if(searchEntity instanceof LivingEntity){
							((LivingEntity)searchEntity).damage(9999);
						}
					}
				}
				Bukkit.broadcastMessage(title);
				titleSender.setTime(0.5, 3.0, 0.5);
				titleSender.sendTitle(title, null);
			}else{
				String deadCoreText = team.getPrefix() + team.getName() + " CORE IS DEAD!" + team.getSuffix() + " (" + villagerCount + " core left)";
				titleSender.setTime(0.1, 3.0, 0.5);
				titleSender.sendTitle(null, deadCoreText);
			}
		}
	}
}
