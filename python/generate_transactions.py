import random
import datetime
import csv

customers = [f"CUST_{i:04d}" for i in range(1, 101)]
start_date = datetime.date(2023, 1, 1)

with open("transactions.csv", "w", newline="") as f:
    writer = csv.writer(f)
    for i in range(10000):
        customer = random.choice(customers)
        transaction_id = f"TXN_{i:08d}"
        amount = round(random.uniform(10, 1000), 2)
        date = start_date + datetime.timedelta(days=random.randint(0, 364))
        writer.writerow([customer, transaction_id, amount, date])

print("Generated 10,000 transactions")

