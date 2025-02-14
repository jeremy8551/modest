package cn.org.expect.database.db2;

import cn.org.expect.ioc.annotation.EasyBean;

@EasyBean(value = "db2")
public class DB2LinuxCommand implements DB2Command {

    public String getTableCommand(String databaseName, String schema, String tableName, String username, String password) {
        return "db2look -e -d " + databaseName + " -i " + username + " -w " + password + " -z " + schema + " -tw " + tableName;
    }

    public String getApplicationDetail(String applicationId) {
        return "db2 list applications show detail | grep \"" + applicationId + "\"";
    }
}
