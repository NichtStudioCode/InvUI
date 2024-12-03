package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

final class TabGuiImpl<C extends Gui> extends AbstractGui implements TabGui<C> {
    
    private final int[] contentListSlots;
    private @Nullable List<BiConsumer<Integer, Integer>> tabChangeHandlers;
    
    private Supplier<List<@Nullable C>> tabsSupplier = List::of;
    private List<@Nullable List<SlotElement.GuiLink>> linkingElements = List.of();
    private int currentTab = -1;
    
    public TabGuiImpl(int width, int height, List<@Nullable C> tabs, int[] contentListSlots) {
        super(width, height);
        if (contentListSlots.length == 0)
            throw new IllegalArgumentException("Content list slots must not be empty");
        this.contentListSlots = contentListSlots;
        setTabs(tabs);
    }
    
    public TabGuiImpl(Supplier<List<@Nullable C>> tabs, Structure structure) {
        super(structure.getWidth(), structure.getHeight());
        applyStructure(structure);
        contentListSlots = structure.getIngredientList().findContentListSlots();
        setTabs(tabs);
    }
    
    @Override
    public void bake() {
        List<@Nullable List<SlotElement.GuiLink>> linkingElements = new ArrayList<>();
        for (var gui : tabsSupplier.get()) {
            if (gui != null) {
                List<SlotElement.GuiLink> elements = new ArrayList<>();
                for (int slot = 0; slot < gui.getSize(); slot++) {
                    var link = new SlotElement.GuiLink(gui, slot);
                    elements.add(link);
                }
                linkingElements.add(elements);
            } else {
                linkingElements.add(null);
            }
        }
        
        this.linkingElements = linkingElements;
        update();
    }
    
    private void update() {
        if (currentTab == -1)
            currentTab = getFirstAvailableTab();
        
        updateContent();
    }
    
    private void updateContent() {
        if (currentTab == -1)
            return;
        
        List<SlotElement.GuiLink> slotElements = linkingElements.get(currentTab);
        for (int i = 0; i < contentListSlots.length; i++) {
            int slot = contentListSlots[i];
            if (slotElements != null && slotElements.size() > i)
                setSlotElement(contentListSlots[i], slotElements.get(i));
            else remove(slot);
        }
    }
    
    public int getFirstAvailableTab() {
        var tabCount = getTabs().size();
        for (int tab = 0; tab < tabCount; tab++) {
            if (isTabAvailable(tab))
                return tab;
        }
        
        return -1;
    }
    
    @Override
    public void setTabs(List<@Nullable C> tabs) {
        setTabs(() -> tabs);
    }
    
    @Override
    public void setTabs(Supplier<List<@Nullable C>> tabsSupplier) {
        this.tabsSupplier = tabsSupplier;
    }
    
    @Override
    public List<@Nullable C> getTabs() {
        return tabsSupplier.get();
    }
    
    @Override
    public boolean isTabAvailable(int tab) {
        var tabs = getTabs();
        return tabs.size() > tab && tabs.get(tab) != null;
    }
    
    @Override
    public void setTab(int tab) {
        if (tab < 0 || tab >= getTabs().size())
            throw new IllegalArgumentException("Tab out of bounds");
        if (!isTabAvailable(tab))
            return;
        if (tab == currentTab)
            return;
        
        int previous = currentTab;
        currentTab = tab;
        update();
        
        if (tabChangeHandlers != null) {
            tabChangeHandlers.forEach(handler -> handler.accept(previous, tab));
        }
    }
    
    public int getTab() {
        return currentTab;
    }
    
    @Override
    public @Nullable List<BiConsumer<Integer, Integer>> getTabChangeHandlers() {
        return tabChangeHandlers;
    }
    
    @Override
    public void setTabChangeHandlers(@Nullable List<BiConsumer<Integer, Integer>> tabChangeHandlers) {
        this.tabChangeHandlers = tabChangeHandlers;
    }
    
    public void addTabChangeHandler(BiConsumer<Integer, Integer> tabChangeHandler) {
        if (tabChangeHandlers == null) tabChangeHandlers = new ArrayList<>();
        tabChangeHandlers.add(tabChangeHandler);
    }
    
    @Override
    public void removeTabChangeHandler(BiConsumer<Integer, Integer> tabChangeHandler) {
        if (tabChangeHandlers != null) tabChangeHandlers.remove(tabChangeHandler);
    }
    
    public static final class Builder<C extends Gui>
        extends AbstractGui.AbstractBuilder<TabGui<C>, TabGui.Builder<C>>
        implements TabGui.Builder<C>
    {
        
        private @Nullable Supplier<List<@Nullable C>> tabSupplier;
        private @Nullable List<@Nullable C> tabs;
        private @Nullable List<BiConsumer<Integer, Integer>> tabChangeHandlers;
        
        @Override
        public TabGui.Builder<C> setTabs(Supplier<List<@Nullable C>> tabsSupplier) {
            this.tabSupplier = tabsSupplier;
            return this;
        }
        
        @Override
        public TabGui.Builder<C> setTabs(List<@Nullable C> tabs) {
            this.tabs = tabs;
            return this;
        }
        
        @Override
        public TabGui.Builder<C> addTab(@Nullable C tab) {
            if (this.tabs == null)
                this.tabs = new ArrayList<>();
            
            this.tabs.add(tab);
            return this;
        }
        
        @Override
        public TabGui.Builder<C> addTabChangeHandler(BiConsumer<Integer, Integer> handler) {
            if (tabChangeHandlers == null)
                tabChangeHandlers = new ArrayList<>(1);
            
            tabChangeHandlers.add(handler);
            return this;
        }
        
        @Override
        public TabGui.Builder<C> setTabChangeHandlers(List<BiConsumer<Integer, Integer>> handlers) {
            tabChangeHandlers = handlers;
            return this;
        }
        
        @Override
        public TabGui<C> build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            Supplier<List<@Nullable C>> supplier = tabSupplier != null
                ? tabSupplier
                : () -> tabs != null ? tabs : List.of();
            
            var gui = new TabGuiImpl<>(supplier, structure);
            
            if (tabChangeHandlers != null) {
                for (var handler : tabChangeHandlers) {
                    gui.addTabChangeHandler(handler);
                }
            }
            
            applyModifiers(gui);
            
            return gui;
        }
        
        @Override
        public TabGui.Builder<C> clone() {
            var clone = (Builder<C>) super.clone();
            if (tabs != null)
                clone.tabs = new ArrayList<>(tabs);
            if (tabChangeHandlers != null)
                clone.tabChangeHandlers = new ArrayList<>(tabChangeHandlers);
            return clone;
        }
        
    }
    
}
