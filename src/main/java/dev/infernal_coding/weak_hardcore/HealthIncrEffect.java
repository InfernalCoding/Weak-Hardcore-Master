package dev.infernal_coding.weak_hardcore;

import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import javax.annotation.Nullable;
import javax.swing.text.html.parser.Entity;

import static dev.infernal_coding.weak_hardcore.Config.HEALTH_CAP;
import static dev.infernal_coding.weak_hardcore.Config.HEALTH_INCREASE;

public class HealthIncrEffect extends InstantenousMobEffect {
    protected HealthIncrEffect(MobEffectCategory category, int color) {
        super(category, color);
    }



    @Override
    public void applyInstantenousEffect(@org.jetbrains.annotations.Nullable net.minecraft.world.entity.Entity source, @org.jetbrains.annotations.Nullable net.minecraft.world.entity.Entity indirectSource, LivingEntity livingEntity, int amplifier, double health) {
        double increment = Config.SEPERATE_ACCRETION.get() ? Config.ACCRETION_INCREASE.get()  : HEALTH_INCREASE.get();

        float newHealth = (float) (livingEntity.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + increment);

        if (newHealth >= HEALTH_CAP.get() && livingEntity.getAttribute(Attributes.MAX_HEALTH).getBaseValue() < HEALTH_CAP.get()) newHealth = HEALTH_CAP.get().longValue();

        if (newHealth <= HEALTH_CAP.get()) {
            livingEntity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(newHealth);
        }
    }
}
