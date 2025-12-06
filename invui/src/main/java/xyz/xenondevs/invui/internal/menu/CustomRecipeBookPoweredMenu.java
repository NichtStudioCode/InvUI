package xyz.xenondevs.invui.internal.menu;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.network.PacketListener;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Abstract superclass for all custom container menus that have a recipe book.
 */
public abstract class CustomRecipeBookPoweredMenu extends CustomContainerMenu {
    
    private @Nullable Consumer<? super Key> recipeSelectHandler;
    
    /**
     * Creates a new {@link CustomContainerMenu} for the specified player.
     *
     * @param menuType The type of the menu
     * @param player   The player that will see the menu
     */
    protected CustomRecipeBookPoweredMenu(MenuType<?> menuType, Player player) {
        super(menuType, player);
    }
    
    @Override
    public void open(Component title) {
        var pl = PacketListener.getInstance();
        pl.redirectIncoming(player, ServerboundPlaceRecipePacket.class, this::handleRecipePlace);
        super.open(title);
    }
    
    @Override
    public void handleClosed() {
        var pl = PacketListener.getInstance();
        pl.removeRedirect(player, ServerboundPlaceRecipePacket.class);
        super.handleClosed();
    }
    
    /**
     * Displays a ghost recipe of the given id in the menu.
     *
     * @param id The recipe id
     */
    public void sendGhostRecipe(Key id) {
        var entryRef = new AtomicReference<@Nullable RecipeDisplayEntry>();
        var rk = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(id.namespace(), id.value()));
        MinecraftServer.getServer().getRecipeManager().listDisplaysForRecipe(rk, entryRef::set);
        var entry = entryRef.get();
        if (entry == null)
            return;
        
        PacketListener.getInstance().injectOutgoing(
            player,
            new ClientboundPlaceGhostRecipePacket(containerId, entry.display())
        );
    }
    
    /**
     * Sets the handler that is called when a recipe is selected in the recipe book.
     *
     * @param recipeClickHandler The recipe click handler
     */
    public void setRecipeClickHandler(Consumer<? super Key> recipeClickHandler) {
        this.recipeSelectHandler = recipeClickHandler;
    }
    
    private void handleRecipePlace(ServerboundPlaceRecipePacket packet) {
        if (recipeSelectHandler == null)
            return;
        var displayInfo = MinecraftServer.getServer()
            .getRecipeManager()
            .getRecipeFromDisplay(packet.recipe());
        if (displayInfo == null)
            return;
        var rl = displayInfo
            .parent()
            .id()
            .identifier();
        var key = Key.key(rl.getNamespace(), rl.getPath());
        recipeSelectHandler.accept(key);
    }
    
}
