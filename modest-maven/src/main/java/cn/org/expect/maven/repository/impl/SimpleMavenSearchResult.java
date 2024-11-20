package cn.org.expect.maven.repository.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cn.org.expect.maven.repository.MavenArtifact;
import cn.org.expect.maven.repository.MavenSearchResult;
import cn.org.expect.util.ArrayUtils;
import cn.org.expect.util.Ensure;
import cn.org.expect.util.StringUtils;

public class SimpleMavenSearchResult implements MavenSearchResult {

    private final List<MavenArtifact> list;

    /** 下次开始读取记录的位置，从1开始 */
    private final int start;

    /** 总记录数 */
    private final int foundNumber;

    public SimpleMavenSearchResult() {
        this(new ArrayList<>(0), 0, 0);
    }

    public SimpleMavenSearchResult(List<MavenArtifact> list, int start, int foundNumber) {
        this.list = Ensure.notNull(list);
        this.start = start;
        this.foundNumber = foundNumber;
    }

    public List<MavenArtifact> getList() {
        return this.list;
    }

    public int getStart() {
        return start;
    }

    public int getFoundNumber() {
        return foundNumber;
    }

    public int size() {
        return this.list.size();
    }

    public boolean contains(String groupId, String artifactId, String version) {
        for (MavenArtifact artifact : this.list) {
            if (artifact.getGroupId().equals(groupId) && artifact.getArtifactId().equals(artifactId) && artifact.getVersion().equals(version)) {
                return true;
            }
        }
        return false;
    }

    public boolean addArtifact(MavenArtifact artifact) {
        for (int i = 0; i < this.list.size(); i++) {
            MavenArtifact mavenArtifact = this.list.get(i);
            if (mavenArtifact.getGroupId().equals(artifact.getGroupId()) //
                    && mavenArtifact.getArtifactId().equals(artifact.getArtifactId()) //
                    && mavenArtifact.getVersion().equals(artifact.getVersion()) //
            ) {
                if (artifact.getType().equalsIgnoreCase("jar")) {
                    this.list.set(i, artifact);
                    return true;
                }

                if (mavenArtifact.getType().equalsIgnoreCase("jar")) {
                    return false;
                }

                if (!artifact.getType().equalsIgnoreCase("pom")) {
                    this.list.set(i, artifact);
                    return true;
                }

                return false;
            }
        }

        this.list.add(artifact);
        this.list.sort(COMPARATOR.reversed());
        return true;
    }

    public final List<String> DELIMITERS = ArrayUtils.asList(".", "-");

    public final Comparator<MavenArtifact> COMPARATOR = (a1, a2) -> {
        String v1 = a1.getVersion();
        String v2 = a2.getVersion();
        String[] array1 = StringUtils.split(v1, DELIMITERS, false);
        String[] array2 = StringUtils.split(v2, DELIMITERS, false);
        int size = Math.min(array1.length, array2.length);
        for (int i = 0; i < size; i++) {
            String element1 = array1[i];
            String element2 = array2[i];

            if (StringUtils.isNumber(element1) && StringUtils.isNumber(element2)) {
                int v = Integer.parseInt(element1) - Integer.parseInt(element2);
                if (v != 0) {
                    return v;
                }
            } else {
                int v = element1.compareTo(element2);
                if (v != 0) {
                    return v;
                }
            }
        }
        return array1.length - array2.length;
    };
}
