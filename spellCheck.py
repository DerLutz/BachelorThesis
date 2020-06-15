import pymongo

'''
	Calculates the Levensthtein-Distance between to words. The case of a-z is ignored
	:param	a (Type: String): first word
	:param	b (Type: String): second word
	:returns (Type: int): the calculated distance
'''
def levenshtein(a, b):
	s = a.upper()
	t = b.upper()

	if s == t:
		return 0
	if len(a) == 0:
		return len(b)
	if len(b) == 0:
		return len(a)

	matrix = []

	for i in range(0, len(s) + 1):
		matrix.append([])
		for j in range(0, len(t) + 1):
			if i != 0 and j != 0:
				if s[i - 1] == t[j - 1]:
					matrix[i].append(matrix[i-1][j-1])
				else:
					value = min(
						matrix[i][j-1],
						matrix[i-1][j],
						matrix[i-1][j-1]
						)
					matrix[i].append(value + 1)
			else:
				matrix[i].append(max(i, j))

	return matrix[len(s)][len(t)]

def main(text, database):

	name = text

	myclient = pymongo.MongoClient("mongodb+srv://test:test123@cluster0-vafta.mongodb.net/test?retryWrites=true&w=majority")

	#print(myclient.list_database_names())

# Check database exists
	dblist = myclient.list_database_names()
	#if "receiptdatabase" in dblist:
		#print("The database exists.")

	mydb = myclient.receiptdatabase

# get all products bought
	if database == "product":
		col = mydb.products
	elif database == "receipt":
		col = mydb.receipts
	else:
		print("wrong database input")
		return "Error"


	bestResult = len(text)
	
	for x in col.find():
		product = x["name"]
		result = levenshtein(text, product)
		if len(text) < len(product):
			length = len(text)
		else:
			length = len(product)

# if words are similar 75% or higher, possibility high enough that correct word ist product 
		if (result < length/4) and (result < bestResult):
			#print("Found similar word")
			name = product
			bestResult = result

# stop searching if found word in dataset
		if result == 0 :
			break


	return name
