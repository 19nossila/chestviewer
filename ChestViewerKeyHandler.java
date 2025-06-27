package chestviewer;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;
import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class ChestViewerKeyHandler extends KeyBindingRegistry.KeyHandler {
  static KeyBinding toggleKeyBinding = new KeyBinding("chestviewer", 25);
  
  public ChestViewerKeyHandler() {
    super(new KeyBinding[] { toggleKeyBinding }, new boolean[] { true });
  }
  
  @Override
  public String getLabel() {
    return "ChestViewerKeyHandler";
  }
  
  @Override
  public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {}
  
  @Override
  public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
    if (tickEnd && kb.equals(toggleKeyBinding)) {
      Minecraft mc = Minecraft.func_71410_x();
      if (mc.field_71462_r == null && !mc.field_71456_v.func_73827_b().func_73760_d()) {
        ChestViewer.instance.enabled = !ChestViewer.instance.enabled;
        mc.field_71439_g.func_71035_c("ChestViewer: " + (ChestViewer.instance.enabled ? "ON" : "OFF"));
      } 
    } 
  }
  
  @Override
  public EnumSet<TickType> ticks() {
    return EnumSet.of(TickType.CLIENT);
  }
}