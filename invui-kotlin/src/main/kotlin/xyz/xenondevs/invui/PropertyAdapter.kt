package xyz.xenondevs.invui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.invui.state.MutableProperty
import xyz.xenondevs.invui.state.Property
import java.util.function.Consumer

@ExperimentalReactiveApi
internal class PropertyAdapter<T>(
    val provider: Provider<T>,
    val mutableView: MutableProvider<T>? = null
) : MutableProperty<T> {
    
    constructor(provider: MutableProvider<T>) : this(provider, provider)
    
    constructor(provider: Provider<T>) : this(provider, null)
    
    override fun get(): T {
        return provider.get()
    }
    
    override fun set(value: T) {
        if (mutableView == null)
            throw UnsupportedOperationException("This property was changed to a non-mutable provider")
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

@ExperimentalReactiveApi
internal fun <T> MutableProperty<T>.toProvider(): Provider<T> {
    if (this is PropertyAdapter<T>)
        return mutableView ?: provider
    
    val child = mutableProvider { get() }
    observeWeak(child) { child -> child.set(get()) }
    child.subscribe { if (get() != it) set(it) }
    return child
}

@ExperimentalReactiveApi
internal fun <T> Property<T>.toProvider(): Provider<T> {
    if (this is PropertyAdapter<T>)
        return mutableView ?: provider
    
    val child = mutableProvider { get() }
    observeWeak(child) { child -> child.set(get()) }
    return child
}