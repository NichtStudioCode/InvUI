package xyz.xenondevs.invui.window

import net.kyori.adventure.text.Component
import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.combinedProvider
import xyz.xenondevs.commons.provider.map
import xyz.xenondevs.invui.ExperimentalReactiveApi

@Suppress("UNCHECKED_CAST")
@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>> S.setTitle(provider: Provider<Component>): S {
    addModifier { window -> provider.observeWeak(window) { weakWindow -> weakWindow.updateTitle() } }
    return setTitleSupplier(provider) as S
}

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>, A> S.setTitle(
    a: Provider<A>,
    mapValue: (A) -> Component
): S = setTitle(a.map(mapValue))

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>, A, B> S.setTitle(
    a: Provider<A>,
    b: Provider<B>,
    mapValue: (A, B) -> Component
): S = setTitle(combinedProvider(a, b, mapValue))

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>, A, B, C> S.setTitle(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    mapValue: (A, B, C) -> Component
): S = setTitle(combinedProvider(a, b, c, mapValue))

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>, A, B, C, D> S.setTitle(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    mapValue: (A, B, C, D) -> Component
): S = setTitle(combinedProvider(a, b, c, d, mapValue))

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>, A, B, C, D, E> S.setTitle(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    mapValue: (A, B, C, D, E) -> Component
): S = setTitle(combinedProvider(a, b, c, d, e, mapValue))

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>, A, B, C, D, E, F> S.setTitle(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    mapValue: (A, B, C, D, E, F) -> Component
): S = setTitle(combinedProvider(a, b, c, d, e, f, mapValue))

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>, A, B, C, D, E, F, G> S.setTitle(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    mapValue: (A, B, C, D, E, F, G) -> Component
): S = setTitle(combinedProvider(a, b, c, d, e, f, g, mapValue))

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>, A, B, C, D, E, F, G, H> S.setTitle(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    mapValue: (A, B, C, D, E, F, G, H) -> Component
): S = setTitle(combinedProvider(a, b, c, d, e, f, g, h, mapValue))

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>, A, B, C, D, E, F, G, H, I> S.setTitle(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    i: Provider<I>,
    mapValue: (A, B, C, D, E, F, G, H, I) -> Component
): S = setTitle(combinedProvider(a, b, c, d, e, f, g, h, i, mapValue))

@ExperimentalReactiveApi
fun <S : Window.Builder<*, *>, A, B, C, D, E, F, G, H, I, J> S.setTitle(
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
    mapValue: (A, B, C, D, E, F, G, H, I, J) -> Component
): S = setTitle(combinedProvider(a, b, c, d, e, f, g, h, i, j, mapValue))

@Suppress("UNCHECKED_CAST")
@ExperimentalReactiveApi
fun <S : AnvilWindow.Builder> S.addRenameHandler(handler: MutableProvider<String>): S {
    return addRenameHandler(handler::set) as S
}

@ExperimentalReactiveApi
fun <S : StonecutterWindow.Builder> S.setSelectedSlot(provider: MutableProvider<Int>): S {
    addModifier { window ->
        window.selectedSlot = provider.get()
        window.addSelectedSlotChangeHandler { _, to -> provider.set(to) }
        provider.subscribeWeak(window) { weakWindow, slot -> weakWindow.selectedSlot = slot }
    }
    return this
}

@ExperimentalReactiveApi
fun <S : StonecutterWindow.Builder> S.setSelectedSlot(provider: Provider<Int>): S {
    addModifier { window ->
        window.selectedSlot = provider.get()
        provider.subscribeWeak(window) { weakWindow, slot -> weakWindow.selectedSlot = slot }
    }
    return this
}