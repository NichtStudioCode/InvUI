package de.studiocode.invui.gui.impl;

import de.studiocode.invui.gui.AbstractPagedGUI;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.builder.GUIBuilder;
import de.studiocode.invui.gui.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A {@link AbstractPagedGUI} where every page is its own {@link GUI}.
 *
 * @see GUIBuilder
 * @see PagedItemsGUIImpl
 */
public final class PageNestedGUIImpl extends AbstractPagedGUI<GUI> {
    
    private List<GUI> guis;
    
    public PageNestedGUIImpl(int width, int height, @Nullable List<GUI> guis, int... contentListSlots) {
        super(width, height, false, contentListSlots);
        setContent(guis);
    }
    
    public PageNestedGUIImpl(@Nullable List<GUI> guis, @NotNull Structure structure) {
        super(structure.getWidth(), structure.getHeight(), false, structure);
        setContent(guis);
    }
    
    @Override
    public int getPageAmount() {
        return guis.size();
    }
    
    @Override
    public void setContent(@Nullable List<GUI> guis) {
        this.guis = guis == null ? new ArrayList<>() : guis;
        update();
    }
    
    @Override
    protected List<SlotElement> getPageElements(int page) {
        if (guis.size() <= page) return new ArrayList<>();
        
        GUI gui = guis.get(page);
        int size = gui.getSize();
        
        return IntStream.range(0, size)
            .mapToObj(i -> new SlotElement.LinkedSlotElement(gui, i))
            .collect(Collectors.toList());
    }
    
}
