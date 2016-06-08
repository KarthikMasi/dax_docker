#
# Ubuntu Dockerfile
#
# https://github.com/dockerfile/ubuntu
#
# Pull base image.
FROM ubuntu:14.04
# Install.
RUN \
apt-get update && \
pip install dax
# Add files.
# Define default command.
CMD ["bash"]
