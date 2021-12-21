#!/bin/bash

mvn clean package

PRODUCERS=(5 15)
CONSUMERS=(5 15)
PRODUCTIONS=(1000)
LAYERS_SIZES=("2 2 2 2 2" "15 10 6" "10 15 50 15 10")

OUTPUT_FILE="res.txt"
EXECUTABLE="./target/tw_csp-1.0.1-jar-with-dependencies.jar"

for pn in "${PRODUCTIONS[@]}"; do
  for p in "${PRODUCERS[@]}"; do
    for c in "${CONSUMERS[@]}"; do
      for ls in "${LAYERS_SIZES[@]}"; do
          echo $pn $p $c $ls;
          echo $pn $p $c $ls >> $OUTPUT_FILE;

          java -jar $EXECUTABLE $p $c $pn $ls >> $OUTPUT_FILE;
      done
    done
  done
done