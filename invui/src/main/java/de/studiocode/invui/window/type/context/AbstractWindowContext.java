package de.studiocode.invui.window.type.context;

import de.studiocode.inventoryaccess.component.BaseComponentWrapper;
import de.studiocode.inventoryaccess.component.ComponentWrapper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractWindowContext<V> implements WindowContext {
    
    protected boolean closeable = true;
    protected boolean retain = false;
    protected ComponentWrapper title;
    protected V viewer;
    
    public boolean isCloseable() {
        return closeable;
    }
    
    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
    }
    
    public boolean isRetain() {
        return retain;
    }
    
    public void setRetain(boolean retain) {
        this.retain = retain;
    }
    
    @Nullable
    public ComponentWrapper getTitle() {
        return title;
    }
    
    public void setTitle(@NotNull ComponentWrapper title) {
        this.title = title;
    }
    
    public void setTitle(@NotNull BaseComponent @NotNull[] title) {
        this.title = new BaseComponentWrapper(title);
    }
    
    public void setTitle(@NotNull String title) {
        this.title = new BaseComponentWrapper(TextComponent.fromLegacyText(title));
    }
    
    @Nullable
    public V getViewer() {
        return viewer;
    }
    
    public void setViewer(@NotNull V viewer) {
        this.viewer = viewer;
    }
    
}
