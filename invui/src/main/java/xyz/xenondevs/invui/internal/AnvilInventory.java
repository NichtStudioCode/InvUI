package xyz.xenondevs.invui.internal;

import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.view.CraftAnvilView;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.function.Consumer;

public class AnvilInventory extends AnvilMenu {
    
    private final List<Consumer<String>> renameHandlers;
    private final CraftAnvilView view;
    private final ServerPlayer player;
    
    private String text = "";
    private boolean open;
    
    public AnvilInventory(org.bukkit.entity.Player player, net.kyori.adventure.text.Component title, List<Consumer<String>> renameHandlers) {
        this(((CraftPlayer) player).getHandle(), PaperAdventure.asVanilla(title), renameHandlers);
    }
    
    public AnvilInventory(ServerPlayer player, Component title, List<Consumer<String>> renameHandlers) {
        super(player.nextContainerCounter(), player.getInventory(),
            ContainerLevelAccess.create(player.level(), new BlockPos(0, 0, 0)));
        
        setTitle(title);
        this.renameHandlers = renameHandlers;
        this.player = player;
        
        CraftInventoryAnvil inventory = new CraftInventoryAnvil(access.getLocation(), inputSlots, resultSlots);
        this.view = new CraftAnvilView(player.getBukkitEntity(), inventory, this);
    }
    
    public void open() {
        open = true;
        
        // call the InventoryOpenEvent
        CraftEventFactory.callInventoryOpenEvent(player, this);
        
        // set active container
        player.containerMenu = this;
        
        // send open packet
        player.connection.send(new ClientboundOpenScreenPacket(containerId, MenuType.ANVIL, getTitle()));
        
        // send initial items
        NonNullList<ItemStack> itemsList = NonNullList.of(ItemStack.EMPTY, getItem(0), getItem(1), getItem(2));
        player.connection.send(new ClientboundContainerSetContentPacket(player.containerMenu.containerId, incrementStateId(), itemsList, ItemStack.EMPTY));
        
        // init menu
        player.initMenu(this);
    }
    
    public void sendItem(int slot) {
        player.connection.send(new ClientboundContainerSetSlotPacket(player.containerMenu.containerId, incrementStateId(), slot, getItem(slot)));
    }
    
    public void setItem(int slot, ItemStack item) {
        if (slot < 2) inputSlots.setItem(slot, item);
        else resultSlots.setItem(0, item);
        
        if (open) sendItem(slot);
    }
    
    private ItemStack getItem(int slot) {
        if (slot < 2) return inputSlots.getItem(slot);
        else return resultSlots.getItem(0);
    }
    
    public void setItem(int slot, org.bukkit.inventory.ItemStack itemStack) {
        setItem(slot, CraftItemStack.asNMSCopy(itemStack));
    }
    
    public Inventory getBukkitInventory() {
        return view.getTopInventory();
    }
    
    public String getRenameText() {
        return text;
    }
    
    public boolean isOpen() {
        return open;
    }
    
    // --- AnvilMenu ---
    
    @Override
    public CraftAnvilView getBukkitView() {
        return view;
    }
    
    /**
     * Called every tick to see if the {@link Player} can still use that container.
     * (Used to for checking the distance between the {@link Player} and the container
     * and closing the window when the distance gets too big.)
     *
     * @param player The {@link Player}
     * @return If the {@link Player} can still use that container
     */
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
    
    /**
     * Called when the rename text gets changed.
     *
     * @param s The new rename text
     */
    @Override
    public boolean setItemName(String s) {
        // save rename text
        text = s;
        
        // call rename handlers
        renameHandlers.forEach(handler -> handler.accept(s));
        
        // the client expects the item to change to its new name and removes it from the inventory, so it needs to be sent again
        sendItem(2);
        
        return false;
    }
    
    /**
     * Called when the container is closed to give the items back.
     *
     * @param player The {@link Player} that closed this container
     */
    @Override
    public void removed(Player player) {
        open = false;
    }
    
    /**
     * Called when the container gets closed to put items back into a players
     * inventory or drop them in the world.
     *
     * @param player    The {@link Player} that closed this container
     * @param container The container
     */
    @Override
    protected void clearContainer(Player player, Container container) {
        open = false;
    }
    
    /**
     * Called when both items in the {@link AnvilMenu#inputSlots} were set to create
     * the resulting product, calculate the level cost and call the {@link PrepareAnvilEvent}.
     */
    @Override
    public void createResult() {
        // empty
    }
    
}
