#!/bin/sh
# ============================================================================
# Docker Compose 环境启动脚本
# 
# 功能说明：
#   1. 检测并安装必要的工具（Rosetta、Homebrew、Docker、docker-compose、colima）
#   2. 启动 colima 虚拟机（用于在 macOS 上运行 Docker）
#   3. 启动测试环境所需的 Docker 容器（FTP、SSH、MySQL、DB2）
# 
# 使用场景：
#   - 在 macOS（特别是 Apple Silicon）上为单元测试准备 Docker 容器环境
#   - 确保测试所需的 SSH、FTP、MySQL、DB2 等服务容器正常运行
# 
# 依赖：
#   - macOS 系统
#   - Homebrew（如未安装会自动安装）
# ============================================================================


# 设置终端类型
export TERM=xterm


# 设置严格的错误处理模式
# -e：命令执行失败时立即退出
# -u：使用未定义变量时报错
# -o pipefail：管道中任意一环失败就算失败
set -euo pipefail


# ============================================================================
# 1. 检测系统架构并安装 Rosetta（Apple Silicon 需要）
# ============================================================================
ARCH=$(uname -m)
if [[ "$ARCH" == "arm64" ]]; then
  echo "Detected Apple Silicon ($ARCH)."

  # 检查 Rosetta 是否已安装（Rosetta 用于在 Apple Silicon 上运行 x86_64 应用）
  if /usr/bin/pgrep oahd >/dev/null 2>&1; then
    echo "Rosetta is already installed."
  else
    echo "Installing Rosetta 2..."
    # 安装 Rosetta 2（用于运行 x86_64 架构的 Docker 镜像）
    softwareupdate --install-rosetta --agree-to-license
    if [[ $? -eq 0 ]]; then
      echo "Rosetta installed successfully."
    else
      echo "Failed to install Rosetta 2. Please try manually: sudo softwareupdate --install-rosetta"
    fi
  fi
else
  echo "This Mac is Intel-based ($ARCH). Rosetta is not required."
fi


# ============================================================================
# 2. 安装 Homebrew（macOS 包管理器）
# ============================================================================
if ! command -v brew >/dev/null 2>&1; then
  # 配置国内镜像源（加速下载）
  export HOMEBREW_BOTTLE_DOMAIN=https://mirrors.aliyun.com/homebrew/homebrew-bottles
  export HOMEBREW_API_DOMAIN="https://mirrors.aliyun.com/homebrew/homebrew-bottles/api"
  # 使用国内脚本安装 Homebrew（速度更快）
  /bin/zsh -c "$(curl -fsSL https://gitee.com/cunkai/HomebrewCN/raw/master/Homebrew.sh)" speed
  # 加载环境变量
  source ~/.zprofile
else
  echo "brew is already installed."
fi


# ============================================================================
# 3. 安装 Docker 相关工具
# ============================================================================
# 安装 Docker CLI（命令行工具）
if ! command -v docker >/dev/null 2>&1; then
  brew install docker
else
  echo "docker is already installed."
fi

# 安装 docker-compose（用于管理多容器应用）
if ! command -v docker-compose >/dev/null 2>&1; then
  brew install docker-compose
else
  echo "docker-compose is already installed."
fi

# 安装 colima（在 macOS 上运行 Docker 的轻量级虚拟机）
# colima 是 Docker Desktop 的替代方案，资源占用更少
if ! command -v colima >/dev/null 2>&1; then
  brew install colima
  # 可选：使用自定义磁盘镜像启动（已注释）
  # colima start --disk-image /Users/user/Library/CloudStorage/SynologyDrive-Jeremy/下载目录/ubuntu-24.04-minimal-cloudimg-arm64-docker.qcow2
else
  echo "colima is already installed."
fi


# ============================================================================
# 4. 启动 colima 虚拟机
# ============================================================================
if ! colima status >/dev/null 2>&1; then
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
  DISK_SIZE=100

  # 显示资源分配信息
  echo "cpu: ${CPU_ALLOC}"
  echo "memory: ${MEM_ALLOC}G"
  echo "disk: ${DISK_SIZE}G"

  # 创建 colima 配置目录
  mkdir -p ~/.colima/default
  # 生成 colima 配置文件
cat <<EOF > ~/.colima/default/colima.yaml
cpu: ${CPU_ALLOC}                    # CPU 核心数
memory: ${MEM_ALLOC}                  # 内存大小（GB）
disk: ${DISK_SIZE}                    # 磁盘大小（GB）
arch: aarch64                         # 架构类型（ARM64）
runtime: docker                       # 容器运行时
hostname: colima                      # 虚拟机主机名
kubernetes:
  enabled: true                       # 启用 Kubernetes
  version: v1.33.4+k3s1              # Kubernetes 版本
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
  colima start
else
  echo "colima is already running."
fi


# ============================================================================
# 5. 切换到 colima Docker 上下文
# ============================================================================
# 将 Docker 上下文切换到 colima（使用 colima 虚拟机中的 Docker）
echo "docker context use colima"
docker context use colima


# ============================================================================
# 6. 启动测试环境所需的 Docker 容器
# ============================================================================

# ----------------------------------------------------------------------------
# 6.1 启动 FTP 服务器容器
# ----------------------------------------------------------------------------
dockerContainerID=`docker ps --filter "name=ftp-server" -q`
if [ -n "${dockerContainerID}" ]; then
  echo "ftp-server is already running."
else
  # 使用 docker-compose 启动 FTP 容器（后台运行）
  docker-compose up -d ftp
fi

# ----------------------------------------------------------------------------
# 6.2 启动 SSH 服务器容器
# ----------------------------------------------------------------------------
dockerContainerID=`docker ps --filter "name=ssh-server" -q`
if [ -n "${dockerContainerID}" ]; then
  echo "ssh-server is already running."
else
  # 使用 docker-compose 启动 SSH 容器（后台运行）
  docker-compose up -d ssh
fi

# ----------------------------------------------------------------------------
# 6.3 启动 MySQL 服务器容器
# ----------------------------------------------------------------------------
dockerContainerID=`docker ps --filter "name=mysql-server" -q`
if [ -n "${dockerContainerID}" ]; then
  echo "mysql-server is already running."
else
  # 清理旧的 MySQL 配置和数据（确保干净的测试环境）
  rm -rf ~/mysql/conf/*
  rm -rf ~/mysql/data/*
  # 创建必要的目录
  mkdir -p ~/mysql/conf
  mkdir -p ~/mysql/data
  # 使用 docker-compose 启动 MySQL 容器（后台运行）
  docker-compose up -d mysql
fi

# ----------------------------------------------------------------------------
# 6.4 启动 DB2 服务器容器
# ----------------------------------------------------------------------------
dockerContainerID=`docker ps --filter "name=db2-server" -q`
if [ -n "${dockerContainerID}" ]; then
  echo "db2-server is already running."
else
  # 使用 docker-compose 启动 DB2 容器（后台运行）
  docker-compose up -d db2

  # 等待 DB2 数据库初始化完成（最多等待 30 分钟）
  timeout=1800  # 超时时间（秒）= 30 分钟
  start_time=$(date +%s)  # 记录开始时间

  echo "wait DB2 Docker start .."
  # 循环检查 DB2 是否已启动完成
  while true; do
    # 检查日志中是否出现数据库创建成功的标志
    if docker-compose logs db2 | grep "Local database alias   = TESTDB" > /dev/null 2>&1; then
      # 再等待 20 秒确保数据库完全就绪
      sleep 20
      echo "DB2 is already start."
      break
    fi

    # 检查是否超时
    now=$(date +%s)
    if (( now - start_time > timeout )); then
      echo "等待超时，数据库未创建"
      exit 1
    fi
    # 每 10 秒检查一次
    sleep 10
  done
fi

# ============================================================================
# 7. 常用命令参考（已注释，需要时可取消注释使用）
# ============================================================================

# SFTP 连接示例（使用 user 用户）
# sshpass -p 'user' sftp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -P 2222 user@127.0.0.1

# SSH 连接示例（使用 user 用户，端口 22222）
# sshpass -p 'user' ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -p 22222 user@127.0.0.1

# SSH 连接示例（使用 root 用户，端口 22222）
# sshpass -p 'root' ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -p 22222 root@127.0.0.1

# SFTP 连接示例（使用 root 用户，端口 22222）
# sshpass -p 'root' sftp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -P 22222 root@127.0.0.1

# FTP 连接示例（使用 ftpuser 用户，端口 2121）
# sshpass -p 'ftpuser' ftp ftpuser@127.0.0.1 2121

# 进入 MySQL 容器并连接数据库
# docker exec -it $(docker ps --filter "name=mysql" --format "{{.Names}}") mysql -u root -p'root'

# 进入 DB2 容器（用于调试）
# docker exec -it $(docker ps --filter "name=db2-server" --format "{{.Names}}") bash

# ============================================================================
# 脚本执行完成
# ============================================================================
exit 0
