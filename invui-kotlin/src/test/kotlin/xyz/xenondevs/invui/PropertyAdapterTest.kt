package xyz.xenondevs.invui

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.state.MutableProperty
import xyz.xenondevs.invui.state.Property
import kotlin.test.assertEquals
import kotlin.test.assertSame

class PropertyAdapterTest {
    
    @Test
    fun `mutable property adapter from mutable provider`() {
        val mutableProvider: MutableProvider<Int> = mutableProvider(0)
        val mutableProperty: MutableProperty<Int> = PropertyAdapter(mutableProvider)
     
        var providerValue = -1
        var propertyValue = -1
        mutableProvider.subscribe { providerValue = it }
        mutableProperty.observeWeak(this) { propertyValue = mutableProperty.get() }
        
        assertEquals(0, mutableProvider.get())
        assertEquals(0, mutableProperty.get())
        assertEquals(-1, providerValue)
        assertEquals(-1, propertyValue)
        
        mutableProperty.set(1)
        
        assertEquals(1, mutableProvider.get())
        assertEquals(1, mutableProperty.get())
        assertEquals(1, providerValue)
        assertEquals(1, propertyValue)
        
        mutableProvider.set(2)
        
        assertEquals(2, mutableProvider.get())
        assertEquals(2, mutableProperty.get())
        assertEquals(2, providerValue)
        assertEquals(2, propertyValue)
    }
    
    @Test
    fun `mutable property adapter from provider`() {
        val rootProvider: MutableProvider<Int> = mutableProvider(0)
        val provider: Provider<Int> = rootProvider.map { it }
        val mutableProperty: MutableProperty<Int> = PropertyAdapter(provider)
        
        var providerValue = -1
        var propertyValue = -1
        provider.subscribe { providerValue = it }
        mutableProperty.observeWeak(this) { propertyValue = mutableProperty.get() }
        
        assertEquals(0, provider.get())
        assertEquals(0, mutableProperty.get())
        assertEquals(-1, providerValue)
        assertEquals(-1, propertyValue)
        
        assertThrows<UnsupportedOperationException> { mutableProperty.set(1) }
        
        rootProvider.set(1)
        
        assertEquals(1, provider.get())
        assertEquals(1, mutableProperty.get())
        assertEquals(1, providerValue)
        assertEquals(1, propertyValue)
    }
    
    @Test
    fun `mutable property adapter to mutable provider`() {
        val mutableProvider: MutableProvider<Int> = mutableProvider(0)
        val mutableProperty: MutableProperty<Int> = PropertyAdapter(mutableProvider)
        
        assertSame(mutableProvider, mutableProperty.toProvider())
        assertSame(mutableProvider, (mutableProperty as Property<Int>).toProvider())
    }
    
    @Test
    fun `property adapter to provider`() {
        val provider: Provider<Int> = provider(0)
        val property: Property<Int> = PropertyAdapter(provider)
        
        assertSame(provider, property.toProvider())
    }
    
    @Test
    fun `default mutable property to mutable provider`() {
        val mutableProperty: MutableProperty<Int> = MutableProperty.of(0)
        val mutableProvider: MutableProvider<Int> = mutableProperty.toProvider()
        
        var providerValue = -1
        var propertyValue = -1
        mutableProvider.subscribe { providerValue = it }
        mutableProperty.observeWeak(this) { propertyValue = mutableProperty.get() }
        
        assertEquals(0, mutableProvider.get())
        assertEquals(0, mutableProperty.get())
        assertEquals(-1, providerValue)
        assertEquals(-1, propertyValue)
        
        mutableProperty.set(1)
        
        assertEquals(1, mutableProvider.get())
        assertEquals(1, mutableProperty.get())
        assertEquals(1, providerValue)
        assertEquals(1, propertyValue)
        
        mutableProvider.set(2)
        
        assertEquals(2, mutableProvider.get())
        assertEquals(2, mutableProperty.get())
        assertEquals(2, providerValue)
        assertEquals(2, propertyValue)
    }
    
    @Test
    fun `default mutable property to provider`() {
        val mutableProperty: MutableProperty<Int> = MutableProperty.of(0)
        val mutableProvider: Provider<Int> = (mutableProperty as Property<Int>).toProvider()
        
        var providerValue = -1
        var propertyValue = -1
        mutableProvider.subscribe { providerValue = it }
        mutableProperty.observeWeak(this) { propertyValue = mutableProperty.get() }
        
        assertEquals(0, mutableProvider.get())
        assertEquals(0, mutableProperty.get())
        assertEquals(-1, providerValue)
        assertEquals(-1, propertyValue)
        
        mutableProperty.set(1)
        
        assertEquals(1, mutableProvider.get())
        assertEquals(1, mutableProperty.get())
        assertEquals(1, providerValue)
        assertEquals(1, propertyValue)
    }
    
    @Test
    fun `default property to provider`() {
        val property: Property<Int> = Property.of(0)
        val provider: Provider<Int> = property.toProvider()
        
        assertEquals(0, provider.get())
        assertEquals(0, property.get())
    }
    
}