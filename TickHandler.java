package chestviewer;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.EnumMovingObjectType;

public class TickHandler implements ITickHandler {

    private int tickCounter = 0;
    private static final int PACKET_DELAY = 5;

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {}

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        tickCounter++;
        if (tickCounter < PACKET_DELAY) {
            return;
        }
        tickCounter = 0;

        Minecraft mc = Minecraft.func_71410_x();
        ChestViewer mod = ChestViewer.instance;

        if (mc.field_71476_x == null || mc.field_71441_e == null || mod == null || !mod.enabled) {
            return;
        }
        
        MovingObjectPosition target = mc.field_71476_x;

        if (target.field_72313_a == EnumMovingObjectType.TILE) {
            int x = target.field_72311_b;
            int y = target.field_72312_c;
            int z = target.field_72309_d;

            TileEntity tileEntity = mc.field_71441_e.func_72796_p(x, y, z);
            if (tileEntity == null) return;
            
            if (tileEntity instanceof TileEntityChest) {
                sendInventoryRequestPacket("CHEST_REQUEST", x, y, z);
            } else if (tileEntity instanceof TileEntityFurnace) {
                sendInventoryRequestPacket("FURNACE_REQUEST", x, y, z);
            } else if (tileEntity instanceof TileEntityHopper) {
                sendInventoryRequestPacket("HOPPER_REQUEST", x, y, z);
            } else if (tileEntity instanceof TileEntityEnderChest) {
                sendInventoryRequestPacket("ENDERCHEST_REQUEST", x, y, z);
            } else if (tileEntity instanceof IInventory) {
                sendInventoryRequestPacket("INVENTORY_REQUEST", x, y, z);
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
            packet.field_73630_a = "chestviewer";
            packet.field_73629_c = bytes.toByteArray();
            packet.field_73628_b = packet.field_73629_c.length;

            Minecraft.func_71410_x().field_71439_g.field_71174_a.func_72552_c(packet);
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