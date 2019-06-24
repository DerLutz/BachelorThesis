from flask import json, Flask, request, url_for, redirect, jsonify
from PIL import Image
import base64
import cv2
import numpy as np
import sys
import evaluation



app = Flask(__name__)

folder = "/Users/chris/Documents/Bachelorarbeit/Server/Server/Recursive-CNNs-Pytorch-RecursiveCNN"

def from_base64(buf):
    print(sys.getsizeof(buf))
    buf_decode = base64.b64decode(buf)
    buf_arr = np.fromstring(buf_decode, dtype=np.uint8)
    return cv2.imdecode(buf_arr, cv2.IMREAD_UNCHANGED)

def corner_detection():
	imagePath = 'Image.jpeg'
	retainFactor = 0.85
	cornerModel = "/Users/chris/Documents/Bachelorarbeit/Server/Server/CornerTraining/nonamecorner_resnet.pb"
	documentModel = "/Users/chris/Documents/Bachelorarbeit/Server/Server/DocumentTraining/nonamedocument_resnet.pb"
	outputPath = "../output.jpg"

	corners_extractor = evaluation.corner_extractor.GetCorners(documentModel)
	corner_refiner = evaluation.corner_refiner.corner_finder(cornerModel)
	
	img = cv2.imread(imagePath)
	height, width, channels = img.shape
	
	oImg = img
	
	extracted_corners = corners_extractor.get(oImg)
	corner_address = []
	# Refine the detected corners using corner refiner
	image_name = 0
	for corner in extracted_corners:
		image_name += 1
		corner_img = corner[0]
		refined_corner = np.array(corner_refiner.get_location(corner_img, 0.85))
			
		# Converting from local co-ordinate to global co-ordinates of the image
		refined_corner[0] += corner[1]
		refined_corner[1] += corner[2]
	
		# Final results
		corner_address.append(refined_corner)

	for a in range(0, len(extracted_corners)):
		cv2.line(oImg, tuple(corner_address[a % 4]), tuple(corner_address[(a + 1) % 4]), (255, 0, 0), 4)

	cv2.imwrite(outputPath, oImg)

	# write corners in json
	data = {}
	data['size'] = []
	data['size'].append({
		'width': str(width),
		'height': str(height),
	})
  
	data['corner1'] = []  
	data['corner1'].append({  
		'x': str(corner_address[0][0]),
		'y': str(corner_address[0][1]),
	})
	data['corner2'] = []
	data['corner2'].append({  
		'x': str(corner_address[1][0]),
		'y': str(corner_address[1][1]),
	})
	data['corner3'] = []
	data['corner3'].append({  
		'x': str(corner_address[2][0]),
		'y': str(corner_address[2][1]),
	})
	data['corner4'] = []
	data['corner4'].append({  
		'x': str(corner_address[3][0]),
		'y': str(corner_address[3][1]),
	})
			
	with open('data.txt', 'w') as outfile:
		json.dump(data, outfile)
	return jsonify(data)



@app.route('/cornerDetection', methods = ['POST', 'GET'])
def cornerDetection():

	if request.headers['Content-Type'] == 'application/x-www-form-urlencoded':
		data = request.data.decode('utf-8')
	    
		#data += "=" * ((4 - len(data) % 4) % 4)
		#imgdata = base64.b64decode(data)
		#print(type(imgdata))
		img_decoded = from_base64(data)
		print(type(img_decoded))
		im = Image.fromarray(img_decoded)
		im.save('Image.jpeg')
		corner_detection()
		return 'x-www-form-urlencoded'

	if request.headers['Content-Type'] == 'text/plain':
		data = request.data.decode('utf-8')
		data += "=" * ((4 - len(data) % 4) % 4)
		imgdata = base64.b64decode(data)
		filename = folder+'/Image.jpeg'  # I assume you have a way of picking unique filenames
		with open(filename, 'wb') as f:
    			f.write(imgdata)
		return 'Text Message: ' + data

	if request.headers['Content-Type'] == 'image/jpeg':
		img_decoded = from_base64(request.data)
		img_decoded = np.asarray(img_decoded)
		im = Image.fromarray(img_decoded)
		im.save('Image.jpeg')
		print('Image save succesful')
		response_json = corner_detection()
		return response_json


	if request.headers['Content-Type'] == 'application/json':
		print('JSON Message: ' + request.data)
		return 'JSON Message: ' + request.data

	if request.headers['Content-Type'] == 'application/octet-stream':
		f = open('.binary', 'wb')
		f.write(request.data)
		f.close()
		print('Binary Message: ' + request.data)
		return 'Binary message written'
	else:
		print('unsupported media type')
		return '415 unsupported media type'

if __name__ == '__main__':
	app.run(host='192.168.89.189',port=1337, debug=False, threaded=True) #True shows all Errors (for develop), later change to False, threaded=True for multithreating
