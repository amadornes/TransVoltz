package es.amadornes.transvoltz.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import es.amadornes.transvoltz.lib.ModInfo;
import es.amadornes.transvoltz.lib.Vector3;
import es.amadornes.transvoltz.pathfind.PathLightningBolt;
import es.amadornes.transvoltz.tileentity.TileEntityTransceiver;

public class PacketHandler implements IPacketHandler {

	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));
		
        try{
        	String key = dis.readUTF();
        	if(key.equalsIgnoreCase("lightning")){
        		handleLightningPacket(dis);
        	}
        }catch(Exception ex){
        	ex.printStackTrace();
        }
	}
	
	private void handleLightningPacket(DataInputStream dis){
        try{
        	int tex = 0;
        	int tey = 0;
        	int tez = 0;
        	StringTokenizer teSt = new StringTokenizer(dis.readUTF(), ":");
        	tex = Integer.parseInt(teSt.nextToken());
        	tey = Integer.parseInt(teSt.nextToken());
        	tez = Integer.parseInt(teSt.nextToken());
			TileEntityTransceiver te = (TileEntityTransceiver) Minecraft.getMinecraft().theWorld.getBlockTileEntity(tex, tey, tez);
        	
			PathLightningBolt p = new PathLightningBolt();
        	while(dis.available() > 0){
        		double x = 0;
        		double y = 0;
        		double z = 0;
            	StringTokenizer st = new StringTokenizer(dis.readUTF(), ":");
            	x = Double.parseDouble(st.nextToken());
            	y = Double.parseDouble(st.nextToken());
            	z = Double.parseDouble(st.nextToken());
            	p.addStep(new Vector3(x, y, z, te.worldObj));
        	}
        	
        	te.addLightningEffect(p);
        }catch(Exception ex){
        	ex.printStackTrace();
        }
	}

	public static Packet250CustomPayload createLightningPacket(TileEntity te, PathLightningBolt path) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeUTF("lightning");
			Vector3 tile = new Vector3(te);
			outputStream.writeUTF(tile.getBlockX() + ":" + tile.getBlockY() + ":" + tile.getBlockZ());
			List<Vector3> steps = new ArrayList<Vector3>();
			for(int i = 0; i < path.getSteps().size(); i++){
				steps.add(path.getSteps().get(path.getSteps().size() - i - 1));
			}
			for(Vector3 v : steps){
				outputStream.writeUTF(v.getX() + ":" + v.getY() + ":" + v.getZ());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = ModInfo.CHANNEL;
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		return packet;
	}

}
