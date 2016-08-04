function phi=Vol2Phi(vol)
% given a binary vol/image
% produce the signed distance function for level set
if length(unique(vol(:)))~=2
    error('the input volume should be binary');
end
phi=bwdist(vol)-bwdist(1-vol)+im2double(vol)-0.5;