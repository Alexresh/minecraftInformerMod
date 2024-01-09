package ru.informer.utils.listeners;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ToolBreakRestriction {
    public static boolean registered = false;

    public static ActionResult run(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        if(!world.isClient) return ActionResult.PASS;
        if(player.getMainHandStack().isDamageable() && player.getMainHandStack().getDamage() > player.getMainHandStack().getMaxDamage() - 3){
            player.sendMessage(Text.translatable("damage.restriction").formatted(Formatting.RED).formatted(Formatting.BOLD), true);
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
}
