function Img_smooth=GauSmooth(Img,sigma,range)
% Image smoothing with Gaussian kernel
% Applicable for 1D 2D 3D
% Input: Img - image to smooth
%        sigma - variance of the gaussian kernel
%        range - the number of sample involved in the smoothing
%        e.g., sigma=1.2;range=15;

left=fix(-range/2);
right=fix(range/2);
gRange=left:right;

switch ndims(Img)
    case 1
        G0=1/(sqrt(2*pi)*sigma)*exp(-0.5*gRange.^2/(sigma^2));
        Img_smooth=conv(Img,G0,'same');
    case 2
        G0=fspecial('gaussian',range,sigma);
        Img_smooth=conv2(Img,G0,'same');
    case 3
        G0=1/(sqrt(2*pi)*sigma)*exp(-0.5*gRange.^2/(sigma^2));
        lenG=length(G0(:));
        G1=reshape(G0,[lenG 1 1]);
        G2=reshape(G0,[1 lenG 1]);
        G3=reshape(G0,[1 1 lenG]);
        Img_smooth=convn(Img,G1,'same');
        Img_smooth=convn(Img_smooth,G2,'same');
        Img_smooth=convn(Img_smooth,G3,'same');
end
        