package xyz.xenondevs.invui.internal.menu;

import net.kyori.adventure.text.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.HashedStack;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.network.PacketListener;
import xyz.xenondevs.invui.internal.util.MathUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * A custom stonecutter menu that allows for custom buttons by sending client-side recipes.
 */
public class CustomStonecutterMenu extends CustomContainerMenu {
    
    private @Nullable BiConsumer<? super Integer, ? super Integer> clickHandler;
    private List<? extends ItemStack> buttons = List.of();
    
    /**
     * Creates a new custom stonecutter menu.
     *
     * @param player The player that will view this menu.
     */
    public CustomStonecutterMenu(org.bukkit.entity.Player player) {
        super(MenuType.STONECUTTER, player);
        dataSlots[0] = -1;
    }
    
    @Override
    public void open(Component title) {
        var pl = PacketListener.getInstance();
        pl.discard(player, ClientboundUpdateRecipesPacket.class);
        
        super.open(title);
    }
    
    @Override
    public void handleClosed() {
        var pl = PacketListener.getInstance();
        pl.stopDiscard(player, ClientboundUpdateRecipesPacket.class);
        
        super.handleClosed();
        
        // unregister recipes
        var recipeManager = MinecraftServer.getServer().getRecipeManager();
        var packet = new ClientboundUpdateRecipesPacket(recipeManager.getSynchronizedItemProperties(), recipeManager.getSynchronizedStonecutterRecipes());
        PacketListener.getInstance().injectOutgoing(player, packet);
    }
    
    @Override
    public void setItem(int slot, org.bukkit.inventory.@Nullable ItemStack item) {
        super.setItem(slot, item);
        if (slot == 0) {
            setNmsButtons(buttons);
            // client-side prediction clears output slot when input slot is modified
            remoteItems.set(1, HashedStack.EMPTY);
        }
    }
    
    /**
     * Sets the buttons (recipes) of the stonecutter menu.
     * This requires a non-air item in the input slot.
     *
     * @param buttons The buttons.
     */
    public void setButtons(List<? extends org.bukkit.inventory.@Nullable ItemStack> buttons) {
        var nmsButtons = buttons.stream().map(CraftItemStack::unwrap).toList();
        setNmsButtons(nmsButtons);
    }
    
    /**
     * Sets the buttons (recipes) of the stonecutter menu.
     * This requires a non-air item in the input slot.
     *
     * @param buttons The buttons.
     */
    private void setNmsButtons(List<? extends ItemStack> buttons) {
        this.buttons = buttons;
        updateButtons(buttons);
        
        // to force the client to recalculate the recipe list, the item needs to be removed and re-added
        // this also triggers result and selected slot to be reset, requiring them to be resent
        var packet = new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, ItemStack.EMPTY);
        PacketListener.getInstance().injectOutgoing(player, packet);
        remoteItems.set(0, HashedStack.EMPTY);
        remoteItems.set(1, HashedStack.EMPTY);
        remoteDataSlots[0] = -1;
        sendChangesToRemote();
    }
    
    /**
     * Creates and sends the required recipes to display the buttons.
     * This requires a non-air item in the input slot.
     *
     * @param buttons The buttons.
     */
    private void updateButtons(List<? extends ItemStack> buttons) {
        var recipeManager = MinecraftServer.getServer().getRecipeManager();
        var stonecutterRecipes = new SelectableRecipe.SingleInputSet<>(createRecipes(buttons));
        var packet = new ClientboundUpdateRecipesPacket(recipeManager.getSynchronizedItemProperties(), stonecutterRecipes);
        PacketListener.getInstance().injectOutgoing(player, packet);
    }
    
    /**
     * Creates the appropriate stonecutter recipes for the given buttons, using the item stack
     * on the input slot as the ingredient.
     *
     * @param buttons The buttons.
     * @return The stonecutter recipes.
     */
    private List<SelectableRecipe.SingleInputEntry<StonecutterRecipe>> createRecipes(List<? extends ItemStack> buttons) {
        return buttons.stream().map(button -> {
            Ingredient ingredient = Ingredient.ofStacks(List.of(items.getFirst()));
            return new SelectableRecipe.SingleInputEntry<>(
                ingredient,
                new SelectableRecipe<>(
                    new SlotDisplay.ItemStackSlotDisplay(button),
                    Optional.of(new RecipeHolder<>(
                        ResourceKey.create(
                            Registries.RECIPE,
                            Identifier.fromNamespaceAndPath("invui", "fake_stonecutter_" + MathUtils.RANDOM.nextInt())
                        ),
                        new StonecutterRecipe("", ingredient, button.copy())
                    ))
                )
            );
        }).toList();
    }
    
    @Override
    public void handleButtonClick(int clicked) {
        int prev = dataSlots[0];
        dataSlots[0] = clicked;
        remoteDataSlots[0] = clicked;
        remoteItems.set(1, HashedStack.EMPTY);
        
        if (clickHandler != null)
            clickHandler.accept(prev, clicked);
        
        sendChangesToRemote();
    }
    
    /**
     * Gets the selected slot (recipe) index of the stonecutter menu.
     *
     * @return The selected slot index.
     */
    public int getSelectedSlot() {
        return dataSlots[0];
    }
    
    /**
     * Sets the selected slot (recipe) index of the stonecutter menu.
     *
     * @param selectedSlot The selected slot index.
     */
    public void setSelectedSlot(int selectedSlot) {
        dataSlots[0] = selectedSlot;
    }
    
    /**
     * Sets the click handler that is called when a recipe button is clicked.
     *
     * @param clickHandler The click handler.
     */
    public void setClickHandler(BiConsumer<? super Integer, ? super Integer> clickHandler) {
        this.clickHandler = clickHandler;
    }
    
}