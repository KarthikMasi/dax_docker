#
# This sets up a base dax docker image
#
FROM ubuntu:14.04
# Install updates, pip and dax
RUN \
apt-get update && \
apt-get -y install wget && \
wget https://www.nitrc.org/frs/download.php/7248/JIST-2014Dec12-03-34PM.jar && \
#Set to bash
CMD ["bash"]
# Now you need to copy install mipav.bin in
docker cp <path_to_file> <docker name>:/
# Then you need to install mipav
# docker run -i -t <docker name> /bin/bash
# sh instalMipav.bin
# Now move the jar and unzip
# mv JIST-2014Dec12-03-34PM.jar /mipav/plugins
# cd /mipav/plugins && unzip JIST-2014Dec12-03-34PM.jar