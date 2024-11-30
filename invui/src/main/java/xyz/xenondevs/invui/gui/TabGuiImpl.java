package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class TabGuiImpl<C extends Gui> extends AbstractTabGui<C> {
    
    private final List<@Nullable C> tabs;
    private final List<@Nullable List<SlotElement.GuiLink>> linkingElements;
    
    public TabGuiImpl(int width, int height, List<@Nullable C> tabs, int[] contentListSlots) {
        super(width, height, tabs.size(), contentListSlots);
        this.linkingElements = tabs.stream().map(this::getLinkingElements).collect(Collectors.toList());
        this.tabs = tabs;
        
        update();
    }
    
    public TabGuiImpl(List<@Nullable C> tabs, Structure structure) {
        super(structure.getWidth(), structure.getHeight(), tabs.size(), structure);
        this.linkingElements = tabs.stream().map(this::getLinkingElements).collect(Collectors.toList());
        this.tabs = tabs;
        
        update();
    }
    
    private @Nullable List<SlotElement.GuiLink> getLinkingElements(@Nullable Gui gui) {
        if (gui == null)
            return null;
        
        List<SlotElement.GuiLink> elements = new ArrayList<>();
        for (int slot = 0; slot < gui.getSize(); slot++) {
            var link = new SlotElement.GuiLink(gui, slot);
            elements.add(link);
        }
        
        return elements;
    }
    
    public List<@Nullable C> getTabs() {
        return Collections.unmodifiableList(tabs);
    }
    
    @Override
    public boolean isTabAvailable(int tab) {
        return tabs.size() > tab && tabs.get(tab) != null;
    }
    
    @Override
    protected @Nullable List<SlotElement.GuiLink> getSlotElements(int tab) {
        return linkingElements.get(tab);
    }
    
    public static final class BuilderImpl<C extends Gui> extends AbstractBuilder<C> {
        
        @Override
        public TabGui<C> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            if (tabs == null)
                throw new IllegalStateException("Tabs are not defined.");
            var gui = new TabGuiImpl<>(tabs, structure);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
