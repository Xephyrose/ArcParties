package org.xephyrose.arcParties;

public class ConfigManager {
    private ArcParties getPlugin() {
        return ArcParties.getPlugin(ArcParties.class);
    }

    public void reload() {
        getPlugin().getLogger().info("Reloading config...");
        getPlugin().reloadConfig();
        getPlugin().saveDefaultConfig();
        getPlugin().reloadConfig();
        getPlugin().getLogger().info("Config reloaded!");
        getPlugin().getLogger().info("xp share is currently set to " + isFeatureEnabled("share-xp"));
    }

    public boolean isFeatureEnabled(String feature) {
        return getPlugin().getConfig().getBoolean("features." + feature, false);
    }
}
