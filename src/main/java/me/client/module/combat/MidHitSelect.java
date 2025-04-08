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
    private boolean atrun;
    public boolean run;


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

        if(run && configDelayExpired){run =false;}else if(run && !configDelayExpired){ run = false;}

        delay = (long) Dark.instance.settingsManager.getSettingByName(this, "Delay").getValDouble();
        updateAttackConditions();    
        // Verificar si el delay de configuración ha expirado
        if (!configDelayExpired && System.currentTimeMillis() > configStartTime + delay) {
            configDelayExpired = true;
            
        }
                    if (!configDelayExpired) { // Durante el delay de configuración
                        if (currentShouldAttack) {
                            //Atacar
                            run = true;
                            return;
                        }                  
                    } else { // Después del delay de configuración
                        //Atacar
                        run = true;
                        atrun = true;
                        resetConfigDelay(); // Reinicia el temporizador de configuración después del ataque
                    }

            

    }
    private void updateAttackConditions() {

        currentShouldAttack = mc.thePlayer.hurtTime > 0 && !mc.thePlayer.onGround && mc.thePlayer.moveForward != 0 || mc.gameSettings.keyBindBack.isKeyDown();
        
    }
    // Método para reiniciar el temporizador de configuración
    private void resetConfigDelay() {
        configStartTime = System.currentTimeMillis();
        configDelayExpired = false;
        
    }
}