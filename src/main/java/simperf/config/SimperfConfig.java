package simperf.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Simperf全局配置信息
 * @author tinghe
 */
public class SimperfConfig {
    /**
     * 为了减小不必要的效率损失，添加config开关
     */
    private static boolean             useConfig  = false;
    public static final String         JTL_RESULT = "JTL.RESULT";
    private static Map<String, Object> attrs      = new HashMap<String, Object>();

    public static boolean hasConfig(String key) {
        if (key == null) {
            return false;
        }
        return attrs.containsKey(key);
    }

    public static Object getConfig(String key) {
        if (hasConfig(key)) {
            return attrs.get(key);
        }
        return null;
    }

    public static void setConfig(String key, Object obj) {
        attrs.put(key, obj);
        setUseConfig(true);
    }

    public static boolean isUseConfig() {
        return useConfig;
    }

    public static void setUseConfig(boolean useConfig) {
        SimperfConfig.useConfig = useConfig;
    }
}
