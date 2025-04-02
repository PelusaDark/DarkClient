package me.client.module.player;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import me.client.settings.Setting;
import me.client.module.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Field;

public class FastPlace extends Module {
    private final Field rightClickDelayTimerField;
    private boolean ConBloqueP;
    private int delay;
    Minecraft mc = Minecraft.getMinecraft();
    public FastPlace() {
        super("FastPlace", "Corrige el retraso de colocacion de Minecraft", Category.PLAYER);
        Dark.instance.settingsManager.rSetting(new Setting("OnlyBlocks",this,true));
        rightClickDelayTimerField = ReflectionHelper.findField(Minecraft.class, "rightClickDelayTimer", "field_71467_ac");
        if (rightClickDelayTimerField != null) {
            rightClickDelayTimerField.setAccessible(true);
        }
        Dark.instance.settingsManager.rSetting(new Setting("DelayTick", this, 1.0, 0.0, 3.0, true));
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (!Util.nullCheck()){
            return;
        }
        boolean OnlyBlockAct = Dark.instance.settingsManager.getSettingByName(this,"OnlyBlocks").getValBoolean();
        if (OnlyBlockAct != ConBloqueP) {
            ConBloqueP = OnlyBlockAct;
        }
        if (isBlockHand() || !ConBloqueP){
        if (event.phase == TickEvent.Phase.END && Mouse.isButtonDown(1) && rightClickDelayTimerField != null) {
          delay = (int) Dark.instance.settingsManager.getSettingByName(this, "DelayTick").getValDouble();

            try {
                int c = delay;
                if(c == 0){ 
                rightClickDelayTimerField.setInt(mc, 0);
                }else {
                        if (c == 4) {
                            return;
                        }

                        int d = rightClickDelayTimerField.getInt(mc);
                        if (d == 4) {
                            rightClickDelayTimerField.set(mc, c);
                        }
                    }
                
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }
    }
    private boolean isBlockHand(){
        if (mc.thePlayer == null){
            return false;
        }
        if (mc.thePlayer.getHeldItem() == null){
            return false;
        }
        return this.mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock;
    }
}