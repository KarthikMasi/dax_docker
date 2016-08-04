function dsc=mydsc(t,e)
dsc=2*sum(and(t(:),e(:)))/(sum(t(:))+sum(e(:)));
