package chestviewer;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathPoint;


@Mod(modid = "chestviewer", name = "chestviewer", version = "2.2-mc1.5.2")
@NetworkMod(clientSideRequired = false, serverSideRequired = true, channels = {"chestviewer"}, packetHandler = PacketHandler.class, connectionHandler = ConnectionHandler.class)
public class ChestViewer {
    public static final String modid = "chestviewer";

    @SidedProxy(clientSide = "chestviewer.ClientProxy", serverSide = "chestviewer.CommonProxy")
    public static CommonProxy proxy;

    public Map<PathPoint, ItemStack[]> inventoryMap = new LinkedHashMap<>();
    public Map<PathPoint, String> inventoryNameMap = new LinkedHashMap<>();

    public boolean enabled = true;

    @Instance("chestviewer")
    public static ChestViewer instance;

    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
    }

    @Init
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }
}