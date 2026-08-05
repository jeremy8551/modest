# modest-ssm

根据数据库设计 Excel 生成 openGauss 建表语句、MyBatis-Plus Entity、数据字典 Enum、Mapper、Dao 和 Mapper XML。
解析器通过列名定位数据，不依赖列顺序；非数据目录页会自动跳过。

```bash
java -cp modest-ssm.jar cn.org.expect.codegen.SsmCodeGeneratorMain \
  --input 数据库设计.xlsx --output generated --config generator.properties
```

配置文件可省略。完整配置示例见 `src/main/resources/generator-example.properties`。
