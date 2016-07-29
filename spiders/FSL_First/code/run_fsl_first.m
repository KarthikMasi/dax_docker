function run_fsl_first(T1MRI_input, outputDir) 
% version 1.0 
% Bennett Landman, bennett.landman@vanderbilt.edu
% 12/6/2013 
% Runs FSL FIRST with default parameters (avoiding brainstem labels)

[P,F,E] = fileparts(T1MRI_input);
[PP,FF,EE]=fileparts(F)
system(['run_first_all -i ' T1MRI_input ...
    ' -o ' outputDir filesep F ...
    ' -s L_Accu,L_Amyg,L_Caud,L_Hipp,L_Pall,L_Puta,L_Thal,R_Accu,R_Amyg,R_Caud,R_Hipp,R_Pall,R_Puta,R_Thal'])

% The labels match that of the CMA's labelling except for label 16. We have merged the brain stem and 4th Ventricle into label 16. We also provide our suggested number of modes of variation for each structure (3rd column). The colour look-up table is provided in fslview. 
% 4 Left-Lateral-Ventricle 40 
% 10 Left-Thalamus-Proper 40 
% 11 Left-Caudate 30 
% 12 Left-Putamen 40 
% 13 Left-Pallidum 40 
% 16 Brain-Stem /4th Ventricle 40 
% 17 Left-Hippocampus 30 
% 18 Left-Amygdala 50 
% 26 Left-Accumbens-area 50 
% 43 Right-Lateral-Ventricle 40 
% 49 Right-Thalamus-Proper 40 
% 50 Right-Caudate 30 
% 51 Right-Putamen 40 
% 52 Right-Pallidum 40 
% 53 Right-Hippocampus 30 
% 54 Right-Amygdala 50 
% 58 Right-Accumbens-area 50 

file=dir([outputDir filesep FF '_all_none_firstseg.nii.gz']);
if(length(file)<1)
    error('no output found')
end

nii = load_nii_gz([outputDir filesep file(1).name]);
save_nii_gz(nii,[outputDir filesep 'RESULT.nii.gz'])

vxres = prod(nii.hdr.dime.pixdim(2:4));

ids = {10, 'Left-Thalamus-Proper'; ...
    11, 'Left-Caudate'; ...
    12,'Left-Putamen'; ... 
    13,'Left-Pallidum'; ...
    17,'Left-Hippocampus'; ...
    18,'Left-Amygdala'; ...
    26,'Left-Accumbens-area'; ...
    49,'Right-Thalamus-Proper'; ...
    50,'Right-Caudate'; ...
    51,'Right-Putamen'; ...
    52,'Right-Pallidum'; ...
    53,'Right-Hippocampus'; ... 
    54,'Right-Amygdala'; ...
    58,'Right-Accumbens-area'} 

fp = fopen([outputDir filesep 'RESULT.CSV'],'wt');
for i=1:size(ids,1)
    vol = vxres*sum(nii.img(:)==ids{i,1});
    fprintf(fp,'%d\t%s(mm3)\t%.03f\n',ids{i,1},ids{i,2},vol);
end
fclose(fp);

ind=find(nii.img(:)>0);[i,j,k]=ind2sub(size(nii.img),ind);

figure(1); clf
montage(uint8(permute(nii.img(:,:,min(k):max(k)),[1 2 4 3])))
colormap([0 0 0; hsv(58)]); caxis([9 58])
axis equal
img = getframe;

figure(1); clf
nii = load_nii_gz(T1MRI_input);
montage(uint8(permute(nii.img(:,:,min(k):max(k)),[1 2 4 3])))

axis equal
img2 = getframe;

img3 = uint8( (double(img.cdata)/255+double(img2.cdata)/255)/2*255);

figure(1);
clf;
imagesc(img3);
axis off
title({'FSL FIRST (default parameters)',FF,date},'Interpreter', 'none')
saveas(gcf,[outputDir filesep 'RESULT.pdf'])

