import cv2
import base64
import numpy as np
from PIL import Image


def to_base64(img):
    _, buf = cv2.imencode(".png", img)

    return base64.b64encode(buf)


def from_base64(buf):
    buf_decode = base64.b64decode(buf)

    buf_arr = np.fromstring(buf_decode, dtype=np.uint8)
    print(type(buf_arr))
    return cv2.imdecode(buf_arr, cv2.IMREAD_UNCHANGED)


img = cv2.imread("test.jpeg")
img_base64 = to_base64(img)
print(type(img_base64))
img_decoded = from_base64(img_base64)
print(type(img_decoded))
im = Image.fromarray(img_decoded)
im.save('test1.jpeg')

print(img_decoded.shape)
