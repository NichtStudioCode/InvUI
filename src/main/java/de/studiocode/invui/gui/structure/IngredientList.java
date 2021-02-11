package de.studiocode.invui.gui.structure;

import de.studiocode.invui.gui.Controllable;
import de.studiocode.invui.gui.GUI;
import de.studiocode.invui.gui.SlotElement;
import de.studiocode.invui.gui.SlotElement.ItemSlotElement;
import de.studiocode.invui.item.Item;
import de.studiocode.invui.item.impl.controlitem.ControlItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IngredientList extends ArrayList<Ingredient> {
    
    public IngredientList(String structure, HashMap<Character, Ingredient> ingredientMap) {
        for (char c : structure.toCharArray()) {
            Ingredient ingredient = null;
            if (ingredientMap.containsKey(c)) ingredient = ingredientMap.get(c);
            add(ingredient);
        }
    }
    
    public void insertIntoGUI(GUI gui) {
        if (size() != gui.getSize())
            throw new IllegalArgumentException("Structure size does not match GUI size");
        
        for (int i = 0; i < size(); i++) {
            Ingredient ingredient = get(i);
            if (ingredient != null && ingredient.isSlotElement()) {
                SlotElement slotElement = ingredient.getSlotElement();
                
                if (gui instanceof Controllable && slotElement instanceof ItemSlotElement) {
                    Item item = ((ItemSlotElement) slotElement).getItem();
                    if (item instanceof ControlItem) {
                        ((Controllable) gui).addControlItem(i, (ControlItem<?>) item);
                        continue;
                    }
                }
                
                gui.setSlotElement(i, ingredient.getSlotElement());
            }
        }
    }
    
    public int[] findIndicesOfMarker(Marker marker) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            Ingredient ingredient = get(i);
            if (ingredient != null && ingredient.isMarker() && ingredient.getMarker() == marker)
                indices.add(i);
        }
        
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }
    
}
