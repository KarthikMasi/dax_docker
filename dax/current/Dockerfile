#
# This sets up a base dax docker image
#
FROM ubuntu:14.04
# Install updates, pip and dax
RUN \
apt-get update && \
apt-get -y install python-pip && \
apt-get -y install libxml2-dev libxslt1-dev python-dev zlib1g-dev && \\
pip install dax
#Set to bash
CMD ["bash"]
