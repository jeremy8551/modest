DROP TABLE IF EXISTS USER_INFO;
create table USER_INFO (
  USER_ID char(20) not null COMMENT '用户编号',
  BUILD_TYPE enum('self', 'weixin') default 'self' COMMENT '账号登陆方式',
  USERNAME varchar(50) not null COMMENT '微信昵称',
  headimgurl varchar(300) not null COMMENT '微信头像地址',
  privilege varchar(100) not null COMMENT '微信权限',
  EMAIL varchar(255) not null COMMENT '邮箱地址',
  PASSWORD_HASH varchar(255) not null COMMENT '登陆密码',
  FIRST_NAME varchar(50) default null COMMENT '名',
  LAST_NAME varchar(50) default null COMMENT '姓',
  BIRTHDATE date default null COMMENT '生日',
  GENDER enum('male', 'female', 'unspecified') default 'unspecified' COMMENT '性别',
  PHONE_NUMBER varchar(20) not null COMMENT '手机号',
  ADDRESS text not null COMMENT '居住地址',
  PROVINCE varchar(100) not null COMMENT '省份',
  CITY varchar(100) not null COMMENT '城市',
  COUNTRY varchar(100) not null COMMENT '国家',
  POSTAL_CODE varchar(20) not null COMMENT '邮编',
  STATUS enum('00', '01', '02') default '00' COMMENT '用户状态', -- 00刚创建（未启用） 01启用 02停用
  CREATE_TIME timestamp default current_timestamp COMMENT '创建时间',
  UPDATE_TIME timestamp default current_timestamp on update current_timestamp COMMENT '最后更新时间',
  primary key (USER_ID)
) ;

DROP TABLE IF EXISTS ROLE_INFO;
create table ROLE_INFO (
    ROLE_ID char(10) not null COMMENT '角色编号',
    ROLE_NAME varchar(200) default '' COMMENT '角色名',
    MEMO varchar(200) default '' COMMENT '角色说明',
    ROLE_NUMBER int default 0 COMMENT '角色数量',
    ROLE_STATUS char(2) default '00' COMMENT '状态',
    primary key(ROLE_ID)
) ;

DROP TABLE IF EXISTS ROLE_USER_INFO;
create table ROLE_USER_INFO (
    ID char(80) not null COMMENT '唯一编号',
    ROLE_ID char(10) not null COMMENT '角色编号',
    ROLE_TYPE enum('00', '01') default '00' COMMENT '成员类型', -- 00用户 01岗位
    ROLE_MEMBER char(20) not null COMMENT '用户编号或岗位编号',
    primary key(ID)
) ;

DROP TABLE IF EXISTS ROLE_EXTEND_INFO;
create table ROLE_EXTEND_INFO (
    ROLE_ID char(10) not null COMMENT '角色编号',
    PARENT_ROLE char(10) not null COMMENT '继承角色编号',
    primary key(ROLE_ID)
) ;

DROP TABLE IF EXISTS ROLE_MUTEX_INFO;
create table ROLE_MUTEX_INFO (
    ID char(80) not null COMMENT '唯一编号',
    ROLE_ID char(10) not null COMMENT '角色编号',
    MUTEX_ROLE char(10) not null COMMENT '互斥角色编号',
    primary key(ID)
) ;

DROP TABLE IF EXISTS ROLE_CONDITION_INFO;
create table ROLE_CONDITION_INFO (
    ID char(80) not null COMMENT '唯一编号',
    ROLE_ID char(10) not null COMMENT '角色编号',
    CONDITION_ROLE char(10) not null COMMENT '先决条件角色编号',
    primary key(ID)
) ;

DROP TABLE IF EXISTS ROLE_API_LIST;
create table ROLE_API_LIST (
    ID char(80) not null COMMENT '唯一编号',
    ROLE_ID char(10) not null COMMENT '角色编号',
    API_URL varchar(100) not null COMMENT 'API信息',
    primary key(ID)
) ;

DROP TABLE IF EXISTS ROLE_API_SCOPE;
create table ROLE_API_SCOPE (
    ID char(80) not null COMMENT '唯一编号',
    ROLE_ID char(10) not null COMMENT '角色编号',
    API_URL varchar(100) not null COMMENT 'API信息',
    API_SCOPE char(50) not null COMMENT '范围编号', -- channel、orgcode、post
    API_SCOPE_VALUE char(10) not null COMMENT '范围格式', -- 格式: value[option1,option2]
    primary key(ID)
) ;

DROP TABLE IF EXISTS ROLE_API_RESPONSE;
create table ROLE_API_RESPONSE (
    ID char(80) not null COMMENT '唯一编号',
    ROLE char(10) not null COMMENT '角色编号',
    API_URL varchar(100) not null COMMENT 'API信息',
    CLASS_NAME varchar(300) not null COMMENT '处理规则类的全名',
    `ORDER` int default 0 COMMENT '序号',
    primary key(ID)
) ;

DROP TABLE IF EXISTS USER_DICTIONARY;
create table USER_DICTIONARY (
    ID char(80) not null COMMENT '唯一编号',
    DICT_ID char(10) not null COMMENT '字典编号',
    DICT_NAME varchar(255) not null COMMENT '字典名',
    OPTION_KEY char(10) not null COMMENT '选项编号',
    OPTION_NAME VARCHAR(50) not null COMMENT '选项名称',
    CREATE_TIME timestamp default current_timestamp COMMENT '创建时间',
    UPDATE_TIME timestamp default current_timestamp on update current_timestamp COMMENT '最后更新时间',
    primary key(ID)
) ;

DROP TABLE IF EXISTS USER_OPERATION;
create table USER_OPERATION (
    ID char(80) not null COMMENT '唯一编号',
    USER_ID char(20) not null COMMENT '用户编号',
    CREATE_TIME timestamp default current_timestamp COMMENT '创建时间',
    OPER_ID varchar(255) not null COMMENT '操作编号',
    OPER_NAME varchar(255) not null COMMENT '操作名称',
    OPER_RESULT char(1) not null COMMENT '操作结果',
    OPER_ERROR text COMMENT '异常信息',
    OPER_IP VARCHAR(50) not null COMMENT '操作IP地址',
    OPER_APP_ID CHAR(20) not null COMMENT '操作应用编号',
    OPER_APP_NAME VARCHAR(50) not null COMMENT '操作应用名称',
    ROLE_ID CHAR(20) not null COMMENT '角色编号',
    ROLE_NAME VARCHAR(255) not null COMMENT '角色名称',
    primary key(ID)
);










