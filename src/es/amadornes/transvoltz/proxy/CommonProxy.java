package es.amadornes.transvoltz.proxy;

import java.io.File;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.registry.GameRegistry;
import es.amadornes.transvoltz.block.BlockAntenna;
import es.amadornes.transvoltz.block.BlockTransceiver;
import es.amadornes.transvoltz.item.ItemBlockTransceiver;
import es.amadornes.transvoltz.item.ItemUpgrade;
import es.amadornes.transvoltz.lib.Blocks;
import es.amadornes.transvoltz.lib.Ids;
import es.amadornes.transvoltz.lib.Items;
import es.amadornes.transvoltz.tileentity.TileEntityAntenna;
import es.amadornes.transvoltz.tileentity.TileEntityTransceiver;

public class CommonProxy {

	public void loadConfig(){
		Configuration conf = new Configuration(new File("config/amadornes/transvoltz.cfg"));
		conf.load();

		Ids.transceiver = conf.get("blocks", "transceiver", 710).getInt();
		Ids.antenna = conf.get("blocks", "antenna", 711).getInt();
		
		Ids.upgrade = conf.get("items", "upgrade", 14250).getInt();
		
		conf.save();
	}
	
	public void registerBlocks(){
		Blocks.transceiver = new BlockTransceiver();
		GameRegistry.registerBlock(Blocks.transceiver, ItemBlockTransceiver.class, Blocks.transceiver.getUnlocalizedName());
		
		Blocks.antenna = new BlockAntenna();
		GameRegistry.registerBlock(Blocks.antenna, Blocks.antenna.getUnlocalizedName());//FIXME ADD ITEMBLOCK
	}
	
	public void registerTileEntities(){
		GameRegistry.registerTileEntity(TileEntityTransceiver.class, Blocks.transceiver.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityAntenna.class, Blocks.antenna.getUnlocalizedName());
	}
	
	public void registerItems(){
		Items.upgrade = new ItemUpgrade();
		GameRegistry.registerItem(Items.upgrade, Items.upgrade.getUnlocalizedName());
	}
	
	public void registerRecipes(){
		
	}
	
	public void registerRenders(){}
	
}
