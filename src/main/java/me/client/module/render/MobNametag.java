package me.client.module.render;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import me.client.module.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
// import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11; // <--- IMPORTANTE: Añadir esta línea

public class MobNametag extends Module {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public MobNametag() {
        super("MobNametag", "Renderiza nametags personalizados sobre mobs con nombres personalizados.", Category.RENDER);
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Specials.Pre event) {
        // Comprobaciones iniciales
        if (!Util.nullCheck() || Dark.instance.destructed || mc.getRenderManager() == null || mc.fontRendererObj == null) {
            return;
        }

        // Comprobar si es una entidad viva, NO es el jugador local, está viva, y TIENE un nombre personalizado
        if (event.entity instanceof EntityLivingBase && event.entity != mc.thePlayer && event.entity.deathTime == 0 && event.entity.hasCustomName()) {

            EntityLivingBase entity = (EntityLivingBase) event.entity; // Casteo correcto
            String name = entity.getDisplayName().getFormattedText(); // Obtener el nombre de la entidad correcta

            // Evitar que Minecraft renderice su nametag por defecto
            event.setCanceled(true);

            // --- Renderizado del Nametag Personalizado ---
            RenderManager renderManager = mc.getRenderManager();
            FontRenderer fontRenderer = mc.fontRendererObj;

            GlStateManager.pushMatrix();
            GlStateManager.translate((float) event.x, (float) event.y + entity.height + 0.5F, (float) event.z);
            GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            // Corrección: Usar playerViewX para la rotación vertical correcta
            GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

            float scale = 0.02666667F;
            GlStateManager.scale(-scale, -scale, scale);

            if (entity.isSneaking()) {
                GlStateManager.translate(0.0F, 9.374999F, 0.0F);
            }

            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();

            // --- CORRECCIÓN AQUÍ ---
            // Usar las constantes de GL11 para la función de blend
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            // ------------------------

            // Renderizar el texto con sombra
            fontRenderer.drawStringWithShadow(name, -fontRenderer.getStringWidth(name) / 2, 0, 0xFFFFFFFF); // Blanco

            // Restaurar estado
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            // GlStateManager.enableLighting(); // Opcional, a menudo no se restaura para UI
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); // Resetear color por si acaso
            GlStateManager.popMatrix();
        }
    }
}