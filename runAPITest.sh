#!/bin/sh
# gradle copyDependencies
# gradle build
java -cp build/libs/*:build/ext_libs/* gti.ingredientsgenius.APITest datas/food_ingredients/13.PNG
