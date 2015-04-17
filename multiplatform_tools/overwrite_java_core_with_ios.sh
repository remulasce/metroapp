# This script takes the .java files that are in the iOS branch and copies them into the Android branch, where the 'real' java_core lives.
# This is used to merge changes to java_core in iOS into the Android main branch.
#
# It helps because of reasons, I think.

cp -R ../MetroApp/metroapp/app/* ../app/