function get_muscle_fat_mask(target_image,body_mask,muscle_mask,fat_mask)
% using fuzzy c-means clustering to separate the muscle from the fat tissue
% given the raw intensity image, and body mask

% target_image - filename of the input raw image
% body_mask - filename of the input body mask
% muscle_mask - filename of the output muscle mask
% fat_mask - filename of the output fat mask

RawNii=load_untouch_nii_gz(target_image);
BodyNii=load_untouch_nii_gz(body_mask);

rr=double(RawNii.img);
kk=double(BodyNii.img);
kk(rr<-500)=0; % considered as air

%% initialization
cc=ones(size(rr));
cc(kk==0)=0;
% roi
uu=rr(kk>0);
id1=find(kk>0);
%% first fcm
[center,u,~]=fcm(uu(:),2);
% make sure that center(1)->muscle, center(2)->fat
if center(1)<center(2)
    center=flipdim(center,1);
    u=flipdim(u,1);
end
% eliminate the region of bones and air
sgm1=sqrt(var(uu(:),u(1,:)'));
sgm2=sqrt(var(uu(:),u(2,:)'));
id_bone=find(uu>center(1)+2*sgm1);
id_air=find(uu<center(2)-2*sgm2);
id_boneair=union(id_bone,id_air);
cc(id1(id_bone))=0;
cc(id1(id_air))=0;
id2=id1;
id2(id_boneair)=[];
clear uu;
uu2=rr(id2);
clear rr;

%% second fcm
[center,u,~]=fcm(uu2(:),2);
if center(1)<center(2)
    center=flipdim(center,1);
    u=flipdim(u,1);
end
cc(id2)=u(2,:)';
clear u;

%% save
Muscle=double(cc<0.5);
Muscle(kk==0)=0;
Muscle(id1(id_boneair))=0;
MuscleMaskNii=BodyNii;
MuscleMaskNii.img=Muscle;
save_untouch_nii_gz(MuscleMaskNii,muscle_mask);
clear Muscle MuscleMaskNii

Fat=double(cc>=0.5);
FatMaskNii=BodyNii;
FatMaskNii.img=Fat;
save_untouch_nii_gz(FatMaskNii,fat_mask)
end