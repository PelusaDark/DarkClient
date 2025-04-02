package me.client.module.render;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import me.client.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD extends Module {

    public HUD() {
        super("HUD", "", Category.RENDER);
        Dark.instance.settingsManager.rSetting(new Setting("R", this, 255, 0, 255, true));
        Dark.instance.settingsManager.rSetting(new Setting("G", this, 255, 0, 255, true));
        Dark.instance.settingsManager.rSetting(new Setting("B", this, 255, 0, 255, true));
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != Phase.END) {
            return;
        }
        if (mc.thePlayer == null || mc.theWorld == null || Dark.instance.destructed ||
            mc.gameSettings.showDebugInfo || mc.currentScreen != null) {
            return;
        }
        if (!this.isToggled()) return;
        int r = (int) Dark.instance.settingsManager.getSettingByName(this, "R").getValDouble();
        int g = (int) Dark.instance.settingsManager.getSettingByName(this, "G").getValDouble();
        int b = (int) Dark.instance.settingsManager.getSettingByName(this, "B").getValDouble();

        // Calcula el color a partir de los valores R, G, B
        int hudColor = (r << 16) | (g << 8) | b; // Combina RGB en un entero de color

        ScaledResolution sr = new ScaledResolution(mc);
        int y = 2;

        FontRenderer fr = mc.fontRendererObj;
        for (Module mod : Dark.instance.moduleManager.getModuleList()) {
            if (!mod.getName().equalsIgnoreCase("HUD") && mod.isToggled() && mod.visible) {
                String moduleName = mod.getName();
                fr.drawStringWithShadow(
                    moduleName,
                    (float)(sr.getScaledWidth() - fr.getStringWidth(moduleName) - 1),
                    (float)y,
                    hudColor // Aqui se usa el color calculado
                );
                y += fr.FONT_HEIGHT + 2;
            }
        }

    }
}
