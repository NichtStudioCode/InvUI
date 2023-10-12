package xyz.xenondevs.inventoryaccess.r4;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventoryCartography;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.abstraction.inventory.CartographyInventory;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.inventoryaccess.util.ReflectionUtils;

import java.lang.reflect.Field;

class CartographyInventoryImpl extends ContainerCartography implements CartographyInventory {
    
    private static final Field RESULT_CONTAINER_FIELD = ReflectionUtils.getField(ContainerCartography.class, true, "resultInventory");
    
    private final InventoryCraftResult resultInventory = ReflectionUtils.getFieldValue(RESULT_CONTAINER_FIELD, this);
    private final IChatBaseComponent title;
    private final CraftInventoryView view;
    private final EntityPlayer player;
    
    private boolean open;
    
    public CartographyInventoryImpl(Player player, @NotNull ComponentWrapper title) {
        this(((CraftPlayer) player).getHandle(), InventoryUtilsImpl.createNMSComponent(title));
    }
    
    public CartographyInventoryImpl(EntityPlayer player, IChatBaseComponent title) {
        super(player.nextContainerCounter(), player.inventory, ContainerAccess.at(player.getWorld(), new BlockPosition(0, 0, 0)));
        
        this.player = player;
        this.title = title;
        CraftInventoryCartography inventory = new CraftInventoryCartography(this.inventory, resultInventory);
        view = new CraftInventoryView(player.getBukkitEntity(), inventory, this);
    }
    
    public void open() {
        open = true;
        
        // call the InventoryOpenEvent
        CraftEventFactory.callInventoryOpenEvent(player, this);
        
        // set active container
        player.activeContainer = this;
        
        // send open packet
        player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(windowId, Containers.CARTOGRAPHY_TABLE, title));
        
        // send initial items
        NonNullList<ItemStack> itemsList = NonNullList.a(ItemStack.a, getItem(0), getItem(1), getItem(2));
        player.playerConnection.sendPacket(new PacketPlayOutWindowItems(InventoryUtilsImpl.getActiveWindowId(player), itemsList));
    }
    
    @Override
    public boolean isOpen() {
        return open;
    }
    
    public void sendItem(int slot) {
        player.playerConnection.sendPacket(new PacketPlayOutSetSlot(InventoryUtilsImpl.getActiveWindowId(player), slot, getItem(slot)));
    }
    
    public void setItem(int slot, ItemStack item) {
        if (slot < 2) inventory.setItem(slot, item);
        else resultInventory.setItem(0, item);
        
        if (open) sendItem(slot);
    }
    
    private ItemStack getItem(int slot) {
        if (slot < 2) return inventory.getItem(slot);
        else return resultInventory.getItem(0);
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
    public void a(IInventory inventory) {
    }
    
    @Override
    public ItemStack shiftClick(EntityHuman entityhuman, int i) {
        return ItemStack.a;
    }
    
    @Override
    public boolean a(ItemStack itemstack, Slot slot) {
        return true;
    }
    
    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return true;
    }
    
    @Override
    public void b(EntityHuman entityHuman) {
        // empty
    }
    
}
