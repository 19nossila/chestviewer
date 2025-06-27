package chestviewer;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;

public class PacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        if (!packet.field_73630_a.equals("chestviewer")) {
            return;
        }

        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(packet.field_73629_c));
        try {
            String command = stream.readUTF();

            
            if (command.endsWith("_REQUEST")) {
                int x = stream.readInt();
                int y = stream.readInt();
                int z = stream.readInt();

                EntityPlayerMP entityPlayer = (EntityPlayerMP) player;
                TileEntity tileEntity = entityPlayer.field_70170_p.func_72796_p(x, y, z);

                if (tileEntity == null) return;

                ItemStack[] itemStacks = null;
                String inventoryName = "Inventory";

                switch (command) {
                    case "CHEST_REQUEST":
                        if (tileEntity instanceof TileEntityChest) {
                            TileEntityChest chest = (TileEntityChest) tileEntity;
                            TileEntityChest[] chests = { chest.field_70421_d, chest.field_70423_b, chest, chest.field_70424_c, chest.field_70422_e };
                            int invSize = 0;
                            for (TileEntityChest c : chests) if (c != null) invSize += c.func_70302_i_();
                            itemStacks = new ItemStack[invSize];
                            int index = 0;
                            for (TileEntityChest c : chests) {
                                if (c != null) {
                                    for (int i = 0; i < c.func_70302_i_(); i++) itemStacks[index++] = c.func_70301_a(i);
                                }
                            }
                            inventoryName = ((IInventory)chest).func_70303_b();
                        }
                        break;

                    case "FURNACE_REQUEST":
                        if (tileEntity instanceof TileEntityFurnace) {
                            TileEntityFurnace furnace = (TileEntityFurnace) tileEntity;
                            itemStacks = new ItemStack[furnace.func_70302_i_()];
                            for (int i = 0; i < itemStacks.length; i++) itemStacks[i] = furnace.func_70301_a(i);
                            inventoryName = furnace.func_70303_b();
                        }
                        break;
                        
                    case "HOPPER_REQUEST":
                        if (tileEntity instanceof TileEntityHopper) {
                        	TileEntityHopper hopper = (TileEntityHopper) tileEntity;
                            itemStacks = new ItemStack[hopper.func_70302_i_()];
                            for (int i = 0; i < itemStacks.length; i++) itemStacks[i] = hopper.func_70301_a(i);
                            inventoryName = hopper.func_70303_b();
                        }
                        break;
                        
                    case "ENDERCHEST_REQUEST":
                        if (tileEntity instanceof TileEntityEnderChest) {
                            InventoryEnderChest enderInventory = entityPlayer.func_71005_bN();
                            itemStacks = new ItemStack[enderInventory.func_70302_i_()];
                            for (int i = 0; i < itemStacks.length; i++) itemStacks[i] = enderInventory.func_70301_a(i);
                            inventoryName = enderInventory.func_70303_b();
                        }
                        break;

                    case "INVENTORY_REQUEST":
                        
                        if (tileEntity instanceof IInventory && !(tileEntity instanceof TileEntityChest) && !(tileEntity instanceof TileEntityFurnace) && !(tileEntity instanceof TileEntityEnderChest) && !(tileEntity instanceof TileEntityHopper)) {
                        	IInventory inventory = (IInventory) tileEntity;
                            inventoryName = inventory.func_70303_b();
                            int inventorySize = inventory.func_70302_i_();
                            itemStacks = new ItemStack[inventorySize];

                            
                            for (int i = 0; i < inventorySize; i++) {
                                itemStacks[i] = inventory.func_70301_a(i);
                            }
                        }
                        break;
                }

                if (itemStacks != null) {
                    sendInventoryResponsePacket(manager, x, y, z, itemStacks, inventoryName);
                }
            }
            
            else if (command.equals("INVENTORY_RESPONSE")) {
                int x = stream.readInt();
                int y = stream.readInt();
                int z = stream.readInt();
                String inventoryName = stream.readUTF();
                int invSize = stream.readInt();
                ItemStack[] itemStacks = new ItemStack[invSize];
                for (int i = 0; i < itemStacks.length; i++) {
                    itemStacks[i] = Packet.func_73276_c(stream);
                }
                
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
            for (ItemStack is : itemStacks) {
                Packet.func_73270_a(is, stream);
            }
            Packet250CustomPayload packet = new Packet250CustomPayload();
            packet.field_73630_a = "chestviewer";
            packet.field_73629_c = bytes.toByteArray();
            packet.field_73628_b = packet.field_73629_c.length;
            manager.func_74429_a(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}