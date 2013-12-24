package es.amadornes.transvoltz.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import es.amadornes.transvoltz.lib.Ids;
import es.amadornes.transvoltz.lib.Items;
import es.amadornes.transvoltz.lib.ModInfo;
import es.amadornes.transvoltz.proxy.ClientProxy;
import es.amadornes.transvoltz.tileentity.TileEntityTransceiver;
import es.amadornes.transvoltz.tileentity.TileEntityTransceiver.UpgradeType;

public class BlockTransceiver extends BlockContainer {

	public BlockTransceiver() {
		super(Ids.transceiver, Material.iron);
		setUnlocalizedName(ModInfo.MOD_ID + ".transceiver");
		setHardness(5F);
		setResistance(5F);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityTransceiver();
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public Icon getIcon(int meta, int side) {
		return null;
	}
	
	@Override
	public int getRenderType() {
		return ClientProxy.renderIdTransceiver;
	}
	
	@Override
	public void onBlockPlacedBy(World w, int x, int y, int z, EntityLivingBase entity, ItemStack item) {
		TileEntityTransceiver core = (TileEntityTransceiver) w.getBlockTileEntity(x, y, z);
		core.setType(item.getItemDamage());
		super.onBlockPlacedBy(w, x, y, z, entity, item);
	}
	
	@Override
	public int onBlockPlaced(World w, int x, int y, int z, int side, float hitx, float hity, float hitz, int meta) {
		return side;
	}
	
	@Override
	public void onBlockPreDestroy(World w, int x, int y, int z, int md) {
		TileEntityTransceiver core = (TileEntityTransceiver) w.getBlockTileEntity(x, y, z);
		ForgeDirection[] faces = core.determineUpgradableFaces();
		if(faces != null){
			for(ForgeDirection fd : faces){
				ItemStack upgrade = core.getUpgradeOnSide(fd);
				if(upgrade != null){
					EntityItem entityitem = new EntityItem(w, (double)((float)x + 0.5 + fd.offsetX), (double)((float)y + 0.5 + fd.offsetY), (double)((float)z + 0.5 + fd.offsetZ), upgrade);
					
					core.removeUpgradeOnSide(fd);
					
		            float f3 = 0.05F;
		            entityitem.motionY = (double)((float)new Random().nextGaussian() * f3 + 0.2F);
		            w.spawnEntityInWorld(entityitem);
				}
			}
		}
	}
	
	@Override
	public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		ItemStack item = player.inventory.getCurrentItem();
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			if(item != null){
				if(item.getItem() == Items.upgrade){
					if(item.getItemDamage() > 0){
						TileEntityTransceiver core = (TileEntityTransceiver) w.getBlockTileEntity(x, y, z);
						boolean can = core.installUpgrade(UpgradeType.getFromDamage(item.getItemDamage()), ForgeDirection.getOrientation(side));
						if(can){
							core.setUpgrade(UpgradeType.getFromDamage(item.getItemDamage()), ForgeDirection.getOrientation(side));
							if(!player.capabilities.isCreativeMode){
								if(item.stackSize - 1 == 0){
									player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
								}else{
									item.stackSize--;
								}
							}
						}
						return can;
					}
				}
			}else{
				if(player.isSneaking()){
					TileEntityTransceiver core = (TileEntityTransceiver) w.getBlockTileEntity(x, y, z);
					ForgeDirection fd = ForgeDirection.getOrientation(side);
					ItemStack upgrade = core.getUpgradeOnSide(fd);
					if(upgrade != null){
						EntityItem entityitem = new EntityItem(w, (double)((float)x + 0.5 + fd.offsetX), (double)((float)y + 0.5 + fd.offsetY), (double)((float)z + 0.5 + fd.offsetZ), upgrade);
						
						core.removeUpgradeOnSide(fd);
						
		                float f3 = 0.05F;
		                entityitem.motionY = (double)((float)new Random().nextGaussian() * f3 + 0.2F);
		                w.spawnEntityInWorld(entityitem);
		                return true;
					}
				}
			}
		}
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
			if(item == null){
				if(!player.isSneaking()){
					TileEntityTransceiver core = (TileEntityTransceiver) w.getBlockTileEntity(x, y, z);
					player.addChatMessage("Item: " + core.getStackInSlot(0));
					FluidStack fs = core.getTankInfo(null)[0].fluid;
					if(fs != null){
						player.addChatMessage("Fluid: " + fs.amount + "x" + fs.getFluid().getName());
					}else{
						player.addChatMessage("Fluid: null");
					}
				}
			}
		}
		
		return super.onBlockActivated(w, x, y, z, player, side, hitX, hitY, hitZ);
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		TileEntityTransceiver transceiver = (TileEntityTransceiver) world.getBlockTileEntity(x, y, z);
		if(Minecraft.getMinecraft().thePlayer.isSneaking()){
			if(transceiver.getUpgradeOnSide(ForgeDirection.getOrientation(target.sideHit)) != null)
				return transceiver.getUpgradeOnSide(ForgeDirection.getOrientation(target.sideHit));
		}
		int type = transceiver.getType();
		if(type == 2){
			return new ItemStack(this, 1, 2);
		}else{
			if(type == 1){
				return new ItemStack(this, 1, 1);
			}else{
				return new ItemStack(this, 1, 0);
			}
		}
	}
	
	@Override
	public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
		// TODO Auto-generated method stub
		return super.canPlaceBlockAt(par1World, par2, par3, par4);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List l) {
		l.add(new ItemStack(this, 1, 0));
		l.add(new ItemStack(this, 1, 1));
		l.add(new ItemStack(this, 1, 2));
	}

}
