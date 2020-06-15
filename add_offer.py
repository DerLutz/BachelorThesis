import pymongo
import datetime

# Examples from
# https://www.w3schools.com/python/python_mongodb_getstarted.asp

# Create database
myclient = pymongo.MongoClient("mongodb+srv://test:test123@cluster0-vafta.mongodb.net/test?retryWrites=true&w=majority")

mydb = myclient["receiptdatabase"]

# Check database exists
dblist = myclient.list_database_names()
if "mydatabase" in dblist:
  print("The database exists.")


#New Collection
col_offer = mydb["offers"]

# Input {"company": "company name", "product":"product name", "price": "0.99", "until": "yyyy-mm-dd"}
# example {"company": "ALDI", "product":"PRINZENROLLE", "price": "0.99", "until": "2020-06-31"}

list_offer = [
  {"company": "ALDI", "product":"PRINZENROLLE", "price": "0.99", "until": "2020-06-01"},
  {"company": "Netto", "product":"PRINZENROLLE", "price": "0.98", "until": "2020-06-31"},
  {"company": "TestCompany", "product":"PRINZENROLLE", "price": "0.99", "until": "2020-06-31"},
  {"company": "ALDI", "product":"TestProduct", "price": "0.99", "until": "2020-06-31"},
  {"company": "TestCompany", "product":"TestProduct", "price": "0.99", "until": "2020-06-31"},
  {"company": "ALDI", "product":"Bananen", "price": "0.99", "until": "2019-06-31"},
]

y = col_offer.insert_many(list_offer)

for x in col_offer.find():
  today = str(datetime.datetime.now().date())
  if today > x["until"]:
    myquery = {"until": x["until"]}
    col_offer.delete_one(x)
#z = col_offer.insert_many(list_offer1)


