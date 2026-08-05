package cn.org.expect.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import cn.org.expect.codegen.config.GeneratorConfig;
import cn.org.expect.codegen.excel.DatabaseDesignReader;
import cn.org.expect.codegen.model.ColumnDesign;
import cn.org.expect.codegen.model.TableDesign;
import cn.org.expect.codegen.render.CodeRenderer;

/**
 * SSM 持久化层代码生成器
 */
public class SsmCodeGenerator {

    /** 生成配置 */
    private final GeneratorConfig config;

    /** 工作簿读取器 */
    private final DatabaseDesignReader reader;

    /** 代码渲染器 */
    private final CodeRenderer renderer;

    /**
     * 创建代码生成器
     *
     * @param config 生成配置，不能为 null
     */
    public SsmCodeGenerator(GeneratorConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("生成配置不能为空");
        }

        this.config = config;
        this.reader = new DatabaseDesignReader();
        this.renderer = new CodeRenderer(config);
    }

    /**
     * 根据数据库设计工作簿生成代码
     *
     * @param workbookFile    数据库设计工作簿
     * @param outputDirectory 输出工程目录
     * @throws IOException 读取或写入文件失败时抛出
     */
    public void generate(File workbookFile, File outputDirectory) throws IOException {
        List<TableDesign> tables = this.reader.read(workbookFile);
        if (tables.isEmpty()) {
            throw new IllegalArgumentException("工作簿中未找到包含必要列名的数据库表设计");
        }

        File javaRoot = new File(outputDirectory, "src/main/java/" + this.config.basePackage().replace('.', '/'));
        this.write(new File(outputDirectory, "src/main/resources/sql/create_table.sql"), this.renderer.renderDdl(tables));
        for (TableDesign table : tables) {
            String entityName = this.renderer.entityName(table);
            this.write(new File(javaRoot, "entity/" + entityName + ".java"), this.renderer.renderEntity(table));
            this.write(new File(javaRoot, "mapper/" + entityName + "Mapper.java"), this.renderer.renderMapper(table));
            this.write(new File(javaRoot, "dao/" + entityName + "Dao.java"), this.renderer.renderDao(table));
            this.write(new File(outputDirectory, "src/main/resources/mapper/nas/" + entityName + "Dao.xml"), this.renderer.renderMapperXml(table));
            for (ColumnDesign column : table.columns()) {
                if (column.dictionary().length() > 0) {
                    this.write(new File(javaRoot, "enums/" + this.renderer.enumName(table, column) + ".java"), this.renderer.renderEnum(table, column));
                }
            }
        }
    }

    /**
     * 使用 UTF-8 写入文件
     *
     * @param file    输出文件
     * @param content 文件内容
     * @throws IOException 创建目录或写入文件失败时抛出
     */
    private void write(File file, String content) throws IOException {
        File parent = file.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs() && !parent.isDirectory()) {
            throw new IOException("创建目录失败: " + parent.getAbsolutePath());
        }

        OutputStream outputStream = new FileOutputStream(file);
        Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
        try {
            writer.write(content);
        } finally {
            writer.close();
        }
    }
}
