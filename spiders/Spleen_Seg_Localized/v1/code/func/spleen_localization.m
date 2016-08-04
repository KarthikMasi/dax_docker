function spleen_localization(mod_image,tgtfn,sub_dir,tools,rf_param)
% localize and create spleen region of interest ussing random forest
% this function use the forest trained for 13 organs
% thus 13 ROIs can be derived, while we only crop the spleen here

% mod_image - raw image
% tgtfn - target filename used for naming processed data
% sub_dir - output image directory
% tools - a struct that includes full path of essential tools
% rf_param - a struct that includes random forest paramters

% internal hierarchy predefined
% prerequisite < - spleen_prepare
% next to run - > spleen_registration

% parameters
irtk_resample=tools.irtk_resample;
resample_size=rf_param.resample_size;
centered_fov_radii=rf_param.centered_fov_radii;
roi_pad=rf_param.roi_pad;
forest_xml_dir=rf_param.forest_xml_dir;
javaloc=rf_param.javaloc;
classpath=rf_param.classpath;
func=rf_param.func;
classCount=rf_param.classCount;
dimCount=rf_param.dimCount;
confidentDataPercentage=rf_param.confidentDataPercentage;

rf_dir=[sub_dir,'localization/'];mkdir_p(rf_dir);
%% resample and crop
rf_resample_dir=[rf_dir,'resampled/'];mkdir_p(rf_resample_dir);
rf_crop_dir=[rf_dir,'cropped/'];mkdir_p(rf_crop_dir);
rf_resample_image=[rf_resample_dir,tgtfn,'.nii.gz'];
cmd_resample=sprintf('%s %s %s -size %0.1f %0.1f %0.1f -linear\n',...
    irtk_resample,mod_image,rf_resample_image,...
    resample_size(1),resample_size(2),resample_size(3));
system(cmd_resample);

rf_crop_image=[rf_crop_dir,tgtfn,'.nii.gz'];
nii=load_untouch_nii_gz(rf_resample_image);
voxdim=nii.hdr.dime.pixdim(2:4);
s=nii.hdr.dime.dim(2:4);
r=centered_fov_radii;
right=max(1,floor(s(1)/2-r/voxdim(1)));
left=min(s(1),ceil(s(1)/2+r/voxdim(1)));
back=max(1,floor(s(2)/2-r/voxdim(2)));
front=min(s(2),ceil(s(2)/2+r/voxdim(2)));
newnii=nii;
newnii.img=nii.img(right:left,back:front,:);
newnii.hdr.dime.dim(2:4)=size(newnii.img);
newnii.hdr.dime.glmax=max(newnii.img(:));
newnii.hdr.dime.glmin=min(newnii.img(:));
save_untouch_nii_gz(newnii,rf_crop_image);

%% apply trained forest (in batch)
rf_bb_dir=[rf_dir,'bbtxt/'];mkdir_p(rf_bb_dir);
cmd_test_rf=sprintf(['%s -cp %s %s ',...
    '%s %s %s ',...
    '%d %d %0.2f\n'],...
    javaloc,classpath,func,...
    forest_xml_dir,rf_crop_dir,rf_bb_dir,...
    classCount,dimCount,confidentDataPercentage);
system(cmd_test_rf);

%% load resultant localization and crop
rf_organ_roi_dir=[rf_dir,'organ_roi/'];mkdir_p(rf_organ_roi_dir);
rf_bb_txt=[rf_bb_dir,tgtfn,'.txt'];
% load localization result
boxest=loadBoxTxt(rf_bb_txt);
% crop organ roi with some padding
nii=load_untouch_nii_gz(mod_image);
voxdim=nii.hdr.dime.pixdim(2:4);
s=nii.hdr.dime.dim(2:4);
right=max(1,floor(s(1)/2-r/voxdim(1)));
back=max(1,floor(s(2)/2-r/voxdim(2)));
boxest(:,[1 2])=boxest(:,[1 2])+(right-1)*voxdim(1); % adjust in uncropped image space
boxest(:,[3 4])=boxest(:,[3 4])+(back-1)*voxdim(2);
pad=roi_pad; % add some padding to each boundary
boxestpadvox(:,1)=max(1,round((boxest(:,1)-pad)/voxdim(1)));
boxestpadvox(:,2)=min(s(1),round((boxest(:,2)+pad)/voxdim(1)));
boxestpadvox(:,3)=max(1,round((boxest(:,3)-pad)/voxdim(2)));
boxestpadvox(:,4)=min(s(2),round((boxest(:,4)+pad)/voxdim(2)));
boxestpadvox(:,5)=max(1,round((boxest(:,5)-pad)/voxdim(3)));
boxestpadvox(:,6)=min(s(3),round((boxest(:,6)+pad)/voxdim(3)));

k=1;
rf_organ_roi_image=[rf_organ_roi_dir,tgtfn,sprintf('_%02d',k),'.nii.gz'];
newnii=nii;
newnii.img=nii.img(boxestpadvox(k,1):boxestpadvox(k,2),...
    boxestpadvox(k,3):boxestpadvox(k,4),...
    boxestpadvox(k,5):boxestpadvox(k,6));
newnii.hdr.dime.dim(2:4)=size(newnii.img);
newnii.hdr.dime.glmax=max(newnii.img(:));
newnii.hdr.dime.glmin=min(newnii.img(:));
save_untouch_nii_gz(newnii,rf_organ_roi_image);
box_mat=[rf_bb_dir,tgtfn,'.mat']; % save "boxest" and boxestpadvox for record
save(box_mat,'boxest','boxestpadvox');
end

function box = loadBoxTxt(txt)
fid=fopen(txt);
tline=fgetl(fid);
idspace=strfind(tline,' ');
s=zeros(2,1);
s(1)=str2num(tline(1:idspace(1)-1));
s(2)=str2num(tline(idspace(1)+1:end));
box=zeros(prod(s),1);
count=0;
while 1
    tline=fgetl(fid);
    if ~ischar(tline), break, end
    count=count+1;
    box(count)=str2double(tline);
end
fclose(fid);
box=reshape(box,[s(2) s(1)]);
box=box';
end
