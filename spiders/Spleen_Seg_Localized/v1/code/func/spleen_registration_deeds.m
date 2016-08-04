function spleen_registration_deeds(mod_image,tgtfn,sub_dir,...
    tools,reg_param,atlas_loc)
% run registrations between localized spleen ROIs

% mod_image - raw image
% tgtfn - target filename used for naming processed data
% sub_dir - output image directory
% tools - a struct that includes full path of essential tools
% reg_param - a struct that includes registration paramters
% atlas_loc - a struct that includes paths of atlases (organ-wise)

% internal hierarchy predefined
% prerequisite < - spleen_localization
% < - spleen_prepare
% next to run - > spleen_context_learning

deeds_loc=[tools.deeds_path,'runDeeds.sh'];
binary_dir=tools.deeds_path;

rf_dir=[sub_dir,'localization/'];
rf_organ_roi_dir=[rf_dir,'organ_roi/'];

reg_dir=[sub_dir,'regs/'];mkdir_p(reg_dir);
regimg_dir=[reg_dir,'regimg/'];mkdir_p(regimg_dir);
reglabel_dir=[reg_dir,'reglabel/'];mkdir_p(reglabel_dir);
regtfm_dir=[reg_dir,'regtfm/'];mkdir_p(regtfm_dir);

k=1;
regimg_organ_dir=[regimg_dir,sprintf('L%02d',k),'/'];mkdir_p(regimg_organ_dir);
reglabel_organ_dir=[reglabel_dir,sprintf('L%02d',k),'/'];mkdir_p(reglabel_organ_dir);
regtfm_organ_dir=[regtfm_dir,sprintf('L%02d',k),'/'];mkdir_p(regtfm_organ_dir);
atlas_image_fns=dir([atlas_loc.raw,'*_',sprintf('%02d',k),'.nii.gz']);
atlas_label_fns=dir([atlas_loc.label,'*_',sprintf('%02d',k),'.nii.gz']);
if length(atlas_image_fns)~=length(atlas_label_fns)
    error('atlas image and atlas label may not match');
end

for j=1:length(atlas_image_fns)
    fprintf('(%d, %d)\n',k,j);
    filename=atlas_image_fns(j).name;
    srcfn=filename(1:strfind(filename,['_',sprintf('%02d',k),'.nii.gz'])-1);
    outfn=[srcfn,'-',tgtfn,'_',sprintf('%02d',k)];
    InImg=[atlas_loc.raw,filename];
    InLabel=[atlas_loc.label,atlas_label_fns(j).name];
    RefImg=[rf_organ_roi_dir,tgtfn,sprintf('_%02d',k),'.nii.gz'];
    if isempty(dir(InLabel)),continue;end
    OutNonRigidImg=[regimg_organ_dir,outfn,'.nii.gz'];
    OutNonRigidLabel=[reglabel_organ_dir,outfn,'.nii.gz'];
    if ~isempty(dir(OutNonRigidLabel)),continue;end
    
    cmds_reg{1}=sprintf('%s %s %s %s %s %s %d %d %s %s\n',...
            deeds_loc,RefImg,InImg,InLabel,OutNonRigidImg,OutNonRigidLabel,...
            0,j,binary_dir,regtfm_organ_dir);
    
    for cm=1:length(cmds_reg)
        if isempty(cmds_reg{cm}),continue;end
        system(cmds_reg{cm});
    end
end
end
