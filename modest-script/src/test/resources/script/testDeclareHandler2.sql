


echo "----------"

handler



if `handler | wc -l` != 3 then
  echo "The child script did not inherit the exception handling logic from the parent script."
  exit 1
fi

