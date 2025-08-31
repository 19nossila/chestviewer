package chestviewer;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;

public class TickHandler implements ITickHandler {
  private int tickCounter = 0;
  
  private static final int PACKET_DELAY = 5;
  
  public void tickStart(EnumSet<TickType> type, Object... tickData) {}
  
  public void tickEnd(EnumSet<TickType> type, Object... tickData) {
    this.tickCounter++;
    if (this.tickCounter < 5)
      return; 
    
    this.tickCounter = 0;
    
    Minecraft mc = Minecraft.getMinecraft(); // Deobfuscated -> func_71410_x
    ChestViewer mod = ChestViewer.instance;
    
    // Deobfuscated -> objectMouseOver, theWorld
    if (mc.objectMouseOver == null || mc.theWorld == null || mod == null || !mod.enabled)
      return; 
    
    MovingObjectPosition target = mc.objectMouseOver;
    
    if (target.typeOfHit == EnumMovingObjectType.TILE) { // Deobfuscated -> field_72313_a
      int x = target.blockX; // Deobfuscated -> field_72311_b
      int y = target.blockY; // Deobfuscated -> field_72312_c
      int z = target.blockZ; // Deobfuscated -> field_72309_d
      
      TileEntity tileEntity = mc.theWorld.getBlockTileEntity(x, y, z); // Deobfuscated -> func_72796_p
      if (tileEntity == null)
        return; 
      
      if (tileEntity instanceof net.minecraft.tileentity.TileEntityChest) {
        sendInventoryRequestPacket("CHEST_REQUEST", x, y, z);
      } else if (tileEntity instanceof net.minecraft.tileentity.TileEntityFurnace) {
        sendInventoryRequestPacket("FURNACE_REQUEST", x, y, z);
      } else if (tileEntity instanceof net.minecraft.tileentity.TileEntityHopper) {
        sendInventoryRequestPacket("HOPPER_REQUEST", x, y, z);
      } else if (tileEntity instanceof net.minecraft.tileentity.TileEntityEnderChest) {
        sendInventoryRequestPacket("ENDERCHEST_REQUEST", x, y, z);
      } else if (tileEntity instanceof net.minecraft.inventory.IInventory) {
        sendInventoryRequestPacket("INVENTORY_REQUEST", x, y, z);
      } 
    } else if (target.typeOfHit == EnumMovingObjectType.ENTITY) {
        Entity entity = target.entityHit;
        if (entity instanceof EntityLiving) {
            sendEntityInventoryRequestPacket("ENTITY_REQUEST", entity.entityId);
        }
    }
  }
  
  private void sendInventoryRequestPacket(String command, int x, int y, int z) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    DataOutputStream stream = new DataOutputStream(bytes);
    try {
      stream.writeUTF(command);
      stream.writeInt(x);
      stream.writeInt(y);
      stream.writeInt(z);
      
      Packet250CustomPayload packet = new Packet250CustomPayload();
      packet.channel = "chestviewer"; // Deobfuscated -> field_73630_a
      packet.data = bytes.toByteArray(); // Deobfuscated -> field_73629_c
      packet.length = packet.data.length; // Deobfuscated -> field_73628_b
      
      // Deobfuscated -> getMinecraft, thePlayer, sendQueue, addToSendQueue
      (Minecraft.getMinecraft()).thePlayer.sendQueue.addToSendQueue((Packet)packet);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  private void sendEntityInventoryRequestPacket(String command, int entityId) {
	    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    DataOutputStream stream = new DataOutputStream(bytes);
	    try {
	      stream.writeUTF(command);
	      stream.writeInt(entityId);

	      Packet250CustomPayload packet = new Packet250CustomPayload("chestviewer", bytes.toByteArray());
	      (Minecraft.getMinecraft()).thePlayer.sendQueue.addToSendQueue(packet);
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  }
  
  public EnumSet<TickType> ticks() {
    return EnumSet.of(TickType.CLIENT);
  }
  
  public String getLabel() {
    return "InventoryViewerTickHandler";
  }
}