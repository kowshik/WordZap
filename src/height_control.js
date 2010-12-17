function setHeight(pageName)
{
	

	var minHeights = new Array();
	
	minHeights['home']=1040;
	minHeights['version_history']=1850;
	minHeights['installation']=3300;
	minHeights['screenshots']=1250;
	minHeights['source_code']=1040;
	minHeights['authors_contact']=1040;
	minHeights['contribute']=1040;
	minHeights['license']=1040;
	
	if(screen.height < minHeights[pageName])
	{
		document.getElementById('content').style.height=''+minHeights[pageName]+'px';
	}
	else
	{
			document.getElementById('content').style.height=screen.height+'px';
	}
	
}
