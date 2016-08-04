function spleen_context_learning_v2(mod_image,tgtfn,sub_dir,cl_param)
% estimate organ-wise probablity based on pre-trained gmm models
% using spatial and intensity features of the target image
% ouputs a two-fold probability map 
% (foregroud - spleen, background - everything else)

% mod_image - raw image
% tgtfn - target filename used for naming processed data
% sub_dir - output image directory
% cl_param - a struct that includes the pre-trained models

% internal hierarchy predefined
% prerequisite < - prepare_image
% next to run - > label_fusion

if ~isfield(cl_param,'gmm_model'),error('gaussian mixture model missing');end

cl_dir=[sub_dir,'context/'];mkdir_p(cl_dir);
cl_body_dir=[cl_dir,'body/'];mkdir_p(cl_body_dir);
cl_landmark_dir=[cl_dir,'landmark/'];mkdir_p(cl_landmark_dir);
cl_prob_dir=[cl_dir,'prob/'];mkdir_p(cl_prob_dir);

rf_dir=[sub_dir,'localization/'];
rf_bb_dir=[rf_dir,'bbtxt/'];
box_mat=[rf_bb_dir,tgtfn,'.mat'];

% get body mask
body_mask=[cl_body_dir,tgtfn,'.nii.gz'];
get_body_mask(mod_image,body_mask);
% get landmark at the lung
landmark_mat=[cl_landmark_dir,tgtfn,'.mat'];
get_landmark_at_lung(mod_image,body_mask,landmark_mat);
% get learned probability
% prob_mat=[cl_prob_dir,tgtfn,'.mat'];
prob_mat_prefix=[cl_prob_dir,tgtfn];
get_spleen_prob_v2(mod_image,cl_param.gmm_model,landmark_mat,box_mat,...
    prob_mat_prefix)
end