package me.dekuscrub.amoebaorigin;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.*;
import io.github.apace100.origins.registry.ModComponents;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.wallentines.midnightlib.config.ConfigSection;
import org.wallentines.midnightlib.config.FileConfig;
import java.io.File;
import java.util.*;

public class Amoebaorigin implements ModInitializer {

    private static final FileConfig config = FileConfig.findOrCreate("amoeba_origin", new File("config"), new ConfigSection().with("timer", "1500").with("minecraft:dolphin", "origins:merling").with("minecraft:phantom", "origins:phantom").with("minecraft:wither_skeleton", "amoeba_origin:wither_skeleton").with("minecraft:piglin_brute", "amoeba_origin:piglin").with("minecraft:endermite", "origins:enderian").with("minecraft:bee", "origins:elytrian").with("minecraft:cow", "amoeba_origin:cow").with("minecraft:spider", "origins:arachnid").with("minecraft:cod", "origins:merling").with("minecraft:hoglin", "amoeba_origin:pig").with("minecraft:blaze", "origins:blazeborn").with("minecraft:piglin", "amoeba_origin:piglin").with("minecraft:chicken", "origins:avian").with("minecraft:parrot", "origins:elytrian").with("minecraft:ender_dragon", "origins:elytrian").with("minecraft:salmon", "origins:merling").with("minecraft:skeleton_horse", "amoeba_origin:skeleton").with("minecraft:elder_guardian", "origins:merling").with("minecraft:strider", "amoeba_origin:strider").with("minecraft:giant", "amoeba_origin:zombie").with("minecraft:zombie_villager", "amoeba_origin:zombie").with("minecraft:guardian", "origins:merling").with("minecraft:husk", "amoeba_origin:zombie").with("minecraft:cat", "origins:feline").with("minecraft:zombie_horse", "amoeba_origin:zombie").with("minecraft:tropical_fish", "origins:merling").with("minecraft:witch", "amoeba_origin:witch").with("minecraft:wither", "amoeba_origin:wither_skeleton").with("minecraft:zoglin", "amoeba_origin:zombie").with("minecraft:creeper", "amoeba_origin:creeper").with("minecraft:stray", "amoeba_origin:skeleton").with("minecraft:pufferfish", "origins:merling").with("minecraft:shulker", "origins;shulk").with("minecraft:cave_spider", "origins:arachnid").with("minecraft:ocelot", "origins:feline").with("minecraft:vex", "origins:elytrian").with("minecraft:pig", "amoeba_origin:pig").with("minecraft:zombified_piglin", "amoeba_origin:zombie").with("minecraft:drowned", "amoeba_origin:zombie").with("minecraft:ghast", "amoeba_origin:ghast").with("minecraft:axolotl", "origins:merling").with("minecraft:mooshroom", "amoeba_origin:mooshroom").with("minecraft:glow_squid", "origins:merling").with("minecraft:enderman", "origins:enderian").with("minecraft:squid", "origins:merling"));
    private static final ConfigBuilder builder = ConfigBuilder.create().setTitle(Text.of("AmoebaOrigin Config"));
    private static final ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("General"));
    private static final ConfigCategory mob_list = builder.getOrCreateCategory(new TranslatableText("Moblist"));
    private static final ConfigEntryBuilder entryBuilder = builder.entryBuilder();
    private static int timeLimit = 150;
    private static final List<Identifier> entityList = Registry.ENTITY_TYPE.getIds().stream().toList();
    public static final List<String> badEntityList = List.of("minecraft:player", "minecraft:area_effect_cloud", "minecraft:trident", "minecraft:item_frame", "minecraft:end_crystal", "minecraft:experience_orb", "minecraft:arrow", "minecraft:hopper_minecart", "minecraft:command_block_minecart", "minecraft:small_fireball", "minecraft:painting", "minecraft:fishing_bobber", "minecraft:potion", "minecraft:furnace_minecart", "minecraft:boat", "minecraft:experience_bottle", "minecraft:bat", "minecraft:armor_stand", "minecraft:shulker_bullet", "minecraft:glow_item_frame", "minecraft:llama_spit", "minecraft:tnt", "minecraft:snowball", "minecraft:item", "minecraft:fireball", "minecraft:firework_rocket", "minecraft:egg", "minecraft:ender_pearl", "minecraft:eye_of_ender", "minecraft:falling_block", "minecraft:wither_skull", "minecraft:spectal_arrow", "minecraft:tnt_minecart", "minecraft:evoker_fangs", "minecraft:leash_knot", "minecraft:spawner_minecart", "minecraft:marker", "origins:enderian_pearl", "minecraft:minecart", "minecraft:chest_minecart", "minecraft:lightning_bolt");
    private static final List<String> valueList = new ArrayList<>();
    public static final PowerType<Power> MANIFEST = new PowerTypeReference<>(new Identifier("amoeba_origin", "manifest"));

    @Override
    public void onInitialize() {
    }

    static Screen makeConfigScreen() {

        builder.setSavingRunnable(() -> {
            for (int i = 0; i < entityList.size(); i++) {
                if (!badEntityList.contains(entityList.get(i).toString())) {
                    config.getRoot().set(entityList.get(i).toString(), valueList.get(i));
                }
            }
            config.save();
            valueList.clear();
        });
        general.addEntry(entryBuilder.startStrField(new TranslatableText("Timer"), String.valueOf(timeLimit))
                .setTooltip(new TranslatableText("amount of time an amoeban has per origin they steal"))
                .setSaveConsumer(newValue -> timeLimit = Integer.getInteger(newValue))
                .build()
        );
        for (Identifier identifier : entityList) {
            if (!badEntityList.contains(identifier.toString())) {
                String currentValue;
                if (!config.getRoot().has(identifier.toString())) {
                    currentValue = "origins:human";
                } else {
                    currentValue = config.getRoot().getString(identifier.toString());
                }
                mob_list.addEntry(entryBuilder.startStrField(new TranslatableText(identifier.toString()), currentValue)
                        .setTooltip(new TranslatableText("modid:origin"))
                        .setSaveConsumer(valueList::add)
                        .build());
            }
        }
        return builder.build();
    }

    public static void resetOrigin(ServerPlayerEntity player) {
        if (MANIFEST.isActive(player)) {
            ModComponents.ORIGIN.get(player).setOrigin(OriginLayers.getLayer(Identifier.tryParse("origins:origin")), OriginRegistry.get(Identifier.tryParse("origins:human")));
            OriginComponent.sync(player);
        }
    }

    public static void bitePlayer(ServerPlayerEntity player, Entity target) {
        OriginLayer layer = OriginLayers.getLayer(Identifier.tryParse("origins:origin"));
        OriginComponent component = ModComponents.ORIGIN.get(player);
        if (player.isPlayer()) {
            if (MANIFEST.isActive(player)) {
                if (player.getInventory().getMainHandStack() == ItemStack.EMPTY) {
                    if (Math.random() <= 0.2) {
                        Origin origin;
                        if (target.isPlayer()) {
                            origin = ModComponents.ORIGIN.get(target).getOrigin(layer);
                        } else {
                            origin = OriginRegistry.get(Identifier.tryParse(config.getRoot().getString(Registry.ENTITY_TYPE.getId(target.getType()).toString())));
                        }
                        component.setOrigin(layer, origin);
                        OriginComponent.sync(player);
                        player.addExperience(2);
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, timeLimit * 2));
                        }
                    player.addExperience(-1);
                }
            }
        }
    }
}