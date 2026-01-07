package cn.org.expect.database.load.serial;

import java.io.File;
import java.util.Date;
import java.util.List;

import cn.org.expect.database.DatabaseTable;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.database.load.DestTable;
import cn.org.expect.database.load.IndexOperation;
import cn.org.expect.database.load.LoadEngineContext;
import cn.org.expect.database.load.LoadException;
import cn.org.expect.database.load.LoadFileMessage;
import cn.org.expect.database.load.LoadListenerFactory;
import cn.org.expect.database.load.LoadMode;
import cn.org.expect.database.load.Loader;
import cn.org.expect.database.load.inernal.DataWriter;
import cn.org.expect.database.load.inernal.DataWriterFactory;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileReader;
import cn.org.expect.io.TextTableLine;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.EasyContextAware;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.Terminator;
import cn.org.expect.util.TimeWatch;

/**
 * 按文件中出现顺序读取数据文件并装载到数据库表中
 *
 * @author jeremy8551@gmail.com
 */
@EasyBean(value = "serial")
public class SerialLoadFileEngine extends Terminator implements Loader, EasyContextAware {
    private final static Log log = LogFactory.getLog(SerialLoadFileEngine.class);

    protected EasyContext context;

    /**
     * 初始化
     */
    public SerialLoadFileEngine() {
        super();
        this.terminate = false;
    }

    public void setContext(EasyContext context) {
        this.context = context;
    }

    public void execute(LoadEngineContext context) throws Exception {
        JdbcDao dao = new JdbcDao(this.context);
        try {
            dao.connect(context.getDataSource());
            this.execute(dao, context);
            dao.commit();
        } finally {
            dao.close();
        }
    }

    /**
     * 装载数据文件
     *
     * @param dao     数据库操作接口
     * @param context 装数引擎上下文信息
     * @throws Exception 装载数据发生错误
     */
    protected void execute(JdbcDao dao, LoadEngineContext context) throws Exception {
        String tableCatalog = StringUtils.toCase(context.getTableCatalog(), false, null);
        String tableSchema = StringUtils.toCase(context.getTableSchema(), false, null);
        String tableName = StringUtils.toCase(context.getTableName(), false, null);

        DatabaseTable table = dao.getTable(tableCatalog, tableSchema, tableName);
        if (table == null) {
            throw new UnsupportedOperationException(tableCatalog + ", " + tableSchema + ", " + tableName);
        }

        DestTable target = new DestTable(dao, table);
        target.open(context);
        try {
            IndexOperation operation = new IndexOperation(table);
            operation.before(context, dao); // 前置操作
            this.execute(dao, context, target); // 装载数据
            operation.after(context, dao); // 后置操作
            dao.commit();
        } finally {
            target.close();
        }
    }

    /**
     * 装载数据文件
     *
     * @param dao     数据库操作接口
     * @param context 数据装载上下文信息
     * @param target  目标表信息
     * @throws Exception 装载数据发生错误
     */
    protected void execute(JdbcDao dao, LoadEngineContext context, DestTable target) throws Exception {
        DataWriterFactory factory = new DataWriterFactory(dao, context, target);
        try {
            DatabaseTable table = target.getTable();
            if (context.getLoadMode() == LoadMode.REPLACE) { // 先清空表在装入数据
                String sql = dao.deleteTableQuickly(table.getCatalog(), table.getSchema(), table.getName());
                if (log.isDebugEnabled()) {
                    log.debug(sql);
                }
                dao.commit();
            }

            // 执行批量插入，向数据库表中大批量插入数据
            DataWriter out = factory.newInstance();
            List<String> files = context.getFiles(); // 按顺序逐个加载文件中的内容
            for (int i = 0; !this.terminate && i < files.size(); i++) {
                String filepath = files.get(i);
                TextTableFile file = this.context.getBean(TextTableFile.class, context.getFiletype(), context); // CommandAttribute.tofile(context, filepath, context.getFiletype());
                file.setAbsolutePath(filepath);

                try {
                    this.execute(context, target, file, null, out);
                } catch (Throwable e) {
                    if (log.isErrorEnabled()) { // 输出批量错误信息
                        log.error(filepath, e);
                    }

                    // 重新建表
                    if (dao.getDialect().isRebuildTableException(e)) {
                        if (log.isDebugEnabled()) {
                            log.debug("load.stdout.message014", context.getName(), table.getFullName());
                        }

                        RebuildTableExceptionProcessor obj = new RebuildTableExceptionProcessor();
                        obj.execute(dao, target);
                        this.execute(context, target, file, null, out);
                        continue;
                    }

                    // 字段值超长
                    if (dao.getDialect().isOverLengthException(e)) {
                        if (log.isDebugEnabled()) {
                            log.debug("load.stdout.message015", context.getName(), table.getFullName());
                        }

                        int thread = StringUtils.parseInt(context.getAttributes().getAttribute("thread"), 2); // 并发任务数
                        OverLengthExceptionProcessor obj = new OverLengthExceptionProcessor(thread);
                        if (obj.execute(this.context, dao, file, target) > 0) { // 扩展字段完成后重新执行装数
                            this.execute(context, target, file, null, out);
                            continue;
                        }
                    }

                    // 主键冲突
                    if (dao.getDialect().isPrimaryRepeatException(e)) {
                        if (log.isDebugEnabled()) {
                            log.debug("load.stdout.message016", context.getName(), table.getFullName());
                        }

                        PrimaryRepeatExceptionProcessor obj = new PrimaryRepeatExceptionProcessor(context);
                        if (obj.execute(dao, context, target.getTable(), file, out)) {
                            this.execute(context, target, file, obj.getReader(), out);
                            continue;
                        }
                    }

                    throw new LoadException("load.stdout.message019", context.getName(), file.getAbsolutePath(), e);
                }
            }
        } finally {
            factory.close();
        }
    }

    /**
     * 装载数据文件
     *
     * @param context 装载引擎上下文信息
     * @param target  数据库表
     * @param file    文件
     * @param reader  输入流
     * @param out     输出流
     * @throws Exception 装载数据发生错误
     */
    protected synchronized void execute(LoadEngineContext context, DestTable target, TextTableFile file, TextTableFileReader reader, DataWriter out) throws Exception {
        TimeWatch watch = new TimeWatch();
        LoadFileMessage msgfile = new LoadFileMessage(context, file);

        // 不能重复加载
        if (context.isNorepeat() // 如果设置了 norepeat 属性，需要检查是否重复装载数据文件
            && msgfile.getStartTime() != null //
            && msgfile.getEndTime() != null //
            && msgfile.getFileModified() != null //
            && msgfile.getFileModified().equals(new Date(file.getFile().lastModified())) //
            && msgfile.getStartTime().compareTo(msgfile.getFileModified()) >= 0 //
            && msgfile.getEndTime().compareTo(msgfile.getStartTime()) >= 0 //
            && context.getLoadMode() == msgfile.getLoadMode() //
            && file.getFile().equals(new File(msgfile.getFile())) //
        ) {
            String fullName = target.getTable().getFullName();
            if (log.isWarnEnabled()) {
                log.warn("load.stdout.message017", context.getName(), file.getFile().getAbsolutePath(), fullName);
            }
            return;
        } else {
            msgfile.setEndTime(null);
        }

        msgfile.setStartTime(new Date());
        msgfile.setFile(file);
        msgfile.setFileModified(file.getFile().lastModified());
        msgfile.setFileType(context.getFiletype());
        msgfile.setFileColumns(target.getFilePositions());
        msgfile.setCharsetName(file.getCharsetName());
        msgfile.setColumn(0);
        msgfile.setLoadMode(context.getLoadMode());
        msgfile.setTableCatalog(target.getTable().getCatalog());
        msgfile.setTableSchema(target.getTable().getSchema());
        msgfile.setTableName(target.getTable().getName());
        msgfile.setTableColumns(target.getTableColumns());

        TextTableFileReader in;
        if (reader == null) {
            in = file.getReader(context.getReadBuffer());
        } else {
            in = reader;
        }

        try {
            in.setListener(LoadListenerFactory.newInstance(context));

            // 逐行从文件中读取数据
            TextTableLine line;
            while (!this.terminate && (line = in.readLine()) != null) {
                out.write(line);
            }
            out.commit();

            // 并行同时执行多个任务
            msgfile.setColumn(file.getColumn()); // 设置文件字段个数
            msgfile.setReadRows(in.getLineNumber());
            msgfile.setCommitRows(out.getCommitRecords());
            msgfile.setDeleteRows(out.getDeleteRecords());
            msgfile.setSkipRows(out.getSkipRecords());
            msgfile.setErrorRows(0);
            msgfile.setEndTime(new Date());
            msgfile.store();

            // 打印消息文件内容
            if (log.isDebugEnabled()) {
                log.debug("load.stdout.message020", context.getName(), msgfile);
            }

            // 打印结果
            if (log.isInfoEnabled()) {
                log.info("load.stdout.message018", file.getAbsolutePath(), String.valueOf(msgfile.getReadRows()), String.valueOf(msgfile.getCommitRows()), watch.useTime());
            }
        } finally {
            in.close();
        }
    }
}
