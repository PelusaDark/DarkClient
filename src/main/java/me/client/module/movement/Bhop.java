package me.client.module.movement;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import me.client.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Bhop extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public Bhop() {
        super("Bhop", "Speed Legit", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {

        // Comprobar si el jugador y el mundo existen
        if (mc.thePlayer != null && mc.theWorld != null && mc.currentScreen == null) {

            EntityPlayerSP player = mc.thePlayer; // Usar la variable local es ligeramente más eficiente si se usa mucho

              if (player.moveForward > 0.8D) { // Usar player.moveForward en lugar de event.getForward()

                player.setSprinting(true);
              }

            // Lógica de auto-salto (puede interferir o ser parte del diseño)
            //if (player.onGround && (player.moveForward > 0 || player.moveStrafing > 0)) {
             //  player.jump();
          //  }

            // Lógica de FastFall
            if (!player.onGround && player.motionY < 0) {
               
                if (player.fallDistance > 1.5) {
                    
                    player.motionY *= 1.075D;
                }
            }
        }
    }
}