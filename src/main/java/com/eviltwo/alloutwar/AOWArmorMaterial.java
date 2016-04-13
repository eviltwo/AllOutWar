package com.eviltwo.alloutwar;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public enum AOWArmorMaterial {
	WOODEN_SWORD("wooden_sword", Material.WOOD_SWORD, AOWArmorType.WEAPON),
	STONE_SWORD("stone_sword", Material.STONE_SWORD, AOWArmorType.WEAPON),
	IRON_SWORD("iron_sword", Material.IRON_SWORD, AOWArmorType.WEAPON),
	DIAMOND_SWORD("diamond_sword", Material.DIAMOND_SWORD, AOWArmorType.WEAPON),
	GOLDEN_SWORD("golden_sword", Material.GOLD_SWORD, AOWArmorType.WEAPON),
	// BOW("bow", Material.BOW, AOWArmorType.WEAPON),
	LEATHER_HELMET("leather_helmet", Material.LEATHER_HELMET, AOWArmorType.HELMET),
	CHAINMAIL_HELMET("chainmail_helmet", Material.CHAINMAIL_HELMET, AOWArmorType.HELMET),
	IRON_HELMET("iron_healmet", Material.IRON_HELMET, AOWArmorType.HELMET),
	DIAMOND_HELMET("diamond_helmet", Material.DIAMOND_HELMET, AOWArmorType.HELMET),
	GOLDEN_HELMET("golden_helmet", Material.GOLD_HELMET, AOWArmorType.HELMET),
	LEATHER_CHESTPLATE("leather_helmet", Material.LEATHER_CHESTPLATE, AOWArmorType.CHESTPLATE),
	CHAINMAIL_CHESTPLATE("chainmail_helmet", Material.CHAINMAIL_CHESTPLATE, AOWArmorType.CHESTPLATE),
	IRON_CHESTPLATE("iron_healmet", Material.IRON_CHESTPLATE, AOWArmorType.CHESTPLATE),
	DIAMOND_CHESTPLATE("diamond_helmet", Material.DIAMOND_CHESTPLATE, AOWArmorType.CHESTPLATE),
	GOLDEN_CHESTPLATE("golden_helmet", Material.GOLD_CHESTPLATE, AOWArmorType.CHESTPLATE),
	LEATHER_LEGGINGS("leather_helmet", Material.LEATHER_LEGGINGS, AOWArmorType.LEGGINGS),
	CHAINMAIL_LEGGINGS("chainmail_helmet", Material.CHAINMAIL_LEGGINGS, AOWArmorType.LEGGINGS),
	IRON_LEGGINGS("iron_healmet", Material.IRON_LEGGINGS, AOWArmorType.LEGGINGS),
	DIAMOND_LEGGINGS("diamond_helmet", Material.DIAMOND_LEGGINGS, AOWArmorType.LEGGINGS),
	GOLDEN_LEGGINGS("golden_helmet", Material.GOLD_LEGGINGS, AOWArmorType.LEGGINGS),
	LEATHER_BOOTS("leather_helmet", Material.LEATHER_BOOTS, AOWArmorType.BOOTS),
	CHAINMAIL_BOOTS("chainmail_helmet", Material.CHAINMAIL_BOOTS, AOWArmorType.BOOTS),
	IRON_BOOTS("iron_healmet", Material.IRON_BOOTS, AOWArmorType.BOOTS),
	DIAMOND_BOOTS("diamond_helmet", Material.DIAMOND_BOOTS, AOWArmorType.BOOTS),
	GOLDEN_BOOTS("golden_helmet", Material.GOLD_BOOTS, AOWArmorType.BOOTS),
	UNKNOWN(null, null, null);
	
	private String name;
	private Material material;
	private AOWArmorType type;
	
	private static final Map<String, AOWArmorMaterial> NAME_MAP = new HashMap<String, AOWArmorMaterial>();
	private static final Map<Material, AOWArmorMaterial> Material_MAP = new HashMap<Material, AOWArmorMaterial>();
    static {
        for (AOWArmorMaterial mat : values()) {
            if (mat.name != null) {
                NAME_MAP.put(mat.name.toLowerCase(), mat);
            }
            if (mat.material != null) {
                Material_MAP.put(mat.material, mat);
            }
        }
    }
    
    private AOWArmorMaterial(String name, Material material, AOWArmorType type) {
    	this.name = name;
    	this.material = material;
    	this.type = type;
    }
    
    public enum AOWArmorType {
    	WEAPON,
    	HELMET,
    	CHESTPLATE,
    	LEGGINGS,
    	BOOTS,
    	UNKNOWN;
    }
    
    public Material getMaterial(){
    	return material;
    }
    
    public AOWArmorType getType(){
    	return type;
    }
    
    public static AOWArmorMaterial fromName(String name){
    	if (name == null) {
            return null;
        }
        return NAME_MAP.get(name.toLowerCase());
    }
    
    public static AOWArmorMaterial fromMaterial(Material material){
    	if(material == null){
    		return null;
    	}
    	return Material_MAP.get(material);
    }
}
