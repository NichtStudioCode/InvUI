package xyz.xenondevs.invui.window.builder;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.BaseComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWindowBuilder<W extends Window, V, S extends AbstractWindowBuilder<W, V, S>> implements WindowBuilder<W> {
    
    protected V viewer;
    protected ComponentWrapper title;
    protected boolean closeable = true;
    protected boolean retain = false;
    protected List<Runnable> closeHandlers;
    
    public S setViewer(@NotNull V viewer) {
        this.viewer = viewer;
        return getThis();
    }
    
    public S setTitle(@NotNull ComponentWrapper title) {
        this.title = title;
        return getThis();
    }
    
    public S setTitle(@NotNull BaseComponent @NotNull [] title) {
        this.title = new BaseComponentWrapper(title);
        return getThis();
    }
    
    public S setTitle(@NotNull String title) {
        this.title = new BaseComponentWrapper(TextComponent.fromLegacyText(title));
        return getThis();
    }
    
    public S setCloseable(boolean closeable) {
        this.closeable = closeable;
        return getThis();
    }
    
    public S setRetain(boolean retain) {
        this.retain = retain;
        return getThis();
    }
    
    public S setCloseHandlers(List<Runnable> closeHandlers) {
        this.closeHandlers = closeHandlers;
        return getThis();
    }
    
    public S addCloseHandler(Runnable closeHandler) {
        if (closeHandlers == null)
            closeHandlers = new ArrayList<>();
        
        closeHandlers.add(closeHandler);
        return getThis();
    }
    
    protected void applyChanges(W window) {
        if (closeHandlers != null) {
            closeHandlers.forEach(window::addCloseHandler);
        }
    }
    
    protected abstract S getThis();
    
}
