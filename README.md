# HDFS Docker Compose

Minmial Set of Configurations for a HDFS playground.
Comes with some JAVA-Python hdfs clients

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
> To change HDFS setup you can visit the `config` file and change it as you wish.

### Running Java Client

To run the Java Client you need to install dependencies with maven:
```
mvn clean package
```

Then, you can run the compiled Java class:
```
java -jar target/<jar-name>.jar
```

### Running Python Client

>[!note]
> Python Client is part of the docker-compose multi-container.

To run the Python client, you can execute the `HDFSClient.py` script inside the `client` container.

```bash
docker-compose exec client pip install hdfs
docker-compose exec client python ./hdfs_client_py/HDFSClient.py
```
