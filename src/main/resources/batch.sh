#!/bin/bash

# Get the current working directory
CURRENT_DIR=$(pwd)

# Check if there are any JPG files in the current directory
IMAGE_FILES=$(ls *.jpg 2>/dev/null)

# Check if there are any JPEG files in the current directory
IMAGE_FILES+=" $(ls *.jpeg 2>/dev/null)"

# Check if there are any PNG files in the current directory
IMAGE_FILES+=" $(ls *.png 2>/dev/null)"

# Check if there are any GIF files in the current directory
IMAGE_FILES+=" $(ls *.gif 2>/dev/null)"

# Check if there are any BMP files in the current directory
IMAGE_FILES+=" $(ls *.bmp 2>/dev/null)"

# Check if there are any TIFF files in the current directory
IMAGE_FILES+=" $(ls *.tiff 2>/dev/null)"

# Check if there are any WEBP files in the current directory
IMAGE_FILES+=" $(ls *.webp 2>/dev/null)"

# Check if there are any HEIC files in the current directory
IMAGE_FILES+=" $(ls *.heic 2>/dev/null)"

# Check if there are any HEIF files in the current directory
IMAGE_FILES+=" $(ls *.heif 2>/dev/null)"

# Check if there are any PDF files in the current directory
IMAGE_FILES+=" $(ls *.pdf 2>/dev/null)"

# Check if no image files are found
if [ -z "$IMAGE_FILES" ]; then
    echo "No image files found in the current directory."
    exit 1
fi

# Loop through each image file
for IMAGE_PATH in $IMAGE_FILES
do
    # Extract the image file name without extension
    IMAGE_NAME=$(basename "$IMAGE_PATH" | cut -d. -f1)
    
    # Define the output JSON file path
    OUTPUT_JSON="$CURRENT_DIR/${IMAGE_NAME}_output.json"

    # Perform OCR using Tesseract and save the result to a text file
    tesseract "$IMAGE_PATH" result --psm 6 -l eng --oem 3

    # Read the OCR result from the text file
    OCR_RESULT=$(cat result.txt)

    # Create a JSON structure with the OCR result
    echo "$OCR_RESULT" > "$OUTPUT_JSON"

    # Remove the temporary text file
    rm result.txt

    echo "OCR completed for $IMAGE_PATH. JSON saved to $OUTPUT_JSON"
done
