package me.client.module.combat;

import me.client.Dark;
import me.client.module.util.Util;
import me.client.module.Category;
import me.client.module.Module;
import me.client.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class AimAssist extends Module {
    boolean teamsActivated;
    private Minecraft mc;
    private int tru;
    private boolean tre = true;
    /*Recuerda Dar Creditos a PelusaDark*/
    public AimAssist() {
        super("AimAssist", "Ayuda a Apuntar Mejor", Category.COMBAT);  
        this.mc = Minecraft.getMinecraft();
        Dark.instance.settingsManager.rSetting(new Setting("Speed", this, 10.0, 5.0, 25.0, true));
        Dark.instance.settingsManager.rSetting(new Setting("FOV", this, 90, 10.0, 180.0, true));
        Dark.instance.settingsManager.rSetting(new Setting("Distance", this, 4.3, 1.0, 6.0, false));
        


        /*Dark.instance.settingsManager.rSetting(new Setting("Vertical", this, true));
        Dark.instance.settingsManager.rSetting(new Setting("VerticslSpeed", this, 10.0, 10.0, 20.0, true));
        */

        Dark.instance.settingsManager.rSetting(new Setting("Teamscheck", this, true));
        Dark.instance.settingsManager.rSetting(new Setting("OnlySworld", this, false));
        Dark.instance.settingsManager.rSetting(new Setting("OnlyClick", this, false));
    }

    public double entityPosCompare(final Entity ent) {
        return ((this.mc.thePlayer.rotationYaw - rotationUntilTarget(ent)) % 360.0 + 540.0) % 360.0 - 180.0;
    }

    private float rotationUntilTarget(Entity ent) {

        double diffX = ent.posX - this.mc.thePlayer.posX;
        double diffZ = ent.posZ - this.mc.thePlayer.posZ;
        return (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;

    }

    public boolean isFovLargeEnough(final Entity en, float a) {
        final double v = ((this.mc.thePlayer.rotationYaw - rotationUntilTarget(en)) % 360.0 + 540.0) % 360.0 - 180.0;
        return (v > 0.0 && v < a) || (-a < v && v < 0.0);
    }

    @SubscribeEvent
    public void tickEvent(final TickEvent.RenderTickEvent event) {
        if (event.phase != Phase.END) {
            return;
        }
        if(!Util.nullCheck()) return;

        int speed = (int) Dark.instance.settingsManager.getSettingByName(this, "Speed").getValDouble();

        //boolean verticalOpt = Dark.instance.settingsManager.getSettingByName(this, "Vertical").getValBoolean();
        //int speedVertical = (int) Dark.instance.settingsManager.getSettingByName(this, "VerticslSpeed").getValDouble();
        
        boolean onlySworldConfig = Dark.instance.settingsManager.getSettingByName(this, "OnlySworld").getValBoolean();
        teamsActivated = Dark.instance.settingsManager.getSettingByName(this, "Teamscheck").getValBoolean();
        boolean clickonlyoption = Dark.instance.settingsManager.getSettingByName(this, "OnlyClick").getValBoolean();
        

        if ((!onlySworldConfig || isSwordInHand()) && (!clickonlyoption || Mouse.isButtonDown(0))) {
            if (this.tre) {
                
                    this.tre = true;
               
                if (this.tre) {
                    ++this.tru;
                }

                if (this.tru >= 30) {
                    this.tru = 0;
                    return;
                }
                final Entity h = this.ent();
                if (h != null) {
                    final float distanceTo = h.getDistanceToEntity(this.mc.thePlayer);
                    if (entityPosCompare(h) > 1.0 || entityPosCompare(h) < -1.0) {
                        final boolean i = entityPosCompare(h) > 0.0;
                        this.mc.thePlayer.setAngles((float) (i ? (-(Math.abs(entityPosCompare(h)) * (speed / 15.0))) : (Math.abs(entityPosCompare(h)) * speed / 20.0)), 0.0f);
                    }
                }
            }
        }
    }

    private Entity ent() {
        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;
        float fov = (int) Dark.instance.settingsManager.getSettingByName(this, "FOV").getValDouble();
        float distance = (float) Dark.instance.settingsManager.getSettingByName(this, "Distance").getValDouble();
        for (Object obj : this.mc.theWorld.loadedEntityList) {
            if (obj instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) obj;

                if (entity.equals(this.mc.thePlayer)) {
                    continue;
                }
                if (!(entity instanceof EntityPlayer)) {
                    continue; // Si no es EntityPlayer, continua a la siguiente entidad
                }

                if (this.mc.getNetHandler() != null && this.mc.getNetHandler().getPlayerInfo(entity.getUniqueID()) == null) {
                    // System.out.println("Skipping potential NPC: " + targetPlayer.getName()); // For debugging
                    continue; // Skip this entity, likely an NPC
                }
                double distance2 = this.mc.thePlayer.getDistanceToEntity(entity);
                if (distance2 < distance && isFovLargeEnough(entity, fov)) {
                    if (isSameTeam(entity) && teamsActivated) {
                        continue;
                    }
                    if (distance < closestDistance) {
                        closestEntity = entity;
                        closestDistance = distance;
                    }
                }
            }
        }
        return closestEntity;
    }

    private boolean isSwordInHand() {
        if (mc.thePlayer == null || this.mc.thePlayer.getHeldItem() == null) {
            return false;
        }
        return this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    private boolean isSameTeam(Entity entity) {
    if (!(entity instanceof EntityPlayer)) {
        return false;
    }

    EntityPlayer otherPlayer = (EntityPlayer) entity;
    ScorePlayerTeam otherTeam = (ScorePlayerTeam) otherPlayer.getTeam();
    ScorePlayerTeam myTeam = (ScorePlayerTeam) this.mc.thePlayer.getTeam();

    if (myTeam == null || otherTeam == null) {
        return false;
    }
    if (!myTeam.equals(otherTeam)) {
        return false;
    }

    String myTeamColor = myTeam.getColorPrefix();
    String otherTeamColor = otherTeam.getColorPrefix();

    return myTeamColor.equals(otherTeamColor);
}

}
