# Motion-Recorded-Android
An Android application for recording movement characteristics using different sensors of the smartphone like accelerometer, gyroscope and magnetometer.

## How does it works

The Android device is attached to the person or object that is going to move. Then, the motion is captured as follows:

* Insert an ID number or string for naming the output file.
* Press start button to start capture motion.
* Press stop button when the motion stops.
* The file automatically is saved in the external storage of the Android device.

## Output format

The captured data is then stored as a time sequence in a CSV file containing eighteen columns, one per each characteristic measured. The measured characteristics are:
* **Accelerometer_x**: Acceleration in the x axis (m/s2).
* **Accelerometer_y**: Acceleration in the y axis (m/s2).
* **Accelerometer_z**: Acceleration in the z axis (m/s2).
* **Gravity_x**: Gravity force in the x axis (m/s2).
* **Gravity_y**: Gravity force in the y axis (m/s2).
* **Gravity_z**: Gravity force in the z axis (m/s2). 
* **Gyros_x**: Rate of rotation around x axis (rad/s).
* **Gyros_y**: Rate of rotation around y axis (rad/s).
* **Gyros_z**: Rate of rotation around z axis (rad/s).
* **Lin_accel_x**: Acceleration in the x axis without gravity (m/s2).
* **Lin_accel_y**: Acceleration in the y axis without gravity (m/s2).
* **Lin_accel_z**: Acceleration in the z axis without gravity (m/s2).
* **Game_rot_vector_x**: Rotation vector component along the x axis (No unit).
* **Game_rot_vector_y**: Rotation vector component along the y axis (No unit).
* **Game_rot_vector_z**: Rotation vector component along the z axis (No unit).
* **Magn_field_x**: Geomagnetic field strength along x axis (μT).
* **Magn_field_y**: Geomagnetic field strength along y axis (μT).
* **Magn_field_z**: Geomagnetic field strength along z axis (μT).

## License
This software is licensed under the [AGPL](https://choosealicense.com/licenses/agpl-3.0/) license.
