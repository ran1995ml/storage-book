import pandas as pd
import os

folder_path = "./file"
for filename in os.listdir(folder_path):
    file_path = os.path.join(folder_path, filename)
    df = pd.read_parquet(file_path)
    df['request_time'] = pd.to_datetime(df['request_time'])
    start_time = pd.Timestamp('2024-04-16 00:02:00')
    end_time = pd.Timestamp('2024-04-16 00:03:00')
    filtered_df = df[(df['request_time'] >= start_time) & (df['request_time'] < end_time)]
    print(len(filtered_df))

# file_name = "./file/amazon-apigateway-new-api-access-logs-7-2024-04-16-00-00-40-0c1b69a1-7a7b-4653-8fe4-b2ab52991e99"

# def parse_message(file_content):
#     count = 0
#     json_str = "{}{}{}".format("}", file_content, "{")
#     json_list = json_str.split("}{")
#     for json_str in json_list[1:-1]:
#         count += 1
#     return count
#
# sum = 0
# sum1 = 0
# with open(file_name, "rb") as f:
#     lines = f.readlines()
#     for line in lines:
#         line = line.decode('utf-8').strip()
#         sum += parse_message(line)
#         request_count = line.count('request_id')
#         sum1 += request_count
#
# print(sum)
# print(sum1)


