package me.client.module.misc;

import me.client.Dark;
import me.client.settings.Setting;
import java.lang.reflect.Field;
import me.client.module.Category;
import me.client.module.Module;
import me.client.module.util.Util;
import me.client.module.util.utilities.StringRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Delay17 extends Module {

    private Field leftClickCounter;
    private Minecraft mc;

    public Delay17() {
        super(StringRegistry.register(new String(new char[] {'D', 'e', 'l', 'a', 'y', ' ', 'F', 'i', 'x'})), "quita el delay de la 1.8", Category.MISC);
        this.mc = Minecraft.getMinecraft();
        Dark.instance.settingsManager.rSetting(new Setting("Activation Distance", this, 3.4, 1.0, 6.0, false));
        
        try {
            this.leftClickCounter = Minecraft.class.getDeclaredField("field_71429_W");
        } catch (Exception var4) {
            try {
                this.leftClickCounter = Minecraft.class.getDeclaredField("leftClickCounter");
            } catch (Exception ex) {
                ex.printStackTrace(); // Print stack trace for debugging in case of errors
            }
        }
        if (this.leftClickCounter != null) {
            this.leftClickCounter.setAccessible(true);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(final TickEvent.PlayerTickEvent e) {
        if (Util.nullCheck()) {
            float dist = (float) Dark.instance.settingsManager.getSettingByName(this, "Activation Distance").getValDouble();

            Entity target = Util.getPointedEntityRayTrace(dist);
            if(target != null){
            try {
                this.leftClickCounter.set(this.mc, 0);
            } catch (IllegalAccessException ex) {
                ex.printStackTrace(); // Print stack trace for debugging in case of errors
            }
            }
       }
    }

}
