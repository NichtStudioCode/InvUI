@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mapNonNull
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.inventory.Inventory
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import xyz.xenondevs.invui.item.setItemProvider
import kotlin.math.ceil

class ItemDslProperty internal constructor() {
    
    internal var value: Item = Item.EMPTY
    
    infix fun by(item: Item) {
        this.value = item
    }
    
    infix fun by(provider: Provider<ItemProvider>): Unit =
        by(Item.builder().setItemProvider(provider).build())
    
    @JvmName("by1")
    infix fun by(provider: Provider<ItemStack>): Unit =
        by(Item.builder().setItemProvider(provider).build())
    
    infix fun by(itemProvider: ItemProvider): Unit =
        by(Item.simple(itemProvider))
    
    infix fun by(itemProvider: ItemStack): Unit =
        by(Item.simple(itemProvider))
    
}

internal data class Dimensions(val width: Int, val height: Int) {
    val size: Int
        get() = width * height
}

class GuiDslProperty internal constructor(
    private val dimensions: List<Dimensions>,
    defaultGui: Gui = Gui.empty(dimensions[0].width, dimensions[0].height),
    private val arbitraryHeight: Boolean = false,
) {
    
    internal var value: Gui = defaultGui
    
    internal constructor(
        width: Int, height: Int,
        defaultGui: Gui = Gui.empty(width, height), 
        arbitraryHeight: Boolean = false
    ) : this(listOf(Dimensions(width, height)), defaultGui, arbitraryHeight)
    
    infix fun by(gui: Gui) {
        this.value = gui
    }
    
    infix fun by(inventory: Inventory) {
        if (arbitraryHeight) {
            value = Gui.of(
                dimensions[0].width,
                ceil(inventory.size / dimensions[0].width.toDouble()).toInt(),
                inventory
            )
        } else {
            val dim = dimensions
                .filter { it.size >= inventory.size }
                .minByOrNull { it.size - inventory.size }
                ?: dimensions.first()
            value = Gui.of(dim.width, dim.height, inventory)
        }
    }
    
}

class NullableItemProviderDslProperty internal constructor() : ProviderDslProperty<ItemProvider?>(null) {
    
    infix fun by(itemStack: ItemStack?) = by(itemStack?.let(::ItemWrapper))
    @JvmName("by1")
    infix fun by(provider: Provider<ItemStack?>) = by(provider.mapNonNull(::ItemWrapper))
    
}

class ItemProviderDslProperty internal constructor() : ProviderDslProperty<ItemProvider>(ItemProvider.EMPTY) {
    
    infix fun by(itemStack: ItemStack) = by(ItemWrapper(itemStack))
    @JvmName("by1")
    infix fun by(provider: Provider<ItemStack>) = by(provider.map(::ItemWrapper))
    
}

open class ProviderDslProperty<T> internal constructor(initial: T) {
    
    internal var value: Provider<T> = mutableProvider(initial)
    
    infix fun by(provider: Provider<T>) {
        this.value = provider
    }
    
    infix fun by(value: T) {
        this.value = mutableProvider(value)
    }
    
}

class MutableProviderDslProperty<T> internal constructor(initial: T) {
    
    internal var value = mutableProvider(initial)
    
    infix fun by(provider: MutableProvider<T>) {
        this.value = provider
    }
    
    infix fun by(value: T) {
        this.value = mutableProvider(value)
    }
    
}

class MutableProvider2dArrayDslProperty<T> internal constructor(
    private val width: Int, private val height: Int,
    initial: T
) {
    
    internal var value = MutableList<MutableProvider<T>>(width * height) { mutableProvider(initial) }
    
    operator fun set(x: Int, y: Int, provider: MutableProvider<T>) {
        value[y * width + x] = provider
    }
    
    operator fun set(x: Int, y: Int, value: T) {
        this.value[y * width + x] = mutableProvider(value)
    }
    
    infix fun by(list: List<MutableProvider<T>>) {
        require(list.size == width * height) { "Size must be ${width * height}, got ${list.size}" }
        value = list.toMutableList()
    }
    
    @JvmName("by1")
    infix fun by(list: List<List<MutableProvider<T>>>) {
        require(list.size == height) { "Height must be $height, got ${list.size}" }
        require(list.all { it.size == width }) { "All rows must have size $width, got ${list.map { it.size }}" }
        
        value = list.flatMapTo(ArrayList()) { it }
    }
    
    infix fun by(array: Array<MutableProvider<T>>) {
        require(array.size == width * height) { "Size must be ${width * height}, got ${array.size}" }
        value = array.toMutableList()
    }
    
    infix fun by(array: Array<Array<MutableProvider<T>>>) {
        require(array.size == height) { "Height must be $height, got ${array.size}" }
        require(array.all { it.size == width }) { "All rows must have size $width, got ${array.map { it.size }}" }
        
        value = array.flatMapTo(ArrayList()) { it.asList() }
    }
    
}