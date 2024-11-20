package cn.org.expect.maven.concurrent;

import cn.org.expect.concurrent.BaseJob;
import cn.org.expect.log.Log;
import cn.org.expect.log.LogFactory;
import cn.org.expect.maven.repository.MavenRepository;
import cn.org.expect.maven.search.MavenSearch;
import cn.org.expect.maven.search.MavenSearchAware;
import cn.org.expect.util.Ensure;

public abstract class MavenSearchJob extends BaseJob implements Runnable, MavenSearchAware {
    protected final static Log log = LogFactory.getLog(MavenSearchJob.class);

    protected volatile MavenSearchExecutorService service;

    private MavenSearch search;

    private volatile MavenRepository remoteRepository;

    public MavenSearchJob() {
        this.terminate = false;
        this.setName(this.getClass().getSimpleName());
    }

    public void setService(MavenSearchExecutorService service) {
        this.service = Ensure.notNull(service);
    }

    public void setSearch(MavenSearch search) {
        this.search = search;
    }

    public MavenSearch getSearch() {
        return search;
    }

    protected MavenRepository getRemoteRepository() {
        if (this.remoteRepository == null) {
            synchronized (this) {
                if (this.remoteRepository == null) {
                    this.remoteRepository = search.getRepository();
                }
            }
        }
        return this.remoteRepository;
    }

    public void terminate() {
        super.terminate();
        if (this.remoteRepository != null) {
            this.remoteRepository.terminate();
        }
    }

    public final void run() {
        try {
            if (this.terminate) {
                return;
            }

            this.execute();
        } catch (Throwable e) {
            log.error(e.getLocalizedMessage(), e);
        } finally {
            if (this.service != null) {
                this.service.removeJob(this);
            }
        }
    }
}
