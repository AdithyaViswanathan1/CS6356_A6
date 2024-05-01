Note: run in another directory having latest pdfbox repo cloned locally
git log 2.0.26 --pretty=format:'%h - %s' --diff-filter=M --name-status > /Users/adithya/Documents/code/6356_a6/logs/v2.0.26.txt
git log 2.0.27 --pretty=format:'%h - %s' --diff-filter=M --name-status > /Users/adithya/Documents/code/6356_a6/logs/v2.0.27.txt
git log 2.0.28 --pretty=format:'%h - %s' --diff-filter=M --name-status > /Users/adithya/Documents/code/6356_a6/logs/v2.0.28.txt
git log 2.0.29 --pretty=format:'%h - %s' --diff-filter=M --name-status > /Users/adithya/Documents/code/6356_a6/logs/v2.0.29.txt
git log 2.0.30 --pretty=format:'%h - %s' --diff-filter=M --name-status > /Users/adithya/Documents/code/6356_a6/logs/v2.0.30.txt
git log 2.0.31 --pretty=format:'%h - %s' --diff-filter=M --name-status > /Users/adithya/Documents/code/6356_a6/logs/v2.0.31.txt

FILES:
- pdfbox-2.0.XX directories: pdfbox repositories for each version
- logs directory: logs obtained by running the above 'git log' commands
- metrics directory: metrics from CodeMR after excel processing
- path_and_buggy_creation.py: parses git log and accesses JIRA API to create two columns of training/testing set: Class path and Buggy column (0/1)
- buggy_jsons directory: jsons containing class paths and buggy boolean for each version
- combine_metrics.py: Combine buggy metric and CodeMR metrics to create training set
- weka_processing.py: Fix buggy column for WEKA acceptance. And create actual testing set as per assignment instructions.
- full_train_1.csv: final training set
- top75_loc.csv: final testing set

