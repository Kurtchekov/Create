package com.simibubi.create.modules.logistics.block;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;

public class LinkedExtractorTileEntityRenderer extends TileEntityRenderer<LinkedExtractorTileEntity> {

	LinkedTileEntityRenderer linkRenderer;
	FilteredTileEntityRenderer filterRenderer;

	public LinkedExtractorTileEntityRenderer() {
		linkRenderer = new LinkedTileEntityRenderer();
		filterRenderer = new FilteredTileEntityRenderer();
	}

	@Override
	public void render(LinkedExtractorTileEntity tileEntityIn, double x, double y, double z, float partialTicks,
			int destroyStage) {
		super.render(tileEntityIn, x, y, z, partialTicks, destroyStage);
		linkRenderer.render(tileEntityIn, x, y, z, partialTicks, destroyStage);
		filterRenderer.render(tileEntityIn, x, y, z, partialTicks, destroyStage);
	}

}
