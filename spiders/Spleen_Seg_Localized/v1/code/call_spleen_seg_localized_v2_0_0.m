clear all;close all;
% using deeds registration 

% abdominal organ segmentation
% for 13 organs
% 1. spleen, 2. right kidney, 3. left kidney, 4. gallbladder
% 5. esophagus, 6. liver, 7. stomach, 8. aorta
% 9. inferior vena cava, 10. portal and splenic vein, 11. pancreas
% 12. right adrenal gland, 13. left adrenal gland

addpath(genpath(pwd));
work_dir=pwd;

% please specify the locations for the tools, atlases, target, and output
% ======================================================================= %
tools=struct;
tools.fslorient='export FSLOUTPUTTYPE=NIFTI_GZ && /share/bin/fsl-64/bin/fslorient'; 
tools.irtk_resample='/share/bin/irtk/resample';
% tools.niftyreg_loc='/home/local/VANDERBILT/xuz8/Desktop/niftyreg/reg-apps/';
tools.deeds_path='/home/local/VANDERBILT/xuz8/deeds_build/bin/';
tools.jf_loc='/home/local/VANDERBILT/xuz8/antsbin/bin/jointfusion';

atlas_loc=struct;
atlas_loc.raw='/fs1/masi/zhoubing/SUBMIT_2015_SATA/Training/CropAlign/cropraw/';
atlas_loc.label='/fs1/masi/zhoubing/SUBMIT_2015_SATA/Training/CropAlign/croplabel/';
out_dir='/fs1/masi/zhoubing/organ_temp/testspleen/';

target_image='/fs1/masi/zhoubing/MICCAI-2015-SATA/Abdomen/RawData/Testing/img/img0073.nii.gz';
% ======================================================================= %

[~,javaloc]=system('which java');
javaloc=javaloc(1:end-1);

rf_param=struct;
rf_param.resample_size=[6,6,6];
rf_param.centered_fov_radii=150;
rf_param.roi_pad=50;
rf_param.forest_xml_dir='/fs1/masi/zhoubing/SUBMIT_2015_SATA/Forest2/';
rf_param.javaloc=javaloc;
rf_param.classpath='./RegressionForest/bin';
rf_param.func='regressionforest.RunTestingOrganInBatch_1';
rf_param.classCount=13;
rf_param.dimCount=6;
rf_param.confidentDataPercentage=0.1;

reg_param=struct;

cl_param=struct;
cl_param.gmm_model='./obj_gmm_igvp.mat'; % gmm model used for atlas selection

fusion_param=struct;
fusion_param.atlasnum=5;
fusion_param.rp=2;
fusion_param.rs=3;
fusion_param.dim=3;
fusion_param.mod=1;
fusion_param.alpha=0.1;
fusion_param.beta=2;

spleen_seg_localized_v3(target_image,out_dir,...
    tools,rf_param,reg_param,atlas_loc,cl_param,fusion_param);