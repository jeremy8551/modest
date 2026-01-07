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

set

# 复制文件
set delfilepath="$TMPDIR/bhc/bhc_finish.del"
rm ${delfilepath}
mkdir $TMPDIR/bhc
cp classpath:/bhc_finish.del $TMPDIR/bhc

declare global test0001 catalog configuration use host ${databaseHost} driver $databaseDriverName url "${databaseUrl}" username ${username} password $password ssh.username ${databaseSSHUser} ssh.password ${databaseSSHUserPw} ssh.port 22
db connect to test0001

-- 测试数据装载功能
quiet drop table bhcp_finish;
CREATE TABLE bhcp_finish (
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
quiet drop index bhcpfinishidx01 on bhcp_finish;
create index bhcpfinishidx01 on bhcp_finish(ORGCODE,error_time);
drop index bhcpfinishidx01;
create index bhcpfinishidx01 on bhcp_finish(ORGCODE,error_time);
commit;

db load from ${delfilepath} of del method p(1,2,3,4,5,6,7,8,9,10,11,12) insert into bhcp_finish(orgcode,task_name,task_file_path,file_data,create_date,finish_date,status,step_id,ERROR_TIME,ERROR_LOG,OPER_ID,OPER_NAME) indexing mode rebuild statistics use profile;
commit;


#db load from ${delfilepath} of del method p(4,3,2,1) replace into bhcp_finish(file_data,task_file_path,task_name,orgcode) indexing mode rebuild statistics use profile;
#db load from ${delfilepath} of del method p(4,3,2,1) modified by charset=gbk replace into bhcp_finish(file_data,task_file_path,task_name,orgcode) indexing mode rebuild statistics use profile prevent repeat operation;
#db load from ${delfilepath} of del method p(4,3,2,1) replace into bhcp_finish(file_data,task_file_path,task_name,orgcode) indexing mode rebuild statistics use profile;

db load from ${delfilepath} of del method p(2,4,5,6,7,8,9,10,11,12) merge into bhcp_finish(task_name,file_data,create_date,finish_date,status,step_id,ERROR_TIME,ERROR_LOG,OPER_ID,OPER_NAME);

delete from bhcp_finish;
commit;

db load from ${delfilepath} of del method p(1,2,3,4,5,6,7,8,9,10,11,12) insert into bhcp_finish(orgcode,task_name,task_file_path,file_data,create_date,finish_date,status,step_id,ERROR_TIME,ERROR_LOG,OPER_ID,OPER_NAME) for exception bhcp_finish_error indexing mode incremental statistics use profile;


echo "Test export data file functionality"
quiet "drop table v12_test_tmp";
create table v12_test_tmp (
	branch_id char(6) not null,
	branch_name varchar(150),
	branch_type char(18),
	branch_no char(12),
	status char(1),
	primary key(branch_id)
);
commit;
ddl V12_TEST_TMP;


DECLARE sname Statement WITH insert into v12_test_tmp (branch_id, branch_name, branch_type, branch_no, status) values (?, ?, ?, ?, ?) ;
declare progress use out print 'insert into ${process}%, total ${totalRecord} records ${leftTime}' total 100000 times
set tcount=1
while $tcount <= 100123 loop
  set c1 = "$tcount"
  set c2 = "orgCode$tcount"
  set c3 = "orgType$tcount"
  set c4 = "ID$tcount"
  set c5 = "0"

  FETCH c1, c2, c3, c4, c5 insert sname;
  progress

  set tcount = $tcount + 1
end loop
undeclare sname Statement
commit

set count = select count(*) from v12_test_tmp ;
echo records $count

rm $TMPDIR/v12_test_tmp.del
rm $TMPDIR/v12_test_tmp.txt

declare exportTaskId progress use out print "${taskId} execute ${process}%, total ${totalRecord} records ${leftTime}" total $count times

db export to $TMPDIR\v12_test_tmp{}.del of del modified by progress=exportTaskId chardel=* charhide=0 escapes=1 writebuf=200 maxrows=30041 title message=$TMPDIR/v12_test_tmp.txt select * from v12_test_tmp ;

echo ""
echo ""

# 总行数
set count=0
# 总字节数
set bytes=0
set trows=0;
set tlen=0;
while read line do
    set array = line.split('=');
    if array.length() == 2 && array[0].trim() == "数据输出信息" then
       set str = array.length() + ", " + array[0].trim() + ", " + array[1].trim()
       set files=array[1].split(',');

       for filepath in ${files} loop
          set str=`wc $filepath`
          echo $str

          set ts=str.split()
          set count = count + ts[0].trim().int()
          set bytes = bytes + ts[2].trim().int()

          head -n 5 $filepath
          echo ""
       end loop

       echo 文件总行数: $count 总字节数: $bytes
    elseif array.length() == 2 && array[0].trim() == "卸载数据的总行数" then
       set trows=array[1].trim().int()
    elseif array.length() == 2 && array[0].trim() == "卸载数据的总字节数" then
       set tlen=array[1].trim().int()
    else
    fi
done < $TMPDIR/v12_test_tmp.del

if $trows != $count || $tlen != $bytes then
   echo "$trows != $count || $tlen != $bytes"
   exit 1011
fi


container to execute tasks in parallel using thread=2 begin
  db export to $TMPDIR/v12_test_tmp_t1.del of del modified by sleep=1000 select * from v12_test_tmp ;
  db export to $TMPDIR/v12_test_tmp_t2.del of del modified by sleep=2000 select * from v12_test_tmp ;
  db export to $TMPDIR/v12_test_tmp_t3.del of del modified by sleep=3000 select * from v12_test_tmp ;
  db export to $TMPDIR/v12_test_tmp_t4.del of del modified by sleep=4000 select * from v12_test_tmp ;
  db export to $TMPDIR/v12_test_tmp_t5.del of del modified by sleep=2000 select * from v12_test_tmp ;
  db export to $TMPDIR/v12_test_tmp_t6.del of del modified by sleep=1000 select * from v12_test_tmp ;
end
echo $?

-- 测试数据装载功能
quiet drop table bhcp_finish;
CREATE TABLE bhcp_finish (
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

db load from ${delfilepath} of del method p(1,2,3,4,5,6,7,8,9,10,11,12) merge into bhcp_finish(orgcode,task_name,task_file_path,file_data,create_date,finish_date,status,step_id,ERROR_TIME,ERROR_LOG,OPER_ID,OPER_NAME) indexing mode rebuild statistics use profile;
commit;


db connect reset


exit 0