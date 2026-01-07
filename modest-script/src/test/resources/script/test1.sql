
db connect to uddb11;
db connect reset;

if "${myname}" != 'this is good ' then
  echo "${myname}" != 'this is good '
  exit 1
fi


db connect to testdb

db connect reset 

step childPartOne



step childPartTwo
step childPart3


java cn.org.expect.script.command.JavaCommandTest '1 ' "2 2" 3 4



echo "Subscript exits script execution"
exit 0