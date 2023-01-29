package xyz.xenondevs.invui.gui.builder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.ScrollGui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class AbstractScrollGuiBuilder<C, S extends AbstractScrollGuiBuilder<C, S>> extends AbstractGuiBuilder<ScrollGui<C>, S>{

    protected List<C> content;
    protected List<BiConsumer<Integer, Integer>> scrollHandlers;
    
    @Contract("_ -> this")
    public S setContent(@NotNull List<@NotNull C> content) {
        this.content = content;
        return getThis();
    }
    
    @Contract("_ -> this")
    public S addContent(@NotNull C content) {
        if (this.content == null)
            this.content = new ArrayList<>();
        
        this.content.add(content);
        return getThis();
    }
    
    @Contract("_ -> this")
    public S setScrollHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> handlers) {
        scrollHandlers = handlers;
        return getThis();
    }
    
    @Contract("_ -> this")
    public S addScrollHandler(@NotNull BiConsumer<Integer, Integer> handler) {
        if (scrollHandlers == null)
            scrollHandlers = new ArrayList<>(1);
        
        scrollHandlers.add(handler);
        return getThis();
    }
    
    @Override
    protected void applyModifiers(@NotNull ScrollGui<C> gui) {
        super.applyModifiers(gui);
        gui.setScrollHandlers(scrollHandlers);
    }
    
    @Override
    public @NotNull AbstractScrollGuiBuilder<C, S> clone() {
        var clone = (AbstractScrollGuiBuilder<C, S>) super.clone();
        clone.content = new ArrayList<>(content);
        clone.scrollHandlers = new ArrayList<>(scrollHandlers);
        return clone;
    }
    
}
