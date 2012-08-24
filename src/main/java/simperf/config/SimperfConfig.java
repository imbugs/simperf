package simperf.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Simperf全局配置信息
 * @author imbugs
 */
public class SimperfConfig {
    /**
     * 为了减小不必要的效率损失，添加config开关
     */
    private static boolean             useConfig  = false;
    public static final String         JTL_RESULT = "JTL.RESULT";
    private static Map<String, Object> attributes = new HashMap<String, Object>();

    public static boolean hasConfig(String key) {
        if (key == null) {
            return false;
        }
        return attributes.containsKey(key);
    }

    public static Object getConfig(String key) {
        if (hasConfig(key)) {
            return attributes.get(key);
        }
        return null;
    }

    public static void setConfig(String key, Object obj) {
        attributes.put(key, obj);
        setUseConfig(true);
    }

    public static String getString(String key) {
        return String.valueOf(attributes.get(key));
    }

    public static boolean getBoolean(String key) {
        Object obj = attributes.get(key);
        if (null == obj) {
            return false;
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        return Boolean.valueOf(getString(key));
    }

    public static int getInt(String key) {
        Object obj = attributes.get(key);
        if (null == obj) {
            return 0;
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        return Integer.valueOf(getString(key));
    }

    public static boolean isUseConfig() {
        return useConfig;
    }

    public static void setUseConfig(boolean useConfig) {
        SimperfConfig.useConfig = useConfig;
    }
}
