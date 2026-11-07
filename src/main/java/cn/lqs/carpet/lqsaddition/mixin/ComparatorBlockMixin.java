package cn.lqs.carpet.lqsaddition.mixin;

import cn.lqs.carpet.lqsaddition.LqsSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 比较器透读链条。
 * <p>
 * 原版 {@link ComparatorBlock#getPower(World, BlockPos, BlockState)} 中，比较器只在
 * 前方方块为实心块（{@code isSolidBlock}）时才会穿透读取其背后的方块/容器，
 * 链条不是实心块因此被挡下。此处在规则开启时让链条（铁链与所有铜链，均为
 * {@link ChainBlock}）在透读判定中视为可穿透，使比较器能检测链条背后的方块或容器。
 * 容器内容变化时的更新传播由 {@link WorldMixin} 同样放行，两处缺一不可。
 * 是否生效由 {@code comparatorReadThroughChains} 规则控制。
 */
@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixin
{
    @ModifyExpressionValue(
        method = "getPower(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)I",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;isSolidBlock(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"
        )
    )
    private boolean lqsAllowReadThroughChains(boolean original, World world, BlockPos pos, BlockState comparatorState)
    {
        if (!original && LqsSettings.comparatorReadThroughChains)
        {
            // 捕获的是外层方法参数：pos 为比较器自身坐标，链条位于其 FACING 前方一格
            BlockPos frontPos = pos.offset(comparatorState.get(ComparatorBlock.FACING));
            return world.getBlockState(frontPos).getBlock() instanceof ChainBlock;
        }
        return original;
    }
}
