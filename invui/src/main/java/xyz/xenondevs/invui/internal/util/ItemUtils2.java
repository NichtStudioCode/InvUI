package xyz.xenondevs.invui.internal.util;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BundleContents;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemBuilder;
import xyz.xenondevs.invui.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.papermc.paper.datacomponent.item.BundleContents.bundleContents;

@SuppressWarnings("UnstableApiUsage")
public class ItemUtils2 {
    
    private static final ItemStack NON_EMPTY_PLACEHOLDER = new ItemBuilder(Material.BARRIER)
        .setName("<red>InvUI Placeholder Item</red>")
        .addLoreLines("<red>Empty slots are not supported at this position</red>")
        .build();
    
    /**
     * Returns the copy of the non-empty placeholder item, which should be used to replace empty slots.
     *
     * @return the non-empty placeholder item
     */
    public static ItemStack getNonEmptyPlaceholder() {
        return NON_EMPTY_PLACEHOLDER.clone();
    }
    
    /**
     * Returns the given item stack if it is not empty, otherwise the non-empty placeholder item.
     *
     * @param itemStack the item stack
     * @return the non-empty item stack
     */
    public static ItemStack nonEmpty(@Nullable ItemStack itemStack) {
        return ItemUtils.isEmpty(itemStack) ? getNonEmptyPlaceholder() : itemStack;
    }
    
    /**
     * Creates a new list where all intermediary empty item stacks are replaced with the non-empty placeholder item
     * and all trailing empty item stacks are removed.
     *
     * @param items the list of item stacks
     * @return the sanitized list of item stacks
     */
    public static List<ItemStack> withoutIntermediaryEmpties(List<@Nullable ItemStack> items) {
        var sanitized = new ArrayList<ItemStack>();
        
        int lastNonNull = -1;
        for (int i = 0; i < items.size(); i++) {
            var itemStack = items.get(i);
            if (itemStack != null) {
                lastNonNull = i;
            }
        }
        
        for (int i = 0; i <= lastNonNull; i++) {
            var itemStack = items.get(i);
            if (ItemUtils.isEmpty(itemStack)) {
                sanitized.add(getNonEmptyPlaceholder());
            } else {
                sanitized.add(itemStack);
            }
        }
        
        return sanitized;
    }
    
    /**
     * Checks whether the given item stack is a bundle.
     *
     * @param bundle the item stack to check
     * @return true if the item stack is a bundle, false otherwise
     */
    public static boolean isBundle(ItemStack bundle) {
        return bundle.hasData(DataComponentTypes.BUNDLE_CONTENTS);
    }
    
    
    /**
     * Adds the given target stack to the given bundle stack, updating both stacks appropriately.
     *
     * @param bundle the bundle item stack
     * @param target the target item stack
     * @return true if anything was added to the bundle, false otherwise
     */
    public static boolean tryMoveIntoBundle(ItemStack bundle, ItemStack target) {
        if (ItemUtils.isEmpty(target))
            return false;
        
        var nmsBundle = CraftItemStack.unwrap(bundle);
        var bundleContents = nmsBundle.get(DataComponents.BUNDLE_CONTENTS);
        if (bundleContents == null)
            return false;
        
        var nmsTarget = CraftItemStack.unwrap(target);
        var mutableBundleContents = new BundleContents.Mutable(bundleContents);
        int added = mutableBundleContents.tryInsert(nmsTarget);
        
        if (added != 0) {
            nmsBundle.set(DataComponents.BUNDLE_CONTENTS, mutableBundleContents.toImmutable());
            return true;
        }
        
        return false;
    }
    
    /**
     * Calculates the difference in item amounts between two bundles, matching for a given target item stack.
     *
     * @param bundleA the first bundle item stack
     * @param bundleB the second bundle item stack
     * @param target  the target item stack to look for in the bundles
     * @return the difference in item amounts between the two bundles for the given target item stack
     */
    public static int getBundleDifference(ItemStack bundleA, ItemStack bundleB, ItemStack target) {
        var contentsA = bundleA.getData(DataComponentTypes.BUNDLE_CONTENTS);
        var contentsB = bundleB.getData(DataComponentTypes.BUNDLE_CONTENTS);
        if (contentsA == null || contentsB == null)
            return 0;
        
        var countA = contentsA.contents().stream()
            .filter(is -> is.isSimilar(target))
            .mapToInt(ItemStack::getAmount)
            .sum();
        var countB = contentsB.contents().stream()
            .filter(is -> is.isSimilar(target))
            .mapToInt(ItemStack::getAmount)
            .sum();
        
        return countA - countB;
    }
    
    /**
     * Gets the maximum amount of items from the given target stack that can be added to the given bundle.
     *
     * @param bundle The bundle item stack
     * @param target The target item stack
     * @return The maximum amount of items from the target stack that can be added to the bundle
     */
    public static int getMaxAmountToAddToBundle(ItemStack bundle, ItemStack target) {
        if (ItemUtils.isEmpty(target))
            return 0;
        
        var nmsBundle = CraftItemStack.unwrap(bundle);
        var bundleContents = nmsBundle.get(DataComponents.BUNDLE_CONTENTS);
        if (bundleContents == null)
            return 0;
        
        var nmsTarget = CraftItemStack.unwrap(target);
        return Math.min(target.getAmount(), bundleContents.getMaxAmountToAdd(nmsTarget));
    }
    
    /**
     * Gets the first item stack from the bundle without removing it.
     *
     * @param bundle the bundle item stack
     * @return the first item stack in the bundle, or null if there is none
     */
    public static @Nullable ItemStack getFirstFromBundle(ItemStack bundle) {
        var bundleContents = bundle.getData(DataComponentTypes.BUNDLE_CONTENTS);
        if (bundleContents == null)
            return null;
        
        var contents = bundleContents.contents();
        return contents.isEmpty() ? null : contents.getFirst();
    }
    
    /**
     * Updates the given bundle by removing target's amount of items similar to target from the bundle.
     *
     * @param bundle the bundle item stack
     * @param target the target item stack
     * @return The remaining amount of items that were not in the bundle and could not be removed.
     */
    public static int removeFromBundle(ItemStack bundle, ItemStack target) {
        if (ItemUtils.isEmpty(bundle) || ItemUtils.isEmpty(target))
            return target.getAmount();
        
        var bundleContents = bundle.getData(DataComponentTypes.BUNDLE_CONTENTS);
        if (bundleContents == null)
            return target.getAmount();
        
        int amountLeft = target.getAmount();
        var contents = new ArrayList<>(bundleContents.contents());
        var iterator = contents.iterator();
        while (iterator.hasNext() && amountLeft > 0) {
            var itemStack = iterator.next();
            if (itemStack.isSimilar(target)) {
                int amount = itemStack.getAmount();
                if (amount > amountLeft) {
                    itemStack.setAmount(amount - amountLeft);
                    amountLeft = 0;
                } else {
                    iterator.remove();
                    amountLeft -= amount;
                }
            }
        }
        
        bundle.setData(DataComponentTypes.BUNDLE_CONTENTS, bundleContents(contents));
        return amountLeft;
    }
    
    /**
     * Removes the selected item stack from the given bundle item stack and returns it.
     *
     * @param bundle the bundle item stack
     * @return the item stack that was removed from the bundle, or null if the
     * bundle is empty or this item stack does not have the bundle contents data component
     */
    public static @Nullable ItemStack takeSelectedFromBundle(ItemStack bundle) {
        var nmsBundle = CraftItemStack.unwrap(bundle);
        var bundleContents = nmsBundle.get(DataComponents.BUNDLE_CONTENTS);
        if (bundleContents != null && !bundleContents.isEmpty()) {
            var items = bundleContents.itemCopyStream()
                .collect(Collectors.toCollection(ArrayList::new));
            int i = Math.min(Math.max(0, bundleContents.getSelectedItem()), bundleContents.size());
            var taken = items.remove(i);
            nmsBundle.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(items));
            return CraftItemStack.asCraftMirror(taken);
        }
        
        return null;
    }
    
    /**
     * Sets the selected bundle slot of the given bundle item stack.
     *
     * @param bundle     the bundle item stack
     * @param bundleSlot the selected bundle slot
     */
    public static void setSelectedBundleSlot(ItemStack bundle, int bundleSlot) {
        var nmsBundle = CraftItemStack.unwrap(bundle);
        var bundleContents = nmsBundle.get(DataComponents.BUNDLE_CONTENTS);
        if (bundleContents != null) {
            var mutableBundleContents = new BundleContents.Mutable(bundleContents);
            mutableBundleContents.toggleSelectedItem(bundleSlot);
            nmsBundle.set(DataComponents.BUNDLE_CONTENTS, mutableBundleContents.toImmutable());
        }
    }
    
}
