package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

sealed abstract class AbstractTabGui<C extends Gui>
    extends AbstractGui
    implements TabGui<C>
    permits TabGuiImpl
{
    
    private final int tabAmount;
    private final int[] listSlots;
    
    private int currentTab = -1;
    
    private @Nullable List<BiConsumer<Integer, Integer>> tabChangeHandlers;
    
    public AbstractTabGui(int width, int height, int tabAmount, int... listSlots) {
        super(width, height);
        this.tabAmount = tabAmount;
        this.listSlots = listSlots;
    }
    
    public AbstractTabGui(int width, int height, int tabAmount, Structure structure) {
        this(width, height, tabAmount, structure.getIngredientList().findContentListSlots());
        applyStructure(structure);
    }
    
    @Override
    public void setTab(int tab) {
        if (tab < 0 || tab >= tabAmount)
            throw new IllegalArgumentException("Tab out of bounds");
        if (!isTabAvailable(tab))
            return;
        
        int previous = currentTab;
        currentTab = tab;
        update();
        
        if (tabChangeHandlers != null) {
            tabChangeHandlers.forEach(handler -> handler.accept(previous, tab));
        }
    }
    
    protected void update() {
        if (currentTab == -1) currentTab = getFirstAvailableTab();
        
        updateContent();
    }
    
    private void updateContent() {
        List<SlotElement.GuiLink> slotElements = getSlotElements(currentTab);
        for (int i = 0; i < listSlots.length; i++) {
            int slot = listSlots[i];
            if (slotElements.size() > i)
                setSlotElement(listSlots[i], slotElements.get(i));
            else remove(slot);
        }
    }
    
    public int getFirstAvailableTab() {
        for (int tab = 0; tab < tabAmount; tab++) {
            if (isTabAvailable(tab)) return tab;
        }
        
        throw new UnsupportedOperationException("At least one tab needs to be available");
    }
    
    public int getCurrentTab() {
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
    
    public abstract boolean isTabAvailable(int tab);
    
    protected abstract @Nullable List<SlotElement.GuiLink> getSlotElements(int tab);
    
    public static sealed abstract class AbstractBuilder<C extends Gui>
        extends AbstractGui.AbstractBuilder<TabGui<C>, TabGui.Builder<C>>
        implements TabGui.Builder<C>
        permits TabGuiImpl.BuilderImpl
    {
        
        protected @Nullable List<@Nullable C> tabs;
        protected @Nullable List<BiConsumer<Integer, Integer>> tabChangeHandlers;
        
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
        protected void applyModifiers(TabGui<C> gui) {
            super.applyModifiers(gui);
            gui.setTabChangeHandlers(tabChangeHandlers);
        }
        
        @Override
        public TabGui.Builder<C> clone() {
            var clone = (AbstractBuilder<C>) super.clone();
            if (tabs != null)
                clone.tabs = new ArrayList<>(tabs);
            if (tabChangeHandlers != null)
                clone.tabChangeHandlers = new ArrayList<>(tabChangeHandlers);
            return clone;
        }
        
    }
    
}
