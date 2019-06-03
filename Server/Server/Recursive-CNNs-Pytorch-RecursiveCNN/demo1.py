import os
import json
import cv2
import numpy as np
from PIL import Image
from flask import Flask, url_for, request, redirect, jsonify
from werkzeug.utils import secure_filename
import evaluation

# Speicherort
folder = "/home/christopher/Documents/Server/Recursive-CNNs-Pytorch-RecursiveCNN"
extensions = set(['jpg', 'JPEG', 'png'])

app = Flask(__name__)

#right type?
def allowed(filename):
	return '.' in filename and filename.rsplit('.', 1)[1].lower() in extensions

@app.route("/", methods=['GET', 'POST'])
def index():
	if request.method == 'POST':
		print(request)
		print(request.files)
		if 'file' not in request.files:
			return redirect(request.url)
		file = request.files['file']
		if file.filename == '':
			file.filename = 'a'
		if allowed(file.filename):
			filename = secure_filename(file.filename) #security against bad filenames
			file.save(os.path.join(folder, filename))
			os.rename(filename, 'Image.jpeg')

			#input for using demo.py part

			imagePath = 'Image.jpeg'
			retainFactor = 0.85
			cornerModel = "/home/christopher/Documents/Server/CornerTraining/nonamecorner_resnet.pb"
			documentModel = "/home/christopher/Documents/Server/DocumentTraining/nonamedocument_resnet.pb"
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
  
			data['corner'] = []  
			data['corner'].append({  
    				'x': str(corner_address[0][0]),
    				'y': str(corner_address[0][1]),
    			})
			data['corner'].append({  
    				'x': str(corner_address[1][0]),
    				'y': str(corner_address[1][1]),
    			})
	
			data['corner'].append({  
    				'x': str(corner_address[2][0]),
    				'y': str(corner_address[2][1]),
    			})
			data['corner'].append({  
    				'x': str(corner_address[3][0]),
    				'y': str(corner_address[3][1]),
    			})
			
			with open('data.txt', 'w') as outfile:
				json.dump(data, outfile)
			return jsonify(data)

	return'''
		<h1>Upload</h1>
		<form method=post enctype=multipart/form-data>
		<input type=file name=file>
		<input type=submit value=upload>
	'''

@app.route("/test")
def test():
	return "Test"

if __name__ == '__main__':
	app.run(host='192.168.56.1',port=1337, debug=False, threaded=True) #True shows all Errors (for develop), later change to False, threaded=True for multithreating
