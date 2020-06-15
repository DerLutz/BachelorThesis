from flask import json, Flask, request, url_for, redirect, jsonify
import pymongo

app = Flask(__name__)


@app.route('/getData', methods = ['POST', 'GET'])
def getData():
  #print(request.headers)
  received_data = request.data.decode('utf-8')
  data_rec = received_data.split('\n', 1)[0]
  print("data_rec: " + data_rec)
  if request.headers['Content-Type'] == 'text/plain':
    if (data_rec == "MainActivity"):
      jsonData = data()
      return jsonData
    if (data_rec == "Receipt"):		# called in Receipt
      name = received_data.split('\n', 1)[1]
      print("Name: " + name)
      jsonData = data_receipt(name)
      return jsonData      
    if (data_rec == "Company"): 	#called in Company
      jsonData = data_company()
      return jsonData
    if (data_rec == "Product"):        #is Receipt in Android App ??
      receiptID = received_data.split('\n', 1)[1]
      print("ReceiptID: " + str(receiptID))
      jsonData = data_product(int(receiptID))
      return jsonData
    if (data_rec == "Offer"):
      jsonData = data_offer()
      return jsonData
    if (data_rec == "Trend"):
      #time = received_data.split('\n', 1)[1]
      #company = received_data.split('\n', 1)[2]
      #product = received_data.split('\n', 1)[3]
      #jsonData = data_trend(time, company, product)
      jsonData = data_trend()
      return jsonData


  
def data_receipt(name):
  # Create database
  myclient = pymongo.MongoClient("mongodb+srv://test:test123@cluster0-vafta.mongodb.net/test?retryWrites=true&w=majority")

  print(myclient.list_database_names())

# Check database exists
  dblist = myclient.list_database_names()
  if "receiptdatabase" in dblist:
    print("The database exists.")

  mydb = myclient.receiptdatabase
  
  col_receipt = mydb.receipts
  if name == "0":
    mycol = col_receipt.find()
  else:
    myquery = { "name": name }
    mycol = col_receipt.find(myquery)

  # write data in json
  data = {}
  i=1

  data["count"] = []
  data["count"].append({'count': str(mycol.count())})
  for x in mycol:
    print(x)
    data[str(i)] = []
    data[str(i)].append({
      'ID': str(x["_id"]),
      #'name': str(x["name"]),
      'date': str(x["date"]),
      'total': str(x["total"]),
    })
    i=i+1

  print("Data:")
  print(data)
  
  with open('data.txt', 'w') as outfile:
    json.dump(data, outfile)
  return jsonify(data)

def data_company():
  # Create database
  myclient = pymongo.MongoClient("mongodb+srv://test:test123@cluster0-vafta.mongodb.net/test?retryWrites=true&w=majority")

  #print(myclient.list_database_names())

# Check database exists
  dblist = myclient.list_database_names()
  if "receiptdatabase" in dblist:
    print("The database exists.")

  mydb = myclient.receiptdatabase
  
  mycol = mydb.receipts
  
  list_name={}
  list_total={}
  i=0
  bool=True
  for x in mycol.find():
    for k in range(i):
      if x["name"] == list_name[k]:
        list_total[k] = list_total[k]+float(x["total"])
        bool=False
    if bool:
      i = len(list_name)
      list_name[i] = x["name"]
      list_total[i] = float(x["total"])
      i=i+1
    bool=True

  print(list_name)
  print(list_total)

  # write data in json
  data = {}
  i=1

  data["count"] = []
  data["count"].append({'count': str(len(list_name))})
  for x in range(len(list_name)):
    
    data[str(i)] = []
    data[str(i)].append({
      'name': str(list_name[x]),
      'total': str(list_total[x]),
    })
    i=i+1

  print("Data:")
  print(data)
  t= str(data)
  print(len(t))

  with open('data.txt', 'w') as outfile:
    json.dump(data, outfile)
  return jsonify(data)



def data_product(receiptID):
  print("in data_product")
  # Create database
  myclient = pymongo.MongoClient("mongodb+srv://test:test123@cluster0-vafta.mongodb.net/test?retryWrites=true&w=majority")

  print(myclient.list_database_names())

# Check database exists
  dblist = myclient.list_database_names()
  if "receiptdatabase" in dblist:
    print("The database exists.")

  mydb = myclient.receiptdatabase
  col_products = mydb.products

  #print(receiptID)
  # find data with correct receiptID
  # if receiptID = 0 find all
  if receiptID == 0:
    mycol = col_products.find()
  else:
    myquery = { "receiptID": str(receiptID) }
    mycol = col_products.find(myquery)

  # write data in json
  data = {}
  i=1

  data["count"] = []
  data["count"].append({'count': str(mycol.count())})
  for x in mycol:
    data[str(i)] = []
    data[str(i)].append({
      'name': str(x["name"]),
      'price': str(x["price"]),
    })
    i=i+1

  print("Data:")
  print(data)

  with open('data.txt', 'w') as outfile:
    json.dump(data, outfile)
  return jsonify(data)

def data_offer():
  myclient = pymongo.MongoClient("mongodb+srv://test:test123@cluster0-vafta.mongodb.net/test?retryWrites=true&w=majority")

  print(myclient.list_database_names())

# Check database exists
  dblist = myclient.list_database_names()
  if "receiptdatabase" in dblist:
    print("The database exists.")

  mydb = myclient.receiptdatabase

  # get all products bought
  col_products = mydb.products

  # get all offers
  col_offers = mydb.offers

  name = ""
  data = {}
  i=0
  data["count"] = []
  for x in col_products.find().sort("name"):      #sort most bought product first

    if x["name"] != name :      #look for every product once
      y = col_offers.find({ "product": x["name"] }).sort("price")      #sort lowest price first
      for z in y:
        print(z)
        data[str(i)] = []
        data[str(i)].append({
          "product": x["name"],
          "company": z["company"],
          "price": z["price"],
          "date": z["until"],
        })
        i = i + 1
      
    name = x["name"]  

  data["count"].append({'count': str(i)})
  print("Data:")
  print(data)

  with open('data.txt', 'w') as outfile:
    json.dump(data, outfile)
  return jsonify(data)

def data_trend():
  # Create database
  myclient = pymongo.MongoClient("mongodb+srv://test:test123@cluster0-vafta.mongodb.net/test?retryWrites=true&w=majority")

  print(myclient.list_database_names())

# Check database exists
  dblist = myclient.list_database_names()
  if "receiptdatabase" in dblist:
    print("The database exists.")

  mydb = myclient.receiptdatabase
  
  compcol = mydb.receipts
  
  data={}

  # collect all company entries
  i=0
  for x in compcol.find():
    data["c"+str(i)] = []
    data["c"+str(i)].append({
      'name': x["name"],
      'date': x["date"],
      'total': x["total"],
    })
    i=i+1

  data["count"] = []
  data["count"].append({'countCompany': str(i)})
    
  print("Data:")
  print(data)

  # collect all product entries and add date and company name by looking what the value are in the corresponding receipt table
  prodcol = mydb.products
  i=0;
  for x in prodcol.find():

    myquery = { "_id": int(x["receiptID"]) }
    
    for y in compcol.find(myquery):

      data["p"+str(i)] = []
      data["p"+str(i)].append({
      'name': x["name"],
      'price' : x["price"],
      'date': y["date"],
      'company': y["name"],
    })
    i=i+1

  data["count"].append({'countProducts': str(i)})

  print("Data:")
  print(data)

  with open('data.txt', 'w') as outfile:
    json.dump(data, outfile)
  return jsonify(data)

if __name__ == '__main__':
	app.run(host='192.168.188.67',port=5000, debug=True, threaded=True) #True shows all Errors (for develop), later change to False, threaded=True for multithreating 
