echo "hello hadoop world" > sample.txt
for i in {1..1000}; do
  echo "line: $i: random data $(date +%s%n | sha256sum | base64 | head -c 50)" >> large_file.txt
done
