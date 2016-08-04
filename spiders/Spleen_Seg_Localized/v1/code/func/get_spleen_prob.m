function get_spleen_prob(image,model_mat,landmark_mat,prob_mat)
% compute the spleen probablity
% by applying the features derived from the target 
% to the pre-trained model

% image - intensity image filename
% model_mat - pre-trained model filename
% bodymask - binary body mask image filename
% landmark_mat - landmark position filename
% prob_mat - output probability filename saved in mat

% load 
load(model_mat,'obj');
load(landmark_mat,'lpt');
nii=load_untouch_nii_gz(image);
voxdim=nii.hdr.dime.pixdim(2:4);

% derive features
Rim=double(nii.img);
Gim=AbsGradient3D(Rim);
Mim=AbsSecondCentralMoment(Rim);
[X,Y,Z]=ndgrid(1:size(Rim,1),1:size(Rim,2),1:size(Rim,3));
Pim=([X(:),Y(:),Z(:)]-repmat(lpt(:)',[numel(Rim),1]))...
    .*repmat(voxdim,[numel(Rim),1]);
XXX=cat(2,Rim(:),Gim(:),Mim(:),Pim);

% apply to pre-trained model
Est=zeros([numel(Rim),length(obj)]);
Prob=zeros([size(Rim),length(obj)]);
for k=1:length(obj)
    Est(:,k)=pdf(obj{k},XXX);
end

% the obj contains 16 pdfs for 13 organs + fat/muscle/other tissues
% gather everything bu spleen as background -> Est(:,1)
Est=cat(2,sum(Est(:,2:end),2),Est(:,1));

% convert to probabilities that summed to 1 for each voxel
sumEst=sum(Est,2);
for k=1:size(Est,2)
    Est(:,k)=Est(:,k)./sumEst;
    if k==1
        Est(sumEst(:)==0,k)=1;
    else
        Est(sumEst(:)==0,k)=0;
    end
    Prob(:,:,:,k)=reshape(Est(:,k),size(Rim));
end

% save
save(prob_mat,'Prob','-v7.3');
end