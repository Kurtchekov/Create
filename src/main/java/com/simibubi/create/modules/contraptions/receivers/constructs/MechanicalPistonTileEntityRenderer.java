package com.simibubi.create.modules.contraptions.receivers.constructs;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.GL11;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.utility.PlacementSimulationWorld;
import com.simibubi.create.modules.contraptions.base.IRotate;
import com.simibubi.create.modules.contraptions.base.KineticTileEntity;
import com.simibubi.create.modules.contraptions.base.KineticTileEntityRenderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.feature.template.Template.BlockInfo;
import net.minecraftforge.client.model.data.EmptyModelData;

public class MechanicalPistonTileEntityRenderer extends KineticTileEntityRenderer {

	protected static Cache<TranslationConstruct, TranslationConstructVertexBuffer> cachedConstructs;
	protected static PlacementSimulationWorld renderWorld;

	@Override
	public void renderTileEntityFast(KineticTileEntity te, double x, double y, double z, float partialTicks,
			int destroyStage, BufferBuilder buffer) {
		super.renderTileEntityFast(te, x, y, z, partialTicks, destroyStage, buffer);

		// SPECIAL RENDER
		MechanicalPistonTileEntity pistonTe = (MechanicalPistonTileEntity) te;

		if (!pistonTe.running)
			return;

		cacheConstructIfMissing(pistonTe.movingConstruct);
		renderConstructFromCache(pistonTe.movingConstruct, pistonTe, x, y, z, partialTicks, buffer);

	}

	protected void cacheConstructIfMissing(TranslationConstruct c) {
		if (cachedConstructs == null)
			cachedConstructs = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.SECONDS).build();
		if (cachedConstructs.getIfPresent(c) != null)
			return;
		if (renderWorld == null || renderWorld.getWorld() != Minecraft.getInstance().world)
			renderWorld = new PlacementSimulationWorld(Minecraft.getInstance().world);

		BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
		BlockModelRenderer blockRenderer = dispatcher.getBlockModelRenderer();
		Random random = new Random();
		BufferBuilder builder = new BufferBuilder(0);
		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		builder.setTranslation(0, 0, 0);

		for (BlockInfo info : c.blocks.values()) {
			renderWorld.setBlockState(info.pos, info.state);
		}

		for (BlockInfo info : c.blocks.values()) {
			IBakedModel originalModel = dispatcher.getModelForState(info.state);
			blockRenderer.renderModel(renderWorld, originalModel, info.state, info.pos, builder, true, random, 42,
					EmptyModelData.INSTANCE);
		}

		builder.finishDrawing();
		renderWorld.clear();
		cachedConstructs.put(c, new TranslationConstructVertexBuffer(builder.getByteBuffer()));
	}

	protected void renderConstructFromCache(TranslationConstruct c, MechanicalPistonTileEntity te, double x, double y,
			double z, float partialTicks, BufferBuilder buffer) {
		final Vec3d offset = te.getConstructOffset(partialTicks);
		buffer.putBulkData(cachedConstructs.getIfPresent(c).getTransformed(te,
				(float) (x + offset.x - te.getPos().getX()), (float) (y + offset.y - te.getPos().getY()),
				(float) (z + offset.z - te.getPos().getZ()), offset));
	}

	@Override
	protected BlockState getRenderedBlockState(KineticTileEntity te) {
		return AllBlocks.SHAFT.block.getDefaultState().with(BlockStateProperties.AXIS,
				((IRotate) te.getBlockState().getBlock()).getRotationAxis(te.getBlockState()));
	}
	
	public static void invalidateCache() {
		if (cachedConstructs != null)
			cachedConstructs.invalidateAll();
	}

}
