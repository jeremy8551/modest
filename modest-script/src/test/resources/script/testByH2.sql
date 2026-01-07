echo currentdir: `pwd`, currentdate: `date`

echo $host

# 进入目录
cd $curr_dir_path

# 测试变量名大小写不敏感
if "${tmpdir}" != "$TMPDIR" then
  echo "${tmpdir} != $TMPDIR"
  exit 1
fi

# 复制文件
set delfilepath="$TMPDIR/bhc_finish.del"
rm ${delfilepath}
cp classpath:/bhc_finish.del ${TMPDIR}

set test1=1
if ${test1} in (1, 2, 3,5) then
else
  echo ${test1}
  exit 123
fi

set test1="1"
if "${test1}" not in ('1', 2, 3,5) then
  echo ${test1}
  exit 1234
else
fi

# 测试for循环语句
for i in (1,2,3,4, 'test') loop
  echo "Iterate over elements in a for loop $i"

  if $i not in (1,2,3,4, "test" ) then
    echo $i not in (1,2,3,4, 'test' )
    exit 5
  fi

end loop

for i in (1 2 3 4 'test') loop
  echo "Iterate over elements in a for loop $i"

  if $i not in (1,2,3,4, "test" ) then
    echo $i not in (1,2,3,4, 'test' )
    exit 5
  fi

end loop

set col = "1 2 3 4 'test'"
for i in `echo $col` loop
  echo "Iterate over elements in a for loop $i"

  if $i not in (1,2,3,4, "test" ) then
    echo $i not in (1,2,3,4, 'test' )
    exit 5
  fi

end loop

df

function test() {}
function test() {
}
function test() {
;
}
function test() {;}
function test() {;;}
function test231() {
	if $# != 3 then
	  	echo $# != 3
	  	exit 11
	fi

	if "$0" != "test231" then
		"$0" != "test231"
	  	exit 11
	fi

	if "$1" != "1" then
		"$1" != "1"
	  	exit 11
	fi

	if "$2" != "2" then
		"$2" != "2"
	  	exit 11
	fi

	if "$3" != "3" then
		"$3" != "3"
	  	exit 11
	fi

}
test231 1 2 3



while 1==2 loop
end loop
while 1==2 loop
;
end loop

while 1==2 loop end loop
while 1==2 loop while 3==4 loop end loop end loop
while 1==2 loop while 3==4 loop while 1==2 loop end loop end loop end loop


while 3==5 loop end loop ; echo test loop syntax1;
while 3==71 loop end loop ; echo test loop syntax2;
while 3==72 loop end loop;echo test loop syntax3;
while 3==73 loop end loop; echo test loop syntax4;

while 1==2 loop end loop;echo test loop syntax5;
while 1==2 loop end loop;


# 测试子脚本继承父脚本的handler 是否正确
declare global continue handler for exitcode != 0 begin
  echo Testing whether the child script correctly inherits the parent script’s handler exitcode != 0 ..
end

declare  global  continue  handler for exception begin
  echo Testing whether the child script correctly inherits the parent script’s handler exception ..
end

. testDeclareHandler.sql

undeclare global handler for exitcode != 0
undeclare global handler for exception


echo test grep command
echo "1 2 3 4 5" > $TMPDIR/greptest.txt
echo "" >> $TMPDIR/greptest.txt
echo "grep test" >> $TMPDIR/greptest.txt
echo "Test1" >> $TMPDIR/greptest.txt
echo "" >> $TMPDIR/greptest.txt
echo "" >> $TMPDIR/greptest.txt
echo "" >> $TMPDIR/greptest.txt

if `cat $TMPDIR/greptest.txt|grep -i test|wc -l` != 2 then
	echo `cat $TMPDIR/greptest.txt|grep -i test` != 2
fi

if `cat $TMPDIR/greptest.txt|grep test|wc -l` != 1 then
	echo `cat $TMPDIR/greptest.txt|grep test|wc -l` != 1
fi

if `cat $TMPDIR/greptest.txt|grep -v grep|grep -i test|wc -l` != 1 then
	echo `cat $TMPDIR/greptest.txt|grep -v grep|grep -i test|wc -l` != 1
fi



set currentstr=`echo 20210101 | date "yyyy-MM-dd"`

if "2021-01-01" != "$currentstr" then
  echo "2021-01-01" != "$currentstr"
  exit 1
fi

   # 测试注释


echo "1" > $TMPDIR/headtest.log
echo "2" >> $TMPDIR/headtest.log
echo "3" >> $TMPDIR/headtest.log
echo "4" >> $TMPDIR/headtest.log
echo "5" >> $TMPDIR/headtest.log

!mkdir $TMPDIR/headtest.log
!isfile $TMPDIR
!isdirectory $TMPDIR/headtest.log

if `cat $TMPDIR/headtest.log | tail -n 1` != 5 then
  exit 1
fi

if `cat $TMPDIR/headtest.log | head -n 1` != 1 then
  exit 1
fi

set testline=`wc -l $TMPDIR/headtest.log`
set testline=testline.split()[1]
if $testline != "5" then
  echo $testline != 5
  exit 1
fi


rm $TMPDIR/headtest.log
!exists $TMPDIR/headtest.log

function testreverse() {
  return 1
}

!testreverse

set logfilepath="$TMPDIR/headtest.log"
!logfilepath.isdirectory()
logfilepath.isfile()


set tmp_startdate="2020-01-01"
# 测试变量赋值
set tmp_enddate=`date -d "${tmp_startdate}" 'yyyy-MM-dd' + 30 day`

set loadtask_stdout="已加载" +`date -d ${tmp_enddate} yyyy年MM月dd日`+"的接口文件，数据通过了校验已生成报文等待上传!"

if "$loadtask_stdout" != "已加载2020年01月31日的接口文件，数据通过了校验已生成报文等待上传!" then
  echo "$loadtask_stdout" != "已加载2020年01月01日的接口文件，数据通过了校验已生成报文等待上传!"
  exit 2
fi

set tmp_enddate=tmp_startdate + 30 day
set loadtask_stdout="已加载" +`date -d ${tmp_enddate} yyyy年MM月dd日`+"的接口文件，数据通过了校验已生成报文等待上传!"
if "$loadtask_stdout" != "已加载2020年01月31日的接口文件，数据通过了校验已生成报文等待上传!" then
  echo "$loadtask_stdout" != "已加载2020年01月01日的接口文件，数据通过了校验已生成报文等待上传!"
  exit 2
fi

echo $tmp_startdate + 30 day is $tmp_enddate  $loadtask_stdout


if '20200101' + 1 day + '' != '2020-01-02' then
  echo '20200101' + 1 day != '2020-01-02'
  exit 1
fi

set currentdate=20200101
if "${currentdate}" + 1 day + '' != '2020-01-02' then
  echo '20200101' + 1 day != '2020-01-02'
  exit 1
fi

set currentdate='20200101'+1day
if "${currentdate}" != '2020-01-02' then
  echo ${currentdate} + 1 day != '2020-01-02'
  exit 1
fi

set currentdate='20200101'
if currentdate.format("yyyy-MM-dd") != '2020-01-01' then
  echo `currentdate.format("yyyy-MM-dd")` != '2020-01-01'
  exit 1
fi

set currentdate='20200101'+1day+"test"
if "${currentdate}" != '2020-01-02test' then
  echo ${currentdate} + 1 day != '2020-01-02test'
  exit 1
fi

set currentdate='20200101'+1day+1month+1year+1hour+1second+1minute+1millis
if "${currentdate}" != '2021-02-02 01:01:01:001' then
  echo ${currentdate} != '2021-02-02 01:01:01:001'
  exit 1
fi

set currentdate='20200101'+1day+1month+1year+1hour+1second+1minute+1millis
set currentdate=currentdate.format("yyyyMMddhhmmss")
if "${currentdate}" != '20210202010101' then
  echo ${currentdate} != '20210202010101'
  exit 1
fi

set currentdate="2021-02-02 06:21:51:999"
if currentdate.getyear() != 2021 then
  echo `currentdate.getyear()` != 2021
  exit 2
fi

if currentdate.getmonth() != 2 then
  echo `currentdate.getmonth()` != 2
  exit 2
fi

if currentdate.getDay() != 2 then
  echo `currentdate.getDay()` != 2
  exit 2
fi

if currentdate.getHour() != 6 then
  echo `currentdate.getHour()` != 6
  exit 2
fi

if currentdate.getminute() != 21 then
  echo `currentdate.getminute()` != 21
  exit 2
fi

if currentdate.getSecond() != 51 then
  echo `currentdate.getSecond()` != 51
  exit 2
fi

if currentdate.getMillis() != 999 then
  echo `currentdate.getMillis()` != 999
  exit 2
fi

default os
df -h
default sql


# 测试变量方法执行是否正确
set testname="12345"
set testvalue=testname.length() + 1
if $testvalue != 6 then
    echo $testvalue
    exit 17
fi

if testname.length() + 1 != 6 then
  echo `testname.length() + 1` != 6
fi

set testvalue=testname[0]
if testvalue[0] != '1' then
  echo `testvalue[0]` != '1'
  exit 6
fi

if testname[4]!='5' then
  echo `testname[4]` != '5'
  exit 6
fi

if testname[4]!="5" then
  echo `testname[4]` != '5'
  exit 6
fi

set newname='t'+testname[1]+testname.substr(3)+'_'+testname.length()
if "$newname"!="t245_5" then
  echo `newname` != 't245_5'
  exit 6
fi

set newname='t'+testname[1]+testname.substr(3)[1]+'_'+testname.length()
if "$newname"!="t25_5" then
  echo `newname` != 't25_5'
  exit 6
fi
echo ""
echo ""
echo ""
echo ""

sleep 2second
# help script
echo ""
echo ""



echo ""
echo ""
echo ""
echo ""
echo ""
# echo "以下是单独打印ssh命令使用说明语句: help ssh"
# help ssh
echo ""
echo ""
echo ""
echo ""
echo ""


echo test if
if " " == '' then set testIfVar='1'; else set testIfVar='2'; fi
if "$testIfVar" != "2" then
  echo "$testIfVar" != "2"
  exit 10
fi

if "" == '' then set testIfVar='3'; else set testIfVar='4'; fi
if "$testIfVar" != "3" then
  echo "$testIfVar" != "3"
  exit 12
fi
echo ""
echo ""
echo ""
echo ""


echo "Test date command .."
if "" + `date -d 20200103` != '2020-01-03 00:00:00' then
  echo `date -d 20200103` != '2020-01-03 00:00:00'
  exit 120
fi

if "" + `date -d 20201213 'yyyy-MM-dd'` != "2020-12-13" then
  echo `date -d 20201213 'yyyy-MM-dd'` != "2020-12-13"
  exit 121
fi

if "" + `date -d 20201212 'yyyy-MM-dd' + 1 day` != "2020-12-13" then
  echo `date -d 20201212 'yyyy-MM-dd' + 1 day` != "2020-12-13"
  exit 122
fi

if "" + `date -d 20201212 'yyyy-MM-dd' +1day` != "2020-12-13" then
  echo `date -d 20201212 'yyyy-MM-dd' +1day` != "2020-12-13"
  exit 122
fi

if "" + `date -d 20201214 'yyyy-MM-dd' -1day` != "2020-12-13" then
  echo `date -d 20201214 'yyyy-MM-dd' -1day` != "2020-12-13"
  exit 122
fi

if "" + `date -d 20201214 'yyyy-MM-dd' - 1 day` != "2020-12-13" then
  echo "`date -d 20201214 'yyyy-MM-dd' - 1 day`" != "2020-12-13"
  exit 122
fi

if "" + `date -d 20201214 'yyyy-MM-dd' - 1day` != "2020-12-13" then
  echo `date -d 20201214 'yyyy-MM-dd' - 1day` != "2020-12-13"
  exit 122
fi

if "" + `date -d 20201213 'yyyy-MM-dd' - 0day` != "2020-12-13" then
  echo `date -d 20201213 'yyyy-MM-dd' - 0day` != "2020-12-13"
  exit 122
fi

if `date -d 20201213 'yyyy-MM-dd' +0day` != "2020-12-13" then
  echo "`date -d 20201213 'yyyy-MM-dd' +0day`" != "2020-12-13"
  exit 122
fi

if `date -d 20201213 'yyyy-MM-dd' +0day +24hour -1day` != "2020-12-13" then
  echo `date -d 20201213 'yyyy-MM-dd' +0day +24hour -1day` != "2020-12-13"
  exit 122
fi

if `date -d 20201213 'yyyy-MM-dd hh:mm:ss:SSS' +0day +1hour +1minute +1second` != "2020-12-13 01:01:01:000" then
  echo `date -d 20201213 'yyyy-MM-dd hh:mm:ss:SSS' +0day +1hour +1minute +1second` != "2020-12-13 01:01:01:000"
  exit 122
fi

if "" + `date -d 20201213 'yyyy-MM-dd hh:mm:ss:SSS' +0day +1hour +1minute +1second + 999millis` != "2020-12-13 01:01:01:999" then
  echo `date -d 20201213 'yyyy-MM-dd hh:mm:ss:SSS' +0day +1hour +1minute +1second + 999millis` != "2020-12-13 01:01:01:999"
  exit 122
fi

if `date -d '2020-12-13 03:14:56' 'H'` != 3 then
  echo `date -d '2020-12-13 03:14:56' 'H'` != 3
  exit 122
fi

if `date -d 20190929 + 30 day` != "2019-10-29 00:00:00" then
  echo `date -d 20190929 + 30 day` != "2019-10-29 00:00:00"
  exit 122
fi

set testdate=`date -d 20190929 + 30 day`
if "$testdate" != "2019-10-29 00:00:00" then
  echo "$testdate" != "2019-10-29 00:00:00"
  exit 122
fi

set jday=testdate.getdays()
if `date -d "$testdate" - ${jday} day` != "1970-01-01 00:00:00" then
  echo "`date -d $testdate - ${jday} day`" != "1970-01-01 00:00:00"
  exit 34
fi

set tpid=`nohup os sleep 30 & | tail -n 1`
echo "tpid: ${tpid}"
ps -s
ps
sleep 2s
terminate -p $tpid
ps


--echo 启动 db2 数据库
--declare continue handler for exitcode != 0 begin
--  echo db2数据库已启动 ..
--end
--ssh $admin@${host}:22?password=${adminPw} && db2start
--undeclare handler for exitcode != 0
--
--declare continue handler for exitcode != 0 begin
--  echo vsftp已启动 ..
--end
--ssh $sshusername@${sshhost}:22?password=${sshpassword} && 'vsftpd /etc/vsftpd/vsftpd.conf'
--undeclare handler for exitcode != 0
--echo ""
--echo ""
--echo ""
--echo ""

echo "JDBC: ${databaseDriverName}, ${databaseUrl}, ${username}, ${password}"
set dbdirverclass="${databaseDriverName}"
set dburl="${databaseUrl}"
set dbusername="${username}"
set dbpassword="${password}"


echo "driverClassName=$dbdirverclass" > $TMPDIR/jdbcConfig.properties
echo "url=$dburl" >> $TMPDIR/jdbcConfig.properties
echo "username=$dbusername" >> $TMPDIR/jdbcConfig.properties
echo "password=$dbpassword" >> $TMPDIR/jdbcConfig.properties
echo jdbc properties file $TMPDIR/jdbcConfig.properties
export set jdbcfilepath="$TMPDIR/jdbcConfig.properties"

echo "Testing the declare jdbc command .."
declare global test0001 catalog configuration use host ${databaseHost} driver $dbdirverclass url "${dburl}" username ${dbusername} password $dbpassword
db connect to test0001
db get cfg for catalog;
db get cfg for schema;
db get cfg for table type;
db get cfg for field type;

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
set pidddd=`nohup ddl V12_TEST_TMP & | tail -n 1`
echo $pidddd
quiet wait pid=$pidddd 1s;

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

set count = select count(*) from v12_test_tmp;
if count != 100123 then
  echo records $count
  exit 1
fi

rm $TMPDIR/v12_test_tmp.txt

declare exportTaskId progress use out print "${taskId} execute ${process}%, total ${totalRecord} records ${leftTime}" total $count times

db export to $TMPDIR/v12_test_tmp{}.del of del modified by progress=exportTaskId chardel=* charhide=0 escapes=1 writebuf=200 maxrows=30041 title message=$TMPDIR/v12_test_tmp.txt select * from v12_test_tmp ;

cat $TMPDIR/v12_test_tmp.txt
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
          set filepath = filepath.trim()
          set str=`wc $filepath`
          echo $str

          set ts=str.split()
          set count = count + ts[1].trim().int()
          set bytes = bytes + ts[3].trim().int()

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
done < $TMPDIR/v12_test_tmp.txt

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


# 测试更新数据的笔数
set updateRows=update v12_test_tmp set status='2';
if updateRows != 100123 then
  echo "$updateRows != 100123"
  exit 1
fi


# 测试删除数据的笔数
set delRows=delete from v12_test_tmp;
if delRows != 100123 then
  echo "$delRows != 100123"
  exit 1
fi


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

db load from ${delfilepath} of del method p(1,2,3,4,5,6,7,8,9,10,11,12) merge into bhcp_finish(orgcode,task_name,task_file_path,file_data,create_date,finish_date,status,step_id,ERROR_TIME,ERROR_LOG,OPER_ID,OPER_NAME) ;
commit;


db connect reset
undeclare global test0001  catalog configuration

declare test0002 catalog configuration use driver $dbdirverclass url '${dburl}' username ${dbusername} password $dbpassword
db connect to test0002
db connect reset
undeclare test0002 catalog configuration

declare test00021 catalog configuration use driver $dbdirverclass url ${dburl} username ${dbusername} password $dbpassword
db connect to test00021
db connect reset
undeclare global test00021  catalog configuration

declare test0003 catalog configuration use file $TMPDIR/jdbcConfig.properties
db connect to test0003
db connect reset

declare global test0004  catalog configuration use file $TMPDIR/jdbcConfig.properties ;
db connect to test0004
db connect reset

echo ""
echo ""
echo ""
echo ""


echo "Test while read command"
rm $TMPDIR/setlist.log
set setcount=0
while read line do
   set setcount = setcount + 1
   echo "variable $line"  >> $TMPDIR/setlist.log
done < set

set name="variable"
set resultcount=name.length()
while read line do
   echo $line

   if line.indexOf('=', 0) == -1 then
     continue
   fi

   echo $line
   set array = line.substr($resultcount).ltrim().split('=')
   echo $array
   set varname=array[0]
   set varvalue=array[1]

   if array.length() < 2 then
     echo split $line error!
     exit 111
   fi
done < $TMPDIR/setlist.log


set line=
rm $TMPDIR/setlist.log
set setcount=0
while read line do
   set setcount = setcount + 1
   echo "variable $line"  >> $TMPDIR/setlist.log
done <   `set`

set name="variable"
set resultcount=name.length()
while read line do

   if line.indexOf('=', 0) == -1 then
     continue
   fi

   set array = line.substr($resultcount).ltrim().split('=')
   echo $array
   set varname=array[0]
   set varvalue=array[1]

   if array.length() < 2 then
     echo split $line error!
     exit 111
   fi
done < $TMPDIR/setlist.log


echo ""
echo ""
echo ""
echo ""
echo ""



echo "Output all variable values using the set command"
echo $TMPDIR/setcommandlist.log
echo `set` > $TMPDIR/setcommandlist.log
while read line do
  echo $line
done < $TMPDIR/setcommandlist.log



echo "test the sleep wait command"
set curdate=`date`
set sleeptime=4
set dstdate=`date -d "${curdate}" + ${sleeptime}second`
echo "sleep exit time： $dstdate"
sleep ${sleeptime}sec
set testdate=`date`
if testdate.substr(0, 17) != dstdate.substr(0, 17) && testdate.substr(18).int() - dstdate.substr(18).int() <= 1 then
  echo "$testdate" != "$dstdate"
  exit 1111
fi
echo "sleep test succeeded"



echo "Test variable methods .."
var testname=123456
if "$testname" != "123456" then
  echo "$testname" != "123456"
  exit 1
fi

set varname="012345"
set nvar=varname.substr(1)
if "$nvar" != "12345" then
  echo "$nvar" != "12345"
  exit 111
fi

set nvar=varname.substr(5)
if "$nvar" != "5" then
  echo "$nvar" != "5"
  exit 111
fi

set nvar=varname.length()
if "$nvar" != "6" then
  echo "$nvar" != "6"
  exit 111
fi

set nvar=varname[0]
if "$nvar" != "0" then
  echo "$nvar" != "0"
  exit 111
fi

set nvar=varname[5]
if "$nvar" != "5" then
  echo "$nvar" != "5"
  exit 111
fi

set nvar=varname.substr(1,5)
if "$nvar" != "1234" then
  echo "$nvar" != "1234"
  exit 112
fi

set nvar=varname.substr(1,6)
if "$nvar" != "12345" then
  echo "$nvar" != "12345"
  exit 112
fi

set nvar=' test   '
set nvar=nvar.trim();
if "$nvar" != "test" then
  echo "$nvar" != "test"
  exit 112
fi

set nvar=' test   '
set nvar=nvar.upper().trim();
if "$nvar" != "TEST" then
  echo "$nvar" != "TEST"
  exit 112
fi

set nvar=' TEST   '
set nvar=nvar.lower().trim();
if "$nvar" != "test" then
  echo "$nvar" != "test"
  exit 112
fi

set varname="0,1,2,3,4,5,"
set nvar=varname.split(',')[0];
if "$nvar" != "0" then
  echo "$nvar" != "0"
  exit 111
fi

set nvar=varname.split(',')[5];
if "$nvar" != "5" then
  echo "$nvar" != "5"
  exit 111
fi

set nvar=varname.split(',')[6];
if "$nvar" != "" then
  echo "$nvar" != ""
  exit 111
fi

set nvar=varname.split(',').indexOf('5');
if "$nvar" != "5" then
  echo "$nvar" != "5"
  exit 111
fi

set nvar=varname.split(',').indexOf('0', 1);
if "$nvar" != "-1" then
  echo "$nvar" != "-1"
  exit 111
fi

set nvar=varname.split(',').indexOf('0');
if "$nvar" != "0" then
  echo "$nvar" != "0"
  exit 111
fi


set varname="\,,1,2,3,4,5,"

set nvar=varname.split(',', '\\').length();
if "$nvar" != "7" then
  echo "$nvar" != "7"
  exit 111
fi

set nvar=varname.split(',', '\\')[0];
if "$nvar" != "," then
  echo "$nvar" != ","
  exit 111
fi

set varname="0 1  2 3 4 5    "
set nvar=varname.split().length();
if "$nvar" != "7" then
  echo "$nvar" != "7"
  exit 111
fi

set nvar=varname.split()[6];
if "$nvar" != "" then
  echo "$nvar" != ""
  exit 111
fi

echo "test" > ${TMPDIR}/test.bak.log

set filename="${TMPDIR}/test.bak.log"
set t1=filename.getfilename();
echo filename is $t1
if "$t1" != "test.bak.log" then
echo "$t1" != "test.bak.log"
exit 123
fi

set t2=filename.getFileExt()
echo filename ext is $t2
if "${t2}" != "log" then
echo "${t2}" != "log"
exit 123
fi

set t3=filename.getFilenameNoExt();
echo filename noext is $t3
if "$t3" != "test.bak" then
echo "$t1" != "test.bak"
exit 123
fi

set t4=filename.getFileSuffix()
echo filename suf is $t4
if "$t4" != "bak.log" then
echo "$t4" != "bak.log"
exit 123
fi

set t5=filename.getFilenameNoSuffix()
echo filename nosuf is $t5
if "$t5" != "test" then
echo "$t5" != "test"
exit 123
fi

set t6=filename.getParent();
echo filename dir is $t6
if "$t6" != "${TMPDIR}" then
echo "$t6" != "${TMPDIR}"
exit 123
fi

set t7=filename.isFile();
if "$t7" != "true" then
echo "$t7" != "true"
exit 123
fi

set t7=filename.deletefile();
if "$t7" != "true" then
echo "$t7" != "true"
exit 123
fi

set t7=filename.existsFile();
if "$t7" != "false" then
echo "$t7" != "false"
exit 123
fi

set filedir="${TMPDIR}/testsdfsdfsdf"
set t7=filedir.mkdir();
if "$t7" != "true" then
echo "$t7" != "true"
exit 123
fi
echo $filedir

set t7=filedir.isDirectory();
if "$t7" != "true" then
echo "$t7" != "true"
exit 123
fi

echo "tset " > ${filedir}/test.log
echo "tset " > ${filedir}/test1.log
filedir.ls().print();

filedir.deletefile();

set t7=filename.existsFile();
if "$t7" != "false" then
echo "$t7" != "false"
exit 123
fi

set testname="0123456"
set testPosIndex=testname.indexOf("0")
if $testPosIndex != 0 then
  echo $testPosIndex != 0
  exit 12
fi

set testPosIndex=testname.indexOf("6")
if $testPosIndex != 6 then
  echo $testPosIndex != 6
  exit 13
fi

set testPosIndex=testname.indexOf("7")
if $testPosIndex != -1 then
  echo $testPosIndex != -1
  exit 13
fi

set curpath=`pwd`
set lscontent=`os ls ${curpath}`
echo local system ls command output: $lscontent


echo "line1" > ${filedir}/test.log
echo "line2" >> ${filedir}/test.log
echo "line3" >> ${filedir}/test.log
echo "line4" >> ${filedir}/test.log
set testfile="${filedir}/test.log"
set fileLsVar=testfile.getfilelineseparator()
set filelsVarLen=fileLsVar.length()
if $filelsVarLen == 0 || $filelsVarLen > 2 then
  echo filelsVarLen is $filelsVarLen
  exit 1999
fi

set uuidVar=`uuid`
set uuidVarLen=uuidVar.length()
if $uuidVarLen != 32 then
  echo $uuidVarLen != 32
  exit 123
fi

if uuidVar.length() != 32 then
  echo $uuidVarLen != 32
  exit 123
fi

echo ""
echo ""
echo ""
echo ""


echo "Testing the set and export commands .."
set test0001="test"
if "$test0001" != "test" then
   echo "$test0001" != "test"
   exit 1100
fi

set test0001=
if "$test0001" != "" then
   echo "$test0001" != ""
   exit 1100
fi

export set test0001="test"
if "$test0001" != "test" then
   echo "$test0001" != "test"
   exit 1100
fi

export set test0001=
if "$test0001" != "" then
   echo "$test0001" != ""
   exit 1100
fi

if `pwd` != "$curr_dir_path" then
   echo `pwd` != "$curr_dir_path"
   exit 190
fi

set t3="3"
set t1='name'
set t2=t1[0] + "${t3}"

if "$t2" != "n3" then
  echo "$t2" == "n3"
  exit 110
fi

set t4=t2
if "$t4" != "n3" then
  echo "$t4" == "n3"
  exit 110
fi

set t5=t2+' is good name!'
if "$t5" != "n3 is good name!" then
  echo "$t5" == "n3 is good name!"
  exit 110
fi

set ttttname="$TMPDIR/testsetsetset.txt"
ttttname.deletefile();

if !ttttname.isfile() then
else
  echo `!ttttname.isfile()`
  exit 111
fi

if ttttname.isfile() then
  echo $ttttname is file!
  exit 1111
fi


if "${blankvar}" != "" then
  echo "${blankvar}" != ""
  exit 120
fi
if "$blankvar" != "" then
  echo "${blankvar}" != ""
  exit 120
fi
set testcode="0000"
export set topOrgcode="${testcode}0"
if "$topOrgcode" != "00000" then
  echo "$topOrgcode" != "00000"
  exit 110
fi
export set topOrgcode11='\${testcode}0'
if "$topOrgcode11" != '${testcode}0' then
  echo "$topOrgcode11" != '${testcode}0'
  exit 110
fi
echo ""
echo ""
echo ""
echo ""


echo "Testing whether export and set statements work correctly across script .."
set testvarkjb=1
export set testvarkjb=2
if $testvarkjb != 2 then
   echo $testvarkjb != 2
   exit 1
fi
export set testvarkjb=3
. `pwd`/testexport.sql

echo ""
echo ""
echo ""
echo ""



echo "Testing exception handling .."
declare continue handler for exception begin
  echo deal exception ${exception}
end

java cn.org.expect.script.command.JavaCommandTest2

undeclare  handler for exception ;
echo ""
echo ""
echo ""
echo ""


# 测试输出信息到日志
echo -n "test 123" 1> ${TMPDIR}/test111.log
while read line do
  if "$line" != "test 123" then
     echo "$line" != "test 123"
     exit 10
  fi
done < ${TMPDIR}/test111.log
echo -n "4" >> ${TMPDIR}/test111.log
while read line do
  if "$line" != "test 1234" then
     echo "$line" != "test 1234"
     exit 10
  fi
done < ${TMPDIR}/test111.log



set totalLoops = 100000
declare progress use out print "test progress output ${process}%, total ${totalRecord} records ${leftTime}" total $totalLoops times
set counter=1
while $counter <= $totalLoops loop
  progress
  #echo counter is $counter
  set counter = $counter + 1
end loop



echo ${TMPDIR}/testerrlog.err
# 测试错误信息输出
declare continue handler for exitcode != 0 begin
  while read line do
    echo $line
    if "$line" == "" then
       echo "$line"
       exit 120
    fi
  done < ${TMPDIR}/testerrlog.err
end

undeclare nonamecur cursor 1>${TMPDIR}/testerrlog.log 2> ${TMPDIR}/testerrlog.err
rm ${TMPDIR}/testerrlog.err 2>&1
undeclare nonamecur cursor > ${TMPDIR}/testerrlog.err 2>&1
undeclare handler for exitcode != 0;




# 测试deamon 获取子脚本变量值
daemon `pwd`/daemontest.sql
if "$deamonvartest" != "true" || "$deamonvartest0" != "true" then
  echo "$deamonvartest" != "true" || "$deamonvartest0" != "true"
  exit 130
fi


echo "Output all variable values using the set command"
set


function test111() {
echo "Test methods across scripts"
}

export function test111

# 测试执行串行脚本
. `pwd`/serialscript.sql



echo $TMPDIR/nohuptest2.log
echo $TMPDIR/nohuptest2.err
set currentdir=`pwd`
export set totalLoops = 10000000
declare global progress use err print "parallel script executed ${process}%, total ${totalRecord} records ${leftTime}" total $totalLoops times
set pid1=`nohup . $currentdir/nohuptest1.sql & | tail -n 1`
echo pid1 is $pid1

set pid2=`nohup . $currentdir/nohuptest2.sql > $TMPDIR/nohuptest2.log 2>&1 | tail -n 1`
echo pid2 is $pid2

wait pid=${pid1}
wait pid=${pid2}


# 测试时间命令
date -d '20200123112233444'

if '1' + '2' != '12' then
set varname='1' + '2'
echo $varname
exit 10
fi

set tttt=1
if $tttt == 2 then
  exit $tttt
elseif $tttt == 3 then
  exit $tttt
elseif $tttt == 4 then
  exit $tttt
else
  set tttt=2
fi

if $tttt == 2 then
  set tttt=8
elseif $tttt == 3 then
  exit $tttt
elseif $tttt == 4 then
  exit $tttt
else
  exit $tttt
fi

if $tttt == 8 then
  if $tttt != 8 then
  exit $tttt
  else

  fi
elseif $tttt == 3 then
  exit $tttt
elseif $tttt == 4 then
  exit $tttt
else
  exit $tttt
fi
echo ""
echo ""
echo ""
echo ""


--echo 测试 ssh 端口转发功能 ..
--declare test SSH tunnel use proxy ${proxyuser}@${proxyhost}:22?password=${proxypass} connect to  0:${sshhost}:22
--undeclare name ssh  tunnel
--
--declare test SSH tunnel use proxy ${proxyuser}@${proxyhost}:22?password=${proxypass} connect to  testlocalport:${sshhost}:22
--if "$testlocalport" == "" then
--  echo 本地转发端口号 $testlocalport
--  exit 2
--fi
--undeclare name ssh  tunnel
--
--declare test ssh client for connect to ${sshusername}@${sshhost}:22?password=${sshpassword}
--undeclare test ssh client
--
--set localport=`declare test ssh tunnel use proxy ${proxyuser}@${proxyhost}:22?password=${proxypass} connect to 0:${sshhost}:22 | tail -n 1`
--echo 本地ssh转发隧道端口 $localport
--undeclare test ssh tunnel;
--echo ""
--echo ""
--echo ""
--echo ""
--
--handler



echo Testing JDBC commands. ..
declare global uddb11  catalog configuration use driver ${databaseDriverName} url '${databaseUrl}' username ${username} password ${password}
db connect to uddb11;
db connect reset;
declare  uddb12  catalog configuration use driver ${databaseDriverName} url '${databaseUrl}' username ${username} password ${password}
db connect to uddb12;

quiet drop table v2_test;
create table v2_test (
f1 char(10) not null,
f2 char(10),
f3 char(10),
primary key(f1)
);
commit;

insert into v2_test (f1, f2) values ('&ads;{}', '');
commit;

   -- 测试sql语句注释
   select * from v2_test;-- 测试 11

   /**
    * 测试sql语句注释
    *
    */
   select * from v2_test;



   /**
    * 测试sql语句注释
    *
    */select * from v2_test;

# 先删除日志文件
rm $TMPDIR/pks.log

echo 日志文件 $TMPDIR/pks.log

# 测试主键冲突发生异常的处理逻辑是否正确
declare continue handler for errorcode == 23505 begin
	echo logfile: $TMPDIR/pks.log
    echo "Testing whether the database primary key conflict exception handling logic works correctly." > $TMPDIR/pks.log
end

# 插入记录
insert into v2_test (f1, f2) values ('&ads;{}', '');
commit;

# 判断日志文件是否存在
exists $TMPDIR/pks.log

# 判断日志文件内容是否正确
while read line do
 if "$line" != "Testing whether the database primary key conflict exception handling logic works correctly." then
   echo "$line" != "Testing whether the database primary key conflict exception handling logic works correctly."
   exit 190
 fi
done < $TMPDIR/pks.log

# 删除异常处理逻辑
undeclare handler for errorcode == 23505;

set v2_test_f1=select f1 from v2_test ;
echo "[${v2_test_f1}]"
set v2_test_f1=v2_test_f1.trim()
if "\${$v2_test_f1}" != '${${}}' then
  echo "\${$v2_test_f1}" != '${${}}'
  exit 110
fi

quiet "drop table v10_test_tmp";
quiet "drop table v10_test_tmp";
create table v10_test_tmp (
	branch_id char(6) not null,
	branch_name varchar(150),
	branch_type char(18),
	branch_no char(12),
	status char(1),
	primary key(branch_id)
); /* 测
试
多行
注释 */commit;


DECLARE sname Statement by 1 batch WITH insert into v10_test_tmp (branch_id, branch_name, branch_type, branch_no, status) values (?, ?, ?, ?, ?) ;
set tcount=1
while $tcount <= 100 loop
  set c1 = "$tcount"
  set c2 = "orgCode$tcount"
  set c3 = "orgType$tcount"
  set c4 = "ID$tcount"
  set c5 = "0"

  echo "$c1, $c2, $c3, $c4, $c5"
  FETCH c1, c2, c3, c4, c5 insert sname;
  progress

  set tcount = $tcount + 1
end loop

undeclare sname Statement

delete from v10_test_tmp;
commit;

DECLARE sname Statement WITH insert into v10_test_tmp (branch_id, branch_name, branch_type, branch_no, status) values (?, ?, ?, ?, ?) ;

declare progress use out print 'insert into ${process}%, total ${totalRecord} records ${leftTime}' total 100000 times

set tcount=1
while $tcount <= 100000 loop
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

echo "Testing whether the SQL interrupt function is available"
declare continue handler for exitcode == -3 begin
  if $exitcode != -3 then
    exit 999
  fi
  echo wait result value $exitcode must equals -3 ..
end
set pidd=`nohup . $PWD/executesql.sql | tail -n 1`
wait pid=$pidd 1sec
undeclare handler for exitcode == -3;

db connect reset
echo ""
echo ""
echo ""
echo ""


--echo 测试使用 ssh 命令远程杀掉进程 ..
--ssh ${sshusername}@${sshhost}:22?password=${sshpassword} && echo LANG is $LANG ;
--# 防止下一个命令报错
--DECLARE continue handler for exitcode != 0 begin echo $exitcode end
--# 杀掉后台进程
--ssh ${sshusername}@${sshhost}:22?password=${sshpassword} && "ps -ef | grep export | grep db2 | grep -v grep | awk -F' ' '{print $2}'|xargs kill" ;
--undeclare handler for exitcode != 0;
--echo ""
--echo ""
--echo ""
--echo ""



#ssh ${sshusername}@${sshhost}:22?password=${sshpassword} && rm -f `pwd`/o_als_acct_loan_bhzx.del && export LANG=zh_CN.GBK && db2 connect to ${databaseName} user ${username} using ${password} \
#&& ( db2 "export to o_als_acct_loan_bhzx.del of del select * from bhcsp.o_als_acct_loan_bhzx " ) \
#;



--echo 测试执行远程命令卸载数据文件 ..
--DECLARE continue handler for exitcode == 2 begin echo "exitcode is $exitcode"; end
--ssh ${admin}@${host}:22?password=${adminPw} && ls -la && pwd && touch dmd_bank_info1.del && rm dmd_bank_info1.del && export LANG=zh_CN.GBK && db2 connect to ${databaseName} user ${username} using ${password} \
--&& ( db2 "export to dmd_bank_info1.del of del select * from v10_test_tmp " ) \
--;
--echo ssh command exitcode is $?
--undeclare handler for exitcode == 2;
--echo ""
--echo ""
--echo ""
--echo ""




--echo 测试 sftp 命令下载数据文件 ..
--sftp ${sshusername}@${sshhost}:22?password=${sshpassword}
--get dmd_bank_info1.del $TMPDIR
--echo $TMPDIR/dmd_bank_info1.del
--bye
--echo ""
--echo ""
--echo ""
--echo ""




--echo 测试远程执行db2数据库 load 命令 ..
--ssh ${admin}@${host}:22?password=${adminPw} && export LANG=zh_CN.GBK && db2 connect to ${databaseName} user ${username} using ${password} \
--&& db2 "load client from /dev/null of del replace into v10_test_tmp " \
--&& db2 "load client from `pwd`/dmd_bank_info1.del of del replace into v10_test_tmp " \
--&& db2 connect reset ;
--echo ssh command exitcode is $?
--echo ""
--echo ""
--echo ""
--echo ""




--echo 测试 sftp 相关命令 ..
--sftp ${ftpuser}@${ftphost}:22?password=${ftppass}
--set ftphome=`pwd`
--echo 远程sftp命令所在目录: $ftphome
--ls ${ftphome}
--set remotetestdir="${ftphome}/rpt1"
--rm ${remotetestdir}
--mkdir ${remotetestdir}
--cd ${remotetestdir}
--put `pwd -l`/test.sql
--ls
--exists ${remotetestdir}/test.sql
--isfile ${remotetestdir}/test.sql
--mkdir ${ftphome}/test
--rm ${ftphome}/test
--get ${remotetestdir}/test.sql ${TMPDIR}
--exists -l ${TMPDIR}\test.sql
--bye
--echo ""
--echo ""
--echo ""
--echo ""


--echo 测试本地文件系统相关命令 ..
--isfile -l ${TMPDIR}\test.sql
--isfile  ${TMPDIR}\test.sql
--md5sum ${TMPDIR}\test.sql
--zip ${TMPDIR}\test.sql
--unzip ${TMPDIR}\test.zip
--tar -zcvf ${TMPDIR}\test.sql
--tar -xvf ${TMPDIR}\test.tar
--gzip ${TMPDIR}\test.sql
--gunzip ${TMPDIR}\test.gz
--rm ${TMPDIR}\test.sql
--echo ""
--echo ""
--echo ""
--echo ""


--echo 测试 ftp 命令 ..
--function testFtpCommands() {
--echo test >> $TMPDIR/test.sql
--ls -l $TMPDIR/test.sql
--ftp ${ftpuser}@${ftphost}:21?password=${ftppass}
--set ftphome=`pwd`
--set remotetestdir="${ftphome}/rpt1"
--pwd
--rm ${remotetestdir}
--mkdir ${remotetestdir}
--exists ${remotetestdir}/
--ls ${remotetestdir}
--cd ${remotetestdir}
--put $TMPDIR/test.sql ${remotetestdir}
--ls ${remotetestdir}
--exists ${remotetestdir}/test.sql
--isfile ${remotetestdir}/test.sql
--mkdir ${ftphome}/test
--isDirectory ${ftphome}/test
--rm ${ftphome}/test
--get ${remotetestdir}/test.sql ${TMPDIR}
--exists -l ${TMPDIR}\test.sql
--bye
--ls -l `pwd`
--rm -l ${TMPDIR}\test.sql
--}
--testFtpCommands
--echo ""
--echo ""
--echo ""
--echo ""


# xxxxx
# 参数1 xxxxx
# 参数2 xxxxx
# 参数3 xxxxx
# 参数4 xxxxx
# 参数5 xxxxx
# 参数6 xxxxx
# 返回值 ${tmp_termNo1} xxxxx
function _update_bhc_cl1_repay_int() {
  set tmp_creditAccntId="$1"
  set tmp_billingId="$2"
  set tmp_repaymentDate="$3"
  set tmp_repayNo=$4
  set tmp_billingAmount_sum=$5
  set tmp_billingInts=$6

  #echo ${tmp_creditAccntId} ${tmp_billingId} ${tmp_repaymentDate} ${tmp_repayNo} ${tmp_billingAmount_sum} ${tmp_billingInts}

  if ${tmp_billingInts} == 0 THEN
      return 0;
  fi

  # xxxxx
  set tmp_plannedAmount_sum=0
  set tmp_termNo1=-1

  DECLARE cnofint CURSOR WITH RETURN FOR select targetRepaymentDate, termNo, plannedInt from BHC_CL2_M where creditAccntId = '${tmp_creditAccntId}' and billingId = '${tmp_billingId}' and termNo != 999 order by termNo asc ;
  CURSOR cnofint loop
      FETCH cnofint INTO tmp_targetRepaymentDate, tmp_termNo1, tmp_plannedAmount;

      # xxxxx
      set tmp_plannedAmount_sum = ${tmp_plannedAmount_sum} + ${tmp_plannedAmount}

      #echo xxxxx ${tmp_plannedAmount_sum} xxxxx ${tmp_billingAmount_sum}

      # xxxxx 等于 xxxxx
      if ${tmp_plannedAmount_sum} == ${tmp_billingAmount_sum} then
          update BHC_CL1_REPAY set repayTerm = ${tmp_termNo1} where creditAccntId = '${tmp_creditAccntId}' and billingId = '${tmp_billingId}' and repaymentDate = '${tmp_repaymentDate}' and repayNo = ${tmp_repayNo};
          commit;
          set current_termNO=${tmp_termNo1}
          return 0
      elseif ${tmp_plannedAmount_sum} > ${tmp_billingAmount_sum} then
          update BHC_CL1_REPAY set repayTerm = ${tmp_termNo1} where creditAccntId = '${tmp_creditAccntId}' and billingId = '${tmp_billingId}' and repaymentDate = '${tmp_repaymentDate}' and repayNo = ${tmp_repayNo};
          commit;
          return 0
      else
          # xxxxx
          if ${current_termNO} <= ${tmp_termNo1} then
            # xxxxx
            continue;
          fi

          # xxxxx 大于 xxxxx
          insert into BHC_CL1_REPAY_INC (
              reqID, -- xxxxx
              creditAccntId, -- xxxxx
              billingId , -- xxxxx
              repaymentDate , -- xxxxx
              repayNo , -- xxxxx
              repayTerm , -- xxxxx
              billingAmount , -- xxxxx
              billingBal , -- xxxxx
              billingInt , -- xxxxx
              repayType , -- xxxxx
              deleteFlag , -- xxxxx
              compFlag, --xxxxx
              compAmount, --xxxxx
              loanAmount, -- xxxxx
              loanBal, -- xxxxx
              realTime, --xxxxx
              memo -- xxxxx
          ) select
              x.reqID,
              x.creditAccntId, -- xxxxx
              x.billingId, -- xxxxx
              x.repaymentDate, -- xxxxx
              1 + COALESCE( ( select max(repayNo) from BHC_CL1_REPAY_INC y where x.creditAccntId = y.creditAccntId and x.billingId = y.billingId and y.repaymentDate = x.repaymentDate ), ( select max(repayNo) from BHC_CL1_REPAY y where x.creditAccntId = y.creditAccntId and x.billingId = y.billingId and y.repaymentDate = x.repaymentDate )), -- xxxxx
              ${tmp_termNo1}, -- xxxxx
              ${tmp_plannedAmount}, -- xxxxx
              0, -- xxxxx
              ${tmp_plannedAmount}, -- xxxxx
              x.repayType, -- xxxxx
              x.deleteFlag, -- xxxxx
              x.compFlag, -- xxxxx
              0, -- xxxxx
              x.loanAmount, -- xxxxx
              0, -- xxxxx
              x.realTime, --xxxxx
              'xxxxx ${tmp_creditAccntId}, ${tmp_billingId}, ${tmp_repaymentDate}, ${tmp_repayNo}' -- xxxxx
          from BHC_CL1_REPAY x
         where creditAccntId = '${tmp_creditAccntId}' -- xxxxx
           and billingId = '${tmp_billingId}' -- xxxxx
           and repaymentDate = '${tmp_repaymentDate}' -- xxxxx
           and repayNo = ${tmp_repayNo} -- xxxxx

         ; -- xxxxx xxxxx
         commit;

         # xxxxx
         update BHC_CL1_REPAY x
            set x.billingInt = x.billingInt - ${tmp_plannedAmount} -- xxxxx
          where creditAccntId = '${tmp_creditAccntId}' -- xxxxx
            and billingId = '${tmp_billingId}' -- xxxxx
            and repaymentDate = '${tmp_repaymentDate}' -- xxxxx
            and repayNo = ${tmp_repayNo} -- xxxxx
         ;   --     xxxxx
         commit;

         set current_termNO=${tmp_termNo1}

      fi

  end loop
  undeclare cnofint cursor

  return 1
}

# 定义 step 命令回调函数
declare global command callback for step begin
  echo "step callback function output of the command: $1"
end


# 定义 echo 命令回调函数
declare global command callback for echo begin end

undeclare global command callback for echo

set _t_test_1=0
function _test() {
set _t_test_1= $_t_test_1 + 1
}

_test
if $_t_test_1 != 1 then
  echo function 错误
	exit 10
fi


# 非正确退出处理
declare continue handler for exitcode == 33 begin
  echo exit with no 0!
end
set _t_test_2 = 2
function _test1() {
set _t_test_2= $_t_test_2 + $1 + $2 + $3
return 33
}
_test1 1 2 3
if $? != 33 then
  echo function 错误
	exit 11
fi
undeclare handler for exitcode == 33;
echo ""
echo ""
echo ""
echo ""


if $_t_test_2 != 8 then
  echo function 错误 $_t_test_2
	exit 10
fi

step PartOne1

declare global testdb  catalog configuration use file ${jdbcfilepath}

. ${PWD}/test2.sql

declare  global testdb1  catalog configuration use driver $databaseDriverName url '${databaseUrl}' username ${username} password $password

declare  uddb  catalog configuration use driver ${$databaseDriverName} url '${databaseUrl}' username  ${username} password ${$password}

declare test0 catalog configuration use file ${jdbcfilepath}



# 异常处理
declare exit handler for exception begin
  db connect to testdb
  echo deal exception ${exception}
  #update BHC_FINISH set step_id = '重建数据库表索引', task_file_path = '/home/user/rpt/testsys/BHC_LOADFEX.sql' where task_name = '接口批量任务' and file_data='2020-03-23';
  db connect reset
end

# 非正确退出处理
declare exit handler for exitcode != 0 begin
  db connect to testdb
  echo exit with no 0!
  #update BHC_FINISH set step_id = '重建数据库表索引', task_file_path = '/home/user/rpt/testsys/BHC_LOADFEX.sql' where task_name = '接口批量任务' and file_data='2020-03-23';
  db connect reset
end

# 正确退出处理
declare exit handler for exitcode == 0 begin
  db connect to testdb
  echo exit with 0!
  #update BHC_FINISH set step_id = '重建数据库表索引', task_file_path = '/home/user/rpt/testsys/BHC_LOADFEX.sql' where task_name = '接口批量任务' and file_data='2020-03-23';
  db connect reset
end

step PartTwo
step Part3


set KSC_NAME='script name is '
export set KSC_INT=0
daemon `pwd`/test3.sql

if "${KSC_NAME}" != 'script name is test3.sql' then
  echo ${KSC_NAME} not equels script name is test3.sql
  exit 10
fi

if ${KSC_INT} != 1 then
  echo ${KSC_INT} is not 1
  exit 11
fi


db connect to testdb
db connect reset

declare testdb1 catalog configuration use file ${jdbcfilepath}
db connect to testdb1







export set myname='this is good '
export set myname = 'this is good ' ;

quiet "drop table v1_test";
create table v1_test (
f1 char(10) not null,
f2 char(10),
f3 char(10),
primary key(f1)
);

set noname=1
DECLARE s1 Statement WITH insert into v1_test (f1, f2, f3) values (?,?, ?) ;
while ${noname} <= 100 loop
  set v1="value${noname}"
  set v2="value ${noname}"
  set v3="value  ${noname}"
  FETCH v1, v2, v3 insert s1;
  commit;
  set noname=${noname}+1
end loop
undeclare s1 statement

function error() {
echo Output error message: $1
echo ""
echo ""
}

set t222=0
jump test

delete from v1_test;
commit;

step test1
echo test1

step test2
echo test2

step test

set t222=select count(*) from v1_test;
if $t222 == 0 then
echo jump 命令错误
exit 3
fi

echo "this"
echo ""
echo ""
echo ""
echo ""
echo ""



set noname=1
DECLARE s1 Statement WITH update v1_test set f2 = ? where f1 = ? ;
while ${noname} <= 100 loop
  set v1="value${noname}"
  set v2="${noname}"
  FETCH v2, v1 insert s1;
  commit;
  set noname=${noname}+1
  if ${noname} == 100 then
    break;
  else
    continue
  fi
end loop
undeclare s1 statement

if ${noname} != 100 then
echo "${noname} > 100"
exit 3
fi


# 测试 rollback 语句
delete from v1_test;
rollback;
set t222=select count(*) from v1_test;
if $t222 == 0 then
echo rollback 命令错误
exit 3
fi



# 测试游标
set loono = 0;
DECLARE cno CURSOR WITH RETURN FOR select f1, f2, f3 from v1_test order by f1 asc;
CURSOR cno loop
  FETCH cno INTO tmp_f1, tmp_f2, tmp_f3;
  set loono = ${loono} + 1; # 序号 + 1

end loop
undeclare cno cursor

if $loono != 100 then
  echo $loono 错误
  exit 5
fi

#delete from table1;
#
#


DECLARE cno1 CURSOR WITH RETURN FOR select * from v1_test ;
  CURSOR cno1 loop
      FETCH cno1 INTO f1, f2, f3;

      set _tvl = 0;
      while ${_tvl} < 3 loop

        echo "loop${_tvl}" > ${TMPDIR}/testloop${_tvl}.log
        while read line do
        	if "${line}" != "loop${_tvl}" then
        	   echo "${line}" != "loop${_tvl}"
        	   exit 111
        	fi
        done < ${TMPDIR}/testloop${_tvl}.log

      	set _tvl = $_tvl + 1
      end loop

  end loop
undeclare cno1 cursor
commit;

sql  "insert into v1_test (f1) values ('$test')";
set count2=select count(*) from v1_test where f1 = '$test';
if $count2 != 1 then
echo $count2
exit 4
fi

java  cn.org.expect.script.command.JavaCommandTest1

echo nohup . ${PWD}/test1.sql &
set ppid=`nohup . ${PWD}/test1.sql & | tail -n 1`

# 用于判断是否运行正确的变量
set testvalue000="1000"

echo "Print all user sessions in the script engine"
ps -s

echo "Print all background processes in user sessions"
ps

echo wait pid=$ppid 10sec
wait pid=$ppid 10sec


echo wait 命令错误, 正常情况下不会执行到这 .
exit 1