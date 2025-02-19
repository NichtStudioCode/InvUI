package xyz.xenondevs.invui.gui;

import org.bukkit.Material;
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
        Animation columnAnimation = Animation.column().build();
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
        
        assertTrue(columnAnimation.isFinished());
    }
    
    @Test
    public void testColumnOddGuiSize() {
        Gui gui = createOddTestGui();
        Animation columnAnimation = Animation.column().build();
        gui.playAnimation(columnAnimation);
        
        server.getScheduler().performTicks(3);
        assertTrue(columnAnimation.isFinished());
    }
    
    @Test
    public void testColumnNoSlots() {
        Gui gui = createTestGui();
        Animation columnAnimation = Animation.column()
            .addSlotFilter((g, s) -> false)
            .build();
        gui.playAnimation(columnAnimation);
        
        assertTrue(columnAnimation.isFinished());
        server.getScheduler().performTicks(1);
        assertTrue(columnAnimation.isFinished());
    }
    
    @Test
    public void testRow() {
        Gui gui = createTestGui();
        Animation rowAnimation = Animation.row().build();
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
        
        assertTrue(rowAnimation.isFinished());
    }
    
    @Test
    public void testRowOddGuiSize() {
        Gui gui = createOddTestGui();
        Animation rowAnimation = Animation.row().build();
        gui.playAnimation(rowAnimation);
        
        server.getScheduler().performTicks(1);
        assertTrue(rowAnimation.isFinished());
    }
    
    @Test
    public void testRowNoSlots() {
        Gui gui = createTestGui();
        Animation rowAnimiation = Animation.row()
            .addSlotFilter((g, s) -> false)
            .build();
        gui.playAnimation(rowAnimiation);
        
        assertTrue(rowAnimiation.isFinished());
        server.getScheduler().performTicks(1);
        assertTrue(rowAnimiation.isFinished());
    }
    
    @Test
    public void testHorizontalSnake() {
        Gui gui = createTestGui();
        Animation snakeAnimation = Animation.horizontalSnake().build();
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
        assertTrue(snakeAnimation.isFinished());
    }
    
    @Test
    public void testHorizontalSnakeOddGuiSize() {
        Gui gui = createOddTestGui();
        Animation snakeAnimation = Animation.horizontalSnake().build();
        gui.playAnimation(snakeAnimation);
        
        server.getScheduler().performTicks(3);
        assertTrue(snakeAnimation.isFinished());
    }
    
    @Test
    public void testHorizontalSnakeNoSlots() {
        Gui gui = createTestGui();
        Animation snakeAnimation = Animation.horizontalSnake()
            .addSlotFilter((g, s) -> false)
            .build();
        gui.playAnimation(snakeAnimation);
        
        assertTrue(snakeAnimation.isFinished());
        server.getScheduler().performTicks(1);
        assertTrue(snakeAnimation.isFinished());
    }
    
    @Test
    public void testVerticalSnake() {
        Gui gui = createTestGui();
        Animation snakeAnimation = Animation.verticalSnake().build();
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
        assertTrue(snakeAnimation.isFinished());
    }
    
    @Test
    public void testVerticalSnakeOddGuiSize() {
        Gui gui = createOddTestGui();
        Animation snakeAnimation = Animation.verticalSnake().build();
        gui.playAnimation(snakeAnimation);
        
        server.getScheduler().performTicks(3);
        assertTrue(snakeAnimation.isFinished());
    }
    
    @Test
    public void testVerticalSnakeNoSlots() {
        Gui gui = createTestGui();
        Animation snakeAnimation = Animation.verticalSnake()
            .addSlotFilter((g, s) -> false)
            .build();
        gui.playAnimation(snakeAnimation);
        
        assertTrue(snakeAnimation.isFinished());
        server.getScheduler().performTicks(1);
        assertTrue(snakeAnimation.isFinished());
    }
    
    @Test
    public void testSequential() {
        Gui gui = createTestGui();
        Animation sequentialAnimation = Animation.sequential().build();
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
        assertTrue(sequentialAnimation.isFinished());
    }
    
    @Test
    public void testSequentialOddGuiSize() {
        Gui gui = createOddTestGui();
        Animation sequentialAnimation = Animation.sequential().build();
        gui.playAnimation(sequentialAnimation);
        
        server.getScheduler().performTicks(3);
        assertTrue(sequentialAnimation.isFinished());
    }
    
    @Test
    public void testSequentialNoSlots() {
        Gui gui = createTestGui();
        Animation sequentialAnimation = Animation.sequential()
            .addSlotFilter((g, s) -> false)
            .build();
        gui.playAnimation(sequentialAnimation);
        
        assertTrue(sequentialAnimation.isFinished());
        server.getScheduler().performTicks(1);
        assertTrue(sequentialAnimation.isFinished());
    }
    
    @Test
    public void testSplitSequential() {
        Gui gui = createTestGui();
        Animation sequentialAnimation = Animation.splitSequential().build();
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
        assertTrue(sequentialAnimation.isFinished());
    }
    
    @Test
    public void testSplitSequentialOddGuiSize() {
        Gui gui = createOddTestGui();
        Animation sequentialAnimation = Animation.splitSequential().build();
        gui.playAnimation(sequentialAnimation);
        
        server.getScheduler().performTicks(2);
        assertTrue(sequentialAnimation.isFinished());
    }
    
    @Test
    public void testSplitSequentialNoSlots() {
        Gui gui = createTestGui();
        Animation sequentialAnimation = Animation.splitSequential()
            .addSlotFilter((g, s) -> false)
            .build();
        gui.playAnimation(sequentialAnimation);
        
        assertTrue(sequentialAnimation.isFinished());
        server.getScheduler().performTicks(1);
        assertTrue(sequentialAnimation.isFinished());
    }
    
    @Test
    public void testRandom() {
        Gui gui = createTestGui();
        Animation randomAnimation = Animation.random().build();
        gui.playAnimation(randomAnimation);
        
        server.getScheduler().performTicks(35);
        assertFalse(randomAnimation.isFinished());
        server.getScheduler().performTicks(1);
        assertTrue(randomAnimation.isFinished());
    }
    
    @Test
    public void testRandomOddGuiSize() {
        Gui gui = createOddTestGui();
        Animation randomAnimation = Animation.random().build();
        gui.playAnimation(randomAnimation);
        
        server.getScheduler().performTicks(3);
        assertTrue(randomAnimation.isFinished());
    }
    
    @Test
    public void testRandomNoSlots() {
        Gui gui = createTestGui();
        Animation randomAnimation = Animation.random()
            .addSlotFilter((g, s) -> false)
            .build();
        gui.playAnimation(randomAnimation);
        
        assertTrue(randomAnimation.isFinished());
        server.getScheduler().performTicks(1);
        assertTrue(randomAnimation.isFinished());
    }
    
    @Test
    public void testTickDelay() {
        Gui gui = createTestGui();
        Animation rowAnimation = Animation.row()
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
        assertTrue(rowAnimation.isFinished());
    }
    
    @Test
    public void testSlotFilter() {
        Gui gui = createTestGui();
        Animation rowAnimation = Animation.row()
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
        assertTrue(rowAnimation.isFinished());
    }
    
    @Test
    public void testShowHandler() {
        Set<Slot> slotsShown = new HashSet<>();
        
        Gui gui = createTestGui();
        Animation rowAnimation = Animation.row()
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
        assertTrue(rowAnimation.isFinished());
        assertEquals(36, slotsShown.size());
    }
    
    @Test
    public void testFinishHandler() {
        AtomicBoolean finished = new AtomicBoolean(false);
        
        Gui gui = createTestGui();
        Animation rowAnimation = Animation.row()
            .addFinishHandler(animation -> finished.set(true))
            .build();
        gui.playAnimation(rowAnimation);
        
        server.getScheduler().performTicks(3);
        assertFalse(rowAnimation.isFinished());
        assertFalse(finished.get());
        
        server.getScheduler().performTicks(1);
        assertTrue(rowAnimation.isFinished());
        assertTrue(finished.get());
    }
    
    @Test
    public void testAnimationCannotBePlayedTwice() {
        Gui gui1 = createTestGui();
        Gui gui2 = createTestGui();
        
        Animation rowAnimation = Animation.row().build();
        
        gui1.playAnimation(rowAnimation);
        assertThrows(IllegalStateException.class, () -> gui2.playAnimation(rowAnimation));
        
        server.getScheduler().performTicks(4);
        assertTrue(rowAnimation.isFinished());
        assertThrows(IllegalStateException.class, () -> gui1.playAnimation(rowAnimation));
        assertThrows(IllegalStateException.class, () -> gui2.playAnimation(rowAnimation));
    }
    
    private Gui createOddTestGui() {
        Gui gui = Gui.empty(3, 1);
        gui.fill(Item.simple(new ItemStack(Material.DIAMOND)), true);
        return gui;
    }
    
    private Gui createTestGui() {
        Gui gui = Gui.empty(WIDTH, 4);
        gui.fill(Item.simple(new ItemStack(Material.DIAMOND)), true);
        return gui;
    }
    
    private boolean[][] createVisibilityMatrix(String... lines) {
        boolean[][] matrix = new boolean[lines.length][WIDTH];
        for (int y = 0; y < lines.length; y++) {
            String line = lines[y];
            if (line.length() != WIDTH)
                throw new IllegalArgumentException("Line length must be equal to width");
            
            for (int x = 0; x < WIDTH; x++) {
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
