package cn.org.expect.increment;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.concurrent.AbstractJob;
import cn.org.expect.increment.sort.OrderByExpression;
import cn.org.expect.increment.sort.TableFileSortContext;
import cn.org.expect.increment.sort.TableFileSorter;
import cn.org.expect.io.CommonTextTableFileReaderListener;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.io.TextTableFileReader;
import cn.org.expect.io.TextTableFileWriter;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.printer.Progress;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.FileUtils;

/**
 * 剥离增量任务
 *
 * @author jeremy8551@gmail.com
 * @createtime 2010-01-19 02:45:22
 */
public class IncrementJob extends AbstractJob {
    private final static Log log = LogFactory.getLog(IncrementJob.class);

    /** 任务的配置信息 */
    private final IncrementContext context;

    /**
     * 初始化
     *
     * @param context 上下文信息
     */
    public IncrementJob(IncrementContext context) {
        super();
        this.context = Ensure.notNull(context);
        new IncrementContextValidator(context);
        this.setName(context.getName());
    }

    public int execute() throws Exception {
        TextTableFile oldfile = this.context.getOldFile();
        TextTableFile newfile = this.context.getNewFile();
        IncrementArith arith = this.context.getArith();
        TextTableFileWriter newOuter = this.context.getNewWriter();
        TextTableFileWriter updOuter = this.context.getUpdWriter();
        TextTableFileWriter delOuter = this.context.getDelWriter();
        boolean sortNewFile = this.context.isSortNewfile();
        boolean sortOldFile = this.context.isSortOldfile();
        IncrementPosition position = this.context.getPosition();
        List<IncrementListener> listeners = this.context.getListeners();
        IncrementReplaceListener replaceList = this.context.getReplaceList();
        IncrementListenerImpl logger = this.context.getLogger();
        Progress newfileProgress = this.context.getNewfileProgress();
        Progress oldfileProgress = this.context.getOldfileProgress();
        TableFileSortContext newfileCxt = this.context.getNewfileSortContext();
        TableFileSortContext oldfileCxt = this.context.getOldfileSortContext();
        Comparator<String> comparator = this.context.getComparator();

        // 设置字符串排序的规则
        IncrementComparator ruler = new IncrementComparatorImpl(comparator, position);

        // 保留排序前的文件路径
        File beforeSortNewfile = newfile.getFile();
        File beforeSortOldfile = oldfile.getFile();

        // 保留排序后的文件路径
        File afterSortNewfile = newfile.getFile();
        File afterSortOldfile = oldfile.getFile();

        try {
            if (sortNewFile) { // 排序新数据
                TableFileSorter tfs = new TableFileSorter(newfileCxt);
                try {
                    this.status.add(tfs);
                    OrderByExpression[] orders = this.valueOf(position.getNewIndexPosition(), comparator);
                    afterSortNewfile = tfs.execute(newfile, orders);
                } finally {
                    this.status.remove(tfs);
                }
            }
        } finally {
            if (sortOldFile) { // 排序旧数据
                TableFileSorter tfs = new TableFileSorter(oldfileCxt);
                try {
                    this.status.add(tfs);
                    OrderByExpression[] orders = this.valueOf(position.getOldIndexPosition(), comparator);
                    afterSortOldfile = tfs.execute(oldfile, orders);
                } finally {
                    this.status.remove(tfs);
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("increment.stdout.message031", this.getName());
        }

        // 开始执行增量剥离
        try {
            this.status.add(arith);

            // 使用排序后的文件作为剥离增量依据
            oldfile.setAbsolutePath(afterSortOldfile.getAbsolutePath());
            newfile.setAbsolutePath(afterSortNewfile.getAbsolutePath());

            TextTableFileReader oldIn = oldfile.getReader(oldfileCxt.getReaderBuffer());
            oldIn.setListener(new CommonTextTableFileReaderListener(oldfileProgress));

            TextTableFileReader newIn = newfile.getReader(newfileCxt.getReaderBuffer());
            newIn.setListener(new CommonTextTableFileReaderListener(newfileProgress));

            IncrementHandler out = new IncrementHandlerImpl(newfile, oldfile, listeners, logger, replaceList, newOuter, updOuter, delOuter);
            try {
                arith.execute(ruler, newIn, oldIn, out);
                return 0;
            } catch (Throwable e) {
                throw new IncrementException("increment.stdout.message026", this.getName(), newIn.getLineNumber(), oldIn.getLineNumber(), e);
            }
        } finally {
            this.status.remove(arith);

            // 排序后的文件与源文件路径不同，删除排序后的文件
            if (!afterSortOldfile.equals(beforeSortOldfile) && beforeSortOldfile.exists()) {
                FileUtils.deleteFile(afterSortOldfile);
            }
            if (!afterSortNewfile.equals(beforeSortNewfile) && beforeSortNewfile.exists()) {
                FileUtils.deleteFile(afterSortNewfile);
            }
        }
    }

    private OrderByExpression[] valueOf(int[] positions, Comparator<String> comparator) {
        OrderByExpression[] array = new OrderByExpression[positions.length];
        for (int i = 0; i < positions.length; i++) {
            array[i] = new OrderByExpression(positions[i], comparator, true);
        }
        return array;
    }

    /**
     * 返回剥离增量任务的上下文信息
     *
     * @return 上下文信息
     */
    public IncrementContext getContext() {
        return context;
    }
}
