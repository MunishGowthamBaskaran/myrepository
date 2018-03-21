<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    

<!DOCTYPE html >
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<!-- Merged JS file -->
<script src="/Inferlytics/pages/js/ecomwidget/InferlyticsWidget.js"></script>

<!-- Merged CSS file -->
<link href="/Inferlytics/pages/css/ecomwidget/mergeStyle.css"	rel="stylesheet" type="text/css" />
<link href="/Inferlytics/pages/css/ecomwidget/testingstyle.css"	rel="stylesheet" type="text/css" />

</head>
<body>
<div>
 			<div class="inf-mb-prd-topImage"></div>																									
			<div class="inf-mb-prd-productImg">                                                                                                    
				<img id="inf-mb-prd-productImg">                                                                                                   
			</div>                                                                                                                      
			<div class="inf-mb-prd-prodnameimg"></div> 
			<div class="inf-mb-prd-rating"></div>                                                                                            
		      <div class="inf-mb-prd-price"><span>Price: </span><b id="inf-mb-prd-price" style="color:#181818;"></b></div>                                                                                                                         
		    <!-- Widget Container Start -->
		    <div class="review-tool-Widget">
		       
		    </div>                                                                                                                          
			<!-- Widget Container End -->
                                                                                                                               
			<div class="inf-mb-prd-proddetails"></div>                                                                                             
			<div class="inf-mb-prd-prodsuggestion"></div>                                                                                          
			<div class="inf-mb-prd-bottomimg"></div>                                                                                               
</div>
 <script type="text/javascript">
 	var prodId = "<c:out value='${param[\"productName\"]}' />";
 	if(window.location.host.indexOf("localhost") != -1)
		address="http://" + window.location.host+ "/Inferlytics";
		else
		address="http://" + window.location.host;

  var widget=   new Inferlytics.InferlyticsEventsWidget({
         //mandatory parameters
         container: "#review-tool-Widget",
         widgetType: "moltonbrown-product-widget",
         partnerBaseUrl: address,
		 brandName:  'moltonbrown', 
		 productName: 'fragranceAW', 
		 productId:prodId  
     });
  
</script>


</body>
</html>