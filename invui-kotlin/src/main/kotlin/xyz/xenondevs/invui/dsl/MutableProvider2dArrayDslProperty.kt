package xyz.xenondevs.invui.dsl

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.mutableProvider

/**
 * A DSL property representing a 2D grid of [MutableProvider]s.
 *
 * Individual cells can be set via the indexing operator, or the entire grid can be replaced
 * using the [by] infix function with a flat or nested list/array:
 * ```
 * // set individual cells
 * slots[0, 0] = true
 * slots[2, 1] = myBooleanProvider
 *
 * // replace entire grid with a flat list
 * slots by listOf(
 *     mutableProvider(true), mutableProvider(false), mutableProvider(true),
 *     mutableProvider(false), mutableProvider(true), mutableProvider(false),
 *     mutableProvider(true), mutableProvider(false), mutableProvider(true),
 * )
 * ```
 */
@ExperimentalDslApi
class MutableProvider2dArrayDslProperty<T> internal constructor(
    private val width: Int, private val height: Int,
    initial: T
) {
    
    internal var value = MutableList<MutableProvider<T>>(width * height) { mutableProvider(initial) }
    
    /**
     * Sets the cell at ([x], [y]) to the given [MutableProvider]. The cell will
     * update reactively when the provider's value changes.
     *
     * ```
     * slots[0, 0] = myBooleanProvider
     * ```
     */
    operator fun set(x: Int, y: Int, provider: MutableProvider<T>) {
        value[y * width + x] = provider
    }
    
    /**
     * Sets the cell at ([x], [y]) to a static value.
     *
     * ```
     * slots[0, 0] = true  // enable top-left slot
     * slots[1, 1] = false // disable center slot
     * ```
     */
    operator fun set(x: Int, y: Int, value: T) {
        this.value[y * width + x] = mutableProvider(value)
    }
    
    /**
     * Replaces the entire grid with a flat list of [MutableProvider]s.
     * The list size must match `width * height`.
     *
     * ```
     * slots by listOf(
     *     mutableProvider(true), mutableProvider(false), mutableProvider(true),
     *     mutableProvider(false), mutableProvider(true), mutableProvider(false),
     *     mutableProvider(true), mutableProvider(false), mutableProvider(true),
     * )
     * ```
     */
    infix fun by(list: List<MutableProvider<T>>) {
        require(list.size == width * height) { "Size must be ${width * height}, got ${list.size}" }
        value = list.toMutableList()
    }
    
    /**
     * Replaces the entire grid with a nested list of [MutableProvider]s, where each inner list
     * represents one row. The outer list size must match the grid height, and each inner list
     * size must match the grid width.
     *
     * ```
     * slots by listOf(
     *     listOf(mutableProvider(true),  mutableProvider(false), mutableProvider(true)),
     *     listOf(mutableProvider(false), mutableProvider(true),  mutableProvider(false)),
     *     listOf(mutableProvider(true),  mutableProvider(false), mutableProvider(true)),
     * )
     * ```
     */
    @JvmName("by1")
    infix fun by(list: List<List<MutableProvider<T>>>) {
        require(list.size == height) { "Height must be $height, got ${list.size}" }
        require(list.all { it.size == width }) { "All rows must have size $width, got ${list.map { it.size }}" }
        
        value = list.flatMapTo(ArrayList()) { it }
    }
    
    /**
     * Replaces the entire grid with a flat array of [MutableProvider]s.
     * The array size must match `width * height`.
     *
     * ```
     * slots by arrayOf(
     *     mutableProvider(true), mutableProvider(false), mutableProvider(true),
     *     mutableProvider(false), mutableProvider(true), mutableProvider(false),
     *     mutableProvider(true), mutableProvider(false), mutableProvider(true),
     * )
     * ```
     */
    infix fun by(array: Array<MutableProvider<T>>) {
        require(array.size == width * height) { "Size must be ${width * height}, got ${array.size}" }
        value = array.toMutableList()
    }
    
    /**
     * Replaces the entire grid with a nested array of [MutableProvider]s, where each inner array
     * represents one row. The outer array size must match the grid height, and each inner array
     * size must match the grid width.
     *
     * ```
     * slots by arrayOf(
     *     arrayOf(mutableProvider(true),  mutableProvider(false), mutableProvider(true)),
     *     arrayOf(mutableProvider(false), mutableProvider(true),  mutableProvider(false)),
     *     arrayOf(mutableProvider(true),  mutableProvider(false), mutableProvider(true)),
     * )
     * ```
     */
    infix fun by(array: Array<Array<MutableProvider<T>>>) {
        require(array.size == height) { "Height must be $height, got ${array.size}" }
        require(array.all { it.size == width }) { "All rows must have size $width, got ${array.map { it.size }}" }
        
        value = array.flatMapTo(ArrayList()) { it.asList() }
    }
    
}