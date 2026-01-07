package cn.org.expect.database.load.serial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.org.expect.concurrent.AbstractJob;
import cn.org.expect.concurrent.EasyJob;
import cn.org.expect.concurrent.EasyJobReader;
import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.concurrent.internal.DefaultJobReader;
import cn.org.expect.database.DatabaseDialect;
import cn.org.expect.database.DatabaseTableColumn;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.database.load.DestTable;
import cn.org.expect.database.load.LoadFileRange;
import cn.org.expect.expression.DataUnitExpression;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileReader;
import cn.org.expect.io.TextTableLine;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.IO;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.Terminator;

/**
 * 处理数据文件中字段值大于数据库表中字段长度的错误
 *
 * @author jeremy8551@gmail.com
 */
public class OverLengthExceptionProcessor {
    private final static Log log = LogFactory.getLog(OverLengthExceptionProcessor.class);

    /** 最大并发任务数 */
    private int concurrent;

    public OverLengthExceptionProcessor(int concurrent) {
        this.concurrent = concurrent;
    }

    /**
     * 扫描数据文件并与目标表中字段类型进行比较，并自动扩容数据库表中字段长度
     *
     * @param context 容器上下文信息
     * @param dao     数据库接口
     * @param file    数据文件
     * @param target  目标表
     * @return 返回已修改字段个数
     * @throws Exception 发生错误
     */
    public int execute(EasyContext context, JdbcDao dao, TextTableFile file, DestTable target) throws Exception {
        // 扫描数据文件中的长度字段
        ExpandLengthJobReader in = new ExpandLengthJobReader(dao, file, target, DataUnitExpression.parse("100M").longValue());
        context.getBean(ThreadSource.class).getJobService(this.concurrent).execute(new DefaultJobReader(in));
        List<DatabaseTableColumn> columns = in.getColumns();
        in.close();

        dao.getDialect().expandLength(dao.getConnection(), target.getTable().getColumns(), columns);
        return columns.size();
    }

    /**
     * 文件分段识别输入流
     */
    private static class ExpandLengthJobReader extends Terminator implements EasyJobReader {

        /** 数据库连接 */
        private JdbcDao dao;

        /** 数据文件 */
        private TextTableFile file;

        /** 目标数据库表 */
        private DestTable target;

        /** 当前读取文件的位置，从0开始 */
        private long index;

        /** 文件的长度，单位字节 */
        private long length;

        /** 每次读取文件的长度，单位字节 */
        private long size;

        /** 发生变化（比如长度已扩展）字段的集合 */
        private Set<DatabaseTableColumn> set;

        /**
         * 初始化
         *
         * @param dao    数据库连接
         * @param file   数据文件
         * @param target 目标表信息
         * @param size   每次读取文件的长度
         */
        public ExpandLengthJobReader(JdbcDao dao, TextTableFile file, DestTable target, long size) {
            super();
            this.dao = Ensure.notNull(dao);
            this.terminate = false;
            this.index = 0;
            this.file = Ensure.notNull(file);
            this.target = Ensure.notNull(target);
            this.length = this.file.getFile().length();
            this.size = Ensure.fromOne(size);
            this.set = Collections.synchronizedSet(new HashSet<DatabaseTableColumn>());
        }

        public boolean hasNext() {
            return !this.terminate && this.index < this.length;
        }

        public EasyJob next() throws Exception {
            long begin = this.index; // 起始位置
            long end = begin + this.size; // 结束位置
            if (end > this.length) {
                end = this.length;
            }

            LoadFileExecutorContext context = new LoadFileExecutorContext();
            context.setDao(this.dao);
            context.setFile(this.file);
            context.setReadBuffer(IO.getCharArrayLength());
            context.setRange(new LoadFileRange(begin, end, -1));

            this.index = end + 1; // 下一次读取位置
            return new ExpandLengthJob(context, this.target, this.set);
        }

        public void close() {
            this.set.clear();
        }

        /**
         * 发生变化（比如长度已扩展）字段的集合
         *
         * @return 字段集合
         */
        public List<DatabaseTableColumn> getColumns() {
            return new ArrayList<DatabaseTableColumn>(this.set);
        }
    }

    /**
     * 分段扫描数据文件中的字段长度是否超过限制
     */
    private static class ExpandLengthJob extends AbstractJob {

        /** 目标表信息 */
        private final DestTable target;

        /** 上下文信息 */
        private final LoadFileExecutorContext context;

        /** 发生变化字段的集合 */
        private final Set<DatabaseTableColumn> set;

        public ExpandLengthJob(LoadFileExecutorContext context, DestTable target, Set<DatabaseTableColumn> set) {
            this.context = Ensure.notNull(context);
            this.target = Ensure.notNull(target);
            this.set = Ensure.notNull(set);
            this.setName(ResourcesUtils.getMessage("load.stdout.message009", context.getFile().getAbsolutePath(), context.getStartPointer()));
        }

        public int execute() throws Exception {
            // 创建一个表格型文件对象
            TextTableFile file = this.context.getFile();
            TextTableFileReader in = file.getReader(this.context.getStartPointer(), this.context.length(), this.context.getReadBuffer());
            try {
                int loop = this.target.getColumn();
                int[] positions = this.target.getFilePositions();
                List<DatabaseTableColumn> list = this.target.getTableColumns();
                String charsetName = this.context.getFile().getCharsetName();
                DatabaseDialect dialect = this.context.getDao().getDialect();

                // 读取文件中的记录并插入到数据库表中
                TextTableLine line;
                while ((line = in.readLine()) != null) {
                    for (int i = 0; i < loop; i++) {
                        int position = positions[i]; // 位置信息
                        String value = line.getColumn(position);

                        DatabaseTableColumn column = list.get(i);
                        if (dialect.expandLength(column, value, charsetName)) {
                            this.set.add(column);
                        }
                    }
                }

                if (log.isDebugEnabled()) {
                    log.debug("load.stdout.message010", this.getName(), in.getLineNumber());
                }
                return 0;
            } finally {
                in.close();
            }
        }
    }
}
