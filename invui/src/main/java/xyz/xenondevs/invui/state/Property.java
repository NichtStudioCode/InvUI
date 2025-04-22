package xyz.xenondevs.invui.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Experimental
public interface Property<T> extends Supplier<T> {
    
    static <T> Property<T> of(T value) {
        return new PropertyImpl<>(value);
    }
    
    T get();
    
    <O> void observeWeak(O owner, Consumer<? super O> observer);
    
    <O> void unobserveWeak(O owner, Consumer<? super O> observer);
    
    void unobserveWeak(Object owner);
    
}

class PropertyImpl<T> implements Property<T> {
    
    private final T value;
    
    public PropertyImpl(T value) {
        this.value = value;
    }
    
    @Override
    public T get() {
        return value;
    }
    
    @Override
    public <O> void observeWeak(O owner, Consumer<? super O> observer) {
        // empty
    }
    
    @Override
    public <O> void unobserveWeak(O owner, Consumer<? super O> observer) {
        // empty
    }
    
    @Override
    public void unobserveWeak(Object owner) {
        // empty
    }
    
}