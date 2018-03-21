package com.raremile.prd.inferlytics.cache;

import java.util.ArrayList;
import java.util.List;

import com.raremile.prd.inferlytics.commons.ApplicationConstants;
import com.raremile.prd.inferlytics.database.DAOFactory;
import com.raremile.prd.inferlytics.entity.UsecaseDetails;

public class ApplicationCache {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(ApplicationCache.class);
	
	static List<UsecaseDetails> usecaseDetails;
	
	public static UsecaseDetails getUsecaseDetail(String entity, String subProduct){
		if(usecaseDetails == null){
			usecaseDetails = new ArrayList<>();
		}
		UsecaseDetails ucd = null;
		for (UsecaseDetails usecaseDetail : usecaseDetails) {
			if(usecaseDetail.getEntityName().equalsIgnoreCase(entity)
					&& usecaseDetail.getSubProductName().equalsIgnoreCase(subProduct)){
				ucd = usecaseDetail;
				break;
			}
		}
		if(null == ucd){
			ucd = DAOFactory.getInstance( ApplicationConstants.LEXICONDB_PROPERTIES_FILE )
	        .getLexiconDAO().getDetailsByEntitySubProduct( entity, subProduct );
			usecaseDetails.add(ucd);
		}
		return ucd;
		
	}
}
