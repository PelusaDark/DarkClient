package me.client.module.render;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import net.minecraft.client.Minecraft;
import me.client.module.util.Util;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.monster.EntityMob;

import org.lwjgl.opengl.GL11;

public class ESP extends Module {
    private Minecraft mc = Minecraft.getMinecraft();
    private static final float RED = 1.0f;
    private static final float GREEN = 1.0f;
    private static final float BLUE = 1.0f;
    private static final float ALPHA = 0.6f;

    public ESP() {
        super("ESP", "Classic 3D box Esp", Category.RENDER);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (Util.nullCheck()) {
            double renderPosX = mc.getRenderManager().viewerPosX;
            double renderPosY = mc.getRenderManager().viewerPosY;
            double renderPosZ = mc.getRenderManager().viewerPosZ;

            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (shouldDrawESP(entity)) {
                    if (entity == mc.thePlayer) continue; // No dibujar ESP en ti mismo
                    if (!(entity instanceof EntityPlayer)) continue; // Dibujar ESP en jugadores
                    drawESP(entity, event.partialTicks, renderPosX, renderPosY, renderPosZ);
                }
            }
        }
    }

    private boolean shouldDrawESP(Entity entity) {
        if (entity == mc.thePlayer) return false; // No dibujar ESP en ti mismo
        if (!(entity instanceof EntityPlayer)) return false;
        if (entity instanceof EntityMob) return false;
        return entity instanceof EntityLivingBase && !(entity instanceof EntityArmorStand); // Dibujar en entidades vivientes excepto armaduras
    }

    private void drawESP(Entity entity, float partialTicks, double renderPosX, double renderPosY, double renderPosZ) {
        AxisAlignedBB entityBox = entity.getEntityBoundingBox();
        if (entityBox == null) return; // No dibujar si el box es nulo

        GlStateManager.pushMatrix(); // Guarda el estado de la matriz actual
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - renderPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - renderPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderPosZ;

        AxisAlignedBB translatedBox = new AxisAlignedBB(
                entityBox.minX - entity.posX + x,
                entityBox.minY - entity.posY + y,
                entityBox.minZ - entity.posZ + z,
                entityBox.maxX - entity.posX + x,
                entityBox.maxY - entity.posY + y,
                entityBox.maxZ - entity.posZ + z
        );

        GL11.glLineWidth(1.0F); // Establece el ancho de línea
        GlStateManager.color(RED, GREEN, BLUE, ALPHA); // Establece el color

        RenderGlobal.drawOutlinedBoundingBox(translatedBox, (int)(RED * 255), (int)(GREEN * 255), (int)(BLUE * 255), (int)(ALPHA * 255));

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix(); // Restaura al estado de la matriz que guardamos
    }
}