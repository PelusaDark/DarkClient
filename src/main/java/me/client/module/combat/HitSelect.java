package me.client.module.combat;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import me.client.settings.Setting;
import me.client.module.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import me.client.module.util.utilities.TimerUtil;

public class HitSelect extends Module {
    private boolean wasHurt = false;
    private int periodMs;          // Tiempo entre clicks en milisegundos
    private int totalClicks;       // Total de clicks a realizar
    private int HurtSetting;

    private int CooldownTicks;
    private int remainingCooldownTicks = 0;

    private int clickCounter = 0;
    private final TimerUtil AttackDelay = new TimerUtil(); 

    public HitSelect() {
        super("HitSelect", "Realiza un burst de hits al recibir daño.", Category.COMBAT);
        Dark.instance.settingsManager.rSetting(new Setting("PeriodMs", this, 100, 20, 600, true)); // Tiempo entre clicks en ms
        Dark.instance.settingsManager.rSetting(new Setting("TotalClicks", this, 6, 1, 18, true));   // Total de clicks en el burst
        Dark.instance.settingsManager.rSetting(new Setting("HurtTime", this, 10, 1, 10, true));
        Dark.instance.settingsManager.rSetting(new Setting("CooldownTicks", this, 1, 0, 10, true));
        
    }
    @Override
    public void onEnable() {
        super.onEnable();
        clickCounter = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        clickCounter = 0;
        remainingCooldownTicks = 0;
        wasHurt = false;
    }
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
    if (/*Dark.instance.moduleManager.getModule("ReceiveHits").isToggled() || */!Util.nullCheck()) {
        wasHurt = false;
        return;
    }
    if (event.phase == Phase.START) {

    periodMs = (int) Dark.instance.settingsManager.getSettingByName(this, "PeriodMs").getValDouble();
    totalClicks = (int) Dark.instance.settingsManager.getSettingByName(this, "TotalClicks").getValDouble();
    HurtSetting = (int) Dark.instance.settingsManager.getSettingByName(this, "HurtTime").getValDouble();
    CooldownTicks = (int) Dark.instance.settingsManager.getSettingByName(this, "CooldownTicks").getValDouble();
    AttackDelay.setDelay(periodMs);

    
        // Primero verificamos si está en cooldown
        if (remainingCooldownTicks > 0) {
            remainingCooldownTicks--;
            return; // Salir temprano si aún está en cooldown
        }

        // Resto del código para detectar daño y ejecutar el burst
        if (mc.thePlayer.hurtTime >= HurtSetting && !wasHurt) {
            wasHurt = true;
        }

        if (clickCounter < totalClicks && wasHurt) {
            if (AttackDelay.hasCooldownExpired()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
                KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
                clickCounter++;
                AttackDelay.reset();
            }
        } else {
            wasHurt = false;
            clickCounter = 0;
            remainingCooldownTicks = CooldownTicks; // Iniciar el cooldown después del burst
        }
    }
 }

}