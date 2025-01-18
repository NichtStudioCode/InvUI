package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.combinedProvider
import xyz.xenondevs.commons.provider.map
import xyz.xenondevs.invui.ExperimentalReactiveApi

@ExperimentalReactiveApi
fun <C : Any> PagedGui.Builder<C>.setContent(
    provider: Provider<List<C>>
): PagedGui.Builder<C> {
    setContentSupplier(provider)
    addModifier { gui -> provider.observeWeak(gui) { weakGui -> weakGui.bake() } }
    return this
}

@ExperimentalReactiveApi
fun <CT : Any, A> PagedGui.Builder<CT>.setContent(
    provider: Provider<A>,
    mapValue: (A) -> List<CT>
): PagedGui.Builder<CT> = setContent(provider.map(mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B> PagedGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    mapValue: (A, B) -> List<CT>
): PagedGui.Builder<CT> = setContent(combinedProvider(a, b, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C> PagedGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    mapValue: (A, B, C) -> List<CT>
): PagedGui.Builder<CT> = setContent(combinedProvider(a, b, c, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D> PagedGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    mapValue: (A, B, C, D) -> List<CT>
): PagedGui.Builder<CT> = setContent(combinedProvider(a, b, c, d, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E> PagedGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    mapValue: (A, B, C, D, E) -> List<CT>
): PagedGui.Builder<CT> = setContent(combinedProvider(a, b, c, d, e, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E, F> PagedGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    mapValue: (A, B, C, D, E, F) -> List<CT>
): PagedGui.Builder<CT> = setContent(combinedProvider(a, b, c, d, e, f, mapValue))

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
): PagedGui.Builder<CT> = setContent(combinedProvider(a, b, c, d, e, f, g, mapValue))

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
): PagedGui.Builder<CT> = setContent(combinedProvider(a, b, c, d, e, f, g, h, mapValue))

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
): PagedGui.Builder<CT> = setContent(combinedProvider(a, b, c, d, e, f, g, h, i, mapValue))

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
): PagedGui.Builder<CT> = setContent(combinedProvider(a, b, c, d, e, f, g, h, i, j, mapValue))

@ExperimentalReactiveApi
fun <C : Any> ScrollGui.Builder<C>.setContent(
    provider: Provider<List<C>>
): ScrollGui.Builder<C> {
    setContentSupplier(provider)
    addModifier { gui -> provider.observeWeak(gui) { weakGui -> weakGui.bake() } }
    return this
}

@ExperimentalReactiveApi
fun <CT : Any, A> ScrollGui.Builder<CT>.setContent(
    provider: Provider<A>,
    mapValue: (A) -> List<CT>
): ScrollGui.Builder<CT> = setContent(provider.map(mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B> ScrollGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    mapValue: (A, B) -> List<CT>
): ScrollGui.Builder<CT> = setContent(combinedProvider(a, b, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C> ScrollGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    mapValue: (A, B, C) -> List<CT>
): ScrollGui.Builder<CT> = setContent(combinedProvider(a, b, c, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D> ScrollGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    mapValue: (A, B, C, D) -> List<CT>
): ScrollGui.Builder<CT> = setContent(combinedProvider(a, b, c, d, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E> ScrollGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    mapValue: (A, B, C, D, E) -> List<CT>
): ScrollGui.Builder<CT> = setContent(combinedProvider(a, b, c, d, e, mapValue))

@ExperimentalReactiveApi
fun <CT : Any, A, B, C, D, E, F> ScrollGui.Builder<CT>.setContent(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    mapValue: (A, B, C, D, E, F) -> List<CT>
): ScrollGui.Builder<CT> = setContent(combinedProvider(a, b, c, d, e, f, mapValue))

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
): ScrollGui.Builder<CT> = setContent(combinedProvider(a, b, c, d, e, f, g, mapValue))

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
): ScrollGui.Builder<CT> = setContent(combinedProvider(a, b, c, d, e, f, g, h, mapValue))

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
): ScrollGui.Builder<CT> = setContent(combinedProvider(a, b, c, d, e, f, g, h, i, mapValue))

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
): ScrollGui.Builder<CT> = setContent(combinedProvider(a, b, c, d, e, f, g, h, i, j, mapValue))

@ExperimentalReactiveApi
fun TabGui.Builder.setTabs(
    provider: Provider<List<Gui?>>
): TabGui.Builder {
    setTabsSupplier(provider)
    addModifier { gui -> provider.observeWeak(gui) { weakGui -> weakGui.bake() } }
    return this
}

@ExperimentalReactiveApi
fun <A> TabGui.Builder.setTabs(
    provider: Provider<A>,
    mapValue: (A) -> List<Gui>
): TabGui.Builder = setTabs(provider.map(mapValue))

@ExperimentalReactiveApi
fun <A, B> TabGui.Builder.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    mapValue: (A, B) -> List<Gui>
): TabGui.Builder = setTabs(combinedProvider(a, b, mapValue))

@ExperimentalReactiveApi
fun <A, B, C> TabGui.Builder.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    mapValue: (A, B, C) -> List<Gui>
): TabGui.Builder = setTabs(combinedProvider(a, b, c, mapValue))

@ExperimentalReactiveApi
fun <A, B, C, D> TabGui.Builder.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    mapValue: (A, B, C, D) -> List<Gui>
): TabGui.Builder = setTabs(combinedProvider(a, b, c, d, mapValue))

@ExperimentalReactiveApi
fun <A, B, C, D, E> TabGui.Builder.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    mapValue: (A, B, C, D, E) -> List<Gui>
): TabGui.Builder = setTabs(combinedProvider(a, b, c, d, e, mapValue))

@ExperimentalReactiveApi
fun <A, B, C, D, E, F> TabGui.Builder.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    mapValue: (A, B, C, D, E, F) -> List<Gui>
): TabGui.Builder = setTabs(combinedProvider(a, b, c, d, e, f, mapValue))

@ExperimentalReactiveApi
fun <A, B, C, D, E, F, G> TabGui.Builder.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    mapValue: (A, B, C, D, E, F, G) -> List<Gui>
): TabGui.Builder = setTabs(combinedProvider(a, b, c, d, e, f, g, mapValue))

@ExperimentalReactiveApi
fun <A, B, C, D, E, F, G, H> TabGui.Builder.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    mapValue: (A, B, C, D, E, F, G, H) -> List<Gui>
): TabGui.Builder = setTabs(combinedProvider(a, b, c, d, e, f, g, h, mapValue))

@ExperimentalReactiveApi
fun <A, B, C, D, E, F, G, H, I> TabGui.Builder.setTabs(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    i: Provider<I>,
    mapValue: (A, B, C, D, E, F, G, H, I) -> List<Gui>
): TabGui.Builder = setTabs(combinedProvider(a, b, c, d, e, f, g, h, i, mapValue))

@ExperimentalReactiveApi
fun <A, B, C, D, E, F, G, H, I, J> TabGui.Builder.setTabs(
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
    mapValue: (A, B, C, D, E, F, G, H, I, J) -> List<Gui>
): TabGui.Builder = setTabs(combinedProvider(a, b, c, d, e, f, g, h, i, j, mapValue))
