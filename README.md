![Logo](https://i.imgur.com/bFqCsuj.png)

[![Stars](https://img.shields.io/github/stars/NichtStudioCode/InvUI?color=ffa200)](https://github.com/NichtStudioCode/InvUI/stargazers)
![GitHub issues](https://img.shields.io/github/issues/NichtStudioCode/InvUI)
![License](https://img.shields.io/github/license/NichtStudioCode/InvUI)

# InvUI

An Inventory API for Minecraft Spigot servers.  
Supports all versions from 1.14 to 1.19.

[Documentation](https://xenondevs.xyz/docs/invui/)

## Features

* Different types of inventories (Chest, Anvil, Cartography Table, Dropper...)
* Different GUI types (Normal, Paged, Tab, Scroll)
* Nested GUIs (For example use a Scroll-GUI as a tab page)
* Easily customizable (Create your own GUI types and Items)
* VirtualInventory: Store real items inside GUIs, customize maximum stack size per slot, etc.
* Easy way to add localization using the ItemProvider system and the built-in ItemBuilder
* Supports custom textures (forced resource pack system, compatible with AuthMe)
* Advanced ItemBuilder (Normal, Potion, Skull) with BaseComponent support
* Support for BaseComponents in inventory titles
* Uncloseable inventories
* GUI Animations
* GUI Builder

## Maven
```xml
<repository>
    <id>xenondevs</id>
    <url>https://repo.xenondevs.xyz/releases</url>
</repository>
```
```xml
<dependency>
    <groupId>de.studiocode.invui</groupId>
    <artifactId>InvUI</artifactId>
    <version>VERSION</version>
</dependency>
```

## Gradle
```groovy
repositories {
    maven {
        name = "xenondevs"
        url = "https://repo.xenondevs.xyz/releases"
    }
}
```
```groovy
dependencies {
    implementation 'de.studiocode.invui:InvUI:VERSION'
}
```

Check out the [InvUI documentation](https://xenondevs.xyz/docs/invui/) for more information.

## Examples

![1](https://i.imgur.com/uaqjHSS.gif)
![2](https://i.imgur.com/rvE7VK5.gif)
