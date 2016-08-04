function G=AbsGradient3D(img)
if ndims(img)~=3,error('img has to be 3D matrix');end
[Ix,Iy,Iz]=gradient(img);
G=sqrt(Ix.^2+Iy.^2+Iz.^2);