package net.infernal_coding;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import static net.infernal_coding.Config.HEALTH_CAP;
import static net.infernal_coding.Config.HEALTH_INCREASE;
import static net.infernal_coding.EventHandler.getColoredComponent;
import static net.infernal_coding.EventHandler.random;

public class HealthIncrEffect extends InstantenousMobEffect {
    protected HealthIncrEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity p_19462_, @Nullable Entity p_19463_, LivingEntity effected, int p_19465_, double p_19466_) {
        float newHealth = (float) (effected.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + HEALTH_INCREASE.get());
        if (newHealth <= HEALTH_CAP.get()) {
            effected.getAttribute(Attributes.MAX_HEALTH).setBaseValue(newHealth);

            if (effected instanceof Player player) {
                int num = random.nextInt(1, 16);
                String key = I18n.get("weakHardcore.gainHeart" + num);
                Component message = getColoredComponent(key);
                player.displayClientMessage(message, false);
                effected.level().playLocalSound(player.getOnPos(), SoundEvents.CHORUS_FLOWER_GROW, SoundSource.NEUTRAL, 0.05F, 1.0F, false);
            }
        }
    }
}
