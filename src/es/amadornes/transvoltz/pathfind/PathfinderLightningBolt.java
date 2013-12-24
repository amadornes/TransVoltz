package es.amadornes.transvoltz.pathfind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraftforge.common.ForgeDirection;
import es.amadornes.transvoltz.lib.Vector3;

public class PathfinderLightningBolt {
	
	protected Vector3 from, to;
	protected int maxPathLength = 8;
	
	public PathfinderLightningBolt(Vector3 from, Vector3 to, int maxPathLength) {
		this.from = from.getRelative(0.5, 0.5, 0.5);
		this.to = to.getRelative(0.5, 0.5, 0.5);
		this.maxPathLength = maxPathLength;
	}
	
	protected List<PathLightningBolt> paths = new ArrayList<PathLightningBolt>();
	
	public void pathfind() {
		PathLightningBolt p = new PathLightningBolt();
		pathfind(from, p);
		/*for(PathLightningBolt plb : paths){
			plb.simplify();
		}*/
	}
	
	private void pathfind(Vector3 from, PathLightningBolt p){
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
			Vector3 rel = from.getRelative(dir);
			if(!p.contains(rel)){
				PathLightningBolt path = p.clone();
				path.addStep(rel);
				if(rel.equals(to)){
					paths.add(path);
					break;
				}
				if(rel.isBlock(null)){//If it's air
					if(path.getLength() <= maxPathLength){
						pathfind(rel, path);
					}
				}
			}
		}
	}
	
	public PathLightningBolt getShortestPath(){
		List<PathLightningBolt> paths = new ArrayList<PathLightningBolt>();
		for(PathLightningBolt p : this.paths)
			paths.add(p);
		if(paths.size() > 0){
			Collections.sort(paths, new PathSorter());
			
			return paths.get(0);
		}
		return null;
	}
	
}
