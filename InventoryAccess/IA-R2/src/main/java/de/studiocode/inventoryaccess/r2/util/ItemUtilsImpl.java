package de.studiocode.inventoryaccess.r2.util;

import de.studiocode.inventoryaccess.abstraction.util.ItemUtils;
import de.studiocode.inventoryaccess.util.ReflectionRegistry;
import de.studiocode.inventoryaccess.util.ReflectionUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class ItemUtilsImpl implements ItemUtils {
    
    private static final Field CRAFT_ITEM_STACK_HANDLE_FIELD = ReflectionUtils.getField(CraftItemStack.class, true, "handle");
    
    @Override
    public byte[] serializeItemStack(org.bukkit.inventory.@NotNull ItemStack itemStack, boolean compressed) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializeItemStack(itemStack, out, compressed);
        return out.toByteArray();
    }
    
    @Override
    public void serializeItemStack(org.bukkit.inventory.@NotNull ItemStack itemStack, @NotNull OutputStream out, boolean compressed) {
        try {
            ItemStack nmsStack;
            
            if (itemStack instanceof CraftItemStack)
                nmsStack = (ItemStack) CRAFT_ITEM_STACK_HANDLE_FIELD.get(itemStack);
            else nmsStack = CraftItemStack.asNMSCopy(itemStack);
            
            NBTTagCompound nbt = nmsStack.save(new NBTTagCompound());
            
            if (compressed) {
                NBTCompressedStreamTools.a(nbt, out);
            } else {
                DataOutputStream dataOut = new DataOutputStream(out);
                NBTCompressedStreamTools.a(nbt, (DataOutput) dataOut);
            }
            
            out.flush();
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
    public org.bukkit.inventory.ItemStack deserializeItemStack(@NotNull InputStream in, boolean compressed) {
        try {
            NBTTagCompound nbt;
            if (compressed) {
                nbt = NBTCompressedStreamTools.a(in);
            } else {
                DataInputStream dataIn = new DataInputStream(in);
                nbt = NBTCompressedStreamTools.a(dataIn);
            }
            
            ItemStack itemStack = ItemStack.a(nbt);
            
            return CraftItemStack.asCraftMirror(itemStack);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public void setDisplayName(@NotNull ItemMeta itemMeta, BaseComponent[] name) {
        ReflectionUtils.setFieldValue(
            ReflectionRegistry.CB_CRAFT_META_ITEM_DISPLAY_NAME_FIELD,
            itemMeta,
            InventoryUtilsImpl.createNMSComponent(name)
        );
    }
    
    @Override
    public void setLore(@NotNull ItemMeta itemMeta, @NotNull List<@NotNull BaseComponent[]> lore) {
        ReflectionUtils.setFieldValue(
            ReflectionRegistry.CB_CRAFT_META_ITEM_LORE_FIELD,
            itemMeta,
            lore.stream().map(InventoryUtilsImpl::createNMSComponent).collect(Collectors.toList())
        );
    }
    
}
