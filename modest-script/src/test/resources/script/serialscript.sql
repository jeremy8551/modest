
# 测试跨脚本方法
test111

echo "Serial script information output"

set test=1
export set test1 = 2

set totalLoops = 100000
declare progress use out print "Serial script executed ${process}%, total ${totalRecord} records, ${leftTime}" total $totalLoops times
set counter=1
while $counter <= $totalLoops loop
  progress
  #echo counter is $counter
  set counter = $counter + 1
end loop

exit 0