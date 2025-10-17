import random
import datetime

# Generate sample web logs
ips = [f"192.168.1.{i}" for i in range(1, 20)]
urls = [
    "/index.html",
    "/api/users",
    "/api/products",
    "/login",
    "/logout",
    "/dashboard",
    "/settings",
    "/help",
    "/about",
    "/contact",
]
methods = ["GET", "POST", "PUT", "DELETE"]
statuses = [200, 201, 204, 301, 302, 400, 401, 403, 404, 500]

with open("web_logs.txt", "w") as f:
    base_time = datetime.datetime(2023, 7, 1, 0, 0, 0)
    for i in range(10000):
        ip = random.choice(ips)
        timestamp = (
            base_time
            + datetime.timedelta(
                hours=random.randint(0, 23),
                minutes=random.randint(0, 59),
                seconds=random.randint(0, 59),
            )
        ).strftime("%d/%b/%Y:%H:%M:%S +0000")
        method = random.choice(methods)
        url = random.choice(urls)
        status = random.choice(statuses)
        bytes_sent = random.randint(100, 10000)
        log_line = f'{ip} - - [{timestamp}] "{method} {url} HTTP/1.1" {status} {bytes_sent}\n'
        f.write(log_line)

print("Generated 10,000 log entries")

