package es.amadornes.transvoltz.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import es.amadornes.transvoltz.lib.ModInfo;
import es.amadornes.transvoltz.model.ModelAntenna;
import es.amadornes.transvoltz.tileentity.TileEntityAntenna;

public class RenderAntenna extends TileEntitySpecialRenderer implements IItemRenderer {
	
	private ModelAntenna model = new ModelAntenna();
	private ResourceLocation texture = new ResourceLocation(ModInfo.MOD_ID, "textures/model/antenna.png");
	
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch(type){
		case ENTITY:
			render(0, 1.25, 0, 180, 0, 0, 1, item.getItemDamage());
			return;
		case EQUIPPED:
			render(0.5, 1.25, 0.5, 180, 0, 0, 1, item.getItemDamage());
			return;
		case EQUIPPED_FIRST_PERSON:
			render(0.5, 1.25, 0.5, 180, 0, 0, 1, item.getItemDamage());
			return;
		case FIRST_PERSON_MAP:
			return;
		case INVENTORY:
			render(0, 0.9, 0, 180, 0, 0, 1, item.getItemDamage());
			return;
		}
	}
	
	public void render(double x, double y, double z, double rx, double ry, double rz, double scale, int metadata){
		boolean isDouble = metadata % 2 == 1;
		//boolean hasDish = metadata % 2 == 0;
		
		GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			GL11.glRotated(rx, 1, 0, 0);
			GL11.glRotated(ry, 0, 1, 0);
			GL11.glRotated(rz, 0, 0, 1);
			
			GL11.glScaled(scale, scale, scale);
			
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
			if(isDouble){
				GL11.glPushMatrix();
					GL11.glRotated(45, 1, 0, 0);
					GL11.glScaled(2, 2, 2);
					GL11.glTranslated(0, -1.5, 0);
					model.render();
				GL11.glPopMatrix();
				GL11.glPushMatrix();
					GL11.glRotated(-45, 1, 0, 0);
					GL11.glTranslated(0, -1.5, 0);
					GL11.glScaled(2, 2, 2);
					model.render();
				GL11.glPopMatrix();
			}else{
				GL11.glTranslated(0, -1.5, 0);
				GL11.glScaled(2, 2, 2);
				model.render();
			}
			
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		TileEntityAntenna te = (TileEntityAntenna) tileentity;
		int metadata = te.blockMetadata;
		
		//boolean hasDish = te.hasDish();
		boolean isDouble = te.isDouble();

		double rx = 0;
		double ry = 0;
		double rz = 0;
		double tx = 0;
		double ty = 0;
		double tz = 0;
		
		switch(metadata){
		case 0:
			tx = 0.5;
			tz = -0.5;
			ty = -0.85;
			break;
		case 1:
			rx = 180;
			tx = 0.5;
			ty = 1.85;
			tz = -0.5;
			break;
		case 2:
			rx = 90;
			tz = -1.85;
			ty = 0.5;
			tx = 0.5;
			break;
		case 3:
			rx = -90;
			tz = 0.85;
			ty = 0.5;
			tx = 0.5;
			break;
		case 4:
			rz = -90;
			tx = -0.85;
			ty = 0.5;
			tz = -0.5;
			break;
		case 5:
			tz = -0.5;
			rz = 90;
			ty = 0.5;
			tx = 1.85;
			break;
		}
		
		GL11.glPushMatrix();
			
			GL11.glTranslated(x, y, z);
	        GL11.glTranslated(0, 0, 1);
	        GL11.glTranslated(tx, ty, tz);
	        GL11.glRotated(rx, 1, 0, 0);
	        GL11.glRotated(ry, 0, 1, 0);
	        GL11.glRotated(rz, 0, 0, 1);
			GL11.glScaled(1.25, 1.25, 1.25);
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
			if(isDouble){
				GL11.glPushMatrix();
					model.render();//TODO
				GL11.glPopMatrix();
			}else{
				model.render();
			}
        
        GL11.glPopMatrix();
		
		
	}
	
}
