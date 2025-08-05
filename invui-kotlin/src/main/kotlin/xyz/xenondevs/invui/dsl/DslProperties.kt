@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui.dsl

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.mapNonNull
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.commons.provider.provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.item.Item
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import xyz.xenondevs.invui.item.setItemProvider


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

class GuiDslProperty internal constructor(width: Int, height: Int) {
    
    internal var value: Gui = Gui.empty(width, height)
    
    infix fun by(gui: Gui) {
        this.value = gui
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