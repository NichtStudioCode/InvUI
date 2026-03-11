package xyz.xenondevs.invui.gui;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;
import xyz.xenondevs.invui.state.MutableProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/**
 * A custom {@link MutableProperty} implementation that only propagates updates from the upstream property when
 * {@link #flushDirty()} is called, introducing a batching mechanism for updates.
 *
 * @param <T> The type of the property value.
 */
@NullUnmarked
class BatchingProperty<T> implements MutableProperty<T> {
    
    private static final ScopedValue<Void> DOWNSTREAM_UPDATE = ScopedValue.newInstance();
    
    public final MutableProperty<T> upstream;
    private final Map<Object, List<Consumer<Object>>> observers = new WeakHashMap<>();
    
    private volatile boolean dirty = false;
    
    public BatchingProperty(T constantValue) {
        this.upstream = MutableProperty.of(constantValue);
    }
    
    public BatchingProperty(MutableProperty<T> upstream, Runnable notifier) {
        this.upstream = upstream;
        upstream.observeWeak(this, thisRef -> {
            if (!DOWNSTREAM_UPDATE.isBound()) {
                thisRef.dirty = true;
                notifier.run();
            }
        });
    }
    
    public void flushDirty() {
        if (!dirty)
            return;
        dirty = false;
        observers.forEach((owner, observers) -> observers.forEach(observer -> observer.accept(owner)));
    }
    
    @Override
    public void set(T value) {
        ScopedValue.where(DOWNSTREAM_UPDATE, null).run(() -> {
            dirty = false;
            upstream.set(value);
            observers.forEach((owner, observers) -> observers.forEach(observer -> observer.accept(owner)));
        });
    }
    
    @Override
    public T get() {
        return upstream.get();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <O> void observeWeak(@NonNull O owner, @NonNull Consumer<? super O> observer) {
        observers.computeIfAbsent(owner, _ -> new ArrayList<>())
            .add((Consumer<Object>) observer);
    }
    
    @Override
    public <O> void unobserveWeak(@NonNull O owner, @NonNull Consumer<? super O> observer) {
        var map = observers.get(owner);
        if (map == null)
            return;
        map.remove(observer);
    }
    
    @Override
    public void unobserveWeak(@NonNull Object owner) {
        observers.remove(owner);
    }
    
}
