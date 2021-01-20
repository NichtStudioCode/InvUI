package de.studiocode.invgui;

import de.studiocode.invgui.window.WindowManager;
import org.bukkit.plugin.Plugin;

public class InvGui {
    
    private static InvGui instance;
    
    private Plugin plugin;
    
    public static InvGui getInstance() {
        return instance == null ? instance = new InvGui() : instance;
    }
    
    public Plugin getPlugin() {
        if (plugin == null)
            throw new IllegalStateException("Please set your plugin using InvGui.getInstance().setPlugin");
        return plugin;
    }
    
    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
    
    public void onDisable() {
        if (WindowManager.hasInstance())
            WindowManager.getInstance().getWindows()
                .forEach(w -> w.close(true));
    }
    
}
