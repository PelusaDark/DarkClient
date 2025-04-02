package me.client.module.misc;

import me.client.Dark;
import me.client.settings.Setting;
import me.client.module.Category;
import me.client.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import me.client.module.util.Util;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.init.Items;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.block.Block;

import net.minecraft.client.entity.EntityPlayerSP; // Import EntityPlayerSP
import net.minecraft.entity.player.EntityPlayer; // Import the base EntityPlayer class

import me.client.module.util.utilities.TimerUtil;
import net.minecraft.world.World;

public class MiniGames extends Module {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private final TimerUtil Cw = new TimerUtil();
    
    private long yellowDyeDeactivationTime = 0; // Tiempo en el que se detectó el tinte amarillo
    private int Delay;
    private boolean isDelayOver;

    //Games cfg
    private boolean wallparty;
    private boolean chairgame;
    private boolean ffall;
    private boolean trafficlight;
    private final TimerUtil chairTimer = new TimerUtil(); // Timer for chair game mounting cooldown
    private boolean wB;
    public MiniGames() {
        super("MiniGames", "Minigames Universocraft", Category.MISC);
        Dark.instance.settingsManager.rSetting(new Setting("WallParty", this, true));
        Dark.instance.settingsManager.rSetting(new Setting("ChairGames", this, true));
        Dark.instance.settingsManager.rSetting(new Setting("FreeFall", this, true));
        Dark.instance.settingsManager.rSetting(new Setting("TrafficLight", this, true));
        Dark.instance.settingsManager.rSetting(new Setting("WaitRedms", this, 1700, 1000, 10000, true));

        
        Cw.setDelay(150);
        chairTimer.setDelay(50); // 0.50 second cooldown for mounting 
    }   


    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != Phase.END || !this.isToggled() || !Util.nullCheck()) {
            return;
        }
        wallparty = Dark.instance.settingsManager.getSettingByName(this, "WallParty").getValBoolean();
        chairgame = Dark.instance.settingsManager.getSettingByName(this, "ChairGames").getValBoolean();
        ffall = Dark.instance.settingsManager.getSettingByName(this, "FreeFall").getValBoolean();
        trafficlight = Dark.instance.settingsManager.getSettingByName(this, "TrafficLight").getValBoolean();
        Delay = (int) Dark.instance.settingsManager.getSettingByName(this, "WaitRedms").getValDouble();
      
        //WallParty
        if(wallparty){
            Wp();
        }
        //ChairGames
        if(chairgame){
            Ch();
        }

        if(trafficlight)
        {
            trafficlight();
        }
        if(ffall){
            freefall();
        }
    }

    //Wallparty
    private void Wp(){
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem == null || !(heldItem.getItem() instanceof ItemBlock)) {
            return;
        }

        ItemBlock itemBlockInHand = (ItemBlock) heldItem.getItem();

        MovingObjectPosition mop = mc.objectMouseOver;
        if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
            IBlockState blockStateLookingAt = mc.theWorld.getBlockState(mop.getBlockPos());
            Block blockLookingAt = blockStateLookingAt.getBlock();

            if (itemBlockInHand.getBlock() == blockLookingAt) {
                // Verificar metadata (color, tipo, etc.)
                int heldItemMetadata = heldItem.getMetadata();
                int blockLookingAtMetadata = blockLookingAt.getMetaFromState(blockStateLookingAt);

                if (heldItemMetadata == blockLookingAtMetadata && Cw.hasCooldownExpired()) {
                    performRightClick();
                }
            }
        }
    }
     
    //ChairGames
    private void Ch() {
    if (mc.thePlayer.isRiding() || !chairTimer.hasCooldownExpired()) {
        return;
    }

    double radius = 12.0D;
    AxisAlignedBB bb = new AxisAlignedBB(
        mc.thePlayer.posX - radius,
        mc.thePlayer.posY - radius,
        mc.thePlayer.posZ - radius,
        mc.thePlayer.posX + radius,
        mc.thePlayer.posY + radius,
        mc.thePlayer.posZ + radius
    );

    EntityMinecart closestMinecart = null;
    double closestDistance = Double.MAX_VALUE;

    // Iterar sobre todas las entidades dentro del eje delimitador alineado
    for (Entity entity : mc.theWorld.getEntitiesWithinAABB(Entity.class, bb)) {
        if (entity != mc.thePlayer && entity.getDistanceToEntity(mc.thePlayer) <= radius) {
            if (entity instanceof EntityMinecart) {
                EntityMinecart minecart = (EntityMinecart) entity;

                // Comprobar que el Minecart no esté siendo montado por otra entidad
                if (minecart.riddenByEntity == null) {
                    double distanceToPlayer = mc.thePlayer.getDistanceToEntity(minecart);

                    // Actualizar el Minecart más cercano
                    if (distanceToPlayer < closestDistance) {
                        closestDistance = distanceToPlayer;
                        closestMinecart = minecart;
                    }
                }
            }
        }
    }

    // Montar el Minecart más cercano, si se encontró uno
    if (closestMinecart != null) {
        mountEntity(closestMinecart);
    }
}

    //TrafficLight
    public void trafficlight() {    
    EntityPlayer player = mc.thePlayer;
    ItemStack heldItem = player.getHeldItem(); // Obtiene el objeto que el jugador tiene en la mano

    GameSettings settings = mc.gameSettings; // Obtiene la configuración del juego para las teclas

    if (heldItem != null) { // Verifica si el jugador tiene un objeto en la mano
        Item item = heldItem.getItem();

        if (item == Items.dye) { // Verifica si el objeto es un tinte
            int metadata = heldItem.getItemDamage(); // Obtiene el metadata del tinte

            if (metadata == 2 || metadata == 10) {
                // Activa la tecla de caminar hacia adelante si el metadata es 2 o 10
                KeyBinding.setKeyBindState(settings.keyBindForward.getKeyCode(), true);
                wB = true;
            } else if (metadata == 1) {
                // Desactiva la tecla de caminar hacia adelante si el metadata es 1 (red dye)
                KeyBinding.setKeyBindState(settings.keyBindForward.getKeyCode(), false);
            } else if (metadata == 11 && wB) {
                // Si el tinte es amarillo (metadata 11), activa la tecla y programa la desactivación
                KeyBinding.setKeyBindState(settings.keyBindForward.getKeyCode(), true);

                // Guarda el tiempo actual en milisegundos cuando se detecta el tinte amarillo
                if (yellowDyeDeactivationTime == 0) {
                    yellowDyeDeactivationTime = System.currentTimeMillis() + Delay; // 3 segundos
                }

                // Verifica si han pasado los 3 segundos
                if (System.currentTimeMillis() >= yellowDyeDeactivationTime) {
                    KeyBinding.setKeyBindState(settings.keyBindForward.getKeyCode(), false); // Desactiva la tecla
                    yellowDyeDeactivationTime = 0; // Reinicia el temporizador
                    wB = false;
                }
                
            }
        }
    } else {
        // Si no hay ningún objeto en la mano, restaura el estado de la tecla al estado físico
        KeyBinding.setKeyBindState(settings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(settings.keyBindForward.getKeyCode()));
        yellowDyeDeactivationTime = 0; // Reinicia el temporizador si no hay objeto en la mano
    }
}

//FreeFall
private void freefall() {
    World world = mc.theWorld;

    double playerX = mc.thePlayer.posX;
    double playerZ = mc.thePlayer.posZ;
    double playerY = mc.thePlayer.posY;

    // Check for water directly below the player
    for (int y = (int) playerY - 1; y > 0; y--) {
        BlockPos blockPos = new BlockPos(playerX, y, playerZ);
        Block block = world.getBlockState(blockPos).getBlock();

        if (block.getMaterial().isLiquid()) {
            // Additional checks to ensure player is centered in the block
            double blockCenterX = blockPos.getX() + 0.5;
            double blockCenterZ = blockPos.getZ() + 0.5;

            double offsetX = Math.abs(playerX - blockCenterX);
            double offsetZ = Math.abs(playerZ - blockCenterZ);

            if (offsetX < 0.25 && offsetZ < 0.25) {
                // Player is aligned with water and is centered

                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);

                mc.thePlayer.addChatMessage(new ChatComponentText("§aEstás alineado y centrado con el agua, caerás seguro.")); // Chat message with green color
            }
            break;
        } else if (!block.isAir(world, blockPos)) {
            break; // Exit the loop.
        }
    }
}


   private void mountEntity(Entity entity) {
        mc.playerController.interactWithEntitySendPacket(mc.thePlayer, entity); //Correct method for 1.8.9
        chairTimer.reset(); // Reset the chair game timer after mounting
    }



    private void performRightClick() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
        KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        isDelayOver = true;
        Cw.reset();
    }

}