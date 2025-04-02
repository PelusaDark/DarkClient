package me.client.module.combat;
import me.client.Dark;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import me.client.module.util.Util;
import net.minecraft.client.settings.KeyBinding;
import me.client.module.Category;
import me.client.module.Module;
import me.client.settings.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import static me.client.module.util.MathUtil.random;

public class LegitVelocity extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public LegitVelocity() {
        super("LegitVelocity", "LegitVelocity LiquidBounce", Category.COMBAT);
        Dark.instance.settingsManager.rSetting(new Setting("Chance", this, 0.5, 0.1, 1.0, false));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != Phase.END) {
            return;
        }
        if (!Util.nullCheck()) return;
        double chance = Dark.instance.settingsManager.getSettingByName(this, "Chance").getValDouble();
        if(random.nextDouble() <= chance){
        if (mc.thePlayer.hurtTime >= 8) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
        }
        if (mc.thePlayer.hurtTime >= 7) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
        } else if (mc.thePlayer.hurtTime >= 4) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
        } else if (mc.thePlayer.hurtTime > 1) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindForward));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindJump));
        }
        }
    }
}
