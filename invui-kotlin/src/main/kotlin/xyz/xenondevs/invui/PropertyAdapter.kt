package xyz.xenondevs.invui

import xyz.xenondevs.commons.provider.DeferredValue
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.UnstableProviderApi
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.invui.state.MutableProperty
import xyz.xenondevs.invui.state.Property
import java.util.function.Consumer

internal class PropertyAdapter<T>(
    val provider: Provider<T>,
    val mutableView: MutableProvider<T>
) : MutableProperty<T> {
    
    constructor(provider: MutableProvider<T>) : this(provider, provider)
    
    constructor(provider: Provider<T>) : this(provider, NonMutableMutableProvider(provider))
    
    override fun get(): T {
        return provider.get()
    }
    
    override fun set(value: T) {
        mutableView.set(value)
    }
    
    override fun <O : Any> observeWeak(owner: O, observer: Consumer<in O>) {
        provider.observeWeak(owner, observer::accept)
    }
    
    override fun <O : Any> unobserveWeak(owner: O, observer: Consumer<in O>) {
        provider.unobserveWeak(owner, observer::accept)
    }
    
    override fun unobserveWeak(owner: Any) {
        provider.unobserveWeak(owner)
    }
    
}

@OptIn(UnstableProviderApi::class)
internal class NonMutableMutableProvider<T>(override val identifier: Provider<T>) : MutableProvider<T>, Provider<T> by identifier {
    override fun <R> strongMap(transform: (T) -> R, untransform: (R) -> T) = throwUoe()
    override fun <R> map(transform: (T) -> R, untransform: (R) -> T) = throwUoe()
    override fun <R> mapObserved(createObservable: (T, () -> Unit) -> R) = throwUoe()
    override fun <R> strongMapObserved(createObservable: (T, () -> Unit) -> R) = throwUoe()
    override fun consume(source: Provider<T>) = throwUoe()
    override fun update(value: DeferredValue<T>, ignore: Set<Provider<*>>) = throwUoe()
    private fun throwUoe(): Nothing =
        throw UnsupportedOperationException("This property was changed to a non-mutable provider")
    
    override fun equals(other: Any?): Boolean = other is Provider<*> && other.identifier === identifier
    override fun hashCode(): Int = System.identityHashCode(identifier)
}

internal fun <T> MutableProperty<T>.toProvider(): MutableProvider<T> {
    if (this is PropertyAdapter<T>) 
        return mutableView
    
    val child = mutableProvider { get() }
    observeWeak(child) { child -> child.set(get()) }
    child.subscribe { if (get() != it) set(it) }
    return child
}

internal fun <T> Property<T>.toProvider(): Provider<T> {
    if (this is PropertyAdapter<T>)
        return provider
    
    val child = mutableProvider { get() }
    observeWeak(child) { child -> child.set(get()) }
    return child
}