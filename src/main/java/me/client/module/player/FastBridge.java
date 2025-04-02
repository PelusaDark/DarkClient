package me.client.module.player;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import me.client.module.util.ReflectionUtil;
import me.client.settings.Setting;
import me.client.module.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import scala.xml.Null;
import org.lwjgl.input.Keyboard;

public class FastBridge extends Module {
Minecraft mc = Minecraft.getMinecraft();
    
    private boolean isSneaking = false;
    public FastBridge() {
        super("FastBridge", "Scandffold legit", Category.PLAYER);
        
    }
    @Override
	public void onDisable() {
		super.onDisable();
        try {
		ReflectionUtil.pressed.set(Minecraft.getMinecraft().gameSettings.keyBindSneak, false);
        } catch (Exception localException) {
        }
    }
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!this.isToggled() || !Util.nullCheck())
            return;
        try {
            if (((mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock)) && (!mc.gameSettings.keyBindJump.isPressed())) {
                BlockPos bp = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
                if (mc.theWorld.getBlockState(bp).getBlock() == Blocks.air) {
                    ReflectionUtil.pressed.set(Minecraft.getMinecraft().gameSettings.keyBindSneak, true);
                    
                } else {
                    ReflectionUtil.pressed.set(Minecraft.getMinecraft().gameSettings.keyBindSneak, false);
                    
                }
            }
        } catch (Exception localException) {
        }
    }
}