package me.client.module.combat;

import me.client.module.Category;
import me.client.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import me.client.module.util.Util;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraft.util.ChatComponentText;

public class TimmingHit extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private int countdown = 0; // Contador en segundos
    private boolean isCountingDown = false; // Estado del contador
    private long lastTickTime = 0; // Tiempo del último tick
    private boolean attackScheduled = false; // Estado para saber si el ataque está programado
    private long attackTime = 0; // Tiempo programado para el ataque

    public TimmingHit() {
        super("TimmingHit", "Espera al ultimo segundo para dar con la tnt", Category.COMBAT);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!Util.nullCheck())
            return;
        if (!this.isToggled()) return;

        String message = event.message.getUnformattedText();
        for (int i = 1; i <= 10; i++) {
            if (message.contains("#" + i)) {
                startCountdown(61 - (5 * (i - 1)));
                break;
            }
        }
    }

    private void startCountdown(int seconds) {
        
        countdown = seconds;
        isCountingDown = true;
        lastTickTime = System.currentTimeMillis(); // Inicializa el tiempo actual
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return;

        if (isCountingDown) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTickTime >= 998) {
                countdown--; // Decrementa el contador cada segundo
                sendMessageToChat(countdown + "s"); // Mensaje de depuración
                lastTickTime = currentTime; // Actualiza el tiempo del último tick

                if (countdown == 1) {
                    attackScheduled = true;
                    attackTime = currentTime + 824; // Tiempo programado para el ataque
                }

                if (countdown <= 0) {
                    isCountingDown = false;
                }
            }
            if (attackScheduled && currentTime >= attackTime) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
                KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false); 
                attackScheduled = false;
                sendMessageToChat("Attacked");
            }
        }
    }
     
    private void sendMessageToChat(String message) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(message));
        }
    }
}
