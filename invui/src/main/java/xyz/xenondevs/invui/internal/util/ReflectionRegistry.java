package xyz.xenondevs.invui.internal.util;

import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static xyz.xenondevs.invui.internal.util.ReflectionUtils.*;

public class ReflectionRegistry {
    
    // Methods
    public static final Method PLAYER_ADVANCEMENTS_REGISTER_LISTENERS_METHOD = ReflectionUtils.getMethod(PlayerAdvancements.class, true, "registerListeners", ServerAdvancementManager.class);
    
    // Fields
    public static final Field SERVER_PLAYER_CONTAINER_LISTENER_FIELD = getField(ServerPlayer.class, true, "containerListener");
    
}
