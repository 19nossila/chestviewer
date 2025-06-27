package chestviewer;

import net.minecraftforge.event.ForgeSubscribe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import org.lwjgl.opengl.GL11;

public class RenderOverlayHandler extends Gui {

    private final RenderItem itemRenderer = new RenderItem();
    private static final String INVENTORY_BACKGROUND_PATH = "/mods/chestviewer/textures/gui/inventory_bg.png";

    @ForgeSubscribe
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.func_71410_x();
        ChestViewer mod = ChestViewer.instance;

        if (mod == null || !mod.enabled || mc.field_71462_r != null || mc.field_71456_v.func_73827_b().func_73760_d()) {
            return;
        }

        MovingObjectPosition target = mc.field_71476_x;
        if (target != null && target.field_72313_a == EnumMovingObjectType.TILE) {
            int x = target.field_72311_b;
            int y = target.field_72312_c;
            int z = target.field_72309_d;

            PathPoint point = new PathPoint(x, y, z);
            ItemStack[] itemStacks = mod.inventoryMap.get(point);
            String inventoryName = mod.inventoryNameMap.get(point);

            if (itemStacks != null && inventoryName != null) {
                TileEntity te = mc.field_71441_e.func_72796_p(x, y, z);
                int columns = getColumnCount(te);
                renderInventoryContents(event.resolution, mc.field_71466_p, itemStacks, columns, inventoryName);
            }
        }
    }

    private void renderInventoryContents(ScaledResolution resolution, FontRenderer fontRenderer, ItemStack[] inventoryItems, int columns, String title) {
        List<ItemStack> filteredItems = new ArrayList<>();
        for (ItemStack stack : inventoryItems) {
            if (stack != null) {
                filteredItems.add(stack);
            }
        }

        if (filteredItems.isEmpty()) {
            return;
        }

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int columnNum = columns;
        int rowNum = (filteredItems.size() + columnNum - 1) / columnNum;
        int boxWidth = Math.min(filteredItems.size(), columnNum) * 18 + 16;
        int boxHeight = rowNum * 18 + fontRenderer.field_78288_b + 12;

        int startX = (resolution.func_78326_a() - boxWidth) / 2;
        int startY = (resolution.func_78328_b() - boxHeight) / 2;

        fontRenderer.func_78276_b(title, startX + 8, startY + 6, 0xFFFFFF);

        int slotsAreaX = startX + 7; // Coordenadas da área dos itens
        int slotsAreaY = startY + fontRenderer.field_78288_b + 9;
        int slotsAreaWidth = boxWidth - 14;
        int slotsAreaHeight = boxHeight - fontRenderer.field_78288_b - 10;

        Minecraft.func_71410_x().field_71446_o.func_98187_b(INVENTORY_BACKGROUND_PATH);
        Tessellator tessellator = Tessellator.field_78398_a;
        tessellator.func_78382_b();
        tessellator.func_78374_a((double)slotsAreaX, (double)slotsAreaY + slotsAreaHeight, 0.0D, 0.0D, 1.0D);
        tessellator.func_78374_a((double)slotsAreaX + slotsAreaWidth, (double)slotsAreaY + slotsAreaHeight, 0.0D, 1.0D, 1.0D);
        tessellator.func_78374_a((double)slotsAreaX + slotsAreaWidth, (double)slotsAreaY, 0.0D, 1.0D, 0.0D);
        tessellator.func_78374_a((double)slotsAreaX, (double)slotsAreaY, 0.0D, 0.0D, 0.0D);
        tessellator.func_78381_a();


        RenderHelper.func_74520_c();
        GL11.glEnable(32826);
        
        int slotsX = startX + 8;
        int slotsY = startY + fontRenderer.field_78288_b + 10;
        
        for (int i = 0; i < filteredItems.size(); i++) {
            int itemX = slotsX + (i % columnNum) * 18;
            int itemY = slotsY + (i / columnNum) * 18;
            
            ItemStack currentItem = filteredItems.get(i);
            
            itemRenderer.field_77023_b = 200.0F;
            itemRenderer.func_82406_b(fontRenderer, Minecraft.func_71410_x().field_71446_o, currentItem, itemX, itemY);
            itemRenderer.func_77021_b(fontRenderer, Minecraft.func_71410_x().field_71446_o, currentItem, itemX, itemY);
            itemRenderer.field_77023_b = 0.0F;
        }
        
        GL11.glDisable(32826);
        RenderHelper.func_74518_a();
        
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
    

    private int getColumnCount(TileEntity te) {
        if (te instanceof net.minecraft.tileentity.TileEntityFurnace) return 3;
        if (te instanceof net.minecraft.tileentity.TileEntityHopper) return 5;
        return 9;
    }
}