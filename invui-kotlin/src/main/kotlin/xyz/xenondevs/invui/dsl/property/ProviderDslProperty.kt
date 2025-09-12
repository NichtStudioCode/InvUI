@file:OptIn(UnstableProviderApi::class)

package xyz.xenondevs.invui.dsl.property

import xyz.xenondevs.commons.provider.DeferredValue
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.UnstableProviderApi
import xyz.xenondevs.commons.provider.immediateFlatten
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.invui.dsl.ExperimentalDslApi

@ExperimentalDslApi
open class ProviderDslProperty<T> internal constructor(
    private val source: MutableProvider<MutableProvider<T>>,
    override val identifier: MutableProvider<T> = source.immediateFlatten()
) : MutableProvider<T> by identifier {
    
    internal constructor(initial: T) : this(mutableProvider(mutableProvider(initial)))
    
    infix fun by(provider: MutableProvider<T>) {
        source.set(provider)
    }
    
    infix fun by(provider: Provider<T>) {
        source.set(NonMutableMutableProvider(provider))
    }
    
    infix fun by(value: T) {
        source.set(mutableProvider(value))
    }
    
    override fun equals(other: Any?): Boolean = other is Provider<*> && other.identifier === identifier
    override fun hashCode(): Int = System.identityHashCode(identifier)
    
}

@ExperimentalDslApi
private class NonMutableMutableProvider<T>(override val identifier: Provider<T>) : MutableProvider<T>, Provider<T> by identifier {
    override fun <R> strongMap(transform: (T) -> R, untransform: (R) -> T) = throwUoe()
    override fun <R> map(transform: (T) -> R, untransform: (R) -> T) = throwUoe()
    override fun <R> mapObserved(createObservable: (T, () -> Unit) -> R) = throwUoe()
    override fun <R> strongMapObserved(createObservable: (T, () -> Unit) -> R) = throwUoe()
    override fun update(value: DeferredValue<T>, ignore: Set<Provider<*>>) = throwUoe()
    private fun throwUoe(): Nothing =
        throw UnsupportedOperationException("This property was changed to a non-mutable provider")
    
    override fun equals(other: Any?): Boolean = other is Provider<*> && other.identifier === identifier
    override fun hashCode(): Int = System.identityHashCode(identifier)
}

@ExperimentalDslApi
class MutableProviderDslProperty<T> internal constructor(
    private val source: MutableProvider<MutableProvider<T>>,
    override val identifier: MutableProvider<T> = source.immediateFlatten()
) : MutableProvider<T> by identifier {
    
    internal constructor(initial: T) : this(mutableProvider(mutableProvider(initial)))
    
    infix fun by(provider: MutableProvider<T>) {
        source.set(provider)
    }
    
    infix fun by(value: T) {
        source.set(mutableProvider(value))
    }
    
    override fun equals(other: Any?): Boolean = other is Provider<*> && other.identifier === identifier
    override fun hashCode(): Int = System.identityHashCode(identifier)
    
}