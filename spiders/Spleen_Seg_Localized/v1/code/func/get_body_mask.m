function get_body_mask(fin,fout)
% get a binary mask of the body
% use kmeans to separate the body from background on CT 

% fin - input intenst image filename
% fout -output mask image filename

se=strel('disk',5); % structural element for morphological operation
pad=50; % pad to ensure the background area is larger the body
nii=load_untouch_nii_gz(fin);


% the two processes below remove large areas 
% distinctive from the background (if there is any)
% which would affect the body/background kmeans clustering

% 1. enforce the minimum intensity of background
nii.img(nii.img<-1024)=-1024; 
% 2. remove zero-padding regions if there is any
% usually happens if the image is registered (assuming larger that 50 voxels)
[r,~]=bwlabeln(nii.img==0);
s=accumarray(r(r(:)>0),1);
idsbig=find(s>50);
for i=1:length(idsbig)
    nii.img(r==idsbig(i))=-1024;
end

% kmeans clustering
count=0; % try 1000 times in case of random failure
while (count<1000)
    try
        IDX=kmeans(double(nii.img(:)),2);break;
    catch ME
        count=count+1;continue;
    end
end

% determine the foreground cluster 
% (assuming the majority of the first 5 voxels is background)
Mim=reshape(double(IDX~=mode(IDX(1:5,1,1))),size(nii.img));

% fill holes
% with padding to ensure the holes are smaller than background
Mim=imopen(Mim,se);
BigMim=zeros(size(Mim,1)+2*pad,size(Mim,2)+2*pad,size(Mim,3));
BigMim(pad+1:end-pad,pad+1:end-pad,:)=Mim;
BigMim=LargestCC(BigMim,0);
BigMim=FillHoles(BigMim,1);
Mim=BigMim(pad+1:end-pad,pad+1:end-pad,:);

% save 
newnii=nii;
newnii.img=uint8(Mim);
newnii.hdr.dime.datatype=2;
newnii.hdr.dime.bitpix=8;
newnii.hdr.dime.glmax=max(Mim(:));
newnii.hdr.dime.glmin=min(Mim(:));
save_untouch_nii_gz(newnii,fout);
end