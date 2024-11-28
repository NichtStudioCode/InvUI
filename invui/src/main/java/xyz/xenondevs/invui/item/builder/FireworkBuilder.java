package xyz.xenondevs.invui.item.builder;

import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class FireworkBuilder extends AbstractItemBuilder<FireworkBuilder> {
    
    private int power = -1;
    private List<FireworkEffect> effects = new ArrayList<>();
    
    public FireworkBuilder() {
        super(Material.FIREWORK_ROCKET);
    }
    
    public FireworkBuilder(int amount) {
        super(Material.FIREWORK_ROCKET, amount);
    }
    
    public FireworkBuilder(ItemStack base) {
        super(base);
    }
    
    public FireworkBuilder setPower(@Range(from = 0, to = 127) int power) {
        this.power = power;
        return this;
    }
    
    public FireworkBuilder addFireworkEffect(FireworkEffect effect) {
        effects.add(effect);
        return this;
    }
    
    public FireworkBuilder addFireworkEffect(FireworkEffect.Builder builder) {
        effects.add(builder.build());
        return this;
    }
    
    public FireworkBuilder setFireworkEffects(List<FireworkEffect> effects) {
        this.effects = effects;
        return this;
    }
    
    
    public FireworkBuilder clearFireworkEffects() {
        effects.clear();
        return this;
    }
    
    
    @Override
    public ItemStack get(@Nullable String lang) {
        ItemStack item = super.get(lang);
        FireworkMeta meta = (FireworkMeta) item.getItemMeta();
        
        if (power != -1) meta.setPower(power);
        meta.clearEffects();
        meta.addEffects(effects);
        
        item.setItemMeta(meta);
        return item;
    }
    
    @Override
    public FireworkBuilder clone() {
        FireworkBuilder builder = super.clone();
        builder.effects = new ArrayList<>(effects);
        return builder;
    }
    
}
