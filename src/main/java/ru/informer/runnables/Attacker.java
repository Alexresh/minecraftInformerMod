package ru.informer.runnables;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Attacker{

    private final MinecraftClient client = MinecraftClient.getInstance();

    private static final ScheduledExecutorService armortask = Executors.newScheduledThreadPool(1);
    private final EntityAttack entityAttack = new EntityAttack();

    private ScheduledFuture existingTask;

    public void startAttack(Class<? extends LivingEntity> entity, long period){
        if(existingTask != null){
            existingTask.cancel(true);
        }
        entityAttack.setAttackableEntity(entity);
        existingTask = armortask.scheduleAtFixedRate(entityAttack,0,period, TimeUnit.MILLISECONDS);
    }

    public void stopAttack(){
        existingTask.cancel(true);
    }

    private class EntityAttack implements Runnable{
        private Class <? extends LivingEntity> currentEntity;

        @Override
        public void run() {
            LivingEntity closestEntity = client.player.getWorld().getClosestEntity(currentEntity, TargetPredicate.DEFAULT, client.player, 0,0,0, new Box(client.player.getBlockPos().add(-5,-5,-5), client.player.getBlockPos().add(5,5,5)));
            if(closestEntity == null){
                client.player.sendMessage(Text.translatable("no.entity.find").formatted(Formatting.RED), true);
                client.player.playSound(SoundEvents.BLOCK_LAVA_POP, SoundCategory.PLAYERS, 100,1);
            }else {
                client.interactionManager.attackEntity(client.player, closestEntity);
            }

        }

        public void setAttackableEntity(Class <? extends LivingEntity>  entity) {
            this.currentEntity = entity;
        }
    }
}
