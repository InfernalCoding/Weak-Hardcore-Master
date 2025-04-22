package dev.infernal_coding.weak_hardcore;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WeakHardcore.MODID)
public class WeakHardcore
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "weak_hardcore";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace

    static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, MODID);
    static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);
    public static final Holder<MobEffect> HEALTH_INCR = EFFECTS.register("health_incr",
            () -> new HealthIncrEffect(MobEffectCategory.BENEFICIAL, 13781449));

    public static final Holder<Potion> ACCRETION = POTIONS.register("accretion",
            () -> new Potion("accretion", new MobEffectInstance(HEALTH_INCR, 1, 1)));

    public static final DeferredItem<Item> REVIVAL_HEART = ITEMS.registerItem("revival_heart", a ->
            new HeartItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16)));

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public static final Holder<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("weak_hardcore", () ->
            CreativeModeTab.builder()
                    .icon(() -> new ItemStack(REVIVAL_HEART.get()))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .displayItems((parameters, output) -> output.accept(REVIVAL_HEART.get()))
                    .title(Component.translatable("weakHardcore.itemGroup"))
                    .build());

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path

    public WeakHardcore(IEventBus eventBus, ModContainer modContainer)
    {
        ITEMS.register(eventBus);
        EFFECTS.register(eventBus);
        POTIONS.register(eventBus);

        // Register the commonSetup method for modloading
        eventBus.addListener(this::commonSetup);

        CREATIVE_MODE_TABS.register(eventBus);

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void initPotions() {
        PotionBrewing.Builder builder = new PotionBrewing.Builder(FeatureFlagSet.of());
        builder.addMix(Potions.STRONG_HARMING, REVIVAL_HEART.get(), ACCRETION);
        PotionBrewing.addVanillaMixes(builder);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        initPotions();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

}
