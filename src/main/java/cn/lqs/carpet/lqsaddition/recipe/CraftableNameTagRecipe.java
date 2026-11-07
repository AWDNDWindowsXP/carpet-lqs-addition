package cn.lqs.carpet.lqsaddition.recipe;

import cn.lqs.carpet.lqsaddition.LqsSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.world.World;

import java.util.List;

/**
 * 命名牌合成配方，参考 26.1+ 官方配方：
 * 1 张纸 + 1 个任意金属颗粒（铜粒/铁粒/金粒） → 1 个命名牌。
 * <p>
 * 实际是否可合成由 {@code craftableNameTag} 规则控制。
 */
public class CraftableNameTagRecipe extends ShapelessRecipe
{
    public CraftableNameTagRecipe()
    {
        super(
            "",
            CraftingRecipeCategory.MISC,
            new ItemStack(Items.NAME_TAG),
            List.of(
                Ingredient.ofItem(Items.PAPER),
                // 26.1+ 使用 #metal_nuggets 标签，1.21.11 中该标签尚不存在，直接列举三种颗粒
                Ingredient.ofItems(Items.COPPER_NUGGET, Items.IRON_NUGGET, Items.GOLD_NUGGET)
            )
        );
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world)
    {
        return LqsSettings.craftableNameTag && super.matches(input, world);
    }
}
