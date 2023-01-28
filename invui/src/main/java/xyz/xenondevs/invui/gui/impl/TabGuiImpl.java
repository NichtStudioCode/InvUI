package xyz.xenondevs.invui.gui.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractTabGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.gui.builder.GuiBuilder;
import xyz.xenondevs.invui.gui.structure.Structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link Gui} that has multiple tabs with which users can switch between {@link Gui}s.
 *
 * @see GuiBuilder
 */
@SuppressWarnings("DeprecatedIsStillUsed")
public final class TabGuiImpl extends AbstractTabGui {
    
    private final List<Gui> tabs;
    private final List<List<SlotElement>> linkingElements;
    
    /**
     * Creates a new {@link TabGuiImpl}.
     *
     * @param width            The width of this Gui.
     * @param height           The height of this Gui.
     * @param tabs             The {@link Gui Guis} to use as tabs.
     * @param contentListSlots The slots where content should be displayed.
     * @deprecated Use {@link TabGui#of(int, int, List, int...)} instead.
     */
    @Deprecated
    public TabGuiImpl(int width, int height, @NotNull List<@Nullable Gui> tabs, int[] contentListSlots) {
        super(width, height, tabs.size(), contentListSlots);
        this.linkingElements = tabs.stream().map(this::getLinkingElements).collect(Collectors.toList());
        this.tabs = tabs;
        
        update();
    }
    
    /**
     * Creates a new {@link TabGuiImpl}.
     *
     * @param tabs      The {@link Gui Guis} to use as tabs.
     * @param structure The {@link Structure} to use.
     * @deprecated Use {@link TabGui#of(Structure, List)} instead.
     */
    @Deprecated
    public TabGuiImpl(@NotNull List<@Nullable Gui> tabs, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), tabs.size(), structure);
        this.linkingElements = tabs.stream().map(this::getLinkingElements).collect(Collectors.toList());
        this.tabs = tabs;
        
        update();
    }
    
    private List<SlotElement> getLinkingElements(Gui gui) {
        if (gui == null) return null;
        
        List<SlotElement> elements = new ArrayList<>();
        for (int slot = 0; slot < gui.getSize(); slot++) {
            SlotElement link = new SlotElement.LinkedSlotElement(gui, slot);
            elements.add(link);
        }
        
        return elements;
    }
    
    public @NotNull List<@Nullable Gui> getTabs() {
        return Collections.unmodifiableList(tabs);
    }
    
    @Override
    public boolean isTabAvailable(int tab) {
        return tabs.get(tab) != null;
    }
    
    @Override
    protected List<SlotElement> getSlotElements(int tab) {
        return linkingElements.get(tab);
    }
    
}
