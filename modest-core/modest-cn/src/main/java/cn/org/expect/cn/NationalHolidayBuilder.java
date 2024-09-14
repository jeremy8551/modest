package cn.org.expect.cn;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.org.expect.annotation.EasyBean;
import cn.org.expect.collection.CaseSensitivMap;
import cn.org.expect.ioc.EasyetlBeanEventListener;
import cn.org.expect.ioc.EasyetlBeanBuilder;
import cn.org.expect.ioc.EasyetlBeanEvent;
import cn.org.expect.ioc.EasyetlBean;
import cn.org.expect.ioc.EasyetlBeanDefine;
import cn.org.expect.ioc.EasyetlContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

/**
 * 国家法定假日工厂类 <br>
 * 根据 Locale 参数返回对应的国家法定假日 <br>
 * 如果参数为空默认取虚拟机默认国家的法定假日
 *
 * @author jeremy8551@qq.com
 * @createtime 2021-04-15
 */
@EasyBean
public class NationalHolidayBuilder implements EasyetlBeanBuilder<NationalHoliday>, EasyetlBeanEventListener {
    private final static Log log = LogFactory.getLog(NationalHolidayBuilder.class);

    /** 国家地区与法定假日的映射关系,如: zh_CN 与 {@linkplain NationalChinaHoliday} 映射 */
    private Map<String, NationalHolidaySet> map;

    /**
     * 初始化
     */
    public NationalHolidayBuilder(EasyetlContext context) {
        this.map = new CaseSensitivMap<NationalHolidaySet>();
        this.addAll(context);
    }

    public void addAll(EasyetlContext context) {
        List<EasyetlBean> list = context.getBeanInfoList(NationalHoliday.class);
        for (EasyetlBean beanInfo : list) {
            this.add(context, beanInfo);
        }
    }

    /**
     * 添加组件实现类
     *
     * @param context  容器上下文信息
     * @param beanInfo 组件信息
     */
    protected synchronized void add(EasyetlContext context, EasyetlBean beanInfo) {
        if (NationalHoliday.class.isAssignableFrom(beanInfo.getType())) {
            String key = beanInfo.getName(); // zh, zh_CN, ch_CN_POSIX

            // 根据区域环境信息查询对应的集合
            NationalHolidaySet set = this.map.get(key);
            if (set == null) {
                set = new NationalHolidaySet();
                this.map.put(key, set);
            }

            // 在集合中添加法定假日
            NationalHoliday holiday = context.createBean(beanInfo.getType());
            set.add(holiday);
        }
    }

    public NationalHoliday getBean(EasyetlContext context, Object... args) throws Exception {
        // 使用当前默认国家语言信息
        if (args.length == 0) {
            String key = this.toKey(Locale.getDefault());
            NationalHolidaySet holidaySet = this.map.get(key);
            if (holidaySet == null) {
                if (log.isWarnEnabled()) {
                    log.warn(ResourcesUtils.getMessage("cn.standard.output.msg001", key));
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

    public void addBean(EasyetlBeanEvent event) {
        EasyetlBeanDefine beanInfo = event.getBeanInfo();
        if (NationalHoliday.class.isAssignableFrom(beanInfo.getType())) {
            this.add(event.getContext(), beanInfo);
        }
    }

    public void removeBean(EasyetlBeanEvent event) {
    }

}
