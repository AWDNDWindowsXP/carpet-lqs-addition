package cn.lqs.carpet.lqsaddition.mixin;

import cn.lqs.carpet.lqsaddition.LqsSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChainBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 比较器透读链条的更新传播补丁。
 * <p>
 * 容器内容变化（{@code BlockEntity.markDirty}）或方块放置/破坏（{@code setBlockState}）
 * 会调用 {@link World#updateComparators(BlockPos, Block)} 通知周围的比较器重算输出。
 * 该方法对每个水平方向检查：邻格是比较器则直接通知；否则要求中间方块
 * {@code isSolidBlock} 才把通知转发给其背后的比较器（即原版透读实心块的更新路径）。
 * 链条不是实心块，通知在此被挡下，导致比较器输出永不重算——
 * 即使 {@code ComparatorBlockMixin} 已放开读取判定也无法检测。
 * 此处在规则开启时让链条（铁链与所有铜链，均为 {@link ChainBlock}）同样放行通知。
 */
@Mixin(World.class)
public abstract class WorldMixin
{
    @ModifyExpressionValue(
        method = "updateComparators(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"
        )
    )
    private boolean lqsAllowComparatorUpdateThroughChains(boolean original, @Local BlockState blockState)
    {
        if (!original && LqsSettings.comparatorReadThroughChains)
        {
            // blockState 为容器与比较器之间的中间方块（循环局部变量）
            return blockState.getBlock() instanceof ChainBlock;
        }
        return original;
    }
}
