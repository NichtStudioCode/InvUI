package xyz.xenondevs.inventoryaccess.r21;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftInventoryCartography;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import xyz.xenondevs.inventoryaccess.abstraction.inventory.CartographyInventory;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.inventoryaccess.util.ReflectionUtils;

import java.lang.reflect.Field;

class CartographyInventoryImpl extends CartographyTableMenu implements CartographyInventory {
    
    private static final Field RESULT_CONTAINER_FIELD = ReflectionUtils.getField(
        CartographyTableMenu.class,
        true,
        "SRF(net.minecraft.world.inventory.CartographyTableMenu resultContainer)"
    );
    
    private final ResultContainer resultContainer = ReflectionUtils.getFieldValue(RESULT_CONTAINER_FIELD, this);
    private final CraftInventoryView<CartographyTableMenu, CraftInventoryCartography> view;
    private final ServerPlayer player;
    
    private boolean open;
    
    public CartographyInventoryImpl(org.bukkit.entity.Player player, ComponentWrapper title) {
        this(((CraftPlayer) player).getHandle(), InventoryUtilsImpl.createNMSComponent(title));
    }
    
    public CartographyInventoryImpl(ServerPlayer player, Component title) {
        super(player.nextContainerCounter(), player.getInventory(), ContainerLevelAccess.create(player.level(), new BlockPos(0, 0, 0)));
        
        this.player = player;
        setTitle(title);
        CraftInventoryCartography inventory = new CraftInventoryCartography(container, resultContainer);
        view = new CraftInventoryView<>(player.getBukkitEntity(), inventory, this);
    }
    
    public void open() {
        open = true;
        
        // call the InventoryOpenEvent
        CraftEventFactory.callInventoryOpenEvent(player, this);
        
        // set active container
        player.containerMenu = this;
        
        // send open packet
        player.connection.send(new ClientboundOpenScreenPacket(containerId, MenuType.CARTOGRAPHY_TABLE, getTitle()));
        
        // send initial items
        NonNullList<ItemStack> itemsList = NonNullList.of(ItemStack.EMPTY, getItem(0), getItem(1), getItem(2));
        player.connection.send(new ClientboundContainerSetContentPacket(InventoryUtilsImpl.getActiveWindowId(player), incrementStateId(), itemsList, ItemStack.EMPTY));
        
        // init menu
        player.initMenu(this);
    }
    
    @Override
    public boolean isOpen() {
        return open;
    }
    
    public void sendItem(int slot) {
        player.connection.send(new ClientboundContainerSetSlotPacket(InventoryUtilsImpl.getActiveWindowId(player), slot, incrementStateId(), getItem(slot)));
    }
    
    public void setItem(int slot, ItemStack item) {
        if (slot < 2) container.setItem(slot, item);
        else resultContainer.setItem(0, item);
        
        if (open) sendItem(slot);
    }
    
    private ItemStack getItem(int slot) {
        if (slot < 2) return container.getItem(slot);
        else return resultContainer.getItem(0);
    }
    
    @Override
    public void setItem(int slot, org.bukkit.inventory.ItemStack itemStack) {
        setItem(slot, CraftItemStack.asNMSCopy(itemStack));
    }
    
    @Override
    public Inventory getBukkitInventory() {
        return view.getTopInventory();
    }
    
    // --- CartographyTableMenu ---
    
    @Override
    public CraftInventoryView getBukkitView() {
        return view;
    }
    
    @Override
    public void slotsChanged(Container container) {
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean canTakeItemForPickAll(ItemStack itemstack, Slot slot) {
        return true;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
    
    @Override
    protected void clearContainer(Player player, Container container) {
        // empty
    }
    
}
