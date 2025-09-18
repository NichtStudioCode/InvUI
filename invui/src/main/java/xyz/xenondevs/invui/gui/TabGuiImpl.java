package xyz.xenondevs.invui.gui;

import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.util.CollectionUtils;
import xyz.xenondevs.invui.internal.util.FuncUtils;
import xyz.xenondevs.invui.internal.util.SlotUtils;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.*;
import java.util.function.BiConsumer;

final class TabGuiImpl extends AbstractGui implements TabGui {
    
    private static final int DEFAULT_TAB = -1;
    
    private List<Slot> contentListSlots = List.of();
    
    private final MutableProperty<Integer> tab;
    private final MutableProperty<List<? extends @Nullable Gui>> tabs;
    private final List<BiConsumer<? super Integer, ? super Integer>> tabChangeHandlers = new ArrayList<>(0);
    private int previousTab = -1;
    
    public TabGuiImpl(
        int width, int height,
        List<? extends Slot> contentListSlots,
        MutableProperty<List<? extends @Nullable Gui>> tabs
    ) {
        super(width, height);
        if (contentListSlots.isEmpty())
            throw new IllegalArgumentException("Content list slots must not be empty");
        this.tab = MutableProperty.of(DEFAULT_TAB);
        tab.observeWeak(this, TabGuiImpl::handleTabChange);
        this.tabs = tabs;
        tabs.observeWeak(this, TabGuiImpl::bake);
        this.contentListSlots = new ArrayList<>(contentListSlots);
        bake();
    }
    
    public TabGuiImpl(
        Structure structure,
        MutableProperty<Integer> tab,
        MutableProperty<List<? extends @Nullable Gui>> tabs,
        MutableProperty<Boolean> frozen,
        MutableProperty<Boolean> ignoreObscuredInventorySlots,
        MutableProperty<@Nullable ItemProvider> background
    ) {
        super(structure.getWidth(), structure.getHeight(), frozen, ignoreObscuredInventorySlots, background);
        this.tab = tab;
        tab.observeWeak(this, TabGuiImpl::handleTabChange);
        this.tabs = tabs;
        tabs.observeWeak(this, TabGuiImpl::bake);
        super.applyStructure(structure); // super call to avoid bake() through applyStructure override
        setContentListSlots(structure.getIngredientMatrix().getContentListSlots());
        this.contentListSlots = structure.getIngredientMatrix().getContentListSlots();
        bake();
    }
    
    @Override
    public void applyStructure(Structure structure) {
        super.applyStructure(structure);
        this.contentListSlots = structure.getIngredientMatrix().getContentListSlots();
        bake();
    }
    
    @Override
    public void setContentListSlots(List<? extends Slot> slots) {
        this.contentListSlots = new ArrayList<>(slots);
        bake();
    }
    
    @Override
    public @Unmodifiable List<Slot> getContentListSlots() {
        return Collections.unmodifiableList(contentListSlots);
    }
    
    @Override
    public void bake() {
        // -- baking removed --
        setTab(getTab()); // corrects tab and refreshes content
    }
    
    private void handleTabChange() {
        int targetTab = getTab();
        int correctedTab = correctTab(targetTab);
        if (correctedTab != targetTab) {
            tab.set(correctedTab);
            return;
        }
        
        updateContent();
        if (targetTab != previousTab) {
            CollectionUtils.forEachCatching(
                tabChangeHandlers,
                handler -> handler.accept(previousTab, targetTab),
                "Failed to handle tab change from " + previousTab + " to " + targetTab
            );
        }
        previousTab = targetTab;
    }
    
    private int correctTab(int tab) {
        // coerce tab in valid range
        tab = Math.max(0, Math.min(tab, getTabs().size() - 1));
        
        // if the tab is available, it is correct
        if (isTabAvailable(tab))
            return tab;
        
        // find the closest available tab to left and right
        for (int i = 1; ; i++) {
            int tabLeft = tab - i;
            int tabRight = tab + i;
            
            if (tabLeft < 0 && tabRight >= getTabs().size())
                return -1;
            
            if (isTabAvailable(tabLeft)) {
                return tabLeft;
            } else if (isTabAvailable(tabRight)) {
                return tabRight;
            }
        }
    }
    
    @Override
    public boolean isTabAvailable(int tab) {
        var tabs = getTabs();
        return tab >= 0 && tab < tabs.size() && tabs.get(tab) != null;
    }
    
    private void updateContent() {
        int currentTab = getTab();
        var tabs = getTabs();
        var min = SlotUtils.min(contentListSlots);
        if (currentTab >= 0 && tabs.size() > currentTab && tabs.get(currentTab) instanceof Gui gui) {
            for (Slot slot : contentListSlots) {
                setSlotElement(slot, SlotUtils.getGuiLinkOrNull(gui, slot.x() - min.x(), slot.y() - min.y()));
            }
        } else {
            for (Slot slot : contentListSlots) {
                setSlotElement(slot, null);
            }
        }
    }
    
    @Override
    public void setTab(int tab) {
        this.tab.set(tab);
    }
    
    public int getTab() {
        return FuncUtils.getSafely(tab, DEFAULT_TAB);
    }
    
    @Override
    public void setTabs(List<? extends @Nullable Gui> tabs) {
        this.tabs.set(tabs);
    }
    
    @Override
    public @UnmodifiableView List<@Nullable Gui> getTabs() {
        return Collections.unmodifiableList(FuncUtils.getSafely(tabs, List.of()));
    }
    
    @Override
    public @UnmodifiableView List<BiConsumer<Integer, Integer>> getTabChangeHandlers() {
        return CollectionUtils.unmodifiableListUnchecked(tabChangeHandlers);
    }
    
    @Override
    public void setTabChangeHandlers(List<? extends BiConsumer<Integer, Integer>> handlers) {
        tabChangeHandlers.clear();
        tabChangeHandlers.addAll(handlers);
    }
    
    @Override
    public void addTabChangeHandler(BiConsumer<? super Integer, ? super Integer> handler) {
        tabChangeHandlers.add(handler);
    }
    
    @Override
    public void removeTabChangeHandler(BiConsumer<? super Integer, ? super Integer> handler) {
        tabChangeHandlers.remove(handler);
    }
    
    public static final class Builder
        extends AbstractGui.AbstractBuilder<TabGui, TabGui.Builder>
        implements TabGui.Builder
    {
        
        private MutableProperty<List<? extends @Nullable Gui>> tabs = MutableProperty.of(List.of());
        private MutableProperty<Integer> tab = MutableProperty.of(DEFAULT_TAB);
        private List<BiConsumer<? super Integer, ? super Integer>> tabChangeHandlers = new ArrayList<>(0);
        
        @Override
        public TabGui.Builder setTabs(MutableProperty<List<? extends @Nullable Gui>> tabs) {
            this.tabs = tabs;
            return this;
        }
        
        @Override
        public TabGui.Builder setTab(MutableProperty<Integer> tab) {
            this.tab = tab;
            return this;
        }
        
        @Override
        public TabGui.Builder setTabChangeHandlers(List<? extends BiConsumer<? super Integer, ? super Integer>> handlers) {
            tabChangeHandlers.clear();
            tabChangeHandlers.addAll(handlers);
            return this;
        }
        
        @Override
        public TabGui.Builder addTabChangeHandler(BiConsumer<? super Integer, ? super Integer> handler) {
            tabChangeHandlers.add(handler);
            return this;
        }
        
        @Override
        public TabGui build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new TabGuiImpl(structure, tab, tabs, frozen, ignoreObscuredInventorySlots, background);
            tabChangeHandlers.forEach(gui::addTabChangeHandler);
            applyModifiers(gui);
            
            return gui;
        }
        
        @Override
        public TabGui.Builder clone() {
            var clone = (Builder) super.clone();
            clone.tabChangeHandlers = new ArrayList<>(tabChangeHandlers);
            return clone;
        }
        
    }
    
}
