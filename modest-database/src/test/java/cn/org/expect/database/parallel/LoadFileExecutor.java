package cn.org.expect.database.parallel;

import cn.org.expect.concurrent.AbstractJob;
import cn.org.expect.database.load.inernal.DataWriter;
import cn.org.expect.database.load.inernal.DataWriterFactory;
import cn.org.expect.database.load.serial.LoadFileExecutorContext;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileReader;
import cn.org.expect.io.TextTableLine;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.IO;

/**
 * 数据文件装载类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2021-06-09
 */
public class LoadFileExecutor extends AbstractJob {
    private final static Log log = LogFactory.getLog(LoadFileExecutor.class);

    /** 文件装载功能的上下文信息 */
    protected LoadFileExecutorContext context;

    /** 数据库输出流 */
    protected DataWriterFactory factory;

    /** 运算结果集 */
    protected ResultSet resultSet;

    /**
     * 创建一个文件加载器
     *
     * @param context   上下文信息
     * @param factory   数据库输出流工厂
     * @param resultSet 装数功能的结果集
     */
    public LoadFileExecutor(LoadFileExecutorContext context, DataWriterFactory factory, ResultSet resultSet) {
        super();

        if (context == null) {
            throw new NullPointerException();
        }
        if (factory == null) {
            throw new NullPointerException();
        }
        if (resultSet == null) {
            throw new NullPointerException();
        }

        this.context = context;
        this.factory = factory;
        this.resultSet = resultSet;
    }

    public int execute() throws Exception {
        // 创建一个表格型文件对象
        TextTableFile file = this.context.getFile();
        DataWriter out = null;
        TextTableFileReader in = file.getReader(this.context.getStartPointer(), this.context.length(), this.context.getReadBuffer());
        try {
            out = this.factory.create();

            // 读取文件中的记录并插入到数据库表中
            TextTableLine line = null;
            while ((line = in.readLine()) != null) {
                out.write(line);
            }
            out.commit();

            // 保存统计信息
            this.resultSet.addTotal(out.getCommitRecords(), out.getSkipRecords(), out.getCommitRecords(), out.getDeleteRecords(), out.getRejectedRecords());

            if (log.isTraceEnabled()) {
                log.trace("load.stdout.message010", this.getName(), out.getCommitRecords());
            }
            return 0;
        } finally {
            IO.close(in, out);
        }
    }
}
