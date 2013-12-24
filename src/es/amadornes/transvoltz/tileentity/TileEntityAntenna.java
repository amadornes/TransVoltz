package es.amadornes.transvoltz.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import es.amadornes.transvoltz.lib.Vector3;

public class TileEntityAntenna extends TileEntity {
	
	private boolean hasDish = false;
	private boolean isDouble = false;
	public static int dishRangeMultiplier = 4;
	public static int defaultRange = 8;
	
	public void setType(int type){
		if(type == 0){
			hasDish = false;
			isDouble = false;
		}else if(type == 1){
			hasDish = false;
			isDouble = true;
		}else if(type == 2){
			hasDish = true;
			isDouble = false;
		}else if(type == 3){
			hasDish = true;
			isDouble = true;
		}
	}
	
	public int getType(){
		if(isDouble){
			if(hasDish){
				return 3;
			}else{
				return 1;
			}
		}else{
			if(hasDish){
				return 2;
			}else{
				return 0;
			}
		}
	}
	
	public boolean hasDish(){
		return hasDish;
	}
	
	public boolean isDouble(){
		return isDouble;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("metadata", blockMetadata);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		blockMetadata = tag.getInteger("metadata");
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound data = new NBTTagCompound();
		writeToNBT(data);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, data);
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
	}
	
	int tick = 0;
	
	@Override
	public void updateEntity() {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			if(tick == 10){
				PacketDispatcher.sendPacketToAllInDimension(getDescriptionPacket(), worldObj.provider.dimensionId);
			}
		}
	}
	
	public TileEntityTransceiver getTransceiver(){
		return (TileEntityTransceiver) new Vector3(this).getRelative(ForgeDirection.getOrientation(blockMetadata)).getTileEntity();
	}
	
}
