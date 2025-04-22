package xyz.xenondevs.invui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.state.MutableProperty
import xyz.xenondevs.invui.state.Property
import java.util.function.Consumer

internal class PropertyAdapter<T : Any>(private val provider: Provider<T>) : Property<T> {
    
    override fun get(): T {
        return provider.get()
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

internal class MutablePropertyAdapter<T : Any>(private val provider: MutableProvider<T>) : MutableProperty<T> {
    
    override fun get(): T {
        return provider.get()
    }
    
    override fun set(value: T) {
        provider.set(value)
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