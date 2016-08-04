function vol=FillHoles(vol,option)

% fill holes in binary 3D volumes
% vol - binary volumes
% option 1 - volume-wise 2 - slice-wise

uv=unique(vol);
if length(uv)>2
    error('should be binary matrix');
end
if length(uv)==1
    vol=vol+0;
elseif length(uv)==2
    switch option
        case 1 % volume-wise
            [r,n]=bwlabeln(1-vol);
            %             s=zeros(n,1);
            %             for i=1:n
            %                 s(i)=sum(r(:)==i);
            %             end
            %             for i=1:n
            %                 if i~=idmax
            %                     vol=vol+(r==i);
            %                 end
            %             end
            s=accumarray(r(r(:)>0),1);
            [va,idmax]=max(s);
            vol=vol+double(and(r~=0,r~=idmax));
        case 2 % slice-wise
            for k=1:size(vol,3)
                [r,n]=bwlabeln(1-vol(:,:,k),4);
                if n>1
                    %                     s=zeros(n,1);
                    %                     for i=1:n
                    %                         s(i)=sum(r(:)==i);
                    %                     end
                    %                     [va,idmax]=max(s);
                    %                     for i=1:n
                    %                         if i~=idmax
                    %                             vol(:,:,k)=vol(:,:,k)+(r==i);
                    %                         end
                    %                     end
                    s=accumarray(r(r(:)>0),1);
                    [va,idmax]=max(s);
                    vol(:,:,k)=vol(:,:,k)+double(and(r~=0,r~=idmax));
                end
            end
    end
end