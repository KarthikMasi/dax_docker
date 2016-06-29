#
# This sets up a base dax docker image
#
FROM ubuntu:14.04
# Install updates, pip and dax
RUN \
apt-get update && \
apt-get -y install python-pip && \
apt-get -y install libxml2-dev libxslt1-dev python-dev zlib1g-dev && \
apt-get -y install git && \
git clone git://git.code.sf.net/p/niftyreg/git niftyreg-git && \
ccmake ANTs &&

#Set to bash
CMD ["bash"]
