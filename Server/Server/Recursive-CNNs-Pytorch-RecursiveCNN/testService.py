import os
import requests
import json
import cv2
import numpy as np
import base64
from PIL import Image
import io

test_url = 'http://192.168.188.63:1337/messages'
#test_url = addr + '/sas/api/ic13/detect_tables' + '_with_image'

# prepare headers for http request
content_type = 'image/jpeg'
headers = {'content-type': content_type}

fileName ='Images/test.png'
# fileName = './data/eu-023-4.jpg

#import Tkinter as tk
#from tkinter import filedialog

#root = tk.Tk()
#root.withdraw()

#fileName = filedialog.askopenfilename()

if not os.path.exists(fileName):
    print ("Error: File not found (%s)" % fileName)
    exit (-1)

img = cv2.imread(fileName)
# encode image as jpeg
_, img_encoded = cv2.imencode('.png', img)

###############################################################################################
img_encoded = base64.b64encode(img_encoded)
##############################################################################################
# send http request with image and receive response
response = requests.post(test_url, data=img_encoded, headers=headers)
# decode response
#jsonReponse = json.loads(response.text)

jsonReponse = response.json()
print(type(jsonReponse))
print ("Detections:", jsonReponse)


def decode_base64(data, altchars=b'+/'):
    """Decode base64, padding being optional.

    :param data: Base64 data as an ASCII byte string
    :returns: The decoded byte string.

    """
    data = re.sub(rb'[^a-zA-Z0-9%s]+' % altchars, b'', data)  # normalize
    missing_padding = len(data) % 4
    if missing_padding:
        data += b'='* (4 - missing_padding)
    return base64.b64decode(data, altchars)
