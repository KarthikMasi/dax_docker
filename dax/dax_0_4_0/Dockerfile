#
# This sets up a base dax docker image
#
FROM ubuntu:14.04
# Install updates, pip and dax
RUN \
apt-get update && \
apt-get -y install python-pip && \
apt-get -y install libxml2-dev libxslt1-dev python-dev zlib1g-dev && \\
apt-get install wget zip unzip && \\
wget https://github.com/VUIIS/dax/archive/v0.4.0.zip && \\
unzip v0.4.0.zip && cd dax-0.4.0 && \\
python setup.py install
#Set to bash
CMD ["bash"]
