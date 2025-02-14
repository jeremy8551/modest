echo 当前目录 `pwd`, 当前时间 `date`

declare global test0001 catalog configuration use host ${databaseHost} driver $databaseDriverName url "${databaseUrl}" username ${username} password $password ssh.username ${databaseSSHUser} ssh.password ${databaseSSHUserPw} ssh.port 22
db connect to test0001

echo 测试导出数据文件功能
quiet "drop table v7_test_tmp";
create table v7_test_tmp (
	branch_id char(6) not null,
	branch_name varchar(150),
	branch_type char(18),
	branch_no char(12),
	status char(1),
	money  decimal(12,2),
	dt     DATE,
	dttime TIMESTAMP,
    intval INTEGER,
	primary key(branch_id)
);
commit;


DECLARE sname Statement WITH insert into v7_test_tmp (branch_id, branch_name, branch_type, branch_no, status, money, dt, dttime, intval) values (?, ?, ?, ?, ?, ?, ?, ?, ?) ;
declare progress use out print '插入数据库记录 ${process}%, 一共${totalRecord}笔记录 ${leftTime}' total 100000 times
set tcount=0
while $tcount <= 100 loop
  set c1 = "$tcount"
  set c2 = "机构$tcount"
  set c3 = "机构类型$tcount"
  set c4 = "编号$tcount"
  set c5 = "0"
  set c6 = "17000.00"
  set c7 = "2010-01-02"
  set c8 = "2010-01-02-10.12.07.000000"
  set c9 = "12345"

  FETCH c1, c2, c3, c4, c5, c6, c7, c8, c9 insert sname;
  progress

  set tcount = $tcount + 1
end loop
undeclare sname Statement
commit

set records = select count(*) from v7_test_tmp;
echo "表 v7_test_tmp 中共有 ${records} 记录！"

declare exportTaskId progress use out print "${taskId} 正在执行 ${process}%, 总共${totalRecord}个记录${leftTime}" total $tcount times

db export to $temp\v7_test_tmp.del of del modified by progress=exportTaskId chardel=* charhide=0 escapes=1 writebuf=200 maxrows=30041 title message=$temp/v7_test_tmp.txt select * from v7_test_tmp ;

cat $temp\v7_test_tmp.del

echo 校验数据库表中记录数与卸载后的文件行数是否相等 ..
set wcOut=`wc -l $temp\v7_test_tmp.del`
if wcOut.split()[1] != "$tcount" then
    echo tcount is $tcount
    echo wcOut is $wcOut
    exit 100
fi

echo ""


db export to $temp\v7_test_tmp1.del of del select * from v7_test_tmp ;


db connect reset
exit 0