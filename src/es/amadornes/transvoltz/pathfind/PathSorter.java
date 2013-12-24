package es.amadornes.transvoltz.pathfind;

import java.util.Comparator;

public class PathSorter implements Comparator<PathLightningBolt> {

	@Override
	public int compare(PathLightningBolt p1, PathLightningBolt p2) {
		double diff = (p1.getLength()*1000000000) - (p2.getLength()*1000000000);
		return (int) diff;
	}

}
