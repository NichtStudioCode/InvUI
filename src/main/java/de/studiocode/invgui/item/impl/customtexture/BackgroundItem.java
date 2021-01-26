package de.studiocode.invgui.item.impl.customtexture;

import de.studiocode.invgui.item.impl.SimpleItem;
import de.studiocode.invgui.item.itembuilder.ItemBuilder;
import de.studiocode.invgui.resourcepack.ForceResourcePack;
import org.bukkit.Material;

public class BackgroundItem extends SimpleItem {
    
    private static final BackgroundItem INSTANCE = new BackgroundItem();
    
    static {
        ForceResourcePack.getInstance(); // initializes ForceResourcePack which automatically activates forced resource packs (if not disabled)
    }
    
    private BackgroundItem() {
        super(new ItemBuilder(Material.POPPY).setDisplayName("§f").setCustomModelData(10000000));
    }
    
    public static BackgroundItem getInstance() {
        return INSTANCE;
    }
    
}
