package es.amadornes.transvoltz.render;

import java.util.Map;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import es.amadornes.transvoltz.lib.ModInfo;
import es.amadornes.transvoltz.pathfind.PathLightningBolt;
import es.amadornes.transvoltz.tileentity.TileEntityTransceiver;

public class RenderTransceiver extends TileEntitySpecialRenderer implements IItemRenderer {

	private IModelCustom model = AdvancedModelLoader.loadModel("/assets/" + ModInfo.MOD_ID + "/model/core.obj");
	private ResourceLocation texture_base = new ResourceLocation(ModInfo.MOD_ID, "textures/model/core_base.png");

	private ResourceLocation texture_input = new ResourceLocation(ModInfo.MOD_ID, "textures/model/core_input.png");
	private ResourceLocation texture_output = new ResourceLocation(ModInfo.MOD_ID, "textures/model/core_output.png");
	private ResourceLocation texture_repeater = new ResourceLocation(ModInfo.MOD_ID, "textures/model/core_repeater.png");
	
	private ResourceLocation texture_empty = new ResourceLocation(ModInfo.MOD_ID, "textures/model/core_empty.png");
	private ResourceLocation texture_items = new ResourceLocation(ModInfo.MOD_ID, "textures/model/core_items.png");
	private ResourceLocation texture_fluids = new ResourceLocation(ModInfo.MOD_ID, "textures/model/core_fluids.png");
	
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		TileEntityTransceiver te = (TileEntityTransceiver) tileentity;
		int metadata = te.blockMetadata;

		float rx = 0;
		float ry = 0;
		float rz = 0;
		float tx = 0;
		float ty = 0;
		float tz = 0;
		
		switch(metadata){
		case 0:
			break;
		case 1:
			rx = 180;
			tz = -1;
			ty = 1;
			break;
		case 2:
			rx = 90;
			tz = -1;
			break;
		case 3:
			rx = -90;
			ty = 1;
			break;
		case 4:
			rz = -90;
			ty = 1;
			break;
		case 5:
			rz = 90;
			tx = 1;
			break;
		}

		int type = te.getType();
		int content = te.getContentType();
		
		GL11.glPushMatrix();
			
			GL11.glTranslated(x, y, z);
	        GL11.glTranslated(0, 0, 1);
	        GL11.glTranslated(tx, ty, tz);
	        GL11.glRotated(rx, 1, 0, 0);
	        GL11.glRotated(ry, 0, 1, 0);
	        GL11.glRotated(rz, 0, 0, 1);

			FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture_base);
			model.renderAll();
			
			switch(type){
			case 0:
				FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture_input);
				break;
			case 1:
				FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture_output);
				break;
			case 2:
				FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture_repeater);
				break;
			}
			
			model.renderAll();
			
			switch(content){
			case 0:
				FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture_empty);
				break;
			case 1:
				FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture_items);
				break;
			case 2:
				FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture_fluids);
				break;
			default:
				FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture_empty);
				break;
			}
			model.renderAll();
			
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
			
			GL11.glTranslated(x, y, z);
			renderUpgrades(te);
			
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
			
			GL11.glTranslated(x, y, z);
			renderLightning(te);
			
		GL11.glPopMatrix();
	}
	
	private void renderUpgrades(TileEntityTransceiver te){
		ItemStack up = te.getUpgradeOnSide(ForgeDirection.UP);
		ItemStack down = te.getUpgradeOnSide(ForgeDirection.DOWN);
		ItemStack east = te.getUpgradeOnSide(ForgeDirection.EAST);
		ItemStack west = te.getUpgradeOnSide(ForgeDirection.WEST);
		ItemStack north = te.getUpgradeOnSide(ForgeDirection.NORTH);
		ItemStack south = te.getUpgradeOnSide(ForgeDirection.SOUTH);
		if(up != null){
			GL11.glPushMatrix();
				GL11.glRotated(90, 1, 0, 0);
				GL11.glScaled(0.5, 0.5, 0.5);
				GL11.glTranslated(0.5, -1.5, -1.75);
				GL11.glTranslated(0.5, 2.45, 0.5);
				RenderUpgrade.render(0, 0, 0, 0, 0, 0, 1, up.getItemDamage());
			GL11.glPopMatrix();
		}
		if(down != null){
			GL11.glPushMatrix();
				GL11.glRotated(-90, 1, 0, 0);
				GL11.glScaled(0.5, 0.5, 0.5);
				GL11.glTranslated(0.5, 0.5, 0.25);
				GL11.glTranslated(0.5, -1.45, 0.5);
				RenderUpgrade.render(0, 0, 0, 0, 0, 0, 1, down.getItemDamage());
			GL11.glPopMatrix();
		}
		if(north != null){
			GL11.glPushMatrix();
				GL11.glScaled(0.5, 0.5, 0.5);
				GL11.glTranslated(1, 1, 0.75);
				RenderUpgrade.render(0, 0, 0, 0, 0, 0, 1, north.getItemDamage());
			GL11.glPopMatrix();
		}
		if(south != null){
			GL11.glPushMatrix();
				GL11.glRotated(180, 0, 1, 0);
				GL11.glScaled(0.5, 0.5, 0.5);
				GL11.glTranslated(-1, 1, -1.25);
				RenderUpgrade.render(0, 0, 0, 0, 0, 0, 1, south.getItemDamage());
			GL11.glPopMatrix();
		}
		if(east != null){
			GL11.glPushMatrix();
				GL11.glRotated(-90, 0, 1, 0);
				GL11.glScaled(0.5, 0.5, 0.5);
				GL11.glTranslated(1, 1, -1.25);
				RenderUpgrade.render(0, 0, 0, 0, 0, 0, 1, east.getItemDamage());
			GL11.glPopMatrix();
		}
		if(west != null){
			GL11.glPushMatrix();
				GL11.glRotated(90, 0, 1, 0);
				GL11.glScaled(0.5, 0.5, 0.5);
				GL11.glTranslated(-1, 1, 0.75);
				RenderUpgrade.render(0, 0, 0, 0, 0, 0, 1, west.getItemDamage());
			GL11.glPopMatrix();
		}
	}
	
	private void renderLightning(TileEntityTransceiver te){
		Map<PathLightningBolt, Integer> effects = te.getEffects();
		for(PathLightningBolt p : effects.keySet()){
			int progress = effects.get(p).intValue();
			GL11.glPushMatrix();
				RenderHelper.renderLightning(p, progress);
			GL11.glPopMatrix();
		}
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@SuppressWarnings("incomplete-switch")
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch(type){
		case ENTITY:
			render(-0.5, 1, -0.5, 180, 0, 0, 1, item.getItemDamage());
			return;
		case EQUIPPED:
			render(0, 1, 0, 180, 0, 0, 1, item.getItemDamage());
			return;
		case EQUIPPED_FIRST_PERSON:
			render(0, 1, 0, 180, 0, 0, 1, item.getItemDamage());
			return;
		case INVENTORY:
			render(0, 0.9, 0, 180, 0, 0, 1, item.getItemDamage());
			return;
		}
	}
	
	public void render(double x, double y, double z, double rx, double ry, double rz, double scale, int metadata){
		boolean isInput = metadata == 0;
		boolean isRepeater = metadata == 3;
		
		GL11.glPushMatrix();
			
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture_empty);
			GL11.glScaled(scale, scale, scale);
			
			GL11.glTranslated(x, y, z);
			GL11.glRotated(rx, 1, 0, 0);
			GL11.glRotated(ry, 0, 1, 0);
			GL11.glRotated(rz, 0, 0, 1);
			
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture_base);
			model.renderAll();
			
			if(isRepeater){
				FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture_repeater);
			}else{
				if(isInput){
					FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture_input);
				}else{
					FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture_output);
				}
			}
			model.renderAll();
			FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture_empty);
			model.renderAll();
			
		GL11.glPopMatrix();
	}

}
