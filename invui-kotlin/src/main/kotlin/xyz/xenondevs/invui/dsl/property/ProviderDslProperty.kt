@file:OptIn(UnstableProviderApi::class)

package xyz.xenondevs.invui.dsl.property

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.UnstableProviderApi
import xyz.xenondevs.commons.provider.immediateFlatten
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.dsl.ExperimentalDslApi

@ExperimentalDslApi
open class ProviderDslProperty<T> internal constructor(
    private val source: MutableProvider<Provider<T>>,
    override val identifier: Provider<T> = source.immediateFlatten()
) : Provider<T> by identifier {
    
    internal constructor(initial: T) : this(mutableProvider(provider(initial)))
    
    internal constructor(initial: Provider<T>) : this(mutableProvider(initial))
    
    infix fun by(provider: Provider<T>) {
        source.set(provider)
    }
    
    infix fun by(value: T) {
        source.set(mutableProvider(value))
    }
    
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