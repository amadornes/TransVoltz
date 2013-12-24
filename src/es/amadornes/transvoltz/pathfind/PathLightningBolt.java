package es.amadornes.transvoltz.pathfind;

import java.util.ArrayList;
import java.util.List;

import es.amadornes.transvoltz.lib.Vector3;

public class PathLightningBolt {

	protected List<Vector3> path = new ArrayList<Vector3>();
	
	public double getLength(){
		Vector3 last = null;
		int l = 0;
		for(Vector3 v : path){
			if(last != null){
				l += v.toVec3().distanceTo(last.toVec3());
			}
			last = v;
		}
		return l;
	}
	
	public boolean contains(Vector3 step){
		for(Vector3 v : path)
			if(v.equalsInt(step))
				return true;
		return false;
	}
	
	public void simplify() {
		if(path.size() > 3){
			List<Vector3> original = path;
			List<Vector3> path = new ArrayList<Vector3>();
			path.add(original.get(0));
			
			for(int i = 0; i < original.size() - 1;){
				Vector3 v = original.get(i);
				do{
					i++;
				}while(i < original.size() || (i < original.size() && !hasBlocksInPath(v, original.get(i))));
				if(!path.contains(v))
					path.add(v);
			}
			
			this.path = path;
		}
	}
	
	private boolean hasBlocksInPath(Vector3 start, Vector3 finish){
		int parts = 100;
		double diffX = start.getX() - finish.getX();
		double diffY = start.getY() - finish.getY();
		double diffZ = start.getZ() - finish.getZ();
		double partX = diffX;
		partX /= parts;
		double partY = diffY;
		partY /= parts;
		double partZ = diffZ;
		partZ /= parts;
		for(int vx = 0; vx < parts; vx++){
			for(int vy = 0; vy < parts; vy++){
				for(int vz = 0; vz < parts; vz++){
					Vector3 vec = new Vector3(start.getX() + (partX * vx), start.getY() + (partY * vy), start.getZ() + (partZ * vz));
					if(!vec.isBlock(null))
						return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public PathLightningBolt clone() {
		PathLightningBolt path = new PathLightningBolt();
		for(Vector3 v : this.path)
			path.addStep(v);
		return path;
	}

	public void addStep(Vector3 step){
		path.add(step);
	}
	
	public List<Vector3> getSteps(){
		List<Vector3> path = new ArrayList<Vector3>();
		for(Vector3 v : this.path)
			path.add(v);
		return path;
	}
	
}
