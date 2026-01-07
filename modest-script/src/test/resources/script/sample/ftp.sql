# 设置变量值
set properties=this.getContext().getContainer().getClassLoader().loadProperties("ftp.properties", "active.test.env")
set ftphost=properties.getProperty("ftp.host")
set ftpport=properties.getProperty("ftp.port")
set ftpuser=properties.getProperty("ftp.username")
set ftppass=properties.getProperty("ftp.password")

echo "select * from table " > ${TMPDIR}/test.sql

ftp ${ftpuser}@${ftphost}:${ftpport}?password=${ftppass}
  passive
  set ftphome=`pwd`
  set remotetestdir="rpt"
  pwd
  rm ${remotetestdir}
  mkdir ${remotetestdir}
  exists ${remotetestdir}
  ls ${remotetestdir}
  ls ${ftphome}
  put $TMPDIR/test.sql ${remotetestdir}
  ls ${remotetestdir}
  exists ${remotetestdir}/test.sql
  isfile ${remotetestdir}/test.sql
  mkdir test
  isDirectory test
  rm test
  get ${remotetestdir}/test.sql ${TMPDIR}
  exists -l ${TMPDIR}/test.sql
  cd ${remotetestdir}
bye