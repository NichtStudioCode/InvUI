package de.studiocode.invui.item.builder;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public final class PotionBuilder extends BaseItemBuilder<PotionBuilder> {
    
    private List<PotionEffect> effects = new ArrayList<>();
    private Color color;
    private PotionData basePotionData;
    
    public PotionBuilder(PotionType type) {
        super(type.getMaterial());
    }
    
    public PotionBuilder(ItemStack base) {
        super(base);
    }
    
    public PotionBuilder setColor(Color color) {
        this.color = color;
        return this;
    }
    
    public PotionBuilder setColor(java.awt.Color color) {
        this.color = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
        return this;
    }
    
    public PotionBuilder setBasePotionData(PotionData basePotionData) {
        this.basePotionData = basePotionData;
        return this;
    }
    
    public PotionBuilder addEffect(PotionEffect effect) {
        effects.add(effect);
        return this;
    }
    
    @Override
    public ItemStack get() {
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
    public PotionBuilder clone() {
        PotionBuilder builder = super.clone();
        builder.effects = new ArrayList<>(effects);
        return builder;
    }
    
    @Override
    protected PotionBuilder getThis() {
        return this;
    }
    
    public enum PotionType {
        
        NORMAL(Material.POTION),
        SPLASH(Material.SPLASH_POTION),
        LINGERING(Material.LINGERING_POTION);
        
        private final Material material;
        
        PotionType(Material material) {
            this.material = material;
        }
        
        public Material getMaterial() {
            return material;
        }
        
    }
    
}
