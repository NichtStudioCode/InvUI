package xyz.xenondevs.inventoryaccess.component;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.i18n.Languages;

public interface ComponentWrapper extends Cloneable {
    
    /**
     * Serializes the component to a json string.
     *
     * @return The json representation of the component.
     */
    @NotNull String serializeToJson();
    
    /**
     * Creates a localized version of the component by replacing all translatable components with text components
     * of the specified language.
     * <p>
     * This method will return the same {@link ComponentWrapper} when {@link Languages} is disabled.
     *
     * @param lang The language to use.
     * @return A new {@link ComponentWrapper} of the localized component or the same {@link ComponentWrapper}
     * if {@link Languages} is disabled.
     */
    @NotNull ComponentWrapper localized(@NotNull String lang);
    
    /**
     * Creates a new {@link ComponentWrapper} that forces the default formatting (white color, no decorations),
     * unless configured otherwise.
     *
     * @return A new {@link ComponentWrapper} with the default formatting.
     */
    @NotNull ComponentWrapper withoutPreFormatting();
    
    /**
     * Creates a localized version of the component by replacing all translatable components with text components
     * of the {@link Player Player's} language.
     *
     * @param player The player to get the language from. Uses {@link Languages#getLanguage(Player)}.
     * @return A new {@link ComponentWrapper} of the localized component or the same {@link ComponentWrapper}
     * if {@link Languages} is disabled.
     */
    default @NotNull ComponentWrapper localized(@NotNull Player player) {
        return localized(Languages.getInstance().getLanguage(player));
    }
    
    /**
     * Clones this {@link ComponentWrapper}.
     *
     * @return The cloned {@link ComponentWrapper}.
     */
    @NotNull ComponentWrapper clone();
    
}
