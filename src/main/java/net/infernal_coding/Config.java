package net.infernal_coding;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = WeakHardcore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();


    public static final ForgeConfigSpec.BooleanValue LIFE_STEAL = BUILDER
            .comment("Allows players to regain hearts by killing other players")
            .define("lifeSteal", false);

    public static final ForgeConfigSpec.ConfigValue<Double> HEALTH_CAP = BUILDER
            .comment("Set the biggest amount of max health that the player can reach")
            .define("maxHealth", 20D);

    public static final ForgeConfigSpec.ConfigValue<Double> INITIAL_PLAYER_HEALTH = BUILDER
            .comment("Set the initial health of a player upon joining a world for the first time")
            .define("initialHealth", 20D);

    public static final ForgeConfigSpec.BooleanValue SEPERATE_ACCRETION = BUILDER
            .comment("Define whether or not accretion potions heal health differently than food")
            .define("isDifferent", false);

    public static final ForgeConfigSpec.BooleanValue SHOW_INCREASE_MESSAGES = BUILDER
            .comment("Define whether or not health increase messages are sent in chat")
            .define("showMessages", true);

    public static final ForgeConfigSpec.ConfigValue<Double> ACCRETION_INCREASE = BUILDER
            .comment("Set how much health that the accretion potion restores")
            .define("accretionIncrease", 2.0D);

    public static final ForgeConfigSpec.ConfigValue<Double> HEALTH_INCREASE = BUILDER
            .comment("Set how much health is gained when eating specified items")
            .define("increase", 2.0D);

    public static final ForgeConfigSpec.ConfigValue<Double> HEALTH_DECREASE = BUILDER
            .comment("Set how much health is lost upon death")
            .define("decrease", 2.0D);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> FOODS = BUILDER
            .comment("A list of items that increase max health upon eating.")
            .defineListAllowEmpty("foods", List.of("minecraft:enchanted_golden_apple"), Config::validateFoodItemName);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> RESURRECTION_STARTERS = BUILDER
            .comment("A list of items that can be used to trigger a resurrection.")
            .defineListAllowEmpty("sacrifices", List.of("minecraft:enchanted_golden_apple"), Config::validateItemName);
    static final ForgeConfigSpec SPEC = BUILDER.build();

    private static boolean validateFoodItemName(final Object obj)
    {
        if (obj instanceof String string) {
            ResourceLocation name = new ResourceLocation(string);
            Optional<Item> item = Optional.ofNullable(ForgeRegistries.ITEMS.getValue(name));
            return item.map(Item::isEdible).orElse(false);
        }
        return false;
    }

    private static boolean validateItemName(final Object obj)
    {
        if (obj instanceof String string) {
            ResourceLocation name = new ResourceLocation(string);
            return ForgeRegistries.ITEMS.getValue(name) != null;
        }
        return false;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {

    }
}
