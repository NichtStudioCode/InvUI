package de.studiocode.inventoryaccess.v1_17_R2.util;

import de.studiocode.inventoryaccess.abstraction.util.ItemUtils;
import de.studiocode.inventoryaccess.util.ReflectionUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;

import java.io.*;
import java.lang.reflect.Field;

public class ItemUtilsImpl implements ItemUtils {
    
    private static final Field CRAFT_ITEM_STACK_HANDLE_FIELD = ReflectionUtils.getField(CraftItemStack.class, true, "handle");
    
    @Override
    public byte[] serializeItemStack(org.bukkit.inventory.ItemStack itemStack, boolean compressed) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializeItemStack(itemStack, out, compressed);
        return out.toByteArray();
    }
    
    @Override
    public void serializeItemStack(org.bukkit.inventory.ItemStack itemStack, OutputStream outputStream, boolean compressed) {
        try {
            ItemStack nmsStack;
            
            if (itemStack instanceof CraftItemStack)
                nmsStack = (ItemStack) CRAFT_ITEM_STACK_HANDLE_FIELD.get(itemStack);
            else nmsStack = CraftItemStack.asNMSCopy(itemStack);
            
            CompoundTag nbt = nmsStack.save(new CompoundTag());
            
            if (compressed) {
                NbtIo.writeCompressed(nbt, outputStream);
            } else {
                DataOutputStream dataOut = new DataOutputStream(outputStream);
                NbtIo.write(nbt, dataOut);
            }
            
            outputStream.flush();
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public org.bukkit.inventory.ItemStack deserializeItemStack(byte[] data, boolean compressed) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        return deserializeItemStack(in, compressed);
    }
    
    @Override
    public org.bukkit.inventory.ItemStack deserializeItemStack(InputStream inputStream, boolean compressed) {
        try {
            CompoundTag nbt;
            if (compressed) {
                nbt = NbtIo.readCompressed(inputStream);
            } else {
                DataInputStream dataIn = new DataInputStream(inputStream);
                nbt = NbtIo.read(dataIn);
            }
            
            ItemStack itemStack = ItemStack.of(nbt);
            
            return CraftItemStack.asCraftMirror(itemStack);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
}