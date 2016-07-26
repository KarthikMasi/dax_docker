#
# This sets up a base dax docker image
#
FROM ubuntu:14.04
# Install updates, pip and dax
RUN \
apt-get update && \
apt-get -y install wget && \
apt-get -y install xvfb && \
wget -O- http://neuro.debian.net/lists/trusty.us-tn.full | tee /etc/apt/sources.list.d/neurodebian.sources.list && \
apt-key adv --recv-keys --keyserver hkp://pgp.mit.edu:80 0xA5D32F012649A5A9 && \
apt-get update && \
apt-get -y install fsl-5.0-complete && \
COPY run /opt/run
#Set to bash
CMD ["bash"]
# Now setup MATLAB using silent install