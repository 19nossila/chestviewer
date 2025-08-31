package chestviewer;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
  public void init() {
    TickRegistry.registerTickHandler(new TickHandler(), Side.CLIENT);

    MinecraftForge.EVENT_BUS.register(new RenderOverlayHandler());

    KeyBindingRegistry.registerKeyBinding(new ChestViewerKeyHandler());
  }
}