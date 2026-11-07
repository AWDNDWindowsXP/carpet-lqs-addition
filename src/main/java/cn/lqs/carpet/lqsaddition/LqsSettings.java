package cn.lqs.carpet.lqsaddition;

import carpet.api.settings.Rule;

/**
 * Carpet LQS Addition 的规则（设置项）定义类。
 * <p>
 * 所有被 {@link Rule} 标注的 public static 字段都会注册为 /carpet 规则。
 * 翻译键格式：carpet.rule.&lt;规则名&gt;.(name|desc|extra.N)（共享 settingsManager 时无扩展前缀）
 */
public class LqsSettings
{
    /**
     * 自定义规则分类，/carpet 指令中可通过该分类名筛选。
     */
    public static final String LQS = "lqs";

    /**
     * 可合成命名牌：参考 26.1+ 官方配方，1 张纸 + 1 个金属颗粒 → 1 个命名牌。
     */
    @Rule(
        categories = {LQS}
    )
    public static boolean craftableNameTag = true;

    /**
     * 陶罐空手取物：空手右键有物品的饰纹陶罐时直接取出内容物。
     */
    @Rule(
        categories = {LQS}
    )
    public static boolean decoratedPotEmptyHandRetrieve = false;

    /**
     * 高压苦力怕全头颅掉落：解除原版"每次爆炸只掉一个"的限制，每个遇害者各掉各的头。
     * 详见 {@link cn.lqs.carpet.lqsaddition.mixin.CreeperEntityMixin}。
     */
    @Rule(
        categories = {LQS}
    )
    public static boolean chargedCreeperDropsAllHeads = false;

    /**
     * 比较器透读链条：比较器可以透过铁链和各种铜链检测其背后的方块或容器。
     * 详见 {@link cn.lqs.carpet.lqsaddition.mixin.ComparatorBlockMixin}（信号读取）
     * 与 {@link cn.lqs.carpet.lqsaddition.mixin.WorldMixin}（内容变化时的更新传播）。
     */
    @Rule(
        categories = {LQS}
    )
    public static boolean comparatorReadThroughChains = false;
}
