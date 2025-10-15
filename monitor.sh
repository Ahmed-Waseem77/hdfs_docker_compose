export PATH=$PATH:/usr/local/sbin:/usr/local/bin:/opt/hadoop/bin:/opt/hadoop/sbin
export JAVA_HOME=/usr/lib/jvm/jre/

for i in {1..10}; do
  echo "Test file $i" > test_$i.txt
  hdfs dfs -put test_$i.txt /user/input/
  sleep 2
done
