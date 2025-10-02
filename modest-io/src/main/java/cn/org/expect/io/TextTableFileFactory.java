package cn.org.expect.io;

import cn.org.expect.ioc.EasyBeanEntry;
import cn.org.expect.ioc.EasyBeanFactory;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.Attribute;
import cn.org.expect.util.CharsetName;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;
import cn.org.expect.util.StringUtils;

/**
 * 从容器上下文信息 {@linkplain EasyContext} 中返回一个 {@linkplain TextTableFile} 表格型文件对象 <br>
 * 第一参数必须是 {@linkplain TextTableFile} <br>
 * 第二个参数必须是文件类型 <br>
 * 第三个参数必须是 {@linkplain Attribute} 对象的引用，属性集合中可以设置 charset，codepage，chardel，rowdel，coldel，escape，column，colname
 *
 * @author jeremy8551@gmail.com
 */
@EasyBean
public class TextTableFileFactory implements EasyBeanFactory<TextTableFile> {

    @SuppressWarnings("unchecked")
    public TextTableFile build(EasyContext context, Object... args) throws Exception {
        // 查询参数中一定要有文件类型
        String name = Ensure.notBlank(ArrayUtils.indexOf(args, String.class, 0));

        // 根据文件类型查询对应的组件
        EasyBeanEntry entry = Ensure.notNull(context.getBeanEntry(TextTableFile.class, name));

        // 创建文件，并设置属性
        TextTableFile file = context.newInstance(entry.getType());
        for (Object obj : args) {
            if (obj instanceof Attribute) {
                Attribute<String> attribute = (Attribute<String>) obj;
                this.setProperty(context, file, attribute); // 设置数据类型的属性
            }
        }
        return file;
    }

    public void setProperty(EasyContext context, TextTableFile file, Attribute<String> attribute) {
        if (attribute.contains("charset") && attribute.contains("codepage")) {
            throw new IllegalArgumentException();
        } else if (attribute.contains("charset")) {
            file.setCharsetName(attribute.getAttribute("charset"));
        } else if (attribute.contains("codepage")) {
            file.setCharsetName(FileUtils.getCodepage(StringUtils.trimBlank(attribute.getAttribute("codepage"))));
        }

        if (attribute.contains("chardel")) {
            file.setCharDelimiter(attribute.getAttribute("chardel"));
        }

        if (attribute.contains("rowdel")) {
            file.setLineSeparator(StringUtils.unescape(attribute.getAttribute("rowdel")));
        }

        if (attribute.contains("coldel")) {
            file.setDelimiter(attribute.getAttribute("coldel"));
        }

        if (attribute.contains("escape")) {
            String escape = attribute.getAttribute("escape");
            if (StringUtils.length(escape, CharsetName.ISO_8859_1) == 1) {
                file.setEscape(escape.charAt(0));
            } else {
                throw new IllegalArgumentException(escape);
            }
        }

        if (attribute.contains("column")) {
            file.setColumn(Integer.parseInt(attribute.getAttribute("column")));
        }

        // 单独设置表格列名
        if (attribute.contains("colname")) { // name1,2:name2,4:name3
            char s = ',';
            char m = ':';
            String[] names = StringUtils.split(attribute.getAttribute("colname"), s);
            for (int i = 0; i < names.length; i++) {
                String expr = names[i];
                if (StringUtils.isBlank(expr)) {
                    continue;
                }

                if (expr.indexOf(m) != -1) {
                    String[] property = StringUtils.splitProperty(expr, m);
                    if (property != null) {
                        if (StringUtils.isNumber(property[0])) {
                            file.setColumnName(Integer.parseInt(property[0]), property[1]);
                            continue;
                        } else {
                            throw new IllegalArgumentException(property[0]);
                        }
                    }
                } else {
                    file.setColumnName(i + 1, expr);
                }
            }
        }
    }
}
