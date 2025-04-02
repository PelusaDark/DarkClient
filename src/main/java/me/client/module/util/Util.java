package me.client.module.util;

import me.client.Dark;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import net.minecraft.world.World;
import java.util.List;
public class Util{

private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean nullCheck() {
        return mc.thePlayer != null && mc.theWorld != null && mc.currentScreen == null && !mc.gameSettings.showDebugInfo && !Dark.instance.destructed;
     }


    public static Entity getPointedEntityRayTrace(double reachDistance) {
        if (mc.getRenderViewEntity() == null || !nullCheck()) return null;

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
}