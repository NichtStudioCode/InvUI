package de.studiocode.invui.gui.builder;

import de.studiocode.invui.gui.Gui;
import de.studiocode.invui.gui.builder.guitype.GuiType;
import de.studiocode.invui.gui.structure.Structure;
import de.studiocode.invui.item.ItemProvider;
import org.jetbrains.annotations.NotNull;

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
