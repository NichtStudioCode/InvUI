package xyz.xenondevs.inventoryaccess.r16;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.abstraction.util.ItemUtils;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.inventoryaccess.util.ReflectionRegistry;
import xyz.xenondevs.inventoryaccess.util.ReflectionUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

class ItemUtilsImpl implements ItemUtils {
    
    private static final Method CRAFT_META_SKULL_SET_PROFILE_METHOD = ReflectionUtils.getMethod(
        ReflectionRegistry.CB_CRAFT_META_SKULL_CLASS, true, "setProfile", GameProfile.class
    );    
    
    @Override
    public byte[] serializeItemStack(org.bukkit.inventory.@NotNull ItemStack itemStack, boolean compressed) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializeItemStack(itemStack, out, compressed);
        return out.toByteArray();
    }
    
    @Override
    public void serializeItemStack(org.bukkit.inventory.@NotNull ItemStack itemStack, @NotNull OutputStream outputStream, boolean compressed) {
        try {
            ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
            CompoundTag nbt = nmsStack.save(new CompoundTag());
            
            if (compressed) {
                NbtIo.writeCompressed(nbt, outputStream);
            } else {
                DataOutputStream dataOut = new DataOutputStream(outputStream);
                NbtIo.write(nbt, dataOut);
            }
            
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public org.bukkit.inventory.ItemStack deserializeItemStack(byte[] data, boolean compressed) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        return deserializeItemStack(in, compressed);
    }
    
    @Override
    public org.bukkit.inventory.ItemStack deserializeItemStack(@NotNull InputStream inputStream, boolean compressed) {
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
    
    @Override
    public void setDisplayName(@NotNull ItemMeta itemMeta, @NotNull ComponentWrapper name) {
        ReflectionUtils.setFieldValue(
            ReflectionRegistry.CB_CRAFT_META_ITEM_DISPLAY_NAME_FIELD,
            itemMeta,
            name.serializeToJson()
        );
    }
    
    @Override
    public void setLore(@NotNull ItemMeta itemMeta, @NotNull List<@NotNull ComponentWrapper> lore) {
        ReflectionUtils.setFieldValue(
            ReflectionRegistry.CB_CRAFT_META_ITEM_LORE_FIELD,
            itemMeta,
            lore.stream().map(ComponentWrapper::serializeToJson).collect(Collectors.toList())
        );
    }
    
    @Override
    public void setSkullGameProfile(@NotNull ItemMeta itemMeta, @NotNull GameProfile gameProfile) {
        ReflectionUtils.invokeMethod(CRAFT_META_SKULL_SET_PROFILE_METHOD, itemMeta, gameProfile);
    }
    
}