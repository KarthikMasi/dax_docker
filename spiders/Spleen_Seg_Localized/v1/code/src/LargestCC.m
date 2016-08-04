function vol=LargestCC(vol,option)
% vol - binary
% option 1 - close and open to reduce components first ; other numbers, no
% close and open
uv=unique(vol);
if length(uv)>2
    error('should be binary matrix');
end
if length(uv)==1
    vol=vol+0;
elseif length(uv)==2
    if option==1 % cut noise
        if ndims(vol)==2
            se=strel('disk',1);
        elseif ndims(vol)==3
            se=zeros(3,3,3);
            se(:,2,2)=1;se(2,:,2)=1;se(2,2,:)=1;
        else
            error('should be two or three dimensions');
        end
        vol=imclose(vol,se);
        vol=imopen(vol,se);
    end
    [r,n]=bwlabeln(vol);
    s=accumarray(r(r(:)>0),1);
%     s=zeros(n,1);
%     for i=1:n
%         s(i)=sum(r(:)==i);
%     end
    [va,idmax]=max(s);
    vol=(r==idmax);
end