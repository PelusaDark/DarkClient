package me.client.module.combat;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import me.client.module.util.Util;
import me.client.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;


public class STap extends Module {
    /*
    TODO: Epaleee Compadreee El Codigo Es Libre Pero Recuerda darme los creditos si vas a cualquier modulo B)
    */
    private boolean mouseButtonDown = false;
    private long lastHitTime = 0;
    private long lastActivationTime = 0;
    private boolean isWDeactivated = false;
    private boolean isDelayOver = false;
    private boolean isSDesactivated = false;
    private boolean flag = false;
    private boolean wasAttackKeyDown = false;


    private static final Minecraft mc = Minecraft.getMinecraft();

    public STap() {
        super("STap", "Auto S Tap", Category.COMBAT);
        Dark.instance.settingsManager.rSetting(new Setting("Duracion (ms)", this, 150, 5, 200, true));
        Dark.instance.settingsManager.rSetting(new Setting("Cooldown (ms)", this, 300, 10, 400, true));
        Dark.instance.settingsManager.rSetting(new Setting("delay (ms)", this, 2, 0, 100, true));
        Dark.instance.settingsManager.rSetting(new Setting("MinDist", this, 1.5, 0, 6, false));
        Dark.instance.settingsManager.rSetting(new Setting("MaxDist", this, 3.5, 0, 6, false));
        Dark.instance.settingsManager.rSetting(new Setting("OnlyOnGround", this, false));
        
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!Util.nullCheck() || event.phase != Phase.END) return;
        boolean isSprinting = Minecraft.getMinecraft().thePlayer.isSprinting();
        float Duracion = (float) Dark.instance.settingsManager.getSettingByName(this, "Duracion (ms)").getValDouble();
        float Cooldown = (float) Dark.instance.settingsManager.getSettingByName(this, "Cooldown (ms)").getValDouble();
        int Delay = (int) Dark.instance.settingsManager.getSettingByName(this, "delay (ms)").getValDouble();
        float MinDist = (float) Dark.instance.settingsManager.getSettingByName(this, "MinDist").getValDouble();
        float MaxDist = (float) Dark.instance.settingsManager.getSettingByName(this, "MaxDist").getValDouble();

        boolean isAttackKeyDown = mc.gameSettings.keyBindAttack.isKeyDown();

        // Detectar flanco de subida de la tecla de ataque
        if (isAttackKeyDown && !wasAttackKeyDown) {
            flag = true;
        }
         wasAttackKeyDown = isAttackKeyDown; // Esta línea debe estar SIEMPRE después de la condición del flanco de subida


            Entity target = mc.objectMouseOver.entityHit;
            if (flag) {
            boolean OnlyGroundOpt = Dark.instance.settingsManager.getSettingByName(this, "OnlyOnGround").getValBoolean();
            boolean isOnGround = mc.thePlayer.onGround;
            if (mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindBack.isKeyDown() && target != null && (isOnGround || !OnlyGroundOpt)) {
                double distance = event.player.getDistanceToEntity(target);
                if (distance >= MinDist && distance <= MaxDist && System.currentTimeMillis() - lastActivationTime >= Cooldown) {

                    lastHitTime = System.currentTimeMillis();
                    isDelayOver = false;

                }                
            }
            flag = false;
            }
            if (!isDelayOver && System.currentTimeMillis() - lastHitTime >= Delay) { // Espera X ms después de cada golpe
                isWDeactivated = true;
                isSDesactivated = true;
                lastActivationTime = System.currentTimeMillis();
                isDelayOver = true;
            }

            if (isWDeactivated && System.currentTimeMillis() - lastActivationTime >= Duracion) {
                isWDeactivated = false;
                isSDesactivated = false;
            }

            if (isWDeactivated) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
            } else {
                if (!(mc.currentScreen instanceof GuiScreen)) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
                } else {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                }
            }
        
        // Liberar keybinds si no se encontró agua
        //KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
        //KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
        //KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
        //KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
        
    }
}