package cn.org.expect.day;

import java.util.Locale;
import java.util.Map;

import cn.org.expect.collection.CaseSensitivMap;
import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanEntryCollection;
import cn.org.expect.ioc.EasyBeanEvent;
import cn.org.expect.ioc.EasyBeanFactory;
import cn.org.expect.ioc.EasyBeanListener;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.StringUtils;

/**
 * 国家法定假日工厂类 <br>
 * 根据 Locale 参数返回对应的国家法定假日 <br>
 * 如果参数为空默认取虚拟机默认国家的法定假日
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-04-15
 */
@EasyBean
public class NationalHolidayFactory implements EasyBeanFactory<NationalHoliday>, EasyBeanListener {
    private final static Log log = LogFactory.getLog(NationalHolidayFactory.class);

    /** 国家地区与法定假日的映射关系,如: zh_CN 与 {@linkplain NationalChinaHoliday} 映射 */
    private final Map<String, NationalHolidaySet> map;

    /**
     * 初始化
     */
    public NationalHolidayFactory(EasyContext context) {
        this.map = new CaseSensitivMap<NationalHolidaySet>();
        EasyBeanEntryCollection collection = context.getBeanEntryCollection(NationalHoliday.class);
        for (EasyBeanEntry entry : collection.values()) {
            this.add(context, entry);
        }
    }

    /**
     * 添加组件实现类
     *
     * @param context 容器上下文信息
     * @param entry   组件信息
     */
    protected synchronized void add(EasyContext context, EasyBeanEntry entry) {
        if (NationalHoliday.class.isAssignableFrom(entry.getType())) {
            String key = entry.getName(); // zh, zh_CN, ch_CN_POSIX

            // 根据区域环境信息查询对应的集合
            NationalHolidaySet set = this.map.get(key);
            if (set == null) {
                set = new NationalHolidaySet();
                this.map.put(key, set);
            }

            // 在集合中添加法定假日
            set.add((NationalHoliday) context.newInstance(entry.getType()));
        }
    }

    public NationalHoliday build(EasyContext context, Object... args) throws Exception {
        // 使用当前默认国家语言信息
        if (args.length == 0) {
            String key = this.toKey(Locale.getDefault());
            NationalHolidaySet holidaySet = this.map.get(key);
            if (holidaySet == null) {
                if (log.isWarnEnabled()) {
                    log.warn("cn.stdout.message001", key);
                }
            }
            return holidaySet;
        }

        // 查询指定国家语言信息
        Locale locale = ArrayUtils.indexOf(args, Locale.class, 0);
        if (locale != null) {
            return this.map.get(this.toKey(locale));
        }

        // 拼接字符串
        return this.map.get(StringUtils.join(args, "_"));
    }

    /**
     * 将字符串解析为地区信息
     *
     * @param locale 国家语言信息
     * @return 字符串，如: zh, zh_CN, ch_CN_POSIX
     */
    protected String toKey(Locale locale) {
        StringBuilder buf = new StringBuilder(15);
        buf.append(locale.getLanguage());
        if (StringUtils.isNotBlank(locale.getCountry())) {
            buf.append('_').append(locale.getCountry());
        }
        return buf.toString();
    }

    public void addBean(EasyBeanEvent event) {
        this.add(event.getContext(), event.getBeanEntry());
    }

    public void removeBean(EasyBeanEvent event) {
    }
}
