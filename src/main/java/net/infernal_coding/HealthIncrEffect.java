package net.infernal_coding;

import net.minecraft.client.player.LocalPlayer;
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
        double increment = Config.SEPERATE_ACCRETION.get() ? Config.ACCRETION_INCREASE.get()  : HEALTH_INCREASE.get();

        float newHealth = (float) (effected.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + increment);

        if (newHealth >= HEALTH_CAP.get() && effected.getAttribute(Attributes.MAX_HEALTH).getBaseValue() < HEALTH_CAP.get()) newHealth = HEALTH_CAP.get().longValue();

        if (newHealth <= HEALTH_CAP.get()) {
            effected.getAttribute(Attributes.MAX_HEALTH).setBaseValue(newHealth);
        }
    }
}
