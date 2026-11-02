# 项目规则

## 通用要求

- 使用中文回复。
- 必须阅读并严格遵守: 《~/Documents/workspace/modest-document/开发规范/Java基础开发规范.md》
- 必须阅读《README.md》了解模块设计
- 新增、修改程序后，不要在 `README.md` 文件或其他文件中增加说明，只能在 `~/Documents/workspace/modest-document/Codex` 目录中增加 markdown 文件说明本次新增的内容与使用说明，文件名: `工程名 - 十四位日期时间 - 业务名称.md`，如 `modest-20230704111231-增加k8s部署.md`

## 编译

编译工程使用 `~/.m2/jdks/jdk-17.0.2.jdk`

## 测试

使用 Docker Compose 启动测试依赖的 ssh、sftp、ftp、mysql、高斯、db2 数据库等容器
