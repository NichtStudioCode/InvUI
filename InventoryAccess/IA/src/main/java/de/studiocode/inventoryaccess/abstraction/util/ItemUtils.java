package de.studiocode.inventoryaccess.abstraction.util;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface ItemUtils {
    
    /**
     * Serializes an {@link ItemStack} to a byte[]
     *
     * @param itemStack  The {@link ItemStack} to serialize
     * @param compressed If the data should be compressed
     * @return The serialized data
     * @see #deserializeItemStack(byte[], boolean)
     */
    byte[] serializeItemStack(@NotNull ItemStack itemStack, boolean compressed);
    
    /**
     * Serializes an {@link ItemStack} to a byte[]
     *
     * @param itemStack    The {@link ItemStack} to serialize
     * @param outputStream The {@link OutputStream} to write the serialized data to
     * @param compressed   If the data should be compressed
     * @see #deserializeItemStack(InputStream, boolean)
     */
    void serializeItemStack(@NotNull ItemStack itemStack, @NotNull OutputStream outputStream, boolean compressed);
    
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
    ItemStack deserializeItemStack(@NotNull InputStream inputStream, boolean compressed);
    
    /**
     * Sets the display name of an {@link ItemMeta}
     *
     * @param itemMeta The {@link ItemMeta}
     * @param name     The display name as a {@link BaseComponent BaseComponent[]}
     */
    void setDisplayName(@NotNull ItemMeta itemMeta, @NotNull BaseComponent[] name);
    
    /**
     * Sets the lore of an {@link ItemMeta}
     *
     * @param itemMeta The {@link ItemMeta}
     * @param lore     The lore as a list of {@link BaseComponent BaseComponent[]}
     */
    void setLore(@NotNull ItemMeta itemMeta, @NotNull List<@NotNull BaseComponent[]> lore);
    
}
