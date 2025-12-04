#!/usr/bin/env python3
"""
Google Coral USB Accelerator vision processing for FRC
Runs on Raspberry Pi or similar coprocessor
Publishes detections to NetworkTables for robot consumption

SETUP INSTRUCTIONS:
1. Install Coral Edge TPU runtime on Raspberry Pi:
   https://coral.ai/docs/accelerator/get-started/

2. Install required packages:
   pip3 install tflite-runtime opencv-python-headless pynetworktables

3. Download a TensorFlow Lite model (with Edge TPU support):
   - Example: SSD MobileNet v2 (COCO)
   - Or train your own model for game pieces (coral, algae)
   - Place model file (.tflite) and labels (.txt) in this directory

4. Edit the configuration below to match your setup

5. Run this script:
   python3 coral_vision.py
"""

import time
import cv2
import numpy as np
from networktables import NetworkTables
from tflite_runtime.interpreter import Interpreter
from tflite_runtime.interpreter import load_delegate
import argparse
import sys
import threading

# ============================================================================
# CONFIGURATION
# ============================================================================

# Camera settings
CAMERA_WIDTH = 640
CAMERA_HEIGHT = 480
CAMERA_FPS = 30
CAMERA_INDEX = 0  # USB camera device index

# Model paths
MODEL_PATH = 'models/detect_edgetpu.tflite'  # Model compiled for Edge TPU
LABELS_PATH = 'models/labelmap.txt'

# Detection settings
CONFIDENCE_THRESHOLD = 0.5
MAX_DETECTIONS = 10

# NetworkTables settings
ROBOT_IP = '10.TE.AM.2'  # Replace TE.AM with your team number
# For team 254, use '10.2.54.2'
# For team 1234, use '10.12.34.2'
TABLE_NAME = 'Coral'

# Display settings (for debugging on coprocessor)
SHOW_DISPLAY = False  # Set to True to show video output (requires display)

# ============================================================================
# LOAD LABELS
# ============================================================================

def load_labels(path):
    """Load labels from file"""
    with open(path, 'r') as f:
        labels = [line.strip() for line in f.readlines()]
    # Remove index numbers if present (e.g., "0 person" -> "person")
    if labels[0].split(' ', 1)[0].isdigit():
        labels = [label.split(' ', 1)[1] for label in labels]
    return labels

# ============================================================================
# DETECTION PROCESSING
# ============================================================================

def run_inference(interpreter, image, threshold):
    """Run inference on image and return detections"""

    # Get input details
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    # Get input shape
    height = input_details[0]['shape'][1]
    width = input_details[0]['shape'][2]

    # Resize image to match model input
    image_resized = cv2.resize(image, (width, height))

    # Normalize if needed (check model requirements)
    input_data = np.expand_dims(image_resized, axis=0)

    # Check if model expects float or uint8
    if input_details[0]['dtype'] == np.float32:
        input_data = (np.float32(input_data) - 127.5) / 127.5

    # Run inference
    interpreter.set_tensor(input_details[0]['index'], input_data)
    interpreter.invoke()

    # Get results
    # Output format depends on model - this is for SSD models
    boxes = interpreter.get_tensor(output_details[0]['index'])[0]  # Bounding box coordinates
    classes = interpreter.get_tensor(output_details[1]['index'])[0]  # Class indices
    scores = interpreter.get_tensor(output_details[2]['index'])[0]  # Confidence scores

    # Filter detections by confidence
    detections = []
    for i in range(len(scores)):
        if scores[i] >= threshold and scores[i] <= 1.0:
            detections.append({
                'class_id': int(classes[i]),
                'score': float(scores[i]),
                'bbox': boxes[i].tolist()  # [ymin, xmin, ymax, xmax] normalized 0-1
            })
            if len(detections) >= MAX_DETECTIONS:
                break

    return detections

# ============================================================================
# NETWORKTABLES PUBLISHING
# ============================================================================

def publish_detections(table, detections, labels):
    """Publish detection results to NetworkTables"""

    num_detections = len(detections)
    table.putNumber('num_detections', num_detections)
    table.putBoolean('connected', True)
    table.putNumber('timestamp', time.time() * 1000)  # Timestamp in milliseconds

    if num_detections == 0:
        # Clear arrays if no detections
        table.putStringArray('labels', [])
        table.putNumberArray('confidences', [])
        table.putNumberArray('x_positions', [])
        table.putNumberArray('y_positions', [])
        table.putNumberArray('widths', [])
        table.putNumberArray('heights', [])
        return

    # Prepare arrays
    label_array = []
    confidence_array = []
    x_array = []
    y_array = []
    width_array = []
    height_array = []

    for det in detections:
        # Get label
        class_id = det['class_id']
        label = labels[class_id] if class_id < len(labels) else f'class_{class_id}'
        label_array.append(label)

        # Get confidence
        confidence_array.append(det['score'])

        # Get bounding box (convert from [ymin, xmin, ymax, xmax] to center + size)
        ymin, xmin, ymax, xmax = det['bbox']

        # Calculate center and size (normalized 0-1)
        x_center = (xmin + xmax) / 2.0
        y_center = (ymin + ymax) / 2.0
        width = xmax - xmin
        height = ymax - ymin

        x_array.append(x_center)
        y_array.append(y_center)
        width_array.append(width)
        height_array.append(height)

    # Publish to NetworkTables
    table.putStringArray('labels', label_array)
    table.putNumberArray('confidences', confidence_array)
    table.putNumberArray('x_positions', x_array)
    table.putNumberArray('y_positions', y_array)
    table.putNumberArray('widths', width_array)
    table.putNumberArray('heights', height_array)

# ============================================================================
# VISUALIZATION (for debugging)
# ============================================================================

def draw_detections(image, detections, labels):
    """Draw bounding boxes on image for visualization"""
    height, width = image.shape[:2]

    for det in detections:
        # Get bbox
        ymin, xmin, ymax, xmax = det['bbox']

        # Convert to pixel coordinates
        left = int(xmin * width)
        top = int(ymin * height)
        right = int(xmax * width)
        bottom = int(ymax * height)

        # Draw rectangle
        cv2.rectangle(image, (left, top), (right, bottom), (0, 255, 0), 2)

        # Get label and confidence
        class_id = det['class_id']
        label = labels[class_id] if class_id < len(labels) else f'class_{class_id}'
        confidence = det['score']

        # Draw label
        label_text = f'{label}: {confidence:.2f}'
        cv2.putText(image, label_text, (left, top - 10),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)

    return image

# ============================================================================
# MAIN LOOP
# ============================================================================

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--model', default=MODEL_PATH, help='Path to TFLite model')
    parser.add_argument('--labels', default=LABELS_PATH, help='Path to labels file')
    parser.add_argument('--robot-ip', default=ROBOT_IP, help='Robot IP address')
    parser.add_argument('--threshold', type=float, default=CONFIDENCE_THRESHOLD,
                        help='Confidence threshold')
    parser.add_argument('--camera', type=int, default=CAMERA_INDEX, help='Camera index')
    parser.add_argument('--display', action='store_true', help='Show video output')
    args = parser.parse_args()

    print('=' * 60)
    print('Coral Vision for FRC')
    print('=' * 60)

    # Load labels
    print(f'Loading labels from {args.labels}...')
    try:
        labels = load_labels(args.labels)
        print(f'Loaded {len(labels)} labels')
    except Exception as e:
        print(f'Error loading labels: {e}')
        return

    # Load TFLite model with Coral Edge TPU delegate
    print(f'Loading model from {args.model}...')
    try:
        interpreter = Interpreter(
            model_path=args.model,
            experimental_delegates=[load_delegate('libedgetpu.so.1')]
        )
        interpreter.allocate_tensors()
        print('Model loaded successfully with Edge TPU delegate')
    except Exception as e:
        print(f'Error loading model: {e}')
        print('Make sure you have a model compiled for Edge TPU (_edgetpu.tflite)')
        return

    # Initialize camera
    print(f'Opening camera {args.camera}...')
    cap = cv2.VideoCapture(args.camera)
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, CAMERA_WIDTH)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, CAMERA_HEIGHT)
    cap.set(cv2.CAP_PROP_FPS, CAMERA_FPS)

    if not cap.isOpened():
        print('Error: Could not open camera')
        return

    print('Camera opened successfully')

    # Initialize NetworkTables
    print(f'Connecting to robot at {args.robot_ip}...')
    NetworkTables.initialize(server=args.robot_ip)
    table = NetworkTables.getTable(TABLE_NAME)

    # Wait for connection
    print('Waiting for NetworkTables connection...')
    while not NetworkTables.isConnected():
        time.sleep(0.1)
    print('Connected to NetworkTables!')

    print('=' * 60)
    print('Starting vision processing...')
    print('Press Ctrl+C to stop')
    print('=' * 60)

    # FPS calculation
    fps_start_time = time.time()
    fps_counter = 0
    fps = 0

    try:
        while True:
            # Capture frame
            ret, frame = cap.read()
            if not ret:
                print('Error: Failed to capture frame')
                break

            # Run inference
            detections = run_inference(interpreter, frame, args.threshold)

            # Publish to NetworkTables
            publish_detections(table, detections, labels)

            # Calculate FPS
            fps_counter += 1
            if fps_counter >= 30:
                fps_end_time = time.time()
                fps = fps_counter / (fps_end_time - fps_start_time)
                fps_start_time = fps_end_time
                fps_counter = 0
                print(f'FPS: {fps:.1f} | Detections: {len(detections)}')

            # Display (optional)
            if args.display or SHOW_DISPLAY:
                display_frame = draw_detections(frame.copy(), detections, labels)
                cv2.putText(display_frame, f'FPS: {fps:.1f}', (10, 30),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)
                cv2.imshow('Coral Vision', display_frame)
                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break

    except KeyboardInterrupt:
        print('\nStopping...')

    finally:
        # Cleanup
        cap.release()
        cv2.destroyAllWindows()
        table.putBoolean('connected', False)
        print('Cleanup complete')

if __name__ == '__main__':
    main()
