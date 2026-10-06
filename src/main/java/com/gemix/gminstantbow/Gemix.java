package com.gemix.gminstantbow;

import com.gemix.gminstantbow.listener.BowShootListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Gemix extends JavaPlugin {

    private static Gemix instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        getServer().getPluginManager().registerEvents(new BowShootListener(this), this);
        getLogger().info("GMInstantBow by Gemix has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GMInstantBow has been disabled!");
    }

    public static Gemix getInstance() {
        return instance;
    }
}
