package cn.org.expect.maven.plugin.copyDependency;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import cn.org.expect.maven.plugin.MavenUtils;
import cn.org.expect.util.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

/**
 * 项目帮助类
 */
public class CopyDependencyUtils {

    /**
     * 深拷贝一个依赖集合
     *
     * @param list 依赖集合
     * @return 深拷贝
     */
    public static List<Dependency> clone(List<Dependency> list) {
        List<Dependency> newlist = new ArrayList<Dependency>(list.size());
        for (Dependency dependency : list) {
            newlist.add(dependency.clone());
        }
        return newlist;
    }

    /**
     * 查找项目信息
     *
     * @param list    项目集合
     * @param modules 项目名
     * @return 项目集合
     */
    public static List<MavenProject> find(List<MavenProject> list, List<String> modules) {
        List<MavenProject> newlist = new ArrayList<MavenProject>(list.size());
        for (String module : modules) {
            newlist.add(MavenUtils.find(list, module));
        }
        return newlist;
    }

    /**
     * 在项目集合中搜索
     *
     * @param list       项目集合
     * @param dependency 依赖
     * @return 项目
     */
    public static MavenProject find(List<MavenProject> list, Dependency dependency) {
        for (MavenProject project : list) {
            if (project.getGroupId().equals(dependency.getGroupId()) //
                && project.getArtifactId().equals(dependency.getArtifactId()) //
            ) {
                return project;
            }
        }
        throw new IllegalArgumentException(dependency.getGroupId() + ":" + dependency.getArtifactId());
    }

    /**
     * 在项目集合中搜索
     *
     * @param list       项目集合
     * @param dependency 依赖
     * @return 项目
     */
    public static MavenProject search(List<MavenProject> list, Dependency dependency) {
        for (MavenProject project : list) {
            if (project.getGroupId().equals(dependency.getGroupId()) //
                && project.getArtifactId().equals(dependency.getArtifactId()) //
            ) {
                return project;
            }
        }
        return null;
    }

    /**
     * 将项目集合中的所有项目从依赖集合 {@code list} 中移除，并解析被移除项目的依赖信息，将这些依赖合并到参数 {@code list} 中
     *
     * @param list     依赖集合
     * @param projects 项目集合
     * @return 依赖集合
     */
    public static List<Dependency> deepReplace(List<Dependency> list, List<MavenProject> projects) {
        List<Dependency> newList = CopyDependencyUtils.clone(list);
        for (int i = 0; i < list.size(); i++) {
            Dependency dependency = list.get(i);
            MavenProject project = CopyDependencyUtils.search(projects, dependency);
            if (project != null) {
                CopyDependencyUtils.remove(newList, dependency); // 移除依赖
                newList.addAll(i, CopyDependencyUtils.clone(project.getModel().getDependencies()));
            }
        }

        if (CopyDependencyUtils.intersect(newList, projects) == 0) {
            return newList;
        } else {
            return deepReplace(newList, projects);
        }
    }

    /**
     * 计算依赖集合与项目集合的交集个数
     *
     * @param dependencies 依赖集合
     * @param projects     项目集合
     * @return 个数
     */
    public static int intersect(List<Dependency> dependencies, List<MavenProject> projects) {
        int count = 0;
        for (Dependency dependency : dependencies) {
            for (MavenProject project : projects) {
                if (dependency.getGroupId().equals(project.getGroupId()) && dependency.getArtifactId().equals(project.getArtifactId())) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    /**
     * 删除指定依赖
     *
     * @param list       依赖集合
     * @param dependency 需要删除的依赖
     */
    public static void remove(List<Dependency> list, Dependency dependency) {
        for (Iterator<Dependency> it = list.iterator(); it.hasNext(); ) {
            Dependency obj = it.next();
            if (obj.getGroupId().equals(dependency.getGroupId()) //
                && obj.getArtifactId().equals(dependency.getArtifactId()) //
                && obj.getVersion().equals(dependency.getVersion()) //
            ) {
                it.remove();
                continue;
            }
        }
    }

    /**
     * 删除范围是 test 与 runtime 的依赖，将 provided 依赖设置为不向下传递
     *
     * @param list 依赖集合
     */
    public static void dealScope(List<Dependency> list) {
        for (Iterator<Dependency> it = list.iterator(); it.hasNext(); ) {
            Dependency obj = it.next();
            if (StringUtils.inArrayIgnoreCase(obj.getScope(), "test", "runtime")) {
                it.remove();
                continue;
            }

            if (obj.getScope().equalsIgnoreCase("provided")) {
                obj.setOptional(true);
            }
        }
    }

    /**
     * 删除重复依赖
     *
     * @param list 依赖集合
     * @param c    依赖排序规则
     */
    public static void removeDuplicate(List<Dependency> list, Comparator<Dependency> c) {
        for (int i = 0; i < list.size(); i++) {
            Dependency obj = list.get(i);
            if (obj != null) {
                for (int j = i + 1; j < list.size(); j++) {
                    Dependency next = list.get(j);
                    if (next != null) {
                        int v = c.compare(obj, next);
                        if (v == 0) {
                            list.set(j, null);
                        }
                    }
                }
            }
        }

        for (Iterator<Dependency> it = list.iterator(); it.hasNext(); ) {
            Dependency obj = it.next();
            if (obj == null) {
                it.remove();
            }
        }
    }

    public static String toMavenProjectString(List<MavenProject> projects) {
        StringBuilder buf = new StringBuilder(100);
        for (MavenProject project : projects) {
            buf.append(project.getGroupId()).append(':').append(project.getArtifactId()).append(", ");
        }
        return buf.toString();
    }

    public static String toDependencyString(List<Dependency> dependencys) {
        StringBuilder buf = new StringBuilder(100);
        for (Dependency dependency : dependencys) {
            buf.append(dependency.getGroupId()).append(':').append(dependency.getArtifactId()).append(", ");
        }
        return buf.toString();
    }
}
