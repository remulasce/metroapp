import sqlite3

# For "a","b",c,"d"
# Return string array {a, b, c, d}
def cleanFormatString(format):
    return [s.strip('"') for s in format.split(",")]

# Returns the index of name in format, or -1 if not present.
def formatIndex(format, name):
    if (not name in format):
        return -1
    return format.index(name)

# For an array and index, return the item at index or return
# -1 if index is -1
def getItem(split, index):
    if (index == -1):
        return -1
    return split[index]

def writeToDb(dbName, tablename, params, values):
    conn = sqlite3.connect(dbName)
    c = conn.cursor()
    # Delete old contents, if any
    c.execute('''DROP TABLE IF EXISTS ''' + tablename)
    # Create table
    c.execute('''CREATE TABLE ''' + tablename + "("+ params + ")")
    c.executemany('INSERT INTO ' + tablename + ' VALUES (' + '?,'*(len(values[0])-1) + '?)', values)
    conn.commit()
    conn.close()