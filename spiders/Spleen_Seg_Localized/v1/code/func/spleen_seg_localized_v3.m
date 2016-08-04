function spleen_seg_localized_v3(target_image,out_dir,...
    tools,rf_param,reg_param,atlas_loc,cl_param,fusion_param)

% target_image - target raw image
% out_dir - output directory, whose internal folder structure is predefined
% tools - a struct that contains path of niftyreg fsl irtk jlf
% rf_param - parameters for random forest localization
% reg_param - parameters for registrations
% atlas_loc - where the atlas image and labels are
% cl_param - parameters for context learning
% fusion_param - parameters for label fusion

[~,tgtfn,~]=fileparts(target_image);
tgtfn=tgtfn(1:strfind(tgtfn,'.nii')-1);
% create a folder for the target subject
% sub_dir=[out_dir,tgtfn,'/'];mkdir_p(sub_dir);
sub_dir=out_dir;

mod_dir=[sub_dir,'mod/'];mkdir_p(mod_dir);
mod_image=[mod_dir,tgtfn,'.nii.gz'];

disp('spleen prepare');
spleen_prepare(target_image,tgtfn,sub_dir,tools);
disp('spleen localization');
spleen_localization(mod_image,tgtfn,sub_dir,tools,rf_param);
disp('spleen context learning');
spleen_context_learning_v2(mod_image,tgtfn,sub_dir,cl_param);
disp('spleen registration');
spleen_registration_deeds(mod_image,tgtfn,sub_dir,tools,reg_param,atlas_loc);
disp('spleen fusion');
spleen_fusion_v2(mod_image,tgtfn,sub_dir,tools,fusion_param);

output_image=[sub_dir,'fusion/',tgtfn,'.nii.gz'];
final_dir=[sub_dir,'final/'];mkdir_p(final_dir);
% present final segmentation in the subject folder
system(sprintf('cp %s %s\n',output_image,[final_dir,tgtfn,'.nii.gz']));
end
