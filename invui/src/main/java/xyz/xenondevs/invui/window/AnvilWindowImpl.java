package xyz.xenondevs.invui.window;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.internal.menu.CustomAnvilMenu;
import xyz.xenondevs.invui.internal.util.CollectionUtils;
import xyz.xenondevs.invui.state.Property;
import xyz.xenondevs.invui.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class AnvilWindowImpl extends AbstractSplitWindow<CustomAnvilMenu> implements AnvilWindow {
    
    private final List<Consumer<? super String>> renameHandlers = new ArrayList<>();
    private final AbstractGui upperGui;
    private final AbstractGui lowerGui;
    private Property<? extends Boolean> textFieldAlwaysEnabled;
    private Property<? extends Boolean> resultAlwaysValid;
    
    public AnvilWindowImpl(
        Player player,
        Supplier<? extends Component> title,
        AbstractGui upperGui,
        AbstractGui lowerGui,
        Property<? extends Boolean> textFieldAlwaysEnabled,
        Property<? extends Boolean> resultAlwaysValid,
        Property<? extends Boolean> closeable
    ) {
        super(player, title, lowerGui, upperGui.getSize() + lowerGui.getSize(), new CustomAnvilMenu(player), closeable);
        if (upperGui.getWidth() != 3 && upperGui.getHeight() != 1)
            throw new IllegalArgumentException("Upper gui must be of dimensions 3x1");
        
        this.upperGui = upperGui;
        this.lowerGui = lowerGui;
        this.textFieldAlwaysEnabled = textFieldAlwaysEnabled;
        this.resultAlwaysValid = resultAlwaysValid;
        
        textFieldAlwaysEnabled.observeWeak(this, thisRef -> thisRef.notifyUpdate(0));
        menu.setRenameHandler(this::handleRename);
    }
    
    private void handleRename(String text) {
        for (var handler : renameHandlers) {
            handler.accept(text);
        }
    }
    
    @Override
    protected void setMenuItem(int slot, @Nullable ItemStack itemStack) {
        if ((slot == 0 && textFieldAlwaysEnabled.get()) || (slot == 2 && resultAlwaysValid.get())) {
            menu.setItem(slot, ItemUtils.takeOrPlaceholder(itemStack));
        } else {
            super.setMenuItem(slot, itemStack);
        }
    }
    
    @Override
    public String getRenameText() {
        return menu.getRenameText();
    }
    
    @Override
    public int getEnchantmentCost() {
        return menu.getEnchantmentCost();
    }
    
    @Override
    public void setEnchantmentCost(int enchantmentCost) {
        menu.setEnchantmentCost(enchantmentCost);
    }
    
    @Override
    public boolean getTextFieldAlwaysEnabled() {
        return textFieldAlwaysEnabled.get();
    }
    
    @Override
    public void setTextFieldAlwaysEnabled(boolean textFieldAlwaysEnabled) {
        this.textFieldAlwaysEnabled.unobserveWeak(this);
        this.textFieldAlwaysEnabled = Property.of(textFieldAlwaysEnabled);
        notifyUpdate(0);
    }
    
    @Override
    public boolean getResultAlwaysValid() {
        return resultAlwaysValid.get();
    }
    
    @Override
    public void setResultAlwaysValid(boolean resultAlwaysValid) {
        this.resultAlwaysValid.unobserveWeak(this);
        this.resultAlwaysValid = Property.of(resultAlwaysValid);
        notifyUpdate(2);
    }
    
    @Override
    public void addRenameHandler(Consumer<? super String> handler) {
        renameHandlers.add(handler);
    }
    
    @Override
    public void removeRenameHandler(Consumer<? super String> handler) {
        renameHandlers.remove(handler);
    }
    
    @Override
    public void setRenameHandlers(List<? extends Consumer<String>> handlers) {
        this.renameHandlers.clear();
        this.renameHandlers.addAll(handlers);
    }
    
    @Override
    public @UnmodifiableView List<Consumer<String>> getRenameHandlers() {
        return CollectionUtils.unmodifiableListUnchecked(renameHandlers);
    }
    
    @Override
    public @Unmodifiable List<Gui> getGuis() {
        return List.of(upperGui, lowerGui);
    }
    
    public static final class BuilderImpl
        extends AbstractSplitWindow.AbstractBuilder<AnvilWindow, AnvilWindow.Builder>
        implements AnvilWindow.Builder
    {
        
        private final List<Consumer<? super String>> renameHandlers = new ArrayList<>();
        private Supplier<? extends Gui> upperGuiSupplier = () -> Gui.empty(3, 1);
        private Property<? extends Boolean> textFieldAlwaysEnabled = Property.of(true);
        private Property<? extends Boolean> resultAlwaysValid = Property.of(false);
        
        @Override
        public BuilderImpl setUpperGui(Supplier<? extends Gui> guiSupplier) {
            upperGuiSupplier = guiSupplier;
            return this;
        }
        
        @Override
        public BuilderImpl setRenameHandlers(List<? extends Consumer<? super String>> handlers) {
            this.renameHandlers.clear();
            this.renameHandlers.addAll(handlers);
            return this;
        }
        
        @Override
        public BuilderImpl addRenameHandler(Consumer<? super String> renameHandler) {
            renameHandlers.add(renameHandler);
            return this;
        }
        
        @Override
        public AnvilWindow.Builder setTextFieldAlwaysEnabled(Property<? extends Boolean> textFieldAlwaysEnabled) {
            this.textFieldAlwaysEnabled = textFieldAlwaysEnabled;
            return this;
        }
        
        @Override
        public AnvilWindow.Builder setResultAlwaysValid(Property<? extends Boolean> resultAlwaysValid) {
            this.resultAlwaysValid = resultAlwaysValid;
            return this;
        }
        
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public AnvilWindow build(Player viewer) {
            var window = new AnvilWindowImpl(
                viewer,
                titleSupplier,
                (AbstractGui) upperGuiSupplier.get(),
                supplyLowerGui(viewer),
                textFieldAlwaysEnabled,
                resultAlwaysValid,
                closeable
            );
            
            window.setRenameHandlers((List) renameHandlers);
            applyModifiers(window);
            
            return window;
        }
        
    }
    
}
