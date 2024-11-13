package cn.org.expect.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletUtils {

    /**
     * 下载文件
     *
     * @param response HttpServletResponse对象
     * @param file     下载文件
     * @param filename 下载后显示文件名
     * @throws IOException 输入流发生错误
     */
    private static void downFile(HttpServletResponse response, File file, String filename) throws IOException {
        response.setContentType("APPLICATION/OCTET-STREAM");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + StringUtils.defaultString(filename, file.getName()) + "\"");
        ServletOutputStream out = response.getOutputStream();
        InputStream in = new FileInputStream(file);
        IO.write(in, out, null);
    }

    /**
     * 下载文件
     *
     * @param request  HttpServletRequest对象
     * @param response HttpServletResponse对象
     * @param file     下载文件
     * @param filename 下载后显示文件名
     * @throws IOException 输入流发生错误
     */
    public static void downFile(HttpServletRequest request, HttpServletResponse response, File file, String filename) throws IOException {
        String name = encodeFilename(request, StringUtils.defaultString(filename, file.getName()));
        downFile(response, file, name);
    }

    /**
     * 根据浏览器类型设置文件编码
     *
     * @param request  HttpServletRequest对象
     * @param filename 文件名
     * @return 文件名编码
     * @throws IOException 输入流发生错误
     */
    public static String encodeFilename(HttpServletRequest request, String filename) throws IOException {
        if (request == null) {
            throw new NullPointerException();
        }
        if (filename == null) {
            throw new NullPointerException();
        }

        String header = request.getHeader("User-Agent");
        if (header == null || header.toLowerCase().contains("msie")) {
            return URLEncoder.encode(filename, CharsetName.UTF_8);
        } else {
            byte[] array = StringUtils.toBytes(StringUtils.removeBlank(filename), StringUtils.CHARSET);
            return StringUtils.toString(array, CharsetName.ISO_8859_1);
        }
    }

}
