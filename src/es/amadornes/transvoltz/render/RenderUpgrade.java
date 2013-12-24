package es.amadornes.transvoltz.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import es.amadornes.transvoltz.lib.Items;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class RenderUpgrade implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch (type) {
		case ENTITY:
			if(item.isOnItemFrame()){
				render(-0.4, 0, 0, 0, -90, 0, 0.75, item.getItemDamage());
			}else{
				render(0, 0.25, 0.55, 0, 0, 0, 1, item.getItemDamage());
			}
			break;
		case EQUIPPED:
			render(0.8, 1, 1.2, -45, 45, 0, 1.5, item.getItemDamage());
			break;
		case EQUIPPED_FIRST_PERSON:
			render(-0.5, 1.25, 0.5, 0, -135, 0, 1, item.getItemDamage());
			break;
		case INVENTORY:
			render(0.15, 0.05, 0, -45, 45, 0, 1.5, item.getItemDamage());
			break;
		}
	}
	
	public static void render(double x, double y, double z, double rx, double ry, double rz, double scale, int metadata){
		rx = clipRotation(rx);
		ry = clipRotation(ry);
		rz = clipRotation(rz);
		
		GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			GL11.glRotated(rx, 1, 0, 0);
			GL11.glRotated(ry, 0, 1, 0);
			GL11.glRotated(rz, 0, 0, 1);
			GL11.glScaled(scale, scale, scale);
			GL11.glRotated(((System.currentTimeMillis()%720)-360)/2, 0, 0, 1);
			GL11.glTranslated(-0.5, -0.5, -0.5);
			renderItem(new ItemStack(Items.upgrade, 1, metadata));
		GL11.glPopMatrix();
	}
	
	private static double clipRotation(double angle){
		double a = angle;
		
		while(a < -180 || a >= 180){
			while(a < -180){
				a += 360;
			}
			while(a >= 180){
				a -= 360;
			}
		}
		
		return a;
	}
	
	private static void renderItem(ItemStack item){
		if(item != null){
			TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
			
			texturemanager.bindTexture(texturemanager.getResourceLocation(item.getItemSpriteNumber()));
            Tessellator tessellator = Tessellator.instance;
            float f = item.getIconIndex().getMinU();
            float f1 = item.getIconIndex().getMaxU();
            float f2 = item.getIconIndex().getMinV();
            float f3 = item.getIconIndex().getMaxV();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            ItemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, item.getIconIndex().getIconWidth(), item.getIconIndex().getIconHeight(), 0.0625F);
		}
	}
	
}