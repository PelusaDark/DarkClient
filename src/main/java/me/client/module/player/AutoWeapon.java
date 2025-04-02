package me.client.module.player;

import com.google.common.collect.Multimap; // Necesario para getDamage
import me.client.Dark; // Asumiendo que esta clase existe
import me.client.module.Category;
import me.client.module.Module;
import me.client.module.util.Util;
import me.client.settings.Setting; // Asumiendo que lo usas en otro lugar
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes; // Necesario para getDamage
import net.minecraft.entity.ai.attributes.AttributeModifier; // Necesario para getDamage
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword; // Importación necesaria
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent; // Importación correcta para el evento
import org.lwjgl.input.Mouse; // Asumiendo que lo usas en otro lugar

import java.util.Map; // Necesario para getDamage

public class AutoWeapon extends Module {
    // mc ya no necesita ser estático si los métodos que lo usan no lo son
    Minecraft mc = Minecraft.getMinecraft();
    private boolean swap;
    private int previousSlot = -1;
    private int ticksHovered;
    private Entity currentEntity;

    public AutoWeapon() {
        super("AutoWeapon", "Automatically switches to the best weapon when hovering over an entity.", Category.PLAYER);
        Dark.instance.settingsManager.rSetting(new Setting("Swap to previous slot", this, false));
        // Asegúrate de registrar el evento en tu clase principal o donde corresponda:
        // MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        // Resetea el estado al activar el módulo
        resetVariables();
        previousSlot = -1; 
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Vuelve al slot original al desactivar si es necesario
        resetSlot();
        resetVariables();
    }

    // Evento correcto y chequeo de fase
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Es buena práctica comprobar la fase del evento
        if (event.phase == TickEvent.Phase.END) { // O .START dependiendo de cuándo quieras ejecutar la lógica

            if (!this.isToggled() || !Util.nullCheck()) {
                // Si no estamos en el juego o el módulo está apagado, intenta volver al slot anterior y resetea
                resetSlot();
                resetVariables();
                return;
            }
            swap = Dark.instance.settingsManager.getSettingByName(this, "Swap to previous slot").getValBoolean();

            Entity hoveredEntity = mc.objectMouseOver != null ? mc.objectMouseOver.entityHit : null;

            // Verifica si la entidad es válida y no es el propio jugador
            if (!(hoveredEntity instanceof EntityLivingBase) || hoveredEntity == mc.thePlayer) {
                // Si no estamos mirando a una entidad válida, vuelve al slot anterior y resetea
                resetSlot();
                resetVariables();
                return;
            }

            // Actualiza contador de ticks mirando a la misma entidad
            ticksHovered = hoveredEntity.equals(currentEntity) ? ticksHovered + 1 : 0;
            currentEntity = hoveredEntity;

            // Si hemos mirado a la entidad por al menos 1 tick (o el umbral que prefieras)
            if (ticksHovered > 0) { // Puedes aumentar este número si quieres un pequeño delay
                int bestWeaponSlot = getWeapon(); // Encuentra la mejor arma

                if (bestWeaponSlot != -1 && mc.thePlayer.inventory.currentItem != bestWeaponSlot) {
                    // Si encontramos un arma y no la tenemos equipada...
                    if (previousSlot == -1) {
                        // Guarda el slot actual *antes* de cambiar
                        previousSlot = getCurrentSlot();
                    }
                    // Cambia al slot del arma
                    setSlot(bestWeaponSlot);
                } else if (bestWeaponSlot == -1) {
                    // Si no se encontró un arma adecuada, vuelve al slot anterior si es necesario
                     resetSlot();
                     resetVariables(); // También resetea las variables de seguimiento
                }
            } else {
                // Si dejamos de mirar a la entidad (ticksHovered == 0), vuelve al slot anterior
                 resetSlot();
                 // No necesariamente resetear 'currentEntity' aquí, se hace al inicio del tick
            }
        }
    }

    // --- Métodos Implementados ---

    /**
     * Cambia el slot de la hotbar del jugador.
     * @param slot El índice del slot (0-8).
     */
    private void setSlot(int slot) {
        if (slot >= 0 && slot < 9) {
             mc.thePlayer.inventory.currentItem = slot;
            // La lógica de cooldown necesitaría una variable de tiempo y comprobación
            // coolDown.start(); // Necesita implementación (ej. usando System.currentTimeMillis())
        }
    }

    /**
     * Vuelve al slot guardado si existe uno.
     */
    private void resetSlot() {

        if (previousSlot == -1 || !swap) {
          return;
        }
            setSlot(previousSlot);
            previousSlot = -1; // Resetea el slot guardado para no volver a cambiar involuntariamente
        
    }

    /**
     * Resetea las variables de seguimiento de la entidad.
     */
    private void resetVariables() {
        ticksHovered = 0;
        currentEntity = null;
    }

    /**
     * Obtiene el slot actual del jugador.
     * @return El índice del slot actual (0-8).
     */
    private int getCurrentSlot() {
        // Ya no necesita ser static
        return mc.thePlayer.inventory.currentItem;
    }

    /**
     * Encuentra el slot con la mejor espada en la hotbar.
     * @return El índice del slot (0-8) con la mejor espada, o -1 si no hay espadas.
     */
    private int getWeapon() {
        // Ya no necesita ser static
        int bestWeaponSlot = -1;
        double maxDamage = 0.0;

        for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() instanceof ItemSword) {
                double currentDamage = getDamage(itemStack); // Obtiene el daño del item
                // Podrías añadir lógica para encantamientos aquí (ej. Sharpness)

                if (currentDamage > maxDamage) {
                    maxDamage = currentDamage;
                    bestWeaponSlot = i;
                }
            }
            // Podrías añadir soporte para hachas u otras armas aquí
        }
        return bestWeaponSlot;
    }

    /**
     * Calcula el daño base de un ItemStack (principalmente para espadas).
     * @param stack El ItemStack del arma.
     * @return El daño base del arma.
     */
    private double getDamage(ItemStack stack) {
        // Ya no necesita ser static
         if (stack == null) {
             return 0.0;
         }

        Multimap<String, AttributeModifier> attributeModifiers = stack.getAttributeModifiers();

        if (attributeModifiers != null && !attributeModifiers.isEmpty()) {
            // Busca el modificador de daño de ataque genérico
             for (Map.Entry<String, AttributeModifier> entry : attributeModifiers.entries()) {
                 if (entry.getKey().equals(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName())) {
                     // El modificador base ya incluye el daño base del item + cualquier bono directo
                     // Sumamos 1.0 porque la mano vacía hace 1 de daño
                    AttributeModifier modifier = entry.getValue();
                    // Simple sum operation: 0 means additively, 1 means multiplicatively (base), 2 means multiplicatively (total)
                    if (modifier.getOperation() == 0) { // Operación aditiva usualmente usada por items
                         return modifier.getAmount() + 1.0; // +1 por daño base del jugador
                    }
                    // Podría haber modificadores multiplicativos, pero para daño base de espada suele ser 0
                 }
             }
        }
        
        // Si no se encuentra el atributo (raro para armas vanilla), devuelve 1 (daño base jugador)
        if (stack.getItem() instanceof ItemSword) {
             // Fallback usando el método interno de ItemSword si el atributo falla
             return ((ItemSword) stack.getItem()).getDamageVsEntity() + 1.0f; 
        }

        return 1.0; // Daño base si no es un arma reconocible con atributo
    }
}
