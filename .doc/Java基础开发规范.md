# 语言特性

采用 Java5 - Java12 特性，包括 Lambda 表达式、Stream API 和 Optional 类。

# 基本原则

代码应优先保证可读性、可维护性和边界清晰，避免为了减少行数牺牲表达力。

业务代码按职责放入对应分层包中，禁止跨层随意调用。典型调用方向为：`controller -> service -> manager -> mapper`

公共能力优先沉淀到 `common`、`util`、`bean`、`config` 等包中，禁止在业务类中复制粘贴重复逻辑。

`cn.org.expect.nas` 下第一层子包必须增加 `package-info.java`，并用一句清晰中文说明该包职责；更深层子包不要求递归增加。

# 代码格式

统一使用 UTF-8 编码。

缩进使用 4 个空格，不使用 Tab。

单行代码不宜过长，超过 120 个字符时应主动换行。

方法之间保留一个空行；字段、构造方法、普通方法按职责分组排列。

导入类禁止使用通配符导入。

类名下方如果是属性或方法，则必须空一行。静态属性不能空一行。

两个连续 } 之间不能空一行。

方法之间必须空一行。

方法与属性之间必须空一行。

```java
// 推荐

import java.util.List;

// 禁止
import java.util.*;
```

# 空值处理

方法参数必须明确是否允许为 null；

不允许为 null 的字段应在 DTO 类中字段上配置 Hibernate Validator 注解校验。

集合返回值禁止返回 null，应返回空集合: `return Collections.emptyList();`

`Optional` 仅用于表达可能为空的返回值，不作为 Entity、DTO、VO 的字段类型，也不作为方法参数类型。

# 注释规范

注释应解释业务意图、约束来源或特殊处理原因，禁止重复描述代码字面含义。

公共类、公共方法、复杂业务逻辑和包级说明必须补充必要注释。

临时代码、调试代码、无效注释和被注释掉的旧代码禁止提交。

类中属性，必须使用单行注释，注释右侧结束位置不能有逗号，句号，分号等符号；不描述 Java 类型

属性的单行注释案例：

```java
/** 是否启用, true表示启用，false表示不启用 */
private boolean isEnable;
```

类中属性的 `set/get` 方法不用写注释。

多行注释中每一行下面保留一个空行。

注释的右侧不能使用句号等符号

方法必须编写 Javadoc，简洁说明功能、参数含义、返回值及异常情况，不要生成冗余的注释内容。

复杂算法或业务逻辑必须添加行内注释解释“为什么这么做”，而不仅仅是“做了什么”。

方法 Javadoc 建议包含: @param @return @throws（有异常时）

复杂业务代码优先解释设计意图，而不是逐行翻译代码。

TODO、FIXME 使用统一格式：

```java
// TODO(v1.1): 支持批量导入
// FIXME: 修复空指针问题
```

# Java 包名

Java 包名全小写，使用点分隔，禁止下划线，多级结构使用点分隔符。

Java 包的根名使用：`cn.org.expect`

# 类名

类名采用大驼峰（UpperCamelCase），名词为主（如 UserService、OrderController）。

# 方法名

方法名使用小驼峰（LowerCamelCase），动词开头，语义明确（如 getUserById、calculateTotalPrice）。

布尔类型返回值的方法建议以 is、has、can、should 开头（如 isValid、hasPermission）。

执行实例方法时，实例方法前必须加 `this.` 前缀。

# 变量名

局部变量、成员变量采用小驼峰，语义清晰，严禁单字母命名（除循环计数器 i、j、k 外）。

集合与数组变量，使用复数形式（如 userList、orderMap）。

在 Service 层注入 Mapper 时，变量名统一为 mapper；

Controller 层注入 Service 时，变量名统一为 service 或具体业务名（如 userService）。

在方法中使用实例变量时，实例变量名前必须加 `this.` 前缀。

常量名: 全部大写 + 下划线分隔。

```java
public static String LOGIN_FAIL_PASSWD = "密码错误";
```

# 集合操作

遍历集合优先使用 for-each 或 Stream API，避免在 foreach 中修改集合结构。

集合判空优先使用 Hutool 或统一工具方法。

Stream 链式调用不宜过长，复杂逻辑应拆分为局部变量或私有方法。

# 并发编程规范

禁止使用 `new Thread()` 创建新线程，从 Ioc 容器中取线程池，使用公共线程池运行并发任务。

异步任务必须考虑异常处理、超时控制和上下文传递。
