# 设置变量值
set properties=this.getContext().getContainer().getClassLoader().loadProperties("sftp.properties", "active.test.env")
set sftphost=properties.getProperty("sftp.host")
set sftpport=properties.getProperty("sftp.port")
set sftpuser=properties.getProperty("sftp.username")
set sftppass=properties.getProperty("sftp.password")

cd ${TMPDIR}
echo "select * from table " > ${TMPDIR}/test.sql

sftp ${sftpuser}@${sftphost}:${sftpport}?password=${sftppass}
  set ftphome=`pwd`
  ls ${ftphome}
  set remotetestdir="test"
  rm ${remotetestdir}
  mkdir ${remotetestdir}
  cd ${remotetestdir}
  put `pwd -l`/test.sql
  ls
  pwd
  cd ..
  exists ${remotetestdir}/test.sql
  isfile ${remotetestdir}/test.sql
  mkdir ${ftphome}/test
  rm ${ftphome}/test
  get ${remotetestdir}/test.sql ${TMPDIR}
  exists -l ${TMPDIR}/test.sql
bye