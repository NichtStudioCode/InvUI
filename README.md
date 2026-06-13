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

Check out the [InvUI Documentation](https://xenondevs.xyz/docs/invui/) to learn more.  
If you have any questions, feel free to join the [Discord](https://discord.com/invite/EpVMXtXB2t) or ask them in [GitHub Discussions](https://github.com/NichtStudioCode/InvUI/discussions).

## Version Compatibility

Starting with v2, InvUI is no longer a multi-version library.
See the table below for an overview of which InvUI version is compatible with which Minecraft version.

| Minecraft version    | InvUI version     |
|----------------------|-------------------|
| `26.2`               | `2.2.x`           |
| `26.1.2`             | `2.0.0` - `2.1.x` |
| `1.14.0` - `1.21.11` | `1.49`            |

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

Check out the [InvUI documentation](https://xenondevs.xyz/docs/invui/) for more information.

## Examples
_These examples are taken from the documentation linked above. To keep the code examples there short, button names are often omitted._

<p>
    <a href="https://docs.xenondevs.xyz/invui/gui/#paged-gui"><img src="https://docs.xenondevs.xyz/invui/assets/img/gui/paged2.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui/gui/#scroll-gui"><img src="https://docs.xenondevs.xyz/invui/assets/img/gui/scroll.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui/gui/#tab-gui"><img src="https://docs.xenondevs.xyz/invui/assets/img/gui/tab.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui/gui/#animations"><img src="https://docs.xenondevs.xyz/invui/assets/img/gui/animation2.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui/inventory/#itempreupdateevent"><img src="https://docs.xenondevs.xyz/invui/assets/img/inventory/item_pre_update_event.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui/inventory/#inventoryclickevent"><img src="https://docs.xenondevs.xyz/invui/assets/img/inventory/inventory_click_event.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui/window/#anvil-window"><img src="https://docs.xenondevs.xyz/invui/assets/img/window/anvil_search.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui/window/#stonecutter-window"><img src="https://docs.xenondevs.xyz/invui/assets/img/window/stonecutter_buttons_as_inventory.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui/window/#cartography-window"><img src="https://docs.xenondevs.xyz/invui/assets/img/window/cartography_drawing.avif" width="256"></a>
    <a href="https://docs.xenondevs.xyz/invui/window/#merchant-window"><img src="https://docs.xenondevs.xyz/invui/assets/img/window/merchant_tabs.avif" width="426"></a>
    <a href="https://docs.xenondevs.xyz/invui/item/#bundle-selection"><img src="https://docs.xenondevs.xyz/invui/assets/img/item/bundleSelect.avif" width="344"></a>
</p>
