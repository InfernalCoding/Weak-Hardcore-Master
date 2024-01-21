package net.infernal_coding;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;

import static net.infernal_coding.Config.*;

@Mod.EventBusSubscriber(modid = WeakHardcore.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    //static Map<String, Float> healthMap = new HashMap<>();
    static Random random = new Random();

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {

        Player player = event.getEntity();
        Player old = event.getOriginal();
        Level world = player.level();

        //healthMap.put(player.getUUID().toString() + world.getSeed(), healthMap.getOrDefault(player.getUUID().toString() + world.getSeed(), player.getMaxHealth()) - HEALTH_DECREASE.get());
        //float health = healthMap.get(player.getUUID().toString() + world.getSeed());
        float health = (float) (old.getAttribute(Attributes.MAX_HEALTH).getBaseValue() - HEALTH_DECREASE.get());
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(health);

        if (health <= 0.0) {
            Component message = getColoredComponent(I18n.get("weakHardcore.finalDeathReminder"));
            player.displayClientMessage(message, false);
        } else {
            int num = random.nextInt(1, 16);
            String key = I18n.get("weakHardcore.respawnText" + num);
            Component message = getColoredComponent(key);
            player.displayClientMessage(message, false);
            world.playLocalSound(player.getOnPos(), SoundEvents.SOUL_ESCAPE, SoundSource.MASTER,1, 1.0F, false);

        }

    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        Level world = event.getEntity().level();

        if (event.getSource().getDirectEntity() instanceof Player player && LIFE_STEAL.get() && event.getEntity() instanceof Player) {
            float newHealth = (float) (player.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + HEALTH_INCREASE.get());
            if (newHealth <= HEALTH_CAP.get()) {
                //healthMap.put(player.getUUID().toString() + world.getSeed(), newHealth);
                player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(newHealth);
                int num = random.nextInt(1, 16);
                String key = I18n.get("weakHardcore.gainHeart" + num);
                Component message = getColoredComponent(key);
                player.displayClientMessage(message, false);
                world.playLocalSound(player.getOnPos(), SoundEvents.CHORUS_FLOWER_GROW, SoundSource.MASTER,1, 1.0F, false);
            }
        }

        if (event.getEntity() instanceof Player player && !world.isClientSide) {
            BlockPos playerLastPos = player.getOnPos();
            double x = playerLastPos.getX(), y = playerLastPos.getY(), z = playerLastPos.getZ();

            if (player.getAttribute(Attributes.MAX_HEALTH).getBaseValue() - HEALTH_DECREASE.get() <= 0.0) {
                    String playerName = player.getName().getString();
                    List<ServerPlayer> players = server.getPlayerList().getPlayers();
                    players.forEach(p -> p.sendSystemMessage(Component.translatable("weakHardcore.finalDeathChatText", playerName)));
                    ItemStack head = getPlayerHead(player.getGameProfile());
                    addLore(head, I18n.get("weakHardcore.skullLore", playerName, playerName, playerName));
                    ItemEntity entity = new ItemEntity(world, x, y, z, head);
                    world.addFreshEntity(entity);
                    world.playLocalSound(playerLastPos, SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD.get(), SoundSource.MASTER,1, 1.0F, false);
                    ghostPlayer(player);
                } else if (Math.random() < .5 && !(event.getSource().getDirectEntity() instanceof Player)) {
                    ItemStack stack = new ItemStack(WeakHardcore.REVIVAL_HEART.get());
                    ItemEntity entity = new ItemEntity(world, x, y, z, stack);
                    world.addFreshEntity(entity);
                }
            }
        }


    static void ghostPlayer(Player player) {
        ServerPlayer serverPlayer = (ServerPlayer) player;
        serverPlayer.setGameMode(GameType.SPECTATOR);
    }

    static ItemStack getPlayerHead(GameProfile profile) {

        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
        head.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), profile));

        return head;
    }

    static void addLore(ItemStack stack, String lore) {
        CompoundTag displayTag = stack.getOrCreateTagElement("display");

        ListTag loreList = new ListTag();
        lore = "{\"text\":" + "\"" + lore + "\", \"italic\": false}";
        loreList.add(StringTag.valueOf(lore));
        displayTag.put("Lore", loreList);
    }


    @SubscribeEvent
    public static void onItemUse(LivingEntityUseItemEvent.Finish event) {
        Level world = event.getEntity().level();

        if (event.getEntity() instanceof Player player) {
            if (event.getItem().isEdible()) {
                String name = BuiltInRegistries.ITEM.getKey(event.getItem().getItem()).toString();
                if (FOODS.get().contains(name)) {

                    //float newHealth = healthMap.getOrDefault(player.getUUID().toString() + world.getSeed(), player.getMaxHealth()) + HEALTH_INCREASE.get();
                    float newHealth = (float) (player.getAttribute(Attributes.MAX_HEALTH).getBaseValue() + HEALTH_INCREASE.get());
                    if (newHealth <= HEALTH_CAP.get()) {
                        //healthMap.put(player.getUUID().toString() + world.getSeed(), newHealth);
                        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(newHealth);
                        int num = random.nextInt(1, 16);
                        String key = I18n.get("weakHardcore.gainHeart" + num);
                        Component message = getColoredComponent(key);
                        player.displayClientMessage(message, false);
                        world.playLocalSound(player.getOnPos(), SoundEvents.CHORUS_FLOWER_GROW, SoundSource.MASTER,1, 1.0F, false);
                    }
                }
            }
        }
    }

    public static Component getColoredComponent(String key) {
        String[] parts = key.split("\\|");
        MutableComponent message;

        if (parts.length >= 2) {
            int color = hexadecimalToDecimal(parts[0]);
            String text = parts[1];
            message = Component.literal(text);
            message.setStyle(message.getStyle().withColor(color));
        } else message = Component.literal(key);
        return message;
    }

    static int hexadecimalToDecimal(String hexVal)
    {
        // Storing the length of the
        int len = hexVal.length();

        // Initializing base value to 1, i.e 16^0
        int base = 1;

        // Initially declaring and initializing
        // decimal value to zero
        int dec_val = 0;

        // Extracting characters as
        // digits from last character

        for (int i = len - 1; i >= 0; i--) {

            // Condition check
            // Case 1
            // If character lies in '0'-'9', converting
            // it to integral 0-9 by subtracting 48 from
            // ASCII value
            if (hexVal.charAt(i) >= '0'
                    && hexVal.charAt(i) <= '9') {
                dec_val += (hexVal.charAt(i) - 48) * base;

                // Incrementing base by power
                base = base * 16;
            }

            // Case 2
            // if case 1 is bypassed

            // Now, if character lies in 'A'-'F' ,
            // converting it to integral 10 - 15 by
            // subtracting 55 from ASCII value
            else if (hexVal.charAt(i) >= 'A'
                    && hexVal.charAt(i) <= 'F') {
                dec_val += (hexVal.charAt(i) - 55) * base;

                // Incrementing base by power
                base = base * 16;
            }
        }

        // Returning the decimal value
        return dec_val;
    }
}

