package de.studiocode.inventoryaccess.r2.util;

import de.studiocode.inventoryaccess.abstraction.util.PlayerUtils;
import de.studiocode.inventoryaccess.util.ReflectionUtils;
import net.minecraft.server.v1_15_R1.AdvancementDataPlayer;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class PlayerUtilsImpl implements PlayerUtils {
    
    private static final Method REGISTER_LISTENERS_METHOD =
        ReflectionUtils.getMethod(AdvancementDataPlayer.class, true, "d");
    
    @Override
    public void stopAdvancementListening(Player player) {
        stopAdvancementListening(((CraftPlayer) player).getHandle());
    }
    
    @Override
    public void stopAdvancementListening(Object player) {
        ((EntityPlayer) player).getAdvancementData().a(); // stops listening
    }
    
    @Override
    public void startAdvancementListening(Player player) {
        startAdvancementListening(((CraftPlayer) player).getHandle());
    }
    
    @Override
    public void startAdvancementListening(Object player) {
        AdvancementDataPlayer advancements = ((EntityPlayer) player).getAdvancementData();
        ReflectionUtils.invokeMethod(REGISTER_LISTENERS_METHOD, advancements);
    }
    
}
