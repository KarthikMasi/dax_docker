function ASCM=AbsSecondCentralMoment(img)
% the L2 norm of the second central moment (variance) 
% of three adjacent voxels for each dimension  
if ndims(img)~=3,error('img has to be 3D matrix');end
K0=[1/3;1/3;1/3];
lenK=length(K0);
K1=reshape(K0,[lenK 1 1]);
K2=reshape(K0,[1 lenK 1]);
K3=reshape(K0,[1 1 lenK]);
IMG=img.^2;
M1=convn(IMG,K1,'same')-power(convn(img,K1,'same'),2);
M2=convn(IMG,K2,'same')-power(convn(img,K2,'same'),2);
M3=convn(IMG,K3,'same')-power(convn(img,K3,'same'),2);
ASCM=sqrt(M1.^2+M2.^2+M3.^2);