from flask import json, Flask, request, url_for, redirect, jsonify
import pymongo
import ast

app = Flask(__name__)


@app.route('/save', methods = ['POST', 'GET'])
def getData():
  #print(request.headers)
  received_data = request.data.decode('utf-8')
  print("DATA")
  print(received_data)
  print(type(received_data))

  test = ast.literal_eval(received_data)
  print("Test")
  print(type(test))
  print(test)

  # Create database
  myclient = pymongo.MongoClient("mongodb+srv://test:test123@cluster0-vafta.mongodb.net/test?retryWrites=true&w=majority")

  mydb = myclient["receiptdatabase"]

# Check database exists
  dblist = myclient.list_database_names()
  if "mydatabase" in dblist:
    print("The database exists.")

#first save receipt (company, date, total)
  col_receipt = mydb["receipts"]
  col_product = mydb["products"]

# j = next ID value
  j=0
  for k in col_receipt.find():
    j = j + 1
  

  #test1 = test["10"]
  try:
    i = 0      #count als ersten Wert übergeben anstatt try und while true
    while True:
      test1 = list(test)[i]

      if (test1 == 'Company'):
        company = test[test1]
      elif (test1 == 'Date'):
        date = test[test1]
      elif (test1 == 'Total'):
        total = test[test1]
      else:
        list_product = [
          { "receiptID": str(j), "name": test1, "price": test[test1]},
        ]

        y = col_product.insert_many(list_product)
        print(list_product)

      i = i + 1

  except IndexError:
    print("finished all lines")

  list_receipt = [
    { "_id": str(j), "name": company, "date": date, "total": total}
  ]

  x = col_receipt.insert_many(list_receipt)
  print(list_receipt)

  return("Data saved")

if __name__ == '__main__':
	app.run(host='192.168.188.67',port=1337, debug=True, threaded=True) #True shows all Errors (for develop), later change to False, threaded=True for multithreating 
