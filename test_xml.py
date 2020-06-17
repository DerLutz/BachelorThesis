import xml.etree.ElementTree as ET
import datetime
from flask import json, Flask, request, url_for, redirect, jsonify
import pymongo
import numpy as np
import sys
import base64
import cv2
from PIL import Image

app = Flask(__name__)

def from_base64(buf):
    print(sys.getsizeof(buf))
    buf_decode = base64.b64decode(buf)
    buf_arr = np.fromstring(buf_decode, dtype=np.uint8)
    return cv2.imdecode(buf_arr, cv2.IMREAD_UNCHANGED)

@app.route('/ocr', methods = ['POST', 'GET'])
def readImage():

	if request.headers['content-Type'] == 'image/jpeg':
		img_decoded = from_base64(request.data)
		img_decoded = np.asarray(img_decoded)

		im = Image.fromarray(img_decoded)
		im.save('Image.jpeg')
		print('Image save succesful')
		response_json = read_xml()
		return response_json

def read_xml():
	xml_file = "edeka (7)-binary-result.xml"
	global user_id
	#tree = ET.parse("edeka6B-result.xml", None)
	tree = ET.parse(xml_file, None)
	root = tree.getroot()

	company = root.find("CompanyInfo")
	purchase = root.find("Purchase")
	articlelist = purchase.find("ArticleList")
	payment = purchase.find("Payment")

	company_info = company[:]

	articles = articlelist[:]

	#inserting user reciept
	#reciept_id = db.UserReciept.insert_one({
	#	"user_id": user_id,

	#})

	# data json for app
	data = {}
	data["count"] = []
	data["count"].append({'count': str(len(articles)+3)})

	msg = ""
	if len(company_info) == 0:
		msg += "None"
	else:
		for i in range(0, len(company_info)):
			info = company_info[i]
			#print("inside company")
			if i == 0:
				#print("if company 0")
				print(info.tag)
				print(info.text)
				company_Id = "companyFk"
				company_name = info.text
				msg += info.tag + "__" + info.text
				#print(msg)
			else:
				#print("if company 1")
				#print(info.tag)
				#print(info.text)
				#print("if company 1")
				msg += "<>" + info.tag + "__" + info.text
				#print(msg)

	data["1"] = []
	data["1"].append({'a': "Company"})
	data["1"].append({'b': company_name})

	data["2"] = []
	data["2"].append({'a': "Date"})
	data["2"].append({'b': str(datetime.date.today())})
	

	msg += "<<>>"	

	pay_info = payment[:]
	if len(pay_info) == 0:
		msg += "None"
	else:
		for i in range(0, len(pay_info)):
			info = pay_info[i]
			#print("inside payment info")
			if i == 0:
				#print("if payment 0")
				#print(info.tag)
				#print(info.text)
				msg += info.tag + "__" + info.text
				if (info.tag == 'Total'):
					total = info.text
				#print(msg)
			else:
				#print("if payment 1")
				#print(info.tag)
				#print(info.text)
				if(info.tag == 'Net_Total'):
					total_Netto = info.text
				msg += "<>" + info.tag + "__" + info.text
				#print(msg)
	data["3"] = []
	data["3"].append({'a': "Total"})
	data["3"].append({'b': total})
	


	if len(articles) == 0:
		msg += "None<<>>"
	else:
		for i in range(0, len(articles)):
			print("inside article : " + str(i))
			art = articles[i]
			data[str(i+4)] = []
			for k in range(0, len(art)):
				info = art[k]
				if k == 0:
					print("if article 0")
					print(info.tag)
					print(info.text)
					msg += info.tag + "__" + info.text
					data[str(i+4)].append({'a': info.text})
					#print(msg)
				else:
					print("if article 1")
					print(info.tag)
					print(info.text)
					msg += "<>" + info.tag + "__" + info.text
					data[str(i+4)].append({'b': info.text})
					#print(msg)
	
			if len(art.attrib) > 0:
				if art.attrib.get("Tax_Rate") is not None:
					msg += "<>" + "Tax_Rate__" + art.attrib.get("Tax_Rate")
			msg += "<<>>"
	


	#db.UserReciept.update(
	#	{'_id': reciept_id},
	#	{
	#		'$set': {
	#			'companyId': company_Id,
	#			'totalArticles': len(articles),
	#			'date': datetime.date.today(),
	#			'totalNetto': total_Netto
	#		}
	#	}
	#)

	print("Resuts:")
	print('CompanyId: ' + company_Id)
	print('totalArticles: ' + str(len(articles)))
	print('date: ' + str(datetime.date.today()))
	print('totalNetto :' + str(total_Netto))
	print('Total :' + str(total))
	print(data)

	with open('data.txt', 'w') as outfile:
		json.dump(data, outfile)
	return jsonify(data)

def read_txt():

	data = {}
	data["count"] = []
	counter = 1

	data["2"] = []
	data["2"].append({'a': "Date"})
	data["2"].append({'b': str(datetime.date.today())})


	with open("demofile.txt") as fp:
		line = fp.readline()
		cnt=1
		while line:
			print("Line" + str(cnt) +": "+ line.strip())
			line = fp.readline()

			counter = counter +1
			
		cnt += 1
		print(line)

	data["count"].append({'count': str(counter)})


if __name__ == '__main__':
	app.run(host='192.168.33.203',port=1337, debug=True, threaded=True) #True shows all Errors (for develop), later change to False, threaded=True for multithreating 	


