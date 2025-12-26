package xyz.xenondevs.invui.inventory;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.internal.util.DataUtils;
import xyz.xenondevs.invui.internal.util.FakeInventoryView;
import xyz.xenondevs.invui.util.ItemUtils;

import java.io.*;
import java.util.Arrays;
import java.util.BitSet;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * A serializable {@link Inventory} implementation that is identified by a {@link UUID} and backed by a simple {@link ItemStack} array.
 *
 * @see VirtualInventoryManager
 */
public final class VirtualInventory extends Inventory {
    
    private final UUID uuid;
    private final @Nullable ItemStack[] items;
    private int[] maxStackSizes;
    
    /**
     * Constructs a new {@link VirtualInventory}
     *
     * @param uuid          The {@link UUID} of this {@link VirtualInventory}. Can be null, only used for serialization.
     * @param size          The amount of slots this {@link VirtualInventory} has.
     * @param items         A predefined array of content. Can be null.
     * @param maxStackSizes An array of maximum allowed stack sizes for each slot in the {@link VirtualInventory}. Can be null for 64.
     * @throws IllegalArgumentException If the given size does not match the length of the items array or the length of the stackSizes array.
     */
    public VirtualInventory(@Nullable UUID uuid, int size, @Nullable ItemStack @Nullable [] items, int @Nullable [] maxStackSizes) {
        super(size);
        this.uuid = uuid == null ? new UUID(0L, 0L) : uuid;
        this.items = new ItemStack[size];
        
        if (maxStackSizes != null) {
            if (size != maxStackSizes.length)
                throw new IllegalArgumentException("Inventory size does not match maxStackSizes array length");
            this.maxStackSizes = maxStackSizes;
        } else {
            this.maxStackSizes = new int[size];
            Arrays.fill(this.maxStackSizes, 64);
        }
        
        if (items != null) {
            if (size != items.length)
                throw new IllegalArgumentException("Inventory size does not match items array length");
            
            for (int i = 0; i < size; i++) {
                var itemStack = items[i];
                if (ItemUtils.isEmpty(itemStack))
                    continue;
                
                this.items[i] = itemStack.clone();
            }
        }
    }
    
    /**
     * Constructs a new {@link VirtualInventory}
     *
     * @param size          The amount of slots this {@link VirtualInventory} has.
     * @param items         A predefined array of content. Can be null.
     * @param maxStackSizes An array of maximum allowed stack sizes for each slot in the {@link VirtualInventory}. Can be null for 64.
     */
    public VirtualInventory(int size, @Nullable ItemStack @Nullable [] items, int @Nullable [] maxStackSizes) {
        this(null, size, items, maxStackSizes);
    }
    
    /**
     * Constructs a new {@link VirtualInventory}
     *
     * @param uuid          The {@link UUID} of this {@link VirtualInventory}. Can be null, only used for serialization.
     * @param items         A predefined array of content.
     * @param maxStackSizes An array of maximum allowed stack sizes for each slot in the {@link VirtualInventory}. Can be null for 64.
     */
    public VirtualInventory(@Nullable UUID uuid, @Nullable ItemStack[] items, int @Nullable [] maxStackSizes) {
        this(uuid, items.length, items, maxStackSizes);
    }
    
    /**
     * Constructs a new {@link VirtualInventory}
     *
     * @param items         A predefined array of content.
     * @param maxStackSizes An array of maximum allowed stack sizes for each slot in the {@link VirtualInventory}. Can be null for 64.
     */
    public VirtualInventory(@Nullable ItemStack[] items, int @Nullable [] maxStackSizes) {
        this(null, items.length, items, maxStackSizes);
    }
    
    /**
     * Constructs a new {@link VirtualInventory}
     *
     * @param uuid  The {@link UUID} of this {@link VirtualInventory}. Can be null, only used for serialization.
     * @param items A predefined array of content.
     */
    public VirtualInventory(@Nullable UUID uuid, @Nullable ItemStack[] items) {
        this(uuid, items.length, items, null);
    }
    
    /**
     * Constructs a new {@link VirtualInventory}
     *
     * @param items A predefined array of content.
     */
    public VirtualInventory(@Nullable ItemStack[] items) {
        this(null, items.length, items, null);
    }
    
    /**
     * Constructs a new {@link VirtualInventory}
     *
     * @param uuid          The {@link UUID} of this {@link VirtualInventory}. Can be null, only used for serialization.
     * @param maxStackSizes An array of maximum allowed stack sizes for each slot in the {@link VirtualInventory}.
     */
    public VirtualInventory(@Nullable UUID uuid, int[] maxStackSizes) {
        this(uuid, maxStackSizes.length, null, maxStackSizes);
    }
    
    /**
     * Constructs a new {@link VirtualInventory}
     *
     * @param maxStackSizes An array of maximum allowed stack sizes for each slot in the {@link VirtualInventory}.
     */
    public VirtualInventory(int[] maxStackSizes) {
        this(null, maxStackSizes.length, null, maxStackSizes);
    }
    
    /**
     * Constructs a new {@link VirtualInventory}
     *
     * @param uuid The {@link UUID} of this {@link VirtualInventory}. Can be null, only used for serialization.
     * @param size The amount of slots this {@link VirtualInventory} has.
     */
    public VirtualInventory(@Nullable UUID uuid, int size) {
        this(uuid, size, null, null);
    }
    
    /**
     * Constructs a new {@link VirtualInventory}
     *
     * @param size The amount of slots this {@link VirtualInventory} has.
     */
    public VirtualInventory(int size) {
        this(null, size, null, null);
    }
    
    /**
     * Creates a copy of the given {@link VirtualInventory}.
     *
     * @param inventory The {@link VirtualInventory} to copy.
     */
    public VirtualInventory(VirtualInventory inventory) {
        this(inventory.uuid, inventory.getSize(), ItemUtils.clone(inventory.items), inventory.maxStackSizes.clone());
        for (var category : OperationCategory.values()) {
            setIterationOrder(category, inventory.getIterationOrder(category));
            setGuiPriority(category, inventory.getGuiPriority(category));
        }
        setPreUpdateHandlers(inventory.getPreUpdateHandlers());
        setPostUpdateHandlers(inventory.getPostUpdateHandlers());
    }
    
    /**
     * Deserializes a {@link VirtualInventory} from a byte array.
     *
     * @param bytes The byte array to deserialize from.
     * @return The deserialized {@link VirtualInventory}.
     */
    public static VirtualInventory deserialize(byte[] bytes) {
        try {
            return deserialize(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            // ByteArrayInputStream should not throw IOException
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Deserializes a {@link VirtualInventory} from an {@link InputStream}.
     *
     * @param in The {@link InputStream} to deserialize from.
     * @return The deserialized {@link VirtualInventory}.
     * @throws IOException If an I/O error has occurred.
     */
    public static VirtualInventory deserialize(InputStream in) throws IOException {
        DataInputStream din = new DataInputStream(in);
        UUID uuid = new UUID(din.readLong(), din.readLong());
        @Nullable ItemStack[] items;
        
        byte id = din.readByte(); // id, pre v1.0: 3, v1.0: 4, v2.0: 5
        switch (id) {
            case 3, 4 -> {
                if (id == 3) {
                    // stack sizes are no longer serialized
                    DataUtils.readByteArray(din);
                }
                
                items = Arrays.stream(DataUtils.read2DByteArray(din))
                    .map(data -> data.length != 0 ? ItemStack.deserializeBytes(data) : null)
                    .toArray(ItemStack[]::new);
            }
            
            case 5 -> {
                int dataVersion = din.readInt();
                int size = din.readInt();
                var itemsMask = BitSet.valueOf(din.readNBytes((size + 7) / 8)); // ceil(size / 8)
                var itemsIn = new BufferedInputStream(new GZIPInputStream(din));
                
                items = new ItemStack[size];
                for (int i = 0; i < size; i++) {
                    if (!itemsMask.get(i))
                        continue;
                    
                    items[i] = DataUtils.deserializeItemStack(dataVersion, itemsIn);
                }
            }
            
            default -> throw new UnsupportedOperationException("Unsupported VirtualInventory version: " + id);
        }
        
        return new VirtualInventory(uuid, items);
    }
    
    /**
     * Serializes this {@link VirtualInventory} to a byte array.
     * <p>
     * This method only serializes the {@link UUID} and {@link ItemStack ItemStacks}.
     *
     * @return The serialized data.
     */
    public byte[] serialize() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            serialize(out);
            return out.toByteArray();
        } catch (IOException e) {
            // ByteArrayOutputStream should not throw IOException
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Serializes this {@link VirtualInventory} to an {@link OutputStream}.
     * <p>
     * This method only serializes the {@link UUID} and {@link ItemStack ItemStacks}.
     *
     * @param out The {@link OutputStream} to write serialized data to.
     * @throws IOException If an I/O error has occurred.
     */
    public void serialize(OutputStream out) throws IOException {
        var dos = new DataOutputStream(out);
        dos.writeLong(uuid.getMostSignificantBits());
        dos.writeLong(uuid.getLeastSignificantBits());
        dos.writeByte((byte) 5); // id, pre v1.0: 3, v1.0: 4, v2.0: 5
        
        dos.writeInt(CraftMagicNumbers.INSTANCE.getDataVersion());
        dos.writeInt(items.length);
        
        var itemMask = new BitSet(items.length);
        var itemsBin = new ByteArrayOutputStream();
        var itemsOut = new BufferedOutputStream(new GZIPOutputStream(itemsBin));
        
        for (int i = 0; i < items.length; i++) {
            var itemStack = items[i];
            if (ItemUtils.isEmpty(itemStack))
                continue;
            
            itemMask.set(i, true);
            DataUtils.serializeItemStack(itemStack, itemsOut);
        }
        
        itemsOut.close();
        dos.write(Arrays.copyOf(itemMask.toByteArray(), (items.length + 7) / 8)); // ceil(size / 8)
        dos.write(itemsBin.toByteArray());
        
        dos.flush();
    }
    
    /**
     * Sets the array of max stack sizes for this {@link Inventory}.
     *
     * @param stackSizes The array defining the max stack sizes for this {@link Inventory}.
     */
    public void setMaxStackSizes(int[] stackSizes) {
        if (stackSizes.length != getSize())
            throw new IllegalArgumentException("Size of stackSizes array (" + stackSizes.length + ") does not match inventory size (" + getSize() + ")");
        
        this.maxStackSizes = stackSizes;
    }
    
    /**
     * Sets the maximum allowed stack size on a specific slot.
     *
     * @param slot         The slot
     * @param maxStackSize The max stack size
     */
    public void setMaxStackSize(int slot, int maxStackSize) {
        if (slot < 0 || slot >= getSize())
            throw new IndexOutOfBoundsException("Slot " + slot + " out of bounds for size " + getSize());
        
        maxStackSizes[slot] = maxStackSize;
    }
    
    /**
     * Gets the {@link UUID} of this {@link VirtualInventory}.
     *
     * @return The {@link UUID} of this {@link VirtualInventory}.
     */
    public UUID getUuid() {
        return uuid;
    }
    
    @Override
    public int[] getMaxStackSizes() {
        return maxStackSizes.clone();
    }
    
    @Override
    public int getMaxSlotStackSize(int slot) {
        if (slot < 0 || slot >= getSize())
            throw new IndexOutOfBoundsException("Slot " + slot + " out of bounds for size " + getSize());
        
        return maxStackSizes[slot];
    }
    
    @Override
    public @Nullable ItemStack[] getItems() {
        return ItemUtils.clone(items);
    }
    
    @Override
    public @Nullable ItemStack[] getUnsafeItems() {
        return items;
    }
    
    @Override
    public @Nullable ItemStack getItem(int slot) {
        ItemStack itemStack = items[slot];
        return itemStack != null ? itemStack.clone() : null;
    }
    
    @Override
    public @Nullable ItemStack getUnsafeItem(int slot) {
        return items[slot];
    }
    
    @Override
    protected void setCloneBackingItem(int slot, @Nullable ItemStack itemStack) {
        items[slot] = itemStack != null ? itemStack.clone() : null;
    }
    
    @Override
    protected void setDirectBackingItem(int slot, @Nullable ItemStack itemStack) {
        items[slot] = itemStack;
    }
    
    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected boolean callClickEvent(int slot, Click click, InventoryAction action, boolean cancelled) {
        cancelled = super.callClickEvent(slot, click, action, cancelled);
        if (!InvUI.getInstance().isFireBukkitInventoryEvents())
            return cancelled;
        
        // the virtual inventory is always put as the top inventory,
        // as most plugins will expect the bottom inventory to be the player's inventory
        var player = click.player();
        var bukkitEvent = new InventoryClickEvent(
            new FakeInventoryView(player, asBukkitInventory()),
            InventoryType.SlotType.CONTAINER,
            slot,
            click.clickType(),
            action,
            click.hotbarButton()
        );
        bukkitEvent.setCancelled(cancelled);
        Bukkit.getPluginManager().callEvent(bukkitEvent);
        return bukkitEvent.isCancelled();
    }
    
    @Override
    public int getUpdatePeriod(int what) {
        return -1;
    }
    
}
