package de.studiocode.invui.gui;

import java.util.List;

public interface ScrollGUI<C> extends GUI {
    
    /**
     * Gets the current line of this {@link ScrollGUI}.
     *
     * @return The current line of this {@link ScrollGUI}.
     */
    int getCurrentLine();
    
    /**
     * Gets the max line index of this {@link ScrollGUI}.
     *
     * @return The max line index of this {@link ScrollGUI}.
     */
    int getMaxLine();
    
    /**
     * Sets the current line of this {@link ScrollGUI}.
     *
     * @param line The line to set.
     */
    void setCurrentLine(int line);
    
    /**
     * Checks if it is possible to scroll the specified amount of lines.
     *
     * @return Whether it is possible to scroll the specified amount of lines.
     */
    boolean canScroll(int lines);
    
    /**
     * Scrolls the specified amount of lines.
     *
     * @param lines The amount of lines to scroll.
     */
    void scroll(int lines);
    
    /**
     * Sets the content of this {@link ScrollGUI} for all lines.
     *
     * @param content The content to set.
     */
    void setContent(List<C> content);
    
}
