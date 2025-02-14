package cn.org.expect.database.parallel;

import java.util.Iterator;
import java.util.List;

import cn.org.expect.concurrent.EasyJob;
import cn.org.expect.concurrent.EasyJobReader;
import cn.org.expect.database.load.LoadFileRange;
import cn.org.expect.database.load.inernal.DataWriterFactory;
import cn.org.expect.database.load.serial.LoadFileExecutorContext;
import cn.org.expect.io.TextTableFile;
import cn.org.expect.util.Terminator;

public class LoadFileExecutorReader extends Terminator implements EasyJobReader {

    /** 表格型文件 */
    private TextTableFile file;

    /** 输入流缓冲区 */
    private int readBuffer;

    /** 数据库输出流 */
    private DataWriterFactory factory;

    /** 读取记录总数 */
    private ResultSet resultSet;

    /** 文件范围集合 */
    private List<LoadFileRange> ranges;

    /** 文件范围遍历器 */
    private Iterator<LoadFileRange> it;

    /**
     * 初始化
     *
     * @param factory    数据库输出流工厂
     * @param file       文件信息
     * @param readBuffer 文件输入流的缓冲区长度，单位字符
     * @param resultSet  结果集
     * @param ranges     文件范围
     * @throws Exception
     */
    public LoadFileExecutorReader(DataWriterFactory factory, TextTableFile file, int readBuffer, ResultSet resultSet, List<LoadFileRange> ranges) throws Exception {
        if (factory == null) {
            throw new NullPointerException();
        }
        if (file == null) {
            throw new NullPointerException();
        }
        if (resultSet == null) {
            throw new NullPointerException();
        }
        if (ranges == null) {
            throw new NullPointerException();
        }

        this.factory = factory;
        this.file = file;
        this.resultSet = resultSet;
        this.ranges = ranges;
        this.readBuffer = readBuffer;
        this.terminate = false;
        this.it = this.ranges.iterator();
    }

    public boolean hasNext() {
        return this.it.hasNext();
    }

    public EasyJob next() {
        if (this.hasNext()) {
            LoadFileExecutorContext context = new LoadFileExecutorContext();
            context.setFile(this.file);
            context.setReadBuffer(this.readBuffer);
            context.setRange(this.it.next());
            return new LoadFileExecutor(context, this.factory, this.resultSet);
        } else {
            return null;
        }
    }

    public void close() {
        this.terminate = false;
    }
}
