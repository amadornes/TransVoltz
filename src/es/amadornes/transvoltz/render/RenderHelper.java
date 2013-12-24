package es.amadornes.transvoltz.render;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import es.amadornes.transvoltz.lib.Vector3;
import es.amadornes.transvoltz.pathfind.PathLightningBolt;

public class RenderHelper {
	
	public static void renderLightning(PathLightningBolt path, int progress){
		int pathLength = path.getSteps().size();
		
		GL11.glPushMatrix();
		GL11.glTranslated(2.5, 0.5, 0.5);
			Tessellator tessellator = Tessellator.instance;
	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        GL11.glDisable(GL11.GL_LIGHTING);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			double minx = Double.MAX_VALUE;
			double miny = Double.MAX_VALUE;
			double minz = Double.MAX_VALUE;
			double maxx = Double.MIN_VALUE;
			double maxy = Double.MIN_VALUE;
			double maxz = Double.MIN_VALUE;
			for(Vector3 vec : path.getSteps()){
				minx = Math.min(minx, vec.getX());
				miny = Math.min(miny, vec.getY());
				minz = Math.min(minz, vec.getZ());
				maxx = Math.max(maxx, vec.getX());
				maxy = Math.max(maxy, vec.getY());
				maxz = Math.max(maxz, vec.getZ());
			}
			
			Vector3 last = null;
			int minq = Math.max(progress - pathLength, 0);
			int maxq = pathLength - Math.abs(Math.min(progress - pathLength, 0));
			for(int q = minq; q < maxq; q++){
				Vector3 vec = path.getSteps().get(q);
				if(last != null){
			        tessellator.startDrawingQuads();
			        
			        tessellator.setColorRGBA(255, 255, 255, 255);
			        
			        double thickness = 0.05;
			        
			        tessellator.addVertex(last.getX() - minx, last.getY() - miny - thickness, last.getZ() - minz);
			        tessellator.addVertex(last.getX() - minx, vec.getY() - miny + thickness, last.getZ() - minz);
			        tessellator.addVertex(vec.getX() - minx , vec.getY() - miny + thickness, last.getZ() - minz);
			        tessellator.addVertex(vec.getX() - minx, last.getY() - miny - thickness, last.getZ() - minz);
			        	
			        tessellator.draw();
				}
				last = vec;
			}
	        
	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glEnable(GL11.GL_LIGHTING);
	        GL11.glEnable(GL11.GL_TEXTURE_2D);
			
		GL11.glPopMatrix();
	}
	
}
