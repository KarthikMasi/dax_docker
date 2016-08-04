function get_landmark_at_lung(image,bodymask,landmark_mat)
% derive a landmark at the lung area
% roughly at the mid-frontal point on the axial slice 
% with the largest lung area

% image - intensity image filename
% bodymask - binary body mask image filename
% landmark_mat - output filename to save the landmark position

% load images
rawnii=load_untouch_nii_gz(image);
rim=double(rawnii.img);
voxdim=rawnii.hdr.dime.pixdim(2:4);
bodynii=load_untouch_nii_gz(bodymask);
bim=double(bodynii.img);

% roughly segment lung area
rim(bim==0)=200; % ignoring background area
% gather chunks of areas larger than 10 cm^3 in less than -500 HUs 
[r,~]=bwlabeln(rim<-500);
s=accumarray(r(r(:)>0),1)*prod(voxdim);
idlungs=find(s>1e4);
Lung=zeros(size(rim));
for k=1:length(idlungs)
    Lung=Lung+double(r==idlungs(k)); 
end

% determine z
numlungvoxel=squeeze(sum(sum(Lung,2),1));
% for robustness, approximately get the first z-slice with 
% larger than 90% of the maximum slice-wise lung area
idz=find(numlungvoxel>0.9*max(numlungvoxel));
idz=idz(1);
lum=Lung(:,:,idz);
% determine x and y
[ix,iy]=ind2sub(size(lum),find(lum>0));
cc=[round(1/2*(max(ix)+min(ix))),max(iy)];
lpt=[cc(1),cc(2),idz];

% save
save(landmark_mat,'lpt');
end