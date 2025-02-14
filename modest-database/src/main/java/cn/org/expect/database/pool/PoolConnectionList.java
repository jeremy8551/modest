package cn.org.expect.database.pool;

import java.sql.Connection;
import java.util.Stack;

import cn.org.expect.database.Jdbc;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.util.IO;

public class PoolConnectionList extends Stack<PoolConnection> {
    private final static Log log = LogFactory.getLog(PoolConnectionList.class);

    private final static long serialVersionUID = 1L;

    public PoolConnectionList() {
        super();
    }

    /**
     * 清空所有连接并关闭所有连接
     */
    public synchronized void close() {
        Stack<PoolConnection> list = this;
        for (int i = 0; i < list.size(); i++) {
            PoolConnection proxy = list.get(i);
            if (proxy != null) {
                Connection conn = proxy.getConnection();
                if (Jdbc.canUseQuietly(conn)) {
                    if (log.isDebugEnabled()) {
                        log.debug("dataSource.standard.output.msg009", proxy);
                    }

                    try {
                        Jdbc.commit(conn);
                    } catch (Throwable e) {
                        Jdbc.rollbackQuiet(conn);
                    } finally {
                        IO.closeQuiet(conn, conn);
                    }
                }
            }
        }
        list.clear();
    }
}
