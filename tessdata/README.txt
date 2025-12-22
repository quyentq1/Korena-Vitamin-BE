IMPORTANT: Tesseract Language Data Files Required
=================================================

The OCR functionality requires trained data files for the languages you want to recognize.
By default, the application is configured to use Vietnamese (vie) and English (eng).

INSTRUCTIONS:
1. Go to: https://github.com/tesseract-ocr/tessdata
2. Download the following files:
   - vie.traineddata
   - eng.traineddata
3. Place them in this directory (`tessdata`).

The application will fail to perform OCR if these files are missing.
