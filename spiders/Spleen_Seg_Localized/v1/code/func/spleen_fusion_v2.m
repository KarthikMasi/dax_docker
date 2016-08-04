function spleen_fusion_v2(mod_image,tgtfn,sub_dir,...
    tools,fusion_param)
% label fusion on spleen
% using CLSIMPLE for atlas selection
% and then JLF for fusion
% derived from the code for multi-organ segmentation

% mod_image - raw image
% tgtfn - target filename used for naming processed data
% sub_dir - output image directory
% tools - a struct that includes the absolute address of tools
% fusion_param - a struct that includes fusion parameters

% internal hierarchy predefined
% prerequisite < - spleen_context_learning
% < - spleen_registration
% < - splenn_localization
% < - splenn_prepare

if ~isfield(tools,'jf_loc'),error('joint fusion location missing');end

lf_dir=[sub_dir,'fusion/'];mkdir_p(lf_dir);
combined_seg_dir=[lf_dir,'combined/'];mkdir_p(combined_seg_dir);
rf_dir=[sub_dir,'localization/'];
rf_organ_roi_dir=[rf_dir,'organ_roi/'];
rf_bb_dir=[rf_dir,'bbtxt/'];
cl_dir=[sub_dir,'context/'];
cl_prob_dir=[cl_dir,'prob/'];
reg_dir=[sub_dir,'regs/'];
reglabel_dir=[reg_dir,'reglabel/'];
regimg_dir=[reg_dir,'regimg/'];


outfn=[lf_dir,tgtfn,'.nii.gz'];
if ~isempty(dir(outfn)),return;end

RawNii=load_untouch_nii_gz(mod_image);
s=size(RawNii.img);
% prob_mat=[cl_prob_dir,tgtfn,'.mat'];
% load(prob_mat,'Prob');
box_mat=[rf_bb_dir,tgtfn,'.mat'];
load(box_mat,'boxestpadvox');
pfms=cell(1,1);
PB=zeros([s 1]);

fprintf('run CLSIMPLE\n');
for k=1:1
    fprintf('L%02d\n',k);
    organ_dir=sprintf('%sL%02d/',lf_dir,k);mkdir_p(organ_dir);
    target=[rf_organ_roi_dir,tgtfn,sprintf('_%02d',k),'.nii.gz'];
    TargetNii=load_untouch_nii_gz(target);
    count=0;
    if exist('reglabel_fns','var'),reglabel_fns=cell(0);end
    if exist('regimg_fns','var'),regimg_fns=cell(0);end
    reg_fns=dir(sprintf('%s/L%02d/*-%s_%02d.nii.gz',reglabel_dir,k,tgtfn,k));
    for j=1:length(reg_fns)
        filename=reg_fns(j).name;
        fout=filename(1:strfind(filename,'.nii.gz')-1);
        reglabelfn=[reglabel_dir,sprintf('L%02d/',k),fout,'.nii.gz'];
        regimgfn=[regimg_dir,sprintf('L%02d/',k),fout,'.nii.gz'];
        if (~isempty(dir(reglabelfn))&&(~isempty(dir(regimgfn))))
            count=count+1;
            reglabel_fns{count}=reglabelfn;
            regimg_fns{count}=regimgfn;
        end
    end
    disp(length(reglabel_fns));
    if isempty(reglabel_fns)
        disp('no atlases available');
        % make dummy singlelabel and pfms
        NewNii=TargetNii;
        NewNii.img=zeros(size(TargetNii.img));
        NewNii.hdr.dime.datatype=2;
        NewNii.hdr.dime.bitpix=8;
        NewNii.hdr.dime.glmax=max(est(:));
        NewNii.hdr.dime.glmin=min(est(:));
        output=sprintf('%sL%02d/%s_singlelabel_clsimple.nii.gz',lf_dir,k,tgtfn);
        save_untouch_nii_gz(NewNii,output);
        pfms{k}=struct;
        pfms{k}.iter=0;
        pfms{k}.num=0;
        pfms{k}.wt=NaN(1,1);
        continue;
    end
    disp('collect obs');
    for i=1:length(reglabel_fns)
        LabelNii=load_untouch_nii_gz(reglabel_fns{i});
        if i==1
            obs=uint8(zeros([size(LabelNii.img),length(reglabel_fns)]));
        end
        obs(:,:,:,i)=LabelNii.img;
    end
    prob_mat=[cl_prob_dir,tgtfn,'_',sprintf('%02d',k),'.mat'];
    load(prob_mat,'Prob');
    Pr=Prob(:,:,:,k+1);
%     Pr=Prob(boxestpadvox(k,1):boxestpadvox(k,2),...
%         boxestpadvox(k,3):boxestpadvox(k,4),...
%         boxestpadvox(k,5):boxestpadvox(k,6),...
%         k+1);
    Pr=cat(4,Pr,1-Pr);
    [est,pb,pfms{k}]=clsimple(uint8(obs==1),Pr,fusion_param.atlasnum);
    NewNii=TargetNii;
    NewNii.img=est;
    NewNii.hdr.dime.datatype=2;
    NewNii.hdr.dime.bitpix=8;
    NewNii.hdr.dime.glmax=max(est(:));
    NewNii.hdr.dime.glmin=min(est(:));
    output=sprintf('%sL%02d/%s_singlelabel_clsimple.nii.gz',lf_dir,k,tgtfn);
    save_untouch_nii_gz(NewNii,output);
    % see the confidence of the segmented area for each label
    PB(boxestpadvox(k,1):boxestpadvox(k,2),...
        boxestpadvox(k,3):boxestpadvox(k,4),...
        boxestpadvox(k,5):boxestpadvox(k,6),k)=pb.*est;
end

fprintf('combined\n');
pfms_dir=[lf_dir,'pfms/'];mkdir_p(pfms_dir);
pfms_mat=[pfms_dir,tgtfn,'_pfms.mat'];
save(pfms_mat,'pfms');
PB=cat(4,sum(PB,4)==0,PB);
[~,est]=max(PB,[],4);
est=est-1;
NewNii=RawNii;
NewNii.img=est;
NewNii.hdr.dime.datatype=2;
NewNii.hdr.dime.bitpix=8;
NewNii.hdr.dime.glmax=max(est(:));
NewNii.hdr.dime.glmin=min(est(:));
outfn=[combined_seg_dir,tgtfn,'_organlabel_clsimple.nii.gz'];
save_untouch_nii_gz(NewNii,outfn);
clear PB Prob est Pr

fprintf('run JLF\n');
load(pfms_mat,'pfms');
for k=1:1
    fprintf('L%02d\n',k);
    organ_dir=sprintf('%sL%02d/',lf_dir,k);
    output=[organ_dir,tgtfn,'_singlelabel_clsimplejlf.nii.gz'];
    postmap_filepattern=[organ_dir,tgtfn,'_postmap%04d.nii.gz'];
    target=[rf_organ_roi_dir,tgtfn,sprintf('_%02d',k),'.nii.gz'];
    TargetNii=load_untouch_nii_gz(target);
    pfms_organ=pfms{k};
    ida=find(~isnan(pfms_organ.wt(:,end))==1);
    
    count=0;
    if exist('reglabel_fns','var'),reglabel_fns=cell(0);end
    if exist('regimg_fns','var'),regimg_fns=cell(0);end
    reg_fns=dir(sprintf('%s/L%02d/*-%s_%02d.nii.gz',reglabel_dir,k,tgtfn,k));
    for j=1:length(reg_fns)
        filename=reg_fns(j).name;
        fout=filename(1:strfind(filename,'.nii.gz')-1);
        reglabelfn=[reglabel_dir,sprintf('L%02d/',k),fout,'.nii.gz'];
        regimgfn=[regimg_dir,sprintf('L%02d/',k),fout,'.nii.gz'];
        if (~isempty(dir(reglabelfn))&&(~isempty(dir(regimgfn))))
            count=count+1;
            reglabel_fns{count}=reglabelfn;
            regimg_fns{count}=regimgfn;
        end
    end
    
    if isempty(ida) % no atlas is useful for that organ
        % then the output is empty
        OutputNii=TargetNii;OutputNii.img=zeros(size(TargetNii.img));
        OutputNii.hdr.dime.datatype=2;
        OutputNii.hdr.dime.bitpix=8;
        OutputNii.hdr.dime.glmax=0;OutputNii.hdr.dime.glmin=0;
        save_untouch_nii_gz(OutputNii,output);
        % postmap background is all one
        PostNii=TargetNii;PostNii.img=ones(size(TargetNii.img));
        PostNii.hdr.dime.bitpix=32;PostNii.hdr.dime.datatype=16;
        PostNii.hdr.dime.glmax=1;PostNii.hdr.dime.glmin=0;
        postbgfn=sprintf('%s%s_postmap%04d.nii.gz',organ_dir,tgtfn,0);
        save_untouch_nii_gz(PostNii,postbgfn);
        % postmap foreground is all zero
        PostNii=TargetNii;PostNii.img=zeros(size(TargetNii.img));
        PostNii.hdr.dime.bitpix=32;PostNii.hdr.dime.datatype=16;
        PostNii.hdr.dime.glmax=0;PostNii.hdr.dime.glmin=0;
        postfgfn=sprintf('%s%s_postmap%04d.nii.gz',organ_dir,tgtfn,1);
        save_untouch_nii_gz(PostNii,postfgfn);
    else
        labelfn_organ=reglabel_fns(ida);
        imgfn_organ=regimg_fns(ida);
        atlases=[];
        labels=[];
        % the cropped registered labels are already in binary format
        for i=1:length(labelfn_organ);
            atlases=[atlases,imgfn_organ{i},' '];
            labels=[labels,labelfn_organ{i},' '];
        end
        cmd_jlf=sprintf(['%s %d %d ',...
            '-g %s -tg %s -l %s ',...
            '-rp %d -rs %d -p %s ',...
            '-m Joint[%f,%f] %s\n'],...
            tools.jf_loc,fusion_param.dim,fusion_param.mod,...
            atlases,target,labels,...
            fusion_param.rp,fusion_param.rs,postmap_filepattern,...
            fusion_param.alpha,fusion_param.beta,output);
        system(cmd_jlf);
    end
end

fprintf('combine into one\n');
PB=zeros([s 1]);
for k=1:1
    organ_dir=sprintf('%sL%02d/',lf_dir,k);
    output=[organ_dir,tgtfn,'_singlelabel_clsimplejlf.nii.gz'];
    EstNii=load_untouch_nii_gz(output);
    postmapfgfn=[organ_dir,tgtfn,'_postmap0001.nii.gz'];
    ProbNii=load_untouch_nii_gz(postmapfgfn);
    PB(boxestpadvox(k,1):boxestpadvox(k,2),...
        boxestpadvox(k,3):boxestpadvox(k,4),...
        boxestpadvox(k,5):boxestpadvox(k,6),k)=double(ProbNii.img).*double(EstNii.img);
end
PB=cat(4,sum(PB,4)==0,PB);
[~,est]=max(PB,[],4);
est=est-1;
NewNii=RawNii;
NewNii.img=est;
NewNii.hdr.dime.datatype=2;
NewNii.hdr.dime.bitpix=8;
NewNii.hdr.dime.glmax=max(est(:));
NewNii.hdr.dime.glmin=min(est(:));
outfn=[combined_seg_dir,tgtfn,'_organlabel_clsimplejlf.nii.gz'];
save_untouch_nii_gz(NewNii,outfn);
% save an official copy
outfinalfn=[lf_dir,tgtfn,'.nii.gz'];
system(sprintf('cp %s %s',outfn,outfinalfn));
end