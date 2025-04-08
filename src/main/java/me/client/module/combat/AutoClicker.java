package me.client.module.combat;

import me.client.Dark;
import me.client.module.misc.ReceiveHits;

import net.minecraft.item.ItemSword;
import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;
import io.netty.util.internal.ThreadLocalRandom;
import me.client.module.util.Util;
import me.client.module.Category;
import me.client.module.Module;
import me.client.settings.Setting;
import me.client.module.combat.MidHitSelect;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import net.minecraft.client.*;
import java.util.*;
import java.lang.reflect.*;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.common.gameevent.*;
import net.minecraft.client.settings.*;
import net.minecraft.client.gui.inventory.*;
import org.lwjgl.input.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;
import net.minecraft.block.material.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.eventhandler.*;
import java.nio.*;

public class AutoClicker extends Module
{

    public static boolean isRunning;
    public static Minecraft mc;
    private boolean swordActivado;
    private long nlu;
    private long nld;
    private long nd;
    private long ne;
    private double drr;
  //  private boolean canclickhit;
    private int Max;
    private int Min;
    private boolean dr;
    public Random r;
    private static Field bst;
    private static Field fff;
    private static Field bff;
    private boolean acthitselect;
    private ReceiveHits receiveHits;
    private MidHitSelect midHitSelectInstance;
    public AutoClicker() {
        super("Autoclicker", "", Category.COMBAT);
        this.r = new Random();
        
        Dark.instance.settingsManager.rSetting(new Setting("Min", this, 12.0, 0.0, 30.0, true));
        Dark.instance.settingsManager.rSetting(new Setting("Max", this, 15.0, 0.0, 40.0, true));
        Dark.instance.settingsManager.rSetting(new Setting("OnlySworld", this, false));

        Dark.instance.settingsManager.rSetting(new Setting("CPS Boost Chance", this, 50.0, 0.0, 100.0, true)); // Nueva setting para la probabilidad del boost de CPS
        Dark.instance.settingsManager.rSetting(new Setting("CPS Boost Amount", this, 2.0, 0.0, 5.0, true)); // Nueva setting para la cantidad del boost de CPS
        Dark.instance.settingsManager.rSetting(new Setting("Click Delay Variance", this, 5.0, 0.0, 20.0, true)); // Nueva setting para la varianza del delay del click
        Dark.instance.settingsManager.rSetting(new Setting("Release Delay Variance", this, 8.0, 0.0, 20.0, true)); // Nueva setting para la varianza del delay de release


        

    }   

    public void setReceiveHits(ReceiveHits receiveHits) {
        this.receiveHits = receiveHits;
    }

    @SubscribeEvent
    public void onTick(final TickEvent.RenderTickEvent e) {
        if (!Util.nullCheck() || e.phase != Phase.START)return;
        
        if (receiveHits == null) {
            receiveHits = (ReceiveHits) Dark.instance.moduleManager.getModule("ReceiveHits");
        }
        if (midHitSelectInstance == null) {
            midHitSelectInstance = (MidHitSelect) Dark.instance.moduleManager.getModule("MidHitSelect");
        }
        if(receiveHits !=null && Dark.instance.moduleManager.getModule("ReceiveHits").isToggled() && !receiveHits.isHab()) return;
        if(midHitSelectInstance != null && Dark.instance.moduleManager.getModule("MidHitSelect").isToggled() && !midHitSelectInstance.run)return;

        Min = (int) Dark.instance.settingsManager.getSettingByName(this, "Min").getValDouble();
        Max = (int) Dark.instance.settingsManager.getSettingByName(this, "Max").getValDouble();
        boolean onlySworldConfig = Dark.instance.settingsManager.getSettingByName(this, "OnlySworld").getValBoolean();

        if (onlySworldConfig != swordActivado) {
            swordActivado = onlySworldConfig;
        }
        if(swordActivado && !isSwordInHand())return;

            Mouse.poll();
            if (Mouse.isButtonDown(0)) {
                if (this.nld > 0L && this.nlu > 0L) {
                    if (System.currentTimeMillis() > this.nld) {
                        final int attackKeyBind = AutoClicker.mc.gameSettings.keyBindAttack.getKeyCode();

                        KeyBinding.setKeyBindState(attackKeyBind, true);
                        KeyBinding.onTick(attackKeyBind);
                        s(0, true);
                        this.vcx();
                        AutoClicker.isRunning = true;
                    }
                    else if (System.currentTimeMillis() > this.nlu) {
                        KeyBinding.setKeyBindState(AutoClicker.mc.gameSettings.keyBindAttack.getKeyCode(), false);
                        s(0, false);
                        AutoClicker.isRunning = false;
                    }
                }
                else {
                    this.vcx();
                }
            }
            else {
                this.nlu = 0L;
                this.nld = 0L;
            }
        
    }

    private boolean isSwordInHand() {
        if (mc.thePlayer == null || this.mc.thePlayer.getHeldItem() == null) {
            return false;
        }
        return this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    private void vcx() {
        final double mcc = Min;
        final double mca = Max+1;
        if (mcc > mca) {
            return;
        }
        double CPS = mcc + this.r.nextDouble() * (mca - mcc);

        double boostChance = Dark.instance.settingsManager.getSettingByName(this, "CPS Boost Chance").getValDouble();
        double boostAmount = Dark.instance.settingsManager.getSettingByName(this, "CPS Boost Amount").getValDouble();

        if(this.r.nextInt(100) <= boostChance){ // Usar <= para que sea más intuitivo el chance (ej: 50% chance)
            CPS += this.r.nextDouble() * boostAmount; // Boost aleatorio hasta la cantidad especificada
        }

        long delay = (int)Math.round(1000.0 / CPS);

        if (System.currentTimeMillis() > this.nd) {
            if (!this.dr && this.r.nextInt(100) >= 55) {
                this.dr = true;
                this.drr = 1.1 + this.r.nextDouble() * 0.15;
            }
            else {
                this.dr = false;
            }
            this.nd = System.currentTimeMillis() + 300L + this.r.nextInt(1300);
        }
        if (this.dr) {
            delay *= (long)this.drr;
        }
        if (System.currentTimeMillis() > this.ne) {
            if (this.r.nextInt(100) >= 50) {
                delay += 20L + this.r.nextInt(135);
            }
            this.ne = System.currentTimeMillis() + 400L + this.r.nextInt(1380);
        }

        double clickVariance = Dark.instance.settingsManager.getSettingByName(this, "Click Delay Variance").getValDouble();
        double releaseVariance = Dark.instance.settingsManager.getSettingByName(this, "Release Delay Variance").getValDouble();


        this.nld = System.currentTimeMillis() + delay + (long)(this.r.nextDouble() * clickVariance); // Añadir varianza al click delay
        this.nlu = System.currentTimeMillis() + delay / 2L - (long)(this.r.nextDouble() * releaseVariance); // Añadir varianza al release delay, y asegurar que no sea negativo


        if (nlu < 0) { // Asegurarse de que nlu no sea negativo
            nlu = 0;
        }
    }

    public static void s(final int button, final boolean state) {
        final MouseEvent m = new MouseEvent();
        AutoClicker.fff.setAccessible(true);
        try {
            AutoClicker.fff.set(m, button);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        AutoClicker.fff.setAccessible(false);
        AutoClicker.bst.setAccessible(true);
        try {
            AutoClicker.bst.set(m, state);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        AutoClicker.bst.setAccessible(false);
        MinecraftForge.EVENT_BUS.post((Event)m);
        try {
            AutoClicker.bff.setAccessible(true);
            final ByteBuffer buffer = (ByteBuffer)AutoClicker.bff.get(null);
            AutoClicker.bff.setAccessible(false);
            buffer.put(button, (byte)(state ? 1 : 0));
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    static {
        AutoClicker.isRunning = false;
        AutoClicker.mc = Minecraft.getMinecraft();
        try {
            AutoClicker.fff = MouseEvent.class.getDeclaredField("button");
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        try {
            AutoClicker.bst = MouseEvent.class.getDeclaredField("buttonstate");
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        try {
            AutoClicker.bff = Mouse.class.getDeclaredField("buttons");
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
