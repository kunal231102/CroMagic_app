# Cromagic

Cromagic is a powerful image processing application that utilizes state-of-the-art deep learning models to transform and enhance images. With features like black-and-white to colorized conversion, background removal, and image sharpening, Cromagic helps you bring old and blurry images back to life.

## ✨ Features

- **Black & White to Colorized Image**  🎨  
  - Uses **DeOldify** to restore colors in grayscale images with deep learning.

- **Background Remover** 🏞️  
  - Powered by **MODNet**, Cromagic can accurately remove backgrounds from images.

- **Image Sharpening & Upscaling** 🔍  
  - Uses **ESRGAN** (Enhanced Super-Resolution Generative Adversarial Network) to enhance image quality.

## 📌 Technologies Used
- **DeOldify** – AI-based image colorization model
- **MODNet** – Background matting model for precise background removal
- **ESRGAN** – Super-resolution model for image upscaling and sharpening
- **Python, OpenCV, PyTorch, TensorFlow** – Core libraries for deep learning and image processing

## 🚀 Installation

1. **Clone the Repository:**
   ```sh
   git clone https://github.com/kunal231102/cromagic.git
   cd cromagic
   ```

2. **Create a Virtual Environment (Recommended):**
   ```sh
   python -m venv venv
   source venv/bin/activate  # On Windows use: venv\Scripts\activate
   ```

3. **Install Dependencies:**
   ```sh
   pip install -r requirements.txt
   ```

4. **Download Pre-trained Models:**
   - Download the required **DeOldify**, **MODNet**, and **ESRGAN** model weights and place them in the `models/` directory.

5. **Run the Application:**
   ```sh
   python app.py
   ```

## 🎯 Usage
1. Upload an image in the web interface.
2. Select the feature you want (Colorize, Remove Background, Sharpen Image).
3. Click **Process** and download the enhanced image!

## 🖼️ Example Outputs
| Original | Colorized | Background Removed | Enhanced |
|----------|-----------|--------------------|----------|
| ![BW](example_bw.jpg) | ![Color](example_color.jpg) | ![BG Removed](example_bg.jpg) | ![Enhanced](example_enhance.jpg) |

## 📜 License
This project is licensed under the **MIT License** – feel free to modify and share!

## 💡 Contributing
Pull requests and contributions are welcome! If you'd like to improve Cromagic, feel free to submit a PR.

## 📧 Contact
For any issues or feature requests, feel free to contact me at **your-email@example.com** or open an issue in the repository.

---
⭐ If you like this project, give it a star on GitHub!

