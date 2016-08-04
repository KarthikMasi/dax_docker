function Look_ab170(varargin)
close all
startdir='/fs1/masi/data/ab170nii/';
patient=dir([startdir,'ab*']);

if length(varargin)==2
    ii=varargin{1};
    jj=varargin{2};
    secdir=[startdir,patient(ii).name,'/'];
    scan=dir([secdir,'*_*']);
    for j=jj:length(scan)
        disp(['patient:',num2str(ii),', scan:',num2str(j)]);
        thddir=[secdir,scan(j).name,'/'];
        fn=dir([thddir,'*.nii.gz']);
        disp([patient(ii).name,'_',scan(j).name]);
        % filename adjustment
        ddd=strcat(thddir,fn(1).name);
        id=strfind(ddd,'(');
        id=union(id,strfind(ddd,')'));
        id=union(id,strfind(ddd,''''));
        if ~isempty(id)
            fnc=ddd(1:id(1)-1);
            for k=1:length(id)-1,fnc=strcat(fnc,'\',ddd(id(k):id(k+1)-1));end
            fnc=strcat(fnc,'\',ddd(id(end):end));
        else 
            fnc=ddd;
        end
        % load
        nft=MyLoadNiiGz(fnc);
        voxdim=nft.hdr.dime.pixdim(2:4);
        Tim=nft.img;
        disp(size(Tim));
        disp(voxdim);
        if ndims(Tim)==4
            disp('four dimensions originally, squeeze');
            Tim=squeeze(Tim);
        end
%         if ndims(Tim)==3
%             [nr,nc,nh]=size(Tim);
%             if and(nh>min([nr,nc]),nr~=nc) 
%                 disp('not axial scan');
%                 if nr<nc
%                     disp('permute [3 2 1]');
%                     Tim=permute(Tim,[3 2 1]);
%                 else
%                     disp('permute [1 3 2]');
%                     Tim=permute(Tim,[1 3 2]);
%                 end
%             else
%                 disp('axial');
%             end
%         end        
        AxialViewStep(Tim,1,1);
    end
else
    ii=0;
end

for i=ii+1:length(patient)
    secdir=[startdir,patient(i).name,'/'];
    scan=dir([secdir,'*_*']);
    for j=1:length(scan)
        disp(['patient:',num2str(i),', scan:',num2str(j)]);
        thddir=[secdir,scan(j).name,'/'];
        fn=dir([thddir,'*.nii.gz']);
        disp([patient(i).name,'_',scan(j).name]);
        % filename adjustment
        ddd=strcat(thddir,fn(1).name);
        id=strfind(ddd,'(');
        id=union(id,strfind(ddd,')'));
        id=union(id,strfind(ddd,''''));
        if ~isempty(id)
            fnc=ddd(1:id(1)-1);
            for k=1:length(id)-1,fnc=strcat(fnc,'\',ddd(id(k):id(k+1)-1));end
            fnc=strcat(fnc,'\',ddd(id(end):end));
        else 
            fnc=ddd;
        end
        % load
        nft=MyLoadNiiGz(fnc);     
        voxdim=nft.hdr.dime.pixdim(2:4);
        Tim=nft.img;
        disp(size(Tim));
        disp(voxdim);
        if ndims(Tim)==4
            disp('four dimensions originally, squeeze');
            Tim=squeeze(Tim);
        end
%         if ndims(Tim)==3
%             [nr,nc,nh]=size(Tim);
%             if and(nh>min([nr,nc]),nr~=nc) 
%                 disp('not axial scan');
%                 if nr<nc
%                     disp('permute [3 2 1]');
%                     Tim=permute(Tim,[3 2 1]);
%                 else
%                     disp('permute [1 3 2]');
%                     Tim=permute(Tim,[1 3 2]);
%                 end
%             else
%                 disp('axial');
%             end
%         end   
        AxialViewStep(Tim,1,1);
    end
end