package xyz.xenondevs.invui.item.builder;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class BannerBuilder extends AbstractItemBuilder<BannerBuilder> {
    
    private List<Pattern> patterns = new ArrayList<>();
    
    public BannerBuilder(Material material) {
        super(material);
    }
    
    public BannerBuilder(Material material, int amount) {
        super(material, amount);
    }
    
    public BannerBuilder(ItemStack base) {
        super(base);
    }
    
    public BannerBuilder addPattern(Pattern pattern) {
        patterns.add(pattern);
        return this;
    }
    
    
    public BannerBuilder addPattern(DyeColor color, PatternType type) {
        patterns.add(new Pattern(color, type));
        return this;
    }
    
    public BannerBuilder setPatterns(List<Pattern> patterns) {
        this.patterns = patterns;
        return this;
    }
    
    
    public BannerBuilder clearPatterns() {
        patterns.clear();
        return this;
    }
    
    
    @Override
    public ItemStack get(@Nullable String lang) {
        ItemStack item = super.get(lang);
        BannerMeta meta = (BannerMeta) item.getItemMeta();
        
        meta.setPatterns(patterns);
        
        item.setItemMeta(meta);
        return item;
    }
    
    @Override
    public BannerBuilder clone() {
        BannerBuilder builder = super.clone();
        builder.patterns = new ArrayList<>(patterns);
        return builder;
    }
    
}
