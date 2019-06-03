from flask import json, Flask, request, url_for
from PIL import Image
import base64
import cv2
import numpy as np
import sys


app = Flask(__name__)

folder = "/home/christopher/Documents/Server/Recursive-CNNs-Pytorch-RecursiveCNN"

def from_base64(buf):
	print(buf)
	buf_decode = base64.b64decode(buf)
	print(buf_decode)
	buf_arr = np.fromstring(buf_decode, dtype=np.uint8)
	print(buf_arr)
	return cv2.imdecode(buf_arr, cv2.IMREAD_UNCHANGED)


@app.route('/')
def api_root():
    return 'Welcome'

@app.route('/articles')
def api_articles():
    return 'List of ' + url_for('api_articles')

@app.route('/articles/<articleid>')
def api_article(articleid):
    return 'You are reading ' + articleid

@app.route('/hello')
def api_hello():
    if 'name' in request.args:
        return 'Hello ' + request.args['name']
    else:
        return 'Hello John Doe'

@app.route('/messages', methods = ['POST'])
def api_messag():

	if request.headers['Content-Type'] == 'application/x-www-form-urlencoded':
		data = request.data
		#data += "=" * ((4 - len(data) % 4) % 4)
		#imgdata = base64.b64decode(data)
		print(data)
		img_decoded = from_base64(data)
		print(type(img_decoded))
		im = Image.fromarray(img_decoded)
		im.save('test.jpeg')
		return 'x-www-form-urlencoded'

	if request.headers['Content-Type'] == 'text/plain':
		data = request.data.decode('utf-8')
		data += "=" * ((4 - len(data) % 4) % 4)
		imgdata = base64.b64decode(data)
		filename = folder+'/some_image.JPEG'  # I assume you have a way of picking unique filenames
		with open(filename, 'wb') as f:
    			f.write(imgdata)
		return 'Text Message: ' + data

	if request.headers['Content-Type'] == 'image/jpeg':
		img_decoded = from_base64(request.data)
		img_decoded = np.asarray(img_decoded)
		print(type(img_decoded))
		im = Image.fromarray(img_decoded)
		im.save('test2.jpeg')
		print('Image save succesful')

		return 'Image saved'


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
	app.run(host='192.168.89.190',port=1337, debug=False, threaded=True) #True shows all Errors (for develop), later change to False, threaded=True for multithreating
