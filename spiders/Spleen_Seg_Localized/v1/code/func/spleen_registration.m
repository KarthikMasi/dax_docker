function spleen_registration(mod_image,tgtfn,sub_dir,...
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
    OutAffineTfm=[regtfm_organ_dir,outfn,'_affine.txt'];
    OutAffineImg=[regimg_organ_dir,outfn,'_affine.nii.gz'];
    OutAffineLabel=[reglabel_organ_dir,outfn,'_affine.nii.gz'];
    OutNonRigidTfm=[regtfm_organ_dir,outfn,'.nii.gz'];
    OutNonRigidImg=[regimg_organ_dir,outfn,'.nii.gz'];
    OutNonRigidLabel=[reglabel_organ_dir,outfn,'.nii.gz'];
    if ~isempty(dir(OutNonRigidLabel)),continue;end
    cmds_reg{1}=sprintf(['[ ! -f %s ] && %sreg_aladin ',...
        '-ln %d -omp %d ',...
        '-ref %s -flo %s -res %s -aff %s\n'],...
        OutAffineImg,tools.niftyreg_loc,...
        reg_param.num_levels,reg_param.num_threads,...
        RefImg,InImg,OutAffineImg,OutAffineTfm);
    cmds_reg{2}=sprintf(['[ ! -f %s ] && %sreg_resample ',...
        '-inter 0 -ref %s -flo %s -trans %s -res %s\n'],...
        OutAffineLabel,tools.niftyreg_loc,...
        RefImg,InLabel,OutAffineTfm,OutAffineLabel);
    cmds_reg{3}=sprintf(['[ ! -f %s ] && %sreg_f3d ',...
        '-ln %d --rUpTh %d --fUpTh %d ',...
        '-omp %d -maxit %d ',...
        '-ref %s -flo %s -aff %s -cpp %s -res %s\n'],...
        OutNonRigidImg,tools.niftyreg_loc,...
        reg_param.num_levels,reg_param.ref_up_thresh,reg_param.in_up_thresh,...
        reg_param.num_threads,reg_param.maxit,...
        RefImg,InImg,OutAffineTfm,OutNonRigidTfm,OutNonRigidImg);
    cmds_reg{4}=sprintf(['[ ! -f %s ] && %sreg_resample ',...
        '-inter 0 -ref %s -flo %s -trans %s -res %s\n'],...
        OutNonRigidLabel,tools.niftyreg_loc,...
        RefImg,InLabel,OutNonRigidTfm,OutNonRigidLabel);
    
    for cm=1:length(cmds_reg)
        if isempty(cmds_reg{cm}),continue;end
        system(cmds_reg{cm});
    end
end

end
