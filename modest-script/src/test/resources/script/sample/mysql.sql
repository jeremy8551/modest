# 设置变量值
set properties=this.getContext().getContainer().getClassLoader().loadProperties("mysql.properties", "active.test.env")
set databaseDriverName=properties.getProperty("databaseDriverName")
set databaseUrl=properties.getProperty("databaseUrl")
set username=properties.getProperty("username")
set password=properties.getProperty("password")

# 打印所有内置变量
set

# 建立数据库连接信息
declare DBID catalog configuration use driver $databaseDriverName url "${databaseUrl}" username ${username} password $password

# 连接数据库
db connect to DBID

# quiet命令会忽略DROP语句的错误
quiet drop table V_TEST_TAB;

# 建表
CREATE TABLE V_TEST_TAB (
    ORGCODE CHAR(20),
    task_name CHAR(60) NOT NULL,
    task_file_path VARCHAR(512),
    file_data DATE NOT NULL,
    CREATE_DATE TIMESTAMP,
    FINISH_DATE TIMESTAMP,
    status CHAR(1),
    step_id VARCHAR(4000),
    error_time TIMESTAMP,
    error_log TEXT,
    oper_id CHAR(20),
    oper_name VARCHAR(60),
    PRIMARY KEY (task_name,file_data)
);
commit;

INSERT INTO V_TEST_TAB
(ORGCODE, TASK_NAME, TASK_FILE_PATH, FILE_DATA, CREATE_DATE, FINISH_DATE, STATUS, STEP_ID, ERROR_TIME, ERROR_LOG, OPER_ID, OPER_NAME)
VALUES('0', '1', '/was/sql', '2021-02-03', '2021-08-09 23:54:26.928000', NULL, '1', '使用sftp登录测试系统服务器', '2021-08-09 23:47:02.197000', '设置脚本引擎异常处理逻辑', '', '');

INSERT INTO V_TEST_TAB
(ORGCODE, TASK_NAME, TASK_FILE_PATH, FILE_DATA, CREATE_DATE, FINISH_DATE, STATUS, STEP_ID, ERROR_TIME, ERROR_LOG, OPER_ID, OPER_NAME)
VALUES('1', '2', '/was/test', '2021-02-03', '2021-08-09 23:54:26.928000', NULL, '1', '使用sftp登录测试系统服务器', '2021-08-09 23:47:02.197000', '使用sftp登录测试系统服务器', '', '');
commit;

# 创建索引
quiet drop index vtesttabidx01;
create index vtesttabidx01 on V_TEST_TAB(ORGCODE,error_time);
commit;

# 打印当前数据库的字段信息
db get cfg for catalog;
db get cfg for schema;
db get cfg for table type;
db get cfg for field type;

# 将表中数据卸载到文件中
db export to ${TMPDIR}/v_test_tab.del of del select * from V_TEST_TAB;

cat ${TMPDIR}/v_test_tab.del

# 将数据文件装载到指定数据库表中
db load from ${TMPDIR}/v_test_tab.del of del replace into V_TEST_TAB;

# 返回0表示脚本执行成功
exit 0