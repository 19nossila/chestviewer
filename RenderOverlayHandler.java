package chestviewer;

import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.ForgeSubscribe;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class RenderOverlayHandler extends Gui {
  private final RenderItem itemRenderer = new RenderItem();
  

  private static ShortBuffer boxVertex = getBoxVertexBuffer();
  
  private static ShortBuffer getBoxVertexBuffer() {
    short s = 18;
    short[][] v = { { 0, 0 }, { 0, s }, { s, s }, { s, 0 } };
    
    ShortBuffer buf = BufferUtils.createShortBuffer(16);
    for (short k = 0; k < 4; k = (short)(k + 1)) {
      short x = v[k][0];
      short y = v[k][1];
      buf.put(x);
      buf.put(y);
    } 
    buf.flip();
    return buf;
  }
  
  private void renderBox(int x, int y) {
    GL11.glPushMatrix();
    GL11.glTranslatef(x, y, 0.0F);
    GL11.glPointSize(1.0F);
    
    GL11.glColor4f(0.5F, 1.0F, 1.0F, 0.8F);
    GL11.glEnableClientState(32884);
    GL11.glVertexPointer(2, 0, boxVertex);
    GL11.glDrawArrays(2, 0, 4); 
    GL11.glDisableClientState(32884);
    GL11.glPopMatrix();
  }
  
  
  @ForgeSubscribe
  public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
    if (event.type != RenderGameOverlayEvent.ElementType.ALL)
      return; 
    
    Minecraft mc = Minecraft.getMinecraft();
    ChestViewer mod = ChestViewer.instance;
    
    if (mod == null || !mod.enabled || mc.currentScreen != null)
      return; 
    
   
    MovingObjectPosition target = mc.objectMouseOver;
    
   
    if (target == null) {
        return;
    }
    
    
    if (target.typeOfHit == EnumMovingObjectType.TILE) {
      int x = target.blockX;
      int y = target.blockY;
      int z = target.blockZ;
      
      PathPoint point = new PathPoint(x, y, z);
      ItemStack[] itemStacks = mod.inventoryMap.get(point);
      String inventoryName = mod.inventoryNameMap.get(point);
      
      if (itemStacks != null && inventoryName != null) {
        TileEntity te = mc.theWorld.getBlockTileEntity(x, y, z);
        int columns = getColumnCount(te, null);
        renderInventoryContents(event.resolution, mc.fontRenderer, itemStacks, columns, inventoryName);
      } 
    
    
    } else if (target.typeOfHit == EnumMovingObjectType.ENTITY && target.entityHit != null) {
        Entity entity = target.entityHit;
        ItemStack[] itemStacks = mod.entityInventoryMap.get(entity.entityId);
        String inventoryName = mod.entityInventoryNameMap.get(entity.entityId);

        if (itemStacks != null && inventoryName != null) {
            int columns = getColumnCount(null, entity);
            renderInventoryContents(event.resolution, mc.fontRenderer, itemStacks, columns, inventoryName);
        }
    }
    
  }
  
  private void renderInventoryContents(ScaledResolution resolution, FontRenderer fontRenderer, ItemStack[] inventoryItems, int columns, String title) {
    List<ItemStack> filteredItems = new ArrayList<>();
    for (ItemStack stack : inventoryItems) {
      if (stack != null)
        filteredItems.add(stack); 
    } 
    
    if (filteredItems.isEmpty())
      return; 
    
    GL11.glPushAttrib(1048575);
    GL11.glPushMatrix();
    
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    
    int columnNum = columns;
    int rowNum = (filteredItems.size() + columnNum - 1) / columnNum;
    int boxWidth = Math.min(filteredItems.size(), columnNum) * 18 + 20;
    int boxHeight = rowNum * 18 + fontRenderer.FONT_HEIGHT + 13;
    
    int startX = (resolution.getScaledWidth() - boxWidth) / 2;
    int startY = (resolution.getScaledHeight() - boxHeight) / 2;
    
    fontRenderer.drawStringWithShadow(title, startX + 8, startY + 6, 16777215);
    
    int slotsX = startX + 8;
    int slotsY = startY + fontRenderer.FONT_HEIGHT + 10;
    
   
    GL11.glDisable(GL11.GL_TEXTURE_2D); 
    GL11.glDisable(GL11.GL_LIGHTING);  
    for (int i = 0; i < filteredItems.size(); i++) {
        int itemX = slotsX + (i % columnNum) * 18;
        int itemY = slotsY + (i / columnNum) * 18;
        renderBox(itemX, itemY);
    }
    GL11.glEnable(GL11.GL_TEXTURE_2D); 
    
    
    RenderHelper.enableGUIStandardItemLighting();
    GL11.glEnable(32826); 
    
    for (int i = 0; i < filteredItems.size(); i++) {
      int itemX = slotsX + i % columnNum * 18;
      int itemY = slotsY + i / columnNum * 18;
      
      ItemStack currentItem = filteredItems.get(i);
      
      this.itemRenderer.zLevel = 200.0F;
      
      this.itemRenderer.renderItemIntoGUI(fontRenderer, Minecraft.getMinecraft().renderEngine, currentItem, itemX + 1, itemY + 1);
      this.itemRenderer.renderItemOverlayIntoGUI(fontRenderer, Minecraft.getMinecraft().renderEngine, currentItem, itemX + 1, itemY + 1);
      
      this.itemRenderer.zLevel = 0.0F;
    } 
    
    GL11.glDisable(32826);
    RenderHelper.disableStandardItemLighting();
  
    
    GL11.glPopMatrix();
    GL11.glPopAttrib();
  }
  
  private int getColumnCount(TileEntity te, Entity ent) {
	if (te != null) {
	  if (te instanceof net.minecraft.tileentity.TileEntityFurnace)
	    return 3; 
	    if (te instanceof net.minecraft.tileentity.TileEntityHopper)
	      return 5; 
	}
	    
	if (ent != null) {
	  if (ent instanceof EntityPlayer)
	    return 9; 
	return 5;
	}    
	return 9;
  }
}