package xyz.xenondevs.inventoryaccess.component;

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
     * Clones this {@link ComponentWrapper}.
     *
     * @return The cloned {@link ComponentWrapper}.
     */
    @NotNull ComponentWrapper clone();
    
}
