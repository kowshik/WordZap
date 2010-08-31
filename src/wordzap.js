function setHeight(pageName)
{
	

	var minHeights = new Array();
	
	minHeights['home']=940;
	minHeights['version_history']=1450;
	minHeights['installation']=3200;
	minHeights['screenshots']=1050;
	minHeights['source_code']=940;
	minHeights['authors_contact']=940;
	minHeights['contribute']=940;
	minHeights['license']=940;
	
	if(screen.height < minHeights[pageName])
	{
		document.getElementById('content').style.height=''+minHeights[pageName]+'px';
	}
	else
	{
			document.getElementById('content').style.height=screen.height+'px';
	}
	
}
