package xyz.xenondevs.invui.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.internal.util.ArrayUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Automatically reads and writes {@link VirtualInventory VirtualInventories} to files when the server starts and stops.
 */
public final class VirtualInventoryManager {
    
    private static final File SAVE_DIR = new File("plugins/InvUI/VirtualInventory/" + InvUI.getInstance().getPlugin().getName() + "/");
    private static final VirtualInventoryManager INSTANCE = new VirtualInventoryManager();
    
    private final Map<UUID, VirtualInventory> inventories = new HashMap<>();
    
    private VirtualInventoryManager() {
        InvUI.getInstance().addDisableHandler(this::serializeAll);
        deserializeAll();
    }
    
    /**
     * Gets the singleton instance of the {@link VirtualInventoryManager}.
     *
     * @return The singleton instance of the {@link VirtualInventoryManager}.
     */
    public static VirtualInventoryManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Creates a new {@link VirtualInventory} with the given UUID and size.
     *
     * @param uuid The UUID of the {@link VirtualInventory}.
     * @param size The size of the {@link VirtualInventory}.
     * @return The created {@link VirtualInventory}.
     */
    public VirtualInventory createNew(UUID uuid, int size) {
        if (inventories.containsKey(uuid))
            throw new IllegalArgumentException("A VirtualInventory with that UUID already exists");
        
        VirtualInventory inventory = new VirtualInventory(uuid, size);
        inventories.put(uuid, inventory);
        
        return inventory;
    }
    
    /**
     * Creates a new {@link VirtualInventory} with the given UUID, size, items and stack sizes.
     *
     * @param uuid          The UUID of the {@link VirtualInventory}.
     * @param size          The size of the {@link VirtualInventory}.
     * @param items         The items of the {@link VirtualInventory}.
     *                      Can be null for an empty inventory.
     * @param maxStackSizes The max stack sizes of the {@link VirtualInventory}.
     *                      Can be null for the default stack size of 64.
     * @return The created {@link VirtualInventory}.
     * @throws IllegalArgumentException If a {@link VirtualInventory} with the given UUID already exists.
     */
    public VirtualInventory createNew(UUID uuid, int size, @Nullable ItemStack @Nullable [] items, int @Nullable [] maxStackSizes) {
        if (inventories.containsKey(uuid))
            throw new IllegalArgumentException("A Virtual Inventory with that UUID already exists");
        
        VirtualInventory inventory = new VirtualInventory(uuid, size, items, maxStackSizes);
        inventories.put(uuid, inventory);
        
        return inventory;
    }
    
    /**
     * Gets the {@link VirtualInventory} with the given UUID.
     *
     * @param uuid The UUID of the {@link VirtualInventory}.
     * @return The {@link VirtualInventory} with the given UUID or null if no such inventory exists.
     */
    public @Nullable VirtualInventory getByUuid(UUID uuid) {
        return inventories.get(uuid);
    }
    
    /**
     * Gets the {@link VirtualInventory} with the given UUID or creates a new one with the given size if no such inventory exists.
     *
     * @param uuid The UUID of the {@link VirtualInventory}.
     * @param size The size of the {@link VirtualInventory} to create if no such inventory exists.
     * @return The {@link VirtualInventory} with the given UUID or a new one with the given size if no such inventory exists.
     */
    public VirtualInventory getOrCreate(UUID uuid, int size) {
        VirtualInventory inventory = getByUuid(uuid);
        return inventory == null ? createNew(uuid, size) : inventory;
    }
    
    /**
     * Gets the {@link VirtualInventory} with the given UUID or creates a new one with the given size and items
     * if no such inventory exists. Since the max stack sizes are not serialized, they are always applied.
     *
     * @param uuid          The UUID of the {@link VirtualInventory}.
     * @param size          The size of the {@link VirtualInventory} to create if no such inventory exists.
     * @param items         The items of the {@link VirtualInventory} to create if no such inventory exists.
     *                      Can be null for an empty inventory.
     * @param maxStackSizes The max stack sizes of the {@link VirtualInventory}.
     *                      Can be null for the default stack size of 64.
     * @return The {@link VirtualInventory} with the given UUID or a new one with the given size, items and stack sizes if no such inventory exists.
     */
    public VirtualInventory getOrCreate(UUID uuid, int size, @Nullable ItemStack @Nullable [] items, int @Nullable [] maxStackSizes) {
        VirtualInventory inventory = getByUuid(uuid);
        if (inventory != null) {
            if (maxStackSizes != null) {
                inventory.setMaxStackSizes(ArrayUtils.copyOf(maxStackSizes, inventory.size, 64));
            }
            return inventory;
        } else {
            return createNew(uuid, size, items, maxStackSizes);
        }
    }
    
    /**
     * Gets all {@link VirtualInventory VirtualInventories}.
     *
     * @return A list of all {@link VirtualInventory VirtualInventories}.
     */
    public List<VirtualInventory> getAllInventories() {
        return new ArrayList<>(inventories.values());
    }
    
    /**
     * Removes the given {@link VirtualInventory} and deletes the file associated with it.
     *
     * @param inventory The {@link VirtualInventory} to remove.
     */
    public void remove(VirtualInventory inventory) {
        inventories.remove(inventory.getUuid(), inventory);
        Bukkit.getAsyncScheduler().runNow(
            InvUI.getInstance().getPlugin(),
            x -> getSaveFile(inventory).delete()
        );
    }
    
    private void deserializeAll() {
        if (!SAVE_DIR.exists())
            return;
        
        for (File file : SAVE_DIR.listFiles()) {
            if (!file.getName().endsWith(".vi2"))
                return;
            
            try (FileInputStream in = new FileInputStream(file)) {
                VirtualInventory inventory = VirtualInventory.deserialize(in);
                inventories.put(inventory.getUuid(), inventory);
            } catch (IOException e) {
                InvUI.getInstance().handleException(
                    "Failed to deserialize a VirtualInventory from file " + file.getPath(),
                    e
                );
            }
        }
    }
    
    private void serializeAll() {
        if (inventories.isEmpty())
            return;
        
        SAVE_DIR.mkdirs();
        
        for (VirtualInventory inventory : inventories.values()) {
            File file = getSaveFile(inventory);
            try (FileOutputStream out = new FileOutputStream(file)) {
                inventory.serialize(out);
            } catch (IOException e) {
                InvUI.getInstance().handleException(
                    "Failed to serialize a VirtualInventory to file " + file.getPath(),
                    e
                );
            }
        }
    }
    
    private File getSaveFile(VirtualInventory inventory) {
        return new File(SAVE_DIR, inventory.getUuid() + ".vi2");
    }
    
}
