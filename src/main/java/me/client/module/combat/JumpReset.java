package me.client.module.combat;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import me.client.settings.Setting;
import me.client.module.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static me.client.module.util.MathUtil.random;

public class JumpReset extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    // Settings
    private double chance;
    private int delay;
    private int cooldown;
    private boolean onlyForward;

    // State variables
    private long lastHurtTime = 0;
    private long lastJumpTime = 0;
    private boolean wasHurt = false;
    private boolean hasJumped = false;

    public JumpReset() {
        super("JumpReset", "Reduce knockback in a legitimate way", Category.COMBAT);

        // Initialize settings
        Dark.instance.settingsManager.rSetting(new Setting("Chance", this, 0.5, 0.1, 1.0, false));
        Dark.instance.settingsManager.rSetting(new Setting("Delay", this, 2, 0, 300, true));
        Dark.instance.settingsManager.rSetting(new Setting("Cooldown", this, 300, 0, 1000, false));
        Dark.instance.settingsManager.rSetting(new Setting("OnlyForward", this, false));

        // Update settings
        updateSettings();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        updateSettings();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        wasHurt = false;
        hasJumped = false;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!Util.nullCheck() || mc.thePlayer.maxHurtTime <= 0) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (mc.thePlayer.hurtTime == 7 && !wasHurt) {
            lastHurtTime = currentTime;
            wasHurt = true;
            hasJumped = false;
        }

        // Check if the player was hurt and the delay has passed
        if (wasHurt && (currentTime - lastHurtTime >= delay)) {
            // Check if the cooldown has passed and the player hasn't jumped yet
            if (currentTime - lastJumpTime >= cooldown && !hasJumped) {
                // Check if the player is moving forward (if "OnlyForward" is enabled)
                if (onlyForward && mc.thePlayer.moveForward == 0) {
                    return;
                }

                // Randomly decide to jump based on the chance setting
                if (random.nextDouble() <= chance && mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    lastJumpTime = currentTime;
                    hasJumped = true;
                }
            }
        }

        if (currentTime - lastHurtTime >= delay + 50 && mc.thePlayer.hurtTime <= 0 && wasHurt) {
            wasHurt = false;
        }
    }

    private void updateSettings() {
        chance = Dark.instance.settingsManager.getSettingByName(this, "Chance").getValDouble();
        delay = (int) Dark.instance.settingsManager.getSettingByName(this, "Delay").getValDouble();
        cooldown = (int) Dark.instance.settingsManager.getSettingByName(this, "Cooldown").getValDouble();
        onlyForward = Dark.instance.settingsManager.getSettingByName(this, "OnlyForward").getValBoolean();
    }
}
