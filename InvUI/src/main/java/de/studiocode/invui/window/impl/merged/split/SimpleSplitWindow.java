package de.studiocode.invui.window.impl.merged.split;

import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.util.InventoryUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class SimpleSplitWindow extends SplitWindow {
    
    public SimpleSplitWindow(Player player, BaseComponent[] title, GUI upperGui, GUI lowerGui, boolean closeable, boolean closeOnEvent) {
        super(player, title, upperGui, lowerGui, InventoryUtils.createMatchingInventory(upperGui, ""), true, closeable, closeOnEvent);
    }
    
    public SimpleSplitWindow(Player player, BaseComponent[] title, GUI upperGui, GUI lowerGui) {
        this(player, title, upperGui, lowerGui, true, true);
    }
    
    public SimpleSplitWindow(Player player, String title, GUI upperGui, GUI lowerGui, boolean closeable, boolean closeOnEvent) {
        this(player, TextComponent.fromLegacyText(title), upperGui, lowerGui, closeable, closeOnEvent);
    }
    
    public SimpleSplitWindow(Player player, String title, GUI upperGui, GUI lowerGui) {
        this(player, title, upperGui, lowerGui, true, true);
    }
    
}
