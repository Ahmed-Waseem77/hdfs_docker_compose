for analysis in status ip url hour bytes; do
  echo "Running $analysis analysis..."
  hadoop jar loganalyzer.jar /user/$(whoami)/logs/web_logs.txt \
    /user/$(whoami)/logs/output_$analysis \
    $analysis

  echo "Results for $analysis:"
  hdfs dfs -cat /user/$(whoami)/logs/output_$analysis/part-r-00000 | head -10
done
