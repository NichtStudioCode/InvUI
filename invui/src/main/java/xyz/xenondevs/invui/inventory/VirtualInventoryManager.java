package xyz.xenondevs.invui.inventory;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.InvUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Automatically reads and writes {@link VirtualInventory VirtualInventories} to files when the server starts and stops.
 */
public class VirtualInventoryManager {
    
    private static final File SAVE_DIR = new File("plugins/InvUI/VirtualInventory/" + InvUI.getInstance().getPlugin().getName() + "/");
    
    private static VirtualInventoryManager instance;
    
    private final Map<UUID, VirtualInventory> inventories = new HashMap<>();
    
    private VirtualInventoryManager() {
        InvUI.getInstance().addDisableHandler(this::serializeAll);
        deserializeAll();
    }
    
    public static VirtualInventoryManager getInstance() {
        return instance == null ? instance = new VirtualInventoryManager() : instance;
    }
    
    public VirtualInventory createNew(@NotNull UUID uuid, int size) {
        if (inventories.containsKey(uuid))
            throw new IllegalArgumentException("A VirtualInventory with that UUID already exists");
        
        VirtualInventory inventory = new VirtualInventory(uuid, size);
        inventories.put(uuid, inventory);
        
        return inventory;
    }
    
    public VirtualInventory createNew(@NotNull UUID uuid, int size, ItemStack[] items, int[] stackSizes) {
        if (inventories.containsKey(uuid))
            throw new IllegalArgumentException("A Virtual Inventory with that UUID already exists");
        
        VirtualInventory inventory = new VirtualInventory(uuid, size, items, stackSizes);
        inventories.put(uuid, inventory);
        
        return inventory;
    }
    
    public VirtualInventory getByUuid(@NotNull UUID uuid) {
        return inventories.get(uuid);
    }
    
    public VirtualInventory getOrCreate(UUID uuid, int size) {
        VirtualInventory inventory = getByUuid(uuid);
        return inventory == null ? createNew(uuid, size) : inventory;
    }
    
    public VirtualInventory getOrCreate(UUID uuid, int size, ItemStack[] items, int[] stackSizes) {
        VirtualInventory inventory = getByUuid(uuid);
        return inventory == null ? createNew(uuid, size, items, stackSizes) : inventory;
    }
    
    public List<VirtualInventory> getAllInventories() {
        return new ArrayList<>(inventories.values());
    }
    
    public void remove(VirtualInventory inventory) {
        inventories.remove(inventory.getUuid(), inventory);
        getSaveFile(inventory).delete();
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
                InvUI.getInstance().getLogger().log(
                    Level.SEVERE,
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
                InvUI.getInstance().getLogger().log(
                    Level.SEVERE,
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
