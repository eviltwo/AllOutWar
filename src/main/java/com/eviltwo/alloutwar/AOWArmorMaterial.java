package com.eviltwo.alloutwar;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public enum AOWArmorMaterial {
	WOODEN_SWORD("wooden_sword", Material.WOOD_SWORD, AOWArmorType.SWORD),
	STONE_SWORD("stone_sword", Material.STONE_SWORD, AOWArmorType.SWORD),
	IRON_SWORD("iron_sword", Material.IRON_SWORD, AOWArmorType.SWORD),
	DIAMOND_SWORD("diamond_sword", Material.DIAMOND_SWORD, AOWArmorType.SWORD),
	GOLDEN_SWORD("golden_sword", Material.GOLD_SWORD, AOWArmorType.SWORD),
	LEATHER_HELMET("leather_helmet", Material.LEATHER_HELMET, AOWArmorType.HELMET),
	CHAINMAIL_HELMET("chainmail_helmet", Material.CHAINMAIL_HELMET, AOWArmorType.HELMET),
	IRON_HELMET("iron_healmet", Material.IRON_HELMET, AOWArmorType.HELMET),
	DIAMOND_HELMET("diamond_helmet", Material.DIAMOND_HELMET, AOWArmorType.HELMET),
	GOLDEN_HELMET("golden_helmet", Material.GOLD_HELMET, AOWArmorType.HELMET),
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
    	SWORD,
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
