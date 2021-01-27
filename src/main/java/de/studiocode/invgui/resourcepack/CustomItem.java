package de.studiocode.invgui.resourcepack;

import de.studiocode.invgui.item.Item;
import de.studiocode.invgui.item.impl.SimpleItem;
import de.studiocode.invgui.item.itembuilder.ItemBuilder;
import org.bukkit.Material;

public enum CustomItem {
    
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
    LIGHT_CORNER_BOTTOM_RIGHT(10000028);
    
    private final ItemBuilder itemBuilder;
    private final Item item;
    
    CustomItem(int customModelData) {
        this.itemBuilder = new ItemBuilder(Material.POPPY)
            .setCustomModelData(customModelData)
            .setDisplayName("§0");
        
        this.item = new SimpleItem(itemBuilder);
    }
    
    /**
     * Gets a copy of the {@link ItemBuilder} for this {@link CustomItem}.
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
    
}
