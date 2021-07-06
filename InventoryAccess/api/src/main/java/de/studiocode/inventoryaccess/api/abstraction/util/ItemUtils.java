package de.studiocode.inventoryaccess.api.abstraction.util;

import org.bukkit.inventory.ItemStack;

import java.io.InputStream;
import java.io.OutputStream;

public interface ItemUtils {
    
    /**
     * Serializes an {@link ItemStack} to a byte[]
     *
     * @param itemStack  The {@link ItemStack} to serialize
     * @param compressed If the data should be compressed
     * @return The serialized data
     * @see #deserializeItemStack(byte[], boolean)
     */
    byte[] serializeItemStack(ItemStack itemStack, boolean compressed);
    
    /**
     * Serializes an {@link ItemStack} to a byte[]
     *
     * @param itemStack    The {@link ItemStack} to serialize
     * @param outputStream The {@link OutputStream} to write the serialized data to
     * @param compressed   If the data should be compressed
     * @see #deserializeItemStack(InputStream, boolean)
     */
    void serializeItemStack(ItemStack itemStack, OutputStream outputStream, boolean compressed);
    
    /**
     * Deserializes an {@link ItemStack} from a byte[]
     *
     * @param data       The data to deserialize
     * @param compressed If the data is compressed
     * @return The {@link ItemStack}
     * @see #serializeItemStack(ItemStack, boolean)
     */
    ItemStack deserializeItemStack(byte[] data, boolean compressed);
    
    /**
     * Deserializes an {@link ItemStack} from a byte[]
     *
     * @param inputStream The {@link InputStream} to read the serialized data from
     * @param compressed  If the data is compressed
     * @return The {@link ItemStack}
     * @see #serializeItemStack(ItemStack, OutputStream, boolean)
     */
    ItemStack deserializeItemStack(InputStream inputStream, boolean compressed);
    
}
