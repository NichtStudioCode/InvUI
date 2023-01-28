package de.studiocode.invui.window.type;

import de.studiocode.invui.gui.AbstractGUI;
import de.studiocode.invui.window.AnvilWindow;
import de.studiocode.invui.window.CartographyWindow;
import de.studiocode.invui.window.Window;
import de.studiocode.invui.window.impl.*;
import de.studiocode.invui.window.type.context.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface WindowType<W extends Window, C extends WindowContext> {
    
    WindowType<Window, NormalSingleWindowContext> NORMAL = new WindowType<>() {
        
        @Override
        public @NotNull NormalSingleWindowContext createContext() {
            return new NormalSingleWindowContext();
        }
        
        @Override
        public @NotNull Window createWindow(@NotNull NormalSingleWindowContext context) {
            return new NormalSingleWindowImpl(
                context.getViewer(),
                context.getTitle(),
                (AbstractGUI) context.getGUI(),
                context.isCloseable(),
                context.isRetain()
            );
        }
        
    };
    
    WindowType<Window, NormalCombinedWindowContext> NORMAL_MERGED = new WindowType<>() {
        
        @Override
        public @NotNull NormalCombinedWindowContext createContext() {
            return new NormalCombinedWindowContext();
        }
        
        @Override
        public @NotNull Window createWindow(@NotNull NormalCombinedWindowContext context) {
            return new NormalMergedWindowImpl(
                context.getViewer(),
                context.getTitle(),
                (AbstractGUI) context.getGUI(),
                context.isCloseable(),
                context.isRetain()
            );
        }
        
    };
    
    WindowType<Window, NormalSplitWindowContext> NORMAL_SPLIT = new WindowType<>() {
        
        @Override
        public @NotNull NormalSplitWindowContext createContext() {
            return new NormalSplitWindowContext();
        }
        
        @Override
        public @NotNull Window createWindow(@NotNull NormalSplitWindowContext context) {
            return new NormalSplitWindowImpl(
                context.getViewer(),
                context.getTitle(),
                (AbstractGUI) context.getUpperGUI(),
                (AbstractGUI) context.getLowerGUI(),
                context.isCloseable(),
                context.isRetain()
            );
        }
        
    };
    
    WindowType<AnvilWindow, AnvilSingleWindowContext> ANVIL = new WindowType<>() {
        
        @Override
        public @NotNull AnvilSingleWindowContext createContext() {
            return new AnvilSingleWindowContext();
        }
        
        @Override
        public @NotNull AnvilWindow createWindow(@NotNull AnvilSingleWindowContext context) {
            return new AnvilSingleWindowImpl(
                context.getViewer(),
                context.getTitle(),
                (AbstractGUI) context.getGUI(),
                context.getRenameHandler(),
                context.isCloseable(),
                context.isRetain()
            );
        }
        
    };
    
    WindowType<AnvilWindow, AnvilSplitWindowContext> ANVIL_SPLIT = new WindowType<>() {
        
        @Override
        public @NotNull AnvilSplitWindowContext createContext() {
            return new AnvilSplitWindowContext();
        }
        
        @Override
        public @NotNull AnvilWindow createWindow(@NotNull AnvilSplitWindowContext context) {
            return new AnvilSplitWindowImpl(
                context.getViewer(),
                context.getTitle(),
                (AbstractGUI) context.getUpperGUI(),
                (AbstractGUI) context.getLowerGUI(),
                context.getRenameHandler(),
                context.isCloseable(),
                context.isRetain()
            );
        }
        
    };
    
    WindowType<CartographyWindow, CartographySingleWindowContext> CARTOGRAPHY = new WindowType<>() {
        
        @Override
        public @NotNull CartographySingleWindowContext createContext() {
            return new CartographySingleWindowContext();
        }
        
        @Override
        public @NotNull CartographyWindow createWindow(@NotNull CartographySingleWindowContext context) {
            return new CartographySingleWindowImpl(
                context.getViewer(),
                context.getTitle(),
                (AbstractGUI) context.getGUI(),
                context.isCloseable(),
                context.isRetain()
            );
        }
        
    };
    
    WindowType<CartographyWindow, CartographySplitWindowContext> CARTOGRAPHY_SPLIT = new WindowType<>() {
        
        @Override
        public @NotNull CartographySplitWindowContext createContext() {
            return new CartographySplitWindowContext();
        }
        
        @Override
        public @NotNull CartographyWindow createWindow(@NotNull CartographySplitWindowContext context) {
            return new CartographySplitWindowImpl(
                context.getViewer(),
                context.getTitle(),
                (AbstractGUI) context.getUpperGUI(),
                (AbstractGUI) context.getLowerGUI(),
                context.isCloseable(),
                context.isRetain()
            );
        }
        
    };
    
    @NotNull C createContext();
    
    @NotNull W createWindow(@NotNull C context);
    
    default @NotNull W createWindow(Consumer<C> contextConsumer) {
        C context = createContext();
        contextConsumer.accept(context);
        return createWindow(context);
    }
    
}
