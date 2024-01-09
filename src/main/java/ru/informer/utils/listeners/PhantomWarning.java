package ru.informer.utils.listeners;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PhantomWarning {
    private static int sleepTimer = 0;
    public static boolean registered = false;
    public static void run(MinecraftClient client){
        sleepTimer++;
        if(sleepTimer > 500 && client.getNetworkHandler() != null && client.player != null){
            sleepTimer = 0;
            client.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
            if(client.player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST)) >= 70000){
                client.getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, Text.translatable("phantom.warn.title").formatted(Formatting.RED), Text.translatable("phantom.warn.description")));
            }
        }
    }


}
