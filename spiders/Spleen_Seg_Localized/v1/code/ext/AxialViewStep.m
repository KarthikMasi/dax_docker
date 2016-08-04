function AxialViewStep(vol,n,type)
% look at every other 10 axial slices of one volume 
% vol - volume
% n - figure id
% type - 1:grayscale others:original

if size(vol,3)<10 
    step=1;
elseif size(vol,3)>500
    step=50;
else
    step=10;
end

for i=1:step:size(vol,3)
    figure(n);
    imagesc(vol(:,:,i));
    title(num2str(i));
    if type==1
        colormap('gray');
    end
    pause;
end