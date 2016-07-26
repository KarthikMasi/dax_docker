# Installation

1. sudo docker build ./
2. docker run -t -i --priveleged <container id>
3. docker cp <path_to_matlab_iso.iso> <docker_container_id>:/home
4. mount -o loop /home/<matlab.iso> /media
5. cp /media/activate.ini /home
6. cp /media/installer_input.txt /home
7. Retrieve license file from mathworks online
8. docker cp <path_to_licence.lic> <container_id>:/home
9. vi /home/activate.ini
..1. Change line 66 to be activateCommand=activateOffline
..2. Change line 77 to licenseFile=/home/license.lic
..3. On line 87, enter your matlab activation key
10. vi /home/installer_input.txt
..1. Change line 43 to destinationFolder=/opt/MATLAB/R2016a
..2. Enter the file installation key from mathworks online on line 53
..3. Change line 67 to agreeToLicense=yes
..4. Change line 98 to mode=silent
..5. Change line 120 to activationPropertiesFile=/home/license.lic
11. /media/install -inputFile /home/installer_input.txt
12. /opt/MATLAB/R2016a/bin/activate_matlab.sh -propertiesFile /home/activate.ini
