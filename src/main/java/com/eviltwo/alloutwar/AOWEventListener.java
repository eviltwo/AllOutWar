package com.eviltwo.alloutwar;

import java.util.List;
import java.util.Random;
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
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.Team;

import com.eviltwo.alloutwar.AOWArmorMaterial.AOWArmorType;
import com.eviltwo.alloutwar.AOWConfigLoader.SpecialMob;

public class AOWEventListener implements Listener {
	
	private final AOW plugin;
	
	public AOWEventListener(AOW plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onRightClickTool(PlayerInteractEvent e){
		if(e == null){
			return;
		}
		Player player = e.getPlayer();
		ItemStack mainIS = player.getInventory().getItemInMainHand();
		ItemMeta mainMeta = mainIS.getItemMeta();
		// Villager
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(mainMeta != null && mainMeta.getDisplayName() != null && mainMeta.getDisplayName().equals("TEAM CORE")){
				// get team
				Team team = plugin.manager.getTeam(player);
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
				location.setYaw(new Random().nextFloat()*360F);
				location.setPitch(new Random().nextFloat());
				plugin.spawner.spawnVillager(team, location);
				// less item
				int haveAmount = player.getInventory().getItemInMainHand().getAmount();
				ItemStack oldItem = player.getInventory().getItemInMainHand();
				ItemStack newItem = new ItemStack(oldItem.getType(),haveAmount-1);
				newItem.setItemMeta(oldItem.getItemMeta());
				player.getInventory().setItemInMainHand(newItem);
				// effect
				player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, location, 1);
				return;
			}
		}
		// Monster
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
			if(mainMeta != null && mainMeta.getLore() != null && mainMeta.getLore().get(0).equals("Team monster spawn egg")){
				e.setCancelled(true);
				// get team
				Team team = plugin.manager.getTeam(player);
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
	public void onClickEntity(PlayerInteractEntityEvent e){
		Player player = e.getPlayer();
		Team playerTeam = plugin.manager.getTeam(player);
		Entity clickedEntity = e.getRightClicked();
		Team clickedTeam = plugin.manager.getTeam(clickedEntity);
		ItemStack mainItem = player.getEquipment().getItemInMainHand();
		// Villager
		if(clickedEntity.getCustomName() != null && clickedEntity.getCustomName().equals("CoreVillager")){
			// ready war mode
			if(plugin.manager.isCoreProtect()){
				player.sendMessage("["+plugin.manager.teamText("CoreVillager", clickedTeam)+"] Wait until the war begins.");
				e.setCancelled(true);
				return;
			}
		}
		// Zombie or Skeleton armor
		if(AOWArmorMaterial.fromMaterial(mainItem.getType()) != null){
			AOWArmorMaterial armor = AOWArmorMaterial.fromMaterial(mainItem.getType());
			if(clickedEntity.getType().equals(EntityType.ZOMBIE) || clickedEntity.getType().equals(EntityType.SKELETON)){
				if(playerTeam != null && clickedTeam != null && playerTeam.equals(clickedTeam)){
					if(clickedEntity instanceof LivingEntity){
						LivingEntity lEntity = (LivingEntity)clickedEntity;
						EntityEquipment equipment = lEntity.getEquipment();
						if(armor.getType().equals(AOWArmorType.HELMET)){
							if(equipment.getHelmet().getType().equals(Material.AIR) == false){
								lEntity.getWorld().dropItem(lEntity.getEyeLocation(), new ItemStack(equipment.getHelmet().getType(), 1));
							}
							equipment.setHelmet(mainItem);
							player.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
							e.setCancelled(true);
						}else if(armor.getType().equals(AOWArmorType.CHESTPLATE)){
							if(equipment.getChestplate().getType().equals(Material.AIR) == false){
								lEntity.getWorld().dropItem(lEntity.getLocation(), new ItemStack(equipment.getChestplate()));
							}
							equipment.setChestplate(mainItem);
							player.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
							e.setCancelled(true);
						}else if(armor.getType().equals(AOWArmorType.LEGGINGS)){
							if(equipment.getLeggings().getType().equals(Material.AIR) == false){
								lEntity.getWorld().dropItem(lEntity.getLocation(), new ItemStack(equipment.getLeggings()));
							}
							equipment.setLeggings(mainItem);
							player.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
							e.setCancelled(true);
						}else if(armor.getType().equals(AOWArmorType.BOOTS)){
							if(equipment.getBoots().getType().equals(Material.AIR) == false){
								lEntity.getWorld().dropItem(lEntity.getLocation(), new ItemStack(equipment.getBoots()));
							}
							equipment.setBoots(mainItem);
							player.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
							e.setCancelled(true);
						}else if(armor.getType().equals(AOWArmorType.WEAPON)){
							if(equipment.getItemInMainHand().getType().equals(Material.AIR) == false){
								lEntity.getWorld().dropItem(lEntity.getLocation(), new ItemStack(equipment.getItemInMainHand()));
							}
							equipment.setItemInMainHand(mainItem);
							player.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
							e.setCancelled(true);
						}
					}
				}
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
		Team team = plugin.manager.getTeam(lEntity);
		if(team == null){
			return;
		}
		team.addEntry(event.getEntity().getUniqueId().toString());
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e){
		Entity atkEntity = e.getDamager();
		Entity dmgEntity = e.getEntity();
		Team atkTeam = plugin.manager.getTeam(atkEntity);
		Team dmgTeam = plugin.manager.getTeam(dmgEntity);
		
		// Villager
		if(dmgEntity.getCustomName() != null && dmgEntity.getCustomName().equals("CoreVillager")){
			// ready war mode
			if(plugin.manager.isCoreProtect()){
				e.setCancelled(true);
				return;
			}
		}
		
		// friendly fire
		if(atkEntity instanceof Player){
			return;
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
            	Team team = plugin.manager.getTeam(shotPlayer);
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
				// Setting specific
				if(monster instanceof Wolf){
					Wolf wolf = (Wolf)monster;
					wolf.setAdult();
					wolf.setSitting(false);
					wolf.setTamed(true);
					wolf.setOwner(shotPlayer);
				}
        	}
        }
    }
	
	@EventHandler
	public void onVillagerTrade(VillagerAcquireTradeEvent e){
		
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e){
		if(e == null){
			return;
		}
		LivingEntity entity = e.getEntity();
		LivingEntity killer = entity.getKiller();
		// Mob dead
		Team deadTeam = plugin.manager.getTeam(entity);
		if(killer != null){
			Team killerTeam = plugin.manager.getTeam(killer);
			if(killerTeam != null){
				SpecialMob deadMob = plugin.configLoader.getMobFromType(entity.getType());
				if(deadMob != null && deadMob.isReward){
					int reward = deadMob.reward;
					if(deadTeam != null && deadTeam.equals(killerTeam) && deadMob.isTrade){
						reward = deadMob.price;
					}
					entity.getWorld().dropItem(entity.getLocation(), new ItemStack(Material.EMERALD, reward));
				}
			}
		}
		// Villager dead
		if(entity.getCustomName() != null && entity.getCustomName().equals("CoreVillager")){
			Team team = plugin.manager.getTeam(entity);
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
				plugin.titleSender.setTime(0.5, 3.0, 0.5);
				plugin.titleSender.sendTitle(title, null);
			}else{
				String deadCoreText = team.getPrefix() + team.getName() + " CORE IS DEAD!" + team.getSuffix() + " (" + villagerCount + " core left)";
				plugin.titleSender.setTime(0.1, 3.0, 0.5);
				plugin.titleSender.sendTitle(null, deadCoreText);
			}
		}
	}
	
	@EventHandler
	public void onTargetEntity(EntityTargetEvent e){
		Entity atkEntity = e.getEntity();
		Team atkTeam = plugin.manager.getTeam(atkEntity);
		Entity tgtEntity = e.getTarget();
		Team tgtTeam = plugin.manager.getTeam(tgtEntity);
		if(atkTeam != null && tgtTeam != null){
			if(atkTeam.equals(tgtTeam)){
				e.setCancelled(true);
				return;
			}
		}
	}
}
