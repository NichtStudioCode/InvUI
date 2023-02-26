package xyz.xenondevs.invui.item.builder;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class PotionBuilder extends AbstractItemBuilder<PotionBuilder> {
    
    private List<PotionEffect> effects = new ArrayList<>();
    private Color color;
    private PotionData basePotionData;
    
    public PotionBuilder(@NotNull PotionType type) {
        super(type.getMaterial());
    }
    
    public PotionBuilder(@NotNull ItemStack base) {
        super(base);
    }
    
    @Contract("_ -> this")
    public @NotNull PotionBuilder setColor(@NotNull Color color) {
        this.color = color;
        return this;
    }
    
    @Contract("_ -> this")
    public @NotNull PotionBuilder setColor(@NotNull java.awt.Color color) {
        this.color = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
        return this;
    }
    
    @Contract("_ -> this")
    public @NotNull PotionBuilder setBasePotionData(@NotNull PotionData basePotionData) {
        this.basePotionData = basePotionData;
        return this;
    }
    
    @Contract("_ -> this")
    public @NotNull PotionBuilder addEffect(@NotNull PotionEffect effect) {
        effects.add(effect);
        return this;
    }
    
    @Contract(value = "-> new", pure = true)
    @Override
    public @NotNull ItemStack get() {
        ItemStack item = super.get();
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        
        meta.clearCustomEffects();
        if (color != null) meta.setColor(color);
        if (basePotionData != null) meta.setBasePotionData(basePotionData);
        effects.forEach(effect -> meta.addCustomEffect(effect, true));
        
        item.setItemMeta(meta);
        return item;
    }
    
    @Override
    public @NotNull PotionBuilder clone() {
        PotionBuilder builder = super.clone();
        builder.effects = new ArrayList<>(effects);
        return builder;
    }
    
    public enum PotionType {
        
        NORMAL(Material.POTION),
        SPLASH(Material.SPLASH_POTION),
        LINGERING(Material.LINGERING_POTION);
        
        private final @NotNull Material material;
        
        PotionType(@NotNull Material material) {
            this.material = material;
        }
        
        public @NotNull Material getMaterial() {
            return material;
        }
        
    }
    
}
