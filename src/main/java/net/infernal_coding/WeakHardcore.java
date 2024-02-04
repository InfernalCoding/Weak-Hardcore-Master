package net.infernal_coding;

import com.mojang.logging.LogUtils;
import net.infernal_coding.network.Network;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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

    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, MODID);
    static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    public static final RegistryObject<MobEffect> HEALTH_INCR = EFFECTS.register("health_incr",
            () -> new HealthIncrEffect(MobEffectCategory.BENEFICIAL, 13781449));

    public static final RegistryObject<Potion> ACCRETION = POTIONS.register("accretion",
            () -> new Potion("accretion", new MobEffectInstance(HEALTH_INCR.get(), 1, 1)));

    public static final RegistryObject<Item> REVIVAL_HEART = ITEMS.register("revival_heart", () ->
            new HeartItem(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(16)));

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("weak_hardcore", () ->
            CreativeModeTab.builder()
                    .icon(() -> new ItemStack(REVIVAL_HEART.get()))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .displayItems((parameters, output) -> output.accept(REVIVAL_HEART.get()))
                    .title(Component.translatable("weakHardcore.itemGroup"))
                    .build());

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path

    public WeakHardcore()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(eventBus);
        EFFECTS.register(eventBus);
        POTIONS.register(eventBus);

        // Register the commonSetup method for modloading
        eventBus.addListener(this::commonSetup);

        CREATIVE_MODE_TABS.register(eventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void initPotions() {
        PotionBrewing.addMix(Potions.STRONG_HARMING, REVIVAL_HEART.get(), ACCRETION.get());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        Network.init();
        initPotions();
    }

}
