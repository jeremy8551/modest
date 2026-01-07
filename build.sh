#!/bin/sh


# -e：命令错误退出 -u：使用未定义变量时报错 -o pipefail：管道中任意一环失败就算失败
set -euo pipefail

# 禁用清理缓存
export HOMEBREW_NO_INSTALL_CLEANUP=1

# 禁用自动更新
export HOMEBREW_NO_AUTO_UPDATE=1

# 显示环境提示
export HOMEBREW_NO_ENV_HINTS=0

# 获取命令行选项
while getopts "c" opt; do
  case "$opt" in
    c)
      export HOMEBREW_BOTTLE_DOMAIN=https://mirrors.aliyun.com/homebrew/homebrew-bottles
      export HOMEBREW_API_DOMAIN="https://mirrors.aliyun.com/homebrew/homebrew-bottles/api"
      ;;
    ?)
      echo "Invalid option"
      exit 1
      ;;
  esac
done


# 把选项从 $@ 中移除
shift $((OPTIND - 1))


# 检测当前 Mac 是否为 Apple Silicon (arm64 架构)
ARCH=$(uname -m)
if [ "$ARCH" = "arm64" ]; then
  echo "Detected Apple Silicon ($ARCH)."

  # 检查 Rosetta 是否已安装
  if /usr/bin/pgrep oahd >/dev/null 2>&1; then
    echo "Rosetta is already installed."
  else
    echo "Installing Rosetta 2..."
    if softwareupdate --install-rosetta --agree-to-license; then
      echo "Rosetta installed successfully."
    else
      echo "Failed to install Rosetta 2. Please try manually: sudo softwareupdate --install-rosetta"
    fi
  fi
else
  echo "This Mac is Intel-based ($ARCH). Rosetta is not required."
fi


# MacOS 包管理
if ! command -v brew >/dev/null 2>&1; then
  /bin/zsh -c "$(curl -fsSL https://gitee.com/cunkai/HomebrewCN/raw/master/Homebrew.sh)" speed
  if [ ! -f ~/.zprofile ]; then
    echo "export PATH=/opt/homebrew/bin:$PATH" >> ~/.zprofile
    eval "$(/opt/homebrew/bin/brew shellenv)"
  else
    . "${HOME}/.zprofile"
  fi
else
  echo "brew is already installed."
fi


# 判断 Docker Desk App 是否已安装
if [ -d /Applications/Docker.app ]; then
  echo "docker-desktop is already installed."

  # 检查 Docker 是否已启动
  echo "Start docker-desktop in the background .."
  open -g -a /Applications/Docker.app
  echo "Waiting for docker-desktop to start .."
  i=1
  while [ "$i" -le 60 ]; do
    if docker info >/dev/null 2>&1; then
      echo "docker-desktop has already start."
      break
    fi
    sleep 2
  done

  # 使用 Docker Hub
  echo "docker context use default"
  docker context use default
else
  # 安装 colima
  if command -v colima >/dev/null 2>&1; then
     echo "colima is already installed."
  else
     brew install colima
  fi


  # ============================================================================
  # 4. 启动 colima 虚拟机
  # ============================================================================
  if colima status >/dev/null 2>&1; then
    echo "colima is already running."
  else
    # -----------------------------
    # 自动检测宿主机资源并分配
    # -----------------------------

    # 获取 CPU 核心总数
    CPU_TOTAL=$(sysctl -n hw.ncpu)

    # 分配 70% CPU 给 colima，至少分配 1 核
    CPU_ALLOC=$((CPU_TOTAL * 7 / 10))
    [ "${CPU_ALLOC}" -lt 1 ] && CPU_ALLOC=1

    # 获取系统总内存大小（字节）
    MEM_TOTAL=$(sysctl -n hw.memsize)

    # 转换为 GB
    MEM_TOTAL_MB=$((MEM_TOTAL / 1024 / 1024 / 1024))

    # 分配 70% 内存给 colima
    MEM_ALLOC=$((MEM_TOTAL_MB * 7 / 10))

    # 磁盘大小（GB）- 可根据需要自定义
    DISK_SIZE=10

    # 显示资源分配信息
    echo "cpu: ${CPU_ALLOC}"
    echo "memory: ${MEM_ALLOC}G"
    echo "disk: ${DISK_SIZE}G"

    # 创建 colima 配置目录
    mkdir -p ~/.colima/default
    # 生成 colima 配置文件
    cat <<EOF > ~/.colima/default/colima.yaml
cpu: ${CPU_ALLOC}                     # CPU 核心数
memory: ${MEM_ALLOC}                  # 内存大小（GB）
disk: ${DISK_SIZE}                    # 磁盘大小（GB）
arch: aarch64                         # 架构类型（ARM64）
runtime: docker                       # 容器运行时
hostname: colima                      # 虚拟机主机名
kubernetes:
  enabled: false                      # 启用 Kubernetes
  version: v1.33.4+k3s1               # Kubernetes 版本
  k3sArgs:
    - --disable=traefik               # 禁用 traefik 入口控制器
  port: 0                             # 自动分配 Kubernetes API Server 端口
autoActivate: true                    # 自动激活 Docker 上下文
network:
  address: false                      # 不设置固定网络地址
  mode: shared                        # 网络模式：共享
  interface: en0                      # 网络接口
  preferredRoute: false               # 不使用首选路由
  dns: []                             # DNS 服务器列表
  dnsHosts: {}                        # DNS 主机映射
  hostAddresses: false                # 不使用主机地址
forwardAgent: false                   # 不转发 SSH 代理
docker:
  registry-mirrors:                   # Docker 镜像加速器（国内镜像源）
    - https://docker.1ms.run
    - https://docker.1panel.live
    - https://mirror.ccs.tencentyun.com
    - https://hub-mirror.c.163.com    # 网易云镜像
    - https://mirror.baidubce.com     # 百度云镜像
    - https://dockerhub.azk8s.cn      # Azure 中国镜像
    - https://docker.mirrors.ustc.edu.cn  # 中科大镜像
    - https://docker.nju.edu.cn       # 南京大学镜像
    - https://registry.docker-cn.com  # Docker 中国官方镜像
vmType: vz                            # 虚拟机类型（VZ 是 macOS 的虚拟化框架）
portForwarder: ssh                    # 端口转发方式：SSH
rosetta: false                        # 不使用 Rosetta（已在系统层面处理）
binfmt: true                          # 启用二进制格式支持
nestedVirtualization: false           # 禁用嵌套虚拟化
mountType: virtiofs                   # 挂载类型：virtiofs（高性能文件系统）
mountInotify: true                    # 启用文件系统事件通知
cpuType: ""                           # CPU 类型（空表示使用默认）
provision: []                         # 初始化脚本列表
sshConfig: true                       # 生成 SSH 配置
sshPort: 0                            # SSH 端口（0 表示自动分配）
mounts: []                            # 挂载点列表
diskImage: ""                         # 自定义磁盘镜像路径（空表示使用默认）
rootDisk: 20                          # 根磁盘大小（GB）
env: {}                               # 环境变量
EOF

    # 启动 colima 虚拟机
    QCOW2_FILE=$(ls -t "$HOME/Downloads"/*.qcow2 2>/dev/null | head -n 1)
    if [ -n "$QCOW2_FILE" ]; then
      echo "colima start -i ${QCOW2_FILE}"
      colima start -i "$QCOW2_FILE"
    else
      echo "colima start"
      colima start
    fi
  fi

  # 设置 Docker 上下文信息
  echo "docker context use colima"
  docker context use colima
fi


echo "Waiting for Docker daemon to be ready."
sleep 2
timeout=60
while ! docker info >/dev/null 2>&1; do
    sleep 2
    timeout=$((timeout-2))
    if [ $timeout -le 0 ]; then
        echo "Docker daemon did not start in time."
        exit 1
    fi
done


# 停止并删除 jdksetup 容器
dockerContainerID=`docker ps --filter "name=jdksetup" -qa`
if [ -n "${dockerContainerID}" ]; then
  docker stop ${dockerContainerID} && docker rm ${dockerContainerID}
fi


# 安装
mkdir -p ~/.m2
echo "docker run -d --name jdksetup -p 3306:3306 -v ~/.m2:/home/user/.m2 -e JDK_SETUP_DIR=${HOME}/.m2/jdks jeremy8551/jdksetup:latest"
docker run -d --name jdksetup -p 3306:3306 -v ~/.m2:/home/user/.m2 -e JDK_SETUP_DIR=${HOME}/.m2/jdks jeremy8551/jdksetup:latest


# 等容器启动稳定 2 秒（可选）
sleep 12


# 打印一次日志，不阻塞
docker logs jdksetup --tail 500


# 编译
# 获取 Java 17 的 JDK_HOME
first_jdk_home=$(grep -A3 '<version>17</version>'  ~/.m2/toolchains-modest.xml | grep '<jdkHome>' | sed -e 's/.*<jdkHome>//' -e 's#</jdkHome>.*##')
echo "export JAVA_HOME=${first_jdk_home}"
export JAVA_HOME=${first_jdk_home}
./mvnw install -DskipTests
exit 0