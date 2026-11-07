package cn.lqs.carpet.lqsaddition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.utils.Translations;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Carpet LQS Addition 扩展入口。
 * <p>
 * 通过 fabric.mod.json 中的 {@code carpet} 入口点注册为 Carpet 扩展，
 * 同时作为常规 Fabric {@link ModInitializer} 以处理初始化逻辑。
 */
public class CarpetLqsAddition implements CarpetExtension, ModInitializer
{
    public static final String MOD_ID = "carpet-lqs-addition";
    /** 资源命名空间：路径中的连字符须转为下划线，与 assets 目录一致 */
    public static final String ASSET_NS = "carpet_lqs_addition";
    public static final String MOD_NAME = "Carpet LQS Addition";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private static CarpetLqsAddition instance;

    public CarpetLqsAddition()
    {
        instance = this;
        CarpetServer.manageExtension(this);
    }

    public static CarpetLqsAddition getInstance()
    {
        return instance;
    }

    @Override
    public void onInitialize()
    {
        LOGGER.info("{} initialized", MOD_NAME);
    }

    /**
     * Carpet 扩展的唯一标识，决定了配置文件名（carpet-lqs-addition.conf）
     * 以及 /carpet 指令中规则所属的扩展分组。
     */
    @Override
    public String version()
    {
        return MOD_ID;
    }

    @Override
    public void onGameStarted()
    {
        // 解析规则类，注册所有 @Rule 标注的设置项
        CarpetServer.settingsManager.parseSettingsClass(LqsSettings.class);
    }

    /**
     * 向 carpet 提供本扩展的规则翻译。
     * <p>
     * 使用共享 settingsManager 时，规则翻译键格式为 {@code carpet.rule.<规则名>.name|desc}，
     * 缺少必需键会导致 {@code parseSettingsClass} 直接抛出空指针异常。
     */
    @Override
    public Map<String, String> canHasTranslations(String lang)
    {
        String path = String.format("assets/%s/lang/%s.json", ASSET_NS, lang);
        if (CarpetLqsAddition.class.getClassLoader().getResource(path) == null)
        {
            // 未提供该语言时回退到英文，避免缺少必需翻译键导致规则解析崩溃
            path = String.format("assets/%s/lang/en_us.json", ASSET_NS);
        }
        return Translations.getTranslationFromResourcePath(path);
    }
}
