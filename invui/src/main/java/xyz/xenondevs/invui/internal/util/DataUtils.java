package xyz.xenondevs.invui.internal.util;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;

import java.io.*;

public class DataUtils {
    
    public static byte[] readByteArray(DataInputStream din) throws IOException {
        int size = din.readInt();
        byte[] array = new byte[size];
        din.readFully(array);
        return array;
    }
    
    public static byte[][] read2DByteArray(DataInputStream din) throws IOException {
        int size2d = din.readInt();
        byte[][] array2d = new byte[size2d][];
        for (int i = 0; i < size2d; i++) {
            array2d[i] = readByteArray(din);
        }
        return array2d;
    }
    
    /**
     * Serializes the given {@link ItemStack} and writes it to the given {@link OutputStream}.
     *
     * @param itemStack The {@link ItemStack} to serialize
     * @param out       The {@link OutputStream} to write the serialized data to
     */
    public static void serializeItemStack(ItemStack itemStack, OutputStream out) {
        try {
            var tag = CraftItemStack.unwrap(itemStack).save(MinecraftServer.getServer().registryAccess());
            NbtIo.write((CompoundTag) tag, new DataOutputStream(out));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Deserializes an {@link ItemStack} from the given {@link InputStream} and applies data fixes if necessary.
     *
     * @param dataVersion The data version of the serialized {@link ItemStack}
     * @param in          The {@link InputStream} to read the serialized data from
     * @return The deserialized {@link ItemStack}
     */
    public static ItemStack deserializeItemStack(int dataVersion, InputStream in) {
        try {
            Tag tag = NbtIo.read(new DataInputStream(in));
            tag = DataFixers.getDataFixer().update(
                References.ITEM_STACK,
                new Dynamic<>(NbtOps.INSTANCE, tag),
                dataVersion, CraftMagicNumbers.INSTANCE.getDataVersion()
            ).getValue();
            
            return net.minecraft.world.item.ItemStack.parse(MinecraftServer.getServer().registryAccess(), tag)
                .orElseThrow()
                .asBukkitMirror();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
