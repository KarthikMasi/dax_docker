#
# This sets up a base dax docker image
#
FROM ubuntu:14.04
# Install updates, pip and dax
RUN \
apt-get update && \
apt-get -y install wget && \
wget -O- http://neuro.debian.net/lists/trusty.us-tn.full | tee /etc/apt/sources.list.d/neurodebian.sources.list && \
apt-key adv --recv-keys --keyserver hkp://pgp.mit.edu:80 0xA5D32F012649A5A9 && \
apt-get update && \
apt-get -y install fsl-5.0-complete && \
echo "fsl5.0-run_first_all -i input.nii.gz -o output.nii.gz -s L_Accu,L_Amyg,L_Caud,L_Hipp,L_Pall,L_Puta,L_Thal,R_Accu,R_Amyg,R_Caud,R_Hipp,R_Pall,R_Puta,R_Thal" > /home/run.sh

#Set to bash
CMD ["bash"]

# Now setup MATLAB using silent install