package xyz.xenondevs.invui.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.item.Item;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class AnimationTest {
    
    private static final int WIDTH = 9;
    
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static ServerMock server;
    
    @BeforeAll
    public static void setUp() {
        server = MockBukkit.mock();
        InvUI.getInstance().setPlugin(MockBukkit.createMockPlugin());
    }
    
    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }
    
    @Test
    public void testColumn() {
        Gui gui = createTestGui();
        Animation columnAnimation = Animation.builder()
            .setSlotSelector(Animation::columnSlotSelector)
            .build();
        gui.playAnimation(columnAnimation);
        
        boolean[][] visMatrix0 = createVisibilityMatrix(
            ".........",
            ".........",
            ".........",
            "........."
        );
        assertVisibility(gui, visMatrix0);
        
        server.getScheduler().performOneTick();
        boolean[][] visMatrix1 = createVisibilityMatrix(
            "x........",
            "x........",
            "x........",
            "x........"
        );
        assertVisibility(gui, visMatrix1);
        
        server.getScheduler().performOneTick();
        boolean[][] visMatrix2 = createVisibilityMatrix(
            "xx.......",
            "xx.......",
            "xx.......",
            "xx......."
        );
        assertVisibility(gui, visMatrix2);
        
        server.getScheduler().performOneTick();
        boolean[][] visMatrix3 = createVisibilityMatrix(
            "xxx......",
            "xxx......",
            "xxx......",
            "xxx......"
        );
        assertVisibility(gui, visMatrix3);
        
        server.getScheduler().performOneTick();
        boolean[][] visMatrix4 = createVisibilityMatrix(
            "xxxx.....",
            "xxxx.....",
            "xxxx.....",
            "xxxx....."
        );
        assertVisibility(gui, visMatrix4);
        
        server.getScheduler().performOneTick();
        boolean[][] visMatrix5 = createVisibilityMatrix(
            "xxxxx....",
            "xxxxx....",
            "xxxxx....",
            "xxxxx...."
        );
        assertVisibility(gui, visMatrix5);
        
        server.getScheduler().performOneTick();
        boolean[][] visMatrix6 = createVisibilityMatrix(
            "xxxxxx...",
            "xxxxxx...",
            "xxxxxx...",
            "xxxxxx..."
        );
        assertVisibility(gui, visMatrix6);
        
        server.getScheduler().performOneTick();
        boolean[][] visMatrix7 = createVisibilityMatrix(
            "xxxxxxx..",
            "xxxxxxx..",
            "xxxxxxx..",
            "xxxxxxx.."
        );
        assertVisibility(gui, visMatrix7);
        
        server.getScheduler().performOneTick();
        boolean[][] visMatrix8 = createVisibilityMatrix(
            "xxxxxxxx.",
            "xxxxxxxx.",
            "xxxxxxxx.",
            "xxxxxxxx."
        );
        assertVisibility(gui, visMatrix8);
        
        server.getScheduler().performOneTick();
        boolean[][] visMatrix9 = createVisibilityMatrix(
            "xxxxxxxxx",
            "xxxxxxxxx",
            "xxxxxxxxx",
            "xxxxxxxxx"
        );
        assertVisibility(gui, visMatrix9);
        
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testColumnOddGuiSize() {
        Gui gui = createOddTestGui();
        Animation columnAnimation = Animation.builder()
            .setSlotSelector(Animation::columnSlotSelector)
            .build();
        gui.playAnimation(columnAnimation);
        
        server.getScheduler().performTicks(3);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testColumnNoSlots() {
        Gui gui = createTestGui();
        Animation columnAnimation = Animation.builder()
            .setSlotSelector(Animation::columnSlotSelector)
            .addSlotFilter((g, s) -> false)
            .build();
        gui.playAnimation(columnAnimation);
        
        assertFalse(gui.isAnimationRunning());
        server.getScheduler().performTicks(1);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testRow() {
        Gui gui = createTestGui();
        Animation rowAnimation = Animation.builder()
            .setSlotSelector(Animation::rowSlotSelector)
            .build();
        gui.playAnimation(rowAnimation);
        
        boolean[][] visMatrix0 = createVisibilityMatrix(
            ".........",
            ".........",
            ".........",
            "........."
        );
        assertVisibility(gui, visMatrix0);
        
        server.getScheduler().performOneTick();
        boolean[][] visMatrix1 = createVisibilityMatrix(
            "xxxxxxxxx",
            ".........",
            ".........",
            "........."
        );
        assertVisibility(gui, visMatrix1);
        
        server.getScheduler().performOneTick();
        boolean[][] visMatrix2 = createVisibilityMatrix(
            "xxxxxxxxx",
            "xxxxxxxxx",
            ".........",
            "........."
        );
        assertVisibility(gui, visMatrix2);
        
        server.getScheduler().performOneTick();
        boolean[][] visMatrix3 = createVisibilityMatrix(
            "xxxxxxxxx",
            "xxxxxxxxx",
            "xxxxxxxxx",
            "........."
        );
        assertVisibility(gui, visMatrix3);
        
        server.getScheduler().performOneTick();
        boolean[][] visMatrix4 = createVisibilityMatrix(
            "xxxxxxxxx",
            "xxxxxxxxx",
            "xxxxxxxxx",
            "xxxxxxxxx"
        );
        assertVisibility(gui, visMatrix4);
        
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testRowOddGuiSize() {
        Gui gui = createOddTestGui();
        Animation rowAnimation = Animation.builder()
            .setSlotSelector(Animation::rowSlotSelector)
            .build();
        gui.playAnimation(rowAnimation);
        
        server.getScheduler().performTicks(1);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testRowNoSlots() {
        Gui gui = createTestGui();
        Animation rowAnimiation = Animation.builder()
            .setSlotSelector(Animation::rowSlotSelector)
            .addSlotFilter((g, s) -> false)
            .build();
        gui.playAnimation(rowAnimiation);
        
        assertFalse(gui.isAnimationRunning());
        server.getScheduler().performTicks(1);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testHorizontalSnake() {
        Gui gui = createTestGui();
        Animation snakeAnimation = Animation.builder()
            .setSlotSelector(Animation::horizontalSnakeSlotSelector)
            .build();
        gui.playAnimation(snakeAnimation);
        
        boolean[][] visMatrix0 = createVisibilityMatrix(
            ".........",
            ".........",
            ".........",
            "........."
        );
        assertVisibility(gui, visMatrix0);
        
        server.getScheduler().performTicks(8);
        boolean[][] visMatrix8 = createVisibilityMatrix(
            "xxxxxxxx.",
            ".........",
            ".........",
            "........."
        );
        assertVisibility(gui, visMatrix8);
        
        server.getScheduler().performTicks(5);
        boolean[][] visMatrix13 = createVisibilityMatrix(
            "xxxxxxxxx",
            ".....xxxx",
            ".........",
            "........."
        );
        assertVisibility(gui, visMatrix13);
        
        server.getScheduler().performTicks(22);
        boolean[][] visMatrix35 = createVisibilityMatrix(
            "xxxxxxxxx",
            "xxxxxxxxx",
            "xxxxxxxxx",
            ".xxxxxxxx"
        );
        assertVisibility(gui, visMatrix35);
        
        server.getScheduler().performTicks(1);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testHorizontalSnakeOddGuiSize() {
        Gui gui = createOddTestGui();
        Animation snakeAnimation = Animation.builder()
            .setSlotSelector(Animation::horizontalSnakeSlotSelector)
            .build();
        gui.playAnimation(snakeAnimation);
        
        server.getScheduler().performTicks(3);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testHorizontalSnakeNoSlots() {
        Gui gui = createTestGui();
        Animation snakeAnimation = Animation.builder()
            .setSlotSelector(Animation::horizontalSnakeSlotSelector)
            .addSlotFilter((g, s) -> false)
            .build();
        gui.playAnimation(snakeAnimation);
        
        assertFalse(gui.isAnimationRunning());
        server.getScheduler().performTicks(1);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testVerticalSnake() {
        Gui gui = createTestGui();
        Animation snakeAnimation = Animation.builder()
            .setSlotSelector(Animation::verticalSnakeSlotSelector)
            .build();
        gui.playAnimation(snakeAnimation);
        
        boolean[][] visMatrix0 = createVisibilityMatrix(
            ".........",
            ".........",
            ".........",
            "........."
        );
        assertVisibility(gui, visMatrix0);
        
        server.getScheduler().performTicks(3);
        boolean[][] visMatrix3 = createVisibilityMatrix(
            "x........",
            "x........",
            "x........",
            "........."
        );
        assertVisibility(gui, visMatrix3);
        
        server.getScheduler().performTicks(16);
        boolean[][] visMatrix19 = createVisibilityMatrix(
            "xxxxx....",
            "xxxxx....",
            "xxxxx....",
            "xxxx....."
        );
        assertVisibility(gui, visMatrix19);
        
        server.getScheduler().performTicks(16);
        boolean[][] visMatrix35 = createVisibilityMatrix(
            "xxxxxxxxx",
            "xxxxxxxxx",
            "xxxxxxxxx",
            "xxxxxxxx."
        );
        assertVisibility(gui, visMatrix35);
        
        server.getScheduler().performTicks(1);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testVerticalSnakeOddGuiSize() {
        Gui gui = createOddTestGui();
        Animation snakeAnimation = Animation.builder()
            .setSlotSelector(Animation::verticalSnakeSlotSelector)
            .build();
        gui.playAnimation(snakeAnimation);
        
        server.getScheduler().performTicks(3);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testVerticalSnakeNoSlots() {
        Gui gui = createTestGui();
        Animation snakeAnimation = Animation.builder()
            .setSlotSelector(Animation::verticalSnakeSlotSelector)
            .addSlotFilter((g, s) -> false)
            .build();
        gui.playAnimation(snakeAnimation);
        
        assertFalse(gui.isAnimationRunning());
        server.getScheduler().performTicks(1);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testSequential() {
        Gui gui = createTestGui();
        Animation sequentialAnimation = Animation.builder()
            .setSlotSelector(Animation::sequentialSlotSelector)
            .build();
        gui.playAnimation(sequentialAnimation);
        
        boolean[][] visMatrix0 = createVisibilityMatrix(
            ".........",
            ".........",
            ".........",
            "........."
        );
        assertVisibility(gui, visMatrix0);
        
        server.getScheduler().performTicks(8);
        boolean[][] visMatrix8 = createVisibilityMatrix(
            "xxxxxxxx.",
            ".........",
            ".........",
            "........."
        );
        assertVisibility(gui, visMatrix8);
        
        server.getScheduler().performTicks(10);
        boolean[][] visMatrix18 = createVisibilityMatrix(
            "xxxxxxxxx",
            "xxxxxxxxx",
            ".........",
            "........."
        );
        assertVisibility(gui, visMatrix18);
        
        server.getScheduler().performTicks(17);
        boolean[][] visMatrix35 = createVisibilityMatrix(
            "xxxxxxxxx",
            "xxxxxxxxx",
            "xxxxxxxxx",
            "xxxxxxxx."
        );
        assertVisibility(gui, visMatrix35);
        
        server.getScheduler().performTicks(1);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testSequentialOddGuiSize() {
        Gui gui = createOddTestGui();
        Animation sequentialAnimation = Animation.builder()
            .setSlotSelector(Animation::sequentialSlotSelector)
            .build();
        gui.playAnimation(sequentialAnimation);
        
        server.getScheduler().performTicks(3);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testSequentialNoSlots() {
        Gui gui = createTestGui();
        Animation sequentialAnimation = Animation.builder()
            .setSlotSelector(Animation::sequentialSlotSelector)
            .addSlotFilter((g, s) -> false)
            .build();
        gui.playAnimation(sequentialAnimation);
        
        assertFalse(gui.isAnimationRunning());
        server.getScheduler().performTicks(1);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testSplitSequential() {
        Gui gui = createTestGui();
        Animation sequentialAnimation = Animation.builder()
            .setSlotSelector(Animation::splitSequentialSlotSelector)
            .build();
        gui.playAnimation(sequentialAnimation);
        
        boolean[][] visMatrix0 = createVisibilityMatrix(
            ".........",
            ".........",
            ".........",
            "........."
        );
        assertVisibility(gui, visMatrix0);
        
        server.getScheduler().performTicks(8);
        boolean[][] visMatrix8 = createVisibilityMatrix(
            "xxxxxxxx.",
            ".........",
            ".........",
            ".xxxxxxxx"
        );
        assertVisibility(gui, visMatrix8);
        
        server.getScheduler().performTicks(9);
        boolean[][] visMatrix17 = createVisibilityMatrix(
            "xxxxxxxxx",
            "xxxxxxxx.",
            ".xxxxxxxx",
            "xxxxxxxxx"
        );
        assertVisibility(gui, visMatrix17);
        
        server.getScheduler().performTicks(1);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testSplitSequentialOddGuiSize() {
        Gui gui = createOddTestGui();
        Animation sequentialAnimation = Animation.builder()
            .setSlotSelector(Animation::splitSequentialSlotSelector)
            .build();
        gui.playAnimation(sequentialAnimation);
        
        server.getScheduler().performTicks(2);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testSplitSequentialNoSlots() {
        Gui gui = createTestGui();
        Animation sequentialAnimation = Animation.builder()
            .setSlotSelector(Animation::splitSequentialSlotSelector)
            .addSlotFilter((g, s) -> false)
            .build();
        gui.playAnimation(sequentialAnimation);
        
        assertFalse(gui.isAnimationRunning());
        server.getScheduler().performTicks(1);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testRandom() {
        Gui gui = createTestGui();
        Animation randomAnimation = Animation.builder()
            .setSlotSelector(Animation::randomSlotSelector)
            .build();
        gui.playAnimation(randomAnimation);
        
        server.getScheduler().performTicks(35);
        assertTrue(gui.isAnimationRunning());
        server.getScheduler().performTicks(1);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testRandomOddGuiSize() {
        Gui gui = createOddTestGui();
        Animation randomAnimation = Animation.builder()
            .setSlotSelector(Animation::randomSlotSelector)
            .build();
        gui.playAnimation(randomAnimation);
        
        server.getScheduler().performTicks(3);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testRandomNoSlots() {
        Gui gui = createTestGui();
        Animation randomAnimation = Animation.builder()
            .setSlotSelector(Animation::randomSlotSelector)
            .addSlotFilter((g, s) -> false)
            .build();
        gui.playAnimation(randomAnimation);
        
        assertFalse(gui.isAnimationRunning());
        server.getScheduler().performTicks(1);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testTickDelay() {
        Gui gui = createTestGui();
        Animation rowAnimation = Animation.builder()
            .setSlotSelector(Animation::rowSlotSelector)
            .setTickDelay(2)
            .build();
        gui.playAnimation(rowAnimation);
        
        server.getScheduler().performTicks(4);
        boolean[][] visMatrix4 = createVisibilityMatrix(
            "xxxxxxxxx",
            "xxxxxxxxx",
            ".........",
            "........."
        );
        assertVisibility(gui, visMatrix4);
        
        server.getScheduler().performTicks(4);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testSlotFilter() {
        Gui gui = createTestGui();
        Animation rowAnimation = Animation.builder()
            .setSlotSelector(Animation::rowSlotSelector)
            .addSlotFilter((g, slot) -> slot.y() % 2 == 0)
            .build();
        gui.playAnimation(rowAnimation);
        
        boolean[][] visMatrix0 = createVisibilityMatrix(
            ".........",
            "xxxxxxxxx",
            ".........",
            "xxxxxxxxx"
        );
        assertVisibility(gui, visMatrix0);
        
        server.getScheduler().performTicks(2);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testNonEmptySlotFilter() {
        Gui gui = Gui.builder()
            .setStructure(
                "# # # # # # # # #",
                ". . . . . . . . ."
            )
            .addIngredient('#', ItemStack.of(Material.DIAMOND))
            .build();
        Animation sequentialAnimation = Animation.builder()
            .setSlotSelector(Animation::sequentialSlotSelector)
            .filterNonEmptySlots()
            .build();
        gui.playAnimation(sequentialAnimation);
        
        assertTrue(gui.isAnimationRunning());
        server.getScheduler().performTicks(9);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testKeySlotFilter() {
        Gui gui = Gui.builder()
            .setStructure("a a b c b a a")
            .addIngredient('a', ItemStack.of(Material.DIAMOND))
            .addIngredient('b', ItemStack.of(Material.DIAMOND_BLOCK))
            .addIngredient('c', ItemStack.of(Material.DIAMOND_ORE))
            .build();
        Animation sequentialAnimation = Animation.builder()
            .setSlotSelector(Animation::sequentialSlotSelector)
            .filterTaggedSlots('a', 'c')
            .build();
        gui.playAnimation(sequentialAnimation);
        
        boolean[][] visMatrix0 = createVisibilityMatrix("..x.x..");
        assertVisibility(gui, visMatrix0);
        
        server.getScheduler().performTicks(4);
        boolean[][] visMatrix4 = createVisibilityMatrix("xxxxxx.");
        assertVisibility(gui, visMatrix4);
        
        server.getScheduler().performTicks(2);
        boolean[][] visMatrix5 = createVisibilityMatrix("xxxxxxx");
        assertVisibility(gui, visMatrix5);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    public void testShowHandler() {
        Set<Slot> slotsShown = new HashSet<>();
        
        Gui gui = createTestGui();
        Animation rowAnimation = Animation.builder()
            .setSlotSelector(Animation::rowSlotSelector)
            .addShowHandler((animation, slots) -> slotsShown.addAll(slots))
            .build();
        gui.playAnimation(rowAnimation);
        
        server.getScheduler().performTicks(1);
        assertEquals(
            Set.of(
                new Slot(0, 0),
                new Slot(1, 0),
                new Slot(2, 0),
                new Slot(3, 0),
                new Slot(4, 0),
                new Slot(5, 0),
                new Slot(6, 0),
                new Slot(7, 0),
                new Slot(8, 0)
            ),
            slotsShown
        );
        
        server.getScheduler().performTicks(3);
        assertFalse(gui.isAnimationRunning());
        assertEquals(36, slotsShown.size());
    }
    
    @Test
    public void testFinishHandler() {
        AtomicBoolean finished = new AtomicBoolean(false);
        
        Gui gui = createTestGui();
        Animation rowAnimation = Animation.builder()
            .setSlotSelector(Animation::rowSlotSelector)
            .addFinishHandler(animation -> finished.set(true))
            .build();
        gui.playAnimation(rowAnimation);
        
        server.getScheduler().performTicks(3);
        assertTrue(gui.isAnimationRunning());
        assertFalse(finished.get());
        
        server.getScheduler().performTicks(1);
        assertFalse(gui.isAnimationRunning());
        assertTrue(finished.get());
    }
    
    @Test
    public void testIntermediaryGenerator() {
        Player player = server.addPlayer();
        
        Gui gui = createTestGui();
        Animation rowAnimation = Animation.builder()
            .setSlotSelector(Animation::rowSlotSelector)
            .setIntermediaryElementGenerator(s -> new SlotElement.Item(Item.simple(ItemStack.of(Material.STONE, s.y() * 9 + s.x() + 1))))
            .build();
        
        gui.playAnimation(rowAnimation);
        
        for (int i = 0; i < 9 * 4; i++) {
            SlotElement element = gui.getSlotElement(i);
            assertNotNull(element, "i: " + i);
            ItemStack itemStack = element.getItemStack(player);
            assertNotNull(itemStack, "i: " + i);
            assertEquals(i + 1, itemStack.getAmount(), "i: " + i);
        }
        
        server.getScheduler().performTicks(2);
        
        for (int i = 0; i < 9 * 2; i++) {
            SlotElement element = gui.getSlotElement(i);
            assertNotNull(element, "i: " + i);
            ItemStack itemStack = element.getItemStack(player);
            assertNotNull(itemStack, "i: " + i);
            assertEquals(Material.DIAMOND, itemStack.getType(), "i: " + i);
        }
        for (int i = 9 * 2; i < 9 * 4; i++) {
            SlotElement element = gui.getSlotElement(i);
            assertNotNull(element, "i: " + i);
            ItemStack itemStack = element.getItemStack(player);
            assertNotNull(itemStack, "i: " + i);
            assertEquals(i + 1, itemStack.getAmount(), "i: " + i);
        }
        
        server.getScheduler().performTicks(2);
        assertFalse(gui.isAnimationRunning());
        
        for (int i = 0; i < 9 * 4; i++) {
            SlotElement element = gui.getSlotElement(i);
            assertNotNull(element, "i: " + i);
            ItemStack itemStack = element.getItemStack(player);
            assertNotNull(itemStack, "i: " + i);
            assertEquals(Material.DIAMOND, itemStack.getType(), "i: " + i);
        }
    }
    
    @Test
    void testRunAnimationTwice() {
        Gui gui = createTestGui();
        Animation rowAnimation = Animation.builder()
            .setSlotSelector(Animation::rowSlotSelector)
            .build();
        
        gui.playAnimation(rowAnimation);
        assertTrue(gui.isAnimationRunning());
        server.getScheduler().performTicks(4);
        assertFalse(gui.isAnimationRunning());
        
        gui.playAnimation(rowAnimation);
        assertTrue(gui.isAnimationRunning());
        server.getScheduler().performTicks(4);
        assertFalse(gui.isAnimationRunning());
    }
    
    @Test
    void testAnimationInterrupted() {
        Gui gui = createTestGui();
        Animation rowAnimation = Animation.builder()
            .setSlotSelector(Animation::rowSlotSelector)
            .build();
        
        gui.playAnimation(rowAnimation);
        server.getScheduler().performOneTick();
        gui.playAnimation(rowAnimation);
        
        server.getScheduler().performTicks(3);
        boolean[][] visMatrix1 = createVisibilityMatrix(
            "xxxxxxxxx",
            "xxxxxxxxx",
            "xxxxxxxxx",
            "........."
        );
        assertVisibility(gui, visMatrix1);
        
        server.getScheduler().performOneTick();
        boolean[][] visMatrix2 = createVisibilityMatrix(
            "xxxxxxxxx",
            "xxxxxxxxx",
            "xxxxxxxxx",
            "xxxxxxxxx"
        );
        assertVisibility(gui, visMatrix2);
    }
    
    private Gui createOddTestGui() {
        Gui gui = Gui.empty(3, 1);
        gui.fill(Item.simple(ItemStack.of(Material.DIAMOND)), true);
        return gui;
    }
    
    private Gui createTestGui() {
        Gui gui = Gui.empty(WIDTH, 4);
        gui.fill(Item.simple(ItemStack.of(Material.DIAMOND)), true);
        return gui;
    }
    
    private boolean[][] createVisibilityMatrix(String... lines) {
        boolean[][] matrix = new boolean[lines.length][lines[0].length()];
        for (int y = 0; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                matrix[y][x] = line.charAt(x) == 'x';
            }
        }
        return matrix;
    }
    
    private boolean[][] createVisibilityMatrix(Gui gui) {
        boolean[][] matrix = new boolean[gui.getHeight()][gui.getWidth()];
        for (int y = 0; y < gui.getHeight(); y++) {
            for (int x = 0; x < gui.getWidth(); x++) {
                matrix[y][x] = gui.getSlotElement(x, y) != null;
            }
        }
        return matrix;
    }
    
    private void assertVisibility(Gui gui, boolean[][] expectedMatrix) {
        boolean[][] guiMatrix = createVisibilityMatrix(gui);
        assertArrayEquals(expectedMatrix, guiMatrix, () ->
            "Expected:\n" + stringify(expectedMatrix) + "\nActual:\n" + stringify(guiMatrix)
        );
    }
    
    private String stringify(boolean[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (boolean[] row : matrix) {
            for (boolean b : row) {
                sb.append(b ? 'x' : '.');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    
}
