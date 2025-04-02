package me.client.module.combat;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.util.Util;
import me.client.module.Module;
import net.minecraft.client.Minecraft;
import me.client.settings.Setting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class LegitHitSelect extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private boolean wasHurt = false;
    private long hurtTime = 0;
    private long lastAttack =0;
    private long lastActivationTime = 0;
    private boolean hasAttacked = false;
    public LegitHitSelect() {
        super("LegitHitSelect", "Igual que el hitselect pero legit", Category.COMBAT);
        Dark.instance.settingsManager.rSetting(new Setting("Delay",this,50,2,200,true));
        Dark.instance.settingsManager.rSetting(new Setting("Cooldown",this,50,10,100,true));
    }


    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != Phase.START) return;
       int delay = (int) Dark.instance.settingsManager.getSettingByName(this,"Delay").getValDouble();
          int cooldown = (int) Dark.instance.settingsManager.getSettingByName(this,"Cooldown").getValDouble();
        
        if (!Util.nullCheck()) return;
        if (event.player.hurtTime == 10 && !wasHurt) {
            wasHurt = true;
            hasAttacked = false;
            hurtTime = System.currentTimeMillis();
            lastActivationTime = System.currentTimeMillis();            
        } else if (wasHurt && !hasAttacked && System.currentTimeMillis() - hurtTime >= delay) {
            KeyBinding keyBindAttack = mc.gameSettings.keyBindAttack;
            int keyCode = keyBindAttack.getKeyCode();

            KeyBinding.setKeyBindState(keyCode, true);
            KeyBinding.onTick(keyCode);
            KeyBinding.setKeyBindState(keyCode, false);
            
            hasAttacked = true;
            lastAttack = System.currentTimeMillis();
        }

        if(System.currentTimeMillis() - lastAttack >= cooldown && hasAttacked){
        hasAttacked=false;
        wasHurt=false;
        }
    }

}
