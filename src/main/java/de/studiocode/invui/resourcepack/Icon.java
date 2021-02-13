package de.studiocode.invui.resourcepack;

import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.impl.SimpleItem;
import de.studiocode.invui.item.ItemBuilder;
import org.bukkit.Material;

import java.util.HashMap;

/**
 * Custom-textured Items that look like they are a part of the inventory.
 * Needs the resource pack to work, activate ResourcePack forcing
 * via {@link ForceResourcePack#setActivated(boolean)}.
 */
public enum Icon {
    
    BACKGROUND(10000000),
    ARROW_1_UP(10000001),
    ARROW_1_RIGHT(10000002),
    ARROW_1_DOWN(10000003),
    ARROW_1_LEFT(10000004),
    LIGHT_ARROW_1_UP(10000005),
    LIGHT_ARROW_1_RIGHT(10000006),
    LIGHT_ARROW_1_DOWN(10000007),
    LIGHT_ARROW_1_LEFT(10000008),
    ARROW_2_UP(10000009),
    ARROW_2_RIGHT(10000010),
    ARROW_2_DOWN(10000011),
    ARROW_2_LEFT(10000012),
    LIGHT_ARROW_2_UP(10000013),
    LIGHT_ARROW_2_RIGHT(10000014),
    LIGHT_ARROW_2_DOWN(10000015),
    LIGHT_ARROW_2_LEFT(10000016),
    HORIZONTAL_LINE(10000017),
    VERTICAL_LINE(10000018),
    LIGHT_HORIZONTAL_LINE(10000019),
    LIGHT_VERTICAL_LINE(10000020),
    CORNER_TOP_LEFT(10000021),
    CORNER_TOP_RIGHT(10000022),
    CORNER_BOTTOM_LEFT(10000023),
    CORNER_BOTTOM_RIGHT(10000024),
    LIGHT_CORNER_TOP_LEFT(10000025),
    LIGHT_CORNER_TOP_RIGHT(10000026),
    LIGHT_CORNER_BOTTOM_LEFT(10000027),
    LIGHT_CORNER_BOTTOM_RIGHT(10000028),
    HORIZONTAL_DOWN(10000029),
    HORIZONTAL_UP(10000030),
    VERTICAL_LEFT(10000031),
    VERTICAL_RIGHT(10000032),
    LIGHT_HORIZONTAL_DOWN(10000033),
    LIGHT_HORIZONTAL_UP(10000034),
    LIGHT_VERTICAL_LEFT(10000035),
    LIGHT_VERTICAL_RIGHT(10000036),
    VERTICAL_HORIZONTAL(10000037),
    LIGHT_VERTICAL_HORIZONTAL(10000038),
    X(10000039),
    LIGHT_X(10000040),
    RED_X(10000041),
    CHECKMARK(10000042),
    LIGHT_CHECKMARK(10000043),
    GREEN_CHECKMARK(10000044),
    ANVIL_OVERLAY_PLUS(10000045),
    ANVIL_OVERLAY_ARROW(10000046),
    ANVIL_OVERLAY_ARROW_LEFT(10000047),
    LENS(10000048),
    LIGHT_LENS(10000049),
    CHECKBOX(10000050),
    CHECKBOX_CHECKED(10000051),
    LIGHT_CHECKBOX(10000052),
    LIGHT_CHECKBOX_CHECKED(10000053),
    HORIZONTAL_DOTS(10000054),
    LIGHT_HORIZONTAL_DOTS(10000055),
    VERTICAL_DOTS(10000056),
    LIGHT_VERTICAL_DOTS(10000057),
    CIRCLE(10000058),
    LIGHT_CIRCLE(10000059),
    BOX(10000060),
    LIGHT_BOX(10000061);
    
    private final ItemBuilder itemBuilder;
    private final Item item;
    
    Icon(int customModelData) {
        this.itemBuilder = new ItemBuilder(Material.POPPY)
            .setCustomModelData(customModelData)
            .setDisplayName("§0");
        
        this.item = new SimpleItem(itemBuilder);
    }
    
    /**
     * Gets a copy of the {@link ItemBuilder} for this {@link Icon}.
     *
     * @return A copy of the {@link ItemBuilder}
     */
    public ItemBuilder getItemBuilder() {
        return itemBuilder.clone();
    }
    
    /**
     * Gets a non-clickable {@link Item} with this texture.
     *
     * @return A non-clickable {@link Item} with this texture.
     */
    public Item getItem() {
        return item;
    }
    
    /**
     * An icon version of a {@link Material}.
     * An item displayed in front of {@link Icon#BACKGROUND}
     */
    public enum MaterialIcon {
        
        FULL(9999998),
        NORMAL(9999999);
        
        private final int customModelData;
        
        private final HashMap<Material, Item> items = new HashMap<>();
        
        MaterialIcon(int customModelData) {
            this.customModelData = customModelData;
        }
        
        public ItemBuilder getItemBuilder(Material material) {
            return new ItemBuilder(material).setCustomModelData(customModelData);
        }
        
        public Item getItem(Material material) {
            if (!items.containsKey(material)) {
                items.put(material, new SimpleItem(getItemBuilder(material)));
            }
            
            return items.get(material);
        }
        
    }
    
}
