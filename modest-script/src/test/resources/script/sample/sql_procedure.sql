# 设置变量值
set properties=this.getContext().getContainer().getClassLoader().loadProperties("db2.properties", "active.test.env")
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
db get cfg for catalog;
db get cfg for schema;
db get cfg for table type;
db get cfg for field type;

# 建立异常捕获逻辑
declare global continue handler for errorcode == -601 begin
  echo 执行命令 ${errorscript} 发生错误, 对象已存在不能重复建立 ${errorcode} ..
end

# 打印所有异常捕获逻辑
handler

# 创建数据库表
CREATE TABLE SMP_TEST (
    ORGCODE CHAR(20),
    task_name CHAR(60) NOT NULL,
    task_file_path VARCHAR(512),
    file_data DATE NOT NULL,
    CREATE_DATE TIMESTAMP,
    FINISH_DATE TIMESTAMP,
    status CHAR(1),
    step_id VARCHAR(4000),
    error_time TIMESTAMP,
    error_log CLOB,
    oper_id CHAR(20),
    oper_name VARCHAR(60),
    PRIMARY KEY (task_name,file_data)
);

COMMENT ON TABLE SMP_TEST IS '接口文件记导入录表';
COMMENT ON COLUMN SMP_TEST.ORGCODE IS '归属机构号';
COMMENT ON COLUMN SMP_TEST.task_name IS '任务名';
COMMENT ON COLUMN SMP_TEST.task_file_path IS '数据文件所在绝对路径';
COMMENT ON COLUMN SMP_TEST.file_data IS '归属数据日期';
COMMENT ON COLUMN SMP_TEST.CREATE_DATE IS '运行起始时间';
COMMENT ON COLUMN SMP_TEST.FINISH_DATE IS '运行终止时间';
COMMENT ON COLUMN SMP_TEST.status IS '加载状态';
COMMENT ON COLUMN SMP_TEST.step_id IS '报错步骤编号';
COMMENT ON COLUMN SMP_TEST.error_time IS '报错时间';
COMMENT ON COLUMN SMP_TEST.error_log IS '报错日志';
COMMENT ON COLUMN SMP_TEST.oper_id IS '操作员id';
COMMENT ON COLUMN SMP_TEST.oper_name IS '操作员名';

commit;
exit 0