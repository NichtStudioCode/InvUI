package xyz.xenondevs.inventoryaccess.r24;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.*;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import org.bukkit.craftbukkit.v1_21_R5.CraftRegistry;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R5.util.CraftMagicNumbers;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.abstraction.util.ItemUtils;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.inventoryaccess.util.ReflectionRegistry;
import xyz.xenondevs.inventoryaccess.util.ReflectionUtils;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

class ItemUtilsImpl implements ItemUtils {
    
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
            CompoundTag nbt = (CompoundTag) ItemStack.CODEC.encode(
                nmsStack, 
                CraftRegistry.getMinecraftRegistry().createSerializationContext(NbtOps.INSTANCE), 
                new CompoundTag()
            ).getOrThrow();
            nbt.putInt("DataVersion", CraftMagicNumbers.INSTANCE.getDataVersion());
            
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
                nbt = NbtIo.readCompressed(inputStream, NbtAccounter.unlimitedHeap());
            } else {
                DataInputStream dataIn = new DataInputStream(inputStream);
                nbt = NbtIo.read(dataIn);
            }
            
            int dataVersion = nbt.getInt("DataVersion").orElse(0);
            if (dataVersion == 0)
                dataVersion = 3700;
            
            Dynamic<Tag> converted = DataFixers.getDataFixer()
                .update(
                    References.ITEM_STACK,
                    new Dynamic<>(NbtOps.INSTANCE, nbt),
                    dataVersion, CraftMagicNumbers.INSTANCE.getDataVersion()
                );
            
            ItemStack itemStack = ItemStack.CODEC.parse(
                CraftRegistry.getMinecraftRegistry().createSerializationContext(NbtOps.INSTANCE),
                converted.getValue()
            ).resultOrPartial().orElse(ItemStack.EMPTY);
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
            InventoryUtilsImpl.createNMSComponent(name)
        );
    }
    
    @Override
    public void setLore(@NotNull ItemMeta itemMeta, @NotNull List<@NotNull ComponentWrapper> lore) {
        ReflectionUtils.setFieldValue(
            ReflectionRegistry.CB_CRAFT_META_ITEM_LORE_FIELD,
            itemMeta,
            lore.stream().map(InventoryUtilsImpl::createNMSComponent).collect(Collectors.toList())
        );
    }
    
    @Override
    public void setSkullProfile(@NotNull ItemMeta itemMeta, @NotNull GameProfile profile) {
        assert ReflectionRegistry.CB_CRAFT_META_SKULL_SET_RESOLVABLE_PROFILE_METHOD != null;
        ReflectionUtils.invokeMethod(ReflectionRegistry.CB_CRAFT_META_SKULL_SET_RESOLVABLE_PROFILE_METHOD, itemMeta, new ResolvableProfile(profile));
    }
    
}