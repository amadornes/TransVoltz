package es.amadornes.transvoltz.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import es.amadornes.transvoltz.lib.Blocks;
import es.amadornes.transvoltz.lib.Items;
import es.amadornes.transvoltz.lib.Vector3;
import es.amadornes.transvoltz.packet.PacketHandler;
import es.amadornes.transvoltz.pathfind.PathLightningBolt;
import es.amadornes.transvoltz.pathfind.PathfinderLightningBolt;

public class TileEntityTransceiver extends TileEntity implements IInventory, IFluidHandler {
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
	
	private int type = 0;
	
	public void setType(int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	public int getContentType(){
		if(item != null){
			return 1;
		}
		if(tank.getFluidAmount() > 0){
			return 2;
		}
		return 0;
	}
	
	public boolean canInteractWithItems(){
		return getContentType() != 2;
	}
	
	public boolean canInteractWithFluids(){
		return getContentType() != 1;
	}
	
	public boolean canInput(){
		return true;//type == 0;
	}
	
	public boolean canOutput(){
		return true;//type == 1;
	}
	
	private void updateTile(TileEntity te){
		PacketDispatcher.sendPacketToAllInDimension(te.getDescriptionPacket(), worldObj.provider.dimensionId);
	}
	
	public TileEntityAntenna getAntenna(){
		TileEntity te = new Vector3(this).getRelative(ForgeDirection.getOrientation(blockMetadata)).getTileEntity();
		if(te instanceof TileEntityAntenna)
			return (TileEntityAntenna) te;
		return null;
	}
	
	public boolean hasAntenna(){
		return getAntenna() != null;
	}
	
	/*
	 *   _   _ ____ _______   _                  
	 *  | \ | |  _ \__   __| | |                 
	 *  |  \| | |_) | | |    | |_ __ _  __ _ ___ 
	 *  | . ` |  _ <  | |    | __/ _` |/ _` / __|
	 *  | |\  | |_) | | |    | || (_| | (_| \__ \
	 *  |_| \_|____/  |_|     \__\__,_|\__, |___/
	 *                                  __/ |    
	 *                                  |___/     
	 */
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeUpgradesToNBT(tag);
		tag.setInteger("metadata", blockMetadata);
		
		tag.setInteger("type", type);
		
		if(tank.getFluidAmount() > 0){
			NBTTagCompound tank = new NBTTagCompound();
			this.tank.writeToNBT(tank);
			tag.setCompoundTag("tank", tank);
		}
		
		if(this.item != null){
			NBTTagCompound item = new NBTTagCompound();
			this.item.writeToNBT(item);
			tag.setCompoundTag("item", item);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readUpgradesFromNBT(tag);
		blockMetadata = tag.getInteger("metadata");
		
		type = tag.getInteger("type");
		
		if(tag.hasKey("tank")){
			NBTTagCompound tank = tag.getCompoundTag("tank");
			this.tank.readFromNBT(tank);
		}else{
			this.tank.setFluid(null);
		}
		
		if(tag.hasKey("item")){
			NBTTagCompound item = tag.getCompoundTag("item");
			this.item = ItemStack.loadItemStackFromNBT(item);
		}else{
			this.item = null;
		}
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
	
	/*
	 *    _______ ______   _   _      _    _             
	 *   |__   __|  ____| | | (_)    | |  (_)            
	 *      | |  | |__    | |_ _  ___| | ___ _ __   __ _ 
	 *      | |  |  __|   | __| |/ __| |/ / | '_ \ / _` |
	 *      | |  | |____  | |_| | (__|   <| | | | | (_| |
	 *      |_|  |______|  \__|_|\___|_|\_\_|_| |_|\__, |
	 *                                              __/ |
	 *                                             |___/ 
	 */
	
	private int randomTick = 0;
	private int randomTickTimer = 0;
	private int tick = 0;
	
	public int getTickDelay(){
		int def = 20;
		int ocMod = 2;
		double delay = def;
		for(int i = 0; i < getUpgradeAmount(UpgradeType.OVERCLOCK); i++)
			delay /= ocMod;
		return (int) delay;
	}
	
	@Override
	public void updateEntity() {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
			if(tick%2 == 0){
				tickLightningBolts();
			}
		}
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			if(hasAntenna()){
				if(type != 0){//If it's not a receiver
					if(randomTickTimer == randomTick){
						int delay = getTickDelay();
						delay = delay + new Random((long)(Math.pow(System.currentTimeMillis()%300, Math.PI)*1000000)).nextInt(delay);
						
						randomTick();
						
						randomTick = delay;
						randomTickTimer = -1;
					}
					this.randomTickTimer++;
					
					if(tick%10 == 0){
						recalculatePossiblePaths();
					}
				}
			}
			if(tick%5 == 0){
				PacketDispatcher.sendPacketToAllInDimension(getDescriptionPacket(), worldObj.provider.dimensionId);
			}
		}
		this.tick++;
	}
	
	private void randomTick(){
		PathLightningBolt p = getPathToRandomDestination();
		if(p != null && getContentType() != 0){
			//FIXME SEND SOMETHING
			PacketDispatcher.sendPacketToAllInDimension(PacketHandler.createLightningPacket(this, p), worldObj.provider.dimensionId);
		}
	}
	
	private Map<TileEntityTransceiver, PathLightningBolt> inRange = new HashMap<TileEntityTransceiver, PathLightningBolt>();
	
	private void recalculatePossiblePaths(){
		inRange.clear();
		Map<TileEntityTransceiver, PathLightningBolt> paths = getNearbyTransceivers(6);
		for(TileEntityTransceiver t : paths.keySet()){
			if(t.getType() != 1){//If it's not a transmitter
				inRange.put(t, paths.get(t));
			}
		}
	}
	
	private PathLightningBolt getPathToRandomDestination(){
		return inRange.size() > 0 ? new ArrayList<PathLightningBolt>(inRange.values()).get(new Random().nextInt(inRange.size())) : null;
	}
	
	private Map<PathLightningBolt, Integer> effects = new HashMap<PathLightningBolt, Integer>();
	
	public void addLightningEffect(PathLightningBolt path){
		getEffects().put(path, new Integer(0));
	}
	
	private void tickLightningBolts(){
		Map<PathLightningBolt, Integer> nEffects = new HashMap<PathLightningBolt, Integer>();
		for(PathLightningBolt p : getEffects().keySet()){
			int i = getEffects().get(p).intValue();
			i++;
			if(!(i >= (p.getSteps().size()*2))){
				nEffects.put(p, new Integer(i));
			}
		}
		getEffects().clear();
		effects = nEffects;
	}
	
	public Map<PathLightningBolt, Integer> getEffects() {
		return effects;
	}

	private Map<TileEntityTransceiver, PathLightningBolt> getNearbyTransceivers(int radius){
		Vec3 thisTE = new Vector3(this).toVec3();
		List<TileEntityTransceiver> nearby = new ArrayList<TileEntityTransceiver>();
		for(int x = (xCoord - radius); x < (xCoord + radius); x++){
			for(int y = (yCoord - radius); y < (yCoord + radius); y++){
				for(int z = (zCoord - radius); z < (zCoord + radius); z++){
					TileEntity te = worldObj.getBlockTileEntity(x, y, z);
					if(te != null){
						if(te instanceof TileEntityTransceiver){
							if(((TileEntityTransceiver)te).hasAntenna()){
								Vec3 tile = new Vector3(x, y, z).toVec3();
								if(thisTE.distanceTo(tile) < radius){
									if(((TileEntityTransceiver) worldObj.getBlockTileEntity(x, y, z)).hasAntenna())
										nearby.add((TileEntityTransceiver) worldObj.getBlockTileEntity(x, y, z));
								}
							}
						}
					}
				}
			}
		}
		if(nearby.contains(this))
			nearby.remove(this);

		Map<TileEntityTransceiver, PathLightningBolt> transceivers = new HashMap<TileEntityTransceiver, PathLightningBolt>();
		for(TileEntityTransceiver c : nearby){
			PathfinderLightningBolt pflb = new PathfinderLightningBolt(new Vector3(getAntenna()), new Vector3(c.getAntenna()), ((int)(radius)));
			pflb.pathfind();
			PathLightningBolt plb = pflb.getShortestPath();
			if(plb != null){
				if(plb.getLength() <= radius){
					transceivers.put(c, plb);
				}
			}
		}
		
		return transceivers;
	}
	
	/*
	 *   _    _                           _            _          __  __ 
	 *  | |  | |                         | |          | |        / _|/ _|
	 *  | |  | |_ __   __ _ _ __ __ _  __| | ___   ___| |_ _   _| |_| |_ 
	 *  | |  | | '_ \ / _` | '__/ _` |/ _` |/ _ \ / __| __| | | |  _|  _|
 	 *  | |__| | |_) | (_| | | | (_| | (_| |  __/ \__ \ |_| |_| | | | |  
 	 *   \____/| .__/ \__, |_|  \__,_|\__,_|\___| |___/\__|\__,_|_| |_|  
 	 *         | |     __/ |                                             
	 *         |_|    |___/                                              
	 */
	
	private UpgradeType[] upgrades = new UpgradeType[4];
	
	public ForgeDirection[] determineUpgradableFaces(){
		switch(blockMetadata){
		case 0:
			return new ForgeDirection[]{ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.SOUTH};
		case 1:
			return new ForgeDirection[]{ForgeDirection.EAST, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.SOUTH};
		case 2:
			return new ForgeDirection[]{ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.EAST, ForgeDirection.WEST};
		case 3:
			return new ForgeDirection[]{ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.EAST, ForgeDirection.WEST};
		case 4:
			return new ForgeDirection[]{ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.NORTH, ForgeDirection.SOUTH};
		case 5:
			return new ForgeDirection[]{ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.NORTH, ForgeDirection.SOUTH};
		}
		
		return null;
	}
	
	private boolean isFaceUpgradable(ForgeDirection face){
		int f = 0;
		ForgeDirection[] upgradable = determineUpgradableFaces();
		if(upgradable != null){
			for(ForgeDirection dir : upgradable){
				if(dir == face)
					if(upgrades[f] == null)
						return true;
				f++;
			}
		}
		return false;
	}
	
	private void writeUpgradesToNBT(NBTTagCompound t){
		NBTTagCompound tag = new NBTTagCompound();
		
		for(int i = 0; i < upgrades.length; i++){
			if(upgrades[i] == null){
				tag.setInteger("upgrade" + i, -1);
			}else{
				tag.setInteger("upgrade" + i, upgrades[i].ordinal());
			}
		}
		
		t.setTag("upgrades", tag);
	}
	
	private void readUpgradesFromNBT(NBTTagCompound t){
		NBTTagCompound tag = t.getCompoundTag("upgrades");
		
		for(int i = 0; i < upgrades.length; i++){
			UpgradeType type = null;
			if(tag.getInteger("upgrade" + i) >= 0){
				type = UpgradeType.getFromDamage(tag.getInteger("upgrade" + i));
			}
			upgrades[i] = type;
		}
	}
	
	public boolean installUpgrade(UpgradeType type, ForgeDirection face){
		if(type != null){
			if(isFaceUpgradable(face)){
				switch(this.type){
				case 0:
					if(type.receiver){
						if(type.maxReceiver > getUpgradeAmount(type)){
							return true;
						}
					}
					break;
				case 1:
					if(type.sender){
						if(type.maxSender > getUpgradeAmount(type)){
							return true;
						}
					}
					break;
				case 2:
					if(type.repeater){
						if(type.maxRepeater > getUpgradeAmount(type)){
							return true;
						}
					}
					break;
				}
			}
		}
		return false;
	}
	
	public boolean hasUpgrade(UpgradeType type){
		for(int i = 0; i < upgrades.length; i++){
			if(upgrades[i] == type)
				return true;
		}
		return false;
	}
	
	public int getUpgradeAmount(UpgradeType type){
		int amount = 0;
		for(int i = 0; i < upgrades.length; i++){
			if(upgrades[i] == type)
				amount++;
		}
		return amount;
	}
	
	public void setUpgrade(UpgradeType type, ForgeDirection face){
		int id = 0;
		ForgeDirection[] faces = determineUpgradableFaces();
		if(faces != null){
			for(ForgeDirection dir : faces){
				if(dir == face)
					break;
				id++;
			}
			if(id < 4){
				if(upgrades[id] == null){
					upgrades[id] = type;
					updateTile(this);
				}
			}
		}
	}
	
	public void removeUpgrade(int id){
		upgrades[id] = null;
		updateTile(this);
	}
	
	public ItemStack getUpgradeItemStack(int upgrade){
		if(upgrades[upgrade] == null)
			return null;
		return new ItemStack(Items.upgrade, 1, upgrades[upgrade].ordinal());
	}
	
	@SuppressWarnings("incomplete-switch")
	public ItemStack getUpgradeOnSide(ForgeDirection side){
		if(blockMetadata == 0){
			switch(side){
			case WEST:
				return getUpgradeItemStack(1);
			case EAST:
				return getUpgradeItemStack(0);
			case SOUTH:
				return getUpgradeItemStack(3);
			case NORTH:
				return getUpgradeItemStack(2);
			}
		}else if(blockMetadata == 1){
			switch(side){
			case EAST:
				return getUpgradeItemStack(0);
			case WEST:
				return getUpgradeItemStack(1);
			case NORTH:
				return getUpgradeItemStack(2);
			case SOUTH:
				return getUpgradeItemStack(3);
			}
		}else if(blockMetadata == 2){
			switch(side){
			case UP:
				return getUpgradeItemStack(0);
			case DOWN:
				return getUpgradeItemStack(1);
			case EAST:
				return getUpgradeItemStack(2);
			case WEST:
				return getUpgradeItemStack(3);
			}
		}else if(blockMetadata == 3){
			switch(side){
			case UP:
				return getUpgradeItemStack(0);
			case DOWN:
				return getUpgradeItemStack(1);
			case EAST:
				return getUpgradeItemStack(2);
			case WEST:
				return getUpgradeItemStack(3);
			}
		}else if(blockMetadata == 4){
			switch(side){
			case UP:
				return getUpgradeItemStack(0);
			case DOWN:
				return getUpgradeItemStack(1);
			case NORTH:
				return getUpgradeItemStack(2);
			case SOUTH:
				return getUpgradeItemStack(3);
			}
		}else if(blockMetadata == 5){
			switch(side){
			case UP:
				return getUpgradeItemStack(0);
			case DOWN:
				return getUpgradeItemStack(1);
			case NORTH:
				return getUpgradeItemStack(2);
			case SOUTH:
				return getUpgradeItemStack(3);
			}
		}
		return null;
	}
	
	@SuppressWarnings("incomplete-switch")
	public void removeUpgradeOnSide(ForgeDirection side){
		if(blockMetadata == 0){
			switch(side){
			case WEST:
				removeUpgrade(1);
				return;
			case EAST:
				removeUpgrade(0);
				return;
			case SOUTH:
				removeUpgrade(3);
				return;
			case NORTH:
				removeUpgrade(2);
				return;
			}
		}else if(blockMetadata == 1){
			switch(side){
			case EAST:
				removeUpgrade(0);
				return;
			case WEST:
				removeUpgrade(1);
				return;
			case NORTH:
				removeUpgrade(2);
				return;
			case SOUTH:
				removeUpgrade(3);
				return;
			}
		}else if(blockMetadata == 2){
			switch(side){
			case UP:
				removeUpgrade(0);
				return;
			case DOWN:
				removeUpgrade(1);
				return;
			case EAST:
				removeUpgrade(2);
				return;
			case WEST:
				removeUpgrade(3);
				return;
			}
		}else if(blockMetadata == 3){
			switch(side){
			case UP:
				removeUpgrade(0);
				return;
			case DOWN:
				removeUpgrade(1);
				return;
			case EAST:
				removeUpgrade(2);
				return;
			case WEST:
				removeUpgrade(3);
				return;
			}
		}else if(blockMetadata == 4){
			switch(side){
			case UP:
				removeUpgrade(0);
				return;
			case DOWN:
				removeUpgrade(1);
				return;
			case NORTH:
				removeUpgrade(2);
				return;
			case SOUTH:
				removeUpgrade(3);
				return;
			}
		}else if(blockMetadata == 5){
			switch(side){
			case UP:
				removeUpgrade(0);
				return;
			case DOWN:
				removeUpgrade(1);
				return;
			case NORTH:
				removeUpgrade(2);
				return;
			case SOUTH:
				removeUpgrade(3);
				return;
			}
		}
	}

	public static enum UpgradeType{
		EMPTY("empty", "Empty upgrade", 0, false, false, false, 0, 0, 0),
		OVERCLOCK("overclock", "Overclocker upgrade", 1, false, true, true, 0, 4, 4),
		AUTO_EJECT("autoeject", "Auto eject upgrade", 2, true, false, false, 1, 0, 0),
		AUTO_SUCK("autoextract", "Auto extract upgrade", 3, false, true, false, 0, 1, 0),
		HV("hv", "HV upgrade", 4, false, true, false, 0, 1, 0);
		
		private String icon;
		private String displayname;
		private int id;
		private boolean receiver, sender, repeater;
		private int maxReceiver, maxSender, maxRepeater;
		
		private UpgradeType(String icon, String displayname, int id, boolean receiver, boolean sender, boolean repeater, int maxReceiver, int maxSender, int maxRepeater) {
			this.icon = icon;
			this.displayname = displayname;
			this.id = id;
			this.receiver = receiver;
			this.sender = sender;
			this.repeater = repeater;
			this.maxReceiver = maxReceiver;
			this.maxSender = maxSender;
			this.maxRepeater = maxRepeater;
		}
		
		public String getIconName(){
			return icon;
		}
		
		public int getId(){
			return id;
		}
		
		public boolean canGoOnReceiver(){
			return receiver;
		}
		
		public boolean canGoOnSender(){
			return sender;
		}
		
		public boolean canGoOnRepeater(){
			return repeater;
		}
		
		public int maxAmoutPerReceiver(){
			return maxReceiver;
		}
		
		public int maxAmoutPerSender(){
			return maxSender;
		}
		
		public int maxAmoutPerRepeater(){
			return maxRepeater;
		}
		
		public String getDisplayName() {
			return displayname;
		}
		
		public static UpgradeType getFromDamage(int damage){
			for(UpgradeType u : values()){
				if(u.id == damage)
					return u;
			}
			return UpgradeType.EMPTY;
		}
		
		public static int getHighestUpgradeID(){
			int highest = 0;
			for(UpgradeType u : values()){
				if(u.id > highest)
					highest = u.id;
			}
			return highest;
		}
	}
	
	/*
	 *    _____                      _                       ___              _    
 	 *   |_   _|                    | |                     / / |            | |   
 	 *     | |  _ ____   _____ _ __ | |_ ___  _ __ _   _   / /| |_ __ _ _ __ | | __
 	 *     | | | '_ \ \ / / _ \ '_ \| __/ _ \| '__| | | | / / | __/ _` | '_ \| |/ /
 	 *    _| |_| | | \ V /  __/ | | | || (_) | |  | |_| |/ /  | || (_| | | | |   < 
 	 *   |_____|_| |_|\_/ \___|_| |_|\__\___/|_|   \__, /_/    \__\__,_|_| |_|_|\_\
  	 *                                             __/ |                          
   	 *                                            |___/                           
	 */
	
	private ItemStack item = null;
	private FluidTank tank = new FluidTank(4 * FluidContainerRegistry.BUCKET_VOLUME);
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(canInteractWithFluids() && canInput()){
			return canFill(from, resource.getFluid()) ? tank.fill(resource, doFill) : 0;
		}
		return 0;
	}
	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if(canInteractWithFluids() && canOutput()){
			if(tank.getFluidAmount() > 0){
				return canDrain(from, resource.getFluid()) ? tank.drain(resource.amount, doDrain) : null;
			}
		}
		return null;
	}
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(canInteractWithFluids() && canOutput()){
			if(tank.getFluidAmount() > 0){
				return canDrain(from, tank.getFluid().getFluid()) ? tank.drain(maxDrain, doDrain) : null;
			}
		}
		return null;
	}
	
	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return canInteractWithFluids() && canInput() && from == ForgeDirection.getOrientation(blockMetadata).getOpposite();
	}
	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return canInteractWithFluids() && canOutput() && from == ForgeDirection.getOrientation(blockMetadata).getOpposite();
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tank.getInfo()};
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		if(canInteractWithItems() && canOutput()){
			if(slot == 0){
				if(item != null){
					item.stackSize -= amt;
					if(item.stackSize > 0){
						return item;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack item) {
		if(canInteractWithItems() && canInput()){
			if(slot == 0){
				this.item = item;
			}
		}
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		return (slot == 0) ? item : null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getStackInSlot(slot);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return canInteractWithItems() && canInput() && slot == 0;
	}
	
	public String getInvName() {return Blocks.transceiver.getUnlocalizedName();}
	public boolean isInvNameLocalized() {return false;}
	public int getInventoryStackLimit() {return 64;}
	public int getSizeInventory() {return 1;}
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {return true;}
	public void openChest() {}
	public void closeChest() {}
	
}
