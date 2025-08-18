package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.state.MutableProperty;

final class NormalGuiImpl extends AbstractGui {
    
    public NormalGuiImpl(int width, int height) {
        super(width, height);
    }
    
    public NormalGuiImpl(
        Structure structure,
        MutableProperty<Boolean> frozen,
        MutableProperty<Boolean> ignoreObscuredInventorySlots,
        MutableProperty<@Nullable ItemProvider> background
    ) {
        super(structure.getWidth(), structure.getHeight(), frozen, ignoreObscuredInventorySlots, background);
        applyStructure(structure);
    }
    
    static final class Builder extends AbstractBuilder<NormalGuiImpl, Builder> {
        
        @Override
        public NormalGuiImpl build() {
            if (structure == null)
                throw new IllegalStateException("Structure is not defined.");
            
            var gui = new NormalGuiImpl(structure, frozen, ignoreObscuredInventorySlots, background);
            applyModifiers(gui);
            return gui;
        }
        
    }
    
}
