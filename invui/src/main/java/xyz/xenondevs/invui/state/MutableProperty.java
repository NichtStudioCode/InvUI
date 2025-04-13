package xyz.xenondevs.invui.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@ApiStatus.Experimental
public interface MutableProperty<T> extends Property<T>, Consumer<T> {
    
    static <T> MutableProperty<T> of(T value) {
        return new MutablePropertyImpl<>(value);
    }
    
    @Override
    default void accept(T value) {
        set(value);
    }
    
    void set(T value);
    
}

class MutablePropertyImpl<T> implements MutableProperty<T> {
    
    private final Set<Runnable> observers = new HashSet<>();
    private T value;
    
    public MutablePropertyImpl(T value) {
        this.value = value;
    }
    
    @Override
    public void set(T value) {
        this.value = value;
        observers.forEach(Runnable::run);
    }
    
    @Override
    public T get() {
        return value;
    }
    
    @Override
    public void observe(Runnable observer) {
        observers.add(observer);
    }
    
    @Override
    public void unobserve(Runnable observer) {
        observers.remove(observer);
    }
    
}
