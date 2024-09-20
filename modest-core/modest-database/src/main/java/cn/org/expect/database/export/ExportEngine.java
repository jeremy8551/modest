package cn.org.expect.database.export;

import java.math.BigDecimal;

import cn.org.expect.concurrent.AbstractJob;
import cn.org.expect.expression.DataUnitExpression;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.ResourcesUtils;
import cn.org.expect.util.TimeWatch;

/**
 * 数据卸载引擎
 *
 * @author jeremy8551@qq.com
 * @createtime 2020-11-12
 */
public class ExportEngine extends AbstractJob {
    private final static Log log = LogFactory.getLog(ExportEngine.class);

    /** 上下文信息 */
    protected ExtracterContext context;

    /** 监听器 */
    protected ExtractListener listener;

    /** 消息信息 */
    protected ExtractMessage message;

    /** 容器上下文信息 */
    protected EasyContext ioc;

    /** 计时器 */
    private TimeWatch watch;

    public ExportEngine(EasyContext context) {
        super();
        this.ioc = Ensure.notNull(context);
        this.context = new ExtracterContext(this);
        this.listener = new ExtractListener(this.context);
        this.watch = new TimeWatch();
    }

    /**
     * 返回数据卸载引擎的上下文信息
     *
     * @return 上下文信息
     */
    public ExtracterContext getContext() {
        return this.context;
    }

    public int execute() throws Exception {
        this.watch.start();
        new ExtracterValidator().check(this.context);
        this.setName(ResourcesUtils.getMessage("extract.standard.output.msg001", " " + this.context.getTarget()));
        this.listener.setListener(this.context.getListener());
        this.message = new ExtractMessage(this.context.getMessagefile(), this.context.getFormat().getCharsetName());
        this.listener.before();
        try {
            this.message.start();
            this.message.store();
            this.execute(this.context);
            this.message.finish();
            this.listener.after();
        } catch (Throwable e) {
            this.message.terminate();
            this.listener.catchError(e);
        } finally {
            this.message.store();
        }

        if (log.isInfoEnabled()) {
            if (this.message.getMessagefile() == null) {
                log.info(ResourcesUtils.getMessage("extract.standard.output.msg008", this.message.getTarget(), this.message.getRows(), DataUnitExpression.toString(new BigDecimal(this.message.getBytes())), this.watch.useTime()));
            } else {
                log.info(ResourcesUtils.getMessage("extract.standard.output.msg002", this.message.getTarget(), this.message.getRows(), DataUnitExpression.toString(new BigDecimal(this.message.getBytes())), this.message.getMessagefile(), this.watch.useTime()));
            }
        }

        this.listener.destory();
        return 0;
    }

    /**
     * 执行数据卸载
     *
     * @param context 卸数引擎上下文
     * @throws Exception 数据卸载发生错误
     */
    protected void execute(ExtracterContext context) throws Exception {
        this.message.setEncoding(context.getFormat().getCharsetName());
        this.message.setLineSeparator(context.getFormat().getLineSeparator());
        this.message.setDelimiter(context.getFormat().getDelimiter());
        this.message.setCharDelimiter(context.getFormat().getCharDelimiter());
        this.message.setSource(context.getSource());
        this.message.setColumn(context.getFormat().getColumn());
        this.message.setRows(0); // 在输出流中设置
        this.message.setBytes(0); // 在输出流中设置
        this.message.setTarget(""); // 在输出流中设置

        ExtractReader in = this.ioc.getBean(ExtractReader.class, context);
        try {
            ExtractWriter out = this.ioc.getBean(ExtractWriter.class, context, this.message);
            try {
                while (in.hasLine()) {
                    if (this.status.isTerminate()) {
                        this.message.terminate();
                        break;
                    } else {
                        out.write(in);
                    }
                    out.rewrite();
                }
                out.flush();
            } finally {
                out.close();
            }

            this.message.setTime(this.watch.useTime());
            if (this.status.isTerminate()) {
                this.message.terminate();
            }
        } finally {
            in.close();
        }
    }

    /**
     * 返回监听器
     *
     * @return 监听器
     */
    public ExtractListener getListener() {
        return listener;
    }

}
