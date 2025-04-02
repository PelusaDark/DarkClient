package me.client.module.combat;

import me.client.Dark;
import me.client.settings.Setting;
import me.client.module.Category;
import me.client.module.Module;
import me.client.module.util.Util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.settings.KeyBinding;

public class TradeHelper extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private long lastAttackTime = 0; // Tiempo del último ataque

    public TradeHelper() {
        super("TradeHelper", "Util Para Tnt Tag", Category.COMBAT);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!Util.nullCheck()) return;

        String displayName = mc.thePlayer.getDisplayName().getUnformattedText().toLowerCase();

        // Detecta a los jugadores cercanos
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player != mc.thePlayer) {
                String playerName = player.getDisplayName().getUnformattedText().toLowerCase();

                // Verifica que el jugador local tenga "tnt" y el otro jugador no
                if (displayName.contains("tnt") && !playerName.contains("tnt") && isWithinDistance(mc.thePlayer, player, 2.96, 4.5)) {
                    // Verifica si ha pasado el tiempo de ataque
                    if (System.currentTimeMillis() - lastAttackTime >= 200) {                     
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true); // Presiona el botón
                    KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode()); // Actualiza el estado
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false); // Suelta el botón
                        lastAttackTime = System.currentTimeMillis();
                    }
                }
            }
        }
    }

    private boolean isWithinDistance(EntityPlayer player1, EntityPlayer player2, double minDistance, double maxDistance) {
        double distance = player1.getDistanceToEntity(player2);
        return distance >= minDistance && distance <= maxDistance;
    }
}
