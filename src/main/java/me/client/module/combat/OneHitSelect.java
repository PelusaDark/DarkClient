package me.client.module.combat;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import me.client.module.util.utilities.TimerUtil; // Importa la clase TimeUtil
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import me.client.module.util.Util;

public class OneHitSelect extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final TimerUtil attackDelayTimer = new TimerUtil(); // Instancia de TimeUtil para el delay
    private boolean Flag = true;

    public OneHitSelect() {
        super("OneHitSelect", "Ataca despues de recibir daño con un delay", Category.COMBAT);
        attackDelayTimer.setDelay(300); // Establece el cooldown de 400ms al inicio
    }

  @SubscribeEvent
public void onPlayerHurt(TickEvent.ClientTickEvent event) {
    if (!Util.nullCheck() || event.phase != Phase.START) {
        return;
    }
    /*if (Dark.instance.moduleManager.getModule("ReceiveHits").isToggled() && ) {
        return;
    }*/

    if (mc.thePlayer.hurtTime > 9 && Flag && attackDelayTimer.hasCooldownExpired()) {
        Flag = false;
    }
    if (!Flag) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
        KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);  
        attackDelayTimer.reset();
        Flag = true;
    }
}
}