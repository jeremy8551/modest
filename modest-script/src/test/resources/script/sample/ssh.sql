# 设置变量值
set properties=this.getContext().getContainer().getClassLoader().loadProperties("ssh.properties", "active.test.env")
set sshHost=properties.getProperty("ssh.host")
set sshPort=properties.getProperty("ssh.port")
set sshUsername=properties.getProperty("ssh.username")
set sshPassword=properties.getProperty("ssh.password")

ssh ${sshUsername}@${sshHost}:${sshPort}?password=${sshPassword} && export LANG=zh_CN.UTF-8 && pwd && ls -ltrha