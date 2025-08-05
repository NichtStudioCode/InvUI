package xyz.xenondevs.invui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.state.MutableProperty
import java.util.function.Consumer

internal class PropertyAdapter<T>(private val provider: Provider<T>) : MutableProperty<T> {
    
    override fun get(): T {
        return provider.get()
    }
    
    override fun set(value: T) {
        if (provider is MutableProvider<T>) {
            provider.set(value)
        } else {
            throw UnsupportedOperationException("This property is backed by a non-mutable provider and cannot be written to.")
        }
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