package net.infernal_coding;

import net.minecraft.resources.ResourceLocation;
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

    public static final ForgeConfigSpec.ConfigValue<Float> HEALTH_CAP = BUILDER
            .comment("Set the biggest amount of max health that the player can reach")
            .define("maxHealth", 20f);

    public static final ForgeConfigSpec.ConfigValue<Float> HEALTH_INCREASE = BUILDER
            .comment("Set how much health is gained when eating specified items")
            .define("increase", 2.0f);

    public static final ForgeConfigSpec.ConfigValue<Float> HEALTH_DECREASE = BUILDER
            .comment("Set how much health is lost upon death")
            .define("decrease", 2.0f);

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
