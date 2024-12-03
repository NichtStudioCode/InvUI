package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.combinedProvider
import xyz.xenondevs.commons.provider.map
import xyz.xenondevs.invui.ExperimentalReactiveApi

@ExperimentalReactiveApi
private fun <C : Any> PagedGui.Builder<C>.setReactiveContent(
    provider: Provider<List<C>>
): PagedGui.Builder<C> {
    setContent(provider)
    addModifier { gui -> provider.observeWeak(gui) { weakGui -> weakGui.bake() } }
    return this
}

@ExperimentalReactiveApi
fun <CT : Any, A> PagedGui.Builder<CT>.setContent(
    provider: Provider<A>,
    mapValue: (A) -> List<CT>
): PagedGui.Builder<CT> = setReactiveContent(provider.map(mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B> PagedGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    mapValue: (A, B) -> List<CT>
): PagedGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C> PagedGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    mapValue: (A, B, C) -> List<CT>
): PagedGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D> PagedGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    mapValue: (A, B, C, D) -> List<CT>
): PagedGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, d, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E> PagedGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    mapValue: (A, B, C, D, E) -> List<CT>
): PagedGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, d, e, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E, F> PagedGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    mapValue: (A, B, C, D, E, F) -> List<CT>
): PagedGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, d, e, f, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E, F, G> PagedGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    mapValue: (A, B, C, D, E, F, G) -> List<CT>
): PagedGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, d, e, f, g, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E, F, G, H> PagedGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    mapValue: (A, B, C, D, E, F, G, H) -> List<CT>
): PagedGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, d, e, f, g, h, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E, F, G, H, I> PagedGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    i: Provider<I>,
    mapValue: (A, B, C, D, E, F, G, H, I) -> List<CT>
): PagedGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, d, e, f, g, h, i, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E, F, G, H, I, J> PagedGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    i: Provider<I>,
    j: Provider<J>,
    mapValue: (A, B, C, D, E, F, G, H, I, J) -> List<CT>
): PagedGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, d, e, f, g, h, i, j, mapValue))

@ExperimentalReactiveApi
private fun <C : Any> ScrollGui.Builder<C>.setReactiveContent(
    provider: Provider<List<C>>
): ScrollGui.Builder<C> {
    setContent(provider)
    addModifier { gui -> provider.observeWeak(gui) { weakGui -> weakGui.bake() } }
    return this
}

@ExperimentalReactiveApi
fun <CT : Any, A> ScrollGui.Builder<CT>.setContent(
    provider: Provider<A>,
    mapValue: (A) -> List<CT>
): ScrollGui.Builder<CT> = setReactiveContent(provider.map(mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B> ScrollGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    mapValue: (A, B) -> List<CT>
): ScrollGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C> ScrollGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    mapValue: (A, B, C) -> List<CT>
): ScrollGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D> ScrollGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    mapValue: (A, B, C, D) -> List<CT>
): ScrollGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, d, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E> ScrollGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    mapValue: (A, B, C, D, E) -> List<CT>
): ScrollGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, d, e, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E, F> ScrollGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    mapValue: (A, B, C, D, E, F) -> List<CT>
): ScrollGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, d, e, f, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E, F, G> ScrollGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    mapValue: (A, B, C, D, E, F, G) -> List<CT>
): ScrollGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, d, e, f, g, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E, F, G, H> ScrollGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    mapValue: (A, B, C, D, E, F, G, H) -> List<CT>
): ScrollGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, d, e, f, g, h, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E, F, G, H, I> ScrollGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    i: Provider<I>,
    mapValue: (A, B, C, D, E, F, G, H, I) -> List<CT>
): ScrollGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, d, e, f, g, h, i, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E, F, G, H, I, J> ScrollGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    i: Provider<I>,
    j: Provider<J>,
    mapValue: (A, B, C, D, E, F, G, H, I, J) -> List<CT>
): ScrollGui.Builder<CT> = setReactiveContent(combinedProvider(a, b, c, d, e, f, g, h, i, j, mapValue))

@ExperimentalReactiveApi
private fun <C : Gui> TabGui.Builder<C>.setReactiveTabs(
    provider: Provider<List<C?>>
): TabGui.Builder<C> {
    setTabs(provider)
    addModifier { gui -> provider.observeWeak(gui) { weakGui -> weakGui.bake() } }
    return this
}

@ExperimentalReactiveApi
fun <GUI : Gui, A> TabGui.Builder<GUI>.setTabs(
    provider: Provider<A>,
    mapValue: (A) -> List<GUI>
): TabGui.Builder<GUI> = setReactiveTabs(provider.map(mapValue))

@ExperimentalReactiveApi
fun <GUI : Gui, A, B> TabGui.Builder<GUI>.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    mapValue: (A, B) -> List<GUI>
): TabGui.Builder<GUI> = setReactiveTabs(combinedProvider(a, b, mapValue))

@ExperimentalReactiveApi
fun <GUI : Gui, A, B, C> TabGui.Builder<GUI>.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    mapValue: (A, B, C) -> List<GUI>
): TabGui.Builder<GUI> = setReactiveTabs(combinedProvider(a, b, c, mapValue))

@ExperimentalReactiveApi
fun <GUI : Gui, A, B, C, D> TabGui.Builder<GUI>.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    mapValue: (A, B, C, D) -> List<GUI>
): TabGui.Builder<GUI> = setReactiveTabs(combinedProvider(a, b, c, d, mapValue))

@ExperimentalReactiveApi
fun <GUI : Gui, A, B, C, D, E> TabGui.Builder<GUI>.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    mapValue: (A, B, C, D, E) -> List<GUI>
): TabGui.Builder<GUI> = setReactiveTabs(combinedProvider(a, b, c, d, e, mapValue))

@ExperimentalReactiveApi
fun <GUI : Gui, A, B, C, D, E, F> TabGui.Builder<GUI>.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    mapValue: (A, B, C, D, E, F) -> List<GUI>
): TabGui.Builder<GUI> = setReactiveTabs(combinedProvider(a, b, c, d, e, f, mapValue))

@ExperimentalReactiveApi
fun <GUI : Gui, A, B, C, D, E, F, G> TabGui.Builder<GUI>.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    mapValue: (A, B, C, D, E, F, G) -> List<GUI>
): TabGui.Builder<GUI> = setReactiveTabs(combinedProvider(a, b, c, d, e, f, g, mapValue))

@ExperimentalReactiveApi
fun <GUI : Gui, A, B, C, D, E, F, G, H> TabGui.Builder<GUI>.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    mapValue: (A, B, C, D, E, F, G, H) -> List<GUI>
): TabGui.Builder<GUI> = setReactiveTabs(combinedProvider(a, b, c, d, e, f, g, h, mapValue))

@ExperimentalReactiveApi
fun <GUI : Gui, A, B, C, D, E, F, G, H, I> TabGui.Builder<GUI>.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    i: Provider<I>,
    mapValue: (A, B, C, D, E, F, G, H, I) -> List<GUI>
): TabGui.Builder<GUI> = setReactiveTabs(combinedProvider(a, b, c, d, e, f, g, h, i, mapValue))

@ExperimentalReactiveApi
fun <GUI : Gui, A, B, C, D, E, F, G, H, I, J> TabGui.Builder<GUI>.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    i: Provider<I>,
    j: Provider<J>,
    mapValue: (A, B, C, D, E, F, G, H, I, J) -> List<GUI>
): TabGui.Builder<GUI> = setReactiveTabs(combinedProvider(a, b, c, d, e, f, g, h, i, j, mapValue))
