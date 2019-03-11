''' Document Localization using Recursive CNN
 Maintainer : Khurram Javed
 Email : kjaved@ualberta.ca '''

import argparse
import time

import numpy as np
import torch
from PIL import Image

import dataprocessor
import evaluation

from utils import utils

parser = argparse.ArgumentParser(description='iCarl2.0')

parser.add_argument("-i", "--data-dir", default="/Users/khurramjaved96/bg5",
                    help="input Directory of test data")

args = parser.parse_args()
args.cuda = torch.cuda.is_available()
if __name__ == '__main__':
    corners_extractor = evaluation.corner_extractor.GetCorners("../documentModelWell")
    corner_refiner = evaluation.corner_refiner.corner_finder("../cornerModelWell")
    test_set_dir = args.data_dir
    iou_results = []
    my_results = []
    dataset_test = dataprocessor.dataset.SmartDocDirectories(test_set_dir)
    for data_elem in dataset_test.myData:

        img_path = data_elem[0]
        # print(img_path)
        target = data_elem[1].reshape((4, 2))
        img_array = np.array(Image.open(img_path))
        computation_start_time = time.clock()
        extracted_corners = corners_extractor.get(img_array)
        temp_time = time.clock()
        corner_address = []
        # Refine the detected corners using corner refiner
        counter=0
        for corner in extracted_corners:
            counter+=1
            corner_img = corner[0]
            refined_corner = np.array(corner_refiner.get_location(corner_img, 0.85))

            # Converting from local co-ordinate to global co-ordinate of the image
            refined_corner[0] += corner[1]
            refined_corner[1] += corner[2]

            # Final results
            corner_address.append(refined_corner)
        computation_end_time = time.clock()
        print("TOTAL TIME : ", computation_end_time - computation_start_time)
        r2 = utils.intersection_with_corection_smart_doc_implementation(target, np.array(corner_address), img_array)
        r3 = utils.intersection_with_corection(target, np.array(corner_address), img_array)

        if r3 - r2 > 0.1:
            print ("Image Name", img_path)
            print ("Prediction", np.array(corner_address), target)
            0/0
        assert (r2 > 0 and r2 < 1)
        iou_results.append(r2)
        my_results.append(r3)
        print("MEAN CORRECTED JI: ", np.mean(np.array(iou_results)))
        print("MEAN CORRECTED MY: ", np.mean(np.array(my_results)))

    print(np.mean(np.array(iou_results)))
