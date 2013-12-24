package es.amadornes.transvoltz.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelAntenna extends ModelBase {
	// fields
	ModelRenderer head;
	ModelRenderer body;

	public ModelAntenna() {
		textureWidth = 32;
		textureHeight = 32;

		head = new ModelRenderer(this, 4, 0);
		head.addBox(0F, 0F, 0F, 2, 3, 2);
		head.setRotationPoint(-1F, 15F, -1F);
		head.setTextureSize(32, 32);
		head.mirror = true;
		setRotation(head, 0F, 0F, 0F);
		body = new ModelRenderer(this, 0, 0);
		body.addBox(0F, 0F, 0F, 1, 6, 1);
		body.setRotationPoint(-0.5F, 18F, -0.5F);
		body.setTextureSize(32, 32);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		head.render(f5);
		body.render(f5);
	}
	
	public void render(){
		float f5 = 0.0625F;
		head.render(f5);
		body.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
