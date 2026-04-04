package xyz.xenondevs.invui.internal.menu;

import net.kyori.adventure.text.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
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
    
    /**
     * An ingredient that matches all item types (except air, since that is forbidden).
     * Using an everything-ingredient instead of changing the ingredient based on the input slot
     * allows changing the input item without needing to resend recipes (which would reset the scroll bar).
     */
    private static final Ingredient INGREDIENT = Ingredient.ofStacks(
        BuiltInRegistries.ITEM.stream()
            .filter(item -> item != Items.AIR)
            .map(ItemStack::new)
            .toList()
    );
    
    private @Nullable BiConsumer<? super Integer, ? super Integer> clickHandler;
    
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
    protected UpdateType handleClick(ServerboundContainerClickPacket packet) {
        remoteDataSlots[0] = -1;
        var updateType = super.handleClick(packet);
        return UpdateType.DIRTY.or(updateType);
    }
    
    @Override
    public void setItem(int slot, org.bukkit.inventory.@Nullable ItemStack item) {
        super.setItem(slot, item);
        if (slot == 0) {
            // client-side prediction may clear the output slot (e.g. when shift-clicking into input)
            forceRemoteItem(1, DIRTY_MARKER);
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
        // create and send the recipes required for displaying the buttons
        var recipeManager = MinecraftServer.getServer().getRecipeManager();
        var stonecutterRecipes = new SelectableRecipe.SingleInputSet<>(createRecipes(buttons));
        var recipesPacket = new ClientboundUpdateRecipesPacket(recipeManager.getSynchronizedItemProperties(), stonecutterRecipes);
        PacketListener.getInstance().injectOutgoing(player, recipesPacket);
        
        // to force the client to recalculate the recipe list, the item needs to be removed and re-added
        // this also triggers result and selected slot to be reset, requiring them to be resent
        var setInputPacket = new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, ItemStack.EMPTY);
        PacketListener.getInstance().injectOutgoing(player, setInputPacket);
        forceRemoteItem(0, ItemStack.EMPTY);
        forceRemoteItem(1, ItemStack.EMPTY);
        remoteDataSlots[0] = -1;
        sendChangesToRemote(-1);
    }
    
    /**
     * Creates the appropriate stonecutter recipes for the given buttons, using the item stack
     * on the input slot as the ingredient.
     *
     * @param buttons The buttons.
     * @return The stonecutter recipes.
     */
    private List<SelectableRecipe.SingleInputEntry<StonecutterRecipe>> createRecipes(List<? extends ItemStack> buttons) {
        return buttons.stream()
            .map(ItemStackTemplate::fromNonEmptyStack)
            .map(button -> new SelectableRecipe.SingleInputEntry<>(
            INGREDIENT,
            new SelectableRecipe<>(
                new SlotDisplay.ItemStackSlotDisplay(button),
                Optional.of(new RecipeHolder<>(
                    ResourceKey.create(
                        Registries.RECIPE,
                        Identifier.fromNamespaceAndPath("invui", "fake_stonecutter_" + MathUtils.RANDOM.nextInt())
                    ),
                    new StonecutterRecipe(new Recipe.CommonInfo(false), INGREDIENT, button)
                ))
            )
        )).toList();
    }
    
    @Override
    public UpdateType handleButtonClick(int clicked) {
        int prev = dataSlots[0];
        dataSlots[0] = clicked;
        remoteDataSlots[0] = clicked;
        forceRemoteItem(1, ItemStack.EMPTY);
        
        if (clickHandler != null)
            clickHandler.accept(prev, clicked);
        
        return UpdateType.DIRTY;
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