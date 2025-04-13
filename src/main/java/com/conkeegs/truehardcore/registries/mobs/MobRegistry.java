// package com.conkeegs.truehardcore.registries.mobs;

// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Random;
// import java.util.function.Consumer;

// import net.minecraftforge.event.entity.EntityJoinLevelEvent;

// public class MobRegistry {
// private static final ArrayList<Float> zombieSpeeds = new ArrayList<Float>(
// Arrays.asList(0.357F, 0.3F, 0.33F, 0.35F, 0.34F, 0.38F, 0.4F, 0.39F, 0.36F,
// 0.352F, 0.37F, 0.32F, 0.36F,
// 0.355F, 0.375F));
// private static final ArrayList<Float> creeperSpeeds = new ArrayList<Float>(
// Arrays.asList(0.3F, 0.28F, 0.35F, 0.32F));

// private static MobRegistry instance;
// private Map<String, Consumer<EntityJoinLevelEvent>> entityMap;

// private MobRegistry() {
// entityMap = new HashMap<>();

// // null speed = the mob has a random speed
// // this.addEntity(Zombie.class.getSimpleName(), null, 9.0D, zombieSpeeds);
// // this.addEntity(ZombieVillager.class.getSimpleName(), null, 9.0D,
// // zombieSpeeds);
// // this.addEntity(Blaze.class.getSimpleName(), null, 11.0D, null);
// // this.addEntity(CaveSpider.class.getSimpleName(), null, 8.0D, null);
// // this.addEntity(Spider.class.getSimpleName(), 0.33F, 8.0D, null);
// // this.addEntity(Creeper.class.getSimpleName(), null, null, creeperSpeeds);
// // this.addEntity(Drowned.class.getSimpleName(), null, 9.0D, zombieSpeeds);
// // this.addEntity(ElderGuardian.class.getSimpleName(), 0.5F, 12.0D, null);
// // this.addEntity(EnderMan.class.getSimpleName(), null, 9.0D, null);
// // this.addEntity(Endermite.class.getSimpleName(), 0.28F, 7.0D, null);
// // this.addEntity(Evoker.class.getSimpleName(), 0.55F, null, null);
// // this.addEntity(Guardian.class.getSimpleName(), 0.55F, 10.0D, null);
// // this.addEntity(Hoglin.class.getSimpleName(), 0.33F, 10.0D, null);
// // this.addEntity(Husk.class.getSimpleName(), null, 9.0D, zombieSpeeds);
// // this.addEntity(Illusioner.class.getSimpleName(), 0.55F, 6.0D, null);
// // this.addEntity(CustomMagmaCube.class.getSimpleName(), 0.6F, 10.0D, null);
// // this.addEntity(Panda.class.getSimpleName(), 0.23F, 12.0D, null);
// // this.addEntity(Piglin.class.getSimpleName(), null, 7.0D, zombieSpeeds);
// // this.addEntity(PiglinBrute.class.getSimpleName(), null, 5.0D,
// zombieSpeeds);
// // this.addEntity(Pillager.class.getSimpleName(), null, 8.0D, null);
// // this.addEntity(PolarBear.class.getSimpleName(), 0.3F, 10.0D, null);
// // this.addEntity(Ravager.class.getSimpleName(), 0.35F, 14.0D, null);
// // this.addEntity(Silverfish.class.getSimpleName(), 0.3F, 4D, null);
// // this.addEntity(CustomSlime.class.getSimpleName(), 0.6F, 8.0D, null);
// // this.addEntity(Skeleton.class.getSimpleName(), 0.3F, null, null);
// // this.addEntity(WitherSkeleton.class.getSimpleName(), 0.3F, 7.3D, null);
// // this.addEntity(Stray.class.getSimpleName(), 0.3F, null, null);
// // this.addEntity(Vex.class.getSimpleName(), null, 4.0D, null);
// // this.addEntity(Vindicator.class.getSimpleName(), 0.37F, 3.5D, null);
// // this.addEntity(Witch.class.getSimpleName(), 0.32F, null, null);
// // this.addEntity(Wolf.class.getSimpleName(), 0.35F, 10.0D, null);
// // this.addEntity(Zoglin.class.getSimpleName(), 0.35F, 11.0D, null);
// // this.addEntity(ZombifiedPiglin.class.getSimpleName(), null, 7.0D,
// // zombieSpeeds);
// }

// public static MobRegistry getInstance() {
// if (instance == null) {
// instance = new MobRegistry();
// }

// return instance;
// }

// public Map<String, MobProperties> getAllMobs() {
// return entityMap;
// }

// private void addEntity(String entityName, Float speed, Double damage,
// ArrayList<Float> randomSpeeds) {
// entityMap.put(entityName, new MobProperties(speed, damage, randomSpeeds));
// }

// public class MobProperties {
// private final Float speed;
// private final Double damage;
// private final ArrayList<Float> randomSpeeds;

// public MobProperties(Float speed, Double damage,
// ArrayList<Float> randomSpeeds) {
// this.speed = speed;
// this.damage = damage;
// this.randomSpeeds = randomSpeeds;
// }

// public Float getSpeed() {
// return speed;
// }

// public Double getDamage() {
// return damage;
// }

// public ArrayList<Float> getRandomSpeeds() {
// return randomSpeeds;
// }

// public Float getRandomSpeed() {
// return this.randomSpeeds.get(new Random().nextInt(this.randomSpeeds.size()));
// }
// }
// }
