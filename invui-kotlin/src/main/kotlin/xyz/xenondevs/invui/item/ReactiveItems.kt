@file:OptIn(ExperimentalTypeInference::class)
@file:Suppress("UNCHECKED_CAST")

package xyz.xenondevs.invui.item

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.commons.provider.combinedProvider
import xyz.xenondevs.commons.provider.map
import xyz.xenondevs.invui.ExperimentalReactiveApi
import kotlin.experimental.ExperimentalTypeInference

@OverloadResolutionByLambdaReturnType
@ExperimentalReactiveApi
private fun <S : Item.Builder<*>> S.reactiveItemProvider(
    itemProviderProvider: Provider<ItemProvider>
): S {
    addModifier { item -> itemProviderProvider.observeWeak(item) { weakItem -> weakItem.notifyWindows() } }
    return setItemProvider(itemProviderProvider) as S
}

@OverloadResolutionByLambdaReturnType
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A> S.setItemProvider(
    a: Provider<A>,
    mapValue: (A) -> ItemProvider
): S = reactiveItemProvider(a.map(mapValue))

@OverloadResolutionByLambdaReturnType
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    mapValue: (A, B) -> ItemProvider
): S = reactiveItemProvider(combinedProvider(a, b, mapValue))

@OverloadResolutionByLambdaReturnType
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    mapValue: (A, B, C) -> ItemProvider
): S = reactiveItemProvider(combinedProvider(a, b, c, mapValue))

@OverloadResolutionByLambdaReturnType
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    mapValue: (A, B, C, D) -> ItemProvider
): S = reactiveItemProvider(combinedProvider(a, b, c, d, mapValue))

@OverloadResolutionByLambdaReturnType
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    mapValue: (A, B, C, D, E) -> ItemProvider
): S = reactiveItemProvider(combinedProvider(a, b, c, d, e, mapValue))

@OverloadResolutionByLambdaReturnType
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    mapValue: (A, B, C, D, E, F) -> ItemProvider
): S = reactiveItemProvider(combinedProvider(a, b, c, d, e, f, mapValue))

@OverloadResolutionByLambdaReturnType
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    mapValue: (A, B, C, D, E, F, G) -> ItemProvider
): S = reactiveItemProvider(combinedProvider(a, b, c, d, e, f, g, mapValue))

@OverloadResolutionByLambdaReturnType
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G, H> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    mapValue: (A, B, C, D, E, F, G, H) -> ItemProvider
): S = reactiveItemProvider(combinedProvider(a, b, c, d, e, f, g, h, mapValue))

@OverloadResolutionByLambdaReturnType
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G, H, I> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    i: Provider<I>,
    mapValue: (A, B, C, D, E, F, G, H, I) -> ItemProvider
): S = reactiveItemProvider(combinedProvider(a, b, c, d, e, f, g, h, i, mapValue))

@OverloadResolutionByLambdaReturnType
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G, H, I, J> S.setItemProvider(
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
    mapValue: (A, B, C, D, E, F, G, H, I, J) -> ItemProvider
): S = reactiveItemProvider(combinedProvider(a, b, c, d, e, f, g, h, i, j, mapValue))

private fun <S : Item.Builder<*>> S.observeAndNotify(vararg providers: Provider<*>) {
    providers.forEach { provider -> addModifier { item -> provider.observeWeak(item) { weakItem -> weakItem.notifyWindows() } } }
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider2")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A> S.setItemProvider(
    a: Provider<A>,
    mapValue: (Player, A) -> ItemProvider
): S {
    observeAndNotify(a)
    return setItemProvider { player: Player -> mapValue(player, a.get()) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider2")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    mapValue: (Player, A, B) -> ItemProvider
): S {
    observeAndNotify(a, b)
    return setItemProvider { player: Player -> mapValue(player, a.get(), b.get()) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider2")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    mapValue: (Player, A, B, C) -> ItemProvider
): S {
    observeAndNotify(a, b, c)
    return setItemProvider { player: Player -> mapValue(player, a.get(), b.get(), c.get()) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider2")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    mapValue: (Player, A, B, C, D) -> ItemProvider
): S {
    observeAndNotify(a, b, c, d)
    return setItemProvider { player: Player -> mapValue(player, a.get(), b.get(), c.get(), d.get()) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider2")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    mapValue: (Player, A, B, C, D, E) -> ItemProvider
): S {
    observeAndNotify(a, b, c, d, e)
    return setItemProvider { player: Player -> mapValue(player, a.get(), b.get(), c.get(), d.get(), e.get()) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider2")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    mapValue: (Player, A, B, C, D, E, F) -> ItemProvider
): S {
    observeAndNotify(a, b, c, d, e, f)
    return setItemProvider { player: Player -> mapValue(player, a.get(), b.get(), c.get(), d.get(), e.get(), f.get()) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider2")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G, H, I, J> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    mapValue: (Player, A, B, C, D, E, F, G) -> ItemProvider
): S {
    observeAndNotify(a, b, c, d, e, f, g)
    return setItemProvider { player: Player -> mapValue(player, a.get(), b.get(), c.get(), d.get(), e.get(), f.get(), g.get()) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider2")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G, H> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    mapValue: (Player, A, B, C, D, E, F, G, H) -> ItemProvider
): S {
    observeAndNotify(a, b, c, d, e, f, g, h)
    return setItemProvider { player: Player -> mapValue(player, a.get(), b.get(), c.get(), d.get(), e.get(), f.get(), g.get(), h.get()) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider2")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G, H, I> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    i: Provider<I>,
    mapValue: (Player, A, B, C, D, E, F, G, H, I) -> ItemProvider
): S {
    observeAndNotify(a, b, c, d, e, f, g, h, i)
    return setItemProvider { player: Player -> mapValue(player, a.get(), b.get(), c.get(), d.get(), e.get(), f.get(), g.get(), h.get(), i.get()) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider2")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G, H, I, J> S.setItemProvider(
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
    mapValue: (Player, A, B, C, D, E, F, G, H, I, J) -> ItemProvider
): S {
    observeAndNotify(a, b, c, d, e, f, g, h, i, j)
    return setItemProvider { player: Player -> mapValue(player, a.get(), b.get(), c.get(), d.get(), e.get(), f.get(), g.get(), h.get(), i.get(), j.get()) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("reactiveItemProvider1")
@ExperimentalReactiveApi
private fun <S : Item.Builder<*>> S.reactiveItemProvider(
    itemStackProvider: Provider<ItemStack>
): S = reactiveItemProvider(itemStackProvider.map(::ItemWrapper))

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider1")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A> S.setItemProvider(
    a: Provider<A>,
    mapValue: (A) -> ItemStack
): S = reactiveItemProvider(a.map(mapValue))

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider1")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    mapValue: (A, B) -> ItemStack
): S = reactiveItemProvider(combinedProvider(a, b, mapValue))

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider1")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    mapValue: (A, B, C) -> ItemStack
): S = reactiveItemProvider(combinedProvider(a, b, c, mapValue))

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider1")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    mapValue: (A, B, C, D) -> ItemStack
): S = reactiveItemProvider(combinedProvider(a, b, c, d, mapValue))

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider1")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    mapValue: (A, B, C, D, E) -> ItemStack
): S = reactiveItemProvider(combinedProvider(a, b, c, d, e, mapValue))

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider1")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    mapValue: (A, B, C, D, E, F) -> ItemStack
): S = reactiveItemProvider(combinedProvider(a, b, c, d, e, f, mapValue))

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider1")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    mapValue: (A, B, C, D, E, F, G) -> ItemStack
): S = reactiveItemProvider(combinedProvider(a, b, c, d, e, f, g, mapValue))

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider1")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G, H> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    mapValue: (A, B, C, D, E, F, G, H) -> ItemStack
): S = reactiveItemProvider(combinedProvider(a, b, c, d, e, f, g, h, mapValue))

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider1")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G, H, I> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    i: Provider<I>,
    mapValue: (A, B, C, D, E, F, G, H, I) -> ItemStack
): S = reactiveItemProvider(combinedProvider(a, b, c, d, e, f, g, h, i, mapValue))

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider1")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G, H, I, J> S.setItemProvider(
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
    mapValue: (A, B, C, D, E, F, G, H, I, J) -> ItemStack
): S = reactiveItemProvider(combinedProvider(a, b, c, d, e, f, g, h, i, j, mapValue))

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider3")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A> S.setItemProvider(
    a: Provider<A>,
    mapValue: (Player, A) -> ItemStack
): S {
    observeAndNotify(a)
    return setItemProvider { player: Player -> ItemWrapper(mapValue(player, a.get())) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider3")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    mapValue: (Player, A, B) -> ItemStack
): S {
    observeAndNotify(a, b)
    return setItemProvider { player: Player -> ItemWrapper(mapValue(player, a.get(), b.get())) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider3")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    mapValue: (Player, A, B, C) -> ItemStack
): S {
    observeAndNotify(a, b, c)
    return setItemProvider { player: Player -> ItemWrapper(mapValue(player, a.get(), b.get(), c.get())) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider3")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    mapValue: (Player, A, B, C, D) -> ItemStack
): S {
    observeAndNotify(a, b, c, d)
    return setItemProvider { player: Player -> ItemWrapper(mapValue(player, a.get(), b.get(), c.get(), d.get())) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider3")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    mapValue: (Player, A, B, C, D, E) -> ItemStack
): S {
    observeAndNotify(a, b, c, d, e)
    return setItemProvider { player: Player -> ItemWrapper(mapValue(player, a.get(), b.get(), c.get(), d.get(), e.get())) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider3")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    mapValue: (Player, A, B, C, D, E, F) -> ItemStack
): S {
    observeAndNotify(a, b, c, d, e, f)
    return setItemProvider { player: Player -> ItemWrapper(mapValue(player, a.get(), b.get(), c.get(), d.get(), e.get(), f.get())) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider3")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G, H, I, J> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    mapValue: (Player, A, B, C, D, E, F, G) -> ItemStack
): S {
    observeAndNotify(a, b, c, d, e, f, g)
    return setItemProvider { player: Player -> ItemWrapper(mapValue(player, a.get(), b.get(), c.get(), d.get(), e.get(), f.get(), g.get())) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider3")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G, H> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    mapValue: (Player, A, B, C, D, E, F, G, H) -> ItemStack
): S {
    observeAndNotify(a, b, c, d, e, f, g, h)
    return setItemProvider { player: Player -> ItemWrapper(mapValue(player, a.get(), b.get(), c.get(), d.get(), e.get(), f.get(), g.get(), h.get())) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider3")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G, H, I> S.setItemProvider(
    a: Provider<A>,
    b: Provider<B>,
    c: Provider<C>,
    d: Provider<D>,
    e: Provider<E>,
    f: Provider<F>,
    g: Provider<G>,
    h: Provider<H>,
    i: Provider<I>,
    mapValue: (Player, A, B, C, D, E, F, G, H, I) -> ItemStack
): S {
    observeAndNotify(a, b, c, d, e, f, g, h, i)
    return setItemProvider { player: Player -> ItemWrapper(mapValue(player, a.get(), b.get(), c.get(), d.get(), e.get(), f.get(), g.get(), h.get(), i.get())) } as S
}

@OverloadResolutionByLambdaReturnType
@JvmName("itemProvider3")
@ExperimentalReactiveApi
fun <S : Item.Builder<*>, A, B, C, D, E, F, G, H, I, J> S.setItemProvider(
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
    mapValue: (Player, A, B, C, D, E, F, G, H, I, J) -> ItemStack
): S {
    observeAndNotify(a, b, c, d, e, f, g, h, i, j)
    return setItemProvider { player: Player -> ItemWrapper(mapValue(player, a.get(), b.get(), c.get(), d.get(), e.get(), f.get(), g.get(), h.get(), i.get(), j.get())) } as S
}