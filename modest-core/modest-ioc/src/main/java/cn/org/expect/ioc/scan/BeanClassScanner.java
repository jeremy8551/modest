package cn.org.expect.ioc.scan;

import java.util.ArrayList;
import java.util.List;

import cn.org.expect.ioc.EasyContext;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.SPI;
import cn.org.expect.util.StringUtils;

/**
 * 组件的类扫描器
 *
 * @author jeremy8551@qq.com
 * @createtime 2023/10/5
 */
public class BeanClassScanner {

    /**
     * 初始化
     */
    public BeanClassScanner() {
    }

    /**
     * 加载组件信息
     *
     * @param context 组件上下文信息
     * @param args    参数
     * @return 返回加载组件的个数
     */
    public int load(EasyContext context, String[] args) {
        List<String> packageNames = ArrayUtils.asList(args);
        List<String> includePackageNames = new ArrayList<String>();
        List<String> excludePackageNames = new ArrayList<String>();
        this.parse(packageNames, includePackageNames, excludePackageNames);
        List<ClassScanRule> processors = SPI.load(context.getClassLoader(), ClassScanRule.class);
        ClassScanner scanner = new ClassScanner(context.getClassLoader(), includePackageNames, excludePackageNames, processors);
        return scanner.load(context); // 扫描包
    }

    /**
     * 解析参数 {@code packageNames}，将扫描的包名与排除的包名，分别添加到第二个参数与第三个参数中
     *
     * @param packageNames        包扫描配置
     * @param includePackageNames 扫描的包名
     * @param excludePackageNames 排除的包名
     */
    private void parse(List<String> packageNames, List<String> includePackageNames, List<String> excludePackageNames) {
        for (String str : packageNames) {
            String[] array = StringUtils.split(str, ',');
            for (String value : array) {
                if (StringUtils.isNotBlank(value)) {
                    EasyScanPattern pattern = new EasyScanPattern(value);
                    if (pattern.isBlank()) {
                        continue;
                    }

                    if (pattern.isExclude()) {
                        excludePackageNames.add(pattern.getPrefix());
                    } else {
                        includePackageNames.add(pattern.getPrefix());
                    }
                }
            }
        }
    }
}
