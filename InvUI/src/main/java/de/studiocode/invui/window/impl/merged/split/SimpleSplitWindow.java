package de.studiocode.invui.window.impl.merged.split;

import de.studiocode.inventoryaccess.component.BaseComponentWrapper;
import de.studiocode.inventoryaccess.component.ComponentWrapper;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.util.InventoryUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public final class SimpleSplitWindow extends SplitWindow {
    
    public SimpleSplitWindow(Player player, ComponentWrapper title, GUI upperGui, GUI lowerGui, boolean closeable, boolean removeOnClose) {
        super(player, title, upperGui, lowerGui, InventoryUtils.createMatchingInventory(upperGui, ""), true, closeable, removeOnClose);
        register();
    }
    
    public SimpleSplitWindow(Player player, ComponentWrapper title, GUI upperGui, GUI lowerGui) {
        this(player, title, upperGui, lowerGui, true, true);
    }
    
    public SimpleSplitWindow(Player player, BaseComponent[] title, GUI upperGui, GUI lowerGui, boolean closeable, boolean removeOnClose) {
        this(player, new BaseComponentWrapper(title), upperGui, lowerGui, closeable, removeOnClose);
    }
    
    public SimpleSplitWindow(Player player, BaseComponent[] title, GUI upperGui, GUI lowerGui) {
        this(player, title, upperGui, lowerGui, true, true);
    }
    
    public SimpleSplitWindow(Player player, String title, GUI upperGui, GUI lowerGui, boolean closeable, boolean removeOnClose) {
        this(player, TextComponent.fromLegacyText(title), upperGui, lowerGui, closeable, removeOnClose);
    }
    
    public SimpleSplitWindow(Player player, String title, GUI upperGui, GUI lowerGui) {
        this(player, title, upperGui, lowerGui, true, true);
    }
    
}
