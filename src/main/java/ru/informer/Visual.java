package ru.informer;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillagerData;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static ru.informer.MainClient.minecraftClient;

public class Visual {
    private final ScheduledExecutorService task = Executors.newScheduledThreadPool(1);
    private ScheduledFuture future;
    private final Runnable Worker = new Worker();
    public static boolean toggleVisual = true;

    public void disableVisual() {
        ClientPlayerEntity user = minecraftClient.player;
        future.cancel(true);
        if(user != null){
            Iterable<Entity> entities = user.clientWorld.getEntities();
            for (Entity ent:entities) {
                if(ent instanceof LivingEntity livingEntity){
                    livingEntity.setCustomName(Text.empty());
                }
            }
        }
    }

    public void enableVisual() {
        future = task.scheduleAtFixedRate(Worker,0,1000, TimeUnit.MILLISECONDS);
    }
    private static class Worker implements Runnable{
        private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
        @Override
        public void run() {
            ClientPlayerEntity user = minecraftClient.player;
            //mood
            if (user != null && user.getMoodPercentage() > 0.95) {
                user.sendMessage(Text.literal("MOOD!" + user.getMoodPercentage()),true);
                user.playSound(SoundEvents.ITEM_TRIDENT_HIT, SoundCategory.AMBIENT,100,1);
            }
            //creeper
            if(user != null){
                Box around = new Box(user.getBlockPos().add(-10,-10,-10), user.getBlockPos().add(10,10,10));
                List<CreeperEntity> creepers = user.clientWorld.getEntitiesByClass(CreeperEntity.class, around, creeperEntity -> true);
                if(!creepers.isEmpty()){
                    user.sendMessage(Text.literal("Creeper!"),true);
                    user.playSound(SoundEvents.ENTITY_PARROT_IMITATE_CREEPER, SoundCategory.HOSTILE,100,1);
                }
            }

            //mob's names
            if(user != null){
                Iterable<Entity> entities = user.clientWorld.getEntities();
                for (Entity ent:entities) {
                    if(ent.distanceTo(user) < 15 && !(ent instanceof ArmorStandEntity) ){
                        //all view
                        if(ent instanceof LivingEntity livingEntity){
                            livingEntity.setCustomName(Text.Serializer.fromJson("{\"text\":\""+ livingEntity.getHealth() + "♥/" + livingEntity.getMaxHealth() + "\",\"color\":\"red\"}"));
                            livingEntity.setCustomNameVisible(true);
                        }
                        //villager view
                        if(ent instanceof VillagerEntity villagerEntity){
                            VillagerData data = villagerEntity.getVillagerData();
                            villagerEntity.setCustomName(Text.literal(villagerEntity.getCustomName().getString() +" P:" + data.getProfession().toString() + "L:"+
                                    data.getLevel()+"T:"+data.getType().toString()));
                            villagerEntity.setCustomNameVisible(false);
                        }
                        //horse view
                        if(ent instanceof HorseEntity horseEntity){
                            double jumpStrenght = horseEntity.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH);
                            double speed = horseEntity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 42.16;
                            horseEntity.setCustomName(Text.literal(horseEntity.getCustomName().getString()
                                    +" Jump:"+decimalFormat.format(jumpStrenght)+" Speed:~"+decimalFormat.format(speed)));
                            horseEntity.setCustomNameVisible(false);
                        }
                    }else{
                        if(ent instanceof LivingEntity livingEntity && !(ent instanceof ArmorStandEntity)){
                            livingEntity.setCustomName(Text.empty());
                            livingEntity.setCustomNameVisible(false);
                        }
                    }
                }
            }
        }
    }
}
