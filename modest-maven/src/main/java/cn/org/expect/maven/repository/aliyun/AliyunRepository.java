package cn.org.expect.maven.repository.aliyun;

/**
 * 阿里云仓库
 */
//@EasyBean("aliyun")
//public class AliyunRepository implements MavenRepository {
//    protected final static Log log = LogFactory.getLog(AliyunRepository.class);
//
//    protected ExtraResultAnalysis pattern;
//
//    protected PatternResultAnalysis extra;
//
//    protected volatile Call call;
//
//    protected volatile boolean terminate;
//
//    protected SimpleMavenRepositoryDatabase database;
//
//    public AliyunRepository(EasyContext ioc) {
//        this.pattern = new ExtraResultAnalysis();
//        this.extra = new PatternResultAnalysis();
//        this.terminate = false;
//        this.database = new SimpleMavenRepositoryDatabase(ioc, this.getClass());
//    }
//
//    public MavenArtifactOperation getSupported() {
//        return new MavenArtifactOperation() {
//
//            public boolean supportOpenInCentralRepository() {
//                return true;
//            }
//
//            public boolean supportDownload() {
//                return true;
//            }
//
//            public boolean supportDelete() {
//                return true;
//            }
//
//            public boolean supportOpenInFileSystem() {
//                return true;
//            }
//        };
//    }
//
//    public MavenRepositoryDatabase getDatabase() {
//        return this.database;
//    }
//
//    public String getAddress() {
//        return "https://developer.aliyun.com/mvn/search";
//    }
//
//    public MavenSearchResult query(String pattern, int start) {
//        this.terminate = false;
//        String url = "https://developer.aliyun.com/artifact/aliyunMaven/searchArtifactByWords?repoId=all&queryTerm=" + StringUtils.trimBlank(StringUtils.replaceAll(pattern, ".", "%2E")) + "&_input_charset=utf-8";
//        String responseBody = this.sendRequest(url);
//
//        if (StringUtils.isBlank(responseBody) || this.terminate) {
//            return null;
//        }
//
//        MavenSearchResult result = this.pattern.parse(responseBody);
//        result.getList().sort(PATTERN_RESULT_COMPARATOR.reversed());
//        return result;
//    }
//
//    public MavenSearchResult query(String groupId, String artifactId) {
//        this.terminate = false;
//        String url = "https://search.maven.org/solrsearch/select?q=g:" + groupId + "+AND+a:" + artifactId + "&core=gav&rows=200&wt=json"; // 构建请求 URL
//        String responseBody = this.sendRequest(url);
//        if (this.terminate) {
//            return null;
//        }
//
//        MavenSearchResult result = this.extra.parse(responseBody);
//        List<MavenArtifact> list = result.getList();
//
//        int start = result.size(); // 起始位置
//        if (result.getFoundNumber() > start) {
//            do {
//                if (this.terminate) {
//                    break;
//                }
//
//                responseBody = this.sendRequest(url + "&start=" + start);
//
//                if (this.terminate) {
//                    break;
//                }
//
//                MavenSearchResult next = this.extra.parse(responseBody);
//                list.addAll(next.getList());
//                start = next.getStart();
//            } while (result.getFoundNumber() > start);
//            list.sort(EXTRA_RESULT_COMPARATOR);
//            return new SimpleMavenSearchResult(list, start, result.getFoundNumber(), System.currentTimeMillis());
//        } else {
//            list.sort(EXTRA_RESULT_COMPARATOR);
//            return result;
//        }
//    }
//
//    public String sendRequest(String url) {
//        Throwable throwable = null;
//        int times = 3;
//        for (int i = 0; i < times; i++) {
//            if (this.terminate) {
//                break;
//            }
//
//            try {
//                return this.sendURL(url);
//            } catch (Throwable e) {
//                if (throwable == null) {
//                    throwable = e;
//                }
//            }
//        }
//
//        if (this.terminate) {
//            return null;
//        } else {
//            throw new RuntimeException("try " + times + " times send request, but fail!", throwable);
//        }
//    }
//
//    public synchronized String sendURL(String url) throws IOException {
//        if (log.isDebugEnabled()) {
//            log.debug("send URL: {}", url);
//        }
//
//        try {
//            OkHttpClient client = new OkHttpClient(); // 创建 OkHttpClient 实例
//            Request request = new Request.Builder().url(url).header("User-Agent", "Mozilla/5.0").build(); // 创建 Request 实例
//            Call call = client.newCall(request);
//            this.call = call;
//            Response response = call.execute(); // 发送请求并获取响应
//            return response.body().string(); // 读取响应体
//        } finally {
//            this.call = null;
//        }
//    }
//
//    public boolean isTerminate() {
//        return this.terminate;
//    }
//
//    public void terminate() {
//        if (log.isDebugEnabled()) {
//            log.debug("{} terminated!", this.getClass().getSimpleName());
//        }
//
//        this.terminate = true;
//        if (this.call != null) {
//            this.call.cancel();
//        }
//    }
//
//    /** 模糊查询结果的排序规则：按时间戳倒序 */
//    protected final static Comparator<MavenArtifact> PATTERN_RESULT_COMPARATOR = (o1, o2) -> {
//        int vv = o1.getVersionCount() - o2.getVersionCount(); // 版本数
//        if (vv != 0) {
//            return vv;
//        }
//
//        int tv = o1.getTimestamp().compareTo(o2.getTimestamp()); // 最新发布
//        if (tv != 0) {
//            return tv;
//        }
//
//        int gv = o1.getGroupId().compareTo(o2.getGroupId());
//        if (gv != 0) {
//            return gv;
//        }
//
//        return o1.getArtifactId().compareTo(o2.getArtifactId());
//    };
//
//    /** 精确查询结果的排序规则：按版本数、最新发布时间等排序 */
//    protected final static Comparator<MavenArtifact> EXTRA_RESULT_COMPARATOR = Comparator.comparing(MavenArtifact::getTimestamp).reversed();
//}
