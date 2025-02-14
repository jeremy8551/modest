package cn.org.expect.increment;

import java.io.IOException;

import cn.org.expect.io.TextTableFileReader;
import cn.org.expect.io.TextTableLine;
import cn.org.expect.util.Terminator;

/**
 * 接口实现类
 *
 * @author jeremy8551@gmail.com
 */
public class IncrementArithImpl extends Terminator implements IncrementArith {

    public void execute(IncrementComparator rule, TextTableFileReader newIn, TextTableFileReader oldIn, IncrementHandler out) throws IOException {
        try {
            // 读取第一行数据
            TextTableLine nl = newIn.readLine();
            TextTableLine ol = oldIn.readLine();

            // 比较文本 没有数据
            if (ol == null) {
                while (nl != null) {
                    if (this.terminate) {
                        return;
                    }

                    out.handleCreateRecord(nl);
                    nl = newIn.readLine();
                }
                return;
            }

            // 被比较文本 没有数据
            if (nl == null) {
                while (ol != null) {
                    if (this.terminate) {
                        return;
                    }

                    out.handleDeleteRecord(ol);
                    ol = oldIn.readLine();
                }
                return;
            }

            while (nl != null && ol != null) {
                if (this.terminate) {
                    return;
                }

                int v = rule.compareIndex(nl, ol);
                if (v == 0) {
                    int p = rule.compareColumn(nl, ol);
                    if (p != 0) {
                        out.handleUpdateRecord(nl, ol, p);
                    }
                    nl = newIn.readLine();
                    ol = oldIn.readLine();
                } else if (v < 0) {
                    out.handleCreateRecord(nl);
                    nl = newIn.readLine();
                } else {
                    out.handleDeleteRecord(ol);
                    ol = oldIn.readLine();
                }
            }

            while (nl != null) {
                if (this.terminate) {
                    return;
                }

                out.handleCreateRecord(nl);
                nl = newIn.readLine();
            }

            while (ol != null) {
                if (this.terminate) {
                    return;
                }

                out.handleDeleteRecord(ol);
                ol = oldIn.readLine();
            }
        } finally {
            oldIn.close();
            newIn.close();
            out.close();
        }
    }
}
