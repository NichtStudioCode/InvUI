package xyz.xenondevs.invui.gui.builder;

import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.builder.guitype.GuiType;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.List;

/**
 * The {@link GuiContext} contains all information from the {@link GuiBuilder} to be passed to
 * an instance of {@link GuiType} to create a new {@link Gui}.
 */
public class GuiContext<C> {
    
    private Structure structure;
    private ItemProvider background;
    private List<C> content;
    
    public Structure getStructure() {
        return structure;
    }
    
    public void setStructure(@NotNull Structure structure) {
        this.structure = structure;
    }
    
    public ItemProvider getBackground() {
        return background;
    }
    
    public void setBackground(ItemProvider background) {
        this.background = background;
    }
    
    public void setContent(@NotNull List<C> content) {
        this.content = content;
    }
    
    public List<C> getContent() {
        return content;
    }
    
}
