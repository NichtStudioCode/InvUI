package xyz.xenondevs.invui.internal;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;

class FakeStonecutterRecipe extends StonecutterRecipe {
    
    public FakeStonecutterRecipe(Ingredient ingredient, ItemStack result) {
        super("", ingredient, result);
    }
    
    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return false;
    }
    
}
