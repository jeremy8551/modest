echo currentdir: `pwd`, currentdate: `date`

# 设置变量值
set properties=this.getContext().getContainer().getClassLoader().loadProperties("db2.properties", "active.test.env")
set databaseDriverName=properties.getProperty("databaseDriverName")
set databaseUrl=properties.getProperty("databaseUrl")
set username=properties.getProperty("username")
set password=properties.getProperty("password")
set databaseHost=properties.getProperty("databaseHost")
set databaseSSHUser=properties.getProperty("databaseSSHUser")
set databaseSSHUserPw=properties.getProperty("databaseSSHUserPw")

declare global test0001 catalog configuration use host ${databaseHost} driver $databaseDriverName url "${databaseUrl}" username ${username} password $password ssh.username ${databaseSSHUser} ssh.password ${databaseSSHUserPw} ssh.port 22
db connect to test0001

echo "Test export data file functionality"
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
declare progress use out print 'insert into ${process}%, total ${totalRecord} records ${leftTime}' total 100000 times
set tcount=0
while $tcount <= 100 loop
  set c1 = "$tcount"
  set c2 = "orgCode$tcount"
  set c3 = "orgType$tcount"
  set c4 = "ID$tcount"
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
echo "database table v7_test_tmp total ${records} records！"

declare exportTaskId progress use out print "${taskId} execute ${process}%, total ${totalRecord} records ${leftTime}" total $tcount times

db export to $TMPDIR\v7_test_tmp.del of del modified by progress=exportTaskId chardel=* charhide=0 escapes=1 writebuf=200 maxrows=30041 title message=$TMPDIR/v7_test_tmp.txt select * from v7_test_tmp ;

cat $TMPDIR\v7_test_tmp.del

echo "Verify whether the number of records in the database table matches the number of lines in the unloaded file. .."
set wcOut=`wc -l $TMPDIR\v7_test_tmp.del`
if wcOut.split()[1] != "$tcount" then
    echo tcount is $tcount
    echo wcOut is $wcOut
    exit 100
fi

echo ""


db export to $TMPDIR\v7_test_tmp1.del of del select * from v7_test_tmp ;


db connect reset
exit 0