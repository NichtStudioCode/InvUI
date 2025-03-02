package xyz.xenondevs.invui.internal.menu;

import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.internal.network.PacketListener;
import xyz.xenondevs.invui.internal.util.ItemUtils2;
import xyz.xenondevs.invui.internal.util.MathUtils;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.MerchantWindow;

import java.lang.invoke.MethodHandle;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodHandles.privateLookupIn;
import static java.lang.invoke.MethodType.methodType;

/**
 * A packet-based merchant menu.
 */
public class CustomMerchantMenu extends CustomContainerMenu {
    
    private static final MethodHandle NEW_MERCHANT_OFFERS;
    private static final MethodHandle NEW_MERCHANT_OFFER;
    
    static {
        try {
            NEW_MERCHANT_OFFERS = privateLookupIn(MerchantOffers.class, lookup())
                .findConstructor(MerchantOffers.class, methodType(Void.TYPE, Collection.class));
            NEW_MERCHANT_OFFER = privateLookupIn(MerchantOffer.class, lookup())
                .findConstructor(
                    MerchantOffer.class,
                    methodType(
                        Void.TYPE,
                        ItemCost.class, // costA
                        Optional.class, // costB
                        ItemStack.class, // result 
                        int.class, // uses
                        int.class, // maxUses
                        boolean.class, // rewardExp
                        int.class, // specialPriceDiff
                        int.class, // demand
                        float.class, // priceMultiplier
                        int.class, // xp
                        boolean.class // ignoreDiscounts
                    )
                );
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    private @Nullable Consumer<Integer> tradeSelectHandler;
    
    /**
     * Creates a new custom crafter menu.
     *
     * @param player The player that will view this menu.
     */
    public CustomMerchantMenu(Player player) {
        super(MenuType.MERCHANT, player);
    }
    
    @Override
    public void open(Component title) {
        var pl = PacketListener.getInstance();
        pl.redirectIncoming(player, ServerboundSelectTradePacket.class, this::handleSelectTrade);
        
        super.open(title);
    }
    
    @Override
    public void handleClosed() {
        var pl = PacketListener.getInstance();
        pl.removeRedirect(player, ServerboundSelectTradePacket.class);
        
        super.handleClosed();
    }
    
    // fixme: removing trades can lead to client's game crashing when trying to click on removed trade
    public void sendTrades(List<? extends MerchantWindow.Trade> trades, int level, double progress, boolean restockMessage) {
        try {
            var offersList = trades.stream()
                .map(this::toMerchantOffer)
                .toList();
            var offers = (MerchantOffers) NEW_MERCHANT_OFFERS.invoke(offersList);
            PacketListener.getInstance().injectOutgoing(
                player,
                new ClientboundMerchantOffersPacket(
                    containerId,
                    offers,
                    level,
                    (int) (VillagerData.getMaxXpPerLevel(level) * progress),
                    progress >= 0,
                    restockMessage
                )
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    
    private MerchantOffer toMerchantOffer(MerchantWindow.Trade trade) {
        ItemCost costA = toItemCost(toItemStack(trade.getFirstInput()));
        Optional<ItemCost> costB = trade.getSecondInput() != null
            ? Optional.of(toItemCost(toItemStack(trade.getSecondInput())))
            : Optional.empty();
        ItemStack result = toItemStack(trade.getOutput());
        
        try {
            return (MerchantOffer) NEW_MERCHANT_OFFER.invoke(
                costA, costB, result,
                trade.isAvailable() ? 0 : 1, 1, // uses, maxUses
                false, // rewardExp
                -trade.getDiscount(), // specialPriceDiff
                0, // demand
                0f, // priceMultiplier
                0, // xp
                false // ignoreDiscounts
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    
    private ItemStack toItemStack(@Nullable Item item) {
        if (item == null)
            return CraftItemStack.unwrap(ItemUtils2.getNonEmptyPlaceholder());
        
        var itemStack = CraftItemStack.unwrap(ItemUtils2.nonEmpty(item.getItemProvider(player).get(player.locale())));
        // add random tag value to prevent client-side insertion of matching items
        itemStack.update(
            DataComponents.CUSTOM_DATA, 
            CustomData.EMPTY, 
            d -> d.update(c -> c.putInt("invui_merchant", MathUtils.RANDOM.nextInt()))
        );
        return itemStack;
    }
    
    private ItemCost toItemCost(ItemStack itemStack) {
        return new ItemCost(
            itemStack.getItemHolder(),
            itemStack.getCount(),
            DataComponentPredicate.allOf(itemStack.getComponents()),
            itemStack
        );
    }
    
    public void setTradeSelectHandler(Consumer<Integer> tradeSelectHandler) {
        this.tradeSelectHandler = tradeSelectHandler;
    }
    
    private void handleSelectTrade(ServerboundSelectTradePacket packet) {
        if (tradeSelectHandler != null)
            tradeSelectHandler.accept(packet.getItem());
    }
    
}
