package cn.org.expect.codegen;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.org.expect.codegen.excel.DatabaseDesignReader;
import cn.org.expect.codegen.model.TableDesign;

/**
 * SSM 持久化层代码生成器
 */
public class SsmCodeGenerator {

    /** 工作簿读取器 */
    private final DatabaseDesignReader reader;

    /**
     * 创建代码生成器
     */
    public SsmCodeGenerator() {
        this.reader = new DatabaseDesignReader();
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

        for (TableDesign table : tables) {
            System.out.println("table: " + table);
        }
    }
}
