package com.eviltwo.alloutwar;

import org.bukkit.Bukkit;

public class AOWTitleSender {
	
	public AOWTitleSender() {
	}
	
	public void setTime(double fadein, double show, double fadeout){
		int dIn = (int)(fadein * 20);
		int dShow = (int)(show * 20);
		int dOut = (int)(fadeout * 20);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a times "+dIn+" "+dShow+" "+dOut);
	}
	
	public void sendTitle(String titleText, String subTitleText){
		if(titleText == null){
			titleText = "";
		}
		if(subTitleText == null){
			subTitleText = "";
		}
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a subtitle [{\"text\":\""+subTitleText+"\"}]");
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title @a title [{\"text\":\""+titleText+"\"}]");
	}
}
