package xyz.xenondevs.invui.gui

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.commons.provider.Provider
import xyz.xenondevs.invui.ExperimentalReactiveApi
import xyz.xenondevs.invui.MutablePropertyAdapter
import xyz.xenondevs.invui.PropertyAdapter

@ExperimentalReactiveApi
fun TabGui.Builder.setTab(provider: MutableProvider<Int>): TabGui.Builder {
    setTab(MutablePropertyAdapter(provider))
    return this
}

@ExperimentalReactiveApi
fun TabGui.Builder.setTabs(provider: Provider<List<Gui?>>): TabGui.Builder {
    setTabs(PropertyAdapter(provider))
    return this
}