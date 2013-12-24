package es.amadornes.transvoltz.proxy;

import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import es.amadornes.transvoltz.lib.Ids;
import es.amadornes.transvoltz.render.RenderAntenna;
import es.amadornes.transvoltz.render.RenderTransceiver;
import es.amadornes.transvoltz.render.RenderUpgrade;
import es.amadornes.transvoltz.tileentity.TileEntityAntenna;
import es.amadornes.transvoltz.tileentity.TileEntityTransceiver;

public class ClientProxy extends CommonProxy {

	public static int renderIdTransceiver = 0;
	public static int renderIdAntenna = 0;
	
	@Override
	public void registerRenders() {
		registerBlockRenders();
		registerItemRenders();
	}
	
	public void registerBlockRenders(){
		renderIdTransceiver = RenderingRegistry.getNextAvailableRenderId();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTransceiver.class, new RenderTransceiver());
		renderIdAntenna = RenderingRegistry.getNextAvailableRenderId();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAntenna.class, new RenderAntenna());
	}
	
	public void registerItemRenders(){
		MinecraftForgeClient.registerItemRenderer(Ids.transceiver, new RenderTransceiver());
		MinecraftForgeClient.registerItemRenderer(Ids.upgrade, new RenderUpgrade());
		MinecraftForgeClient.registerItemRenderer(Ids.antenna, new RenderAntenna());
	}
	
}
