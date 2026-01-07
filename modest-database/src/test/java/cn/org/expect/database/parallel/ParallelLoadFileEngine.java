package cn.org.expect.database.parallel;

import java.util.Date;
import java.util.List;

import cn.org.expect.concurrent.EasyJobService;
import cn.org.expect.concurrent.ThreadSource;
import cn.org.expect.concurrent.internal.DefaultJobReader;
import cn.org.expect.database.JdbcDao;
import cn.org.expect.database.load.DestTable;
import cn.org.expect.database.load.IndexOperation;
import cn.org.expect.database.load.LoadEngineContext;
import cn.org.expect.database.load.LoadFileMessage;
import cn.org.expect.database.load.Loader;
import cn.org.expect.database.load.inernal.DataWriterFactory;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.ioc.EasyContextAware;
import cn.org.expect.ioc.annotation.EasyBean;
import cn.org.expect.util.StringUtils;
import cn.org.expect.util.Terminator;

@EasyBean(value = "replace")
public class ParallelLoadFileEngine extends Terminator implements Loader, EasyContextAware {

    /** 上下文信息 */
    private LoadEngineContext context;

    /** 容器上下文信息 */
    protected EasyContext ioc;

    /**
     * 初始化
     */
    public ParallelLoadFileEngine() {
        super();
    }

    public void setContext(EasyContext context) {
        this.ioc = context;
    }

    public void execute(LoadEngineContext context) throws Exception {
        if (context == null) {
            throw new NullPointerException();
        } else {
            this.context = context;
        }

        JdbcDao dao = new JdbcDao(this.ioc);
        try {
            dao.connect(this.context.getDataSource());
            DestTable target = new DestTable(dao, null);

            // 前置操作
            IndexOperation listener = new IndexOperation(target.getTable());
            listener.before(context, dao);

            // 将大数据文件分成四十个任务，并行将数据插入到数据库表中
            DataWriterFactory factory = new DataWriterFactory(dao, context, target);
            try {
                List<String> sources = context.getFiles();
                for (String filepath : sources) {
                    TextTableFile file = this.ioc.getBean(TextTableFile.class, context.getFiletype(), context); // CommandAttribute.tofile(context, filepath, context.getFiletype());
                    file.setAbsolutePath(filepath);
                    this.execute(dao, factory, file);
                }
            } finally {
                factory.close();
            }

            // 后置操作
            listener.after(context, dao);
        } finally {
            dao.close();
        }
    }

    /**
     * 执行数据文件装载任务
     *
     * @param dao     数据库操作接口
     * @param factory 数据库批量插入工厂
     * @param txtfile 文本文件
     * @return 数据装载结果集
     * @throws Exception
     */
    public StandardResultSet execute(JdbcDao dao, DataWriterFactory factory, TextTableFile txtfile) throws Exception {
        // 消息文件相关
        LoadFileMessage msg = new LoadFileMessage(this.context, txtfile);
        msg.setFile(txtfile);
        msg.setFileModified(txtfile.getFile().lastModified());
        msg.setCharsetName(txtfile.getCharsetName());
        msg.setColumn(txtfile.getColumn());
        msg.setTableName(this.context.getTableName());
        msg.setTableSchema(this.context.getTableSchema());
        msg.setTableCatalog(this.context.getTableCatalog());

        try {
            StandardResultSet result = new StandardResultSet();
            int thread = StringUtils.parseInt(this.context.getAttributes().getAttribute("thread"), 2); // 并发任务数
            int readBuffer = this.context.getReadBuffer(); // 读取输入流缓存大小

            // 创建并行任务输入与输出设备
            LoadFileExecutorReader in = new LoadFileExecutorReader(factory, txtfile, readBuffer, result, msg.getFileRangeList());

            // 将任务添加到容齐中并行执行装数任务
            EasyJobService container = this.ioc.getBean(ThreadSource.class).getJobService(thread);
            container.execute(new DefaultJobReader(in));

            // 并行同时执行多个任务
            msg.setEndTime(new Date());
            msg.setReadRows(result.getReadCount());
            msg.setCommitRows(result.getCommitCount());
            msg.setDeleteRows(result.getDeleteCount());
            msg.setErrorRows(result.getErrorCount());
            msg.setSkipRows(result.getSkipCount());

//				for (LoaderListener listener : this.listeners) {
//					listener.catchExecption(dao, txtfile, out);
//				}

            msg.setEndTime(new Date());
            msg.setFileRange(null);
            msg.setReadRows(result.getReadCount());
            msg.setCommitRows(result.getCommitCount());
            msg.setDeleteRows(result.getDeleteCount());
            msg.setErrorRows(result.getErrorCount());
            msg.setSkipRows(result.getSkipCount());
            return result;
        } finally {
            msg.store();
        }
    }
}
