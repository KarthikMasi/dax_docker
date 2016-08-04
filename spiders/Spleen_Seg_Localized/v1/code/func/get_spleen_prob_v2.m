function get_spleen_prob_v2(image,model_mat,landmark_mat,box_mat,...
    prob_mat_prefix)
% compute the spleen probablity
% by applying the features derived from the target
% to the pre-trained model

% image - intensity image filename
% model_mat - pre-trained model filename
% bodymask - binary body mask image filename
% landmark_mat - landmark position filename
% box_mat - ROI cropping coordinates
% prob_mat_prefix - output probability filename prefix for spleen ROI

% load
load(model_mat,'obj');
load(landmark_mat,'lpt');
load(box_mat,'boxestpadvox');
nii=load_untouch_nii_gz(image);
voxdim=nii.hdr.dime.pixdim(2:4);

[XV,YV,ZV]=ndgrid(1:size(nii.img,1),1:size(nii.img,2),1:size(nii.img,3));
for c=1:1
    xrange=boxestpadvox(c,1):boxestpadvox(c,2);
    yrange=boxestpadvox(c,3):boxestpadvox(c,4);
    zrange=boxestpadvox(c,5):boxestpadvox(c,6);
    % derive features
    Rim=double(nii.img(xrange,yrange,zrange));
    Gim=AbsGradient3D(Rim);
    Mim=AbsSecondCentralMoment(Rim);
    X=XV(xrange,yrange,zrange);
    Y=YV(xrange,yrange,zrange);
    Z=ZV(xrange,yrange,zrange);
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
    prob_mat=sprintf('%s_%02d.mat',prob_mat_prefix,c);
    save(prob_mat,'Prob','-v7.3');
end
end