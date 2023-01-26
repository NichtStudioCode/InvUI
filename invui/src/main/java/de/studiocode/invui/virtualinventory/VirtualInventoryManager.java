package de.studiocode.invui.virtualinventory;

import de.studiocode.inventoryaccess.version.InventoryAccess;
import de.studiocode.invui.InvUI;
import de.studiocode.invui.util.DataUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

/**
 * Automatically serializes and deserializes {@link VirtualInventory}s
 * when the server reloads / restarts.
 */
public class VirtualInventoryManager {
    
    private static final File SAVE_DIR = new File("plugins/InvUI/VirtualInventory/" + InvUI.getInstance().getPlugin().getName() + "/");
    
    private static VirtualInventoryManager instance;
    
    private final Map<UUID, VirtualInventory> inventories = new HashMap<>();
    
    private VirtualInventoryManager() {
        SAVE_DIR.mkdirs();
        
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
    
    public List<VirtualInventory> getAllInventories() {
        return new ArrayList<>(inventories.values());
    }
    
    public void remove(VirtualInventory virtualInventory) {
        inventories.remove(virtualInventory.getUuid(), virtualInventory);
        getSaveFile(virtualInventory).delete();
    }
    
    private void deserializeAll() {
        if (SAVE_DIR.exists()) {
            Arrays.stream(SAVE_DIR.listFiles())
                .forEach(file -> {
                    if (file.getName().endsWith(".vi2")) {
                        try {
                            FileInputStream in = new FileInputStream(file);
                            VirtualInventory virtualInventory = deserializeInventory(in);
                            inventories.put(virtualInventory.getUuid(), virtualInventory);
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        }
    }
    
    private void serializeAll() {
        inventories.values().forEach(virtualInventory -> {
            try {
                File file = getSaveFile(virtualInventory);
                FileOutputStream out = new FileOutputStream(file);
                serializeInventory(virtualInventory, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    private File getSaveFile(VirtualInventory virtualInventory) {
        return new File(SAVE_DIR, virtualInventory.getUuid() + ".vi2");
    }
    
    public byte[] serializeInventory(VirtualInventory vi) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializeInventory(vi, out);
        return out.toByteArray();
    }
    
    public void serializeInventory(VirtualInventory vi, OutputStream out) {
        try {
            DataOutputStream dos = new DataOutputStream(out);
            UUID uuid = vi.getUuid();
            dos.writeLong(uuid.getMostSignificantBits());
            dos.writeLong(uuid.getLeastSignificantBits());
            dos.writeByte((byte) 3); // Placeholder
            DataUtils.writeByteArray(dos, DataUtils.toByteArray(vi.getStackSizes()));
            
            byte[][] items = Arrays.stream(vi.getItems()).map(itemStack -> {
                    if (itemStack != null) {
                        return InventoryAccess.getItemUtils().serializeItemStack(itemStack, true);
                    } else return new byte[0];
                }
            ).toArray(byte[][]::new);
            
            DataUtils.write2DByteArray(dos, items);
            
            dos.flush();
        } catch (
            IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public VirtualInventory deserializeInventory(byte[] bytes) {
        return deserializeInventory(new ByteArrayInputStream(bytes));
    }
    
    public VirtualInventory deserializeInventory(InputStream in) {
        try {
            DataInputStream din = new DataInputStream(in);
            UUID uuid = new UUID(din.readLong(), din.readLong());
            din.readByte(); // Placeholder
            int[] stackSizes = DataUtils.toIntArray(DataUtils.readByteArray(din));
            
            ItemStack[] items = Arrays.stream(DataUtils.read2DByteArray(din)).map(data -> {
                    if (data.length != 0) {
                        return InventoryAccess.getItemUtils().deserializeItemStack(data, true);
                    } else return null;
                }
            ).toArray(ItemStack[]::new);
            
            return new VirtualInventory(uuid, stackSizes.length, items, stackSizes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
}
