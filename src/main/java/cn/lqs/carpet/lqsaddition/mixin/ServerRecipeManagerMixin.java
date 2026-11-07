package cn.lqs.carpet.lqsaddition.mixin;

import cn.lqs.carpet.lqsaddition.CarpetLqsAddition;
import cn.lqs.carpet.lqsaddition.recipe.CraftableNameTagRecipe;
import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 在配方加载完成后注入本扩展提供的自定义配方。
 * <p>
 * 配方始终以固定身份加载，是否生效由对应规则在
 * {@link net.minecraft.recipe.Recipe#matches} 中动态判断。
 * <p>
 * 注意：不能在 {@code prepare()} 的 RETURN 处用 setReturnValue 替换返回值——
 * 该方法的返回值已被 CompletableFuture 链提前捕获，替换会抛出
 * CancellationException 导致整个数据包重载失败；
 * 因此改为在 {@code apply()} 中直接替换 preparedRecipes 字段。
 */
@Mixin(ServerRecipeManager.class)
public abstract class ServerRecipeManagerMixin
{
    @Shadow
    private PreparedRecipes preparedRecipes;

    @Inject(
        method = "apply(Lnet/minecraft/recipe/PreparedRecipes;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
        at = @At("RETURN")
    )
    private void lqsInjectRecipes(PreparedRecipes prepared, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci)
    {
        List<RecipeEntry<?>> recipes = new ArrayList<>(prepared.recipes());
        recipes.add(new RecipeEntry<>(
            RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(CarpetLqsAddition.MOD_ID, "craftable_name_tag")),
            new CraftableNameTagRecipe()
        ));
        // 在原版 apply 保存完成之后替换；后续 initialize 构建的配方书索引会基于此字段
        this.preparedRecipes = PreparedRecipes.of(recipes);
        CarpetLqsAddition.LOGGER.info("Injected craftable_name_tag recipe, total recipes: {}", recipes.size());
    }
}
