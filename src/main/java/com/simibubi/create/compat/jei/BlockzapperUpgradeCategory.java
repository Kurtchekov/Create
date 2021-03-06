package com.simibubi.create.compat.jei;

import static com.simibubi.create.ScreenResources.BLOCKZAPPER_UPGRADE_RECIPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.platform.GlStateManager;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.gui.ScreenElementRenderer;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.modules.curiosities.placementHandgun.BuilderGunUpgradeRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class BlockzapperUpgradeCategory implements IRecipeCategory<BuilderGunUpgradeRecipe> {

	private static ResourceLocation ID = new ResourceLocation(Create.ID, "blockzapper_upgrade");
	private IDrawable icon;

	public BlockzapperUpgradeCategory() {
		icon = new DoubleItemIcon(() -> new ItemStack(AllItems.PLACEMENT_HANDGUN.get()),
				() -> ItemStack.EMPTY); // replace with uparrow when available
	}
	
	@Override
	public IDrawable getIcon() {
		return icon;
	}
	
	@Override
	public ResourceLocation getUid() {
		return ID;
	}

	@Override
	public Class<? extends BuilderGunUpgradeRecipe> getRecipeClass() {
		return BuilderGunUpgradeRecipe.class;
	}

	@Override
	public String getTitle() {
		return Lang.translate("recipe.blockzapperUpgrade");
	}

	@Override
	public IDrawable getBackground() {
		return new ScreenResourceWrapper(BLOCKZAPPER_UPGRADE_RECIPE);
	}

	@Override
	public void setIngredients(BuilderGunUpgradeRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, BuilderGunUpgradeRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
		ShapedRecipe shape = recipe.getRecipe();
		NonNullList<Ingredient> shapedIngredients = shape.getIngredients();

		int top = 0;
		int left = 0;

		int i = 0;
		for (int y = 0; y < shape.getRecipeHeight(); y++) {
			for (int x = 0; x < shape.getRecipeWidth(); x++) {
				itemStacks.init(i, true, left + x * 18, top + y * 18);
				itemStacks.set(i, Arrays.asList(shapedIngredients.get(i).getMatchingStacks()));
				i++;
			}
		}
//		itemStacks.init(9, false, BLOCKZAPPER_UPGRADE_RECIPE.width / 2 - 9, BLOCKZAPPER_UPGRADE_RECIPE.height - 18 - 10);
//		itemStacks.set(9, recipe.getRecipeOutput());
	}

	@Override
	public List<String> getTooltipStrings(BuilderGunUpgradeRecipe recipe, double mouseX, double mouseY) {
		List<String> list = new ArrayList<>();
		if (mouseX < 91 || mouseX > 91 + 52 || mouseY < 1 || mouseY > 53)
			return list;
		list.addAll(recipe.getRecipeOutput()
				.getTooltip(Minecraft.getInstance().player,
						Minecraft.getInstance().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED
								: ITooltipFlag.TooltipFlags.NORMAL)
				.stream().map(ITextComponent::getFormattedText).collect(Collectors.toList()));
		return list;
	}

	@Override
	public void draw(BuilderGunUpgradeRecipe recipe, double mouseX, double mouseY) {
		FontRenderer font = Minecraft.getInstance().fontRenderer;
		String componentName = Lang
				.translate("blockzapper.component." + Lang.asId(recipe.getUpgradedComponent().name()));
		String text = "+ " + recipe.getTier().color + componentName;
		font.drawStringWithShadow(text,
				(BLOCKZAPPER_UPGRADE_RECIPE.width - font.getStringWidth(text)) / 2, 57, 0x8B8B8B);

		GlStateManager.pushMatrix();
		GlStateManager.translated(126, 0, 0);
		GlStateManager.scaled(3.5, 3.5, 3.5);
		GlStateManager.translated(-10, 0, 0);
		GlStateManager.color3f(1, 1, 1);
		GlStateManager.enableDepthTest();
		ScreenElementRenderer.render3DItem(() -> recipe.getRecipeOutput());
		GlStateManager.popMatrix();
	}
}