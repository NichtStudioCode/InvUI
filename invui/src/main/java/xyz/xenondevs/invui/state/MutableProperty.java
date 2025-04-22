package xyz.xenondevs.invui.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.*;
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
    
    private final Map<Object, List<Consumer<Object>>> weakObservers = new WeakHashMap<>();
    private T value;
    
    public MutablePropertyImpl(T value) {
        this.value = value;
    }
    
    @Override
    public void set(T value) {
        this.value = value;
        weakObservers.forEach((owner, observers) -> observers.forEach(observer -> observer.accept(owner)));
    }
    
    @Override
    public T get() {
        return value;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <O> void observeWeak(O owner, Consumer<? super O> observer) {
        weakObservers.computeIfAbsent(owner, k -> new ArrayList<>())
            .add((Consumer<Object>)observer);
    }
    
    @Override
    public <O> void unobserveWeak(O owner, Consumer<? super O> observer) {
        List<Consumer<Object>> observers = weakObservers.get(owner);
        if (observers != null) {
            observers.remove(observer);
            if (observers.isEmpty()) {
                weakObservers.remove(owner);
            }
        }
    }
    
    @Override
    public void unobserveWeak(Object owner) {
        weakObservers.remove(owner);
    }
    
}
