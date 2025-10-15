from hdfs import InsecureClient
import os


class HDFSOperations:
    def __init__(
        self,
        namenode_host="namenode",
        namenode_port=9870,
    ):
        self.client = InsecureClient(
            f"http://{namenode_host}:{namenode_port}", user="hadoop"
        )
        print("Connected successfully to client")

    def upload_file(self, local_path, hdfs_path):
        """Upload file to HDFS"""
        with open(local_path, "rb") as local_file:
            self.client.write(hdfs_path, local_file, overwrite=True)
        print(f"Uploaded {local_path} to {hdfs_path}")

    def read_file(self, hdfs_path):
        """Read file from HDFS"""
        with self.client.read(hdfs_path) as reader:
            content = reader.read()
            return content.decode("utf-8")

    def list_directory(self, hdfs_path):
        """List HDFS directory contents"""
        files = self.client.list(hdfs_path, status=True)
        for name, status in files:
            print(f"(name): (status['length']) bytes")

    def delete_file(self, hdfs_path):
        """Delete file from HDFS"""
        self.client.delete(hdfs_path)
        print(f"Deleted {hdfs_path}")


# Test the client
if __name__ == "__main__":
    hdfs = HDFSOperations()

    # Create test file
    with open("test.txt", "w") as f:
        f.write("Hello from Python HDFS Client!")

    # Test operations
    hdfs.upload_file("test.txt", "/user/test/python_test.txt")
    content = hdfs.read_file("/user/test/python_test.txt")
    print(f"File content: {content}")
    hdfs.list_directory("/user/test")
