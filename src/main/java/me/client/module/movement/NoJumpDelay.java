package me.client.module.movement;

import java.lang.reflect.Field;
import me.client.module.Category;
import me.client.module.Module;
import me.client.module.util.Util;
import me.client.module.util.utilities.StringRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase; // Import EntityLivingBase
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NoJumpDelay extends Module
{
    private Field jumpTicks;
    private Minecraft mc;

    public NoJumpDelay() {
        super(StringRegistry.register(new String(new char[] {'N', 'o', 'J', 'u', 'm', 'p', 'D', 'e', 'l', 'a', 'y'})),"", Category.MOVEMENT);
        this.mc = Minecraft.getMinecraft();
        try {      
            this.jumpTicks = EntityLivingBase.class.getDeclaredField("field_70773_bE"); // Changed to EntityLivingBase.class
        }
        catch (Exception var4) {
            try {          
                this.jumpTicks = EntityLivingBase.class.getDeclaredField("jumpTicks"); // Changed to EntityLivingBase.class
            }
            catch (Exception ex) {
                ex.printStackTrace(); // Print stack trace for debugging in case of errors
            }
        }
        if (this.jumpTicks != null) {          
            this.jumpTicks.setAccessible(true);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(final TickEvent.PlayerTickEvent e) {
        if(!Util.nullCheck())return;
            try {               
                this.jumpTicks.set(this.mc.thePlayer, 0); // Use mc.thePlayer instance to set jumpTicks
            }
            catch (IllegalAccessException ex) {
                ex.printStackTrace(); // Print stack trace for debugging in case of errors
            }
        
    }
}