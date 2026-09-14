@file:OptIn(ExperimentalReactiveApi::class)

package xyz.xenondevs.invui

import org.junit.jupiter.api.Test
import xyz.xenondevs.commons.provider.mutableProvider
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.gui.Markers
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.gui.ScrollGui
import xyz.xenondevs.invui.gui.TabGui
import xyz.xenondevs.invui.gui.lineProvider
import xyz.xenondevs.invui.gui.pageProvider
import xyz.xenondevs.invui.gui.setLine
import xyz.xenondevs.invui.gui.setPage
import xyz.xenondevs.invui.gui.setTab
import xyz.xenondevs.invui.gui.tabProvider
import kotlin.test.assertSame

class GuiProviderIdentityTest {
    
    @Test
    fun `paged gui exposes original page provider`() {
        val page = mutableProvider(0)
        val gui = PagedGui.itemsBuilder()
            .setStructure("x")
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setPage(page)
            .build()
        
        assertSame(page, gui.pageProvider)
    }
    
    @Test
    fun `scroll gui exposes original line provider`() {
        val line = mutableProvider(0)
        val gui = ScrollGui.itemsBuilder()
            .setStructure("x")
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setLine(line)
            .build()
        
        assertSame(line, gui.lineProvider)
    }
    
    @Test
    fun `tab gui exposes original tab provider`() {
        val tab = mutableProvider(0)
        val gui = TabGui.builder()
            .setStructure("x")
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setTabs(listOf(Gui.empty(1, 1)))
            .setTab(tab)
            .build()
        
        assertSame(tab, gui.tabProvider)
    }
    
}
