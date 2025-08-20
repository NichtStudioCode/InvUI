@file:OptIn(UnstableProviderApi::class)

package xyz.xenondevs.invui.dsl.property

import xyz.xenondevs.commons.provider.DeferredValue
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.flatten
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.provider.UnstableProviderApi

open class ProviderDslProperty<T> internal constructor(
    delegate: MutableProvider<MutableProvider<T>>
) : MutableProvider<T> by delegate.flatten() {
    
    internal constructor(initial: T) : this(mutableProvider(mutableProvider(initial)))
    
    internal var delegate: MutableProvider<T> by delegate
    
    infix fun by(provider: Provider<T>) {
        if (provider is MutableProvider<T>) {
            this.delegate = provider
        } else {
            this.delegate = NonMutableMutableProvider(provider)
        }
    }
    
    infix fun by(value: T) {
        this.delegate = mutableProvider(value)
    }
    
}

private class NonMutableMutableProvider<T>(delegate: Provider<T>) : MutableProvider<T>, Provider<T> by delegate {
    override fun <R> strongMap(transform: (T) -> R, untransform: (R) -> T) = throwUoe()
    override fun <R> map(transform: (T) -> R, untransform: (R) -> T) = throwUoe()
    override fun <R> mapObserved(createObservable: (T, () -> Unit) -> R) = throwUoe()
    override fun <R> strongMapObserved(createObservable: (T, () -> Unit) -> R) = throwUoe()
    override fun update(value: DeferredValue<T>, ignore: Set<Provider<*>>) = throwUoe()
    private fun throwUoe(): Nothing =
        throw UnsupportedOperationException("This property was changed to a non-mutable provider")
}

class MutableProviderDslProperty<T> internal constructor(
    delegate: MutableProvider<MutableProvider<T>>
) : MutableProvider<T> by delegate.flatten() {
    
    internal constructor(initial: T) : this(mutableProvider(mutableProvider(initial)))
    
    internal var delegate: MutableProvider<T> by delegate
    
    infix fun by(provider: MutableProvider<T>) {
        this.delegate = provider
    }
    
    infix fun by(value: T) {
        this.delegate = mutableProvider(value)
    }
    
}