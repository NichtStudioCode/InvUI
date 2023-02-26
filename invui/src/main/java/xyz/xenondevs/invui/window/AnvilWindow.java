package xyz.xenondevs.invui.window;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public interface AnvilWindow extends Window {
    
    static @NotNull Builder.Single single() {
        return new AnvilSingleWindowImpl.BuilderImpl();
    }
    
    static @NotNull AnvilWindow single(@NotNull Consumer<Builder.@NotNull Single> consumer) {
        Builder.Single builder = single();
        consumer.accept(builder);
        return builder.build();
    }
    
    static @NotNull Builder.Split split() {
        return new AnvilSplitWindowImpl.BuilderImpl();
    }
    
    static @NotNull AnvilWindow split(Consumer<Builder.@NotNull Split> consumer) {
        Builder.Split builder = split();
        consumer.accept(builder);
        return builder.build();
    }
    
    @Nullable String getRenameText();
    
    interface Builder<S extends Builder<S>> extends Window.Builder<AnvilWindow, Player, S> {
        
        @Contract("_ -> this")
        @NotNull S setRenameHandlers(@NotNull List<@NotNull Consumer<String>> renameHandlers);
        
        @Contract("_ -> this")
        @NotNull S addRenameHandler(@NotNull Consumer<String> renameHandler);
        
        interface Single extends Builder<Single>, Window.Builder.Single<AnvilWindow, Player, Single> {}
        
        interface Split extends Builder<Split>, Window.Builder.Double<AnvilWindow, Player, Split> {}
        
    }
    
}
