package xyz.xenondevs.inventoryaccess.r4;

import net.minecraft.server.v1_16_R2.*;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.abstraction.inventory.AnvilInventory;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;

import java.util.List;
import java.util.function.Consumer;

class AnvilInventoryImpl extends ContainerAnvil implements AnvilInventory {
    
    private final IChatBaseComponent title;
    private final List<Consumer<String>> renameHandlers;
    private final CraftInventoryView view;
    private final EntityPlayer player;
    
    private String text;
    private boolean open;
    
    public AnvilInventoryImpl(Player player, @NotNull ComponentWrapper title, List<Consumer<String>> renameHandlers) {
        this(((CraftPlayer) player).getHandle(), InventoryUtilsImpl.createNMSComponent(title), renameHandlers);
    }
    
    public AnvilInventoryImpl(EntityPlayer player, IChatBaseComponent title, List<Consumer<String>> renameHandlers) {
        super(player.nextContainerCounter(), player.inventory,
            ContainerAccess.at(player.getWorld(), new BlockPosition(0, 0, 0)));
        
        this.title = title;
        this.renameHandlers = renameHandlers;
        this.player = player;
        
        CraftInventoryAnvil inventory = new CraftInventoryAnvil(containerAccess.getLocation(),
            repairInventory, resultInventory, this);
        this.view = new CraftInventoryView(player.getBukkitEntity(), inventory, this);
    }
    
    public void open() {
        open = true;
        
        // call the InventoryOpenEvent
        CraftEventFactory.callInventoryOpenEvent(player, this);
        
        // set active container
        player.activeContainer = this;
        
        // send open packet
        player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(windowId, Containers.ANVIL, title));
        
        // send initial items
        NonNullList<ItemStack> itemsList = NonNullList.a(ItemStack.b, getItem(0), getItem(1), getItem(2));
        player.playerConnection.sendPacket(new PacketPlayOutWindowItems(InventoryUtilsImpl.getActiveWindowId(player), itemsList));
    }
    
    public void sendItem(int slot) {
        player.playerConnection.sendPacket(new PacketPlayOutSetSlot(InventoryUtilsImpl.getActiveWindowId(player), slot, getItem(slot)));
    }
    
    public void setItem(int slot, ItemStack item) {
        if (slot < 2) repairInventory.setItem(slot, item);
        else resultInventory.setItem(0, item);
        
        if (open) sendItem(slot);
    }
    
    private ItemStack getItem(int slot) {
        if (slot < 2) return repairInventory.getItem(slot);
        else return resultInventory.getItem(0);
    }
    
    @Override
    public void setItem(int slot, org.bukkit.inventory.ItemStack itemStack) {
        setItem(slot, CraftItemStack.asNMSCopy(itemStack));
    }
    
    @Override
    public @NotNull Inventory getBukkitInventory() {
        return view.getTopInventory();
    }
    
    @Override
    public String getRenameText() {
        return text;
    }
    
    @Override
    public boolean isOpen() {
        return open;
    }
    
    // --- ContainerAnvil ---
    
    @Override
    public CraftInventoryView getBukkitView() {
        return view;
    }
    
    /**
     * Called every tick to see if the {@link EntityHuman} can still use that container.
     * (Used to for checking the distance between the {@link EntityHuman} and the container
     * and closing the window when the distance gets too big.)
     *
     * @param entityhuman The {@link EntityHuman}
     * @return If the {@link EntityHuman} can still use that container
     */
    @Override
    public boolean canUse(EntityHuman entityhuman) {
        return true;
    }
    
    /**
     * Called when the rename text gets changed.
     *
     * @param s The new rename text
     */
    @Override
    public void a(String s) {
        // save rename text
        text = s;
        
        // call rename handlers
        if (renameHandlers != null)
            renameHandlers.forEach(handler -> handler.accept(s));
        
        // the client expects the item to change to it's new name and removes it from the inventory, so it needs to be sent again
        sendItem(2);
    }
    
    /**
     * Called when the container is closed to give the items back.
     *
     * @param entityhuman The {@link EntityHuman} that closed this container
     */
    @Override
    public void b(EntityHuman entityhuman) {
        open = false;
        // don't give them the items, they don't own them
    }
    
    /**
     * Called when both items in the {@link ContainerAnvil#repairInventory} were set to create
     * the resulting product, calculate the level cost and call the {@link PrepareAnvilEvent}.
     */
    @Override
    public void e() {
        // no
    }
    
    
}
