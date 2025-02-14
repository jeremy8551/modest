package cn.org.expect.message;

import java.io.File;
import java.io.FileInputStream;

import cn.org.expect.util.Logs;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.StringUtils;

public class ResourceMessageExternalBundle extends ResourceMessageInternalBundle {

    public void load(ClassLoader classLoader) {
        File file = this.getExternalFile();
        if (file == null) {
            return;
        }

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            this.properties.clear();
            this.properties.load(in);
        } catch (Throwable e) {
            Logs.error("{} = {}", ResourcesUtils.PROPERTY_RESOURCE, file.getAbsolutePath(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    if (Logs.isErrorEnabled()) {
                        Logs.error(file.getAbsolutePath(), e);
                    }
                }
            }
        }
    }

    /**
     * 返回外部设置的国际化资源文件
     *
     * @return 外部资源文件
     */
    public File getExternalFile() {
        String filepath = System.getProperty(ResourcesUtils.PROPERTY_RESOURCE);
        if (StringUtils.isBlank(filepath)) {
            return null;
        }

        File file = new File(filepath);
        if (!file.exists()) {
            if (Logs.isErrorEnabled()) {
                Logs.error("{} bundle resource file {} not found!", ResourcesUtils.PROPERTY_RESOURCE, filepath);
            }
            return null;
        }

        if (!file.isFile()) {
            if (Logs.isErrorEnabled()) {
                Logs.error("{} bundle resource file {} is not a file!", ResourcesUtils.PROPERTY_RESOURCE, filepath);
            }
            return null;
        }

        return file;
    }
}
