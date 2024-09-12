package cn.org.expect.maven;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import cn.org.expect.util.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * 项目帮助类
 */
public class MavenUtils {

    public static List<Dependency> copy(List<Dependency> list) {
        List<Dependency> newlist = new ArrayList<Dependency>(list.size());
        for (Dependency dependency : list) {
            newlist.add(dependency.clone());
        }
        return newlist;
    }

    /**
     * 断言：模块名（项目名）集合必须在 Maven 项目中存在
     *
     * @param allProjects 项目信息
     * @param names       项目名集合
     * @throws MojoExecutionException 项目名不在 Maven 项目中
     */
    public static void assertContains(List<MavenProject> allProjects, List<String> names) throws MojoExecutionException {
        for (String name : names) {
            if (!MavenUtils.contains(allProjects, name)) {
                String str = "";
                for (Iterator<MavenProject> it = allProjects.iterator(); it.hasNext(); ) {
                    str += it.next().getName();
                    if (it.hasNext()) {
                        str += ", ";
                    }
                }
                throw new MojoExecutionException("MavenProject " + name + " not in [" + str + "]");
            }
        }
    }

    /**
     * 判断模块名（项目名）是否合法
     *
     * @param list 项目集合
     * @param name 模块名（项目名）
     * @return 返回true表示模块名合法 false表示不合法
     */
    public static boolean contains(List<MavenProject> list, String name) {
        for (MavenProject project : list) {
            if (project.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查找模块
     *
     * @param list 模块集合
     * @param name 模块名
     * @return 模块信息
     */
    public static MavenProject find(List<MavenProject> list, String name) {
        for (MavenProject project : list) {
            if (project.getName().equals(name)) {
                return project;
            }
        }
        return null;
    }

    public static List<MavenProject> find3(List<MavenProject> list, List<String> names) {
        List<MavenProject> newlist = new ArrayList<MavenProject>(list.size());
        for (String name : names) {
            MavenProject project = MavenUtils.find(list, name);
            if (project != null) {
                newlist.add(project);
            }
        }
        return newlist;
    }

    public static MavenProject find(List<MavenProject> list, Dependency dependency) {
        for (MavenProject project : list) {
            if (project.getGroupId().equals(dependency.getGroupId()) && project.getArtifactId().equals(dependency.getArtifactId())) {
                return project;
            }
        }
        return null;
    }

    public static List<MavenProject> find(List<MavenProject> list, List<Dependency> dependencys) {
        List<MavenProject> projects = new ArrayList<MavenProject>(list.size());
        for (Dependency dependency : dependencys) {
            MavenProject project = MavenUtils.find(list, dependency);
            if (project != null) {
                projects.add(project);
            }
        }
        return projects;
    }

    public static List<MavenProject> find2(List<MavenProject> list, List<MavenProject> searchs) {
        List<MavenProject> projects = new ArrayList<MavenProject>(list.size());
        for (MavenProject project : searchs) {
            MavenProject mp = find1(list, project);
            if (mp != null) {
                projects.add(mp);
            }
        }
        return projects;
    }

    public static MavenProject find1(List<MavenProject> list, MavenProject project) {
        for (MavenProject mp : list) {
            if (mp.getName().equals(project.getName())) {
                return mp;
            }
        }
        return null;
    }

    public static int count(List<Dependency> list, List<MavenProject> searchs) {
        int count = 0;
        for (Dependency dependency : list) {
            for (MavenProject mp : searchs) {
                if (dependency.getGroupId().equals(mp.getGroupId()) && dependency.getArtifactId().equals(mp.getArtifactId())) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    public static String toString(List<MavenProject> projects) {
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

    public static void remove(List<Dependency> list, Dependency dependency) {
        for (Iterator<Dependency> it = list.iterator(); it.hasNext(); ) {
            Dependency obj = it.next();
            if (obj.getGroupId().equals(dependency.getGroupId()) && obj.getArtifactId().equals(dependency.getArtifactId()) && obj.getVersion().equals(dependency.getVersion())) {
                it.remove();
            }
        }
    }

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

    public static void removeDuplicate(List<Dependency> list, Comparator<Dependency> c) {
        for (int i = 0; i < list.size(); i++) {
            Dependency obj = list.get(i);
            if (obj == null) {
                continue;
            }

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

        for (Iterator<Dependency> it = list.iterator(); it.hasNext(); ) {
            Dependency obj = it.next();
            if (obj == null) {
                it.remove();
            }
        }
    }
}
