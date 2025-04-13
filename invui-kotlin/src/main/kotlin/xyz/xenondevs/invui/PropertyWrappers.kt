package xyz.xenondevs.invui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.state.MutableProperty
import xyz.xenondevs.invui.state.Property

internal class PropertyAdapter<T : Any>(private val provider: Provider<T>) : Property<T> {
    
    override fun get(): T {
        return provider.get()
    }
    
    override fun observe(observer: Runnable) {
        provider.observe(observer::run)
    }
    
    override fun unobserve(observer: Runnable) {
        provider.unobserve(observer::run)
    }
    
    
}

internal class MutablePropertyAdapter<T : Any>(private val provider: MutableProvider<T>) : MutableProperty<T> {
    
    override fun get(): T {
        return provider.get()
    }
    
    override fun set(value: T) {
        provider.set(value)
    }
    
    override fun observe(observer: Runnable) {
        provider.observe(observer::run)
    }
    
    override fun unobserve(observer: Runnable) {
        provider.unobserve(observer::run)
    }
    
}