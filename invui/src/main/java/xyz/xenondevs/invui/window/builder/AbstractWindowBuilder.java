package xyz.xenondevs.invui.window.builder;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.BaseComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractWindowBuilder<W extends Window, V, S extends AbstractWindowBuilder<W, V, S>> implements WindowBuilder<W> {
    
    protected V viewer;
    protected ComponentWrapper title;
    protected boolean closeable = true;
    protected boolean retain = false;
    protected List<Runnable> closeHandlers;
    protected List<Consumer<Window>> modifiers;
    
    @Contract("_ -> this")
    public S setViewer(@NotNull V viewer) {
        this.viewer = viewer;
        return getThis();
    }
    
    @Contract("_ -> this")
    public S setTitle(@NotNull ComponentWrapper title) {
        this.title = title;
        return getThis();
    }
    
    @Contract("_ -> this")
    public S setTitle(@NotNull BaseComponent @NotNull [] title) {
        this.title = new BaseComponentWrapper(title);
        return getThis();
    }
    
    @Contract("_ -> this")
    public S setTitle(@NotNull String title) {
        this.title = new BaseComponentWrapper(TextComponent.fromLegacyText(title));
        return getThis();
    }
    
    @Contract("_ -> this")
    public S setCloseable(boolean closeable) {
        this.closeable = closeable;
        return getThis();
    }
    
    @Contract("_ -> this")
    public S setRetain(boolean retain) {
        this.retain = retain;
        return getThis();
    }
    
    @Contract("_ -> this")
    public S setCloseHandlers(List<Runnable> closeHandlers) {
        this.closeHandlers = closeHandlers;
        return getThis();
    }
    
    @Contract("_ -> this")
    public S addCloseHandler(Runnable closeHandler) {
        if (closeHandlers == null)
            closeHandlers = new ArrayList<>();
        
        closeHandlers.add(closeHandler);
        return getThis();
    }
    
    @Contract("_ -> this")
    public S setModifiers(List<Consumer<Window>> modifiers) {
        this.modifiers = modifiers;
        return getThis();
    }
    
    @Contract("_ -> this")
    public S addModifier(Consumer<Window> modifier) {
        if (modifiers == null)
            modifiers = new ArrayList<>();
        
        modifiers.add(modifier);
        return getThis();
    }
    
    protected void applyModifiers(W window) {
        if (closeHandlers != null)
            window.setCloseHandlers(closeHandlers);
        
        if (modifiers != null)
            modifiers.forEach(modifier -> modifier.accept(window));
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public @NotNull AbstractWindowBuilder<W, V, S> clone() {
        try {
            var clone = (AbstractWindowBuilder<W, V, S>) super.clone();
            if (title != null)
                clone.title = title.clone();
            if (closeHandlers != null)
                clone.closeHandlers = new ArrayList<>(closeHandlers);
            if (modifiers != null)
                clone.modifiers = new ArrayList<>(modifiers);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
    @Contract(value = "-> this", pure = true)
    protected abstract S getThis();
    
}
