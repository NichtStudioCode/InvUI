package xyz.xenondevs.invui.window

import xyz.xenondevs.commons.provider.MutableProvider
import xyz.xenondevs.invui.ExperimentalReactiveApi

@Suppress("UNCHECKED_CAST")
@ExperimentalReactiveApi
fun AnvilWindow.Builder.addRenameHandler(handler: MutableProvider<String>): AnvilWindow.Builder =
    addRenameHandler(handler::set)