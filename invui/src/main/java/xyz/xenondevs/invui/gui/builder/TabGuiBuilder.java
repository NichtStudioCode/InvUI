package xyz.xenondevs.invui.gui.builder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.gui.impl.TabGuiImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public final class TabGuiBuilder extends AbstractGuiBuilder<TabGui, TabGuiBuilder> {
    
    private List<Gui> tabs;
    private List<BiConsumer<Integer, Integer>> tabChangeHandlers;
    
    TabGuiBuilder() {
    }
    
    @Contract("_ -> this")
    public TabGuiBuilder setTabs(@NotNull List<@Nullable Gui> tabs) {
        this.tabs = tabs;
        return this;
    }
    
    @Contract("_ -> this")
    public TabGuiBuilder addTab(@Nullable Gui tab) {
        if (this.tabs == null)
            this.tabs = new ArrayList<>();
        
        this.tabs.add(tab);
        return this;
    }
    
    @Contract("_ -> this")
    public TabGuiBuilder addTabChangeHandler(@NotNull BiConsumer<Integer, Integer> handler) {
        if (tabChangeHandlers == null)
            tabChangeHandlers = new ArrayList<>(1);
        
        tabChangeHandlers.add(handler);
        return this;
    }
    
    @Contract("_ -> this")
    public TabGuiBuilder setTabChangeHandlers(@NotNull List<@NotNull BiConsumer<Integer, Integer>> handlers) {
        tabChangeHandlers = handlers;
        return this;
    }
    
    @Override
    protected void applyModifiers(@NotNull TabGui gui) {
        super.applyModifiers(gui);
        gui.setTabChangeHandlers(tabChangeHandlers);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull TabGui build() {
        if (structure == null)
            throw new IllegalStateException("Structure is not defined.");
        if (tabs == null)
            throw new IllegalStateException("Tabs are not defined.");
        var gui = new TabGuiImpl(tabs, structure);
        applyModifiers(gui);
        return gui;
    }
    
    @Override
    protected TabGuiBuilder getThis() {
        return this;
    }
    
    @Override
    public @NotNull TabGuiBuilder clone() {
        var clone = (TabGuiBuilder) super.clone();
        clone.tabs = new ArrayList<>(tabs);
        clone.tabChangeHandlers = new ArrayList<>(tabChangeHandlers);
        return clone;
    }
    
}
