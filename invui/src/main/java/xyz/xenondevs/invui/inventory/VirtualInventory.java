package xyz.xenondevs.invui.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.internal.util.DataUtils;
import xyz.xenondevs.invui.util.ItemUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.logging.Level;

/**
 * A serializable {@link Inventory} implementation that is identified by a {@link UUID} and backed by a simple {@link ItemStack} array.
 *
 * @see VirtualInventoryManager
 */
@SuppressWarnings("SynchronizeOnNonFinalField")
public final class VirtualInventory extends Inventory {
    
    private final UUID uuid;
    private @Nullable ItemStack[] items;
    private int[] maxStackSizes;
    private @Nullable List<BiConsumer<Integer, Integer>> resizeHandlers;
    
    /**
     * Constructs a new {@link VirtualInventory}
     *
     * @param uuid          The {@link UUID} of this {@link VirtualInventory}. Can be null, only used for serialization.
     * @param size          The amount of slots this {@link VirtualInventory} has.
     * @param items         A predefined array of content. Can be null.
     * @param maxStackSizes An array of maximum allowed stack sizes for each slot in the {@link VirtualInventory}. Can be null for 64.
     * @throws IllegalArgumentException If the given size does not match the length of the items array or the length of the stackSizes array.
     * @throws IllegalArgumentException If the items array contains air {@link ItemStack ItemStacks}.
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
        this(inventory.uuid, inventory.size, ItemUtils.clone(inventory.items), inventory.maxStackSizes.clone());
        setGuiPriority(inventory.getGuiPriority());
        setPreUpdateHandlers(inventory.getPreUpdateHandlers());
        setPostUpdateHandlers(inventory.getPostUpdateHandlers());
        setResizeHandlers(inventory.getResizeHandlers());
    }
    
    /**
     * Deserializes a {@link VirtualInventory} from a byte array.
     *
     * @param bytes The byte array to deserialize from.
     * @return The deserialized {@link VirtualInventory}.
     */
    public static VirtualInventory deserialize(byte[] bytes) {
        return deserialize(new ByteArrayInputStream(bytes));
    }
    
    /**
     * Deserializes a {@link VirtualInventory} from an {@link InputStream}.
     *
     * @param in The {@link InputStream} to deserialize from.
     * @return The deserialized {@link VirtualInventory}.
     */
    public static VirtualInventory deserialize(InputStream in) {
        try {
            DataInputStream din = new DataInputStream(in);
            UUID uuid = new UUID(din.readLong(), din.readLong());
            
            byte id = din.readByte(); // id, pre v1.0: 3, v1.0: 4
            if (id == 3) {
                // stack sizes are no longer serialized
                DataUtils.readByteArray(din);
            }
            
            @Nullable ItemStack[] items = Arrays.stream(DataUtils.read2DByteArray(din))
                .map(data -> data.length != 0 ? ItemStack.deserializeBytes(data) : null)
                .toArray(ItemStack[]::new);
            
            return new VirtualInventory(uuid, items);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize VirtualInventory", e);
        }
    }
    
    /**
     * Serializes this {@link VirtualInventory} to a byte array.
     * <p>
     * This method only serializes the {@link UUID} and {@link ItemStack ItemStacks}.
     *
     * @return The serialized data.
     */
    public byte[] serialize() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serialize(out);
        return out.toByteArray();
    }
    
    /**
     * Serializes this {@link VirtualInventory} to an {@link OutputStream}.
     * <p>
     * This method only serializes the {@link UUID} and {@link ItemStack ItemStacks}.
     *
     * @param out The {@link OutputStream} to write serialized data to.
     */
    public void serialize(OutputStream out) {
        try {
            DataOutputStream dos = new DataOutputStream(out);
            dos.writeLong(uuid.getMostSignificantBits());
            dos.writeLong(uuid.getLeastSignificantBits());
            dos.writeByte((byte) 4); // id, pre v1.0: 3, v1.0: 4
            
            byte[][] items = Arrays.stream(this.items)
                .map(itemStack -> itemStack != null ? itemStack.serializeAsBytes() : new byte[0])
                .toArray(byte[][]::new);
            
            DataUtils.write2DByteArray(dos, items);
            
            dos.flush();
        } catch (IOException e) {
            InvUI.getInstance().getLogger().log(Level.SEVERE, "Failed to serialize VirtualInventory", e);
        }
    }
    
    /**
     * Sets the handlers that are called every time this {@link VirtualInventory} is resized.
     *
     * @param resizeHandlers The handlers to set.
     */
    public void setResizeHandlers(@Nullable List<BiConsumer<Integer, Integer>> resizeHandlers) {
        this.resizeHandlers = resizeHandlers;
    }
    
    /**
     * Gets the handlers that are called every time this {@link VirtualInventory} is resized.
     *
     * @return The handlers.
     */
    public @Nullable List<BiConsumer<Integer, Integer>> getResizeHandlers() {
        return resizeHandlers;
    }
    
    /**
     * Adds a handler that is called every time this {@link VirtualInventory} is resized.
     *
     * @param resizeHandler The handler to add.
     */
    public void addResizeHandler(BiConsumer<Integer, Integer> resizeHandler) {
        if (resizeHandlers == null)
            resizeHandlers = new ArrayList<>();
        
        resizeHandlers.add(resizeHandler);
    }
    
    /**
     * Removes a handler that is called every time this {@link VirtualInventory} is resized.
     *
     * @param resizeHandler The handler to remove.
     */
    public void removeResizeHandler(BiConsumer<Integer, Integer> resizeHandler) {
        if (resizeHandlers != null)
            resizeHandlers.remove(resizeHandler);
    }
    
    /**
     * Changes the size of the {@link VirtualInventory}.
     * <p>
     * {@link ItemStack ItemStacks} in slots which are no longer valid will be removed from the {@link VirtualInventory}.
     * This method does not call an event.
     *
     * @param size The new size of the {@link VirtualInventory}
     */
    public void resize(int size) {
        if (this.size == size)
            return;
        
        int previousSize = this.size;
        
        this.size = size;
        items = Arrays.copyOf(items, size);
        maxStackSizes = Arrays.copyOf(maxStackSizes, size);
        synchronized(viewers) { 
            viewers = Arrays.copyOf(viewers, size);
        }
        
        // fill stackSizes with the last stack size if the array was extended
        if (size > previousSize) {
            int stackSize = previousSize != 0 ? maxStackSizes[previousSize - 1] : 64;
            Arrays.fill(maxStackSizes, previousSize, maxStackSizes.length, stackSize);
        }
        
        // call resize handlers if present
        if (resizeHandlers != null) {
            for (BiConsumer<Integer, Integer> resizeHandler : resizeHandlers) {
                resizeHandler.accept(previousSize, size);
            }
        }
    }
    
    /**
     * Sets the array of max stack sizes for this {@link Inventory}.
     *
     * @param stackSizes The array defining the max stack sizes for this {@link Inventory}.
     */
    public void setMaxStackSizes(int[] stackSizes) {
        this.maxStackSizes = stackSizes;
    }
    
    /**
     * Sets the maximum allowed stack size on a specific slot.
     *
     * @param slot         The slot
     * @param maxStackSize The max stack size
     */
    public void setMaxStackSize(int slot, int maxStackSize) {
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
    
}
