function spleen_prepare(target_image,tgtfn,sub_dir,tools)
% convert image into standard format for later processing
% same as the multi-organ version

% target_image - absolute address of the target intensity image
% tgtfn - target filename used for naming processed data
% sub_dir - output folder for the target 
% tools - a struct that includes the absolute address of tools

% internal folder hierarchy predefined
% next to run -> spleen_localization

if ~isfield(tools,'fslorient'),
    error('fslorient location missing');
end

mod_dir=[sub_dir,'mod/'];mkdir_p(mod_dir);
% copy the "target_image" to "mod_image" for deleting orientation info
% this copy is necessary because "fslorient" edit directly on the image
% otherwise it corrupt the original image
mod_image=[mod_dir,tgtfn,'.nii.gz'];
cmd_copy=sprintf('cp %s %s\n',target_image,mod_image);
system(cmd_copy);
% delete the orientation in header
cmd_delorient=sprintf('%s -deleteorient %s\n',tools.fslorient,mod_image);
system(cmd_delorient);
% removing intensity offsets from the header
% apply (if there is any) scl_inter to the image content 
% (assuming scl_slope = 1)
nii=load_untouch_nii_gz(mod_image);
if nii.hdr.dime.scl_inter~=0
    nii.img=nii.img+nii.hdr.dime.scl_inter;
    nii.hdr.dime.scl_inter=0;
    nii.hdr.dime.glmax=max(nii.img(:));
    nii.hdr.dime.glmin=min(nii.img(:));
    save_untouch_nii_gz(nii,mod_image);
end
end