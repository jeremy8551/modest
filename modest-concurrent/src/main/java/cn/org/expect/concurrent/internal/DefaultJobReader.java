package cn.org.expect.concurrent.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.org.expect.concurrent.EasyJob;
import cn.org.expect.concurrent.EasyJobReader;
import cn.org.expect.util.Terminator;

/**
 * 接口实现类
 *
 * @author jeremy8551@gmail.com
 * @createtime 2023/12/2
 */
public class DefaultJobReader extends Terminator implements EasyJobReader {

    private Iterator<? extends EasyJob> it;

    public DefaultJobReader(List<? extends EasyJob> list) {
        this.terminate = false;
        this.it = list.iterator();
    }

    public DefaultJobReader(EasyJobReader in) throws Exception {
        this(toList(in));
    }

    private static List<? extends EasyJob> toList(EasyJobReader in) throws Exception {
        List<EasyJob> list = new ArrayList<EasyJob>();
        while (in.hasNext()) {
            EasyJob next = in.next();
            if (next != null) {
                list.add(next);
            }
        }
        return list;
    }

    public boolean hasNext() {
        return this.it.hasNext();
    }

    public EasyJob next() {
        return this.it.next();
    }

    public void close() {
        this.it = null;
    }
}
