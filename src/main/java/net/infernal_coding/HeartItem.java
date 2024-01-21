package net.infernal_coding;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.infernal_coding.EventHandler.getColoredComponent;

public class HeartItem extends Item {
    public HeartItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(getColoredComponent(I18n.get("weakHardcore.heartToolTip")));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
