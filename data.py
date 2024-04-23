# different versions: https://github.com/apache/pdfbox/tags
import os

def get_version_name(repository_name):
    return repository_name.split("-")[1]

def count_file_types(directory):
    file_type_count = {}  # Using a dictionary to store file extension counts
    # Iterate through all files and directories in the given directory
    for root, dirs, files in os.walk(directory):
        for file_name in files:
            # Extract the file extension
            _, extension = os.path.splitext(file_name)
            # Increment the count for the file extension
            file_type_count[extension] = file_type_count.get(extension, 0) + 1
    return file_type_count

def iterate_files_in_directory(directory):
    # Iterate through all files and directories in the given directory
    paths = []
    for root, dirs, files in os.walk(directory):
        for file_name in files:
            if file_name.endswith(".java"):
                # Print the absolute path of each file
                # file_path = os.path.join(root, file_name)
                file_path = os.path.relpath(os.path.join(root, file_name), directory)
                file_path = f"{file_path}-{get_version_name(directory)}"
                # print(file_path)
                paths.append(file_path)
    return paths

# Example usage:
print("started")
repository_paths = ['pdfbox-2.0.26','pdfbox-2.0.27','pdfbox-2.0.28','pdfbox-2.0.29','pdfbox-2.0.30','pdfbox-2.0.31']

# get number of each file type (ex. .java 100, .csv 12, etc)
# file_type_count = count_file_types(repository_path)
# print(f"Count of each file type in v{version_name}:")
# for file_type, count in file_type_count.items():
#     print(f"{file_type}:\t {count}")

# iterate through all files in directory
for repository_path in repository_paths:
    print("\nVersion:",repository_path)
    paths = iterate_files_in_directory(repository_path)
    print("Number of files:", len(paths))
    print("Example paths:")
    for path in paths[1000:1005]:
        print(path)

