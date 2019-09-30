package com.example.chris.myapplication;

public class Categories {
// List of all known Categories
    // Unknown category to Others
    // Todo Create categories from json/string

    public static String getCategory(int number){
        switch (number){
            case 0:
                return "Edeka";
            case 1:
                return "Globus";
            case 2:
                return "Aldi";
            case 3:
                return "MediaMarkt";
            case 4:
                return "Tankstelle";

            default:
                return "Sonstige";

        }
    }

}
