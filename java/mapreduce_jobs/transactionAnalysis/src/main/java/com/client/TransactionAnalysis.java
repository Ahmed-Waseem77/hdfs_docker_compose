package com.client;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TransactionAnalysis {

    public static class CustomerStats implements Writable {
        private int transactionCount;
        private double totalAmount;
        private double maxTransaction;

        public CustomerStats() {
            this.transactionCount = 0;
            this.totalAmount = 0.0;
            this.maxTransaction = 0.0;
        }

        public void set(int count, double total, double max) {
            this.transactionCount = count;
            this.totalAmount = total;
            this.maxTransaction = max;
        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeInt(transactionCount);
            out.writeDouble(totalAmount);
            out.writeDouble(maxTransaction);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            transactionCount = in.readInt();
            totalAmount = in.readDouble();
            maxTransaction = in.readDouble();
        }

        @Override
        public String toString() {
            return String.format(
                "Count=%d, Total=%.2f, Max=%.2f, Avg=%.2f",
                transactionCount,
                totalAmount,
                maxTransaction,
                totalAmount / transactionCount);
        }
    }

    public static class TransactionMapper
        extends Mapper<LongWritable, Text, Text, CustomerStats> {
        private Text customerId = new Text();
        private CustomerStats stats = new CustomerStats();

        @Override
        public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
            // Parse CSV: customer_id,transaction_id,amount,date
            String[] fields = value.toString().split(",");
            if (fields.length >= 3) {
                customerId.set(fields[0]);
                double amount = Double.parseDouble(fields[2]);
                stats.set(1, amount, amount);
                context.write(customerId, stats);
            }
        }
    }

    public static class TransactionReducer
        extends Reducer<Text, CustomerStats, Text, CustomerStats> {
        private CustomerStats result = new CustomerStats();

        @Override
        public void reduce(Text key, Iterable<CustomerStats> values, Context context)
            throws IOException, InterruptedException {
            int totalCount = 0;
            double totalAmount = 0.0;
            double maxTransaction = 0.0;
            for (CustomerStats stats : values) {
                totalCount += stats.transactionCount;
                totalAmount += stats.totalAmount;
                maxTransaction = Math.max(maxTransaction, stats.maxTransaction);
            }
            result.set(totalCount, totalAmount, maxTransaction);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "transaction analysis");

        job.setJarByClass(TransactionAnalysis.class);
        job.setMapperClass(TransactionMapper.class);
        job.setReducerClass(TransactionReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(CustomerStats.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

