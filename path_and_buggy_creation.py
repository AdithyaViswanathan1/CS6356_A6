# different versions: https://github.com/apache/pdfbox/tags
import os
import requests
import re
import json
import time
import httpx

def is_substring_of_keys(string, dictionary):
    for key in dictionary.keys():
        if string in key:
            return key
    return None

def write_to_json(file_path, data):
    with open(file_path, "w") as json_file:
        json.dump(data, json_file, indent=4)

    print("JSON data has been written to", file_path)

def is_bug(issue):
    url = f"https://issues.apache.org/jira/rest/api/2/issue/{issue}"

    response = None
    while response is None:
        response_time_start = time.time()
        try:
            response = requests.get(url, timeout=10)
        except:
            print(issue, "failed")
        response_time_end = time.time()
        # print("Response time", response_time_end - response_time_start)

    if response.status_code == 200:
        data = response.json()
        issue_type = data['fields']['issuetype']['name']
        # print(issue_type)
        if issue_type.lower() == 'bug':
            return True
        return False
    else:
        print("Failed to fetch data. Status code:", response.status_code)
    return None

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
    paths = {}
    class_names = []
    for root, dirs, files in os.walk(directory):
        # print("ROOT",root)
        for file_name in files:
            if file_name.endswith(".java"):
                # Print the absolute path of each file
                # file_path = os.path.join(root, file_name)
                class_names.append(file_name)
                file_path = os.path.relpath(os.path.join(root, file_name), directory)
                file_path = f"{file_path}-{get_version_name(directory)}"
                # print(file_path)
                if '/test/' in file_path:
                    continue
                paths[file_path] = 0

    # print("Should be equal:", len(class_names), len(set(class_names)))
    # duplicates = [item for item in class_names if class_names.count(item) > 1]

    # print(len(set(duplicates)), "Duplicates:", set(duplicates))
    return paths

# Example usage:
print("started")
repository_paths = ['pdfbox-2.0.26','pdfbox-2.0.27','pdfbox-2.0.28','pdfbox-2.0.29','pdfbox-2.0.30','pdfbox-2.0.31']

'''
To compute the number of bugs in a class C, 
find all commits that change the file of class C between the two considered
versions and link it to an issue in the issue tracker. 
If the issue is a bug report, then you mark/add one bug for class C. 

Plan:
1. dict will have structure {"SplitBooklet.java": "PDFBOX-3423", "PDFHighlighter.java": "PDF-BOX-5551", ...}
2. iterate through git log
3. Add its classes and issue# to the dict

4. Loop through dict
5. if issue is a bug, then add 1 to class
'''
buggy_dir = "buggy_jsons"
if not os.path.exists(buggy_dir):
    os.makedirs(buggy_dir)
    print(f"Directory '{buggy_dir}' created successfully.")
else:
    print(f"Directory '{buggy_dir}' already exists.")

for i in range(len(repository_paths)):
    curr_version = repository_paths[i]
    print(curr_version, "started")
    buggy_dict = iterate_files_in_directory(curr_version)
    file = open(f"logs/v{get_version_name(curr_version)}.txt").read()
    lines = file.split('\n\n')
    # print("total commits", len(lines))
    all_time = time.time()
    times_updated = 0
    for i,line in enumerate(lines):
        total_start = time.time()
        
        part1_start = time.time()
        if i % 500 == 0:
            print(f"COMMIT {i}/{len(lines)}")
        try:
            if "- PDFBOX" in line.split("\n")[1]:
                #print("!!",line,"!!")
                line = '\n'.join(line.split("\n")[1:])
        except Exception as e:
            continue
        # get commit number
        commit_number = line[:9]
        # print(commit_number)
        
        # get issue id
        sublines = line.split("\n")
        first_line = sublines[0]
        pattern = r'(PDFBOX-\s*\d+):'

        # Search for the pattern in the line
        matches = re.findall(pattern, first_line)
        if len(matches) == 0:
            continue
        issue_id = matches[0]
        # print("ISSUE", issue_id)
        part1_end = time.time()
        # print("PART 1", part1_end - part1_start)

        part2_start = time.time()
        bug = is_bug(issue_id)
        part2_end = time.time()
        # print("PART 2", part2_end - part2_start)

        if not bug:
            continue
        
        files_lines = sublines[1:]
        for file in files_lines:
            if not file.endswith(".java"):
                continue
            parts = file.split("\t")
            if parts[0] == "":
                break
            character = parts[0]
            filename = parts[1]
            # if issue is a bug report, then add 1 to this class's bug counter and link this class to this issue
            key = is_substring_of_keys(filename, buggy_dict)
            if key is not None:
                # print("updating dict", commit_number, key)
                times_updated += 1
                buggy_dict[key] = 1
            # else:
            #     print("FILE NOT FOUND IN SCANNED LIST", filename)
        total_end = time.time()
        elapsed_time = total_end - total_start
        # print("TIME: ", elapsed_time, "\n")

    all_time_end = time.time()
    print("TOTAL", round(all_time_end - all_time,4), "seconds")
    write_to_json(f"{buggy_dir}/buggy_{get_version_name(curr_version)}.json", buggy_dict)
    # print("TIMES UPDATED", times_updated)
    # print(is_bug("PDFBOX-4071"))

