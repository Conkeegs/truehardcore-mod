package com.conkeegs.truehardcore.registries.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.conkeegs.truehardcore.overrides.entities.CustomSmallFireball;
import com.conkeegs.truehardcore.utils.TruestLogger;
import com.conkeegs.truehardcore.utils.Utils;
import com.starfish_studios.naturalist.common.entity.Alligator;
import com.starfish_studios.naturalist.common.entity.Bear;
import com.starfish_studios.naturalist.common.entity.Boar;
import com.starfish_studios.naturalist.common.entity.Elephant;
import com.starfish_studios.naturalist.common.entity.Hippo;
import com.starfish_studios.naturalist.common.entity.Lion;
import com.starfish_studios.naturalist.common.entity.Rhino;
import com.starfish_studios.naturalist.common.entity.Snake;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import tallestegg.guardvillagers.entities.Guard;
import twilightforest.entity.boss.Lich;
import twilightforest.entity.monster.BlockChainGoblin;
import twilightforest.entity.monster.CarminiteGolem;
import twilightforest.entity.monster.DeathTome;
import twilightforest.entity.monster.FireBeetle;
import twilightforest.entity.monster.HostileWolf;
import twilightforest.entity.monster.Kobold;
import twilightforest.entity.monster.LowerGoblinKnight;
import twilightforest.entity.monster.Minotaur;
import twilightforest.entity.monster.MistWolf;

/**
 * Registry of entities that will have their properties modified upon spawning.
 */
public class EntityRegistry {
    /**
     * Singleton {@code EntityRegistry} instance.
     */
    private static EntityRegistry instance;
    /**
     * Map of entities, keyed by their description ids, so that spawn listeners can
     * tell which entities we want to modify.
     */
    private Map<String, Consumer<EntityJoinLevelEvent>> entityMap;
    /**
     * List of all the speeds a zombie can spawn with.
     */
    private static final ArrayList<Float> zombieSpeeds = new ArrayList<Float>(
            Arrays.asList(0.357F, 0.3F, 0.33F, 0.35F, 0.34F, 0.38F, 0.4F, 0.39F, 0.36F, 0.352F, 0.37F, 0.32F, 0.36F,
                    0.355F, 0.375F));
    /**
     * List of all the speeds a creeper can spawn with.
     */
    private static final ArrayList<Float> creeperSpeeds = new ArrayList<Float>(
            Arrays.asList(0.3F, 0.28F, 0.35F, 0.32F));
    private static final Logger LOGGER = TruestLogger.getLogger();
    /**
     * List of all the types of arrows we want to modify.
     */
    private static final ArrayList<String> arrowDescriptionIds = new ArrayList<String>(Arrays.asList(
            "entity.minecraft.arrow",
            "entity.minecraft.spectral_arrow",
            "entity.twilightforest.ice_arrow",
            "entity.twilightforest.seeker_arrow",
            "entity.quark.torch_arrow"));
    /**
     * List of all the types of zombies we want to modify.
     */
    private static final ArrayList<String> zombieDescriptionIds = new ArrayList<String>(
            Arrays.asList("entity.minecraft.zombie", "entity.minecraft.zombie_villager", "entity.minecraft.drowned",
                    "entity.minecraft.husk", "entity.minecraft.zombified_piglin"));
    /**
     * List of all the types of spiders we want to modify.
     */
    private static final ArrayList<String> spiderDescriptionIds = new ArrayList<String>(
            Arrays.asList("entity.minecraft.cave_spider", "entity.minecraft.spider",
                    "entity.twilightforest.carminite_broodling", "entity.twilightforest.hedge_spider"));
    /**
     * List of all the types of skeletons we want to modify.
     */
    private static final ArrayList<String> skeletonDescriptionIds = new ArrayList<String>(
            Arrays.asList("entity.minecraft.skeleton", "entity.minecraft.wither_skeleton", "entity.minecraft.stray"));
    /**
     * List of all the types of slimes we want to modify.
     */
    private static final ArrayList<String> slimeDescriptionIds = new ArrayList<String>(
            Arrays.asList("entity.minecraft.slime", "entity.twilightforest.maze_slime"));

    /**
     * Private {@code EntityRegistry} singleton constructor.
     */
    private EntityRegistry() {
        entityMap = new HashMap<>();

        for (String arrowDescriptionId : arrowDescriptionIds) {
            this.addEntity(arrowDescriptionId, (EntityJoinLevelEvent event) -> {
                ((AbstractArrow) event.getEntity()).setBaseDamage(4.5D);
            });
        }

        this.addEntity("entity.minecraft.trident", (EntityJoinLevelEvent event) -> {
            ((ThrownTrident) event.getEntity()).setBaseDamage(11.0D);
        });
        this.addEntity("entity.minecraft.evoker", (EntityJoinLevelEvent event) -> {
            Utils.modifySpeed((Evoker) event.getEntity(), 0.55F);
        });
        this.addEntity("entity.minecraft.small_fireball", (EntityJoinLevelEvent event) -> {
            SmallFireball oldEntity = (SmallFireball) event.getEntity();

            // if a blaze didn't shoot the fireball, ignore it
            if (!((oldEntity.getOwner()) instanceof Blaze blaze)) {
                return;
            }

            LivingEntity blazeTarget = blaze.getTarget();

            if (blazeTarget == null) {
                LOGGER.error("Blaze target is null");

                return;
            }

            Vec3 target = blazeTarget.getEyePosition();
            double deltaX = target.x - oldEntity.getX();
            double deltaY = target.y - oldEntity.getY();
            double deltaZ = target.z - oldEntity.getZ();
            // Calculate the distance between blaze and player
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            // Normalize the direction vector to maintain constant speed
            double velocityX = deltaX / distance;
            double velocityY = deltaY / distance;
            double velocityZ = deltaZ / distance;
            Level oldEntityLevel = oldEntity.level();

            Utils.replaceEntity(event, new CustomSmallFireball(
                    oldEntityLevel,
                    oldEntity.getX(),
                    oldEntity.getY(),
                    oldEntity.getZ(),
                    velocityX,
                    velocityY,
                    velocityZ),
                    oldEntity,
                    oldEntityLevel);
        });

        for (String zombieDescriptionId : zombieDescriptionIds) {
            this.addEntity(zombieDescriptionId, (EntityJoinLevelEvent event) -> {
                Zombie zombie = (Zombie) event.getEntity();

                Utils.modifyAttackDamage(zombie, (!(zombie instanceof ZombifiedPiglin)) ? 9.0D : 7.0D);

                // don't modify speed of baby zombies as they will become ungodly fast
                if (zombie.isBaby()) {
                    return;
                }

                Utils.modifySpeed(zombie, Utils.getRandomFromArrayList(zombieSpeeds));
            });
        }

        this.addEntity("entity.minecraft.blaze", (EntityJoinLevelEvent event) -> {
            Utils.modifyAttackDamage((Blaze) event.getEntity(), 11.0D);
        });

        for (String spiderDescString : spiderDescriptionIds) {
            this.addEntity(spiderDescString, (EntityJoinLevelEvent event) -> {
                Spider spider = (Spider) event.getEntity();

                Utils.modifyAttackDamage(spider, 8.0D);

                if (spider instanceof CaveSpider) {
                    return;
                }

                Utils.modifySpeed(spider, 0.33F);
            });
        }

        this.addEntity("entity.minecraft.creeper", (EntityJoinLevelEvent event) -> {
            Utils.modifySpeed((Creeper) event.getEntity(), Utils.getRandomFromArrayList(creeperSpeeds));
        });
        this.addEntity("entity.minecraft.elder_guardian", (EntityJoinLevelEvent event) -> {
            Utils.modifyAttackDamage((ElderGuardian) event.getEntity(), 12.0D);
        });
        this.addEntity("entity.minecraft.enderman", (EntityJoinLevelEvent event) -> {
            Utils.modifyAttackDamage((EnderMan) event.getEntity(), 9.0D);
        });
        this.addEntity("entity.minecraft.endermite", (EntityJoinLevelEvent event) -> {
            Utils.modifyAttackDamage((Endermite) event.getEntity(), 7.0D);
        });
        this.addEntity("entity.minecraft.guardian", (EntityJoinLevelEvent event) -> {
            Guardian guardian = (Guardian) event.getEntity();

            Utils.modifyAttackDamage(guardian, 10.0D);
            Utils.modifySpeed(guardian, 0.55F);
        });
        this.addEntity("entity.minecraft.hoglin", (EntityJoinLevelEvent event) -> {
            Hoglin hoglin = (Hoglin) event.getEntity();

            Utils.modifyAttackDamage(hoglin, 10.0D);
            Utils.modifySpeed(hoglin, 0.33F);
        });
        this.addEntity("entity.minecraft.panda", (EntityJoinLevelEvent event) -> {
            Panda panda = (Panda) event.getEntity();

            Utils.modifyAttackDamage(panda, 12.0D);
            Utils.modifySpeed(panda, 0.23F);
        });
        this.addEntity("entity.minecraft.piglin", (EntityJoinLevelEvent event) -> {
            Piglin piglin = (Piglin) event.getEntity();

            Utils.modifyAttackDamage(piglin, 7.0D);

            // don't modify speed of baby as they will become ungodly fast
            if (piglin.isBaby()) {
                return;
            }

            Utils.modifySpeed(piglin, Utils.getRandomFromArrayList(zombieSpeeds));
        });
        this.addEntity("entity.minecraft.piglin_brute", (EntityJoinLevelEvent event) -> {
            PiglinBrute piglinBrute = (PiglinBrute) event.getEntity();

            Utils.modifyAttackDamage(piglinBrute, 5.0D);

            // don't modify speed of baby as they will become ungodly fast
            if (piglinBrute.isBaby()) {
                return;
            }

            Utils.modifySpeed(piglinBrute, Utils.getRandomFromArrayList(zombieSpeeds));
        });
        this.addEntity("entity.minecraft.pillager", (EntityJoinLevelEvent event) -> {
            Utils.modifyAttackDamage((Pillager) event.getEntity(), 8.0D);
        });
        this.addEntity("entity.minecraft.polar_bear", (EntityJoinLevelEvent event) -> {
            PolarBear polarBear = (PolarBear) event.getEntity();

            Utils.modifyAttackDamage(polarBear, 10.0D);
            Utils.modifySpeed(polarBear, 0.3F);
        });
        this.addEntity("entity.minecraft.ravager", (EntityJoinLevelEvent event) -> {
            Ravager ravager = (Ravager) event.getEntity();

            Utils.modifyAttackDamage(ravager, 14.0D);
            Utils.modifySpeed(ravager, 0.35F);
        });
        this.addEntity("entity.minecraft.silverfish", (EntityJoinLevelEvent event) -> {
            Silverfish silverfish = (Silverfish) event.getEntity();

            Utils.modifyAttackDamage(silverfish, 4.0D);
            Utils.modifySpeed(silverfish, 0.3F);
        });
        this.addEntity("entity.minecraft.magma_cube", (EntityJoinLevelEvent event) -> {
            MagmaCube slime = (MagmaCube) event.getEntity();

            Utils.modifyAttackDamage(slime, 10.0D);
            Utils.modifySpeed(slime, 0.7F);
        });

        for (String skeletonDescriptionId : skeletonDescriptionIds) {
            this.addEntity(skeletonDescriptionId, (EntityJoinLevelEvent event) -> {
                AbstractSkeleton skeleton = (AbstractSkeleton) event.getEntity();

                Utils.modifySpeed(skeleton, 0.3F);

                if (!(skeleton instanceof WitherSkeleton)) {
                    return;
                }

                Utils.modifyAttackDamage(skeleton, 7.3D);
            });
        }

        this.addEntity("entity.minecraft.vex", (EntityJoinLevelEvent event) -> {
            Utils.modifyAttackDamage((Vex) event.getEntity(), 6.5D);
        });
        this.addEntity("entity.minecraft.witch", (EntityJoinLevelEvent event) -> {
            Utils.modifySpeed((Witch) event.getEntity(), 0.32F);
        });
        this.addEntity("entity.minecraft.vindicator", (EntityJoinLevelEvent event) -> {
            Vindicator vindicator = (Vindicator) event.getEntity();

            // Utils.modifyAttackDamage(vindicator, 6.0D);
            Utils.modifySpeed(vindicator, 0.37F);
        });
        this.addEntity("entity.minecraft.wolf", (EntityJoinLevelEvent event) -> {
            Wolf wolf = (Wolf) event.getEntity();

            Utils.modifyAttackDamage(wolf, 10.0D);
            Utils.modifySpeed(wolf, 0.35F);
        });
        this.addEntity("entity.minecraft.zoglin", (EntityJoinLevelEvent event) -> {
            Zoglin zoglin = (Zoglin) event.getEntity();

            Utils.modifyAttackDamage(zoglin, 11.0D);
            Utils.modifySpeed(zoglin, 0.35F);
        });
        this.addEntity("entity.naturalist.alligator", (EntityJoinLevelEvent event) -> {
            Alligator alligator = (Alligator) event.getEntity();

            Utils.modifyAttackDamage(alligator, 9.0D);
            Utils.modifySpeed(alligator, 0.35);
        });
        this.addEntity("entity.naturalist.bear", (EntityJoinLevelEvent event) -> {
            Bear bear = (Bear) event.getEntity();

            Utils.modifyAttackDamage(bear, 9.0D);
            Utils.modifySpeed(bear, 0.28);
        });
        this.addEntity("entity.naturalist.boar", (EntityJoinLevelEvent event) -> {
            Boar boar = (Boar) event.getEntity();

            Utils.modifyAttackDamage(boar, 7.0D);
            Utils.modifySpeed(boar, 0.28);
        });
        this.addEntity("entity.naturalist.coral_snake", (EntityJoinLevelEvent event) -> {
            Snake snake = (Snake) event.getEntity();

            Utils.modifyAttackDamage(snake, 7.5D);
            Utils.modifySpeed(snake, 0.25);
        });
        this.addEntity("entity.naturalist.rattlesnake", (EntityJoinLevelEvent event) -> {
            Snake snake = (Snake) event.getEntity();

            Utils.modifyAttackDamage(snake, 7.5D);
            Utils.modifySpeed(snake, 0.265);
        });
        this.addEntity("entity.naturalist.snake", (EntityJoinLevelEvent event) -> {
            Snake snake = (Snake) event.getEntity();

            Utils.modifyAttackDamage(snake, 6.5D);
            Utils.modifySpeed(snake, 0.23);
        });
        this.addEntity("entity.naturalist.elephant", (EntityJoinLevelEvent event) -> {
            Elephant elephant = (Elephant) event.getEntity();

            Utils.modifySpeed(elephant, 0.29);
        });
        this.addEntity("entity.naturalist.hippo", (EntityJoinLevelEvent event) -> {
            Hippo hippo = (Hippo) event.getEntity();

            Utils.modifyAttackDamage(hippo, 9.0D);
            Utils.modifySpeed(hippo, 0.27);
        });
        this.addEntity("entity.naturalist.lion", (EntityJoinLevelEvent event) -> {
            Lion lion = (Lion) event.getEntity();

            Utils.modifyAttackDamage(lion, 8.0D);
            Utils.modifySpeed(lion, 0.21);
        });
        this.addEntity("entity.naturalist.rhino", (EntityJoinLevelEvent event) -> {
            Rhino rhino = (Rhino) event.getEntity();

            Utils.modifyAttackDamage(rhino, 11.0D);
            Utils.modifySpeed(rhino, 0.25);
        });
        this.addEntity("entity.guardvillagers.guard", (EntityJoinLevelEvent event) -> {
            Guard guardVillager = (Guard) event.getEntity();

            Utils.modifyAttackDamage(guardVillager, 6.5D);
        });
        this.addEntity("entity.twilightforest.blockchain_goblin", (EntityJoinLevelEvent event) -> {
            BlockChainGoblin blockChainGoblin = (BlockChainGoblin) event.getEntity();

            Utils.modifyAttackDamage(blockChainGoblin, 10.0D);
            Utils.modifySpeed(blockChainGoblin, 0.35);
        });
        this.addEntity("entity.twilightforest.carminite_golem", (EntityJoinLevelEvent event) -> {
            CarminiteGolem carminiteGolem = (CarminiteGolem) event.getEntity();

            Utils.modifyAttackDamage(carminiteGolem, 10.0D);
            Utils.modifySpeed(carminiteGolem, 0.34);
        });
        this.addEntity("entity.twilightforest.death_tome", (EntityJoinLevelEvent event) -> {
            DeathTome deathTome = (DeathTome) event.getEntity();

            Utils.modifyAttackDamage(deathTome, 9.0D);
            Utils.modifySpeed(deathTome, 0.32);
        });
        this.addEntity("entity.twilightforest.fire_beetle", (EntityJoinLevelEvent event) -> {
            FireBeetle fireBeetle = (FireBeetle) event.getEntity();

            Utils.modifyAttackDamage(fireBeetle, 8.0D);
            Utils.modifySpeed(fireBeetle, 0.33);
        });
        this.addEntity("entity.twilightforest.hostile_wolf", (EntityJoinLevelEvent event) -> {
            HostileWolf hostileWolf = (HostileWolf) event.getEntity();

            Utils.modifyAttackDamage(hostileWolf, 9.0D);
            Utils.modifySpeed(hostileWolf, 0.35);
        });
        this.addEntity("entity.twilightforest.kobold", (EntityJoinLevelEvent event) -> {
            Kobold kobold = (Kobold) event.getEntity();

            Utils.modifySpeed(kobold, 0.4);
        });
        this.addEntity("entity.twilightforest.lich", (EntityJoinLevelEvent event) -> {
            Lich lich = (Lich) event.getEntity();

            Utils.modifyAttackDamage(lich, 5.0);
        });
        this.addEntity("entity.twilightforest.lower_goblin_knight", (EntityJoinLevelEvent event) -> {
            LowerGoblinKnight lowerGoblinKnight = (LowerGoblinKnight) event.getEntity();

            Utils.modifyAttackDamage(lowerGoblinKnight, 7.0D);
            Utils.modifySpeed(lowerGoblinKnight, 0.32);
        });

        for (String slimeDescriptionId : slimeDescriptionIds) {
            this.addEntity(slimeDescriptionId, (EntityJoinLevelEvent event) -> {
                Slime slime = (Slime) event.getEntity();

                Utils.modifyAttackDamage(slime, 8.0D);
                Utils.modifySpeed(slime, 0.6F);
            });
        }

        this.addEntity("entity.twilightforest.minotaur", (EntityJoinLevelEvent event) -> {
            Minotaur minotaur = (Minotaur) event.getEntity();

            Utils.modifyAttackDamage(minotaur, 7.0D);
            Utils.modifySpeed(minotaur, 0.32);
        });
        this.addEntity("entity.twilightforest.mist_wolf", (EntityJoinLevelEvent event) -> {
            MistWolf mistWolf = (MistWolf) event.getEntity();

        // Utils.modifyAttackDamage(lowerGoblinKnight, 8.0D);
        // Utils.modifySpeed(lowerGoblinKnight, 0.32);
        // });
    }

    /**
     * Get the singleton {@code EntityRegistry} instance.
     *
     * @return singleton {@code EntityRegistry} instance
     */
    public static EntityRegistry getInstance() {
        if (instance == null) {
            instance = new EntityRegistry();
        }

        return instance;
    }

    /**
     * Get the map of all entities to modify, keyed by description id.
     *
     * @return map of all entities to modify
     */
    public Map<String, Consumer<EntityJoinLevelEvent>> getAllEntities() {
        return entityMap;
    }

    /**
     * Add an entity to the entity map to mark it as an entity we want to modify.
     *
     * @param entityDescriptionId the unique description id to identify the entity
     * @param action              callback to execute after the entity spawns to
     *                            modify it
     */
    private void addEntity(String entityDescriptionId, Consumer<EntityJoinLevelEvent> action) {
        entityMap.put(entityDescriptionId, action);
    }
}
