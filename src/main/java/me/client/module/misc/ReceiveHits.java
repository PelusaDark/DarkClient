package me.client.module.misc;

import me.client.Dark;
import me.client.module.Category;
import me.client.module.Module;
import me.client.module.util.Util;
import me.client.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer; // Importar EntityPlayer
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ReceiveHits extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private Setting hitsSetting;
    private Setting distanceSetting;
    private int hitCounter;
    // Flag para evitar contar el mismo golpe múltiples veces
    private boolean justHit = false; 
    private boolean Hab;
    public ReceiveHits() {
        super("ReceiveHits", "Se desactiva despues de recibir una cantidad de golpes", Category.MISC);
        this.hitsSetting = new Setting("Hits", this, 1, 1, 4, true);
        this.distanceSetting = new Setting("Distance Reset", this, 3.9, 1, 6, false);
        Dark.instance.settingsManager.rSetting(new Setting("Distance Based", this, false));
             
        Dark.instance.settingsManager.rSetting(this.distanceSetting);
        Dark.instance.settingsManager.rSetting(this.hitsSetting);

        this.hitCounter = 0;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.Hab = false;
        this.hitCounter = 0; // Reiniciar contador al activar
        this.justHit = false; // Reiniciar flag de golpe
        // System.out.println("ReceiveHits Enabled - Hit counter reset"); // Mensaje de depuración
    }


    @SubscribeEvent
public void onClientTick(TickEvent.ClientTickEvent event) {
    // Ejecutar solo al final del tick para evitar doble procesamiento y asegurar que el mundo esté actualizado
    if (event.phase != TickEvent.Phase.END) {
        return;
    }

    // Comprobaciones básicas para evitar NullPointerException
    if (!Util.nullCheck()) {
        return;
    }
    boolean DistB = Dark.instance.settingsManager.getSettingByName(this, "Distance Based").getValBoolean();
        
    if (!Hab && mc.thePlayer.hurtTime > 0 && !justHit) {
        hitCounter++;
        justHit = true; // Marcar que acabamos de contar un golpe

        int hitsLimit = (int) hitsSetting.getValDouble();
        if (hitCounter >= hitsLimit) {

            Hab = true;
            this.hitCounter = 0;
            if(!DistB){
            this.justHit = false; // Reiniciar flag de golpe
            this.setToggled(false);
            }
        }
    } else if (mc.thePlayer.hurtTime == 0) {
        // Si el jugador ya no está herido, resetear el flag para detectar el próximo golpe
        justHit = false;
    }
    
    
    if(DistB){
    EntityPlayer closestPlayer = findClosestPlayer();
    
    float distance = (float) distanceSetting.getValDouble();
    // Verificar si el jugador más cercano es nulo antes de acceder a su distancia
    if (Hab && (closestPlayer == null || mc.thePlayer.getDistanceToEntity(closestPlayer) > distance)) {
        Hab = false;
    }
    }

}

private EntityPlayer findClosestPlayer() {
    EntityPlayer closest = null;
    double minDistanceSq = Double.MAX_VALUE; // Usar distancia al cuadrado para eficiencia

    // Iterar sobre todos los jugadores en el mundo
    for (EntityPlayer player : mc.theWorld.playerEntities) {
        // Ignorar al propio jugador y jugadores muertos
        if (player == mc.thePlayer || player.isDead) {
            continue;
        }

        // Calcular distancia (al cuadrado para evitar sqrt)
        double distanceSq = mc.thePlayer.getDistanceSqToEntity(player);

        // Si este jugador está más cerca que el 'closest' actual
        if (distanceSq < minDistanceSq) {
            minDistanceSq = distanceSq;
            closest = player;
        }
    }
    return closest; // Devuelve el más cercano encontrado o null
}

    public boolean isHab() {
    return Hab;
    }

}