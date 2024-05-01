import pandas as pd

def convert(filename):
    df = pd.read_csv(filename)
    df['Buggy'] = df['Buggy'].replace({0: 'No', 1: 'Yes'})
    df.to_csv(f'{filename.split(".")[0]}_1.csv', index=False)
convert("full_train.csv")

def create_test_set(filename):
    df = pd.read_csv(filename)
    df_sorted = df.sort_values(by='LOC', ascending=False)
    df_top_75 = df_sorted.head(75)
    df_top_75.to_csv('top75_loc.csv', index=False)
convert("full_test.csv")
create_test_set("full_test_1.csv")

# df1 = pd.read_csv("full_train_1.csv")
