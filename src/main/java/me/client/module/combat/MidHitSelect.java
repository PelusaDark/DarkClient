package me.client.module.combat;

import me.client.Dark;
import me.client.settings.Setting;
import me.client.module.Category;
import me.client.module.util.Util;
import me.client.module.Module;
import org.lwjgl.input.Keyboard;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.settings.KeyBinding;

import java.util.Random;

public class MidHitSelect extends Module {
    private Random random = new Random();
    private final Minecraft mc = Minecraft.getMinecraft();
    private long lastAttackTime = 0;
    private boolean currentShouldAttack = false;
    private long delay;
    private long configStartTime = 0;
    private boolean configDelayExpired = false;

    public MidHitSelect() {
        super("MidHitSelect", "Util Para Ganar Trades", Category.COMBAT);
        Dark.instance.settingsManager.rSetting(new Setting("Delay", this, 420, 50, 500, true));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        resetConfigDelay(); // Inicia el temporizador de configuración
    }

    @Override
    public void onDisable() {
        super.onDisable();
        configDelayExpired = true; // Para resetear el comportamiento si se vuelve a habilitar sin cambiar el delay
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!Util.nullCheck()) return;
        delay = (long) Dark.instance.settingsManager.getSettingByName(this, "Delay").getValDouble();
        updateAttackConditions();

        // Verificar si el delay de configuración ha expirado
        if (!configDelayExpired && System.currentTimeMillis() > configStartTime + delay) {
            configDelayExpired = true;
        }

        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player != mc.thePlayer && isWithinDistance(mc.thePlayer, player, 5)) {
                if (System.currentTimeMillis() - lastAttackTime >= getAttackDelay()) {
                    if (!configDelayExpired) { // Durante el delay de configuración
                        if (currentShouldAttack) {
                            attackPlayer();
                        }
                    } else { // Después del delay de configuración
                        attackPlayer(); // Ataca siempre después del delay si hay enemigo cerca
                        resetConfigDelay(); // Reinicia el temporizador de configuración después del ataque
                    }
                }
            }
        }
    }

    private void attackPlayer() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
        KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
        lastAttackTime = System.currentTimeMillis();
    }

    private void updateAttackConditions() {
        currentShouldAttack = mc.thePlayer.hurtTime > 0 && !mc.thePlayer.onGround && mc.thePlayer.moveForward != 0 || mc.gameSettings.keyBindBack.isKeyDown();
    }

    private long getAttackDelay() {
        int minDelay = 70;
        int maxDelay = 100;
        int delayRange = maxDelay - minDelay + 1; // Calcula el rango de posibles delays
        return minDelay + random.nextInt(delayRange); // Genera un número aleatorio dentro del rango y lo suma al mínimo
    }

    private boolean isWithinDistance(EntityPlayer player1, EntityPlayer player2, double distance) {
        return player1.getDistanceToEntity(player2) <= distance;
    }

    // Método para reiniciar el temporizador de configuración
    private void resetConfigDelay() {
        configStartTime = System.currentTimeMillis();
        configDelayExpired = false;
    }
}