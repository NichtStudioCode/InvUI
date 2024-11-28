package xyz.xenondevs.inventoryaccess.r21;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.*;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import xyz.xenondevs.inventoryaccess.abstraction.util.ItemUtils;

import java.io.*;

class ItemUtilsImpl implements ItemUtils {
    
    @Override
    public byte[] serializeItemStack(org.bukkit.inventory.ItemStack itemStack, boolean compressed) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializeItemStack(itemStack, out, compressed);
        return out.toByteArray();
    }
    
    @Override
    public void serializeItemStack(org.bukkit.inventory.ItemStack itemStack, OutputStream outputStream, boolean compressed) {
        try {
            ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
            CompoundTag nbt = (CompoundTag) nmsStack.save(CraftRegistry.getMinecraftRegistry(), new CompoundTag());
            
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
    public org.bukkit.inventory.ItemStack deserializeItemStack(InputStream inputStream, boolean compressed) {
        try {
            CompoundTag nbt;
            if (compressed) {
                nbt = NbtIo.readCompressed(inputStream, NbtAccounter.unlimitedHeap());
            } else {
                DataInputStream dataIn = new DataInputStream(inputStream);
                nbt = NbtIo.read(dataIn);
            }
            
            Dynamic<Tag> converted = DataFixers.getDataFixer()
                .update(
                    References.ITEM_STACK,
                    new Dynamic<>(NbtOps.INSTANCE, nbt),
                    3700, CraftMagicNumbers.INSTANCE.getDataVersion()
                );
            
            ItemStack itemStack = ItemStack.parse(
                CraftRegistry.getMinecraftRegistry(),
                converted.getValue()
            ).orElse(ItemStack.EMPTY);
            return CraftItemStack.asCraftMirror(itemStack);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
}