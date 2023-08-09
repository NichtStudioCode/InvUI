package xyz.xenondevs.invui.item.builder;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class BannerBuilder extends AbstractItemBuilder<BannerBuilder> {
    
    private List<Pattern> patterns = new ArrayList<>();
    
    public BannerBuilder(@NotNull Material material) {
        super(material);
    }
    
    public BannerBuilder(@NotNull Material material, int amount) {
        super(material, amount);
    }
    
    public BannerBuilder(@NotNull ItemStack base) {
        super(base);
    }
    
    @Contract("_ -> this")
    public @NotNull BannerBuilder addPattern(@NotNull Pattern pattern) {
        patterns.add(pattern);
        return this;
    }
    
    @Contract("_, _ -> this")
    public @NotNull BannerBuilder addPattern(@NotNull DyeColor color, @NotNull PatternType type) {
        patterns.add(new Pattern(color, type));
        return this;
    }
    
    @Contract("_ -> this")
    public @NotNull BannerBuilder setPatterns(@NotNull List<@NotNull Pattern> patterns) {
        this.patterns = patterns;
        return this;
    }
    
    @Contract("-> this")
    public @NotNull BannerBuilder clearPatterns() {
        patterns.clear();
        return this;
    }
    
    @Contract(value = "_ -> new", pure = true)
    @Override
    public @NotNull ItemStack get(@Nullable String lang) {
        ItemStack item = super.get(lang);
        BannerMeta meta = (BannerMeta) item.getItemMeta();
        
        meta.setPatterns(patterns);
        
        item.setItemMeta(meta);
        return item;
    }
    
    @Override
    public @NotNull BannerBuilder clone() {
        BannerBuilder builder = super.clone();
        builder.patterns = new ArrayList<>(patterns);
        return builder;
    }
    
}
