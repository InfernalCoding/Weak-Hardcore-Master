package dev.infernal_coding.weak_hardcore;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

@EventBusSubscriber(modid = WeakHardcore.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();


    public static final ModConfigSpec.BooleanValue LIFE_STEAL = BUILDER
            .comment("Allows players to regain hearts by killing other players")
            .define("lifeSteal", false);

    public static final ModConfigSpec.ConfigValue<Double> HEALTH_CAP = BUILDER
            .comment("Set the biggest amount of max health that the player can reach")
            .define("maxHealth", 20D);

    public static final ModConfigSpec.ConfigValue<Double> INITIAL_PLAYER_HEALTH = BUILDER
            .comment("Set the initial health of a player upon joining a world for the first time")
            .define("initialHealth", 20D);

    public static final ModConfigSpec.BooleanValue SEPERATE_ACCRETION = BUILDER
            .comment("Define whether or not accretion potions heal health differently than food")
            .define("isDifferent", false);

    public static final ModConfigSpec.BooleanValue SHOW_INCREASE_MESSAGES = BUILDER
            .comment("Define whether or not health increase messages are sent in chat")
            .define("showMessages", true);

    public static final ModConfigSpec.ConfigValue<Double> ACCRETION_INCREASE = BUILDER
            .comment("Set how much health that the accretion potion restores")
            .define("accretionIncrease", 2.0D);

    public static final ModConfigSpec.ConfigValue<Double> HEALTH_INCREASE = BUILDER
            .comment("Set how much health is gained when eating specified items")
            .define("increase", 2.0D);

    public static final ModConfigSpec.ConfigValue<Double> HEALTH_DECREASE = BUILDER
            .comment("Set how much health is lost upon death")
            .define("decrease", 2.0D);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> FOODS = BUILDER
            .comment("A list of items that increase max health upon eating.")
            .defineListAllowEmpty("foods", List.of("minecraft:enchanted_golden_apple"), Config::validateFoodItemName);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> RESURRECTION_STARTERS = BUILDER
            .comment("A list of items that can be used to trigger a resurrection.")
            .defineListAllowEmpty("sacrifices", List.of("minecraft:enchanted_golden_apple"), Config::validateItemName);
    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateFoodItemName(final Object obj)
    {

        if (obj instanceof String string) {
            ResourceLocation name = ResourceLocation.parse(string);
            Item item = BuiltInRegistries.ITEM.get(name);
            return item.getFoodProperties(new ItemStack(item), null) != null;
        }
        return false;
    }

    private static boolean validateItemName(final Object obj)
    {
        if (obj instanceof String string) {
            ResourceLocation name = ResourceLocation.parse(string);
            return BuiltInRegistries.ITEM.get(name) != Items.AIR;
        }
        return false;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {

    }
}
