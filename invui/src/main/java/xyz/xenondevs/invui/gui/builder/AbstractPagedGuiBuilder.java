package xyz.xenondevs.invui.gui.builder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.PagedGui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class AbstractPagedGuiBuilder<C, S extends AbstractPagedGuiBuilder<C, S>> extends AbstractGuiBuilder<PagedGui<C>, S> {
    
    protected List<C> content;
    protected List<BiConsumer<Integer, Integer>> pageChangeHandlers;
    
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
    public S setPageChangeHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> handlers) {
        pageChangeHandlers = handlers;
        return getThis();
    }
    
    @Contract("_ -> this")
    public S addPageChangeHandler(@NotNull BiConsumer<Integer, Integer> handler) {
        if (pageChangeHandlers == null)
            pageChangeHandlers = new ArrayList<>(1);
        
        pageChangeHandlers.add(handler);
        return getThis();
    }
    
    @Override
    protected void applyModifiers(@NotNull PagedGui<C> gui) {
        super.applyModifiers(gui);
        gui.setPageChangeHandlers(pageChangeHandlers);
    }
    
    @Override
    public @NotNull AbstractPagedGuiBuilder<C, S> clone() {
        var clone = (AbstractPagedGuiBuilder<C, S>) super.clone();
        clone.content = new ArrayList<>(content);
        clone.pageChangeHandlers = new ArrayList<>(pageChangeHandlers);
        return clone;
    }

}
