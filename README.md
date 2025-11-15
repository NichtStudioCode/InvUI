![Logo](https://i.imgur.com/yim89HQ.png)

[![Stars](https://img.shields.io/github/stars/NichtStudioCode/InvUI?color=ffa200)](https://github.com/NichtStudioCode/InvUI/stargazers)
![GitHub issues](https://img.shields.io/github/issues/NichtStudioCode/InvUI)
![License](https://img.shields.io/github/license/NichtStudioCode/InvUI)

# InvUI

An Inventory GUI library for Minecraft Paper servers.  

Highlighted features:
* Supports most inventory types: Chest, Anvil, Brewer, Cartography, Crafter, Crafting, Furnace, Grindstone, Merchant, Smithing, Stonecutter
* Different Gui types: Normal, Paged, Tab, Scroll
* Gui-embeddable inventories with a powerful event system (e.g. only allow input for certain item types, customize maximum stack size per slot, etc.)
* First-class [MiniMessage](https://docs.advntr.dev/minimessage/index.html) support
* Easy localization of UI items using the built-in ItemBuilder

Check out the [InvUI Documentation](https://xenondevs.xyz/docs/invui2/) to learn more.  
If you have any questions, feel free to join the [Discord](https://discord.com/invite/EpVMXtXB2t) or ask them in [GitHub Discussions](https://github.com/NichtStudioCode/InvUI/discussions).

## Version Compatibility

Starting with v2, InvUI is no longer a multi-version library.
See the table below for an overview of which InvUI version is compatible with which Minecraft version.

| InvUI version | Minecraft version |
|---------------|-------------------|
| `2.0.x`       | `TBD`             |
| `1.46`        | `1.14.0` - `TBD`  |

## Maven

```xml
<repository>
    <id>xenondevs</id>
    <url>https://repo.xenondevs.xyz/releases</url>
</repository>
```

```xml
<dependency>
    <groupId>xyz.xenondevs.invui</groupId>
    <artifactId>invui</artifactId>
    <version>VERSION</version>
</dependency>
```

Check out the [InvUI documentation](https://xenondevs.xyz/docs/invui2/) for more information.

## Examples
_These examples are taken from the documentation linked above. To keep the code examples there short, button names are often omitted._

<p>
    <a href="https://docs.xenondevs.xyz/invui2/gui/#paged-gui"><img src="https://docs.xenondevs.xyz/invui2/assets/img/gui/paged2.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui2/gui/#scroll-gui"><img src="https://docs.xenondevs.xyz/invui2/assets/img/gui/scroll.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui2/gui/#tab-gui"><img src="https://docs.xenondevs.xyz/invui2/assets/img/gui/tab.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui2/gui/#animations"><img src="https://docs.xenondevs.xyz/invui2/assets/img/gui/animation2.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui2/inventory/#itempreupdateevent"><img src="https://docs.xenondevs.xyz/invui2/assets/img/inventory/item_pre_update_event.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui2/inventory/#inventoryclickevent"><img src="https://docs.xenondevs.xyz/invui2/assets/img/inventory/inventory_click_event.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui2/window/#anvil-window"><img src="https://docs.xenondevs.xyz/invui2/assets/img/window/anvil_search.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui2/window/#stonecutter-window"><img src="https://docs.xenondevs.xyz/invui2/assets/img/window/stonecutter_buttons_as_inventory.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui2/window/#cartography-window"><img src="https://docs.xenondevs.xyz/invui2/assets/img/window/cartography_drawing.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui2/window/#merchant-window"><img src="https://docs.xenondevs.xyz/invui2/assets/img/window/merchant_tabs.avif" width="400"></a>
</p>
