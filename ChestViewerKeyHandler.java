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
  
  public String getLabel() {
    return "ChestViewerKeyHandler";
  }
  
  public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {}
  
  public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
    if (tickEnd && kb.equals(toggleKeyBinding)) {
      Minecraft mc = Minecraft.getMinecraft(); // Deobfuscated -> func_71410_x
      // Deobfuscated -> currentScreen, ingameGUI, getChatGUI, getSentMessageList (isChatOpen)
      if (mc.currentScreen == null) { 
        ChestViewer.instance.enabled = !ChestViewer.instance.enabled;
        // Deobfuscated -> thePlayer, addChatMessage
        mc.thePlayer.addChatMessage("ChestViewer: " + (ChestViewer.instance.enabled ? "ON" : "OFF"));
      } 
    } 
  }
  
  public EnumSet<TickType> ticks() {
    return EnumSet.of(TickType.CLIENT);
  }
}