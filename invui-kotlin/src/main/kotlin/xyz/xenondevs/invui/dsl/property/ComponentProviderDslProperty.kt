package xyz.xenondevs.invui.dsl.property

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import xyz.xenondevs.commons.provider.Provider

class ComponentProviderDslProperty internal constructor() : ProviderDslProperty<Component>(Component.empty()) {
    
    infix fun by(miniMessage: String): Unit =
        by(MiniMessage.miniMessage().deserialize(miniMessage))
    
    @JvmName("by1")
    infix fun by(provider: Provider<String>): Unit =
        by(provider.map(MiniMessage.miniMessage()::deserialize))
    
}