package xyz.xenondevs.invui.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Experimental
public interface Property<T> extends Supplier<T> {
    
    static <T> Property<T> of(T value) {
        return new PropertyImpl<>(value);
    }
    
    T get();
    
    void observe(Runnable observer);
    
    void unobserve(Runnable observer);
    
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
    public void observe(Runnable observer) {
        // empty
    }
    
    @Override
    public void unobserve(Runnable observer) {
        // empty
    }
    
}