# 功能介绍

通用脚本引擎（以下简称脚本引擎）基于 **Java** 语言实现，具备以下核心功能：

- 支持通过实现自定义 **命令编译器** 扩展业务指令；
- 支持通过定义自定义 **变量方法** 扩展变量解析和处理能力；
- 支持执行 **SQL DML（数据操作语言）** 与 **DDL（数据定义语言）** 语句，操作关系型数据库；
- 支持将**数据文件解析并装载至数据库表**；
- 支持从**数据库表导出数据至文件**；
- 支持**文件剥离增量处理机制**，并可按需实现自定义文件格式解析器；
- 支持**远程主机连接**，在远程服务器上执行 **Shell 命令**；
- 支持**多线程并发执行任务**；
- 兼容 **JDK5** 及以上版本；
- 提供对 **JSR 223: Scripting for the Java Platform** 标准接口 `javax.script.ScriptEngineManager` 与 `javax.script.ScriptEngine` 的实现，支持通过标准化方式集成脚本引擎。




# 使用方法



## 引入依赖

**JDK5** 环境下，在项目的 `pom.xml` 文件中添加以下依赖：

```xml
<dependency>
  <groupId>{0}</groupId>
  <artifactId>{1}</artifactId>
  <version>{2}</version>
</dependency>
```

**JDK6** 环境下，在项目的 `pom.xml` 文件中添加以下依赖：

```xml
<dependency>
  <groupId>{0}</groupId>
  <artifactId>{1}-engine</artifactId>
  <version>{2}</version>
</dependency>
```



## 运行示例

基于 **JDK6**（**JSR-223**）脚本引擎标准**API**的调用：


```java
public class Main {
    public static void main(String[] args) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("etl");
        engine.eval("echo hello world!");
    }
}
```

基于工厂模式的脚本引擎实例化与执行：


```java
public class Main {
    public static void main(String[] args) {
        EasyContext context = new EasyBeanContext();
        UniversalScriptEngineFactory factory = new UniversalScriptEngineFactory(context);
        UniversalScriptEngine engine = factory.getScriptEngine();
        engine.eval("echo hello world!");
    }
}
```



##  SpringBoot场景启动器

脚本引擎可作为 **Spring Boot Starter** 自动装配模块集成至项目中，随着 **Spring Boot 应用上下文** 启动自动初始化，引擎实例可通过依赖注入获取，进一步简化创建与使用流程。



### 引入依赖

在 **Spring Boot 项目** 的 **pom.xml** 文件中配置如下依赖项：

```xml
<dependency>
    <groupId>{0}</groupId>
    <artifactId>{3}-spring-boot-starter</artifactId>
    <version>{2}</version>
</dependency>
```

上述 **Starter 模块** 内部已集成脚本引擎的配置与生命周期管理，开发者无需手动初始化，依赖注入后即可直接使用。



### 场景示例

基于 **Spring MVC** 构建接口服务，通过注入脚本引擎实例动态执行脚本：

```java
@Controller
public class HelloController {

    @Autowired
    private ScriptEngine engine;
  
    @Autowired
    private EasyContext context;

    @RequestMapping("/help")
    @ResponseBody
    public String help() throws ScriptException, IOException {
      engine.eval("echo hello world!");
      return "success";
    }
}
```



### 属性

在编写脚本时，脚本引擎支持读取 **Spring Boot 配置文件**（如 `application.properties` 和 `application.yaml`），包括按环境分隔的配置。



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

脚本引擎容器 `{28}`：单例作用域（`@Scope("singleton")`），负责全局脚本环境管理；

脚本引擎实例 `{91}`：请求作用域（`@Scope("request")`），每次请求生成一个新的引擎实例。



## ETL示例

在 `resources` 目录下新建脚本文件 `script/test_etl.sql`

```sql
# 设置变量值
set databaseDriverName="com.ibm.db2.jcc.DB2Driver"
set databaseUrl="jdbc:db2://127.0.0.1:50000/sample"
set username="db2inst1"
set password="db2inst1"

# 打印所有内置变量
set

# 建立数据库连接信息
declare DBID catalog configuration use driver $databaseDriverName url "${databaseUrl}" username ${username} password $password

# 连接数据库
db connect to DBID

# quiet命令会忽略DROP语句的错误
quiet drop table v_test_tab;

# 建表
CREATE TABLE v_test_tab (
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
commit;

INSERT INTO v_test_tab
(ORGCODE, TASK_NAME, TASK_FILE_PATH, FILE_DATA, CREATE_DATE, FINISH_DATE, STATUS, STEP_ID, ERROR_TIME, ERROR_LOG, OPER_ID, OPER_NAME)
VALUES('0', '1', '/was/sql', '2021-02-03', '2021-08-09 23:54:26.928000', NULL, '1', '使用sftp登录测试系统服务器', '2021-08-09 23:47:02.197000', '设置脚本引擎异常处理逻辑', '', '');

INSERT INTO v_test_tab
(ORGCODE, TASK_NAME, TASK_FILE_PATH, FILE_DATA, CREATE_DATE, FINISH_DATE, STATUS, STEP_ID, ERROR_TIME, ERROR_LOG, OPER_ID, OPER_NAME)
VALUES('1', '2', '/was/test', '2021-02-03', '2021-08-09 23:54:26.928000', NULL, '1', '使用sftp登录测试系统服务器', '2021-08-09 23:47:02.197000', '使用sftp登录测试系统服务器', '', '');
commit;

# 创建索引
quiet drop index vtesttabidx01;
create index vtesttabidx01 on v_test_tab(ORGCODE,error_time);
commit;

# 将表中数据卸载到文件中
db export to $temp/v_test_tab.del of del select * from v_test_tab;

# 将数据文件装载到指定数据库表中
db load from $temp/v_test_tab.del of del replace into v_test_tab;

# 返回0表示脚本执行成功
exit 0
```

运行脚本文件：

```JAVA
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Assert;
import org.junit.Test;

public class ScriptEngineTest {

    @Test
    public void test() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("etl");
        engine.eval(". classpath:/script/test_etl.sql");
    }
}
```



## 存储过程示例

可以作为存储过程使用，首先建立脚本文件 `/script/test_procedure.sql`，内容如下所示： 

```sql
# 设置变量值
set databaseDriverName="com.ibm.db2.jcc.DB2Driver"
set databaseUrl="jdbc:db2://127.0.0.1:50000/sample"
set username="db2inst1"
set password="db2inst1"

# 打印所有内置变量
set

# 建立数据库连接信息
declare DBID catalog configuration use driver $databaseDriverName url "${databaseUrl}" username ${username} password $password

# 连接数据库
db connect to DBID

# 建立异常捕获逻辑
declare continue global handler for errorcode == -601 begin
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

运行脚本文件：

```java
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Assert;
import org.junit.Test;

public class ScriptEngineTest {

    @Test
    public void test() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("etl");
        engine.eval(". classpath:/script/test_produce.sql");
    }
}
```



## 脚本示例

```sql
# 设置变量值
set databaseDriverName="com.ibm.db2.jcc.DB2Driver"
set databaseUrl="jdbc:db2://127.0.0.1:50000/sample"
set username="db2inst1"
set password="db2inst1"

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
declare progress use out print '插入数据库记录 ${process}%, 一共${totalRecord}笔记录 ${leftTime}' total ${v_test_tmp_records} times

# 逐条插入数据
set i=1
while $i <= $v_test_tmp_records loop
  set c1 = "$i"
  set c2 = "机构$i"
  set c3 = "机构类型$i"
  set c4 = "编号$i"
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
$ ssh ${admin}@${host}:22?password=${adminPw} \
&& export LANG=zh_CN.GBK \
&& db2 connect to ${databaseName} user ${username} using ${password} \
&& db2 "load client from /dev/null of del replace into v10_test_tmp " \
&& db2 "load client from `pwd`/v_test_tmp.del of del replace into v_test_tmp " \
&& db2 connect reset;
```



## sftp示例

```shell
sftp ${ftpuser}@${ftphost}:22?password=${ftppass}
  set ftphome=`pwd`
  ls ${ftphome}
  set remotetestdir="${ftphome}/test"
  rm ${remotetestdir}
  mkdir ${remotetestdir}
  cd ${remotetestdir}
  put `pwd -l`/test.sql
  ls
  exists ${remotetestdir}/test.sql
  isfile ${remotetestdir}/test.sql
  mkdir ${ftphome}/test
  rm ${ftphome}/test
  get ${remotetestdir}/test.sql ${temp}
  exists -l ${temp}/test.sql
bye
```



## ftp示例

```shell
ftp ${ftpuser}@${ftphost}:21?password=${ftppass}
  set ftphome=`pwd`
  set remotetestdir="${ftphome}/rpt"
  pwd
  rm ${remotetestdir}
  mkdir ${remotetestdir}
  exists ${remotetestdir}/
  ls ${remotetestdir}
  cd ${remotetestdir}
  put $temp/test.sql ${remotetestdir}
  ls ${remotetestdir}
  exists ${remotetestdir}/test.sql
  isfile ${remotetestdir}/test.sql
  mkdir ${ftphome}/test
  isDirectory ${ftphome}/test
  rm ${ftphome}/test
  get ${remotetestdir}/test.sql ${temp}
  exists -l ${temp}/test.sql
bye
```



# 编译方法

将工程根目录下的 `toolchains.xml` 文件复制到本地 Maven 仓库的根目录下（通常是 `~/.m2` 目录）。

然后编辑 `toolchains.xml` 文件，将其中的 `jdkHome` 路径修改为实际安装的 JDK 目录。

执行 `mvn install`



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
- **db**：配置或切换数据库连接。



### 网络操作类命令

- **os**：远程操作系统命令执行。
- **ftp**：FTP 文件传输。
- **sftp**：SFTP 文件安全传输。



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
- **命令替换符 \`command\`**：执行嵌套命令，并将其输出作为当前命令的输入。



## 自定义命令

在脚本引擎中，可以通过 **接口实现** 或 **模版继承** 两种方式实现自定义命令，满足业务扩展需求。

### 接口方式

- 实现 `{93}` 接口（代表一个标准的命令实现）；
- 在实现类上标注 `{50}` 注解（表明该类是一个可被引擎识别的命令）；

### 模版方式

为了简化开发流程，脚本引擎提供了一系列 **命令模版基类**。

开发人员可继承模版类，复写业务逻辑，大幅减少开发工作量。

使用方式：

- 继承适合的模版类，实现具体业务逻辑。
- 在命令类上配置 `{50}` 注解，使其成为可用命令。

### 命令模版分类

| 模版类型               | 基类名称 | 适用场景                                           |
| ---------------------- | -------- | -------------------------------------------------- |
| 带日志输出的命令模版   | `{110}`  | 需要在执行过程中生成日志，支持将日志输出至文件。   |
| 不带日志输出的命令模版 | `{111}`  | 普通功能型命令，无需日志记录。                     |
| 支持文件操作的命令模版 | `{112}`  | 命令涉及文件读写、目录操作等，如 `cat`、`rm`。     |
| 支持全局功能的命令模版 | `{113}`  | 适合执行全局级别配置或环境初始化相关操作。         |
| 主从关系的命令模版     | `{114}`  | 命令存在父子或主从关系，例如容器类命令或块级命令。 |

### 附加扩展接口

根据业务复杂度，可选择实现以下接口，为命令提供额外能力：

| 接口名称 | 功能描述       | 典型应用场景                                      |
| -------- | -------------- | ------------------------------------------------- |
| `{115}`  | 支持管道操作   | 命令与管道符 `                                    |
| `{116}`  | 控制循环体     | 影响 `while`、`for` 等循环行为。                  |
| `{117}`  | 异步并发运行   | 支持并发任务处理，例如 `nohup`。                  |
| `{118}`  | 回调函数       | 任务执行过程中触发回调。                          |
| `{119}`  | 在循环体中使用 | 可嵌套在 `while`、`for` 结构内执行。              |
| `{121}`  | 跳跃执行       | 配合 `jump` 命令，实现跳过特定命令块。            |
| `{122}`  | 命令块         | 支持成组命令，例如：`while`、`for` 块内多条语句。 |



## 已注册命令

{55}



# 变量方法

**变量方法**是指在脚本执行过程中，通过 **变量名.方法名()** 的方式，对变量值进行操作或获取信息的调用方式。

示例：

```javascript
set str = "12345 ";
set strtrim = str.trim();
echo "字符串内容是 $strtrim .."
```

上述例子中，`str.trim()` 便是变量方法的调用，它调用了 `String` 类的 `trim()` 方法，对变量 `str` 的值去除首尾空白符。

## 调用说明

- 变量方法可以调用 **Java 类的公共方法**（`public` 方法），如 `substring()`、`length()` 等。
- 自定义类型变量也可以调用其对应 Java 类中的方法。
- 支持脚本扩展自定义变量方法，以满足特定业务需求。



## 内置变量

脚本引擎内置了一批系统级变量，可在脚本执行过程中直接访问：

| 变量标识 | 描述                                                      |
| -------- | --------------------------------------------------------- |
| `{21}`   | 当前脚本引擎实例对象。                                    |
| `{4}`    | 当前执行目录的绝对路径。                                  |
| `{41}`   | 当前用户的根目录路径。                                    |
| `{5}`    | 当前正在执行的脚本文件名。                                |
| `{6}`    | 当前脚本文件的字符集编码。                                |
| `{8}`    | 脚本文件的行间分隔符，变量值为 `{9}`。                    |
| `{10}`   | 最近一次异常的堆栈信息。                                  |
| `{11}`   | 最近一次异常发生时的脚本语句。                            |
| `{12}`   | 数据库厂商定义的异常错误码。                              |
| `{13}`   | 数据库厂商定义的 SQL 状态码。                             |
| `{14}`   | 最近一次执行脚本的状态码。                                |
| `{15}`   | 最近一次 SQL 语句影响的数据记录数。                       |
| `{16}`   | 当前是否处于 `jump` 语句执行过程中，`true` 表示正在执行。 |
| `{17}`   | 上一个 `step` 命令的参数值。                              |
| `{18}`   | 系统临时文件目录路径。                                    |
| `{19}`   | 当前执行脚本的绝对路径。                                  |
| `{20}`   | 最近一次使用的数据库编目名。                              |



## 自定义变量方法

脚本引擎支持 **两种方式** 扩展变量方法，满足个性化操作变量的需求。

### 接口方式

通过实现标准接口，定义变量方法的行为：

- 实现 `{54}` 接口，该接口约定变量方法的执行逻辑。
- 类上标注 `{52}` 注解，注册为变量方法。
- 在注解 `{53}` 的 `name` 属性指定变量方法名称，`variable` 属性指定方法适用的变量类型。

示例

实现一个数组打印方法：

```java
@{53}(name = "print", variable = Object[].class)
public class PrintMethod implements {54} {
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
| `{54}`      | 变量方法执行接口，定义方法调用逻辑。   |
| `{52}`      | 标记自定义变量方法类，使引擎识别加载。 |
| `{53}.{57}` | 描述变量方法的元信息：名称             |

### 静态方法方式

通过 **静态方法** 方式实现变量方法，更加简洁直观：

开发步骤：

- 在类上标注 `{58}` 注解，声明该类为变量方法提供者。
- 在类中定义静态方法：
  - 第一个参数：变量本身。
  - 后续参数：变量方法的调用参数。

示例

实现 JSON 数据操作方法：

```java
@{59}
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

{56}



# 内部设计



## 脚本引擎容器

脚本引擎容器类似于 **Spring** 容器的概念，用来管理组件。

脚本引擎启动前，需要先启动一个脚本引擎容器类 `{28}` 的实例对象。

脚本引擎容器包含：类扫描器、组件信息表、组件工厂、容器等。



### 类扫描器

当脚本引擎容器启动时，会使用类扫描器扫描指定 **Java** 包中的类，将扫描到的类存储到**组件信息表**中。

可通过参数指定扫描规则：

```java
System.setProperty("{70}", "{0},!org.apache"); // 包名前面使用叹号，表示排除该包名下的所有类
```

可在创建 `{42}` 类的实例对象时，通过参数设置扫描规则：

```java
{42} ioc = new {42}(
        "sout+:info", // 默认日志级别
        "cn.org.expect.io:debug", //
        "cn.org.expect.ioc:trace" //
);
```

类扫描器的实现类是 `{107}`，扫描规则详见 `{108}` 接口的实现类。

类扫描器默认只扫描被注解 `@{22}`、`@{51}`、`@{53}` 标记的类，如果想增加扫描规则，则可以在**SPI**配置文件 `resources/META-INF/services/{102}` 中增加 `{108}` 接口的实现类。



### 组件信息

组件信息接口是：`{38}`

类扫描器扫描到一个被注解 `{29}` 标记的类后，会将该类信息转为接口 `{38}` 的实例对象。

随后容器会对所有必要的组件进行初始化操作，然后等待其他功能调用组件。



### 组件信息表

组件信息表类是：`{36}`

用于存储容器中所有组件，存储格式是组件类上的接口类信息与组件类信息的映射。

从脚本引擎容器中查找指定组件时，会根据接口或类信息在组件信息表中查找对应的组件。



### 组件工厂

组件工厂接口是：`{37}`

如果有组件需要使用工厂模式创建，则可以在组件工厂接口 `{37}` 实现类上并标注注解 `{22}`。

脚本引擎容器会使用这个组件工厂实例来创建该组件。



## 脚本引擎工厂 

脚本引擎工厂接口是 `{90}`，用于创建脚本引擎实对象。



## 脚本引擎配置信息

脚本引擎配置信息接口是  `{98}`，用于管理脚本引擎基本属性信息。



## 脚本引擎

脚本引擎类是 `{91}`，是所有脚本语句的运行器。



## 脚本引擎上下文信息 

脚本引擎上下文信息类是 `{92}`，用于管理脚本引擎运行中产生的变量与程序。



## 编译脚本

脚本引擎编译器接口是 `{109}`，可以将脚本语句编译为可以执行的脚本命令。

### 编译器组成

语法分析器 `{94}`

语句输入流 `{95}`

语句分析器 `{96}` 

命令编译器 `{93}` 

### 编译过程

在类扫描阶段，如果类配置了 `{50}` 注解，并且该类也实现了接口 `{93}`，则将这个（命令编译器）类实例化。

将上一步生成的命令编译器的实例对象，交给脚本引擎编译器管理和使用。

脚本引擎编译器对某一个脚本语句进行编译时，会逐个执行命令编译器实例上的 `{106}` 方法，来判断脚本语句应该使用哪个命令编译器来执行编译操作。

找出脚本语句对应的命令编译器后，会执行该命令编译器上的 `{103}` 方法来读取一个完整的语句。

再执行命令编译器上的 `{104}` 方法，对上一步得到的脚本语句进行编译，得到一个脚本命令实例。



## 运行命令

经过脚本引擎编译器编译后，会得到一个脚本命令对象，即 `{99}` 接口的实例对象。

运行实例上的 `{105}` 方法（即执行该命令的业务逻辑），会得到一个返回值与状态码。

根据上一步得到的状态码，判断该命令运行的是否成功。

如果状态码是零，表示命令运行成功，读取下一个命令并运行。

如果状态码是非零，表示命令运行错误，立即抛出异常（可用 `set -E` 命令来设置不抛出异常）。



## 国际化信息

脚本引擎加载国际化资源文件的顺序如下：

### 约定资源文件

脚本引擎默认先加载中文国际化资源文件：`{84}.properties`

你可以通过 JVM 参数设置加载英文资源文件：`{84}_en_US.properties`

```java
java -D{83}=en_US cn.org.expect.Modest
```

### 自定义资源文件

如果已在工程中使用了自定义资源文件 `com.test.Message_en_US.properties`，则可通过 JVM 参数配置自定义的资源名：

```shell
java -D{83}=en_US -D{82}=com/test/Messages cn.org.expect.Modest
```

### 外部资源文件

可以通过 JVM 参数设置外部的国际化资源文件路径：

```shell
java -D{75}=/home/user/project/resources/Messages.properties cn.org.expect.Modest
```

### 工具类

国际化资源工具类是 `{101}`，通过该类可以方便地获取国际化资源文件中的配置项。

例如：

```java
ResourcesUtils.getMessage("date.stdout.message003")
```



## 表达式支持

支持在 `set`，`if`，`while` 等命令中使用表达式进行计算。

开发人员可以使用类 `{63}` 完成如下表达式运算：

算数运算 `() +`(正)`-`(负) `*`(乘) `/`(除) `%`(取余) `+`(加) `-`(减) 

三目运算 `?:`

布尔运算 `< <= > >= == !=`

逻辑运算 `&& || and or`

范围运算 `in` 与`not in` 运算符的返回值是布尔值，判断变量是否在设置的范围内，操作符右侧是小括号，小括号内的元素用符号 {68} 分割。

取反运算 `!` 只支持对布尔值进行取反



## 日志输出

脚本引擎容器启动时，会检查类路径下是否有 **Slf4j** 日志组件。

如果在类路径中检测到 **Slf4j** 相关jar包，则直接使用 **Slf4j** 作为日志输出接口。

如果在类路径中未检测到 **Slf4j** 相关jar包，则默认使用控制台 `System.out` 输出日志。

当使用控制台输出日志时，可配置如下参数设置日志的输出级别与输出格式：

```java
System.setProperty("{71}", "debug"); // 设置日志输出级别
System.setProperty("{72}", "{81}"); // 设置用控制台输出日志，且指定了日志的输出格式
```

设置打印日志堆栈跟踪信息（用于确定 `fqcn` 值）：

```java
System.setProperty("{78}", "true");
```

设置打印数据库操作日志（可以显示执行的类名、方法名、参数值、返回值）：

```java
System.setProperty("{79}", "true");
```



## 字符集

脚本引擎内部默认使用的字符集编码是 `file.encoding` 属性值，也可通过如下参数修改默认的字符集：

```java
System.setProperty("{73}", "UTF-8");
```

可以通过脚本命令来设置脚本文件、外部文件（如 `tail`、`head` 等命令的输入文件）的字符集：

```shell
set {6}=GBK
. /home/user/script.etl
```



## 临时文件

脚本引擎产生的临时文件，默认存储在 `${java.io.tmpdir}/{35}` 路径下

可通过参数修改临时文件存储的默认目录：

```java
System.setProperty("{77}", "/home/user/temp/");
```

在编程时可通过如下代码，得到临时文件的存储目录：

```java
{43}.{44};
```



## 数据库方言

因为不同品牌数据库（或同品牌不同版本），其语法与功能实现各不相同，可通过数据库方言接口 `{26}` 来统一操作数据库的接口。但是需要为不同品牌（或同品牌的不同版本）的数据库开发不同的方言实现类。

在脚本引擎中数据库相关的命令，都是通过 **JDBC** 接口实现的。

在使用数据库相关命令前，需要先将数据库的 **JDBC** 驱动包加入到 **classpath** 下。



### 已有方言类

已注册的数据库方言如下所示：

{27}



### 开发方言类

可以通过自定义数据库方言的方式来增加对其他品牌据库的支持。

例如：想要增加对 `informix` 数据库的支持，如下所示需要新建并实现数据库方言类，且在该类上配置注解 `{29}` 。

```java
@{22}(name = "informix")
public class InformixDialect extends {25} implements {26} {
	...
}
```

可以针对同一数据库的不同版本，开发对应的数据库方言类。

因为在同一个品牌数据库的不同版本中，同一个功能的实现也可能不同，这时可针对数据库的特殊版本增加方言类，如下所示：

```java
@{22}(name = "db2")
public class DB2Dialect115 extends {33} implements {30} {

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

在编写代码时，可通过容器的 `{28}.{39}` 方法得到数据库方言的实例对象，如下所示：

```java
@{22}
public class JdbcTest1 {

    /** 容器上下文信息 */
    private {32} context;
  
    /** 数据库连接 */
    private Connection conn;

    /**
     * 初始化
     *
     * @param context   容器上下文信息
     * @param conn      数据库连接
     */
    public JdbcTest1({32} context, Connection conn) {
        this.context = context;
        this.conn = conn;
    }

    /**
     * 返回数据库方言
     *
     * @return 数据库方言
     */
    public {30} getDialect() {
        return this.context.getBean({30}.class, this.conn);
    }
}
```

在上面这个案例中，容器方法 `{28}.{39}` 的第一个参数是数据库方言接口，第二个参数是一个有效的数据库连接。

容器会根据数据库连接信息中的数据库缩写与版本号，查找对应的数据库方言。

```java
@{22}
public class JdbcTest2 {

    /** 容器上下文信息 */
    @{22}
    private {32} context;

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
    public {30} getDialect() {
        return this.context.getBean({30}.class, this.url);
    }
}
```

在上面这个案例中，容器自动注入了容器上下文信息，即 `context` 实例。

容器方法 `{28}.{39}` 的第一个参数是数据库方言接口，第二个参数是 **JDBC** 的 **URL**，容器会根据URL中的数据库信息，查找（规则详见 `{31}`）对应的数据库方言。



### 匹配规则

先根据数据库简称，遍历已注册数据库方言类上 `@{22}` 注解的 `name()	` 值，查找匹配的方言类。

如果在上一步查找匹配到多个数据库方言类（存在不同版本的方言类），则优先使用大版本号、小版本号与数据库进行匹配；

如果在上一步中，不能匹配到对应版本号的方言类时，则会使用未设置版本号（即`{30}.getDatabaseMajorVersion()`与`{30}.getDatabaseMinorVersion()`方法返回 null 或空字符）的数据库方言。

如果在上一步中，所有数据库方言类都设置了版本号，则优先返回版本号最接近的方言类。

具体的匹配规则详见类：`{31}`



## 输出接口

- 标准信息输出接口；
- 错误信息输出接口；
- 进度信息输出接口；



## 类型转换器

类型转换器  `{97}`，用于将 JDBC 查询结果集返回值转为脚本引擎内部使用的类型。



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

- 将 XML 文件存储到 Java 工程 `{86}` 包中，文件名必须为：`holidays.xml`

- 将 XML 文件存储到 `{87}`，文件名必须遵循命名规则：`holiday*.xml`

- 通过 JVM 参数指定 XML 文件所在目录，文件名必须遵循命名规则：`holiday*.xml`

  ```shell
  java -D{85}=/home/user/locale/ cn.org.expect.Modest
  ```

完成 XML 配置后，重启服务或通过以下代码加载新的国家法定假日配置文件：

```java
Dates.HOLIDAYS.reload();
```



## 其他配置

输入流缓冲区字符数组的长度：

```java
System.setProperty("{74}", "10000");
```

输入流缓冲区字节数组的长度：

```java
System.setProperty("{76}", "10000");
```

设置 **Linux** 操作系统内置账户名（用于过滤操作系统内置账户信息）：

```java
System.setProperty("{80}", "daemon,apache");
```



## 组件附录

脚本引擎容器中已注册的组件如下所示：



{150}
