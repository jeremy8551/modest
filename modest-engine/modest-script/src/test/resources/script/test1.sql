
db connect to uddb11;
db connect reset;

if "${myname}" != 'this is good ' then
  echo "${myname}" != 'this is good '
  exit 1
fi


db connect to testdb

db connect reset 

step 子脚本第一步



step 子脚本第二部
step 子脚本第3部


java cn.org.expect.script.command.JavaCommandTest '1 ' "2 2" 3 4



echo 子脚本退出脚本执行
exit 0