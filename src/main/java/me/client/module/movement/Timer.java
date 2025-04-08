package me.client.module.movement;

import me.client.Dark;
import me.client.settings.Setting;
import me.client.module.util.Util;
import me.client.module.Category;
import me.client.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.*;
import net.minecraft.client.*;
import java.lang.reflect.*;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraftforge.fml.common.eventhandler.*;
public class Timer extends Module {
        float Speed;
	public Timer() {
		super("Timer", "", Category.MOVEMENT);
                
        Dark.instance.settingsManager.rSetting(new Setting("Speed", this, 0.5, 0.01, 2.0, false));
	}

        
	private void setTimerRate(final float tick, float sp) {
        
        
        try {
            final Field timerField = Minecraft.class.getDeclaredField("field_71428_T");
            final Field tickPSField = net.minecraft.util.Timer.class.getDeclaredField("field_74278_d");
            if (timerField != null) {
                timerField.setAccessible(true);
                final net.minecraft.util.Timer timer = (net.minecraft.util.Timer)timerField.get(Timer.mc);
                timerField.setAccessible(false);
                tickPSField.setAccessible(true);
                tickPSField.set(timer, 1.0f + ((float)sp - 1.0f));
                tickPSField.setAccessible(false);
            }
            else {
                System.out.println("timerfield is null");
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

	@SubscribeEvent
	public void RenderTick(final TickEvent.RenderTickEvent e) {
		if (mc.theWorld == null || !this.isToggled())
			return;
        if (Timer.mc == null) {            
            return;
        }
        Speed = (float) Dark.instance.settingsManager.getSettingByName(this, "Speed").getValDouble();
        this.setTimerRate(1,Speed);		
	}
    @Override
	public void onDisable() {
		super.onDisable();
        this.setTimerRate(1,1);
		Speed = 1;
	}
}
