package de.studiocode.inventoryaccess.v1_16_R2.inventory;

import de.studiocode.inventoryaccess.api.abstraction.inventory.AnvilInventory;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftInventoryAnvil;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;

import java.util.function.Consumer;

public class AnvilInventoryImpl extends ContainerAnvil implements AnvilInventory {
    
    private final String title;
    private final Consumer<String> renameHandler;
    private final CraftInventoryView view;
    private final EntityPlayer player;
    
    private String text;
    private boolean open;
    
    public AnvilInventoryImpl(Player player, String title, Consumer<String> renameHandler) {
        this(((CraftPlayer) player).getHandle(), title, renameHandler);
    }
    
    public AnvilInventoryImpl(EntityPlayer player, String title, Consumer<String> renameHandler) {
        super(player.nextContainerCounter(), player.inventory,
            ContainerAccess.at(player.getWorld(), new BlockPosition(Integer.MAX_VALUE, 0, 0)));
        
        this.title = title;
        this.renameHandler = renameHandler;
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
        player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(windowId, Containers.ANVIL, new ChatMessage(title)));
        
        // send initial items
        NonNullList<ItemStack> itemsList = NonNullList.a(ItemStack.b, getItem(0), getItem(1), getItem(2));
        player.playerConnection.sendPacket(new PacketPlayOutWindowItems(getActiveWindowId(player), itemsList));
    }
    
    public void sendItem(int slot) {
        player.playerConnection.sendPacket(new PacketPlayOutSetSlot(getActiveWindowId(player), slot, getItem(slot)));
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
    
    private int getActiveWindowId(EntityPlayer player) {
        Container container = player.activeContainer;
        return container == null ? -1 : container.windowId;
    }
    
    @Override
    public void setItem(int slot, org.bukkit.inventory.ItemStack itemStack) {
        setItem(slot, CraftItemStack.asNMSCopy(itemStack));
    }
    
    @Override
    public Inventory getBukkitInventory() {
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
        
        // call the rename handler
        if (renameHandler != null) renameHandler.accept(s);
        
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
