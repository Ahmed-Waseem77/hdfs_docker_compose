package com.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

/**
 * A simple client for interacting with the Hadoop Distributed File System (HDFS).
 * This class provides basic functionality to upload, read, and list files.
 */
public class HDFSClient {

    private FileSystem fs;

    /**
     * Constructor to initialize the HDFS client.
     * It sets up the configuration to connect to an HDFS instance running on localhost.
     * @throws IOException if there is an issue connecting to HDFS.
     */
    public HDFSClient() throws IOException {
        Configuration conf = new Configuration();
        // Set the default file system to your HDFS NameNode address
        conf.set("fs.defaultFS", "hdfs://192.168.8.8:8020");
        fs = FileSystem.get(conf);
    }

    /**
     * Uploads a local file to a specified destination in HDFS.
     * @param source The local file path.
     * @param dest The destination path in HDFS.
     * @throws IOException if the file transfer fails.
     */
    public void uploadFile(String source, String dest) throws IOException {
        Path srcPath = new Path(source);
        Path dstPath = new Path(dest);
        fs.copyFromLocalFile(srcPath, dstPath);
        System.out.println("Upload completed for: " + dest);
    }

    /**
     * Reads a file from HDFS and prints its content to the console.
     * @param filePath The path of the file to read in HDFS.
     * @throws IOException if the file cannot be read.
     */
    public void readFile(String filePath) throws IOException {
        Path path = new Path(filePath);
        FSDataInputStream in = fs.open(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();
        in.close();
    }

    /**
     * Lists all files in a given HDFS directory.
     * @param dir The directory path in HDFS.
     * @throws IOException if there is an issue accessing the directory.
     */
    public void listFiles(String dir) throws IOException {
        Path path = new Path(dir);
        RemoteIterator<LocatedFileStatus> files = fs.listFiles(path, true);
        System.out.println("\nListing files in: " + dir);
        while (files.hasNext()) {
            LocatedFileStatus fileStatus = files.next();
            System.out.println(
                fileStatus.getPath() + "\t" + fileStatus.getLen() + " bytes"
            );
        }
    }

    /**
     * The main method to demonstrate the HDFS client functionality.
     * @param args Command line arguments (not used).
     * @throws IOException if any HDFS operation fails.
     */
    public static void main(String[] args) throws IOException {
        HDFSClient client = new HDFSClient();

        // Example Usage:
        // Make sure you have a 'sample.txt' file in your project's root directory.
        System.out.println("--- Starting HDFS Operations ---");

        // 1. Upload a file
        client.uploadFile("sample.txt", "/user/test/sample.txt");

        // 2. Read the uploaded file
        System.out.println("\n--- Reading file from HDFS ---");
        client.readFile("/user/test/sample.txt");

        // 3. List files in the directory
        System.out.println("\n--- Listing files in HDFS directory ---");
        client.listFiles("/user/test");

        System.out.println("\n--- HDFS Operations Complete ---");
    }
}
