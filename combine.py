import os
import json
import pandas as pd

def create_buggy_column():
    directory = 'buggy_jsons'
    # List all files in the directory
    json_files = [file for file in os.listdir(directory) if file.endswith('.json')]

    train_data = {}
    test_data = {}

    # Iterate over each JSON file and read its contents
    for file_name in json_files:
        file_path = os.path.join(directory, file_name)
        with open(file_path, 'r') as file:
            json_data = json.load(file)
            # Process the JSON data as needed
            if "31" in file_name:
                print("found test data")
                test_data.update(json_data)
            else:
                train_data.update(json_data)

    train_df = pd.DataFrame([{'Path': key, 'Buggy': value} for key, value in train_data.items()])
    test_df = pd.DataFrame([{'Path': key, 'Buggy': value} for key, value in test_data.items()])
    return train_df,test_df

def combine_with_metrics(buggy_train, buggy_test):
    directory = "metrics"

    csv_files = sorted([file for file in os.listdir(directory) if file.endswith('.csv')])


    dfs_train = []
    dfs_test = []

    # Iterate over each csv file and read its contents
    for file_name in csv_files:
        file_path = os.path.join(directory, file_name)
        with open(file_path, 'r') as file:
            if "31" in file_path:
                dfs_test.append(pd.read_csv(file_path))
            else:
                # print("File", file_name, "added to df")
                temp = pd.read_csv(file_path)
                dfs_train.append(temp)
            

    # Concatenate DataFrames into a single DataFrame
    combined_train_df = pd.concat(dfs_train, ignore_index=True)
    # print("1",combined_train_df.loc[4390:4395])
    combined_train_df = combined_train_df.drop(columns=['QualifiedName'])
    # print("2",combined_train_df.loc[4390:4395])
    combined_train_df.rename(columns={'ModifiedName': 'Path'}, inplace=True)
    # print("3",combined_train_df.loc[4390:4395])
    nan_rows_train = combined_train_df[combined_train_df['Path'].isna()].index.tolist()
    # print(len(nan_rows_train), nan_rows_train)
    # print("4",combined_train_df.loc[4390:4395])


    combined_test_df = pd.concat(dfs_test, ignore_index=True)
    combined_test_df = combined_test_df.drop(columns=['QualifiedName'])
    combined_test_df.rename(columns={'ModifiedName': 'Path'}, inplace=True)

    # print(combined_train_df.head())
    # print(combined_test_df.head())
    # print(combined_train_df.shape, combined_test_df.shape)
    # print(combined_train_df.columns)
    # print(combined_test_df.columns)

    # Iterate over each row in DataFrame A and find matching rows in DataFrame B
    # print(combined_train_df.shape)
    # print(combined_train_df.iloc[4419])
    
    matched_rows = []
    for _, row_A in combined_train_df.iterrows():
        if _ % 500 == 0:
            print(f"{_}/{combined_train_df.shape[0]}")
        for _, row_B in buggy_train.iterrows():
            try:
                if row_A['Path'] in row_B['Path']:
                    matched_rows.append((row_B['Path'], row_A['CBO'], row_A['DIT'], row_A['WMC'], row_A['LOC'], row_A['LCOM'], row_B['Buggy']))
            except:
                print("ROW", _)
                print("rowA", row_A)
                print("rowB", row_B)
                break
    # Create a DataFrame from the matched rows
    result = pd.DataFrame(matched_rows, columns=["Path", "CBO", "DIT", "WMC", "LOC", "LCOM", "Buggy"])
    print(result.head())
    print(result.shape)
    result.to_csv("full_train.csv", index=False)

def combine_with_metrics_test(buggy_test):
    directory = "metrics"

    csv_files = sorted([file for file in os.listdir(directory) if file.endswith('.csv')])

    dfs_test = []
    
    # Iterate over each csv file and read its contents
    for file_name in csv_files:
        file_path = os.path.join(directory, file_name)
        with open(file_path, 'r') as file:
            if "31" in file_path:
                dfs_test.append(pd.read_csv(file_path))

    # Concatenate DataFrames into a single DataFrame
    combined_test_df = pd.concat(dfs_test, ignore_index=True)
    # print("1",combined_train_df.loc[4390:4395])
    combined_test_df = combined_test_df.drop(columns=['QualifiedName'])
    # print("2",combined_train_df.loc[4390:4395])
    combined_test_df.rename(columns={'ModifiedName': 'Path'}, inplace=True)
    # print("3",combined_train_df.loc[4390:4395])
    nan_rows_test = combined_test_df[combined_test_df['Path'].isna()].index.tolist()
    print(len(nan_rows_test))
    # print("4",combined_train_df.loc[4390:4395])

    matched_rows = []
    for _, row_A in combined_test_df.iterrows():
        if _ % 500 == 0:
            print(f"{_}/{combined_test_df.shape[0]}")
        for _, row_B in buggy_test.iterrows():
            try:
                if row_A['Path'] in row_B['Path']:
                    matched_rows.append((row_B['Path'], row_A['CBO'], row_A['DIT'], row_A['WMC'], row_A['LOC'], row_A['LCOM'], row_B['Buggy']))
            except:
                print("ROW", _)
                print("rowA", row_A)
                print("rowB", row_B)
                break
    # Create a DataFrame from the matched rows
    result = pd.DataFrame(matched_rows, columns=["Path", "CBO", "DIT", "WMC", "LOC", "LCOM", "Buggy"])
    print(result.head())
    print(result.shape)
    result.to_csv("full_test.csv", index=False)



buggy_train, buggy_test = create_buggy_column()
# combine_with_metrics(buggy_train, buggy_test)
combine_with_metrics_test(buggy_test)
# print(train_df.head())
# print(train_df.shape, test_df.shape)
# print(train_df['Buggy'].value_counts())
# print(test_df['Buggy'].value_counts())
