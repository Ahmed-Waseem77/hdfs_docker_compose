# HDFS Docker Compose

Minmial Set of Configurations for a HDFS playground.
Comes with some JAVA-Python hdfs clients

## Ports

| Server | RPC/IPC Port | Webapp |
| ---    | ---          | ---    | 
| `jobhistoryserver` | `10020` | `19888` |
| `resourcemanager` | - | `8088` |
| `namenode` | `8020` | `9870` |


## Setup and Running Instructions

1.  **Clone the repository** (if you haven't already):

    ```bash
    git clone <repository_url>
    cd hdfs_docker_compose
    ```

2.  **Start the services:**

    ```bash
    docker-compose up -d
    ```

3.  **Format the Namenode** (First time only):

    ```bash
    docker exec -it namenode hdfs namenode -format -force
    ```

4.  **Access the HDFS Web UI:**

    Open your web browser and go to `http://localhost:9870`

5.  **Interact with HDFS:**

    you can launch an interactive shell in the namenode server as follows:

    ```bash
    docker-compose exec -it namenode bin/bash
    ```

### Closing the multi-container and removing the volumes
```
docker-compose down -v
```

### Running X number of datanodes

To run multiple datanodes, you can scale the `datanode` service in the `docker-compose.yml` file. For example, to run 3 datanodes, use the following command:

```bash
docker-compose up --scale datanode=3 -d
```

This will start 3 instances of the datanode service.

>[!note]
> The default replication factors on the datanodes is `3`
> To change HDFS setup you can visit the `config` file and change it as you wish.

### Running Java Client

To run the Java Client you need to install dependencies with maven:
```bash
mvn clean package
```

Then, you can run the compiled Java class:
```bash
java -jar target/<jar-name>.jar
```

### Running a Java Mapreduce Job

Enter the project directory of a certain job e.g `wordCountjob`
1. Compile The Java main class with maven:
```bash
mvn clean package
```

2. Copy the `jar` to the namenode server (perferably int he ${HADOOP_HOME} Directory:
```bash
docker-compose cp ./target/<jar-name>.jar namenode:/opt/hadoop/
```

3. Run the `jar` from docker compose:
```bash
docker-compose exec -u hadoop namenode hadoop jar <jar-name>.jar <arg1> <arg2> ...
```

Or alternatively launch an interactive shell in the namenode server and run the docker:
```bash
docker-compose exec -u hadoop -it namenode bash
# Now in namenode server shell
hadoop jar <jar-name>.jar <arg1> <arg2> ...
```

### Running Python Client

>[!note]
> Python Client is part of the docker-compose multi-container.

To run the Python client, you can execute the `HDFSClient.py` script inside the `client` container.

```bash
docker-compose exec client pip install hdfs
docker-compose exec client python ./hdfs_client_py/HDFSClient.py
```

### Running a Python Job


>[!important] 
> > As per [this thread](https://serverfault.com/questions/1161816/mirrorlist-centos-org-no-longer-resolve)
> 
> CentOS is EOL as of july 2024, so most mirrorlists do not exist anymore,
> as a workaround you can update the repo files in `/etc/yum.repos.d/` **Inside the namenode server**
> After doing `docker-compose exec -u hadoop -it namenode bash`:
> ```bash
> sed -i 's/mirror\.centos\.org/vault.centos.org/g' /etc/yum.repos.d/CentOS-*.repo
> sed -i 's/^#.*baseurl=http/baseurl=http/g' /etc/yum.repos.d/CentOS-*.repo
> sed -i 's/^mirrorlist=http/#mirrorlist=http/g' /etc/yum.repos.d/CentOS-*.repo
> ```
>
> This updates mirrorlist to use a baseurl instead of the ip addresses of the servers.
> all in all if we keep using `vault.centos.org` for centos packages we will just keep downloading outdated packages with CVEs in them, so it is probably better to migrate.
>
> **you need to replicate the steps above for the nodemanager servers too**\
> To log in to an interactive shell in the nodemanager do `docker-compose exec -u hadoop -it lab1-nodemanager1 bash`\
> If the nodemanager name doesnt match you can alway sdo `docker ps` to see its name, and if it is running

You can then install `python3` on **both** the namenode and the nodemanager with:
```
yum install python3
```

Once python is installed you can copy your scripts, to the **namenode** server:

```bash
docker-compose cp <your-script>.py namenode:/opt/hadoop/
```

and execute it:
```
python3 <your-script>.py
```

To execute it on the hadoop cluster you can do:
```bash
hadoop jar ./share/hadoop/tools/lib/hadoop-streaming-3.3.6.jar \
 -files <your-scirpt>.py,<your-script>.py \
 -mapper "python3 <your-script>.py" \
 -reducer "python3 <your-script>.py" \
 -input <hdfs-input-file-path> \
 -output <hdfs-output-file-path>
```



