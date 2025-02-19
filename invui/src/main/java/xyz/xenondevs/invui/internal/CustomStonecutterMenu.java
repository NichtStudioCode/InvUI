package xyz.xenondevs.invui.internal;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import xyz.xenondevs.invui.internal.util.MathUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * A custom stonecutter menu that allows for custom buttons by sending client-side recipes.
 */
public class CustomStonecutterMenu extends AbstractContainerMenu {
    
    private final Container inputResult = new SimpleContainer(2);
    private final DataSlot selectedSlot = DataSlot.standalone();
    private final CraftInventoryView<CustomStonecutterMenu, Inventory> bukkitView;
    private final BiConsumer<Integer, Integer> clickHandler;
    private final ServerPlayer player;
    
    private List<ItemStack> buttons = List.of();
    
    /**
     * Creates a new custom stonecutter menu.
     *
     * @param player       The player that will view this menu.
     * @param clickHandler The click handler that is called when a button is clicked, receiving the previous and new selected slot.
     */
    public CustomStonecutterMenu(org.bukkit.entity.Player player, BiConsumer<Integer, Integer> clickHandler) {
        this(((CraftPlayer) player).getHandle(), clickHandler);
    }
    
    /**
     * Creates a new custom stonecutter menu.
     *
     * @param player       The player that will view this menu.
     * @param clickHandler The click handler that is called when a button is clicked, receiving the previous and new selected slot.
     */
    private CustomStonecutterMenu(ServerPlayer player, BiConsumer<Integer, Integer> clickHandler) {
        super(MenuType.STONECUTTER, player.nextContainerCounter());
        this.player = player;
        this.clickHandler = clickHandler;
        this.bukkitView = new CraftInventoryView<>(player.getBukkitEntity(), new CraftInventory(inputResult), this);
        this.selectedSlot.set(-1);
        
        addDataSlot(selectedSlot);
        addSlot(new Slot(inputResult, 0, 20, 33));
        addSlot(new Slot(inputResult, 1, 143, 33));
        addStandardInventorySlots(player.getInventory(), 8, 84);
    }
    
    /**
     * Gets the selected slot (recipe) index of the stonecutter menu.
     *
     * @return The selected slot index.
     */
    public int getSelectedSlot() {
        return selectedSlot.get();
    }
    
    /**
     * Sets the selected slot (recipe) index of the stonecutter menu.
     *
     * @param selectedSlot The selected slot index.
     */
    public void setSelectedSlot(int selectedSlot) {
        if (this.selectedSlot.get() == selectedSlot)
            return;
        
        this.selectedSlot.set(selectedSlot);
        sendAllDataToRemote();
    }
    
    /**
     * Sets the buttons (recipes) of the stonecutter menu.
     * This requires a non-air item in the input slot.
     *
     * @param buttons The buttons.
     */
    public void setButtons(List<org.bukkit.inventory.ItemStack> buttons) {
        var nmsButtons = buttons.stream().map(CraftItemStack::unwrap).toList();
        this.buttons = nmsButtons;
        updateButtons(nmsButtons);
        
        // update input slot to trigger client-side recipe update, then re-send everything as the client clears it
        var p1 = new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, ItemStack.EMPTY);
        player.connection.send(p1);
        sendAllDataToRemote();
    }
    
    /**
     * Sets the buttons (recipes) of the stonecutter menu.
     * This requires a non-air item in the input slot.
     *
     * @param buttons The buttons.
     */
    private void updateButtons(List<ItemStack> buttons) {
        var recipeManager = MinecraftServer.getServer().getRecipeManager();
        
        var entries = Stream.concat(
            recipeManager.getSynchronizedStonecutterRecipes().entries().stream(),
            createRecipes(buttons).stream()
        ).toList();
        var stonecutterRecipes = new SelectableRecipe.SingleInputSet<>(entries);
        
        // fixme: this breaks if recipes are updated while the menu is open
        var packet = new ClientboundUpdateRecipesPacket(recipeManager.getSynchronizedItemProperties(), stonecutterRecipes);
        player.connection.send(packet);
    }
    
    /**
     * Creates the appropriate stonecutter recipes for the given buttons, using the item stack
     * on the input slot as the ingredient.
     *
     * @param buttons The buttons.
     * @return The stonecutter recipes.
     */
    private List<SelectableRecipe.SingleInputEntry<StonecutterRecipe>> createRecipes(List<ItemStack> buttons) {
        return buttons.stream().map(button -> {
            Ingredient ingredient = Ingredient.ofStacks(List.of(inputResult.getItem(0)));
            return new SelectableRecipe.SingleInputEntry<StonecutterRecipe>(
                ingredient,
                new SelectableRecipe<>(
                    new SlotDisplay.ItemStackSlotDisplay(button),
                    Optional.of(new RecipeHolder<>(
                        ResourceKey.create(
                            Registries.RECIPE,
                            ResourceLocation.fromNamespaceAndPath("invui", "fake_stonecutter_" + MathUtils.RANDOM.nextInt())
                        ),
                        new FakeStonecutterRecipe(ingredient, button.copy())
                    ))
                )
            );
        }).toList();
    }
    
    /**
     * Removes the client-side stonecutter recipes that were registered for this menu.
     */
    public void unregisterRecipes() {
        var recipeManager = MinecraftServer.getServer().getRecipeManager();
        var packet = new ClientboundUpdateRecipesPacket(recipeManager.getSynchronizedItemProperties(), recipeManager.getSynchronizedStonecutterRecipes());
        player.connection.send(packet);
    }
    
    /**
     * Updates the item on the given slot.
     *
     * @param slotId  The slot index.
     * @param stateId The new state id of this menu.
     * @param stack   The new item stack to place in the slot.
     */
    @Override
    public void setItem(int slotId, int stateId, ItemStack stack) {
        super.setItem(slotId, stateId, stack);
        if (slotId == 0)
            updateButtons(buttons);
    }
    
    /**
     * Handles a click on the menu button, which represents changing the recipe selection.
     *
     * @param player  The player that clicked the button.
     * @param clicked The index of the clicked button.
     * @return Whether the click was successful.
     */
    @Override
    public boolean clickMenuButton(Player player, int clicked) {
        int prev = selectedSlot.get();
        selectedSlot.set(clicked);
        
        clickHandler.accept(prev, clicked);
        
        // syncs output slot, updates value of selected slot
        sendAllDataToRemote();
        
        return false;
    }
    
    @Override
    public InventoryView getBukkitView() {
        return bukkitView;
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
    
}