package xyz.xenondevs.invui.dsl.property

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.invui.dsl.ExperimentalDslApi

@ExperimentalDslApi
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