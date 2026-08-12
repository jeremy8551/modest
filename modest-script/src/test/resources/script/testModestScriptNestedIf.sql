# modest-script 1.0.5 嵌套 if 解析测试
# 预期：输出 THIRD_LEVEL，且不输出 ELSEIF_BRANCH

set firstCondition = true
set secondCondition = true
set thirdCondition = true
set elseifCondition = false

if firstCondition then
  if secondCondition then
    if thirdCondition then
      echo "THIRD_LEVEL"
    fi
  elseif elseifCondition then
    echo "ELSEIF_BRANCH"
  fi
fi

echo "TEST_COMPLETED"
