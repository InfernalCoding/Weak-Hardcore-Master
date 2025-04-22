package dev.infernal_coding.weak_hardcore;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Level;

import static dev.infernal_coding.weak_hardcore.EventHandler.getColoredComponent;

public class HeartItem extends Item {
    public HeartItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(getColoredComponent(I18n.get("weakHardcore.heartToolTip")));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}