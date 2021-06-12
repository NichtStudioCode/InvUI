package de.studiocode.invui.virtualinventory;

import de.studiocode.invui.InvUI;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Automatically serializes and deserializes {@link VirtualInventory}s
 * when the server reloads / restarts.
 */
public class VirtualInventoryManager {
    
    private static final File SAVE_DIR = new File("plugins/InvUI/VirtualInventory/" + InvUI.getInstance().getPlugin().getName() + "/");
    
    private static VirtualInventoryManager instance;
    
    private final Map<UUID, VirtualInventory> inventories = new HashMap<>();
    
    private VirtualInventoryManager() {
        ConfigurationSerialization.registerClass(VirtualInventory.class);
        InvUI.getInstance().addDisableHandler(this::serializeAll);
        deserializeAll();
    }
    
    public static VirtualInventoryManager getInstance() {
        return instance == null ? instance = new VirtualInventoryManager() : instance;
    }
    
    public VirtualInventory createNew(@NotNull UUID uuid, int size) {
        if (inventories.containsKey(uuid))
            throw new IllegalArgumentException("A VirtualInventory with that UUID already exists");
        
        VirtualInventory virtualInventory = new VirtualInventory(uuid, size);
        inventories.put(uuid, virtualInventory);
        
        return virtualInventory;
    }
    
    public VirtualInventory createNew(@NotNull UUID uuid, int size, ItemStack[] items, int[] stackSizes) {
        if (inventories.containsKey(uuid))
            throw new IllegalArgumentException("A Virtual Inventory with that UUID already exists");
        
        VirtualInventory virtualInventory = new VirtualInventory(uuid, size, items, stackSizes);
        inventories.put(uuid, virtualInventory);
        
        return virtualInventory;
    }
    
    public VirtualInventory getByUuid(@NotNull UUID uuid) {
        return inventories.get(uuid);
    }
    
    public VirtualInventory getOrCreate(UUID uuid, int size) {
        VirtualInventory virtualInventory = getByUuid(uuid);
        return virtualInventory == null ? createNew(uuid, size) : virtualInventory;
    }
    
    public VirtualInventory getOrCreate(UUID uuid, int size, ItemStack[] items, int[] stackSizes) {
        VirtualInventory virtualInventory = getByUuid(uuid);
        return virtualInventory == null ? createNew(uuid, size, items, stackSizes) : virtualInventory;
    }
    
    public void remove(VirtualInventory virtualInventory) {
        inventories.remove(virtualInventory.getUuid(), virtualInventory);
        getSaveFile(virtualInventory).delete();
    }
    
    private void deserializeAll() {
        if (SAVE_DIR.exists()) {
            Arrays.stream(SAVE_DIR.listFiles())
                .filter(file -> file.getName().endsWith(".vi"))
                .forEach(file -> {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    VirtualInventory virtualInventory = config.getSerializable("vi", VirtualInventory.class);
                    
                    inventories.put(virtualInventory.getUuid(), virtualInventory);
                });
        }
    }
    
    private void serializeAll() {
        inventories.values().forEach(virtualInventory -> {
            try {
                File file = getSaveFile(virtualInventory);
                YamlConfiguration config = new YamlConfiguration();
                config.set("vi", virtualInventory);
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    private File getSaveFile(VirtualInventory virtualInventory) {
        return new File(SAVE_DIR, virtualInventory.getUuid() + ".vi");
    }
    
}
