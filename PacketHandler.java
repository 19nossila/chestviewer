package chestviewer;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;

public class PacketHandler implements IPacketHandler {
  public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
    if (!packet.channel.equals("chestviewer")) // Deobfuscated -> field_73630_a
      return; 
    
    DataInputStream stream = new DataInputStream(new ByteArrayInputStream(packet.data)); // Deobfuscated -> field_73629_c
    try {
      String command = stream.readUTF();
      
      if (command.endsWith("_REQUEST")) {
    	  EntityPlayerMP entityPlayer = (EntityPlayerMP)player;
    	  if (command.equals("ENTITY_REQUEST")) {
              int entityId = stream.readInt();
              Entity entity = entityPlayer.worldObj.getEntityByID(entityId);
              if (entity instanceof EntityLiving) {
                  EntityLiving livingEntity = (EntityLiving) entity;
                  ItemStack[] equipment;
                  String entityName = livingEntity.getEntityName();

                  if (livingEntity instanceof EntityPlayer) {
                      EntityPlayer targetPlayer = (EntityPlayer) livingEntity;
                      
                      List<ItemStack> items = new ArrayList<>();
                      items.addAll(Arrays.asList(targetPlayer.inventory.mainInventory));
                      items.addAll(Arrays.asList(targetPlayer.inventory.armorInventory));
                      equipment = items.toArray(new ItemStack[0]);
                  } else {
                      // Pega equipamentos de mobs
                      equipment = new ItemStack[5];
                      for(int i = 0; i < 5; i++){
                          equipment[i] = livingEntity.getCurrentItemOrArmor(i);
                      }
                  }
                  sendEntityInventoryResponsePacket(manager, entityId, equipment, entityName);
              }
              return;
          }
    	
        int x = stream.readInt();
        int y = stream.readInt();
        int z = stream.readInt();
        
        EntityPlayerMP entityPlayer1 = (EntityPlayerMP)player;
        // Deobfuscated -> worldObj, getBlockTileEntity
        TileEntity tileEntity = entityPlayer1.worldObj.getBlockTileEntity(x, y, z);
        
        if (tileEntity == null)
          return; 
        
        ItemStack[] itemStacks = null;
        String inventoryName = "Inventory";
        String str1;
        switch ((str1 = command).hashCode()) {
        case -1965841647: // Hash para "CHEST_REQUEST"
            if (!str1.equals("CHEST_REQUEST"))
              break; 
            
            if (tileEntity instanceof TileEntityChest) {
              TileEntityChest chest = (TileEntityChest)tileEntity;

              
              TileEntityChest[] chests = { 
                  chest.adjacentChestZNeg, 
                  chest.adjacentChestXPos, 
                  chest, 
                  chest.adjacentChestXNeg, 
                  chest.adjacentChestZPosition 
              };
              
              
              List<ItemStack> stacks = new ArrayList<>();
              for (TileEntityChest c : chests) {
                if (c != null) {
                  for (int k = 0; k < c.getSizeInventory(); k++) {
                    stacks.add(c.getStackInSlot(k));
                  }
                }
              }
              
              
              itemStacks = stacks.toArray(new ItemStack[0]);
              inventoryName = chest.getInvName();
            } 
            break;
          case -1781243543:
            if (!str1.equals("ENDERCHEST_REQUEST"))
              break; 
            
            if (tileEntity instanceof net.minecraft.tileentity.TileEntityEnderChest) {
              // Deobfuscated -> getInventoryEnderChest
              InventoryEnderChest enderInventory = entityPlayer1.getInventoryEnderChest(); 
              itemStacks = new ItemStack[enderInventory.getSizeInventory()]; // Deobfuscated -> func_70302_i_
              for (int i = 0; i < itemStacks.length; ) {
                itemStacks[i] = enderInventory.getStackInSlot(i); // Deobfuscated -> func_70301_a
                i++;
              } 
              inventoryName = enderInventory.getInvName(); // Deobfuscated -> func_70303_b
            } 
            break;
          case -1272753556:
            if (!str1.equals("INVENTORY_REQUEST"))
              break; 
            
            if (tileEntity instanceof IInventory && !(tileEntity instanceof TileEntityChest) && !(tileEntity instanceof TileEntityFurnace) && !(tileEntity instanceof net.minecraft.tileentity.TileEntityEnderChest) && !(tileEntity instanceof TileEntityHopper)) {
              IInventory inventory = (IInventory)tileEntity;
              inventoryName = inventory.getInvName(); // Deobfuscated -> func_70303_b
              int inventorySize = inventory.getSizeInventory(); // Deobfuscated -> func_70302_i_
              itemStacks = new ItemStack[inventorySize];
              
              for (int i = 0; i < inventorySize; i++)
                itemStacks[i] = inventory.getStackInSlot(i); // Deobfuscated -> func_70301_a
            } 
            break;
          case -81688984:
            if (!str1.equals("FURNACE_REQUEST"))
              break; 
            
            if (tileEntity instanceof TileEntityFurnace) {
              TileEntityFurnace furnace = (TileEntityFurnace)tileEntity;
              itemStacks = new ItemStack[furnace.getSizeInventory()]; // Deobfuscated -> func_70302_i_
              for (int i = 0; i < itemStacks.length; ) {
                itemStacks[i] = furnace.getStackInSlot(i); // Deobfuscated -> func_70301_a
                i++;
              } 
              inventoryName = furnace.getInvName(); // Deobfuscated -> func_70303_b
            } 
            break;
          case 1579970916:
            if (!str1.equals("HOPPER_REQUEST"))
              break; 
            
            if (tileEntity instanceof TileEntityHopper) {
              TileEntityHopper hopper = (TileEntityHopper)tileEntity;
              itemStacks = new ItemStack[hopper.getSizeInventory()]; // Deobfuscated -> func_70302_i_
              for (int i = 0; i < itemStacks.length; ) {
                itemStacks[i] = hopper.getStackInSlot(i); // Deobfuscated -> func_70301_a
                i++;
              } 
              inventoryName = hopper.getInvName(); // Deobfuscated -> func_70303_b
            } 
            break;
        } 
        
        if (itemStacks != null)
          sendInventoryResponsePacket(manager, x, y, z, itemStacks, inventoryName); 
        
      } else if (command.endsWith("_RESPONSE")) {
    	if (command.equals("ENTITY_INVENTORY_RESPONSE")) {
          int entityId = stream.readInt();
          String inventoryName = stream.readUTF();
          int invSize = stream.readInt();
          ItemStack[] itemStacks = new ItemStack[invSize];
          for (int i = 0; i < itemStacks.length; i++)
          itemStacks[i] = Packet.readItemStack(stream);
              
          if (ChestViewer.instance != null) {
            ChestViewer.instance.entityInventoryMap.put(entityId, itemStacks);
            ChestViewer.instance.entityInventoryNameMap.put(entityId, inventoryName);
          }
          return;
        }
        int x = stream.readInt();
        int y = stream.readInt();
        int z = stream.readInt();
        String inventoryName = stream.readUTF();
        int invSize = stream.readInt();
        ItemStack[] itemStacks = new ItemStack[invSize];
        for (int i = 0; i < itemStacks.length; i++)
          itemStacks[i] = Packet.readItemStack(stream); // Deobfuscated -> func_73276_c
        
        if (ChestViewer.instance != null) {
          PathPoint point = new PathPoint(x, y, z);
          ChestViewer.instance.inventoryMap.put(point, itemStacks);
          ChestViewer.instance.inventoryNameMap.put(point, inventoryName);
        } 
      } 
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  private void sendEntityInventoryResponsePacket(INetworkManager manager, int entityId, ItemStack[] itemStacks, String name) {
	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	DataOutputStream stream = new DataOutputStream(bytes);
	try {
	  stream.writeUTF("ENTITY_INVENTORY_RESPONSE");
	  stream.writeInt(entityId);
	  stream.writeUTF(name);
	  stream.writeInt(itemStacks.length);
	  for (ItemStack is : itemStacks) {
	     Packet.writeItemStack(is, stream);
	  } 
	  Packet250CustomPayload packet = new Packet250CustomPayload("chestviewer", bytes.toByteArray());
	  manager.addToSendQueue(packet);
	} catch (IOException e) {
	    e.printStackTrace();
	  } 
  }
  
  private void sendInventoryResponsePacket(INetworkManager manager, int x, int y, int z, ItemStack[] itemStacks, String name) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    DataOutputStream stream = new DataOutputStream(bytes);
    try {
      stream.writeUTF("INVENTORY_RESPONSE");
      stream.writeInt(x);
      stream.writeInt(y);
      stream.writeInt(z);
      stream.writeUTF(name);
      stream.writeInt(itemStacks.length);
      byte b;
      int i;
      ItemStack[] arrayOfItemStack;
      for (i = (arrayOfItemStack = itemStacks).length, b = 0; b < i; ) {
        ItemStack is = arrayOfItemStack[b];
        Packet.writeItemStack(is, stream); // Deobfuscated -> func_73270_a
        b++;
      } 
      Packet250CustomPayload packet = new Packet250CustomPayload();
      packet.channel = "chestviewer"; // Deobfuscated -> field_73630_a
      packet.data = bytes.toByteArray(); // Deobfuscated -> field_73629_c
      packet.length = packet.data.length; // Deobfuscated -> field_73628_b
      manager.addToSendQueue((Packet)packet); // Deobfuscated -> func_74429_a
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}