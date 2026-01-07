#!/bin/sh
# =============================================================================
# Author:             吕钊军
# Created Date:       2025-10-21
# Last Modified:      2025-10-30
# Version:            1.0
# Description:        安装 brew, Docker, Docker app, Docker Hub, 生成Docker镜像
# =============================================================================

# -e：命令错误退出 -u：使用未定义变量时报错 -o pipefail：管道中任意一环失败就算失败
# shellcheck disable=SC3040
set -euo pipefail


# 设置终端类型与进入目录
export TERM=xterm && cd ${PROJECT_BASEDIR}


# 检测当前 Mac 是否为 Apple Silicon (arm64 架构)
ARCH=$(uname -m)
# shellcheck disable=SC3010
if [ "$ARCH" = "arm64" ]; then
  echo "detected Apple Silicon ($ARCH)."

  # 检查 Rosetta 是否已安装
  if /usr/bin/pgrep oahd >/dev/null 2>&1; then
    echo "rosetta is already installed."
  else
    echo "installing Rosetta 2..."
    if softwareupdate --install-rosetta --agree-to-license; then
      echo "rosetta installed successfully."
    else
      echo "failed to install Rosetta 2. Please try manually: sudo softwareupdate --install-rosetta"
    fi
  fi
else
  echo "this Mac is Intel-based ($ARCH). Rosetta is not required."
fi


# MacOS 包管理
if ! command -v brew >/dev/null 2>&1; then
  export HOMEBREW_BOTTLE_DOMAIN=https://mirrors.aliyun.com/homebrew/homebrew-bottles
  export HOMEBREW_API_DOMAIN="https://mirrors.aliyun.com/homebrew/homebrew-bottles/api"
  /bin/zsh -c "$(curl -fsSL https://gitee.com/cunkai/HomebrewCN/raw/master/Homebrew.sh)" speed
  . "${HOME}/.zprofile"
else
  echo "brew is already installed."
fi


# 安装 Docker
if ! command -v docker >/dev/null 2>&1; then
  brew install docker
else
  echo "docker is already installed."
fi


# 安装 Docker app
if [ ! -d /Applications/Docker.app ]; then
  brew install --cask docker
else
  echo "docker-desktop is already installed."
fi


# 检查 Docker 是否已启动
echo "start docker-desktop in the background .."
open -g -a /Applications/Docker.app
echo "waiting for docker-desktop to start .."
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
echo "waiting for Docker daemon to be ready."
sleep 2
timeout=60
while ! docker info >/dev/null 2>&1; do
    sleep 2
    timeout=$((timeout-2))
    if [ $timeout -le 0 ]; then
        echo "docker daemon did not start in time."
        exit 1
    fi
done


# 删除悬空镜像
echo "docker image prune -f"
docker image prune -f


# 删除镜像
echo "docker rmi -f ${DOCKER_HUB_USERNAME}/jdksetup:${REVISION}"
docker rmi -f "${DOCKER_HUB_USERNAME}"/jdksetup:${REVISION} 2>/dev/null
docker rmi -f "${DOCKER_HUB_USERNAME}"/jdksetup:latest 2>/dev/null


# 构建镜像
echo "docker build -f target/Dockerfile -t ${DOCKER_HUB_USERNAME}/jdksetup:${REVISION} ."
docker build -f target/Dockerfile -t "${DOCKER_HUB_USERNAME}"/jdksetup:${REVISION} .

echo "docker build -f target/Dockerfile -t ${DOCKER_HUB_USERNAME}/jdksetup:latest ."
docker build -f target/Dockerfile -t "${DOCKER_HUB_USERNAME}"/jdksetup:latest .


# 登陆 Docker Hub
echo "login Docker Hub .."
echo "${DOCKER_HUB_PASSWORD}" | docker login -u ${DOCKER_HUB_USERNAME} --password-stdin


# 登陆推送
echo "docker push ${DOCKER_HUB_USERNAME}/jdksetup:${REVISION}"
docker push "${DOCKER_HUB_USERNAME}"/jdksetup:${REVISION}
docker push "${DOCKER_HUB_USERNAME}"/jdksetup:latest
docker logout
exit 0