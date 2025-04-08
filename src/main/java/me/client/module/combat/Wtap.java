package me.client.module.combat;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import me.client.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import me.client.module.util.Util;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import io.netty.util.internal.ThreadLocalRandom;
import java.util.List;
import java.util.Random;

public class Wtap extends Module {

    private long lastActivationTime = 0;
    private long lastCooldownAct = 0;
    private boolean isWDeengaged = false;
    private boolean flag = false;
    private boolean wasAttackKeyDown = false;
    private static final Minecraft mc = Minecraft.getMinecraft();
    public Random ra;
    private int durationMs;
    private int cooldownMs;
    private int delayMs;
    private float minDist;
    private float maxDist;
    private int sRandom;
    private final double rayTraceReach = 6.0;

    public Wtap() {
        
        super("WTap", "Auto W-Tap con Raytrace", Category.COMBAT);

        this.ra = new Random();
        Dark.instance.settingsManager.rSetting(new Setting("Duracion (ms)", this, 150, 5, 200, true));
        Dark.instance.settingsManager.rSetting(new Setting("Cooldown (ms)", this, 300, 10, 400, true));
        Dark.instance.settingsManager.rSetting(new Setting("Delay (ms)", this, 2, 0, 100, true));
        Dark.instance.settingsManager.rSetting(new Setting("MinDist", this, 1.5, 0, 6, false));
        Dark.instance.settingsManager.rSetting(new Setting("MaxDist", this, 3.5, 0, 6, false));
        Dark.instance.settingsManager.rSetting(new Setting("Small Random", this, 10, 0, 100, true));
        
        Dark.instance.settingsManager.rSetting(new Setting("OnlyOnGround", this, false));
        
    }

    @Override
    public void onEnable() {
        super.onEnable();
        updateSettings();
    }

    private boolean isSp() {
        return Math.abs(mc.thePlayer.motionZ) >= 10 || Math.abs(mc.thePlayer.motionX) >= 10;
    }


    private void updateSettings() {
        this.durationMs = (int) Dark.instance.settingsManager.getSettingByName(this, "Duracion (ms)").getValDouble();
        this.cooldownMs = (int) Dark.instance.settingsManager.getSettingByName(this, "Cooldown (ms)").getValDouble();
        this.delayMs = (int) Dark.instance.settingsManager.getSettingByName(this, "Delay (ms)").getValDouble();
        this.minDist = (float) Dark.instance.settingsManager.getSettingByName(this, "MinDist").getValDouble();
        this.maxDist = (float) Dark.instance.settingsManager.getSettingByName(this, "MaxDist").getValDouble();
        this.sRandom = (int) Dark.instance.settingsManager.getSettingByName(this, "Small Random").getValDouble();
    }


    @SubscribeEvent
    public void tickEvent(TickEvent.ClientTickEvent event) {
        if (!Util.nullCheck() || event.phase != Phase.END) return;
        updateSettings();
        
        boolean isAttackKeyDown = mc.gameSettings.keyBindAttack.isKeyDown();
        
        if (isAttackKeyDown && !wasAttackKeyDown) {
            flag = true;
        }
         wasAttackKeyDown = isAttackKeyDown;
        double randomdelay =  this.ra.nextDouble() * sRandom;
        if (flag) {
              
            boolean OnlyGroundOpt = Dark.instance.settingsManager.getSettingByName(this, "OnlyOnGround").getValBoolean();
            boolean isOnGround = mc.thePlayer.onGround;
            boolean isSprinting = mc.thePlayer.isSprinting();
            Entity target = getPointedEntityRayTrace(rayTraceReach);

            if (mc.gameSettings.keyBindForward.isKeyDown() && target != null && isSprinting && (isOnGround || !OnlyGroundOpt)) {
                double distance = getRayTraceDistance(target);
                if (distance >= minDist && distance <= maxDist && System.currentTimeMillis() - lastCooldownAct >= (cooldownMs + randomdelay)) {
                    lastActivationTime = System.currentTimeMillis();
                    isWDeengaged = true;
                    lastCooldownAct = System.currentTimeMillis();
                }
            }
            flag = false;
        }


        if (isWDeengaged) {
            if (System.currentTimeMillis() - lastActivationTime >= (durationMs + randomdelay)) {
                isWDeengaged = false;
            } else {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                return;
            }
        }

        if (!isWDeengaged) {

                boolean shouldBePressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), shouldBePressed);
            
        }
    }

    private Entity getPointedEntityRayTrace(double reachDistance) {
        if (mc.getRenderViewEntity() == null || mc.theWorld == null) return null;

        Entity pointedEntity = null;
        Vec3 vec3 = mc.getRenderViewEntity().getPositionEyes(1.0F);
        Vec3 lookVec = mc.getRenderViewEntity().getLook(1.0F);
        Vec3 vec32 = vec3.addVector(lookVec.xCoord * reachDistance, lookVec.yCoord * reachDistance, lookVec.zCoord * reachDistance);
        float f1 = 1.0F;
        List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.getRenderViewEntity(), mc.getRenderViewEntity().getEntityBoundingBox().addCoord(lookVec.xCoord * reachDistance, lookVec.yCoord * reachDistance, lookVec.zCoord * reachDistance).expand(f1, f1, f1));
        double d2 = reachDistance;

        for (Entity entity : list) {
            if (entity.canBeCollidedWith()) {
                float collisionBorderSize = 0.13F;
                AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (0.0D < d2 || d2 == 0.0D) {
                        pointedEntity = entity;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    double distance = vec3.distanceTo(movingobjectposition.hitVec);
                    if (distance < d2 || d2 == 0.0D) {
                        if (entity == mc.getRenderViewEntity().ridingEntity && !entity.canRiderInteract()) {
                            if (d2 == 0.0D) {
                                pointedEntity = entity;
                            }
                        } else {
                            pointedEntity = entity;
                            d2 = distance;
                        }
                    }
                }
            }
        }
        return pointedEntity;
    }

    private double getRayTraceDistance(Entity target) {
        if (mc.getRenderViewEntity() == null || mc.theWorld == null || target == null) return Double.MAX_VALUE;

        Vec3 vec3 = mc.getRenderViewEntity().getPositionEyes(1.0F);
        Vec3 lookVec = mc.getRenderViewEntity().getLook(1.0F);
        Vec3 vec32 = vec3.addVector(lookVec.xCoord * rayTraceReach, lookVec.yCoord * rayTraceReach, lookVec.zCoord * rayTraceReach);

        float collisionBorderSize = 0.13F;
        AxisAlignedBB axisalignedbb = target.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

        if (movingobjectposition != null) {
            return vec3.distanceTo(movingobjectposition.hitVec);
        }
        return Double.MAX_VALUE;
    }
}
