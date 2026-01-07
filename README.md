# 功能介绍

通用脚本引擎（以下简称脚本引擎）基于 **Java** 语言实现，具备以下功能：

- 支持执行 SQL 语句，操作关系型数据库；
- 支持将数据文件解析并装载至数据库表；
- 支持从数据库表导出数据至文件；
- 支持文件剥离增量处理机制，并可按需实现自定义文件格式解析器；
- 支持远程主机连接，在远程服务器上执行 Shell 命令；
- 支持多线程并发执行任务；
- 支持自定义扩展脚本引擎的命令；
- 支持自定义扩展脚本引擎的变量方法；
- 兼容 **JDK5** 及以上版本；
- 提供对 **JSR 223: Scripting for the Java Platform** 标准接口的实现，支持通过标准化方式集成脚本引擎；




# 使用方法



## 引入依赖

**JDK5** 环境添加依赖：

```xml
<dependency>
  <groupId>cn.org.expect</groupId>
  <artifactId>modest-script</artifactId>
  <version>1.0.4</version>
</dependency>
```

**JDK6** 及更高版的 JDK 添加依赖：

```xml
<dependency>
  <groupId>cn.org.expect</groupId>
  <artifactId>modest-script-engine</artifactId>
  <version>1.0.4</version>
</dependency>
```

**Spring Boot 项目**添加依赖：

```xml
<dependency>
    <groupId>cn.org.expect</groupId>
    <artifactId>modest-spring-boot-starter</artifactId>
    <version>1.0.4</version>
</dependency>
```



## Hello World

**JDK6**（**JSR-223**）脚本引擎标准**API**：


```java
package cn.org.expect.javax.script.sample;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptSample {

    public static void main(String[] args) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("usl");
        engine.eval("echo hello world!");
    }
}

```

脚本引擎实例化与执行：


```java
package cn.org.expect.script.sample;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;

public class ScriptSample {

    public static void main(String[] args) {
        EasyContext context = new DefaultEasyContext();
        UniversalScriptEngineFactory factory = new UniversalScriptEngineFactory(context);
        UniversalScriptEngine engine = factory.getScriptEngine();
        engine.evaluate("echo hello world!");
    }
}

```

在 **Spring Boot** 项目注入脚本引擎实例并执行脚本：

```java
package cn.org.expect.ssm.controller;

import java.io.IOException;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import cn.org.expect.ioc.EasyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloControllerSample {

    @Autowired
    private ScriptEngine engine;

    @Autowired
    private EasyContext context;

    @RequestMapping("/helloWorld")
    @ResponseBody
    public String help() throws ScriptException, IOException {
        this.engine.eval("echo hello world!");
        return "success";
    }
}

```



## ETL脚本示例

```sql
package cn.org.expect.script.sample;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithFeature;
import cn.org.expect.test.annotation.RunWithLogSettings;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@RunWithLogSettings("sout:info")
@RunWithFeature("mysql")
public class MysqlTest {

    @Test
    public void test() {
        EasyContext context = new DefaultEasyContext();
        UniversalScriptEngineFactory factory = new UniversalScriptEngineFactory(context);
        UniversalScriptEngine engine = factory.getScriptEngine();
        engine.evaluate(". classpath:/script/sample/mysql.sql");
    }
}

```

在 `resources` 目录下新建脚本文件：

```JAVA
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
```



## 存储过程示例

可以作为存储过程使用

```sql
package cn.org.expect.script.sample;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithFeature;
import cn.org.expect.test.annotation.RunWithLogSettings;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@RunWithLogSettings("sout:info")
@RunWithFeature("db2")
public class SQLProcedureTest {

    @Test
    public void test() {
        EasyContext context = new DefaultEasyContext();
        UniversalScriptEngineFactory factory = new UniversalScriptEngineFactory(context);
        UniversalScriptEngine engine = factory.getScriptEngine();
        engine.evaluate(". classpath:/script/sample/sql_procedure.sql");
    }
}

```

在 `resources` 目录下新建脚本文件：

```java
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
```



## 脚本示例

```sql
package cn.org.expect.script.sample;

import cn.org.expect.ioc.DefaultEasyContext;
import cn.org.expect.ioc.EasyContext;
import cn.org.expect.script.UniversalScriptEngine;
import cn.org.expect.script.UniversalScriptEngineFactory;
import cn.org.expect.test.ModestRunner;
import cn.org.expect.test.annotation.RunWithFeature;
import cn.org.expect.test.annotation.RunWithLogSettings;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ModestRunner.class)
@RunWithLogSettings("sout:info")
@RunWithFeature("db2")
public class DB2Test {

    @Test
    public void test() {
        EasyContext context = new DefaultEasyContext();
        UniversalScriptEngineFactory factory = new UniversalScriptEngineFactory(context);
        UniversalScriptEngine engine = factory.getScriptEngine();
        engine.evaluate(". classpath:/script/sample/db2.sql");
    }
}

```

在 `resources` 目录下新建脚本文件：

```
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

# 重新建表
quiet drop table v_test_tmp;
create table v_test_tmp (
	branch_id char(6) not null, -- 机构编号
	branch_name varchar(150), -- 机构名称
	branch_type char(18), -- 机构类型
	branch_no char(12), -- 机构序号
	status char(1), -- 状态
	primary key(branch_id)
);
commit;

# 设置总记录数
set v_test_tmp_records=10123

# 批量插入
DECLARE s1 Statement WITH insert into v_test_tmp (branch_id, branch_name, branch_type, branch_no, status) values (?, ?, ?, ?, ?) ;

# 建立进度输出
declare progress use out print 'insert records ${process}%, total ${totalRecord} records ${leftTime}' total ${v_test_tmp_records} times

# 逐条插入数据
set i=1
while $i <= $v_test_tmp_records loop
  set c1 = "$i"
  set c2 = "orgCode$i"
  set c3 = "orgType$i"
  set c4 = "ID$i"
  set c5 = "0"

  # 设置SQL参数
  FETCH c1, c2, c3, c4, c5 insert s1;

  # 进度输出
  progress

  # 自动加一
  set i = $i + 1
end loop

# 提交事物
commit;

# 关闭批量插入
undeclare s1 Statement

exit 0
```



## shell示例

```shell
# 设置变量值
set properties=this.getContext().getContainer().getClassLoader().loadProperties("ssh.properties", "active.test.env")
set sshHost=properties.getProperty("ssh.host")
set sshPort=properties.getProperty("ssh.port")
set sshUsername=properties.getProperty("ssh.username")
set sshPassword=properties.getProperty("ssh.password")

ssh ${sshUsername}@${sshHost}:${sshPort}?password=${sshPassword} && export LANG=zh_CN.UTF-8 && pwd && ls -ltrha
```



## sftp示例

```shell
# 设置变量值
set properties=this.getContext().getContainer().getClassLoader().loadProperties("sftp.properties", "active.test.env")
set sftphost=properties.getProperty("sftp.host")
set sftpport=properties.getProperty("sftp.port")
set sftpuser=properties.getProperty("sftp.username")
set sftppass=properties.getProperty("sftp.password")

cd ${TMPDIR}
echo "select * from table " > ${TMPDIR}/test.sql

sftp ${sftpuser}@${sftphost}:${sftpport}?password=${sftppass}
  set ftphome=`pwd`
  ls ${ftphome}
  set remotetestdir="test"
  rm ${remotetestdir}
  mkdir ${remotetestdir}
  cd ${remotetestdir}
  put `pwd -l`/test.sql
  ls
  pwd
  cd ..
  exists ${remotetestdir}/test.sql
  isfile ${remotetestdir}/test.sql
  mkdir ${ftphome}/test
  rm ${ftphome}/test
  get ${remotetestdir}/test.sql ${TMPDIR}
  exists -l ${TMPDIR}/test.sql
bye
```



## ftp示例

```shell
# 设置变量值
set properties=this.getContext().getContainer().getClassLoader().loadProperties("ftp.properties", "active.test.env")
set ftphost=properties.getProperty("ftp.host")
set ftpport=properties.getProperty("ftp.port")
set ftpuser=properties.getProperty("ftp.username")
set ftppass=properties.getProperty("ftp.password")

echo "select * from table " > ${TMPDIR}/test.sql

ftp ${ftpuser}@${ftphost}:${ftpport}?password=${ftppass}
  passive
  set ftphome=`pwd`
  set remotetestdir="rpt"
  pwd
  rm ${remotetestdir}
  mkdir ${remotetestdir}
  exists ${remotetestdir}
  ls ${remotetestdir}
  ls ${ftphome}
  put $TMPDIR/test.sql ${remotetestdir}
  ls ${remotetestdir}
  exists ${remotetestdir}/test.sql
  isfile ${remotetestdir}/test.sql
  mkdir test
  isDirectory test
  rm test
  get ${remotetestdir}/test.sql ${TMPDIR}
  exists -l ${TMPDIR}/test.sql
  cd ${remotetestdir}
bye
```



# 编译方法

在 MacOS 下执行命令：

```shell
git clone git@github.com:jeremy8551/modest.git && cd modest && ./build.sh
```

自动下载项目、JDK、Maven、生成 `toolchains-modest.xml` 文件、编译项目。

> 其他操作系统需要手动安装 JDK，并将 `.mvn/toolchains-modest.xml` 复制到用户根目录中的 `.m2` 目录下，并修改配置文件中的 JDK 的安装目录。



# 脚本命令



## 内置命令概览

脚本引擎内置了一套丰富的命令体系，覆盖了 **数据处理**、**逻辑控制**、**文件操作**、**数据库访问**、**网络通信**、**线程管理** 等关键功能场景。

命令风格与类 **Unix Shell** 类似，使开发者在使用过程中能够快速上手，编写高效、可维护的脚本任务流程。

所有命令可与变量、函数、组件等结合，实现复杂业务逻辑编排与自动化处理能力。

按功能类别划分如下：



### 基础命令类

- **echo**：输出字符串或变量内容。
- **pwd**：打印当前工作目录。
- **export**：设置环境变量。
- **exit**：退出脚本执行。
- **default**：设置变量默认值。
- **grep**：文本过滤与模式匹配。



### 声明类命令

所有 **declare** 开头的命令，用于变量、函数、组件等声明，例如：
- **declare var**：声明变量。
- **declare function**：声明函数。
- **declare bean**：声明组件。



### 逻辑控制类命令

- **if**：条件判断。
- **while**：条件循环。
- **for**：遍历循环。
- **break**：终止当前循环。
- **continue**：跳过本次循环，进入下一次。
- **step**：步进循环。
- **jump**：跳转执行流程。



### 数据库操作类命令

- **select**：执行数据库查询操作。
- **insert**：执行数据库插入操作。
- **delete**：执行数据库删除操作。
- **update**：执行数据库更新操作。
- **merge**：执行数据库合并操作。
- **db**：配置或连接数据库、卸载与装载数据文件、查看数据库元信息。



### 网络操作类命令

- **os**：远程操作系统命令执行。
- **ftp**：FTP 文件传输。
- **sftp**：SFTP 文件安全传输。
- **wget**：下载网络文件



### 文件操作类命令

- **cd**：切换目录。
- **touch**：创建文件。
- **rm**：删除文件或目录。
- **mkdir**：创建目录。
- **cat**：查看文件内容。
- **head**：查看文件头部内容。
- **tail**：查看文件尾部内容。
- **tar**：打包文件。
- **zip**：压缩文件。
- **gunzip**：解压缩文件。
- **rar**：创建或解压 RAR 文件。



### 日期处理类命令

- **date**：获取或格式化日期时间。



### 线程与异步类命令

- **container**：创建脚本执行容器，隔离执行环境。
- **sleep**：线程休眠。
- **nohup**：后台异步执行脚本，类似 Linux 中的 `nohup`。
- **wait**：等待线程任务结束。



### 特殊操作符

- **管道符 `|`**：将前一条命令的输出作为后一条命令的输入。
- **命令替换符 `command`**：执行嵌套命令，并将其输出作为当前命令的输入。



## 自定义命令

在脚本引擎中，可以通过 **接口实现** 或 **模版继承** 两种方式实现自定义命令，满足业务扩展需求。

### 接口方式

- 实现 `cn.org.expect.script.UniversalCommandCompiler` 接口（代表一个标准的命令实现）；
- 在实现类上标注 `cn.org.expect.script.annotation.EasyCommandCompiler` 注解（表明该类是一个可被引擎识别的命令）；

### 模版方式

为了简化开发流程，脚本引擎提供了一系列 **命令模版基类**。

开发人员可继承模版类，复写业务逻辑，大幅减少开发工作量。

使用方式：

- 继承适合的模版类，实现具体业务逻辑。
- 在命令类上配置 `cn.org.expect.script.annotation.EasyCommandCompiler` 注解，使其成为可用命令。

### 命令模版分类

| 模版类型               | 基类名称 | 适用场景                                           |
| ---------------------- | -------- | -------------------------------------------------- |
| 带日志输出的命令模版   | `cn.org.expect.script.command.AbstractTraceCommandCompiler`  | 需要在执行过程中生成日志，支持将日志输出至文件。   |
| 不带日志输出的命令模版 | `cn.org.expect.script.command.AbstractCommandCompiler`  | 普通功能型命令，无需日志记录。                     |
| 支持文件操作的命令模版 | `cn.org.expect.script.command.AbstractFileCommandCompiler`  | 命令涉及文件读写、目录操作等，如 `cat`、`rm`。     |
| 支持全局功能的命令模版 | `cn.org.expect.script.command.AbstractGlobalCommandCompiler`  | 适合执行全局级别配置或环境初始化相关操作。         |
| 主从关系的命令模版     | `cn.org.expect.script.command.AbstractSlaveCommandCompiler`  | 命令存在父子或主从关系，例如容器类命令或块级命令。 |

### 附加扩展接口

根据业务复杂度，可选择实现以下接口，为命令提供额外能力：

| 接口名称 | 功能描述       | 典型应用场景                                      |
| -------- | -------------- | ------------------------------------------------- |
| `cn.org.expect.script.UniversalScriptInputStream`  | 支持管道操作   | 命令与管道符 `                                    |
| `cn.org.expect.script.command.feature.LoopCommandKind`  | 控制循环体     | 影响 `while`、`for` 等循环行为。                  |
| `cn.org.expect.script.command.feature.NohupCommandSupported`  | 异步并发运行   | 支持并发任务处理，例如 `nohup`。                  |
| `cn.org.expect.script.command.feature.CallbackCommandSupported`  | 回调函数       | 任务执行过程中触发回调。                          |
| `cn.org.expect.script.command.feature.LoopCommandSupported`  | 在循环体中使用 | 可嵌套在 `while`、`for` 结构内执行。              |
| `cn.org.expect.script.command.feature.JumpCommandSupported`  | 跳跃执行       | 配合 `jump` 命令，实现跳过特定命令块。            |
| `cn.org.expect.script.command.feature.WithBodyCommandSupported`  | 命令块         | 支持成组命令，例如：`while`、`for` 块内多条语句。 |



## 已注册命令



### memo

注释内容是以符号 `#` 开始的一个字符串。

#### 语法

```shell
$ # 注释内容
```

#### 示例

```shell
echo test # 注释内容
### 注释内容
```



### echo

标准输出

#### 语法

```shell
$ echo [-n] 字符串[;]
```

转义字符是反斜杠

#### 示例

打开信息输出：

```shell
$ echo on
```

关闭信息输出：

```shell
$ echo off
```

输出字符串：

```shell
$ echo hello world!
```

输出双引号中的字符串，会替换占位符 `${name}`

```shell
$ echo "hello ${name}!"
```

输出字符串，右侧无回车与换行符

```shell
$ echo -n "test"
```

输出字符串常量，单引号内表示字符串常量，不会对占位符、命令替换符进行解析

```shell
$ echo 'hello `pwd` ${name}'
```

输出标准信息与错误信息到日志文件

```shell
$ echo "${variable}" 1>`pwd`/echo.log 2>`pwd`/echo_err.log
```

输出标准信息与错误信息到日志文件

```shell
$ echo "${variable}" 1>`pwd`/echo.log 2>&1
```



### error

错误输出

#### 语法

```shell
$ error 字符串[;]
```



### set

设置局部变量

#### 语法

```shell
$ set varname=value[;]
```

#### 示例

在变量的赋值表达式中可以引用已有的全局变量、局部变量、全局JDBC配置信息、脚本引擎内部变量、环境变量。

```shell
$ set varname=count + 1;
```

可以对数值型变量使用**加减乘除**运算

```shell
$ set varname=1+1;
```

可以对字符型变量进行加法操作

```shell
$ set varname='1'+'2';
```

可以使用变量方法（详 variablemethod 命令）修改变量值

```shell
$ set varname=varname.substr(1, 2).length();
```

设置字符串常量

```shell
$ set varname='content';
```

设置字符型变量

```shell
$ set varname="content";
$ set varMultiyLine="""
	1213
	567
	890
"""
```

设置数值型变量

```shell
$ set varname=0;
```

保存 SQL 语句查询结果

```shell
$ set varname=select count(*) from tablename;
```

命令执行失败或发生错误，不会抛出异常

```shell
$ set -E
```

命令执行失败或发生错误，会抛出异常

```shell
$ set -e
```



### variable method

执行变量方法

#### 语法

```shell
$ 变量名.变量方法 | 变量名[位置信息]
```

#### 示例

```shell
set testline=`wc -l $TMPDIR/test.log`
set testline=testline.split()[0]
echo $testline
```



### pipe

管道操作

#### 语法

```shell
$ 命令一 | 命令二 | .. | 命令N
```

#### 示例

```shell
$ cat `pwd`/text | tail -n 1
```



### sub

命令替换

#### 语法

```shell
$ `命令`
```

#### 示例

在赋值语句中使用：

```shell
$ set currentstr=`echo 20210101 | date "yyyy-MM-dd"`
```

在布尔表达式中使用：

```shell
if `date -d 20201213 'yyyy-MM-dd' +0day` != "2020-12-13" then
  echo `date -d 20201213 'yyyy-MM-dd' +0day` != "2020-12-13"
  exit 1
fi
```

在表达式中使用：

```shell
$ set loadtask_stdout="现在是" + `date -d ${tmp_enddate} yyyy年MM月dd日` + ", 进行日期检查!"
```

在输出语句中使用：

```shell
$ echo `currentdate.format(yyyy-MM-dd)` != '2020-01-01'
```



### function

自定义方法

#### 语法

```shell
function 方法名() {
..
}
```

在方法体中使用 `$1` 表示外部输入的参数1， `$0` 表示方法名，`$#` 表示输入参数个数，使用 `return` 关键字可以退出方法体。

需要注意在方法体中不能使用 `step` 与`jump` 命令。

#### 示例

```shell
function testfunc() {
  echo "execute testfunc() $1 $2"
  return 1
}

testfunc "1" "2"
```

#### 保留方法

```shell
### 执行 step 命令的处理逻辑
$ function step() {echo $1;}

### 执行 echo 命令的处理逻辑
$ function echo() {echo $1;}

### 执行错误的处理逻辑
$ function error() {echo $1;}
```



### execute function

执行自定义方法

#### 语法

```shell
$ functionName [参数]...[;]
```

#### 示例

```shell
$ function test() {echo $1;}
$ test “hello world!”
```



### debug

调试命令

如下代码，是一个复制文件的脚本片段

```shell
set delfilepath="$TMPDIR/bhc_finish.del"
rm ${delfilepath}
cp classpath:/bhc_finish.del ${TMPDIR}
```

想要在 cp 命令执行之前进入 debug 模式，可以在脚本命令中增加 debug 命令，在 `cn.org.expect.script.command.DebugCommand` 类的 `execute` 方法中打断点

```shell
set delfilepath="$TMPDIR/bhc_finish.del"
rm ${delfilepath}
debug
cp classpath:/bhc_finish.del ${TMPDIR}
```

IDE 在执行到 debug 命令时会停留在断点位置上，便于调试



### export

设置全局变量与自定义方法

#### 语法

```shell
$ export set name = value [;]
$ export function 自定义方法名 [;]
```

子脚本会继承父脚本中定义的全局变量与全局用户自定义方法

#### 示例

设置全局变量

```shell
export set myname='this is good'
```

设置全局自定义方法

```shell
function test() {
	echo "test .."
}

export function test;
```



### step

建立步骤标记

#### 语法

```shell
$ step 步骤名[;]
```

标记当前执行位置，配合 `jump` 命令，可以实现跳转到标记位置处开始执行的效果。

#### 示例

```shell
set jumpvar="string1" 
jump ${jumpvar} 

... 

step string1 

delete from ... ; 
insert into ... ; 
commit; 
... 
step string2; 
...
```

配合 `jump` 与 `function()` `error() {}` 命令使用可以实现从脚本上一次执行报错位置开始向下执行的效果，跳过已执行部分，如下:

```shell
### script start 

function error() { 
### 记录报错时 step 位置信息, 用于下一次从报错处开始执行 
insert into table ... 
... 
}


### 查询上一次执行位置信息 
set jumpvar=select ... ; 

### 跳转到上一次执行位置 
jump ${jumpvar} 

... 

step string1 

delete from ... ; 
insert into ... ; 
commit; 
... 
step string2; 
...
```



### jump

跳转到 `step` 命令位置后继续向下执行

在找到 `step` 命令前会根据命令的 `enableJump()` 方法返回值判断是否越过（不执行）命令。

在脚本文件中可以使用内置变量 `jump` 判断当前脚本引擎是否处于 `jump` 命令状态。

#### 语法

```shell
$ jump 步骤名[;]
```

#### 示例

```shell
### script start 
jump stepMessage; 

... 

if "$jump" == "true" then 
... 
else 
... 
fi 

...

step stepMessage 
... 
```



### exit

退出语句

#### 语法

```shell
$ exit 返回值
```

返回零表示执行正确，返回非零整数表示错误。

#### 示例

```shell
$ exit 0
```

退出并返回某个对象，Java 类：

```java
public class Main {
		public static void main(String[] args) {
        EasyContext context = new EasyBeanContext();
        UniversalScriptEngineFactory factory = new UniversalScriptEngineFactory(context);
        UniversalScriptEngine engine = factory.getScriptEngine();
        ArrayList result = engine.eval(". classpath:/script/test.sql");
    }
}
```

`script/test.sql` 脚本：

```shell
set result = this.forName("java.util.ArrayList").newInstance()
exit result
```



### sql

执行 SQL 语句，SQL 语句必须以 `;` 符号结束标志。

需要对 SQL 语句进行转义：把字符 `$` 替换成 `&ads;`

在脚本文件中可以使用 SQL 注释 `--` 与 `/** */`

#### 语法

```shell
$ [sql] [ select .. | insert .. | delete .. | update .. | merge .. | alter .. | create .. | drop .. ];
```

#### 示例

```shell
set rows=update tableName set field='a' where ...;
echo "SQL共更新 ${rows} 条记录!"
```



### declare catalog

定义数据库编目信息

#### 语法

```shell
$ declare [global] 数据库编目名 catalog configuration use driver 数据库驱动类名 url 数据库JDBC的URL路径 username 用户名 password 密码 [;]
$ declare [global] 数据库编目名 catalog configuration use file JDBC配置文件绝对路径 [;]
```

**global** 是可选项，表示编目信息可被子脚本使用。

属性列表：

```properties
host                        可选，数据库服务器地址
driver                      数据库驱动类名
url                         数据库JDBC URL
username                    数据库用户名
password                    数据库密码
admin.username              可选，数据库管理员用户
admin.password              可选，数据库管理员密码
file                        JDBC 配置文件地址
ssh.username                可选，表示 ssh 用户名
ssh.password                可选，表示 ssh 用户密码
ssh.port                    可选，表示 ssh 服务端口
```

数据库驱动类名是必填选项，二端可用单引号或双引号。

数据库**JDBC**的**URL**路径是必填选项，二端可用单引号或双引号。

用户名是必填选项，二端可用单引号或双引号。

密码是必填选项，二端可用单引号或双引号。

**JDBC** 配置文件中必须要有 **driverClassName** 属性，**url** 属性，**username** 属性，**password** 属性。



#### 示例

通过 **JDBC** 配置文件来定义数据库编目

```shell
$ declare global name catalog configuration use file /home/udsf/jdbc.properties;
```

通过 **JDBC** 属性来定义数据库编目

```shell
$ declare global name catalog configuration use driver com.ibm.db2.jcc.DB2Driver url 'jdbc:db2://127.0.0.1:50000/databaseName' username admin password admin;
```



### db connect

#### 语法

建立连接

```shell
$ db connect to 数据库编目名[;]
```

关闭连接

```shell
$ db connect reset[;]
```



### db export

从数据库中卸载数据到指定位置。

#### 语法

```shell
$ db export to 卸载位置 of 文件类型 [ modified by 参数名=参数值 参数名=参数值 参数名 ] select * from table;
```

#### 卸载位置

| Component Name | Class Name                                                | Description                                                                                                                                                                                                                                                  |
| -------------- | --------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
|    **ftp**     | `cn.org.expect.database.export.inernal.FtpFileWriter`     | 卸载数据到远程ftp服务器<br>ftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径                                                                                                                                                               |
|    **http**    | `cn.org.expect.database.export.inernal.HttpRequestWriter` | 卸载数据到用户浏览器<br>http://download/HttpServletRequest 对象的变量名/HttpServletResponse对象的变量名/下载文件名（需要提前将 HttpServletRequest 对象与 HttpServletResponse 对象保存到脚本引擎变量中，变量分别是: httpServletRequest, httpServletResponse） |
|   **local**    | `cn.org.expect.database.export.inernal.ExtractFileWriter` | 卸载数据到本地文件                                                                                                                                                                                                                                           |
|    **sftp**    | `cn.org.expect.database.export.inernal.SftpFileWriter`    | 卸载数据到远程sftp服务器<br>sftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径                                                                                                                                                             |


自定义卸载位置格式：`bean://name`，例如：

```java
@EasyBean(name = "name")
public class UserDefineWriter implements cn.org.expect.database.export.ExtractWriter {
..
}
```

```shell
db export to bean://name of txt select * from table;
```

#### 文件类型

| Component Name | Class Name                                 | Description                                                                     |
| -------------- | ------------------------------------------ | ------------------------------------------------------------------------------- |
|    **csv**     | `cn.org.expect.io.CsvFile`                 | CSV格式文件                                                                     |
|    **del**     | `cn.org.expect.database.db2.DB2ExportFile` | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符 |
|    **txt**     | `cn.org.expect.io.CommonTextTableFile`     | 文本文件, 逗号分隔，无转义字符，无字符串限定符                                  |


自定义文件类型：实现 `cn.org.expect.io.TextTableFile` 接口

如: 自定义 csv 文件类型：

```java
@EasyBean(name = "csv")
public class CsvExtractStyle implements cn.org.expect.io.TextTableFile {
..
public CsvExtractStyle() ..
..
}
```

#### 参数说明

```properties
charset:        表示数据卸载后的字符集，默认使用 JVM 的 file.encoding 参数作为默认值 
codepage:       表示数据卸载后的字符集，默认使用 JVM 的 file.encoding 参数作为默认值（与 charset 属性冲突） 
rowdel:         表示行间分隔符，使用回车或换行符需要转义，如: r n 
coldel:         表示文件中字段间的分隔符 
escape:         表示文件中字符串中的转义字符 
chardel:        表示字符串型的字段二端的限定符 
column:         表示文件中每行记录的字段个数（如果记录的字段数不等于这个值时会抛出异常） 
colname:        表示文件中字段名，格式是：位置信息:字段名，如: 1:客户名,2:客户编号 如果已设置 table 属性则可以使用表中字段名如：username:客户名,2:userage 
catalog:        表示数据库编目编号，用于设置从哪个数据源中卸载数据，默认使用脚本引擎当前数据库编目 
message:        消息文件绝对路径参数, 用于保存卸载任务的运行结果
listener:       任务生命周期监听器集合, 每个监听器的 Java 类名之间用半角逗号分隔，监听器类必须实现 cn.org.expect.database.export.ExtractUserListener 接口    
convert:        数据集字段的处理逻辑集合, 格式：字段名:字段处理逻辑类名，格式: JAVA处理逻辑类名?属性名=属性值&属性名=属性值 
                其中字段处理逻辑类名必须实现 cn.org.expect.database.JdbcObjectConverter 接口, 可以在类名后使用 “?属性名=属性值” 格式向处理逻辑中设置属性 
                使用半角逗号分隔 
                convert 参数映射关系的优先级 高于 数据库方言提供的映射关系 
charhide:       字符串型字段的字符过滤器参数，指定哪些字符需要过滤，未设置参数时默认过滤回车符和换行符    
escapes:        字符串型字段中需要进行转义的所有字符集合 
writebuf:       数据输出流中缓冲区的行数（必须大于零），默认缓冲 100 行    
append:         写入数据的方式, 无值型属性，设置属性时表示将数据追加写入到卸载位置上，不设置属性时表示覆盖原有数据。
maxrows:        表示最大行数, 超过最大行数时将后续数据写入到一个新存储上，通过增加数字编号区分不同存储信息，默认 0 表示无限制 
dateformat:     表示日期格式 
timeformat:     表示时间格式 
timestampformat:表示时间戳格式 
progress:       表示卸载数据的进度输出接口编号，需要提前在脚本引擎中定义进度输出接口 
```

`db export` 命令支持 `container` 命令，可以并行执行多个数据卸载命令。

#### 示例

```shell
declare global test0001 catalog configuration use host ${databaseHost} driver $databaseDriverName url "${databaseUrl}" username ${username} password $password sshuser ${databaseSSHUser} sshuserpw ${databaseSSHUserPw} ssh 22

db connect to test0001

declare exportTaskId progress use out print "${taskId}正在执行 ${process}%, 总共${totalRecord}个记录${leftTime}" total $tcount times

db export to $TMPDIRv7_test_tmp.del of del modified by progress=exportTaskId chardel=* charhide=0 escapes=1 writebuf=200 maxrows=30041 title message=$TMPDIR/v7_test_tmp.txt select * from v7_test_tmp with ur;
```



### db load

将指定位置的数据文件装载到数据库表中。

#### 语法

```shell
$ db load from 文件位置 of 文件类型 [ method P(3,2,1) C(字段名, 字段名) ] [ modified by 参数名=参数值 参数名=参数值 参数名 ] [ replace | insert | merge ] into table[(字段名,字段名,字段名)] [ for exception tableName ] [ indexing mode [ rebuild | incremental ]] [ statistics use profile ] [ prevent repeat operation ];
```

#### 文件位置

数据文件的绝对路径（两端可以使用引号），多个文件用半角逗号分割。

#### 文件类型

| Component Name | Class Name                                 | Description                                                                     |
| -------------- | ------------------------------------------ | ------------------------------------------------------------------------------- |
|    **csv**     | `cn.org.expect.io.CsvFile`                 | CSV格式文件                                                                     |
|    **del**     | `cn.org.expect.database.db2.DB2ExportFile` | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符 |
|    **txt**     | `cn.org.expect.io.CommonTextTableFile`     | 文本文件, 逗号分隔，无转义字符，无字符串限定符                                  |


实现用户自定义文件类型：实现 `cn.org.expect.io.TextTableFile` 接口

如：自定义 csv 文件类型

```java
@EasyBean(name = "csv", description = "csv文件格式")
public class CsvExtractStyle implements cn.org.expect.io.TextTableFile {
..
public CsvExtractStyle() ..
..
}
```

#### 参数说明

```properties
charset:        表示数据文件的字符集，默认使用 JVM 的 file.encoding 参数作为默认值 
codepage:       表示数据文件的字符集，默认使用 JVM 的 file.encoding 参数作为默认值（与 charset 属性冲突） 
rowdel:         表示数据文件中行间分隔符，使用回车或换行符需要转义，如: r n 
coldel:         表示数据文件中字段间的分隔符 
escape:         表示数据文件中字符串中的转义字符 
chardel:        表示数据文件中字符串型字段的二端的限定符 
column:         表示文件中每行记录的字段个数（如果记录的字段数不等于这个值时会抛出异常） 
colname:        表示文件中字段名，格式是：位置信息:字段名，如: 1:客户名,2:客户编号 如果已设置 table 属性则可以使用表中字段名如：username:客户名,2:userage
readbuf:        用于指定输入流缓冲区长度（单位字节），可以是 100M 或 1G 这种格式  
catalog:        表示数据库编目编号，用于设置从哪个数据源中卸载数据，默认使用脚本引擎当前数据库编目。 
tableCatalog:   表示数据库表所属数据库的编目  
launch:         表示装数引擎启动条件，属性值可以是类名或脚本语句（脚本语句返回值是0表示可以执行数据装载，返回值是非0不能执行数据装载）  
convert:        数据集字段的处理逻辑集合, 格式：字段名:字段处理逻辑类名?属性名=属性值&属性名=属性值，用于设置字段名数据转换器的映射关系  
savecount:      表示每装载 n 行后建立一致点。消息文件中将生成和记录一些消息，用于表明在保存点所在时间上有多少输入行被成功地装载。 
dateformat:     表示日期字符串格式 
timeformat:     表示时间字符串格式 
timestampformat:表示时间戳字符串格式 
keepblanks:     表示将数据装入到一个变长列时，会截断尾部空格，若未指定则将保留空格。只有参数名无需设置参数值；
message:        用于指定消息文件绝对路径  
nocrlf:         表示自动删除字符串中的回车符和换行符，只有参数名无需设置参数值  
dumpfile:       用于指定错误数据存储文件路径  
progress:       用于指定装载文件的进度输出接口编号，需要提前在脚本引擎中定义进度输出接口
```

#### 数据装载模式

```properties
replace:        表示先清空数据库表中所有数据，然后再读取文件批量插入到数据库表中。 
insert:         表示插入模式：读取文件内容，并将文件每行记录通过批量接口插入到数据库表中。 
merge:          表示合并模式：读取文件内容，并将文件每行记录通过批量接口插入到数据库表中。如果数据在数据库表中已存在则使用文件内容更新，如果数据不存在则将文件行插入到表中。
                使用合并模式时，需要使用 method C(字段名, 字段名) 语句设置判断记录是否相等的字段名。 
```

#### 设置字段顺序

可以使用 `method` 句柄设置文件中列插入到数据库表的顺序，如 ` method P(3,2,1)` 表示按第三列，第二列，第一列的顺序读取文件中每列数据并插入到数据库表中。

可以在数据库表名后面使用小括号与字段名的方式指定向数据库表中插入字段的顺序，如：`tableName(fname1,fname3,fname2)`表示按 `fname1,fname3,fname2` 字段顺序插入数据。

#### 其他句柄说明

指定对于主键冲突错误问题，自动将重复记录保存到 for 语句指定的表中。

```shell
for exception tableName
```

指定装载文件之前，先根据消息文件中的内容判断是否需要重新装载数据文件，只有参数名无需设置参数值

```shell
prevent repeat operation
```

用于设置索引模式，rebuild 表示先删除索引文件装载成功后重建索引，incremental 表示只向索引中添加新的数据

```shell
indexing mode [ rebuild | incremental ]
```

表示在数据文件装载成功后，因为数据库表中添加了更多的数据，导致之前的目标表统计信息很可能已经无效了。可以为数据库表重新收集统计信息

```shell
statistics use profile
```

`db load` 命令支持 `container` 命令，可以并行执行多个数据文件装载命令。



### db get cfg for

返回数据库元信息

#### 语法

打印数据库支持的字段类型

```shell
$ db get cfg for field type
```

打印数据库中的表的类型

```shell
$ db get cfg for table type
```

打印数据库中的 catalog

```shell
$ db get cfg for catalog
```

打印数据库中的 schema

```shell
$ db get cfg for schema
```



### increment

对比 2 个表格型数据文件并抽取增量数据

#### 语法

```shell
$ extract increment compare 新文件 of 文件类型 modified by 属性名=属性值 and 旧文件 of 文件类型 modified by 属性名=属性值 write [new [and] upd [and] del] into filepath [of 文件类型] [modified by 属性名=属性值] [write log into [filepath | stdout | stderr]]
```

新文件表示新文件的绝对路径 

旧文件表示旧文件的绝对路径 

#### 文件类型

| Component Name | Class Name                                 | Description                                                                     |
| -------------- | ------------------------------------------ | ------------------------------------------------------------------------------- |
|    **csv**     | `cn.org.expect.io.CsvFile`                 | CSV格式文件                                                                     |
|    **del**     | `cn.org.expect.database.db2.DB2ExportFile` | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符 |
|    **txt**     | `cn.org.expect.io.CommonTextTableFile`     | 文本文件, 逗号分隔，无转义字符，无字符串限定符                                  |


可以自定义增量数据输出流：将新增数据，变化数据，删除数据输出到指定文件中，如下面语句表示将新增数据与变化的数据写入到 filepath 文件中，且新增数据的第一个字段修改为false，第二个字段内容修改 uuid，第三个字段内容修改为格式是 `yyyyMMddHHmmss` 的当前时间

```shell
write new and upd into /home/user/inc.txt of txt modified by newchg=1:false,2:uuid updchg=3:date=yyyyMMddHHmmss
```

可以将新增，发生变化，已删除的数据都写入到一个文件（如果输出流中未定义文件类型时，默认使用新文件的文件类型），如：

```shell
write into /home/user/inc.txt modified by newchg=1:uuid
```

可以定义日志信息输出流（非必填）将增量日志写入到指定文件中或脚本引擎的标准输出流中，如：

```shell
write log into /home/user/inc.log
write log into stdout
write log into stderr
```

#### 文件属性

```properties
charset:    表示数据文件的字符集，默认使用 JVM 的 file.encoding 参数作为默认值
codepage:   表示数据文件的字符集，默认使用 JVM 的 file.encoding 参数作为默认值（与 charset 属性冲突）
rowdel:     表示数据文件中的行间分隔符，使用回车或换行符需要转义，如: r n
coldel:     表示数据文件中字段间的分隔符
escape:     表示数据文件中字符串中的转义字符 
chardel:    表示数据文件中字符串型字段的二端的限定符 
column:     表示数据文件中每行记录的字段个数（如果记录的字段数不等于这个值时会抛出异常）
colname:    表示数据文件中字段名，格式是：位置信息:字段名，如: 1:客户名,2:客户编号 如果已设置 table 属性则可以使用表中字段名如：username:客户名,2:userage 
index:      必填，表示数据文件中唯一确定一条记录的索引字段集合，格式: 字段位置信息, 如：1,2,3,4 如果已设置 table 属性则可以使用表中字段名如： id,name,age,value 
compare:    表示文件中比较字段（相同索引字段时，用于区分二条记录是否相等的字段，如果二条记录中的索引字段与比较字段都相等则认为二条记录相等），格式: 字段位置信息如：1,2,3,4 如果已设置 table 属性则可以使用表中字段名如：name,age,val1,val2。未设置参数时会默认比较记录中每个字段值
table:      表示文件中字段对应的数据库表名（可以是 schema.tableName 格式）
catalog:    表示脚本引擎中定义的数据库编目号
readbuf:    表示读取文件时使用的字符缓冲区长度，默认 100M 个字符 
progress:   表示脚本引擎中已定义的进度输出编号，用于输出文件的读取进度信息 
nosort:     设置 true 表示剥离增量之前不会排序文件，默认是 false 表示先排序文件然后再执行剥离增量
sortcache:  排序文件输出流使用的缓冲行数，默认是 100 行 
sortrows:   排序文件时每个临时文件的最大行数，默认是 10000 行
sortThread: 排序文件时的线程数，默认是 3 个线程
sortReadBuf:排序文件时的输入流的缓冲区长度，默认是 10M 个字符
maxfile:    排序文件时，每个线程每次合并的最大临时文件数, 默认是 4 个文件
keeptemp:   设置 true 表示排序文件后保留临时文件，默认是 false 表示删除产生的临时文件
covsrc:     设置 true 表示排序文件后覆盖源文件，默认是 false 表示保留源文件内容
temp:       排序文件使用的临时目录
```

增量数据输出流支持的属性有：

```properties
newchg:     表示对新增数据中字段的替换规则
updchg:     表示对变化数据中字段的替换规则
delchg:     表示对删除数据中字段的替换规则
charset:    表示文件对应的字符集编码，默认使用JAVA虚拟机默认的文件字符集
codepage:   表示文件对应的代码页（与 charset 属性冲突）
append:     设置 true 表示追加方式写入文件，默认是 false 表示覆盖文件
outbuf:     设置输出流的缓冲行数，默认是 20 行
```

剥离增量命令支持 container 命令，可以并行执行多个剥离增量命令。



### sort table file

对表格型文件排序

#### 语法

```shell
$ sort table file 数据文件绝对路径 of 文件类型 [modified by 属性名=属性值 属性名] order by 排序字段 {asc | desc}
```

#### 文件类型

| Component Name | Class Name                                 | Description                                                                     |
| -------------- | ------------------------------------------ | ------------------------------------------------------------------------------- |
|    **csv**     | `cn.org.expect.io.CsvFile`                 | CSV格式文件                                                                     |
|    **del**     | `cn.org.expect.database.db2.DB2ExportFile` | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符 |
|    **txt**     | `cn.org.expect.io.CommonTextTableFile`     | 文本文件, 逗号分隔，无转义字符，无字符串限定符                                  |


#### 文件属性

```properties
charset:    表示数据卸载后的字符集，默认使用 JVM 的 file.encoding 参数作为默认值
codepage:   表示数据卸载后的字符集，默认使用 JVM 的 file.encoding 参数作为默认值（与 charset 属性冲突）
rowdel:     表示行间分隔符，使用回车或换行符需要转义，如: r n
coldel:     表示文件中字段间的分隔符
escape:     表示文件中字符串中的转义字符
chardel:    表示字符串型的字段二端的限定符
column:     表示文件中每行记录的字段个数（如果记录的字段数不等于这个值时会抛出异常）
colname:    表示文件中字段名，格式是：位置信息:字段名，如: 1:客户名,2:客户编号 如果已设置 table 属性则可以使用表中字段名如：username:客户名,2:userage
readbuf:    表示读取文件时使用的字符缓冲区长度，默认 10M 个字符
writebuf:   表示写文件时使用的缓存行数，默认 100 行
thread:     排序文件时的线程数，默认是 3 个线程
maxrow:     表示每个临时文件最大记录书, 默认是 10000 行
maxfile:    排序文件时，每个线程每次合并的最大临时文件数, 默认是 4 个文件
keeptemp:   无属性值，使用属性表示排序文件后保留临时文件，否则表示自动删除产生的临时文件
covsrc:     设置 true 表示排序文件后覆盖源文件，默认是 false 表示保留源文件内容
```

排序字段:    由排序字段的位置（大于零）组成，如: 1、2、3

排序方向:    asc 或 desc

asc:        排序字段从小到大排列

desc:       排序字段从大到小排列



### container

线程池，并发运行任务

#### 语法

```shell
$ container to execute tasks in parallel [ using 参数名=参数值 参数名=参数值 参数名 ] begin 并发任务 end
```

#### 示例

```shell
container to execute tasks in parallel using thread=3 rowdel=rn coldel=: begin
  db export to $filepath1.del of del select * from table with ur;
  db export to $filepath2.del of del select * from table with ur;
  db export to $filepath3.del of del select * from table with ur;
  db export to $filepath4.del of del select * from table with ur;
  db export to $filepath5.del of del select * from table with ur;
  db export to $filepath6.del of del select * from table with ur;
end
```

`thread` 参数表示并发任务数，默认值是 2



### commit

提交数据库事务

#### 语法

```shell
$ commit[;]
```



### rollback

回滚数据库事务

#### 语法

```shell
$ rollback[;]
```



### quiet

以静默（不抛异常、不输出信息）方式执行命令

#### 语法

```shell
$ quiet 命令;
```

#### 示例

```shell
$ quiet select * from table;
$ quiet commit
```



### call procudure

执行存储过程

#### 语法

```shell
$ call SCHEMA.PRODUCENAME(?)[;]
```

#### 示例

```shell
$ call SYSPROC.ADMIN_CMD('reorg indexes all for table ALLOW READ ACCESS');
$ call TEST('read in msg', ?);
$ call TEST('read in msg', $RES); echo $RES;
```



### declare cursor

声明游标

使用 `cursor 游标名 loop .. end loop` 语句遍历游标对象。

使用 `fetch cursorName into variableName1, variableName2, variableName3` 语句将游标中当前行的字段保存到自定义变量中。

使用 `undeclare 游标名 cursor` 语句关闭游标。

#### 语法

```shell
$ declare 游标名 cursor with return for select * from table;
```

#### 示例

```shell
db connect to databasename 
declare cno cursor with return for select * from table;
cursor cno loop 
  fetch cno into tmp_val1, tmp_val2, tmp_val3; 
  echo ${tmp_val1} ${tmp_val2} ${tmp_val3} 
end loop
undeclare cno cursor;
```



### cursor

遍历游标

#### 语法

```shell
cursor 游标名 loop
..
end loop
```

可以在循环体中使用 break、continue、return 控制语句

- break：退出当前循环
- continue：执行下一次循环
- return：退出当前方法



### while

while 语句

#### 语法

```shell
while .. loop
..
end loop
```

可以在循环体中使用 break、continue、return 控制语句

- break：退出当前循环
- continue：执行下一次循环
- return：退出当前方法



### for

**for** 循环语句，用于便利数组与集合中的元素，可通过变量名在循环体中使用数组或集合中的元素。

#### 语法

```shell
for 变量名 in 表达式 loop
..
end loop
```

表达式：

1）可以是替换命令如：`ls` 
2）可以是数组或集合的变量名，如: `${arrayName}` 或 `listName`
3）可以是字符串常量，如：`(1,2,3,4)`

可以在循环体中使用 break、continue、return 控制语句

- break：退出当前循环
- continue：执行下一次循环
- return：退出当前方法



### read

读取文件或文本信息

#### 语法

```shell
while read 变量名 do
..
done < [ filepath | command ]
```

可以在循环体中使用 `break`、`continue`、`return` 控制语句

- `break`：退出当前循环
- `continue`：执行下一次循环
- `return`：退出当前方法

#### 示例

循环遍历 `/home/user` 目录下的文件

```shell
while read line do
.. 
done < ls /home/user
```

逐行读取文件中内容

```shell
while read line do
..
done < /home/user/list.txt
```



### if

`if` 语句

#### 语法

```shell
if .. then .. elsif .. then .. elseif .. then .. fi
```



### ssh2

登录 `ssh` 服务器执行命令

#### 语法

```shell
$ ssh username@host:port?password= && . /etc.profile && . ~/.profile && shell command [;]
```

#### 示例

```shell
$ ssh admin@192.168.1.1:10?password=admin && ./shell.sh && . ~/load.sh
```



### declare ssh tunnel

建立本地端口转发隧道，配合 **sftp** 命令实现通过本地局域网代理服务器访问远程服务器 **ssh** 端口功能。

#### 语法

建立隧道命令：

```shell
$ declare 隧道名 ssh tunnel use proxy 代理服务器用户名@代理服务器HOST:代理服务器SSH端口号?password=密码 connect to 本地端口号:远程服务器HOST:远程服务器SSH端口号 [;]
```

其中本地服务器端口号为零时表示端口由操作系统随机分配，随机分配的端口号通过标准输出接口输出

关闭隧道命令：

```shell
$ undeclare 隧道名 ssh tunnel [;]
```

#### 示例

```shell
### 建立隧道并获取本地端口号 
set localport=`declare sshname ssh tunnel use proxy root@192.168.1.10:22?password=root connect to 0:192.168.10.20:22 | tail -n 1` 

### 建立sftp连接
sftp test@127.0.0.1:${localport}?password=test
   put `pwd`/file.txt /home/test 
bye

### 关闭隧道 
undeclare sshname tunnel
```



### sftp

建立 **sftp** 连接

#### 语法

```shell
$ sftp 用户名@服务器HOST:端口?password=密码
```

#### 相关命令

| 命令          | 说明                                 |
| ------------- | ------------------------------------ |
| `cd`          | 进入远程服务器目录                   |
| `ls`          | 查看远程服务器上文件列表信息         |
| `rm`          | 删除远程服务器上文件或目录           |
| `mkdir`       | 在远程服务器上创建目录               |
| `pwd`         | 查看远程服务器上当前目录的绝对路径   |
| `exists`      | 判断远程服务器上的文件或目录是否存在 |
| `isfile`      | 判断远程服务器的文件是否存在         |
| `isDirectory` | 判断远程服务器的目录文件是否存在     |
| `get`         | 从远程服务器下载文件                 |
| `put`         | 上传文件到远程服务器                 |
| `bye`         | 关闭 FTP 连接                        |

在 `cd ls rm mkdir pwd exists isFile isDirectory` 语句中可以使用 `-l` 选项，表示操作本地操作系统上的文件。



### ftp

建立 **ftp** 连接

#### 语法

```shell
$ ftp 用户名@服务器HOST:端口?password=密码
```

#### 相关命令

| 命令          | 说明                                 |
| ------------- | ------------------------------------ |
| `passive`     | 被动模式                             |
| `cd`          | 进入远程服务器目录                   |
| `ls`          | 查看远程服务器上文件列表信息         |
| `rm`          | 删除远程服务器上文件或目录           |
| `mkdir`       | 在远程服务器上创建目录               |
| `pwd`         | 查看远程服务器上当前目录的绝对路径   |
| `exists`      | 判断远程服务器上的文件或目录是否存在 |
| `isfile`      | 判断远程服务器的文件是否存在         |
| `isDirectory` | 判断远程服务器的目录文件是否存在     |
| `get`         | 从远程服务器下载文件                 |
| `put`         | 上传文件到远程服务器                 |
| `bye`         | 关闭 FTP 连接                        |

在 `cd ls rm mkdir pwd exists isFile isDirectory` 语句中可以使用 `-l` 选项，表示操作本地操作系统上的文件。



### passive

使用被动模式连接 FTP 服务器

#### 语法

```shell
$ passive -r
```

`-r` 选项：强制 FTP 客户端以反向模式解析 PASV 地址



### ls

显示本地目录下的文件或远程 `sftp/ftp` 服务器当前目录下文件。

#### 语法

```shell
$ ls 文件名或文件路径 [;]
```

文件名或文件路径二端可以使用成对的单引号或双引号。

#### 选项

```shell
-l 表示文件名或文件路径是本地操作系统文件路径
```



### cd

进入本地目录或远程 `sftp/ftp` 服务器目录。

#### 语法

```shell
$ cd 文件名或文件路径 [;]
```

文件名或文件路径二端可以使用成对的单引号或双引号。

```shell
$ cd -
```

切换到上一次所在的目录

#### 选项

```shell
-l 表示文件名或文件路径是本地操作系统文件路径
```



### length

显示本地文件或远程 `sftp/ftp` 文件的大小，或测量字符串的长度。

#### 语法

```shell
$ length string;
```

#### 选项

`-h` 选项：表示输出可读高的信息
`-b` 选项：表示显示字节数 
`-c` 选项：表示显示字符数 
`-f` 选项：表示本地文件的字节数 
`-r` 选项：表示显示远程文件的字节数

#### 示例

```shell
length -h string;   
length -b string;   
length -c string;   
length -f filepath; 
length -r remotefilepath;
```



### pwd

显示本地目录路径或远程 `sftp/ftp` 服务器当前目录路径。

#### 语法

```shell
$ pwd [-l] [;]
```

#### 选项

```shell
-l 选项表示显示本地操作系统上的目录
```



### mkdir

创建本地目录或在远程 `sftp/ftp` 服务器上创建目录。

#### 语法

```shell
$ mkdir [-l] 文件名或文件路径 [;]
```

文件名或文件路径二端可以使用成对的单引号或双引号。

#### 选项

```shell
-l 选项表示文件名或文件路径是本地操作系统上的文件
```



### rm

删除本地文件或目录或远程 `sftp/ftp` 服务器上的文件或目录。

#### 语法

```shell
$ rm [-l] 文件名或文件路径 [;]
```

文件名或文件路径二端可以使用成对的单引号或双引号。

#### 选项

```shell
-l 选项表示文件名或文件路径是本地操作系统上的文件
```



### isfile

判断文件是否存在或远程 `sftp/ftp` 服务器上是否存在文件。

#### 语法

```shell
$ [!]isfile [-l] 文件名或文件路径 [;]
```

文件名或文件路径二端可以使用成对的单引号或双引号。

#### 选项

```shell
-l 选项表示文件名或文件路径是本地操作系统上的文件
```



### isdirectory

判断目录文件是否存在或远程 `sftp/ftp` 服务器上是否存在目录。

#### 语法

```shell
$ [!]isDirectory [-l] 文件名或文件路径 [;]
```

文件名或文件路径二端可以使用成对的单引号或双引号。

#### 选项

```shell
-l 选项表示文件名或文件路径是本地操作系统上的文件
```



### exists

判断文件路径在本地或远程 `sftp/ftp` 服务器上是否存在文件路径。

#### 语法

```shell
$ [!]exists [-l] 文件名或文件路径 [;]
```

文件名或文件路径二端可以使用成对的单引号或双引号。

#### 选项

```shell
-l 选项表示文件名或文件路径是本地操作系统上的文件
```



### cat

输出本地文件内容

#### 语法

```shell
$ cat 文件名或文件路径 [;]
```

文件名或文件路径二端可以使用成对的单引号或双引号



### head

输出本地文件前 N 行的内容。

#### 语法

```shell
$ head [-n 行号] 文件名或文件路径 [;]
```

文件名或文件路径二端可以使用成对的单引号或双引号。

#### 示例

表示输出文件前 10 行的内容

```shell
$ head -n 10 /home/user/file.txt
```



### tail

输出文件结尾 n 行的内容

#### 语法

```shell
$ tail [-n 行号] 文件名或文件路径 [;]
```

文件名或文件路径二端可以使用成对的单引号或双引号。

#### 示例

表示输出文件结尾最后一行的内容

```shell
$ tail -n 1 /home/user/file.txt
```



### wc

显示文件的行数、字数、字节数、文件名。

#### 语法

```shell
$ wc [-l] [-w] [-c] filepath [;]
```

文件名或文件路径二端可以使用成对的单引号或双引号。

#### 选项

`-l` 选项：表示行数 
`-w` 选项：表示字符数 
`-c` 选项：表示字节数 



### df

显示当前操作系统的文件系统信息。

#### 语法

```shell
$ df [;]
```

#### 输出格式

```shell
第一个字段是文件系统 
第二个字段是总容量 
第三个字段是剩余容量 
第四个字段是已用容量 
第五个字段是文件系统类型（如: ext4） 
第六个字段是挂载位置信息
```



### dos2unix

将文件或字符串中的行间分隔符转为换行符。

#### 语法

```shell
$ dos2unix 文件路径|字符串 [;]
```



### grep

过滤数据

#### 语法

```shell
$ grep string
```

可以在管道符后过滤前一个命令的标准输出信息。

#### 选项

`-i` 选项：表示忽略字符大小写 
`-v` 选项：表示不包括字符串参数 

#### 示例

```shell
$ cat $TMPDIR/greptest.txt | grep -i test | wc -l
```



### os

执行本地操作系统命令

#### 语法

```shell
$ os command [;]
```

可以使用 `default os` 设置默认命令，执行本地操作系统命令可以不使用 `os` 前缀。

#### 示例

```shell
$ os cd /home/user/dir 
$ os ipconfig /all 
```



### execute file

执行脚本文件，使用 `nohup` 命令实现并行执行脚本文件。

#### 语法

```shell
$ . 文件名或文件路径 [;]
```

```shell
$ source 文件名或文件路径 [;]
```

子脚本可继承父脚本的全局变量、全局的数据库编目配置信息、全局的用户自定义方法、全局的异常错误处理逻辑、全局的 echo 命令处理逻辑、全局的错误处理逻辑、全局的步骤输出逻辑。

#### 相关命令

```shell
可以使用 >> 或 > 字符将日志信息输出到指定文件 
可以使用 1>> stdlogfile 表示将标准输出信息写入 stdlogfile 日志 . 
可以使用 2>> errlogfile 表示将错误输出信息写入 errlogfile 日志 . 
可以使用 2>&1 语句将标准输出与错误输出都写到同一个日志文件. 
可以使用 wait 命令等待并行脚本执行完毕，并返回脚本的返回值
```

#### 示例

```shell
$ set pid=`nohup . /home/user/script.sql | tail -n 1`
$ wait -pid=$pid 1min
```



### daemon

执行脚本文件，与 source 命令不同点：脚本执行完毕后，会将脚本产生的局部变量，全局变量，全局的数据库编目信息同步到当前脚本引擎中。

#### 语法

```shell
$ daemon 文件名或文件路径 [;]
```



### declare progress

进度输出

#### 语法

```shell
$ declare [global] [任务编号] progress use 输出方式 print 输出信息 total 总循环次数 times [;]
```

任务编号是可选选项，用于在并发任务区分不同任务的唯一编号。

`global` 是可选选项，用于表示是全局进度输出逻辑（可被子脚本继承）。

输出方式是必填选项，用于设置进度输出方式：

`out` 表示使用标准输出。

`err` 表示使用错误输出。

`step` 表示使用 **step** 输出。

总循环次数是必填选项，用于设置总循环次数，只能是整数数值。

###### 占位符

```shell
输出信息中可以使用 ${process} 输出当前进度百分比 
输出信息中可以使用 ${totalRecord} 输出总循环次数 
输出信息中可以使用 ${leftTime} 输出预估的剩余时间 
输出信息中可以使用 ${taskId} 输出多任务输出的任务编号 
```

#### 示例

```shell
### 定义一个进度输出信息 
declare global progress use step print "正在更新数据库记录 ${process}, 共有 ${totalRecord} 笔数据记录, ${leftTime} " total 10000 times 

while ... loop 
  # 进度输出 
  progress 
  ... 
end loop
```



### declare handler

异常处理逻辑

#### 语法

```shell
$ declare (exit | continue) handler for ( exception | exitcode != 0 | sqlstate == '02501' | errorcode -803 ) begin .. end
```

#### 保留变量

在 `begin` 与 `end` 区块中，脚本引擎会提供一组内置变量，用于获取异常或语句执行的相关信息：

| 变量   | 说明                                                         |
| ------ | ------------------------------------------------------------ |
| `exception` | 当脚本引擎触发异常时，表示异常的完整描述信息。               |
| `errorcode` | 当发生数据库错误时，表示数据库厂商提供的错误码（Vendor Error Code）。 |
| `sqlstate` | 当发生数据库错误时，表示数据库厂商提供的 SQL 状态码（SQLSTATE）。 |
| `errorscript` | 当发生异常时，表示引发错误的脚本语句内容。                   |
| `exitcode` | 表示当前语句的执行返回值。一般 0 表示成功，非 0 表示失败。   |



### undeclare handler

删除异常处理逻辑

#### 语法

```shell
$ undeclare handler for ( exception | exitcode == 0 | sqlstate == 120 | sqlcode == -803 ) ;
```



### handler

打印脚本引擎当前的 `echo` 方法处理逻辑、`error` 方法处理逻辑、`step` 方法处理逻辑、所有异常处理逻辑。

#### 语法

```shell
$ handler[;]
```



### callback

命令的回调函数：在宿主命令执行完毕之后自动执行的函数。

宿主命令表达式对应的脚本命令必须实现 `cn.org.expect.script.command.feature.CallbackCommandSupported` 接口，命令表达式可以是一个单词（如：echo 或 step 或 error）或一个语句（语句中不能有 begin 关键字）。

回调函数内容可以由单个或多行命令组成的段落，在回调函数内容中可以通过 `$1` 这种形式使用宿主命令的参数。

每个宿主命令都可以定义多个回调函数，按定义先后顺序执行回调函数内容。 

#### 语法

```shell
$ declare [global] command callback for 宿主命令表达式 begin 回调函数内容 end
```

#### 示例

```shell
### 定义一个 echo 命令的回调函数，实现将 echo 命令输出的内容同时写入到数据库表中。
declare gloabl command callback for echo begin
...
insert into logtable (content) values ($1) 
...
end
```

可以使用如下命令删除宿主命令 echo 上的所有回调函数: 

```shell
$ undeclare global command callback for echo
```



### declare statement

数据库批处理

可以使用 `fetch 变量名1, 变量名2, .. into 批处理名字` 语句批量更新数据库中数据

可以使用 `undeclare 批处理名字 statement` 语句关闭批处理程序

#### 语法

```shell
$ declare 批处理名字 statement by 笔数 batch with insert into table (f1,f2) values (?,?) ;
```

#### 示例

```shell
declare s1 statement by 1000 batch with insert into table (f1,f2) values (?,?) ; 
  set val1='1'
  set val2='2'
  FETCH val1, val2 insert s1; 
undeclare s1 statement;
```



### nohup

后台执行命令

#### 语法

```shell
$ nohup 命令语句 [&] [;]
```

#### 示例

并行执行脚本

```shell
$ nohup . /home/user/script.sql &
```

后台执行脚本并获取脚本 **pid** 编号

```shell
$ set pid=`nohup . scriptfile.sql & | tail -n 1`
```



### terminate

终止用户会话

#### 语法

```shell
$ terminate [-p 后台进程编号] [-s 用户会话编号] [;]
```

执行完 `terminate` 命令后是否立即退出，取决于命令的 `terminate()` 方法实现。

#### 示例

终止所有用户会话

```shell
$ terminate;
```

终止某个用户会话 

```shell
$ terminate -s Mc6e4645c26d94666a0a65621078aaeff;
```

终止当前用户会话中某个线程

```shell
$ terminate -p 21;
```



### wait

等待某个线程执行完毕

#### 语法

```shell
$ wait pid=进程编号 1{day|h|m|s|millis} [;]
```

使用 `1day` 设置超时时间，超时后自动退出。

#### 时间单位

|         |          |
| ------- | -------- |
| day     | 表示天   |
| millis  | 表示毫秒 |
| seconds | 表示秒   |
| second  | 表示秒   |
| sec     | 表示秒   |
| s       | 表示秒   |
| minutes | 表示分钟 |
| minute  | 表示分钟 |
| min     | 表示分钟 |
| m       | 表示分钟 |
| hour    | 表示小时 |
| hou     | 表示小时 |
| h       | 表示小时 |



### ps

查看进程信息

#### 语法

```shell
$ ps [-s] [;]
```

#### 选项

```shell
-s 选项显示所有用户会话
```



### sleep

使当前线程进入休眠

#### 语法

```shell
$ sleep 1 {day|h|m|s|millis}[;]
```

#### 时间单位

|         |          |
| ------- | -------- |
| day     | 表示天   |
| millis  | 表示毫秒 |
| seconds | 表示秒   |
| second  | 表示秒   |
| sec     | 表示秒   |
| s       | 表示秒   |
| minutes | 表示分钟 |
| minute  | 表示分钟 |
| min     | 表示分钟 |
| m       | 表示分钟 |
| hour    | 表示小时 |
| hou     | 表示小时 |
| h       | 表示小时 |



### stacktrace

打印最后一个异常错误信息，打印格式跟 `cn.org.expect.script.UniversalScriptFormatter` 的实现有关。 

#### 语法

```shell
$ stacktrace[;]
```



### date

日期命令

#### 语法

```shell
$ date [-d 日期字符串] { 日期输出后的格式表达式 } [ +|- 数字 day|month|year|hour|minute|second|millis ]* [;]
```

#### 选项

```shell
-d 设置日期字符串, 可以使用单引号或双引号包住日期字符串
```

#### 日期时间格式

```properties
y+.*MM.*dd  
 
yyyy-MM-dd, e.g: 2017-01-01 || 2017/01/01 || 2017.01.01 # 年月日之间的分隔符可以是以下字符之一: - / |  _ : ： . 。 
 
MM/dd/yyyy  
 
yyyy-M-d, e.g: 2017-1-1  
 
yyyyMMdd  
yyyyMMddHH  
yyyyMMddHHmm  
yyyyMMddHHmmss  
yyyyMMddHHmmssSSS  

yyyy-MM-dd hh  
yyyy-MM-dd hh:mm  
yyyy-MM-dd hh:mm:ss  
yyyy-MM-dd hh:mm:ss:SSS  

Sun Oct 11 00:00:00 GMT+08:00 1998  
Sun Oct 11 00:00:00:000 GMT+08:00 1998  

二零一七年十二月二十三  
1998年10月11日  
 
31 december 2017 at 08:38  
31 dec 2017  
```

#### 示例

```shell
$ date                        # 输出当前日期时间，显示格式：yyyy-MM-dd hh:mm:ss 
$ date -d 2020-01-01 yyyyMMdd # 格式化指定日期，-d参数值格式详见“支持的日期格式” 
$ date + 1 day                # 当前时间加一天
```



### default

当脚本引擎编译器不能识别脚本语句时，会使用提前设置的默认命令处理语句。

使用 `default sql;` 命令设置了 SQL 语句为默认命令后，当脚本引擎编译器遇到不能识别的命令语句时会将脚本语句交给默认命令解析并执行。

可以使用 `default` 命令查看脚本引擎当前设置的默认命令。

#### 语法

```shell
$ default [;]
$ default [sql | os] [;]
```

#### 示例

```shell
$ default sql; # 不能识别语句时默认作为 SQL 语句
$ default os;  # 不能识别语句时默认作为本地操作系统命令
```



### find

搜索文件

#### 语法

```shell
$ find -n string [-r] [-h] [-e charsetName] [-o logfilepath] [-s delimiter] [-d] [-p] filepath [;]
```

#### 选项

`-n` 选项：搜索内容（可以是正则表达式） 
`-R` 选项：只遍历当前目录
`-h` 选项：查找隐藏文件
`-e` 选项：被搜索文件的字符集
`-o` 选项：输出文件
`-s` 选项：输出信息的分隔符 
`-d` 选项：去掉重复记录 
`-p` 选项：显示字符串所在位置的详细信息 



### java

执行 **JAVA** 类，被执行的 **JAVA** 类需要继承 `cn.org.expect.script.command.AbstractJavaCommand`

#### 语法

```shell
$ java JavaClassName [参数]... [;] 
```

#### 示例

```shell
$ java cn.test.JavaCommandTest 10 -c 20200101
```



### wget

从网络下载文件

#### 语法

```shell
$ wget -PO: -n URL
```

`-P` 选项：文件保存的目录

`-O` 选项：文件保存的名字

`-n` 选项：只打印从服务器端得到的文件名

#### 示例

```shell
$ wget -P ${project.basedir}/.cache https://mirrors.huaweicloud.com/openjdk-25.tar.gz
```



### uuid

生成唯一 `32` 位字符串

#### 语法

```shell
$ uuid[;]
```



### md5

生成文件或字符内容的 `MD5` 值

#### 语法

```shell
$ md5sum 文件名或文件路径
$ md5sum 字符内容
```



### tar

压缩文件或目录

解压文件

#### 语法

```shell
$ tar -tzxcvfC 文件名或绝对路径
```

`-t` 选项：只打印不解压

`-z` 选项：gzip 解压/压缩文件

`-x` 选项：解压文件

`-c` 选项：压缩文件

`-v` 选项：打印解压缩文件日志

`-f` 选项：解压/压缩文件的绝对路径

`-C` 选项：文件解压到的目录

#### 示例

压缩文件: 

```shell
$ tar -zcvf 文件名或绝对路径
```

解压文件: 

```shell
$ tar -xvf 文件名或绝对路径
```



### gzip

压缩文件或目录

#### 语法

```shell
$ gzip 文件路径
```



### gunzip

解压文件

#### 语法

```shell
$ gunzip 文件路径
```



### zip

压缩文件或目录

#### 语法

```shell
$ zip [压缩文件路径] 被压缩文件路径
```

`-r` 选项：使用递归压缩目录

`-m` 选项：使用移动模式（压缩后删除源文件）

`-v` 选项：打印日志



### unrar

解压文件

#### 语法

```shell
$ unrar 压缩文件路径 [解压文件的目录]
```

`-v` 选项：打印日志



### unzip

解压文件

#### 语法

```shell
$ unzip 压缩文件路径
```

`-d` 选项：设置解压文件的目录



### help

打印帮助信息

#### 语法

```shell
$ help
```

#### 示例

```shell
$ help
```




# 变量方法

**变量方法**是指在脚本执行过程中，通过 **变量名.方法名()** 的方式，对变量值进行操作或获取信息的调用方式。

示例：

```javascript
set str = "12345 ";
set str1 = """12345n678n9n0""";
set str2 = str.trim();
echo "字符串内容是 $str2 .."
```

上述例子中，`str.trim()` 便是变量方法的调用，它调用了 `String` 类的 `trim()` 方法，对变量 `str` 的值去除首尾空白符。

## 调用说明

- 变量方法可以调用 **Java 类的公共方法**（`public` 方法），如 `substring()`、`length()` 等。
- 自定义类型变量也可以调用其对应 Java 类中的方法。
- 支持脚本扩展自定义变量方法，以满足特定业务需求。



## 内置变量

脚本引擎内置了一批系统级变量，可在脚本执行过程中直接访问：

| 变量标识 | 描述                       |
| -------- | -------------------------- |
| `this`    | 当前脚本引擎实例对象。     |
| `HOME`    | 操作系统当前用户的根目录。 |
| `TMPDIR`   | 系统临时文件目录路径。     |
| `scriptFile`   | 当前执行脚本的绝对路径。   |
| `PWD`   | 当前目录的绝对路径。       |
| `OLDPWD`   | 上一次所在的目录。         |
| `charset`   | 当前脚本文件的字符集编码。 |



## 自定义变量方法

脚本引擎支持 **两种方式** 扩展变量方法，满足个性化操作变量的需求。

### 接口方式

通过实现标准接口，定义变量方法的行为：

- 实现 `cn.org.expect.script.UniversalScriptVariableMethod` 接口，该接口约定变量方法的执行逻辑。
- 类上标注 `cn.org.expect.script.annotation.EasyVariableMethod` 注解，注册为变量方法。
- 在注解 `EasyVariableMethod` 的 `name` 属性指定变量方法名称，`variable` 属性指定方法适用的变量类型。

示例

实现一个数组打印方法：

```java
@EasyVariableMethod(name = "print", variable = Object[].class)
public class PrintMethod implements cn.org.expect.script.UniversalScriptVariableMethod {
    @Override
    public Object execute(UniversalScriptSession session, UniversalScriptContext context, UniversalScriptStdout stdout, UniversalScriptStderr stderr, UniversalScriptAnalysis analysis, Object variable, UniversalScriptVariableMethodParameters parameters) throws Exception {
        UniversalScriptFormatter formatter = context.getEngine().getFormatter();
        Object[] array = (Object[]) variable;
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            buf.append(formatter.format(array[i]));
            if (i < array.length - 1) {
                buf.append(' ');
            }
        }

        if (session.isEchoEnable()) {
            stdout.println(buf);
        }
        return buf.toString();
    }
}
```

核心接口解析：

| 接口或注解  | 描述                                   |
| ----------- | -------------------------------------- |
| `cn.org.expect.script.UniversalScriptVariableMethod`      | 变量方法执行接口，定义方法调用逻辑。   |
| `cn.org.expect.script.annotation.EasyVariableMethod`      | 标记自定义变量方法类，使引擎识别加载。 |
| `EasyVariableMethod.name()` | 描述变量方法的元信息：名称             |

### 静态方法方式

通过 **静态方法** 方式实现变量方法，更加简洁直观：

开发步骤：

- 在类上标注 `cn.org.expect.script.annotation.EasyVariableExtension` 注解，声明该类为变量方法提供者。
- 在类中定义静态方法：
  - 第一个参数：变量本身。
  - 后续参数：变量方法的调用参数。

示例

实现 JSON 数据操作方法：

```java
@EasyVariableExtension
public class JsonFunction {

    public static JSONObject getJSONObject(JSONObject jsonObject, String key) {
        return jsonObject.getJSONObject(key);
    }

    public static JSONObject getJSONObject(JSONArray jsonArray, int index) {
        return jsonArray.getJSONObject(index);
    }

    public static JSONObject getJSONObject(JSONArray jsonArray, long index) {
        return getJSONObject(jsonArray, (int) index);
    }
}
```



## 已注册变量方法

### Object[int]
返回数组中指定位置上的元素

**Variable**

`Object[]`


**Method**
```java
Object[int index]
```

**Implement Class**
```java
cn.org.expect.script.method.array.ElementMethod
```
### CharSequence[int]
返回字符串中指定位置上的字符

**Variable**

`CharSequence`


**Method**
```java
CharSequence[int index]
```

**Implement Class**
```java
cn.org.expect.script.method.string.CharAtMethod
```
### add(Object)
在集合中添加元素

**Variable**

`Collection` 集合


**Method**
```java
add(Object object)
```
**Parameter 1**

元素


**Return Value**

返回true表示成功，false表示失败

**Implement Class**
```java
cn.org.expect.script.method.CollectionExtension.add(java.util.Collection,java.lang.Object)
```
### booleanValue()
将字符串转为布尔值

**Variable**

`CharSequence` 字符串


**Method**
```java
booleanValue()
```

**Return Value**

布尔值

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.booleanValue(java.lang.CharSequence)
```
### charAt(int)
获取字符串中某个位置上的字符

**Variable**

`CharSequence` 字符串


**Method**
```java
charAt(int index)
```
**Parameter 1**

位置信息，从0开始


**Return Value**

字符

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.charAt(java.lang.CharSequence,int)
```
### check(int)
检查位置信息是否数组越界

**Variable**

`Object[]` 数组


**Method**
```java
check(int index)
```
**Parameter 1**

位置信息


**Return Value**

返回true表示未越界，false表示越界

**Implement Class**
```java
cn.org.expect.script.method.ArrayExtension.check(java.lang.Object[],int)
```
### currentTimeMillis()
返回当前时间戳

**Variable**

`UniversalScriptEngine` 脚本引擎


**Method**
```java
currentTimeMillis()
```

**Return Value**

时间戳

**Implement Class**
```java
cn.org.expect.script.method.ScriptEngineExtension.currentTimeMillis(cn.org.expect.script.UniversalScriptEngine)
```
### date()
将对象解析为日期时间

**Variable**

`Object` 对象（字符串、日期、时间、long、日历）


**Method**
```java
date()
```

**Return Value**

日期时间

**Implement Class**
```java
cn.org.expect.script.method.DateExtension.date(java.lang.Object)
```
### deleteFile()
删除文件

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
deleteFile()
```

**Return Value**

返回true表示成功，false表示失败

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.deleteFile(java.lang.CharSequence)
```
### evaluate(int, List)


**Variable**

`UniversalScriptEngine`


**Method**
```java
evaluate(int, List)
```

**Implement Class**
```java
cn.org.expect.script.method.ScriptEngineExtension.evaluate(cn.org.expect.script.UniversalScriptEngine,int,java.util.List<java.lang.String>) throws java.lang.Exception
```
### existsFile()
判断文件是否存在

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
existsFile()
```

**Return Value**

返回true表示存在，false表示不存在

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.existsFile(java.lang.CharSequence)
```
### filter(String...)


**Variable**

`List`


**Method**
```java
filter(String...)
```

**Implement Class**
```java
cn.org.expect.script.method.XmlExtension.filter(java.util.List<org.w3c.dom.Node>,java.lang.String...)
```
### forName(String)
根据类全名加载类

**Variable**

`UniversalScriptEngine`


**Method**
```java
forName(String)
```

**Implement Class**
```java
cn.org.expect.script.method.clazz.ForNameStrMethod
```
### forName()
根据类全名加载类

**Variable**

`CharSequence`


**Method**
```java
forName()
```

**Implement Class**
```java
cn.org.expect.script.method.clazz.ForNameMethod
```
### format(String)
按指定格式打印日期时间

**Variable**

`Object` 对象（字符串、日期、时间、long、日历）


**Method**
```java
format(String pattern)
```
**Parameter 1**

格式, 如: yyyy-MM-dd 详见: SimpleDateFormat


**Return Value**

字符串

**Implement Class**
```java
cn.org.expect.script.method.DateExtension.format(java.lang.Object,java.lang.String)
```
### get(int)
返回集合中某个位置上的元素

**Variable**

`List` 集合


**Method**
```java
get(int index)
```
**Parameter 1**

位置信息，从 0 开始


**Return Value**

集合元素

**Implement Class**
```java
cn.org.expect.script.method.CollectionExtension.get(java.util.List,int)
```
### get(String)
返回标签中的属性值

**Variable**

`Node` 标签


**Method**
```java
get(String name)
```
**Parameter 1**

属性名


**Return Value**

属性值

**Implement Class**
```java
cn.org.expect.script.method.XmlExtension.get(org.w3c.dom.Node,java.lang.String)
```
### getBean(String, String)
返回组件

**Variable**

`UniversalScriptEngine` 脚本引擎


**Method**
```java
getBean(String beanClassName, String name)
```
**Parameter 1**

组件类信息

**Parameter 2**

组件名


**Return Value**

组件

**Implement Class**
```java
cn.org.expect.script.method.ScriptEngineExtension.getBean(cn.org.expect.script.UniversalScriptEngine,java.lang.String,java.lang.String)
```
### getChildNodes(String...)


**Variable**

`Node`


**Method**
```java
getChildNodes(String...)
```

**Implement Class**
```java
cn.org.expect.script.method.XmlExtension.getChildNodes(org.w3c.dom.Node,java.lang.String...)
```
### getClass()
返回对象的类信息

**Variable**

`Object` 对象


**Method**
```java
getClass()
```

**Return Value**

类信息

**Implement Class**
```java
cn.org.expect.script.method.ClassExtension.getClass(java.lang.Object)
```
### getDay()
返回日期是月份中的第几天

**Variable**

`Object` 对象（字符串、日期、时间、long、日历）


**Method**
```java
getDay()
```

**Return Value**

月份中的第几天

**Implement Class**
```java
cn.org.expect.script.method.DateExtension.getDay(java.lang.Object)
```
### getDays()
返回日期从1970年开始的第几天

**Variable**

`Object` 对象（字符串、日期、时间、long、日历）


**Method**
```java
getDays()
```

**Return Value**

整数

**Implement Class**
```java
cn.org.expect.script.method.DateExtension.getDays(java.lang.Object)
```
### getEnum(String)
返回枚举常量

**Variable**

`Class` 枚举的类信息


**Method**
```java
getEnum(String name)
```
**Parameter 1**

枚举常量名


**Return Value**

枚举常量

**Implement Class**
```java
cn.org.expect.script.method.ClassExtension.getEnum(java.lang.Class<? extends java.lang.Enum>,java.lang.String)
```
### getFileExt()
返回文件扩展名

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
getFileExt()
```

**Return Value**

文件扩展名

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.getFileExt(java.lang.CharSequence)
```
### getFileLineSeparator()
返回文件的换行符

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
getFileLineSeparator()
```

**Return Value**

换行符

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.getFileLineSeparator(java.lang.CharSequence) throws java.io.IOException
```
### getFileSuffix()
返回文件名后缀

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
getFileSuffix()
```

**Return Value**

文件名后缀

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.getFileSuffix(java.lang.CharSequence) throws java.io.IOException
```
### getFilename()
返回文件名

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
getFilename()
```

**Return Value**

文件名

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.getFilename(java.lang.CharSequence) throws java.io.IOException
```
### getFilenameNoExt()
返回不含扩展名的文件名

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
getFilenameNoExt()
```

**Return Value**

文件名

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.getFilenameNoExt(java.lang.CharSequence) throws java.io.IOException
```
### getFilenameNoSuffix()
返回不含后缀的文件名

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
getFilenameNoSuffix()
```

**Return Value**

文件名

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.getFilenameNoSuffix(java.lang.CharSequence) throws java.io.IOException
```
### getHour()
返回小时

**Variable**

`Object` 对象（字符串、日期、时间、long、日历）


**Method**
```java
getHour()
```

**Return Value**

小时

**Implement Class**
```java
cn.org.expect.script.method.DateExtension.getHour(java.lang.Object)
```
### getJobService(int)
返回并发任务容器

**Variable**

`UniversalScriptEngine` 脚本引擎


**Method**
```java
getJobService(int number)
```
**Parameter 1**

容器并发数（同时运行任务的个数）


**Return Value**

并发任务容器

**Implement Class**
```java
cn.org.expect.script.method.ScriptEngineExtension.getJobService(cn.org.expect.script.UniversalScriptEngine,int)
```
### getMillis()
返回毫秒数

**Variable**

`Object` 对象（字符串、日期、时间、long、日历）


**Method**
```java
getMillis()
```

**Return Value**

毫秒数

**Implement Class**
```java
cn.org.expect.script.method.DateExtension.getMillis(java.lang.Object)
```
### getMinute()
返回分钟

**Variable**

`Object` 对象（字符串、日期、时间、long、日历）


**Method**
```java
getMinute()
```

**Return Value**

分钟

**Implement Class**
```java
cn.org.expect.script.method.DateExtension.getMinute(java.lang.Object)
```
### getMonth()
返回月份

**Variable**

`Object` 对象（字符串、日期、时间、long、日历）


**Method**
```java
getMonth()
```

**Return Value**

月份

**Implement Class**
```java
cn.org.expect.script.method.DateExtension.getMonth(java.lang.Object)
```
### getName()
返回标签名

**Variable**

`Node` 标签信息


**Method**
```java
getName()
```

**Return Value**

标签名

**Implement Class**
```java
cn.org.expect.script.method.XmlExtension.getName(org.w3c.dom.Node)
```
### getParent()
返回文件的目录

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
getParent()
```

**Return Value**

文件的上级目录

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.getParent(java.lang.CharSequence) throws java.io.IOException
```
### getProperty(String)
Settings.getProperty(key)

**Variable**

`UniversalScriptEngine`


**Method**
```java
getProperty(String)
```

**Implement Class**
```java
cn.org.expect.script.method.clazz.GetPropertyStrMethod
```
### getSecond()
返回秒钟

**Variable**

`Object` 对象（字符串、日期、时间、long、日历）


**Method**
```java
getSecond()
```

**Return Value**

秒钟

**Implement Class**
```java
cn.org.expect.script.method.DateExtension.getSecond(java.lang.Object)
```
### getText()
返回标签内容

**Variable**

`Node` 标签信息


**Method**
```java
getText()
```

**Return Value**

标签中的字符串

**Implement Class**
```java
cn.org.expect.script.method.XmlExtension.getText(org.w3c.dom.Node)
```
### getTime()
返回日期从1970年开始的毫秒

**Variable**

`Object` 对象（字符串、日期、时间、long、日历）


**Method**
```java
getTime()
```

**Return Value**

毫秒

**Implement Class**
```java
cn.org.expect.script.method.DateExtension.getTime(java.lang.Object)
```
### getYear()
返回年份

**Variable**

`Object` 对象（字符串、日期、时间、long、日历）


**Method**
```java
getYear()
```

**Return Value**

年份

**Implement Class**
```java
cn.org.expect.script.method.DateExtension.getYear(java.lang.Object)
```
### help()
打印所有变量方法

**Variable**

`Object`


**Method**
```java
help()
```

**Implement Class**
```java
cn.org.expect.script.method.object.HelpMethod
```
### indexOf(String)
在数组中搜索

**Variable**

`Object[]`


**Method**
```java
indexOf(String)
```

**Implement Class**
```java
cn.org.expect.script.method.array.IndexOfMethod
```
### indexOf(String, int)
在字符串中搜索

**Variable**

`Object[]`


**Method**
```java
indexOf(String, int)
```

**Implement Class**
```java
cn.org.expect.script.method.array.IndexOfStrIntMethod
```
### indexOf(CharSequence)
在字符串中搜索

**Variable**

`CharSequence` 字符串


**Method**
```java
indexOf(CharSequence dest)
```
**Parameter 1**

搜索的内容


**Return Value**

位置信息，从0开始，-1表示未搜索到

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.indexOf(java.lang.CharSequence,java.lang.CharSequence)
```
### indexOf(CharSequence, int)
在字符串中搜索

**Variable**

`CharSequence` 字符串


**Method**
```java
indexOf(CharSequence dest, int from)
```
**Parameter 1**

搜索的内容

**Parameter 2**

搜索起始位置，从 0 开始


**Return Value**

位置信息，从0开始，-1表示未搜索到

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.indexOf(java.lang.CharSequence,java.lang.CharSequence,int)
```
### indexOf(CharSequence, int, boolean)
在字符串中搜索

**Variable**

`CharSequence` 字符串


**Method**
```java
indexOf(CharSequence dest, int from, boolean ignoreCase)
```
**Parameter 1**

搜索的内容

**Parameter 2**

搜索起始位置，从 0 开始

**Parameter 3**

true表示忽略大小写


**Return Value**

位置信息，从0开始，-1表示未搜索到

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.indexOf(java.lang.CharSequence,java.lang.CharSequence,int,boolean)
```
### int()
将对象转为整数

**Variable**

`Object`


**Method**
```java
int()
```

**Implement Class**
```java
cn.org.expect.script.method.object.IntMethod
```
### isBlank()
判断字符串是否是空白

**Variable**

`CharSequence` 字符串


**Method**
```java
isBlank()
```

**Return Value**

返回true表示空白，false表示不是空白

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.isBlank(java.lang.CharSequence)
```
### isDirectory()
判读文件路径是否是目录

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
isDirectory()
```

**Return Value**

返回true表示是目录，false表示不是目录

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.isDirectory(java.lang.CharSequence) throws java.io.IOException
```
### isFile()
判断文件路径是否是一个文件

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
isFile()
```

**Return Value**

返回true表示是文件，false表示不是文件

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.isFile(java.lang.CharSequence) throws java.io.IOException
```
### joinPath(String...)


**Variable**

`String`


**Method**
```java
joinPath(String...)
```

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.joinPath(java.lang.String,java.lang.String...)
```
### length()
返回数组长度

**Variable**

`Object[]` 数组


**Method**
```java
length()
```

**Return Value**

长度

**Implement Class**
```java
cn.org.expect.script.method.ArrayExtension.length(java.lang.Object[])
```
### length()
返回字符串长度

**Variable**

`CharSequence` 字符串


**Method**
```java
length()
```

**Return Value**

长度

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.length(java.lang.CharSequence)
```
### loadProperties()
加载属性集合

**Variable**

`InputStream` 属性集合输入流


**Method**
```java
loadProperties()
```

**Return Value**

属性集合

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.loadProperties(java.io.InputStream)
```
### loadProperties(String, String)
加载 Properties 文件中的属性

**Variable**

`ClassLoader` 类加载器


**Method**
```java
loadProperties(String name, String envPropertyName)
```
**Parameter 1**

资源文件位置

**Parameter 2**

分环境资源文件


**Return Value**

属性集合

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.loadProperties(java.lang.ClassLoader,java.lang.String,java.lang.String)
```
### lower()
将字符串转为小写

**Variable**

`CharSequence` 字符串


**Method**
```java
lower()
```

**Return Value**

字符串

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.lower(java.lang.CharSequence)
```
### ls()
显示目录中的文件

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
ls()
```

**Return Value**

目录中文件

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.ls(java.lang.CharSequence) throws java.io.IOException
```
### ltrim()
删除数组中字符串左侧的空白字符

**Variable**

`Object[]` 数组


**Method**
```java
ltrim()
```

**Return Value**

新数组

**Implement Class**
```java
cn.org.expect.script.method.ArrayExtension.ltrim(java.lang.Object[])
```
### ltrim(String)
删除数组中字符串左侧的空白字符与指定字符

**Variable**

`Object[]` 数组


**Method**
```java
ltrim(String chars)
```
**Parameter 1**

待删除的字符


**Return Value**

新数组

**Implement Class**
```java
cn.org.expect.script.method.ArrayExtension.ltrim(java.lang.Object[],java.lang.String)
```
### ltrim()
删除字符串左侧的空白字符

**Variable**

`CharSequence` 字符串


**Method**
```java
ltrim()
```

**Return Value**

字符串

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.ltrim(java.lang.CharSequence)
```
### ltrim(String)
删除字符串左侧的空白字符与字符参数

**Variable**

`CharSequence` 字符串


**Method**
```java
ltrim(String chars)
```
**Parameter 1**

字符参数


**Return Value**

字符串

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.ltrim(java.lang.CharSequence,java.lang.String)
```
### mkdir()
创建目录

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
mkdir()
```

**Return Value**

返回true表示成功，false表示失败

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.mkdir(java.lang.CharSequence) throws java.io.IOException
```
### newDocument()
将 xml 解析为 Document 对象

**Variable**

`String` 字符串


**Method**
```java
newDocument()
```

**Return Value**

Document 对象

**Implement Class**
```java
cn.org.expect.script.method.XmlExtension.newDocument(java.lang.String)
```
### newInstance(Object...)
创建类的实例对象

**Variable**

`Class`


**Method**
```java
newInstance(Object...)
```

**Implement Class**
```java
cn.org.expect.script.method.clazz.NewInstanceMethod
```
### print()
打印数组

**Variable**

`Object[]`


**Method**
```java
print()
```

**Implement Class**
```java
cn.org.expect.script.method.array.PrintMethod
```
### print()
打印对象

**Variable**

`Object`


**Method**
```java
print()
```

**Implement Class**
```java
cn.org.expect.script.method.object.PrintMethod
```
### read(String)
读取文件内容

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
read(String charsetName)
```
**Parameter 1**

文件的字符集编码


**Return Value**

文件内容

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.read(java.lang.CharSequence,java.lang.String) throws java.io.IOException
```
### readTag(String, int)
读取 xml 中指定标签

**Variable**

`String` 字符串


**Method**
```java
readTag(String tagName, int from)
```
**Parameter 1**

标签名

**Parameter 2**

起始位置，从 0 开始


**Return Value**

标签内容

**Implement Class**
```java
cn.org.expect.script.method.XmlExtension.readTag(java.lang.String,java.lang.String,int)
```
### removePrefix(CharSequence)
从字符串 str 的最左段移除字符串 prefix

**Variable**

`CharSequence` 字符串


**Method**
```java
removePrefix(CharSequence prefix)
```
**Parameter 1**

字符串


**Return Value**

移除后的字符串

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.removePrefix(java.lang.CharSequence,java.lang.CharSequence)
```
### replace(String, String)
替换字符串中的内容

**Variable**

`CharSequence` 字符串


**Method**
```java
replace(String oldStr, String newStr)
```
**Parameter 1**

替换的字符串

**Parameter 2**

替换后的字符串


**Return Value**

字符串

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.replace(java.lang.CharSequence,java.lang.String,java.lang.String)
```
### replaceFolderSeparator()
把文件路径参数 filepath 中的 '/' 和 '\' 字符替换成当前操作系统的路径分隔符

**Variable**

`String` 文件路径


**Method**
```java
replaceFolderSeparator()
```

**Return Value**

文件路径

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.replaceFolderSeparator(java.lang.String)
```
### rtrim()
删除数组中字符串右侧的空白字符

**Variable**

`Object[]` 数组


**Method**
```java
rtrim()
```

**Return Value**

新数组

**Implement Class**
```java
cn.org.expect.script.method.ArrayExtension.rtrim(java.lang.Object[])
```
### rtrim(String)
删除数组中字符串右侧的空白字符与指定字符

**Variable**

`Object[]` 数组


**Method**
```java
rtrim(String chars)
```
**Parameter 1**

待删除的字符


**Return Value**

新数组

**Implement Class**
```java
cn.org.expect.script.method.ArrayExtension.rtrim(java.lang.Object[],java.lang.String)
```
### rtrim()
删除字符串右侧的空白字符

**Variable**

`CharSequence` 字符串


**Method**
```java
rtrim()
```

**Return Value**

字符串

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.rtrim(java.lang.CharSequence)
```
### rtrim(String)
删除字符串右侧的空白字符与字符参数

**Variable**

`CharSequence` 字符串


**Method**
```java
rtrim(String chars)
```
**Parameter 1**

字符参数


**Return Value**

字符串

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.rtrim(java.lang.CharSequence,java.lang.String)
```
### sendHttp(CharSequence, Object...)


**Variable**

`UniversalScriptEngine`


**Method**
```java
sendHttp(CharSequence, Object...)
```

**Implement Class**
```java
cn.org.expect.script.method.HttpExtension.sendHttp(cn.org.expect.script.UniversalScriptEngine,java.lang.CharSequence,java.lang.Object...) throws java.net.UnknownHostException
```
### size()
返回集合长度

**Variable**

`Collection` 集合


**Method**
```java
size()
```

**Return Value**

集合长度

**Implement Class**
```java
cn.org.expect.script.method.CollectionExtension.size(java.util.Collection)
```
### size()
返回集合长度

**Variable**

`Map` 集合


**Method**
```java
size()
```

**Return Value**

集合长度

**Implement Class**
```java
cn.org.expect.script.method.CollectionExtension.size(java.util.Map)
```
### split()
将字符串使用空白字符分隔

**Variable**

`CharSequence` 字符串


**Method**
```java
split()
```

**Return Value**

字段集合

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.split(java.lang.CharSequence)
```
### split(String)
将字符串使用指定字符分隔

**Variable**

`CharSequence` 字符串


**Method**
```java
split(String delimiter)
```
**Parameter 1**

分隔字符


**Return Value**

字段集合

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.split(java.lang.CharSequence,java.lang.String)
```
### split(String, String)
使用分隔符、转义字符提取字符串中的字段

**Variable**

`CharSequence`


**Method**
```java
split(String, String)
```

**Implement Class**
```java
cn.org.expect.script.method.string.SplitMethod
```
### startsWith(CharSequence)
判断字符串是否以指定前缀开头

**Variable**

`CharSequence` 字符串


**Method**
```java
startsWith(CharSequence prefix)
```
**Parameter 1**

前缀


**Return Value**

返回true表示是，false表示否

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.startsWith(java.lang.CharSequence,java.lang.CharSequence)
```
### startsWith(CharSequence, int)
判断字符串是否以指定前缀开头

**Variable**

`CharSequence` 字符串


**Method**
```java
startsWith(CharSequence prefix, int from)
```
**Parameter 1**

前缀

**Parameter 2**

起始位置，从 0 开始


**Return Value**

返回true表示是，false表示否

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.startsWith(java.lang.CharSequence,java.lang.CharSequence,int)
```
### startsWith(CharSequence, int, boolean)
判断字符串是否以指定前缀开头

**Variable**

`CharSequence` 字符串


**Method**
```java
startsWith(CharSequence prefix, int from, boolean ignoreCase)
```
**Parameter 1**

前缀

**Parameter 2**

起始位置，从 0 开始

**Parameter 3**

true表示忽略大小写


**Return Value**

返回true表示是，false表示否

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.startsWith(java.lang.CharSequence,java.lang.CharSequence,int,boolean)
```
### startsWith(CharSequence, int, boolean, boolean)
判断字符串是否以指定前缀开头

**Variable**

`CharSequence` 字符串


**Method**
```java
startsWith(CharSequence prefix, int from, boolean ignoreCase, boolean ignoreBlank)
```
**Parameter 1**

前缀

**Parameter 2**

起始位置，从 0 开始

**Parameter 3**

true表示忽略大小写

**Parameter 4**

false表示忽略空白字符


**Return Value**

返回true表示是，false表示否

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.startsWith(java.lang.CharSequence,java.lang.CharSequence,int,boolean,boolean)
```
### subArray(int)
截取数组

**Variable**

`Object[]` 数组


**Method**
```java
subArray(int begin)
```
**Parameter 1**

截取数组的长度


**Return Value**

新数组

**Implement Class**
```java
cn.org.expect.script.method.ArrayExtension.subArray(java.lang.Object[],int)
```
### subArray(int, int)
截取数组

**Variable**

`Object[]` 数组


**Method**
```java
subArray(int begin, int end)
```
**Parameter 1**

起始位置，从 0 开始

**Parameter 2**

结束位置（不包括）


**Return Value**

新数组

**Implement Class**
```java
cn.org.expect.script.method.ArrayExtension.subArray(java.lang.Object[],int,int)
```
### substr(int)
截取字符串

**Variable**

`CharSequence` 字符串


**Method**
```java
substr(int begin)
```
**Parameter 1**

截取起始位置，从 0 开始


**Return Value**

字符串

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.substr(java.lang.CharSequence,int)
```
### substr(int, int)
截取字符串

**Variable**

`CharSequence` 字符串


**Method**
```java
substr(int begin, int end)
```
**Parameter 1**

截取起始位置，从 0 开始

**Parameter 2**

结束位置（不包括）


**Return Value**

字符串

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.substr(java.lang.CharSequence,int,int)
```
### touch()
创建文件

**Variable**

`CharSequence` 文件绝对路径


**Method**
```java
touch()
```

**Return Value**

返回true表示成功，false表示失败

**Implement Class**
```java
cn.org.expect.script.method.FileExtension.touch(java.lang.CharSequence) throws java.io.IOException
```
### trim()
删除数组中字符串二端的空白字符

**Variable**

`Object[]` 数组


**Method**
```java
trim()
```

**Return Value**

新数组

**Implement Class**
```java
cn.org.expect.script.method.ArrayExtension.trim(java.lang.Object[])
```
### trim(String)
删除数组中字符串二端的空白字符与指定字符

**Variable**

`Object[]` 数组


**Method**
```java
trim(String chars)
```
**Parameter 1**

待删除的字符


**Return Value**

新数组

**Implement Class**
```java
cn.org.expect.script.method.ArrayExtension.trim(java.lang.Object[],java.lang.String)
```
### trim()
删除字符串二端的空白字符

**Variable**

`CharSequence` 字符串


**Method**
```java
trim()
```

**Return Value**

字符串

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.trim(java.lang.CharSequence)
```
### trim(String)
删除字符串二端的空白字符与字符参数

**Variable**

`CharSequence` 字符串


**Method**
```java
trim(String chars)
```
**Parameter 1**

字符参数


**Return Value**

字符串

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.trim(java.lang.CharSequence,java.lang.String)
```
### upper()
将字符串转为大写

**Variable**

`CharSequence` 字符串


**Method**
```java
upper()
```

**Return Value**

字符串

**Implement Class**
```java
cn.org.expect.script.method.StringExtension.upper(java.lang.CharSequence)
```




# 内部设计



##  Spring Boot 场景启动器

脚本引擎可作为 **Spring Boot Starter** 自动装配模块集成至项目中，随着 **Spring Boot 应用上下文** 启动自动初始化，引擎实例可通过依赖注入获取，进一步简化创建与使用流程。



### 属性

在编写脚本时，支持使用 **Spring Boot 配置文件**（如 `application.properties` 和 `application.yaml`）中设置的属性。



### 线程池复用机制

为避免脚本执行过程中线程资源的无序扩张，脚本引擎支持与 **Spring 容器** 线程池资源共享：

脚本引擎复用 **Spring** 容器线程池的机制：

- 优先查找 **Spring 容器** 中名称为 `taskExecutor` 的线程池；
- 若未找到，查找类型为 `ThreadPoolTaskExecutor` 的线程池实例；
- 若仍未找到，则尝试获取实现了 `ExecutorService` 接口的线程池实例；



### 启动流程与 Bean 生命周期管理

在 **Spring Boot 项目启动阶段**，脚本引擎容器与实例的初始化流程如下：

应用启动时，初始化 **脚本引擎容器组件**，负责维护脚本运行环境及组件信息；

初始化完成后，将该 **容器组件** 交由 **Spring 容器** 进行统一管理；

开发者可通过 **Spring 依赖注入机制** 获取以下两个核心对象：

脚本引擎容器 `cn.org.expect.ioc.EasyContext`：单例作用域（`@Scope("singleton")`），负责全局脚本环境管理；

脚本引擎实例 `cn.org.expect.script.UniversalScriptEngine`：请求作用域（`@Scope("request")`），每次请求生成一个新的引擎实例。



## 脚本引擎容器

脚本引擎容器类似于 **Spring** 容器的概念，用来管理组件。

脚本引擎启动前，需要先启动一个脚本引擎容器类 `cn.org.expect.ioc.EasyContext` 的实例对象。

脚本引擎容器包含：类扫描器、组件信息表、组件工厂、容器等。



### 类扫描器

当脚本引擎容器启动时，会使用类扫描器扫描指定 **Java** 包中的类，将扫描到的类存储到**组件信息表**中。

可通过参数指定扫描规则：

```java
System.setProperty("cn.org.expect.scan", "cn.org.expect,!org.apache"); // 包名前面使用叹号，表示排除该包名下的所有类
```

可在创建 `cn.org.expect.ioc.DefaultEasyContext` 类的实例对象时，通过参数设置扫描规则：

```java
cn.org.expect.ioc.DefaultEasyContext ioc = new cn.org.expect.ioc.DefaultEasyContext(
        "sout+:info", // 默认日志级别
        "cn.org.expect.io:debug", //
        "cn.org.expect.ioc:trace" //
);
```

类扫描器的实现类是 `cn.org.expect.ioc.EasyClassScanner`，扫描规则详见 `EasyClassScan` 接口的实现类。

类扫描器默认只扫描被注解 `@EasyBean`、`@EasyCommandCompiler`、`@EasyVariableMethod` 标记的类，如果想增加扫描规则，则可以在**SPI**配置文件 `resources/META-INF/services/cn.org.expect.ioc.EasyClassScan` 中增加 `EasyClassScan` 接口的实现类。



### 组件信息

组件信息接口是：`cn.org.expect.ioc.EasyBeanEntry`

类扫描器扫描到一个被注解 `cn.org.expect.ioc.annotation.EasyBean` 标记的类后，会将该类信息转为接口 `cn.org.expect.ioc.EasyBeanEntry` 的实例对象。

随后容器会对所有必要的组件进行初始化操作，然后等待其他功能调用组件。



### 组件信息表

组件信息表类是：`cn.org.expect.ioc.internal.BeanRepository`

用于存储容器中所有组件，存储格式是组件类上的接口类信息与组件类信息的映射。

从脚本引擎容器中查找指定组件时，会根据接口或类信息在组件信息表中查找对应的组件。



### 组件工厂

组件工厂接口是：`cn.org.expect.ioc.EasyBeanFactory`

如果有组件需要使用工厂模式创建，则可以在组件工厂接口 `cn.org.expect.ioc.EasyBeanFactory` 实现类上并标注注解 `EasyBean`。

脚本引擎容器会使用这个组件工厂实例来创建该组件。



## 脚本引擎工厂 

脚本引擎工厂接口是 `cn.org.expect.script.UniversalScriptEngineFactory`，用于创建脚本引擎实对象。



## 脚本引擎配置信息

脚本引擎配置信息接口是  `cn.org.expect.script.UniversalScriptConfiguration`，用于管理脚本引擎基本属性信息。



## 脚本引擎

脚本引擎类是 `cn.org.expect.script.UniversalScriptEngine`，是所有脚本语句的运行器。



## 脚本引擎上下文信息 

脚本引擎上下文信息类是 `cn.org.expect.script.UniversalScriptContext`，用于管理脚本引擎运行中产生的变量与程序。



## 编译脚本

脚本引擎编译器接口是 `cn.org.expect.script.UniversalScriptCompiler`，可以将脚本语句编译为可以执行的脚本命令。

### 编译器组成

语法分析器 `cn.org.expect.script.UniversalScriptParser`

语句输入流 `cn.org.expect.script.UniversalScriptReader`

语句分析器 `cn.org.expect.script.UniversalScriptAnalysis` 

命令编译器 `cn.org.expect.script.UniversalCommandCompiler` 

### 编译过程

在类扫描阶段，如果类配置了 `cn.org.expect.script.annotation.EasyCommandCompiler` 注解，并且该类也实现了接口 `cn.org.expect.script.UniversalCommandCompiler`，则将这个（命令编译器）类实例化。

将上一步生成的命令编译器的实例对象，交给脚本引擎编译器管理和使用。

脚本引擎编译器对某一个脚本语句进行编译时，会逐个执行命令编译器实例上的 `match(UniversalScriptAnalysis, String, String)` 方法，来判断脚本语句应该使用哪个命令编译器来执行编译操作。

找出脚本语句对应的命令编译器后，会执行该命令编译器上的 `read(UniversalScriptReader, UniversalScriptAnalysis)` 方法来读取一个完整的语句。

再执行命令编译器上的 `compile(UniversalScriptSession, UniversalScriptContext, UniversalScriptParser, UniversalScriptAnalysis, String)` 方法，对上一步得到的脚本语句进行编译，得到一个脚本命令实例。



## 运行命令

经过脚本引擎编译器编译后，会得到一个脚本命令对象，即 `cn.org.expect.script.UniversalScriptCommand` 接口的实例对象。

运行实例上的 `execute(UniversalScriptSession, UniversalScriptContext, UniversalScriptStdout, UniversalScriptStderr, boolean)` 方法（即执行该命令的业务逻辑），会得到一个返回值与状态码。

根据上一步得到的状态码，判断该命令运行的是否成功。

如果状态码是零，表示命令运行成功，读取下一个命令并运行。

如果状态码是非零，表示命令运行错误，立即抛出异常（可用 `set -E` 命令来设置不抛出异常）。



## 国际化信息

脚本引擎加载国际化资源文件的顺序如下：

### 约定资源文件

脚本引擎默认先加载中文国际化资源文件：`cn/org/expect/Messages.properties`

你可以通过 JVM 参数设置加载英文资源文件：`cn/org/expect/Messages_en_US.properties`

```java
java -Dcn.org.expect.resource.locale=en_US cn.org.expect.Modest
```

### 自定义资源文件

如果已在工程中使用了自定义资源文件 `com.test.Message_en_US.properties`，则可通过 JVM 参数配置自定义的资源名：

```shell
java -Dcn.org.expect.resource.locale=en_US -Dcn.org.expect.resource.name=com/test/Messages cn.org.expect.Modest
```

### 外部资源文件

可以通过 JVM 参数设置外部的国际化资源文件路径：

```shell
java -Dcn.org.expect.resource=/home/user/project/resources/Messages.properties cn.org.expect.Modest
```

### 工具类

国际化资源工具类是 `cn.org.expect.util.ResourcesUtils`，通过该类可以方便地获取国际化资源文件中的配置项。

例如：

```java
ResourcesUtils.getMessage("date.stdout.message003")
```



## 表达式支持

支持在 `set`，`if`，`while` 等命令中使用表达式进行计算。

开发人员可以使用类 `cn.org.expect.expression.Expression` 完成如下表达式运算：

算数运算 `() +`(正)`-`(负) `*`(乘) `/`(除) `%`(取余) `+`(加) `-`(减) 

三目运算 `?:`

布尔运算 `< <= > >= == !=`

逻辑运算 `&& || and or`

范围运算 `in` 与`not in` 运算符的返回值是布尔值，判断变量是否在设置的范围内，操作符右侧是小括号，小括号内的元素用符号  分割。

取反运算 `!` 只支持对布尔值进行取反



## 日志输出

脚本引擎容器启动时，会检查类路径下是否有 **Slf4j** 日志组件。

如果在类路径中检测到 **Slf4j** 相关jar包，则直接使用 **Slf4j** 作为日志输出接口。

如果在类路径中未检测到 **Slf4j** 相关jar包，则默认使用控制台 `System.out` 输出日志。

当使用控制台输出日志时，可配置如下参数设置日志的输出级别与输出格式：

```java
System.setProperty("cn.org.expect.log", "debug"); // 设置日志输出级别
System.setProperty("cn.org.expect.log.sout", "%d|%-5.5p|%30.30c|%50.50l|%m%ex%n"); // 设置用控制台输出日志，且指定了日志的输出格式
```

设置打印日志堆栈跟踪信息（用于确定 `fqcn` 值）：

```java
System.setProperty("cn.org.expect.stacktrace.print", "true");
```

设置打印数据库操作日志（可以显示执行的类名、方法名、参数值、返回值）：

```java
System.setProperty("cn.org.expect.db.log", "true");
```



## 字符集

脚本引擎内部默认使用的字符集编码是 `file.encoding` 属性值，也可通过如下参数修改默认的字符集：

```java
System.setProperty("cn.org.expect.charset", "UTF-8");
```

可以通过脚本命令来设置脚本文件、外部文件（如 `tail`、`head` 等命令的输入文件）的字符集：

```shell
set charset=GBK
. /home/user/script.etl
```



## 临时文件

脚本引擎产生的临时文件，默认存储在 `${java.io.tmpdir}/cn/org/expect` 路径下

可通过参数修改临时文件存储的默认目录：

```java
System.setProperty("cn.org.expect.tempDir", "/home/user/temp/");
```

在编程时可通过如下代码，得到临时文件的存储目录：

```java
FileUtils.getTempDir(String[]);
```



## 数据库方言

因为不同品牌数据库（或同品牌不同版本），其语法与功能实现各不相同，可通过数据库方言接口 `cn.org.expect.database.DatabaseDialect` 来统一操作数据库的接口。但是需要为不同品牌（或同品牌的不同版本）的数据库开发不同的方言实现类。

在脚本引擎中数据库相关的命令，都是通过 **JDBC** 接口实现的。

在使用数据库相关命令前，需要先将数据库的 **JDBC** 驱动包加入到 **classpath** 下。



### 已有方言类

已注册的数据库方言如下所示：

|  Database  | Description |                   Database Dialect Class Name |
| ---------- | ----------- | --------------------------------------------- |
|  **db2**   |             |       `cn.org.expect.database.db2.DB2Dialect` |
|  **db2**   | DB2 11.5    |   `cn.org.expect.database.db2.DB2Dialect11_5` |
|   **h2**   |             |         `cn.org.expect.database.h2.H2Dialect` |
| **mysql**  |             |   `cn.org.expect.database.mysql.MysqlDialect` |
| **oracle** |             | `cn.org.expect.database.oracle.OracleDialect` |




### 开发方言类

可以通过自定义数据库方言的方式来增加对其他品牌据库的支持。

例如：想要增加对 `informix` 数据库的支持，如下所示需要新建并实现数据库方言类，且在该类上配置注解 `cn.org.expect.ioc.annotation.EasyBean` 。

```java
@EasyBean(name = "informix")
public class InformixDialect extends cn.org.expect.database.internal.AbstractDialect implements cn.org.expect.database.DatabaseDialect {
	...
}
```

可以针对同一数据库的不同版本，开发对应的数据库方言类。

因为在同一个品牌数据库的不同版本中，同一个功能的实现也可能不同，这时可针对数据库的特殊版本增加方言类，如下所示：

```java
@EasyBean(name = "db2")
public class DB2Dialect115 extends DB2Dialect implements DatabaseDialect {

    /**
     * 大版本号，对应 DatabaseMetaData.getDatabaseMajorVersion() 的返回值
     */
    public String getDatabaseMajorVersion() {
        return "11";
    }

    /**
     * 小版本号，对应 DatabaseMetaData.getDatabaseMinorVersion() 的返回值
     */
    public String getDatabaseMinorVersion() {
        return "5";
    }
}
```



### 使用方言类

在编写代码时，可通过容器的 `cn.org.expect.ioc.EasyContext.getBean(Class, Object[])` 方法得到数据库方言的实例对象，如下所示：

```java
@EasyBean
public class JdbcTest1 {

    /** 容器上下文信息 */
    private EasyContext context;
  
    /** 数据库连接 */
    private Connection conn;

    /**
     * 初始化
     *
     * @param context   容器上下文信息
     * @param conn      数据库连接
     */
    public JdbcTest1(EasyContext context, Connection conn) {
        this.context = context;
        this.conn = conn;
    }

    /**
     * 返回数据库方言
     *
     * @return 数据库方言
     */
    public DatabaseDialect getDialect() {
        return this.context.getBean(DatabaseDialect.class, this.conn);
    }
}
```

在上面这个案例中，容器方法 `cn.org.expect.ioc.EasyContext.getBean(Class, Object[])` 的第一个参数是数据库方言接口，第二个参数是一个有效的数据库连接。

容器会根据数据库连接信息中的数据库缩写与版本号，查找对应的数据库方言。

```java
@EasyBean
public class JdbcTest2 {

    /** 容器上下文信息 */
    @EasyBean
    private EasyContext context;

    /** JDBC URL */
    private String url = "jdbc:db2://127.0.0.1:50000/sample";

    /**
     * 初始化
     */
    public JdbcTest2() {
    }

    /**
     * 返回数据库方言
     *
     * @return 数据库方言
     */
    public DatabaseDialect getDialect() {
        return this.context.getBean(DatabaseDialect.class, this.url);
    }
}
```

在上面这个案例中，容器自动注入了容器上下文信息，即 `context` 实例。

容器方法 `cn.org.expect.ioc.EasyContext.getBean(Class, Object[])` 的第一个参数是数据库方言接口，第二个参数是 **JDBC** 的 **URL**，容器会根据URL中的数据库信息，查找（规则详见 `cn.org.expect.database.internal.DatabaseDialectFactory`）对应的数据库方言。



### 匹配规则

先根据数据库简称，遍历已注册数据库方言类上 `@EasyBean` 注解的 `name()	` 值，查找匹配的方言类。

如果在上一步查找匹配到多个数据库方言类（存在不同版本的方言类），则优先使用大版本号、小版本号与数据库进行匹配；

如果在上一步中，不能匹配到对应版本号的方言类时，则会使用未设置版本号（即`DatabaseDialect.getDatabaseMajorVersion()`与`DatabaseDialect.getDatabaseMinorVersion()`方法返回 null 或空字符）的数据库方言。

如果在上一步中，所有数据库方言类都设置了版本号，则优先返回版本号最接近的方言类。

具体的匹配规则详见类：`cn.org.expect.database.internal.DatabaseDialectFactory`



## 输出接口

- 标准信息输出接口；
- 错误信息输出接口；
- 进度信息输出接口；



## 类型转换器

类型转换器  `cn.org.expect.script.UniversalScriptFormatter`，用于将 JDBC 查询结果集返回值转为脚本引擎内部使用的类型。



## 国家法定假日

判断日期是否为中国法定假日

```java
Dates.isRestDay("zh_CN", Dates.parse("2019-08-30"));
```

判断日期是否为中国法定工作日

```java
Dates.isWorkDay("zh_CN", Dates.parse("2019-08-31"));
```

您可以通过自定义 XML 配置文件来扩展国家法定假日。例如：

```xml
<?xml version="1.0" encoding="utf-8"?>
<holidays>
    <locale name="zh_CN" description="2100中国法定假日">
        <date value="2100-01-01" name="元旦" reset="true"/>
    </locale>
</holidays>
```

自定义的国家法定假日 XML 配置文件可以放置在以下三个位置：

- 将 XML 文件存储到 Java 工程 `cn/org/expect` 包中，文件名必须为：`holidays.xml`

- 将 XML 文件存储到 `/Users/user/.modest/config`，文件名必须遵循命名规则：`holiday*.xml`

- 通过 JVM 参数指定 XML 文件所在目录，文件名必须遵循命名规则：`holiday*.xml`

  ```shell
  java -Dcn.org.expect.holiday=/home/user/locale/ cn.org.expect.Modest
  ```

完成 XML 配置后，重启服务或通过以下代码加载新的国家法定假日配置文件：

```java
Dates.HOLIDAYS.reload();
```



## 其他配置

输入流缓冲区字符数组的长度：

```java
System.setProperty("cn.org.expect.io.buffer.byteArrayLength", "10000");
```

输入流缓冲区字节数组的长度：

```java
System.setProperty("cn.org.expect.io.buffer.charArrayLength", "10000");
```

设置 **Linux** 操作系统内置账户名（用于过滤操作系统内置账户信息）：

```java
System.setProperty("cn.org.expect.linux.builtin.accounts", "daemon,apache");
```



## 组件附录

脚本引擎容器中已注册的组件如下所示：



### AbstractCommandCompiler
| Component Class Name                                             | Description |
| ---------------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.BreakCommandCompiler`              |             |
| `cn.org.expect.script.command.ByeCommandCompiler`                |             |
| `cn.org.expect.script.command.CallProcudureCommandCompiler`      |             |
| `cn.org.expect.script.command.CallbackCommandCompiler`           |             |
| `cn.org.expect.script.command.CatCommandCompiler`                |             |
| `cn.org.expect.script.command.CdCommandCompiler`                 |             |
| `cn.org.expect.script.command.CommitCommandCompiler`             |             |
| `cn.org.expect.script.command.ContainerCommandCompiler`          |             |
| `cn.org.expect.script.command.ContinueCommandCompiler`           |             |
| `cn.org.expect.script.command.CpCommandCompiler`                 |             |
| `cn.org.expect.script.command.CursorCommandCompiler`             |             |
| `cn.org.expect.script.command.DBConnectCommandCompiler`          |             |
| `cn.org.expect.script.command.DBExportCommandCompiler`           |             |
| `cn.org.expect.script.command.DBGetCfgForCommandCompiler`        |             |
| `cn.org.expect.script.command.DBLoadCommandCompiler`             |             |
| `cn.org.expect.script.command.DDLCommandCompiler`                |             |
| `cn.org.expect.script.command.DaemonCommandCompiler`             |             |
| `cn.org.expect.script.command.DateCommandCompiler`               |             |
| `cn.org.expect.script.command.DebugCommandCompiler`              |             |
| `cn.org.expect.script.command.DeclareCatalogCommandCompiler`     |             |
| `cn.org.expect.script.command.DeclareCursorCommandCompiler`      |             |
| `cn.org.expect.script.command.DeclareHandlerCommandCompiler`     |             |
| `cn.org.expect.script.command.DeclareProgressCommandCompiler`    |             |
| `cn.org.expect.script.command.DeclareSSHClientCommandCompiler`   |             |
| `cn.org.expect.script.command.DeclareSSHTunnelCommandCompiler`   |             |
| `cn.org.expect.script.command.DeclareStatementCommandCompiler`   |             |
| `cn.org.expect.script.command.DefaultCommandCompiler`            |             |
| `cn.org.expect.script.command.DfCommandCompiler`                 |             |
| `cn.org.expect.script.command.Dos2UnixCommandCompiler`           |             |
| `cn.org.expect.script.command.EchoCommandCompiler`               |             |
| `cn.org.expect.script.command.EmailSendCommandCompiler`          |             |
| `cn.org.expect.script.command.ErrorCommandCompiler`              |             |
| `cn.org.expect.script.command.ExecuteFileCommandCompiler`        |             |
| `cn.org.expect.script.command.ExecuteFunctionCommandCompiler`    |             |
| `cn.org.expect.script.command.ExecuteOSCommandCompiler`          |             |
| `cn.org.expect.script.command.ExistsCommandCompiler`             |             |
| `cn.org.expect.script.command.ExitCommandCompiler`               |             |
| `cn.org.expect.script.command.ExportCommandCompiler`             |             |
| `cn.org.expect.script.command.FetchCursorCommandCompiler`        |             |
| `cn.org.expect.script.command.FetchStatementCommandCompiler`     |             |
| `cn.org.expect.script.command.FindCommandCompiler`               |             |
| `cn.org.expect.script.command.ForCommandCompiler`                |             |
| `cn.org.expect.script.command.FtpCommandCompiler`                |             |
| `cn.org.expect.script.command.FunctionCommandCompiler`           |             |
| `cn.org.expect.script.command.GetCommandCompiler`                |             |
| `cn.org.expect.script.command.GrepCommandCompiler`               |             |
| `cn.org.expect.script.command.GunzipCommandCompiler`             |             |
| `cn.org.expect.script.command.GzipCommandCompiler`               |             |
| `cn.org.expect.script.command.HandlerCommandCompiler`            |             |
| `cn.org.expect.script.command.HeadCommandCompiler`               |             |
| `cn.org.expect.script.command.HelpCommandCompiler`               |             |
| `cn.org.expect.script.command.IfCommandCompiler`                 |             |
| `cn.org.expect.script.command.IncrementCommandCompiler`          |             |
| `cn.org.expect.script.command.IsDirectoryCommandCompiler`        |             |
| `cn.org.expect.script.command.IsFileCommandCompiler`             |             |
| `cn.org.expect.script.command.JavaCommandCompiler`               |             |
| `cn.org.expect.script.command.JumpCommandCompiler`               |             |
| `cn.org.expect.script.command.LengthCommandCompiler`             |             |
| `cn.org.expect.script.command.LsCommandCompiler`                 |             |
| `cn.org.expect.script.command.MD5CommandCompiler`                |             |
| `cn.org.expect.script.command.MkdirCommandCompiler`              |             |
| `cn.org.expect.script.command.NohupCommandCompiler`              |             |
| `cn.org.expect.script.command.PSCommandCompiler`                 |             |
| `cn.org.expect.script.command.PassiveCommandCompiler`            |             |
| `cn.org.expect.script.command.PipeCommandCompiler`               |             |
| `cn.org.expect.script.command.ProgressCommandCompiler`           |             |
| `cn.org.expect.script.command.PutCommandCompiler`                |             |
| `cn.org.expect.script.command.PwdCommandCompiler`                |             |
| `cn.org.expect.script.command.QuietCommandCompiler`              |             |
| `cn.org.expect.script.command.ReadCommandCompiler`               |             |
| `cn.org.expect.script.command.ReturnCommandCompiler`             |             |
| `cn.org.expect.script.command.RmCommandCompiler`                 |             |
| `cn.org.expect.script.command.RollbackCommandCompiler`           |             |
| `cn.org.expect.script.command.SQLCommandCompiler`                |             |
| `cn.org.expect.script.command.SSH2CommandCompiler`               |             |
| `cn.org.expect.script.command.SetCommandCompiler`                |             |
| `cn.org.expect.script.command.SftpCommandCompiler`               |             |
| `cn.org.expect.script.command.SleepCommandCompiler`              |             |
| `cn.org.expect.script.command.SortTableFileCommandCompiler`      |             |
| `cn.org.expect.script.command.StacktraceCommandCompiler`         |             |
| `cn.org.expect.script.command.StepCommandCompiler`               |             |
| `cn.org.expect.script.command.SubCommandCompiler`                |             |
| `cn.org.expect.script.command.TailCommandCompiler`               |             |
| `cn.org.expect.script.command.TarCommandCompiler`                |             |
| `cn.org.expect.script.command.TerminateCommandCompiler`          |             |
| `cn.org.expect.script.command.UUIDCommandCompiler`               |             |
| `cn.org.expect.script.command.UndeclareCallbackCommandCompiler`  |             |
| `cn.org.expect.script.command.UndeclareCatalogCommandCompiler`   |             |
| `cn.org.expect.script.command.UndeclareCursorCommandCompiler`    |             |
| `cn.org.expect.script.command.UndeclareHandlerCommandCompiler`   |             |
| `cn.org.expect.script.command.UndeclareSSHCommandCompiler`       |             |
| `cn.org.expect.script.command.UndeclareStatementCommandCompiler` |             |
| `cn.org.expect.script.command.UnrarCommandCompiler`              |             |
| `cn.org.expect.script.command.UnzipCommandCompiler`              |             |
| `cn.org.expect.script.command.VariableMethodCommandCompiler`     |             |
| `cn.org.expect.script.command.WaitCommandCompiler`               |             |
| `cn.org.expect.script.command.WcCommandCompiler`                 |             |
| `cn.org.expect.script.command.WgetCommandCompiler`               |             |
| `cn.org.expect.script.command.WhileCommandCompiler`              |             |
| `cn.org.expect.script.command.ZipCommandCompiler`                |             |



### AbstractCompress
| Component Class Name                  | Description |
| ------------------------------------- | ----------- |
| `cn.org.expect.compress.GzipCompress` |             |
| `cn.org.expect.compress.RarCompress`  |             |
| `cn.org.expect.compress.TarCompress`  |             |
| `cn.org.expect.compress.ZipCompress`  |             |



### AbstractDialect
| Component Class Name                          | Description |
| --------------------------------------------- | ----------- |
| `cn.org.expect.database.db2.DB2Dialect`       |             |
| `cn.org.expect.database.db2.DB2Dialect11_5`   | DB2 11.5    |
| `cn.org.expect.database.h2.H2Dialect`         |             |
| `cn.org.expect.database.mysql.MysqlDialect`   |             |
| `cn.org.expect.database.oracle.OracleDialect` |             |



### AbstractFileCommandCompiler
| Component Class Name                                      | Description |
| --------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.CatCommandCompiler`         |             |
| `cn.org.expect.script.command.CdCommandCompiler`          |             |
| `cn.org.expect.script.command.CpCommandCompiler`          |             |
| `cn.org.expect.script.command.ExistsCommandCompiler`      |             |
| `cn.org.expect.script.command.FtpCommandCompiler`         |             |
| `cn.org.expect.script.command.GetCommandCompiler`         |             |
| `cn.org.expect.script.command.GunzipCommandCompiler`      |             |
| `cn.org.expect.script.command.GzipCommandCompiler`        |             |
| `cn.org.expect.script.command.HeadCommandCompiler`        |             |
| `cn.org.expect.script.command.IsDirectoryCommandCompiler` |             |
| `cn.org.expect.script.command.IsFileCommandCompiler`      |             |
| `cn.org.expect.script.command.LsCommandCompiler`          |             |
| `cn.org.expect.script.command.MD5CommandCompiler`         |             |
| `cn.org.expect.script.command.MkdirCommandCompiler`       |             |
| `cn.org.expect.script.command.PassiveCommandCompiler`     |             |
| `cn.org.expect.script.command.PutCommandCompiler`         |             |
| `cn.org.expect.script.command.PwdCommandCompiler`         |             |
| `cn.org.expect.script.command.RmCommandCompiler`          |             |
| `cn.org.expect.script.command.SftpCommandCompiler`        |             |
| `cn.org.expect.script.command.TailCommandCompiler`        |             |
| `cn.org.expect.script.command.TarCommandCompiler`         |             |
| `cn.org.expect.script.command.UnrarCommandCompiler`       |             |
| `cn.org.expect.script.command.UnzipCommandCompiler`       |             |
| `cn.org.expect.script.command.WcCommandCompiler`          |             |
| `cn.org.expect.script.command.WgetCommandCompiler`        |             |
| `cn.org.expect.script.command.ZipCommandCompiler`         |             |



### AbstractGlobalCommandCompiler
| Component Class Name                                            | Description |
| --------------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.CallbackCommandCompiler`          |             |
| `cn.org.expect.script.command.DeclareCatalogCommandCompiler`    |             |
| `cn.org.expect.script.command.DeclareHandlerCommandCompiler`    |             |
| `cn.org.expect.script.command.DeclareProgressCommandCompiler`   |             |
| `cn.org.expect.script.command.SetCommandCompiler`               |             |
| `cn.org.expect.script.command.UndeclareCallbackCommandCompiler` |             |
| `cn.org.expect.script.command.UndeclareCatalogCommandCompiler`  |             |
| `cn.org.expect.script.command.UndeclareHandlerCommandCompiler`  |             |



### AbstractSlaveCommandCompiler
| Component Class Name                                   | Description |
| ------------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.BreakCommandCompiler`    |             |
| `cn.org.expect.script.command.ContinueCommandCompiler` |             |
| `cn.org.expect.script.command.ReturnCommandCompiler`   |             |



### AbstractTraceCommandCompiler
| Component Class Name                                             | Description |
| ---------------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.BreakCommandCompiler`              |             |
| `cn.org.expect.script.command.ByeCommandCompiler`                |             |
| `cn.org.expect.script.command.CallProcudureCommandCompiler`      |             |
| `cn.org.expect.script.command.CatCommandCompiler`                |             |
| `cn.org.expect.script.command.CdCommandCompiler`                 |             |
| `cn.org.expect.script.command.CommitCommandCompiler`             |             |
| `cn.org.expect.script.command.ContinueCommandCompiler`           |             |
| `cn.org.expect.script.command.CpCommandCompiler`                 |             |
| `cn.org.expect.script.command.DBConnectCommandCompiler`          |             |
| `cn.org.expect.script.command.DBExportCommandCompiler`           |             |
| `cn.org.expect.script.command.DBGetCfgForCommandCompiler`        |             |
| `cn.org.expect.script.command.DBLoadCommandCompiler`             |             |
| `cn.org.expect.script.command.DDLCommandCompiler`                |             |
| `cn.org.expect.script.command.DaemonCommandCompiler`             |             |
| `cn.org.expect.script.command.DateCommandCompiler`               |             |
| `cn.org.expect.script.command.DefaultCommandCompiler`            |             |
| `cn.org.expect.script.command.DfCommandCompiler`                 |             |
| `cn.org.expect.script.command.Dos2UnixCommandCompiler`           |             |
| `cn.org.expect.script.command.EchoCommandCompiler`               |             |
| `cn.org.expect.script.command.EmailSendCommandCompiler`          |             |
| `cn.org.expect.script.command.ErrorCommandCompiler`              |             |
| `cn.org.expect.script.command.ExecuteFileCommandCompiler`        |             |
| `cn.org.expect.script.command.ExecuteFunctionCommandCompiler`    |             |
| `cn.org.expect.script.command.ExistsCommandCompiler`             |             |
| `cn.org.expect.script.command.ExitCommandCompiler`               |             |
| `cn.org.expect.script.command.FindCommandCompiler`               |             |
| `cn.org.expect.script.command.FtpCommandCompiler`                |             |
| `cn.org.expect.script.command.GetCommandCompiler`                |             |
| `cn.org.expect.script.command.GrepCommandCompiler`               |             |
| `cn.org.expect.script.command.GunzipCommandCompiler`             |             |
| `cn.org.expect.script.command.GzipCommandCompiler`               |             |
| `cn.org.expect.script.command.HandlerCommandCompiler`            |             |
| `cn.org.expect.script.command.HeadCommandCompiler`               |             |
| `cn.org.expect.script.command.HelpCommandCompiler`               |             |
| `cn.org.expect.script.command.IncrementCommandCompiler`          |             |
| `cn.org.expect.script.command.IsDirectoryCommandCompiler`        |             |
| `cn.org.expect.script.command.IsFileCommandCompiler`             |             |
| `cn.org.expect.script.command.JavaCommandCompiler`               |             |
| `cn.org.expect.script.command.JumpCommandCompiler`               |             |
| `cn.org.expect.script.command.LengthCommandCompiler`             |             |
| `cn.org.expect.script.command.LsCommandCompiler`                 |             |
| `cn.org.expect.script.command.MD5CommandCompiler`                |             |
| `cn.org.expect.script.command.MkdirCommandCompiler`              |             |
| `cn.org.expect.script.command.PSCommandCompiler`                 |             |
| `cn.org.expect.script.command.PassiveCommandCompiler`            |             |
| `cn.org.expect.script.command.ProgressCommandCompiler`           |             |
| `cn.org.expect.script.command.PutCommandCompiler`                |             |
| `cn.org.expect.script.command.PwdCommandCompiler`                |             |
| `cn.org.expect.script.command.QuietCommandCompiler`              |             |
| `cn.org.expect.script.command.ReturnCommandCompiler`             |             |
| `cn.org.expect.script.command.RmCommandCompiler`                 |             |
| `cn.org.expect.script.command.RollbackCommandCompiler`           |             |
| `cn.org.expect.script.command.SSH2CommandCompiler`               |             |
| `cn.org.expect.script.command.SftpCommandCompiler`               |             |
| `cn.org.expect.script.command.SleepCommandCompiler`              |             |
| `cn.org.expect.script.command.SortTableFileCommandCompiler`      |             |
| `cn.org.expect.script.command.StacktraceCommandCompiler`         |             |
| `cn.org.expect.script.command.StepCommandCompiler`               |             |
| `cn.org.expect.script.command.TailCommandCompiler`               |             |
| `cn.org.expect.script.command.TarCommandCompiler`                |             |
| `cn.org.expect.script.command.TerminateCommandCompiler`          |             |
| `cn.org.expect.script.command.UUIDCommandCompiler`               |             |
| `cn.org.expect.script.command.UndeclareCursorCommandCompiler`    |             |
| `cn.org.expect.script.command.UndeclareSSHCommandCompiler`       |             |
| `cn.org.expect.script.command.UndeclareStatementCommandCompiler` |             |
| `cn.org.expect.script.command.UnrarCommandCompiler`              |             |
| `cn.org.expect.script.command.UnzipCommandCompiler`              |             |
| `cn.org.expect.script.command.VariableMethodCommandCompiler`     |             |
| `cn.org.expect.script.command.WaitCommandCompiler`               |             |
| `cn.org.expect.script.command.WcCommandCompiler`                 |             |
| `cn.org.expect.script.command.WgetCommandCompiler`               |             |
| `cn.org.expect.script.command.ZipCommandCompiler`                |             |



### Analysis
| Component Class Name                           | Description    |
| ---------------------------------------------- | -------------- |
| `cn.org.expect.expression.DefaultAnalysis`     | 脚本语句分析器 |
| `cn.org.expect.script.compiler.ScriptAnalysis` |                |



### ArrayExtension
| Component Class Name                         | Description |
| -------------------------------------------- | ----------- |
| `cn.org.expect.script.method.ArrayExtension` |             |



### AutoCloseable
| Component Class Name                                      | Description                                                                                                                                                                                                                                                  |
| --------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `cn.org.expect.concurrent.EasyThreadSource`               | 线程池                                                                                                                                                                                                                                                       |
| `cn.org.expect.database.export.inernal.ExtractFileWriter` | 卸载数据到本地文件                                                                                                                                                                                                                                           |
| `cn.org.expect.database.export.inernal.FtpFileWriter`     | 卸载数据到远程ftp服务器<br>ftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径                                                                                                                                                               |
| `cn.org.expect.compress.GzipCompress`                     |                                                                                                                                                                                                                                                              |
| `cn.org.expect.database.export.inernal.HttpRequestWriter` | 卸载数据到用户浏览器<br>http://download/HttpServletRequest 对象的变量名/HttpServletResponse对象的变量名/下载文件名（需要提前将 HttpServletRequest 对象与 HttpServletResponse 对象保存到脚本引擎变量中，变量分别是: httpServletRequest, httpServletResponse） |
| `cn.org.expect.compress.RarCompress`                      |                                                                                                                                                                                                                                                              |
| `cn.org.expect.database.export.inernal.SftpFileWriter`    | 卸载数据到远程sftp服务器<br>sftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径                                                                                                                                                             |
| `cn.org.expect.compress.TarCompress`                      |                                                                                                                                                                                                                                                              |
| `cn.org.expect.compress.ZipCompress`                      |                                                                                                                                                                                                                                                              |



### BaseAnalysis
| Component Class Name                           | Description    |
| ---------------------------------------------- | -------------- |
| `cn.org.expect.expression.DefaultAnalysis`     | 脚本语句分析器 |
| `cn.org.expect.script.compiler.ScriptAnalysis` |                |



### BreakCommandCompiler
| Component Class Name                                | Description |
| --------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.BreakCommandCompiler` |             |



### ByeCommandCompiler
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.ByeCommandCompiler` |             |



### CallProcudureCommandCompiler
| Component Class Name                                        | Description |
| ----------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.CallProcudureCommandCompiler` |             |



### CallbackCommandCompiler
| Component Class Name                                   | Description |
| ------------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.CallbackCommandCompiler` |             |



### CatCommandCompiler
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.CatCommandCompiler` |             |



### CdCommandCompiler
| Component Class Name                             | Description |
| ------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.CdCommandCompiler` |             |



### CharAtMethod
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.method.string.CharAtMethod` |             |



### CharsetName
| Component Class Name                       | Description                                                                     |
| ------------------------------------------ | ------------------------------------------------------------------------------- |
| `cn.org.expect.io.CommonTextTableFile`     | 文本文件, 逗号分隔，无转义字符，无字符串限定符                                  |
| `cn.org.expect.io.CsvFile`                 | CSV格式文件                                                                     |
| `cn.org.expect.database.db2.DB2ExportFile` | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符 |
| `cn.org.expect.os.ftp.FtpCommand`          | FTP协议的实现类                                                                 |
| `cn.org.expect.os.linux.LinuxRemoteOS`     |                                                                                 |
| `cn.org.expect.os.ssh.SecureShellCommand`  | jsch                                                                            |
| `cn.org.expect.os.ssh.SftpCommand`         | jsch-0.1.51                                                                     |
| `cn.org.expect.os.telnet.TelnetCommand`    | apache-net                                                                      |



### ClassExtension
| Component Class Name                         | Description |
| -------------------------------------------- | ----------- |
| `cn.org.expect.script.method.ClassExtension` |             |



### Cloneable
| Component Class Name                            | Description                                                                     |
| ----------------------------------------------- | ------------------------------------------------------------------------------- |
| `cn.org.expect.io.CommonTextTableFile`          | 文本文件, 逗号分隔，无转义字符，无字符串限定符                                  |
| `cn.org.expect.io.CsvFile`                      | CSV格式文件                                                                     |
| `cn.org.expect.database.db2.DB2ExportFile`      | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符 |
| `cn.org.expect.script.internal.ScriptFormatter` |                                                                                 |
| `cn.org.expect.util.StringComparator`           | 字符串比较规则                                                                  |



### Closeable
| Component Class Name                                      | Description                                                                                                                                                                                                                                                  |
| --------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `cn.org.expect.concurrent.EasyThreadSource`               | 线程池                                                                                                                                                                                                                                                       |
| `cn.org.expect.database.export.inernal.ExtractFileWriter` | 卸载数据到本地文件                                                                                                                                                                                                                                           |
| `cn.org.expect.database.export.inernal.FtpFileWriter`     | 卸载数据到远程ftp服务器<br>ftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径                                                                                                                                                               |
| `cn.org.expect.compress.GzipCompress`                     |                                                                                                                                                                                                                                                              |
| `cn.org.expect.database.export.inernal.HttpRequestWriter` | 卸载数据到用户浏览器<br>http://download/HttpServletRequest 对象的变量名/HttpServletResponse对象的变量名/下载文件名（需要提前将 HttpServletRequest 对象与 HttpServletResponse 对象保存到脚本引擎变量中，变量分别是: httpServletRequest, httpServletResponse） |
| `cn.org.expect.compress.RarCompress`                      |                                                                                                                                                                                                                                                              |
| `cn.org.expect.database.export.inernal.SftpFileWriter`    | 卸载数据到远程sftp服务器<br>sftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径                                                                                                                                                             |
| `cn.org.expect.compress.TarCompress`                      |                                                                                                                                                                                                                                                              |
| `cn.org.expect.compress.ZipCompress`                      |                                                                                                                                                                                                                                                              |



### CollectionExtension
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.method.CollectionExtension` |             |



### CommitCommandCompiler
| Component Class Name                                 | Description |
| ---------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.CommitCommandCompiler` |             |



### CommonTextTableFile
| Component Class Name                       | Description                                                                     |
| ------------------------------------------ | ------------------------------------------------------------------------------- |
| `cn.org.expect.io.CommonTextTableFile`     | 文本文件, 逗号分隔，无转义字符，无字符串限定符                                  |
| `cn.org.expect.io.CsvFile`                 | CSV格式文件                                                                     |
| `cn.org.expect.database.db2.DB2ExportFile` | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符 |



### Comparator
| Component Class Name                       | Description                |
| ------------------------------------------ | -------------------------- |
| `cn.org.expect.util.StrAsIntComparator`    | 字符串作为整数的比较规则   |
| `cn.org.expect.util.StrAsNumberComparator` | 字符串作为浮点数的比较规则 |
| `cn.org.expect.util.StringComparator`      | 字符串比较规则             |



### CompressFactory
| Component Class Name                     | Description |
| ---------------------------------------- | ----------- |
| `cn.org.expect.compress.CompressFactory` |             |



### ContainerCommandCompiler
| Component Class Name                                    | Description |
| ------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.ContainerCommandCompiler` |             |



### ContinueCommandCompiler
| Component Class Name                                   | Description |
| ------------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.ContinueCommandCompiler` |             |



### CpCommandCompiler
| Component Class Name                             | Description |
| ------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.CpCommandCompiler` |             |



### CsvFile
| Component Class Name       | Description |
| -------------------------- | ----------- |
| `cn.org.expect.io.CsvFile` | CSV格式文件 |



### CursorCommandCompiler
| Component Class Name                                 | Description |
| ---------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.CursorCommandCompiler` |             |



### DB2Command
| Component Class Name                         | Description |
| -------------------------------------------- | ----------- |
| `cn.org.expect.database.db2.DB2LinuxCommand` |             |



### DB2Dialect
| Component Class Name                        | Description |
| ------------------------------------------- | ----------- |
| `cn.org.expect.database.db2.DB2Dialect`     |             |
| `cn.org.expect.database.db2.DB2Dialect11_5` | DB2 11.5    |



### DB2Dialect11_5
| Component Class Name                        | Description |
| ------------------------------------------- | ----------- |
| `cn.org.expect.database.db2.DB2Dialect11_5` | DB2 11.5    |



### DB2ExportFile
| Component Class Name                       | Description                                                                     |
| ------------------------------------------ | ------------------------------------------------------------------------------- |
| `cn.org.expect.database.db2.DB2ExportFile` | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符 |



### DB2LinuxCommand
| Component Class Name                         | Description |
| -------------------------------------------- | ----------- |
| `cn.org.expect.database.db2.DB2LinuxCommand` |             |



### DBConnectCommandCompiler
| Component Class Name                                    | Description |
| ------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.DBConnectCommandCompiler` |             |



### DBExportCommandCompiler
| Component Class Name                                   | Description |
| ------------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.DBExportCommandCompiler` |             |



### DBGetCfgForCommandCompiler
| Component Class Name                                      | Description |
| --------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.DBGetCfgForCommandCompiler` |             |



### DBLoadCommandCompiler
| Component Class Name                                 | Description |
| ---------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.DBLoadCommandCompiler` |             |



### DDLCommandCompiler
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.DDLCommandCompiler` |             |



### DaemonCommandCompiler
| Component Class Name                                 | Description |
| ---------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.DaemonCommandCompiler` |             |



### DatabaseConfigurationContainer
| Component Class Name                                                     | Description |
| ------------------------------------------------------------------------ | ----------- |
| `cn.org.expect.database.internal.StandardDatabaseConfigurationContainer` |             |



### DatabaseDialectFactory
| Component Class Name                                     | Description |
| -------------------------------------------------------- | ----------- |
| `cn.org.expect.database.internal.DatabaseDialectFactory` |             |



### DateCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.DateCommandCompiler` |             |



### DateExtension
| Component Class Name                        | Description |
| ------------------------------------------- | ----------- |
| `cn.org.expect.script.method.DateExtension` |             |



### DebugCommandCompiler
| Component Class Name                                | Description |
| --------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.DebugCommandCompiler` |             |



### DeclareCatalogCommandCompiler
| Component Class Name                                         | Description |
| ------------------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.DeclareCatalogCommandCompiler` |             |



### DeclareCursorCommandCompiler
| Component Class Name                                        | Description |
| ----------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.DeclareCursorCommandCompiler` |             |



### DeclareHandlerCommandCompiler
| Component Class Name                                         | Description |
| ------------------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.DeclareHandlerCommandCompiler` |             |



### DeclareProgressCommandCompiler
| Component Class Name                                          | Description |
| ------------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.DeclareProgressCommandCompiler` |             |



### DeclareSSHClientCommandCompiler
| Component Class Name                                           | Description |
| -------------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.DeclareSSHClientCommandCompiler` |             |



### DeclareSSHTunnelCommandCompiler
| Component Class Name                                           | Description |
| -------------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.DeclareSSHTunnelCommandCompiler` |             |



### DeclareStatementCommandCompiler
| Component Class Name                                           | Description |
| -------------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.DeclareStatementCommandCompiler` |             |



### DefaultAnalysis
| Component Class Name                           | Description    |
| ---------------------------------------------- | -------------- |
| `cn.org.expect.expression.DefaultAnalysis`     | 脚本语句分析器 |
| `cn.org.expect.script.compiler.ScriptAnalysis` |                |



### DefaultCommandCompiler
| Component Class Name                                  | Description |
| ----------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.DefaultCommandCompiler` |             |



### DefaultCommandSupported
| Component Class Name                                    | Description |
| ------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.ExecuteOSCommandCompiler` |             |
| `cn.org.expect.script.command.QuietCommandCompiler`     |             |
| `cn.org.expect.script.command.SQLCommandCompiler`       |             |



### DefaultEasyContext
| Component Class Name                   | Description |
| -------------------------------------- | ----------- |
| `cn.org.expect.ioc.DefaultEasyContext` |             |



### DfCommandCompiler
| Component Class Name                             | Description |
| ------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.DfCommandCompiler` |             |



### Dos2UnixCommandCompiler
| Component Class Name                                   | Description |
| ------------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.Dos2UnixCommandCompiler` |             |



### EasyBeanFactory
| Component Class Name                                     | Description |
| -------------------------------------------------------- | ----------- |
| `cn.org.expect.compress.CompressFactory`                 |             |
| `cn.org.expect.database.internal.DatabaseDialectFactory` |             |
| `cn.org.expect.database.export.ExtractWriterFactory`     |             |
| `cn.org.expect.increment.IncrementReplaceFactory`        |             |
| `cn.org.expect.os.OSFactory`                             |             |
| `cn.org.expect.database.export.inernal.ReaderFactory`    |             |
| `cn.org.expect.io.TableLineRulerFactory`                 |             |
| `cn.org.expect.io.TextTableFileFactory`                  |             |
| `cn.org.expect.ioc.impl.ThreadSourceFactory`             |             |



### EasyBeanFactoryRepository
| Component Class Name                   | Description |
| -------------------------------------- | ----------- |
| `cn.org.expect.ioc.DefaultEasyContext` |             |



### EasyBeanInjector
| Component Class Name                   | Description |
| -------------------------------------- | ----------- |
| `cn.org.expect.ioc.DefaultEasyContext` |             |



### EasyBeanRepository
| Component Class Name                   | Description |
| -------------------------------------- | ----------- |
| `cn.org.expect.ioc.DefaultEasyContext` |             |



### EasyContainer
| Component Class Name                   | Description |
| -------------------------------------- | ----------- |
| `cn.org.expect.ioc.DefaultEasyContext` |             |



### EasyContainerRepository
| Component Class Name                   | Description |
| -------------------------------------- | ----------- |
| `cn.org.expect.ioc.DefaultEasyContext` |             |



### EasyContext
| Component Class Name                   | Description |
| -------------------------------------- | ----------- |
| `cn.org.expect.ioc.DefaultEasyContext` |             |



### EasyContextAware
| Component Class Name                                      | Description                                                                                      |
| --------------------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| `cn.org.expect.io.CommonTextTableFile`                    | 文本文件, 逗号分隔，无转义字符，无字符串限定符                                                   |
| `cn.org.expect.io.CsvFile`                                | CSV格式文件                                                                                      |
| `cn.org.expect.database.db2.DB2Dialect`                   |                                                                                                  |
| `cn.org.expect.database.db2.DB2Dialect11_5`               | DB2 11.5                                                                                         |
| `cn.org.expect.database.db2.DB2ExportFile`                | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符                  |
| `cn.org.expect.database.export.inernal.ExtractFileWriter` | 卸载数据到本地文件                                                                               |
| `cn.org.expect.os.ftp.FtpCommand`                         | FTP协议的实现类                                                                                  |
| `cn.org.expect.database.export.inernal.FtpFileWriter`     | 卸载数据到远程ftp服务器<br>ftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径   |
| `cn.org.expect.os.linux.LinuxRemoteOS`                    |                                                                                                  |
| `cn.org.expect.database.load.serial.SerialLoadFileEngine` |                                                                                                  |
| `cn.org.expect.database.export.inernal.SftpFileWriter`    | 卸载数据到远程sftp服务器<br>sftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径 |



### EasyPropertyProvider
| Component Class Name                   | Description |
| -------------------------------------- | ----------- |
| `cn.org.expect.ioc.DefaultEasyContext` |             |



### EasyThreadSource
| Component Class Name                        | Description |
| ------------------------------------------- | ----------- |
| `cn.org.expect.concurrent.EasyThreadSource` | 线程池      |



### EchoCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.EchoCommandCompiler` |             |



### ElementMethod
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.method.array.ElementMethod` |             |



### EmailSendCommandCompiler
| Component Class Name                                    | Description |
| ------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.EmailSendCommandCompiler` |             |



### ErrorCommandCompiler
| Component Class Name                                | Description |
| --------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.ErrorCommandCompiler` |             |



### Escape
| Component Class Name                           | Description                                                                     |
| ---------------------------------------------- | ------------------------------------------------------------------------------- |
| `cn.org.expect.io.CommonTextTableFile`         | 文本文件, 逗号分隔，无转义字符，无字符串限定符                                  |
| `cn.org.expect.io.CsvFile`                     | CSV格式文件                                                                     |
| `cn.org.expect.database.db2.DB2ExportFile`     | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符 |
| `cn.org.expect.script.compiler.ScriptAnalysis` |                                                                                 |



### ExecuteFileCommandCompiler
| Component Class Name                                      | Description |
| --------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.DaemonCommandCompiler`      |             |
| `cn.org.expect.script.command.ExecuteFileCommandCompiler` |             |



### ExecuteFunctionCommandCompiler
| Component Class Name                                          | Description |
| ------------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.ExecuteFunctionCommandCompiler` |             |



### ExecuteOSCommandCompiler
| Component Class Name                                    | Description |
| ------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.ExecuteOSCommandCompiler` |             |



### ExistsCommandCompiler
| Component Class Name                                 | Description |
| ---------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.ExistsCommandCompiler` |             |



### ExitCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.ExitCommandCompiler` |             |



### ExportCommandCompiler
| Component Class Name                                 | Description |
| ---------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.ExportCommandCompiler` |             |



### ExtractFileWriter
| Component Class Name                                      | Description        |
| --------------------------------------------------------- | ------------------ |
| `cn.org.expect.database.export.inernal.ExtractFileWriter` | 卸载数据到本地文件 |



### ExtractWriterFactory
| Component Class Name                                 | Description |
| ---------------------------------------------------- | ----------- |
| `cn.org.expect.database.export.ExtractWriterFactory` |             |



### FetchCursorCommandCompiler
| Component Class Name                                      | Description |
| --------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.FetchCursorCommandCompiler` |             |



### FetchStatementCommandCompiler
| Component Class Name                                         | Description |
| ------------------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.FetchStatementCommandCompiler` |             |



### FileExtension
| Component Class Name                        | Description |
| ------------------------------------------- | ----------- |
| `cn.org.expect.script.method.FileExtension` |             |



### FindCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.FindCommandCompiler` |             |



### Flushable
| Component Class Name                                      | Description                                                                                                                                                                                                                                                  |
| --------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `cn.org.expect.database.export.inernal.ExtractFileWriter` | 卸载数据到本地文件                                                                                                                                                                                                                                           |
| `cn.org.expect.database.export.inernal.FtpFileWriter`     | 卸载数据到远程ftp服务器<br>ftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径                                                                                                                                                               |
| `cn.org.expect.database.export.inernal.HttpRequestWriter` | 卸载数据到用户浏览器<br>http://download/HttpServletRequest 对象的变量名/HttpServletResponse对象的变量名/下载文件名（需要提前将 HttpServletRequest 对象与 HttpServletResponse 对象保存到脚本引擎变量中，变量分别是: httpServletRequest, httpServletResponse） |
| `cn.org.expect.database.export.inernal.SftpFileWriter`    | 卸载数据到远程sftp服务器<br>sftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径                                                                                                                                                             |



### ForCommandCompiler
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.ForCommandCompiler` |             |



### ForNameMethod
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.method.clazz.ForNameMethod` |             |



### ForNameStrMethod
| Component Class Name                                 | Description |
| ---------------------------------------------------- | ----------- |
| `cn.org.expect.script.method.clazz.ForNameStrMethod` |             |



### Format
| Component Class Name                            | Description |
| ----------------------------------------------- | ----------- |
| `cn.org.expect.script.internal.ScriptFormatter` |             |



### FtpCommand
| Component Class Name              | Description     |
| --------------------------------- | --------------- |
| `cn.org.expect.os.ftp.FtpCommand` | FTP协议的实现类 |



### FtpCommandCompiler
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.FtpCommandCompiler` |             |



### FtpFileWriter
| Component Class Name                                  | Description                                                                                    |
| ----------------------------------------------------- | ---------------------------------------------------------------------------------------------- |
| `cn.org.expect.database.export.inernal.FtpFileWriter` | 卸载数据到远程ftp服务器<br>ftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径 |



### FunctionCommandCompiler
| Component Class Name                                   | Description |
| ------------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.FunctionCommandCompiler` |             |



### GetCommandCompiler
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.GetCommandCompiler` |             |



### GetPropertyStrMethod
| Component Class Name                                     | Description |
| -------------------------------------------------------- | ----------- |
| `cn.org.expect.script.method.clazz.GetPropertyStrMethod` |             |



### GrepCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.GrepCommandCompiler` |             |



### GunzipCommandCompiler
| Component Class Name                                 | Description |
| ---------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.GunzipCommandCompiler` |             |



### GzipCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.GzipCommandCompiler` |             |



### GzipCompress
| Component Class Name                  | Description |
| ------------------------------------- | ----------- |
| `cn.org.expect.compress.GzipCompress` |             |



### H2Dialect
| Component Class Name                  | Description |
| ------------------------------------- | ----------- |
| `cn.org.expect.database.h2.H2Dialect` |             |



### HandlerCommandCompiler
| Component Class Name                                  | Description |
| ----------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.HandlerCommandCompiler` |             |



### HeadCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.HeadCommandCompiler` |             |



### HelpCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.HelpCommandCompiler` |             |



### HelpMethod
| Component Class Name                            | Description |
| ----------------------------------------------- | ----------- |
| `cn.org.expect.script.method.object.HelpMethod` |             |



### HttpExtension
| Component Class Name                        | Description |
| ------------------------------------------- | ----------- |
| `cn.org.expect.script.method.HttpExtension` |             |



### HttpRequestWriter
| Component Class Name                                      | Description                                                                                                                                                                                                                                                  |
| --------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `cn.org.expect.database.export.inernal.HttpRequestWriter` | 卸载数据到用户浏览器<br>http://download/HttpServletRequest 对象的变量名/HttpServletResponse对象的变量名/下载文件名（需要提前将 HttpServletRequest 对象与 HttpServletResponse 对象保存到脚本引擎变量中，变量分别是: httpServletRequest, httpServletResponse） |



### IfCommandCompiler
| Component Class Name                             | Description |
| ------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.IfCommandCompiler` |             |



### IncrementCommandCompiler
| Component Class Name                                    | Description |
| ------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.IncrementCommandCompiler` |             |



### IncrementReplaceFactory
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.increment.IncrementReplaceFactory` |             |



### IndexOfMethod
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.method.array.IndexOfMethod` |             |



### IndexOfStrIntMethod
| Component Class Name                                    | Description |
| ------------------------------------------------------- | ----------- |
| `cn.org.expect.script.method.array.IndexOfStrIntMethod` |             |



### IntMethod
| Component Class Name                           | Description |
| ---------------------------------------------- | ----------- |
| `cn.org.expect.script.method.object.IntMethod` |             |



### IsDirectoryCommandCompiler
| Component Class Name                                      | Description |
| --------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.IsDirectoryCommandCompiler` |             |



### IsFileCommandCompiler
| Component Class Name                                 | Description |
| ---------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.IsFileCommandCompiler` |             |



### JavaCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.JavaCommandCompiler` |             |



### JumpCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.JumpCommandCompiler` |             |



### LengthCommandCompiler
| Component Class Name                                 | Description |
| ---------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.LengthCommandCompiler` |             |



### LineSeparator
| Component Class Name                       | Description                                                                     |
| ------------------------------------------ | ------------------------------------------------------------------------------- |
| `cn.org.expect.io.CommonTextTableFile`     | 文本文件, 逗号分隔，无转义字符，无字符串限定符                                  |
| `cn.org.expect.io.CsvFile`                 | CSV格式文件                                                                     |
| `cn.org.expect.database.db2.DB2ExportFile` | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符 |



### LinuxLocalOS
| Component Class Name                  | Description |
| ------------------------------------- | ----------- |
| `cn.org.expect.os.linux.LinuxLocalOS` |             |
| `cn.org.expect.os.macos.MacOS`        |             |



### LinuxRemoteOS
| Component Class Name                   | Description |
| -------------------------------------- | ----------- |
| `cn.org.expect.os.linux.LinuxRemoteOS` |             |



### Loader
| Component Class Name                                      | Description |
| --------------------------------------------------------- | ----------- |
| `cn.org.expect.database.load.serial.SerialLoadFileEngine` |             |



### LsCommandCompiler
| Component Class Name                             | Description |
| ------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.LsCommandCompiler` |             |



### MD5CommandCompiler
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.MD5CommandCompiler` |             |



### MacOS
| Component Class Name           | Description |
| ------------------------------ | ----------- |
| `cn.org.expect.os.macos.MacOS` |             |



### Mail
| Component Class Name          | Description |
| ----------------------------- | ----------- |
| `cn.org.expect.mail.MailImpl` |             |



### MailImpl
| Component Class Name          | Description |
| ----------------------------- | ----------- |
| `cn.org.expect.mail.MailImpl` |             |



### MkdirCommandCompiler
| Component Class Name                                | Description |
| --------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.MkdirCommandCompiler` |             |



### MysqlDialect
| Component Class Name                        | Description |
| ------------------------------------------- | ----------- |
| `cn.org.expect.database.mysql.MysqlDialect` |             |



### NewInstanceMethod
| Component Class Name                                  | Description |
| ----------------------------------------------------- | ----------- |
| `cn.org.expect.script.method.clazz.NewInstanceMethod` |             |



### NohupCommandCompiler
| Component Class Name                                | Description |
| --------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.NohupCommandCompiler` |             |



### OSCommand
| Component Class Name                      | Description |
| ----------------------------------------- | ----------- |
| `cn.org.expect.os.ssh.SecureShellCommand` | jsch        |
| `cn.org.expect.os.telnet.TelnetCommand`   | apache-net  |



### OSConnectCommand
| Component Class Name                      | Description     |
| ----------------------------------------- | --------------- |
| `cn.org.expect.os.ftp.FtpCommand`         | FTP协议的实现类 |
| `cn.org.expect.os.ssh.SecureShellCommand` | jsch            |
| `cn.org.expect.os.ssh.SftpCommand`        | jsch-0.1.51     |
| `cn.org.expect.os.telnet.TelnetCommand`   | apache-net      |



### OSDateCommand
| Component Class Name                   | Description |
| -------------------------------------- | ----------- |
| `cn.org.expect.os.linux.LinuxLocalOS`  |             |
| `cn.org.expect.os.linux.LinuxRemoteOS` |             |
| `cn.org.expect.os.macos.MacOS`         |             |



### OSFactory
| Component Class Name         | Description |
| ---------------------------- | ----------- |
| `cn.org.expect.os.OSFactory` |             |



### OSFileCommand
| Component Class Name                   | Description     |
| -------------------------------------- | --------------- |
| `cn.org.expect.os.ftp.FtpCommand`      | FTP协议的实现类 |
| `cn.org.expect.os.linux.LinuxRemoteOS` |                 |
| `cn.org.expect.os.ssh.SftpCommand`     | jsch-0.1.51     |



### OSFtpCommand
| Component Class Name               | Description     |
| ---------------------------------- | --------------- |
| `cn.org.expect.os.ftp.FtpCommand`  | FTP协议的实现类 |
| `cn.org.expect.os.ssh.SftpCommand` | jsch-0.1.51     |



### OSNetwork
| Component Class Name                   | Description |
| -------------------------------------- | ----------- |
| `cn.org.expect.os.linux.LinuxLocalOS`  |             |
| `cn.org.expect.os.linux.LinuxRemoteOS` |             |
| `cn.org.expect.os.macos.MacOS`         |             |



### OSSecureShellCommand
| Component Class Name                      | Description |
| ----------------------------------------- | ----------- |
| `cn.org.expect.os.ssh.SecureShellCommand` | jsch        |



### OSShellCommand
| Component Class Name                      | Description |
| ----------------------------------------- | ----------- |
| `cn.org.expect.os.ssh.SecureShellCommand` | jsch        |
| `cn.org.expect.os.telnet.TelnetCommand`   | apache-net  |



### OracleDialect
| Component Class Name                          | Description |
| --------------------------------------------- | ----------- |
| `cn.org.expect.database.oracle.OracleDialect` |             |



### PSCommandCompiler
| Component Class Name                             | Description |
| ------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.PSCommandCompiler` |             |



### PassiveCommandCompiler
| Component Class Name                                  | Description |
| ----------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.PassiveCommandCompiler` |             |



### PipeCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.PipeCommandCompiler` |             |



### PrintMethod
| Component Class Name                            | Description |
| ----------------------------------------------- | ----------- |
| `cn.org.expect.script.method.array.PrintMethod` |             |



### PrintMethod
| Component Class Name                             | Description |
| ------------------------------------------------ | ----------- |
| `cn.org.expect.script.method.object.PrintMethod` |             |



### ProgressCommandCompiler
| Component Class Name                                   | Description |
| ------------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.ProgressCommandCompiler` |             |



### PutCommandCompiler
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.PutCommandCompiler` |             |



### PwdCommandCompiler
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.PwdCommandCompiler` |             |



### QuietCommandCompiler
| Component Class Name                                | Description |
| --------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.QuietCommandCompiler` |             |



### RarCompress
| Component Class Name                 | Description |
| ------------------------------------ | ----------- |
| `cn.org.expect.compress.RarCompress` |             |



### ReadCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.ReadCommandCompiler` |             |



### ReaderFactory
| Component Class Name                                  | Description |
| ----------------------------------------------------- | ----------- |
| `cn.org.expect.database.export.inernal.ReaderFactory` |             |



### ResourceMessageBundle
| Component Class Name                                    | Description |
| ------------------------------------------------------- | ----------- |
| `cn.org.expect.message.ResourceMessageBundleRepository` |             |



### ResourceMessageBundleRepository
| Component Class Name                                    | Description |
| ------------------------------------------------------- | ----------- |
| `cn.org.expect.message.ResourceMessageBundleRepository` |             |



### ReturnCommandCompiler
| Component Class Name                                 | Description |
| ---------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.ReturnCommandCompiler` |             |



### RmCommandCompiler
| Component Class Name                             | Description |
| ------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.RmCommandCompiler` |             |



### RollbackCommandCompiler
| Component Class Name                                   | Description |
| ------------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.RollbackCommandCompiler` |             |



### Runnable
| Component Class Name                    | Description |
| --------------------------------------- | ----------- |
| `cn.org.expect.os.telnet.TelnetCommand` | apache-net  |



### SQLCommandCompiler
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.SQLCommandCompiler` |             |



### SSH2CommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.SSH2CommandCompiler` |             |



### ScriptAnalysis
| Component Class Name                           | Description |
| ---------------------------------------------- | ----------- |
| `cn.org.expect.script.compiler.ScriptAnalysis` |             |



### ScriptChecker
| Component Class Name                          | Description |
| --------------------------------------------- | ----------- |
| `cn.org.expect.script.compiler.ScriptChecker` |             |



### ScriptCompiler
| Component Class Name                           | Description |
| ---------------------------------------------- | ----------- |
| `cn.org.expect.script.compiler.ScriptCompiler` | 即时编译器  |



### ScriptConfiguration
| Component Class Name                                | Description |
| --------------------------------------------------- | ----------- |
| `cn.org.expect.script.internal.ScriptConfiguration` |             |



### ScriptEngineExtension
| Component Class Name                                | Description |
| --------------------------------------------------- | ----------- |
| `cn.org.expect.script.method.ScriptEngineExtension` |             |



### ScriptFormatter
| Component Class Name                            | Description |
| ----------------------------------------------- | ----------- |
| `cn.org.expect.script.internal.ScriptFormatter` |             |



### SecureShellCommand
| Component Class Name                      | Description |
| ----------------------------------------- | ----------- |
| `cn.org.expect.os.ssh.SecureShellCommand` | jsch        |



### SerialLoadFileEngine
| Component Class Name                                      | Description |
| --------------------------------------------------------- | ----------- |
| `cn.org.expect.database.load.serial.SerialLoadFileEngine` |             |



### Serializable
| Component Class Name                            | Description |
| ----------------------------------------------- | ----------- |
| `cn.org.expect.script.internal.ScriptFormatter` |             |



### SessionFactory
| Component Class Name                          | Description      |
| --------------------------------------------- | ---------------- |
| `cn.org.expect.script.session.SessionFactory` | 脚本引擎会话工厂 |



### SetCommandCompiler
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.SetCommandCompiler` |             |



### SftpCommand
| Component Class Name               | Description |
| ---------------------------------- | ----------- |
| `cn.org.expect.os.ssh.SftpCommand` | jsch-0.1.51 |



### SftpCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.SftpCommandCompiler` |             |



### SftpFileWriter
| Component Class Name                                   | Description                                                                                      |
| ------------------------------------------------------ | ------------------------------------------------------------------------------------------------ |
| `cn.org.expect.database.export.inernal.FtpFileWriter`  | 卸载数据到远程ftp服务器<br>ftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径   |
| `cn.org.expect.database.export.inernal.SftpFileWriter` | 卸载数据到远程sftp服务器<br>sftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径 |



### SleepCommandCompiler
| Component Class Name                                | Description |
| --------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.SleepCommandCompiler` |             |



### SortTableFileCommandCompiler
| Component Class Name                                        | Description |
| ----------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.SortTableFileCommandCompiler` |             |



### SplitMethod
| Component Class Name                             | Description |
| ------------------------------------------------ | ----------- |
| `cn.org.expect.script.method.string.SplitMethod` |             |



### StacktraceCommandCompiler
| Component Class Name                                     | Description |
| -------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.StacktraceCommandCompiler` |             |



### StandardDatabaseConfigurationContainer
| Component Class Name                                                     | Description |
| ------------------------------------------------------------------------ | ----------- |
| `cn.org.expect.database.internal.StandardDatabaseConfigurationContainer` |             |



### StepCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.StepCommandCompiler` |             |



### StrAsIntComparator
| Component Class Name                    | Description              |
| --------------------------------------- | ------------------------ |
| `cn.org.expect.util.StrAsIntComparator` | 字符串作为整数的比较规则 |



### StrAsNumberComparator
| Component Class Name                       | Description                |
| ------------------------------------------ | -------------------------- |
| `cn.org.expect.util.StrAsNumberComparator` | 字符串作为浮点数的比较规则 |



### StringComparator
| Component Class Name                  | Description    |
| ------------------------------------- | -------------- |
| `cn.org.expect.util.StringComparator` | 字符串比较规则 |



### StringExtension
| Component Class Name                          | Description |
| --------------------------------------------- | ----------- |
| `cn.org.expect.script.method.StringExtension` |             |



### SubCommandCompiler
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.SubCommandCompiler` |             |



### Table
| Component Class Name                       | Description                                                                     |
| ------------------------------------------ | ------------------------------------------------------------------------------- |
| `cn.org.expect.io.CommonTextTableFile`     | 文本文件, 逗号分隔，无转义字符，无字符串限定符                                  |
| `cn.org.expect.io.CsvFile`                 | CSV格式文件                                                                     |
| `cn.org.expect.database.db2.DB2ExportFile` | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符 |



### TableLineRulerFactory
| Component Class Name                     | Description |
| ---------------------------------------- | ----------- |
| `cn.org.expect.io.TableLineRulerFactory` |             |



### TailCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.TailCommandCompiler` |             |



### TarCommandCompiler
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.TarCommandCompiler` |             |



### TarCompress
| Component Class Name                  | Description |
| ------------------------------------- | ----------- |
| `cn.org.expect.compress.GzipCompress` |             |
| `cn.org.expect.compress.TarCompress`  |             |



### TelnetCommand
| Component Class Name                    | Description |
| --------------------------------------- | ----------- |
| `cn.org.expect.os.telnet.TelnetCommand` | apache-net  |



### TelnetNotificationHandler
| Component Class Name                    | Description |
| --------------------------------------- | ----------- |
| `cn.org.expect.os.telnet.TelnetCommand` | apache-net  |



### Terminate
| Component Class Name                                      | Description |
| --------------------------------------------------------- | ----------- |
| `cn.org.expect.compress.GzipCompress`                     |             |
| `cn.org.expect.compress.RarCompress`                      |             |
| `cn.org.expect.script.compiler.ScriptCompiler`            | 即时编译器  |
| `cn.org.expect.os.ssh.SecureShellCommand`                 | jsch        |
| `cn.org.expect.database.load.serial.SerialLoadFileEngine` |             |
| `cn.org.expect.compress.TarCompress`                      |             |
| `cn.org.expect.os.telnet.TelnetCommand`                   | apache-net  |
| `cn.org.expect.compress.ZipCompress`                      |             |



### TerminateCommandCompiler
| Component Class Name                                    | Description |
| ------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.TerminateCommandCompiler` |             |



### Terminator
| Component Class Name                                      | Description |
| --------------------------------------------------------- | ----------- |
| `cn.org.expect.compress.GzipCompress`                     |             |
| `cn.org.expect.compress.RarCompress`                      |             |
| `cn.org.expect.script.compiler.ScriptCompiler`            | 即时编译器  |
| `cn.org.expect.os.ssh.SecureShellCommand`                 | jsch        |
| `cn.org.expect.database.load.serial.SerialLoadFileEngine` |             |
| `cn.org.expect.compress.TarCompress`                      |             |
| `cn.org.expect.os.telnet.TelnetCommand`                   | apache-net  |
| `cn.org.expect.compress.ZipCompress`                      |             |



### TextFile
| Component Class Name                       | Description                                                                     |
| ------------------------------------------ | ------------------------------------------------------------------------------- |
| `cn.org.expect.io.CommonTextTableFile`     | 文本文件, 逗号分隔，无转义字符，无字符串限定符                                  |
| `cn.org.expect.io.CsvFile`                 | CSV格式文件                                                                     |
| `cn.org.expect.database.db2.DB2ExportFile` | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符 |



### TextTable
| Component Class Name                       | Description                                                                     |
| ------------------------------------------ | ------------------------------------------------------------------------------- |
| `cn.org.expect.io.CommonTextTableFile`     | 文本文件, 逗号分隔，无转义字符，无字符串限定符                                  |
| `cn.org.expect.io.CsvFile`                 | CSV格式文件                                                                     |
| `cn.org.expect.database.db2.DB2ExportFile` | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符 |



### TextTableFileFactory
| Component Class Name                    | Description |
| --------------------------------------- | ----------- |
| `cn.org.expect.io.TextTableFileFactory` |             |



### ThreadSourceFactory
| Component Class Name                         | Description |
| -------------------------------------------- | ----------- |
| `cn.org.expect.ioc.impl.ThreadSourceFactory` |             |



### UUIDCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.UUIDCommandCompiler` |             |



### UndeclareCallbackCommandCompiler
| Component Class Name                                            | Description |
| --------------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.UndeclareCallbackCommandCompiler` |             |



### UndeclareCatalogCommandCompiler
| Component Class Name                                           | Description |
| -------------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.UndeclareCatalogCommandCompiler` |             |



### UndeclareCursorCommandCompiler
| Component Class Name                                          | Description |
| ------------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.UndeclareCursorCommandCompiler` |             |



### UndeclareHandlerCommandCompiler
| Component Class Name                                           | Description |
| -------------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.UndeclareHandlerCommandCompiler` |             |



### UndeclareSSHCommandCompiler
| Component Class Name                                       | Description |
| ---------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.UndeclareSSHCommandCompiler` |             |



### UndeclareStatementCommandCompiler
| Component Class Name                                             | Description |
| ---------------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.UndeclareStatementCommandCompiler` |             |



### UniversalScriptAnalysis
| Component Class Name                           | Description |
| ---------------------------------------------- | ----------- |
| `cn.org.expect.script.compiler.ScriptAnalysis` |             |



### UniversalScriptChecker
| Component Class Name                          | Description |
| --------------------------------------------- | ----------- |
| `cn.org.expect.script.compiler.ScriptChecker` |             |



### UniversalScriptCompiler
| Component Class Name                           | Description |
| ---------------------------------------------- | ----------- |
| `cn.org.expect.script.compiler.ScriptCompiler` | 即时编译器  |



### UniversalScriptConfiguration
| Component Class Name                                | Description |
| --------------------------------------------------- | ----------- |
| `cn.org.expect.script.internal.ScriptConfiguration` |             |



### UniversalScriptContextAware
| Component Class Name                                          | Description |
| ------------------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.ExecuteFunctionCommandCompiler` |             |
| `cn.org.expect.script.command.VariableMethodCommandCompiler`  |             |



### UniversalScriptEngineFactory
| Component Class Name                                | Description |
| --------------------------------------------------- | ----------- |
| `cn.org.expect.script.UniversalScriptEngineFactory` |             |



### UniversalScriptFormatter
| Component Class Name                            | Description |
| ----------------------------------------------- | ----------- |
| `cn.org.expect.script.internal.ScriptFormatter` |             |



### UniversalScriptSessionFactory
| Component Class Name                          | Description      |
| --------------------------------------------- | ---------------- |
| `cn.org.expect.script.session.SessionFactory` | 脚本引擎会话工厂 |



### UnrarCommandCompiler
| Component Class Name                                | Description |
| --------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.UnrarCommandCompiler` |             |



### UnzipCommandCompiler
| Component Class Name                                | Description |
| --------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.UnzipCommandCompiler` |             |



### VariableMethodCommandCompiler
| Component Class Name                                         | Description |
| ------------------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.VariableMethodCommandCompiler` |             |



### VariableMethodRepository
| Component Class Name                                   | Description |
| ------------------------------------------------------ | ----------- |
| `cn.org.expect.script.method.VariableMethodRepository` |             |



### WaitCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.WaitCommandCompiler` |             |



### WcCommandCompiler
| Component Class Name                             | Description |
| ------------------------------------------------ | ----------- |
| `cn.org.expect.script.command.WcCommandCompiler` |             |



### WgetCommandCompiler
| Component Class Name                               | Description |
| -------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.WgetCommandCompiler` |             |



### WhileCommandCompiler
| Component Class Name                                | Description |
| --------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.WhileCommandCompiler` |             |



### XmlExtension
| Component Class Name                       | Description |
| ------------------------------------------ | ----------- |
| `cn.org.expect.script.method.XmlExtension` |             |



### ZipCommandCompiler
| Component Class Name                              | Description |
| ------------------------------------------------- | ----------- |
| `cn.org.expect.script.command.ZipCommandCompiler` |             |



### ZipCompress
| Component Class Name                 | Description |
| ------------------------------------ | ----------- |
| `cn.org.expect.compress.ZipCompress` |             |



### Compress
**Container use component factory `cn.org.expect.compress.CompressFactory` to create instance**
| Component Class Name                  | Description |
| ------------------------------------- | ----------- |
| `cn.org.expect.compress.ZipCompress`  |             |
| `cn.org.expect.compress.TarCompress`  |             |
| `cn.org.expect.compress.RarCompress`  |             |
| `cn.org.expect.compress.GzipCompress` |             |



### ExtractWriter
**Container use component factory `cn.org.expect.database.export.ExtractWriterFactory` to create instance**
| Component Class Name                                      | Description                                                                                                                                                                                                                                                  |
| --------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `cn.org.expect.database.export.inernal.FtpFileWriter`     | 卸载数据到远程ftp服务器<br>ftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径                                                                                                                                                               |
| `cn.org.expect.database.export.inernal.HttpRequestWriter` | 卸载数据到用户浏览器<br>http://download/HttpServletRequest 对象的变量名/HttpServletResponse对象的变量名/下载文件名（需要提前将 HttpServletRequest 对象与 HttpServletResponse 对象保存到脚本引擎变量中，变量分别是: httpServletRequest, httpServletResponse） |
| `cn.org.expect.database.export.inernal.ExtractFileWriter` | 卸载数据到本地文件                                                                                                                                                                                                                                           |
| `cn.org.expect.database.export.inernal.SftpFileWriter`    | 卸载数据到远程sftp服务器<br>sftp://用户名@远程服务器host:端口?password=登陆密码/数据文件存储路径                                                                                                                                                             |



### OS
**Container use component factory `cn.org.expect.os.OSFactory` to create instance**
| Component Class Name                   | Description |
| -------------------------------------- | ----------- |
| `cn.org.expect.os.macos.MacOS`         |             |
| `cn.org.expect.os.linux.LinuxLocalOS`  |             |
| `cn.org.expect.os.linux.LinuxRemoteOS` |             |



### TextTableFile
**Container use component factory `cn.org.expect.io.TextTableFileFactory` to create instance**
| Component Class Name                       | Description                                                                     |
| ------------------------------------------ | ------------------------------------------------------------------------------- |
| `cn.org.expect.io.CsvFile`                 | CSV格式文件                                                                     |
| `cn.org.expect.database.db2.DB2ExportFile` | DB2数据库export命令导出文件格式, 逗号分隔，双引号转义字符，双引号是字符串限定符 |
| `cn.org.expect.io.CommonTextTableFile`     | 文本文件, 逗号分隔，无转义字符，无字符串限定符                                  |



### ThreadSource
**Container use component factory `cn.org.expect.ioc.impl.ThreadSourceFactory` to create instance**
| Component Class Name                        | Description |
| ------------------------------------------- | ----------- |
| `cn.org.expect.concurrent.EasyThreadSource` | 线程池      |
