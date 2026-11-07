package cn.lqs.carpet.lqsaddition.mixin;

import cn.lqs.carpet.lqsaddition.LqsSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 陶罐空手取物：空手右键有物品的饰纹陶罐时直接取出内容物。
 * <p>
 * 原版空手右键只会播放放入失败音效并摇晃陶罐；
 * 持物品交互无法放入时原版返回 {@code PASS_TO_DEFAULT_BLOCK_ACTION}，
 * 同样会进入 {@code onUse}，因此这里以"陶罐内有物品"作为拦截条件。
 * 是否生效由 {@code decoratedPotEmptyHandRetrieve} 规则控制。
 */
@Mixin(DecoratedPotBlock.class)
public abstract class DecoratedPotBlockMixin
{
    @Inject(
        method = "onUse(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void lqsEmptyHandRetrieve(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir)
    {
        if (!LqsSettings.decoratedPotEmptyHandRetrieve)
        {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof DecoratedPotBlockEntity pot) || pot.getStack().isEmpty())
        {
            // 空罐保持原版的失败反馈
            return;
        }
        if (world.isClient())
        {
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }
        ItemStack stack = pot.removeStack(0);
        pot.wobble(DecoratedPotBlockEntity.WobbleType.POSITIVE);
        world.playSound(null, pos, SoundEvents.BLOCK_DECORATED_POT_INSERT, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        if (!player.getInventory().insertStack(stack))
        {
            player.dropItem(stack, false);
        }
        cir.setReturnValue(ActionResult.SUCCESS);
    }
}
