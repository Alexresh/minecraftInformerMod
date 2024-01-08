package ru.informer.runnables;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.debug.BeeDebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillagerData;
import ru.informer.utils.Configuration;
import ru.informer.Main;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static ru.informer.MainClient.minecraftClient;

@Environment(EnvType.CLIENT)
public class Visual {
    private static final ScheduledExecutorService visualTask = Executors.newScheduledThreadPool(1);
    private ScheduledFuture existingTask;
    private final Runnable Worker = new VisualWorker();

    private boolean featureEnabled;

    private static boolean moodNotificationEnabled = false;
    private static boolean moodSoundNotificationEnabled = false;
    private static boolean creeperNotificationEnabled = false;
    private static int creeperNotificationRadius = 10;
    private static boolean creeperSoundNotificationEnabled = false;
    private static boolean mobHealthRender = false;
    private static int mobHealthRenderRadius = 15;
    private static boolean villagerExtendRender = false;
    private static boolean horseExtendRender = false;
    private static final int schedulePeriod = 1000;


    public Visual(){
        reload();
    }

    public void reload(){
        try {
            featureEnabled = Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.Visual));
            moodNotificationEnabled = Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.MoodNotification));
            moodSoundNotificationEnabled = Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.MoodSoundNotification));
            creeperNotificationEnabled = Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.CreeperNotification));
            creeperNotificationRadius = Integer.parseInt(Main.config.getProperty(Configuration.allProperties.CreeperNotificationRadius));
            creeperSoundNotificationEnabled = Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.CreeperSoundNotification));
            mobHealthRender = Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.MobHealthRender));
            mobHealthRenderRadius = Integer.parseInt(Main.config.getProperty(Configuration.allProperties.MobHealthRenderRadius));
            villagerExtendRender = Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.VillagerExtendRender));
            horseExtendRender = Boolean.parseBoolean(Main.config.getProperty(Configuration.allProperties.HorseExtendRender));
        }catch (NumberFormatException e){
            Main.LOGGER.error("(Visual.reload) Config corrupted!");
        }

        if(featureEnabled){
            enableVisual();
        }else {
            disableVisual();
        }
    }


    private void enableVisual() {
        if(existingTask != null){
            existingTask.cancel(true);
        }
        existingTask = visualTask.scheduleAtFixedRate(Worker,0, schedulePeriod, TimeUnit.MILLISECONDS);
    }
    private void disableVisual() {
        if(existingTask != null) {
            ClientPlayerEntity user = minecraftClient.player;
            existingTask.cancel(true);
            if (user != null) {
                Iterable<Entity> entities = user.clientWorld.getEntities();
                for (Entity ent : entities) {
                    if (ent instanceof LivingEntity livingEntity) {
                        livingEntity.setCustomName(Text.empty());
                    }
                }
            }
        }
    }

    private static class VisualWorker implements Runnable{
        private final DecimalFormat decimalFormat = new DecimalFormat("#.##");
        @Override
        public void run() {
            ClientPlayerEntity user = minecraftClient.player;
            //mood
            if (moodNotificationEnabled && user != null && user.getMoodPercentage() > 0.98) {
                user.sendMessage(Text.literal("MOOD!").formatted(Formatting.RED).formatted(Formatting.BOLD),true);
                if(moodSoundNotificationEnabled) user.playSound(SoundEvents.ITEM_TRIDENT_HIT, SoundCategory.AMBIENT,100,1);
            }
            //creeper
            if(creeperNotificationEnabled && user != null){
                Box around = new Box(
                        user.getPos().add(-creeperNotificationRadius,-creeperNotificationRadius,-creeperNotificationRadius),
                        user.getPos().add(creeperNotificationRadius,creeperNotificationRadius,creeperNotificationRadius)
                );
                List<CreeperEntity> creepers = user.clientWorld.getEntitiesByClass(CreeperEntity.class, around, creeperEntity -> true);
                if(!creepers.isEmpty()){
                    user.sendMessage(Text.translatable("creeper.name").formatted(Formatting.RED).formatted(Formatting.BOLD),true);
                    if(creeperSoundNotificationEnabled)
                        user.playSound(SoundEvents.ENTITY_PARROT_IMITATE_CREEPER,
                            SoundCategory.HOSTILE,
                            100,
                            1
                        );
                }
            }

            //mob's names
            if(mobHealthRender && user != null){
                Iterable<Entity> entities = user.clientWorld.getEntities();
                for (Entity ent:entities) {
                    if(ent.distanceTo(user) < mobHealthRenderRadius){
                        //all view
                        if(ent instanceof LivingEntity livingEntity){
                            livingEntity.setCustomName(Text.literal(livingEntity.getHealth() + "♥/" + livingEntity.getMaxHealth()).formatted(Formatting.RED));
                            livingEntity.setCustomNameVisible(true);
                        }
                        //villager view
                        if(villagerExtendRender && ent instanceof VillagerEntity villagerEntity){
                            VillagerData data = villagerEntity.getVillagerData();
                            villagerEntity.setCustomName(Text.literal(villagerEntity.getCustomName().getString() +" P:" + data.getProfession().toString() + "L:"+
                                    data.getLevel()+"T:"+data.getType().toString()));
                            villagerEntity.setCustomNameVisible(false);
                        }
                        //horse view
                        if(horseExtendRender && ent instanceof HorseEntity horseEntity){
                            double jumpStrength = horseEntity.getAttributeValue(EntityAttributes.HORSE_JUMP_STRENGTH);
                            double speed = horseEntity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 42.16;
                            horseEntity.setCustomName(Text.literal(horseEntity.getCustomName().getString()
                                    +" Jump:"+decimalFormat.format(jumpStrength)+" Speed:~"+decimalFormat.format(speed)));
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
