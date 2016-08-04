function [est,pb,pfms]=clsimple(obs,Prob,num,niter_keep,init_wt,measure_type)
% implement CLSIMPLE method for atlas selection and label fusion
% input:
%   obs - 4D matrix with a collection of 3D observations
%   Prob - 4D matrix for probability of organ vs non-organ (2 for 4th dim)
%   num - least number of atlases to keep if possible, num+5 - maximum
%   number to keep
%   niter_keep - number of iterations not to discard observations
%   init_wt - the initial weight for voting (the 0th iteration)
%   measure_type - 1: Dice; 2: Jaccard; else: Sensitivity
% output:
%   est - the estimated segmentation after fusion
%   pb - the probability after fusion
%   pfms - a struct to store the performance of the observations all along

% assign default measure type as dsc
if nargin<6,measure_type=1;end
% assign default initial weight as average over all subjects
if nargin<5,init_wt=ones(size(obs,4),1)/size(obs,4);end
% assign the number of iteration to keep all observation
if nargin<4,niter_keep=0;end
if nargin<3,num=5;end
% make sure the observation is provided
if nargin<2,error('at least two inputs required');end
if ndims(obs)~=4,error('obs has to be a 4-D matrix');end
if ndims(Prob)~=4,error('Prob has to be a 4-D matrix');end
if size(Prob,4)~=2,error('the 4th dimension of Prob has to be two');end
sobs=size(obs);sprob=size(Prob);
if ~isequal(sobs(1:3),sprob(1:3))
    error('the first three dimension of obs and Prob should match');
end
if size(obs,4)~=length(init_wt)
    error('length of init_wt should match the 4th dim of obs');
end

% prepare the output performace struct
pfms=struct;
% .iter - the iteration of the algorithm
% .wt - a [size(obs,4)) x (.iter+1)] recording the performace of each
% observation (use NaN for discarded obs) for each iteration, the first
% column is for the 0th iteration
% .num - number of observations left at convergence

iter=0;
pfms.wt=init_wt(:);
alpha=1;
use_obs=ones(size(obs,4),1); % indicates whether the obs is still in use

% est=weighted_vote(obs,init_wt,use_obs);
mvprob=mv_prob(obs,use_obs);
ccest=est_correction(mvprob,Prob);
if sum(ccest(:))==0 % if empty, then no need to go further
    pfms.iter=iter;
    pfms.wt=cat(2,pfms.wt,NaN(size(init_wt(:))));
    pfms.num=0;
    est=zeros(size(ccest));
    pb=zeros(size(ccest));
    return;
end

convergence=false;
while ~convergence
    iter=iter+1;
    fprintf('CLSIMPLE iteration: %d\n',iter);
    fprintf('Number of obs: %d\n',sum(use_obs(:)));
    if iter>100,warning('does not seem to converge, please check!');break;end
    prev_obs=sum(use_obs(:));
    % calculate weights
    wt=estimate_performance(obs,ccest,measure_type,use_obs);
    pfms.wt=cat(2,pfms.wt,wt(:));
    % find threshold
    thresh=nanmean(wt)-alpha*nanstd(wt);
    % at least to discard the ones with no overlap with the current est
    thresh=max(1e-10,thresh);
    % discard observations
    if iter>niter_keep
        use_obs(wt<thresh)=0;
        wt(wt<thresh)=NaN;
    end
    % weighted vote estimate
    mvprob=mv_prob(obs,use_obs);
    ccest=est_correction(mvprob,Prob);
    % check convergence
    curr_obs=sum(use_obs(:));
    if prev_obs==curr_obs
        if curr_obs>num+5
            use_obs(wt==min(wt(:)))=0;
            wt(wt==min(wt(:)))=NaN;
        else
            convergence=true;
        end
    elseif curr_obs<=num
        convergence=true;
    end
end
pfms.iter=iter;
pfms.num=sum(use_obs(:));
% final est, just use mv
est=double(mvprob>=0.5);
pb=mvprob;
end

function mvprob=mv_prob(obs,use_obs)
mvprob=zeros(size(obs,1),size(obs,2),size(obs,3));
for i=1:size(obs,4)
    if use_obs(i)==1
        mvprob=mvprob+double(obs(:,:,:,i));
    end
end
mvprob=mvprob/sum(use_obs(:));
end

function ccest=est_correction(mvprob,Prob)
ccprob=cat(4,mvprob,1-mvprob).*Prob;
ccest=double(ccprob(:,:,:,1)>=ccprob(:,:,:,2));
ccest=FillHoles(LargestCC(ccest,0),1);
end

function wt=estimate_performance(obs,est,measure_type,use_obs)
wt=NaN(size(obs,4),1);
for i=1:size(obs,4)
    if  use_obs(i)==1;
        switch measure_type
            case 1
                wt(i)=calc_dsc(est,obs(:,:,:,i));
            case 2
                wt(i)=calc_jaccard(est,obs(:,:,:,i));
            case 3
                wt(i)=calc_sensitivity(est,obs(:,:,:,i));
        end
    end
end
end

function dsc=calc_dsc(t,e)
t=double(t);e=double(e);
dsc=2*sum(and(t(:),e(:)))/(sum(t(:))+sum(e(:)));
end

function jcd=calc_jaccard(t,e)
t=double(t);e=double(e);
jcd=sum(and(t(:),e(:)))/sum(or(t(:),e(:)));
end

function sen=calc_sensitivity(t,e)
t=double(t);e=double(e);
sen=sum(and(t(:),e(:)))/sum(t(:));
end