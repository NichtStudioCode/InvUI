package xyz.xenondevs.invui.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import xyz.xenondevs.invui.internal.util.SlotUtils;
import xyz.xenondevs.invui.item.Item;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class PagedItemsGuiTest {
    
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static ServerMock server;
    
    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
    }
    
    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }
    
    @Test
    public void testPagedItemsInitWithBuilder() {
        testPagedItemsInit(content -> PagedGui.itemsBuilder()
            .setStructure(
                "x x x",
                "x x x",
                "x x x"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(content)
            .build());
    }
    
    @Test
    public void testPagedItemsInitWithStaticFactoryFunction() {
        testPagedItemsInit(content ->
            PagedGui.ofItems(
                3, 3,
                content,
                SlotUtils.toSlotSet(new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8}, 3)
            )
        );
    }
    
    private void testPagedItemsInit(Function<? super List<Item>, ? extends Gui> createGui) {
        var content = List.of(
            Item.simple(ItemStack.of(Material.DIAMOND)),
            Item.simple(ItemStack.of(Material.DIAMOND)),
            Item.simple(ItemStack.of(Material.DIAMOND)),
            Item.simple(ItemStack.of(Material.DIAMOND))
        );
        
        var gui = createGui.apply(content);
        
        for (int i = 0; i < 4; i++) {
            assertSame(content.get(i), gui.getItem(i), "i=" + i);
        }
        for (int i = 4; i < 9; i++) {
            assertNull(gui.getSlotElement(i), "i=" + i);
        }
    }
    
}
