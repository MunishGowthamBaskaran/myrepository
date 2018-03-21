/** *******Widget JS****************** */

if (!Inferlytics) {
    var Inferlytics = {};
}

/**
 * Entry point for widget
 * 
 * @this { Inferlytics.InferlyticsEventsWidget}
 * @param {Object}
 *            parameters - container for all custom information
 */

    Inferlytics.InferlyticsEventsWidget = function(parameters) {
    
    var data=null;
    var queryParams = {};
    var widgetOpts = {};
    var subDimClicked = null;
    var wordClicked = null;
     
    
    this.init = function(parameters) {
        widgetOpts.minJqueryVersion = '1.10.1';
        widgetOpts.container = parameters.container;
        // to remove '#' that comes in the container name
        widgetOpts.containerId = widgetOpts.container.slice(1);
        widgetOpts.containerDom = document.querySelectorAll('.'+widgetOpts.containerId)[0]||document.getElementsByClassName(widgetOpts.containerId)[0];
        
        widgetOpts.widgetType = parameters.widgetType;
        widgetOpts.partnerBaseUrl = parameters.partnerBaseUrl;

                widgetOpts.brandName=parameters.brandName;
            widgetOpts.productName=parameters.productName;
        widgetOpts.productId= parameters.productId;
        widgetOpts.category=parameters.category;
        // internal parameters
        widgetOpts.jquerySrcToUse = 'http://ajax.googleapis.com/ajax/libs/jquery/' + widgetOpts.minJqueryVersion + '/jquery.min.js';
        jQueryLoadHandler(widgetOpts);
    };

    this.init(parameters);


       
    /** ******** */
   /* highlight.js */
   /** ******** */
    var Inf_Hilitor=function(e,t){var n=document.getElementById(e)||document.body;var r=t||"EM";var i=new RegExp("^(?:|SCRIPT|FORM|SPAN)$");var s=["#ff6","#a0ffff","#9f9","#f99","#f6f"];var o=[];var u=0;var a="";var f=false;var l=false;this.setMatchType=function(e){switch(e){case"left":this.openLeft=false;this.openRight=true;break;case"right":this.openLeft=true;this.openRight=false;break;case"open":this.openLeft=this.openRight=true;break;default:this.openLeft=this.openRight=false}};this.setRegex=function(e){e=e.replace(/^[^\w]+|[^\w]+$/g,"").replace(/,/g,"|");var t="("+e+")";if(!this.openLeft)t="\\b"+t;if(!this.openRight)t=t+"\\b";a=new RegExp(t,"i")};this.getRegex=function(){var e=a.toString();e=e.replace(/(^\/(\\b)?|\(|\)|(\\b)?\/i$)/g,"");e=e.replace(/\|/g," ");return e};this.hiliteWords=function(e){if(e==undefined||!e)return;if(!a)return;if(i.test(e.nodeName))return;if(e.hasChildNodes()){for(var t=0;t<e.childNodes.length;t++)this.hiliteWords(e.childNodes[t])}if(e.nodeType==3){var n,r;if((n=e.nodeValue)&&(r=a.exec(n))){e.parentNode.style.backgroundColor="#ff6"}}};this.remove=function(){var e=document.getElementsByTagName(r);while(e.length&&(el=e[0])){var t=el.parentNode;t.replaceChild(el.firstChild,el);t.normalize()}};this.apply=function(e){if(e==undefined||!e)return;this.setRegex(e);this.hiliteWords(n)}}
    
    /* FragBuilder.js */
    var InfFragBuilder=(function(){var e=function(e,t){for(var n in t){e.style[n]=t[n]}};var t=function(e,t){for(var n in t){e.onclick=n}};var n=function(t){var r=document.createDocumentFragment();for(var i in t){if(!("tagName"in t[i])&&"textContent"in t[i]){r.appendChild(document.createTextNode(t[i]["textContent"]))}else if("tagName"in t[i]){var s=document.createElement(t[i].tagName);delete t[i].tagName;for(var o in t[i]){var u=t[i][o];switch(o){case"textContent":s.appendChild(document.createTextNode(u));break;case"style":e(s,u);break;case"click":s.setAttribute("onclick",u);break;case"childNodes":s.appendChild(n(u));break;case"src":s.src=u;break;case"href":s.setAttribute("href",u);s.setAttribute("target","_blank");break;case"id":s.setAttribute("id",u);break;case"subDim":s.setAttribute("subDim",u);break;case"word":s.setAttribute("word",u);break;case"scNo":s.setAttribute("scNo",u);break;default:if(o in s){s[o]=u}break}}r.appendChild(s)}else{throw"Error: Malformed JSON Fragment"}}return r};var r=function(e){var t=document.createElement("div"),n=document.createDocumentFragment();t.innerHTML=e;while(t.hasChildNodes()){n.appendChild(t.firstChild)}return n};return function(e){if(typeof e==="string"){return r(e)}else{return n(e)}}}());
   
    /* This variable contains all the scripts for the feature widget */
    var FeaturesWidget =function (){
            
            /** ******* */
            /* custom_script.js */
            /** ******* */

            // JavaScript Document
            var globalHeight = 300; /*
									 * This is to set the review container
									 * height.Changes when resize is done.
									 */
            var calledProduct = false;
            var featurejsonurl;
            var skipLimit = 6; /*
								 * This keeps track of the number of products to
								 * be shown at once or to load at once.
								 */
            var tagname; 		/* Keeps track of the Word that was clicked. */
            var header = 1; 	/*
             					* 1 indicates the header should be (reviews) else
             					* header should be (products).
             					*/
            var scrollPos = 0;			 /* Keeps track of the scroll position */
            var scrollPosForProducts = 0;

            var limitForProduct = 6; 	/* Keep track of limit globally */
            var skipForProd; 			/* Keep track of the skip Count globally */
            var totalCountForProd; 		/*
									 	* Keep track of the Total Count of products
									 	* globally for the selected keyword.
									 	*/
            var CHROME = (navigator.userAgent.toString().toLowerCase().indexOf("chrome") != -1);
            var jq;
            var isPositive=2;/*3 indicates negative,2 indicates positive.  Keeps track of the type of reviews being read*/
            var categoryForklWines = [];//Keep track of category used in klwines.
            var totalProductsCountwithinCategory=0;//Keeps track of the products count for selected category used only in klwines.
            var totalProductsCountforAllCategory=0;//Keeps the value of the no of products in all the category used only in klwines.
            var  selectedsubDimension="";
            var selectedWord="";
            var selectedCountry="All";
            var selectedsubRegion="All";
            var totalProductsCountforCountry=0;//Keeps track of number of products for each country
            this.onloadMethod=onloadMethod;
            function onloadMethod(jquery)  {
                     
                    jq=jquery;
                                            
                                            var   img1 = new Image(); /* Preload the image.*/
                                                    img1.src = "../../Inferlytics/pages/images/ecomwidget/showmorehover.png";

                                                    jq('.inf-bw-header_section #inf-bw-product_name').hide();
                                                    jq('.inf-bw-header_section #inf-bw-comment_info').hide();
                                                    var slices = getUrlParams();
                                                    var entity = slices[3]; /*   Gives the	Entity Name to display the background image.*/

                                                    if (entity != null) {
                                                            var url = 'url(/Inferlytics/pages/images/ecomwidget/'
                                                                            + entity
                                                                            + ') top center no-repeat';	/*Url of the background image*/
                                                            jq(".inf-bw-page").css('background', url);
                                                    }

                                                    jq(".inf-bw-data_container1").mCustomScrollbar({
                                                            scrollButtons : {
                                                                    enable : true
                                                            },
                                                            advanced : {
                                                                    updateOnContentResize : true
                                                            }
                                                    });
                                                    jq(".inf-bw-review_container").mCustomScrollbar({
                                                            scrollButtons : {
                                                                    enable : true
                                                            },
                                                            advanced : {
                                                                    updateOnContentResize : true
                                                            },
                                                            callbacks : {
                                                                    onScroll : function() {
                                                                            myCustomFn();

                                                                    }
                                                            }
                                                    });
                                                    jq('.inf-bw-header_section').hover(function() {
                                                        jq(document).bind('mousewheel DOMMouseScroll',function(){ 
                                                            stopWheel(); 
                                                        });
                                                    }, function() {
                                                        jq(document).unbind('mousewheel DOMMouseScroll');
                                                    });
                                                    /**
                                                     * Add price html to container here
                                                     */
                                                    if(widgetOpts.maxprice > 0){
                                                    var pricesliderhtml =   '<div class=\"inf-bw-slider-container\"> '+
                                                    '<label class=\"inf-bw-slider-price-lbl\">Price Range: </label>'+
                                                    '<div class=\"inf-bw-slider-text-input-container int-bw-slider-pricemin-container\"><input  id=\"inf-bw-pricemin\" class=\"inf-bw-price-input-text\" value=\"0\"/>'+widgetOpts.currency+'</div>'+
                                                    '<div id=\"inf-bw-slider-range\" class=\"inf-bw-slider\"></div>'+
                                                    '<div class=\"inf-bw-slider-text-input-container int-bw-slider-pricemax-container\"><input  id=\"inf-bw-pricemax\" class=\"inf-bw-price-input-text\" value=\"'+widgetOpts.maxprice+'\" />'+widgetOpts.currency+'</div>'+
                                                    '<a class=\"inf-bw-price-submit\"></a>'+
                                                    '</div>';

                                                    jq('#subcontainer').before(pricesliderhtml);
                                                    jq('.inf-bw-price-submit').click(function(){
                                                   	 widgetOpts.price = jq( "#inf-bw-pricemin" ).val()+"-"+ jq( "#inf-bw-pricemax" ).val();
                                                   	 data=null; // reset global variable
                                                        categoryRelatedProduct(jq, widgetOpts,false);
                                                   	});
                                                    jq('.inf-bw-price-input-text').on('keypress', function (e) {
                                                        if ((e.keyCode < 48) || (e.keyCode > 57)) {
                                                            return false;
                                                        }
                                                    });
                                                    jq('.inf-bw-price-input-text').bind("cut copy paste",function(e) {
                                                        e.preventDefault();
                                                    });
                                                    jq('#inf-bw-pricemin').change(function () {
                                                        var minpricevalue = this.value;
                                                        var maxpricevalue = jq( "#inf-bw-pricemax" ).val();
                                                        jq( "#inf-bw-slider-range" ).slider({ values: [ minpricevalue, maxpricevalue] });
                                                    });
                                                    jq('#inf-bw-pricemax').change(function () {
                                                    	var minpricevalue = jq( "#inf-bw-pricemin" ).val();
                                                        var maxpricevalue = this.value;
                                                        jq( "#inf-bw-slider-range" ).slider({ values: [ minpricevalue ,maxpricevalue] });
                                                    });
                                                    }
                                                    jq('.inf-bw-tabs_panel li').click(function() {
                                                            jq(".inf-bw-data_container1").mCustomScrollbar("update");
                                                    });
                                                    jq('.inf-bw-header_section #inf-bw-span_back').click(
                                                                    function() {
                                                                            jq(this).removeClass('inf-bw-span_back').addClass(
                                                                                            'inf-bw-span_back_hidden');
                                                                            jq('.inf-bw-header_section #inf-bw-product_name').hide();
                                                                            jq('.inf-bw-header_section #inf-bw-comment_info').hide();
                                                                            jq('.inf-bw-review_container').height(globalHeight);

                                                                    });

                                                    jq('.inf-bw-slidepanel').click(function() {
                                                            if (jq('.inf-bw-info_panel').hasClass('inf-bw-panel_open')) {
                                                                    jq('.inf-bw-info_panel').removeClass('inf-bw-panel_open');
                                                                    jq('.inf-bw-info_panel').addClass('inf-bw-panel_close');
                                                                    var dx = jq('.inf-bw-info_panel').width() - 30;
                                                                    jq('.inf-bw-info_panel').animate({
                                                                            right : '-=' + dx
                                                                    }, 500, function() {
                                                                            // Animation
																			// complete.
                                                                    });

                                                            }
                                                    });
                                                    
                                                    
                                                                       
                                                         jq('.inf-bw-showcase_btn').click(function() {
                                                            var current = jq(this);
                                                            jq('.inf-bw-popup_overlay').css({
                                                                    'width' : jq(window).width(),
                                                                    'height' : jq(window).height()
                                                            }).show();
                                                            jq(this).animate({
                                                                    right : '0px'
                                                            }, 500, function() {
                                                                    jq(this).hide();
                                                                    var width = jq('#resizable').outerWidth();
                                                                    if (!jq(this).hasClass('open')) {
                                                                            jq('.inf-bw-outer_wrapper').animate({
                                                                                    right : '-1px'
                                                                            }, 500, function() {
                                                                                    jq('.inf-bw-info_panel').animate({
                                                                                            right : jq('#resizable').width() - 470
                                                                                    }, 500);
                                                                                    jq('.inf-bw-icon').addClass('open');
                                                                            });
                                                                    }
                                                                   
                                                            });
                                                            addFeatures();
                                                            addEventToProductAnalytics();
                                                            if(jq("#inf-bw-slider-range").length){
                                                        	jq( "#inf-bw-slider-range" ).slider({
                                                                range: true,
                                                                min: 0,
                                                                max: widgetOpts.maxprice,
                                                                values: [ 0, widgetOpts.maxprice],
                                                                slide: function( event, ui ) {
                                                                	 //jq( "#inf-bw-slider-amount" ).text( "$" + ui.values[ 0 ] + " - $" + ui.values[ 1 ] );
                                                                    jq("#inf-bw-pricemin" ).val( ui.values[ 0 ]); 
                                                                    jq("#inf-bw-pricemax" ).val( ui.values[ 1 ]);
                                                                }
                                                              });
                                                               /*jq( "#inf-bw-slider-amount" ).text( "$" + jq( "#inf-bw-slider-range" ).slider( "values", 0 ) +
                                                                " - $" + jq( "#inf-bw-slider-range" ).slider( "values", 1 ) );*/
                                                            }
                                                            
                                                    });

                                                    jq('.inf-bw-minimize_panel').click(function() {
                                                            if (jq('.inf-bw-container #inf-bw-info_panel').hasClass('inf-bw-panel_open')) {
                                                                    jq('.inf-bw-container #inf-bw-info_panel').animate({
                                                                            left : 'auto',
                                                                            right : '0px',
                                                                            width : "498px"
                                                                    }, 500, function() {
                                                                    });
                                                            }
                                                            jq('.inf-bw-info_panel').animate({
                                                                    left : 'auto',
                                                                    right : '0px'
                                                            }, 500, function() {
                                                                    if (jq('.inf-bw-info_panel').hasClass('inf-bw-panel_open')) {
                                                                            jq('.inf-bw-info_panel').removeClass('inf-bw-panel_open');
                                                                            jq('.inf-bw-info_panel').addClass('inf-bw-panel_close');
                                                                    }
                                                                    jq('.inf-bw-outer_wrapper').css('left', 'auto');
                                                                    // jq('.outer_wrapper').animate({left:'auto',height:'400px',width:'400px',right:'-1px',top:'150px'},
                                                                    // 500,function(){});
                                                                    var width = jq('#resizable').outerWidth() + 2;
                                                                    jq('.inf-bw-outer_wrapper').animate({
                                                                            right : '-' + width + 'px',
                                                                            left : ' 0px'
                                                                    }, 500, function() {
                                                                            jq('.inf-bw-popup_overlay').hide();
                                                                            jq('.inf-bw-icon').removeClass('open');
                                                                            jq('.inf-bw-showcase_btn').show();
                                                                            jq('.inf-bw-showcase_btn').animate({
                                                                                    right : '110px'
                                                                            }, 500, function() {
                                                                            });
                                                                    });

                                                            });

                                                    });

                                                    jq('.inf-bw-review_container')
                                                                    .on(
                                                                                    "click",
                                                                                    ".inf-bw-review",
                                                                                    function() {
                                                                                            if (jq(this).find('.inf-bw-review_comment')
                                                                                                            .hasClass('expand')) {
                                                                                                    if (!calledProduct) {
                                                                                                            var word = jq('.inf-bw-selected_key li div').find('span')
                                                                                                                            // .find('.inf-bw-keyword_header')
                                                                                                                            // .find('.inf-bw-front
																															// span')
                                                                                                                            .text();
                                                                                                           /*
																											 * if
																											 * (word ==
																											 * "") {
																											 * word =
																											 * jq('div.inf-bw-selected_key')
																											 * .find('.inf-bw-keyword_block_header')
																											 * .find('.inf-bw-front
																											 * span')
																											 * .text(); }
																											 */
                                                                                                            // getClientDetailsForComments(word,
																											// jq(this).attr('id'),
																											// jq(this).attr('subDim'));
                                                                                                            addViewReviewEvent(word, jq(this).attr('id'), jq(this).attr('subDim'));
                                                                                                    } else {
                                                                                                            calledProduct = false;
                                                                                                    }

                                                                                                    var h = jq(this).find(
                                                                                                                    '.inf-bw-review_comment p')
                                                                                                                    .height();

                                                                                                    jq(this).find('.inf-bw-review_comment')
                                                                                                                    .removeClass('expand');
                                                                                                    jq(this)
                                                                                                                    .find('.inf-bw-review_comment')
                                                                                                                    .animate(
                                                                                                                                    {
                                                                                                                                            'height' : h
                                                                                                                                                            + 'px'
                                                                                                                                    },
                                                                                                                                    {
                                                                                                                                            duration : 50,
                                                                                                                                            complete : function() {
                                                                                                                                                    var timerId = null;
                                                                                                                                                    timerId = window
                                                                                                                                                                    .setInterval(
                                                                                                                                                                                    function() {
                                                                                                                                                                                            jq(
                                                                                                                                                                                                            ".inf-bw-review_container")
                                                                                                                                                                                                            .mCustomScrollbar(
                                                                                                                                                                                                                            "update");
                                                                                                                                                                                            window
                                                                                                                                                                                                            .clearInterval(timerId);
                                                                                                                                                                                    },
                                                                                                                                                                                    50);

                                                                                                                                            }
                                                                                                                                    }

                                                                                                                    );

                                                                                            } else {
                                                                                                    jq(this).find('.inf-bw-review_comment')
                                                                                                                    .addClass('expand');

                                                                                                    jq(this)
                                                                                                                    .find('.inf-bw-review_comment')
                                                                                                                    .animate(
                                                                                                                                    {
                                                                                                                                            'height' : '54px'
                                                                                                                                    },
                                                                                                                                    {       duration : 50,
                                                                                                                                            complete : function() {
                                                                                                                                                    var timerId = null;
                                                                                                                                                    timerId = window.setInterval(
                                                                                                                                                                                    function() {
                                                                                                                                                                                            jq(
                                                                                                                                                                                                            ".inf-bw-review_container")
                                                                                                                                                                                                            .mCustomScrollbar(
                                                                                                                                                                                                                            "update");
                                                                                                                                                                                            window
                                                                                                                                                                                                            .clearInterval(timerId);
                                                                                                                                                                                    },50);
                                                                                                                                                                                    
                                                                                                                                            }
                                                                                                                                    });
                                                                                            }
                                                                                    });

                                                    jq("#resizable").resizable({
                                                    		handles : 'nw',
                                                            minWidth : 530,
                                                            minHeight : 400,
                                                            maxWidth : 700,
                                                            containment : "body",
                                                            aspectRatio: false,
                                                            // alsoResize:
															// "#info_panel"
                                                            resize : function(event, ui) {
                                                                    /*Fix For Chrome Browser*/
                                                                    if(CHROME){
                                                                            var top= ui.position.top-window.pageYOffset;
                                                                              jq(".inf-bw-outer_wrapper").css({"top" :top});
                                                                              jq('#resizable').css('left',""); 
                                                                            }
                                                                    var w = jq('#resizable').width();
                                                                    
                                                                    
                                                                    globalHeight=jq('.inf-bw-review_container').height();
                                                                    jq(".inf-bw-tabs ul.inf-bw-tabs_panel").width(w);
                                                            
                                                                    if (jq('.inf-bw-info_panel').hasClass('inf-bw-panel_open')) {
                                                                            jq('.inf-bw-info_panel').css({
                                                                                    'right' : w + 'px'
                                                                            });
                                                                    } else {
                                                                            var dw = w - 498 + 30;
                                                                            jq('.inf-bw-info_panel').css({
                                                                                    'right' : dw + 'px'
                                                                            });
                                                                            
                                                                    }
                                                                    w = jq('.inf-bw-tab_container').width() - 20;
                                                    
                                                            
                                                            
                                                                    jq('.inf-bw-review_container').css({
                                                                            'height' : jq('.inf-bw-info_panel').height() - 120-jq('.inf-bw-header_section #inf-bw-product_name').height() + 'px'
                                                                    });
                                                            
                                                                    jq('.inf-bw-data_container1').css({
                                                                            'height' : jq('.inf-bw-info_panel').height() - 140 + 'px'
                                                                            
                                                                    });
                                                                    jq('.inf-bw-data_container1').css({
                                                                            'width' : w + 'px'
                                                                    });
                                                                    loadfeatureeffect();
                                                                    jq('#feature_sorting').css({
                                                                            'width' : w + 'px'
                                                                    });
                                                                    jq('#feature_sorting .inf-bw-tab_content .tab_content2').css({
                                                                            'width' : w + 'px'
                                                                    });
                                                                    
                                                                    /* jq(".data_container").mCustomScrollbar("update"); */
                                                                    jq(".inf-bw-data_container1").mCustomScrollbar("update");
                                                                    jq(".inf-bw-review_container").mCustomScrollbar("update");

                                                            }
                                                    
                                                    });
                                                    
                                                    
                                                    //This code is only for klwines(for adding categoty) remove it later.
                                                    if(queryParams.subProduct=="winesnew"){
                                                    	var checkBoxdiv='<form class="inf-bw-categoryCheckbox-form" id="inf-bw-category"><div>Country:<select id="winesCountry" class="inf-bw-klwines-categorySelect"><option value="all">All</option></select>SubRegion:<select id="winessubRegion" class="inf-bw-klwines-categorySelect"> <option value="all">All</option></select></div><div class="inf-bw-checkboxText"><input type="checkbox" id="red" value="red" count=""><div class="inf-bw-categoryName">Red</div></div><div class="inf-bw-checkboxText"><input type="checkbox" id="white" value="white" count=""><div class="inf-bw-categoryName">Whites</div></div><div class="inf-bw-checkboxText"><input type="checkbox" id="others" value="others" count=""><div class="inf-bw-categoryName">Others</div></div></form>';
                                                    	jq(".inf-bw-userinfo").html(checkBoxdiv);
                                                    	jq('#inf-bw-category :checkbox').click(function() {
                                                    	    var thisCheckbox = jq(this);
                                                    	    // $this will contain a reference to the checkbox   
                                                    	    if (thisCheckbox.is(':checked')) {
                                                    	    	categoryForklWines.push(thisCheckbox.attr("value"));
                                                    	    	totalProductsCountwithinCategory+=parseInt(thisCheckbox.attr("count"));
                                                    	    	allKeysClick(selectedWord, 0, 0, totalProductsCountwithinCategory, this, null,
                                                                        selectedSubdimension,true);
                                                    	    } else {
                                                    	    	totalProductsCountwithinCategory-=parseInt(thisCheckbox.attr("count"));
                                                    	    	var index = categoryForklWines.indexOf(thisCheckbox.attr("value"));
                                                    	    	if (index > -1) {
                                                    	    			categoryForklWines.splice(index, 1);
                                                    	    	}
                                                    	    	if(totalProductsCountwithinCategory==0){
                                                    	    		allKeysClick(selectedWord, 0, 0,totalProductsCountforAllCategory, this, null,
                                                                            selectedSubdimension,false);
                                                    	    	}else{
                                                    	    		allKeysClick(selectedWord, 0, 0,totalProductsCountwithinCategory, this, null,
                                                                            selectedSubdimension,true);
                                                    	    	}
                                                    	    	
                                                    	    }
                                                    	});
                                                    	
                                                    	jq( "#winesCountry" ).change(function() {
                                                    		getsubRegionsForklWines(jq(this).find("option:selected").text(),"subRegion");
                                                    		  selectedCountry=jq(this).find("option:selected").text();
                                                              selectedsubRegion="All";
                                                              categoryForklWines.length=0;
                                                              getCategoryCountForKlwines(selectedSubdimension,selectedWord,"catCount",true)
                                                             
                                                    		});
                                                    	jq( "#winessubRegion" ).change(function() {
                                                  		selectedsubRegion=jq(this).find("option:selected").text();
                                                  		getCategoryCountForKlwines(selectedSubdimension,selectedWord,"catCount")
                                                  		  categoryForklWines.length=0;
                                                         getCategoryCountForKlwines(selectedSubdimension,selectedWord,"catCount",true);
                                                  		});
                                                    }	
                                                    
                                            }

            function getUrlVars() {
                    var vars = [], hash;
                    var hashes = window.location.href.slice(
                                    window.location.href.indexOf('?') + 1).split('&');
                    for ( var i = 0; i < hashes.length; i++) {
                            hash = hashes[i].split('=');
                            vars.push(hash[0]);
                            vars[hash[0]] = hash[1];
                    }
                    return vars;
            }
            
            
            function getsubRegionsForklWines(country,type){
           	 jq.ajax({
           		 type : "POST",
                    url : queryParams.url+"/GetCategoryCountForKlwines",
                    data : "&synonymword=" + encodeURIComponent(selectedWord) + "&subDimension="+selectedSubdimension+"&country="+country+"&type="+type,
                    dataType : "json",
                    success:function(data) {
                   	 var subRegionSelect="<option value='All'>All</option>";
                   	  jq.each(
                                 data,
                                 function(i, value) {
                               	 subRegionSelect+="<option value='"+value+"'>"+value+"</option>";
                                 });
                   	  jq("#winessubRegion").html(subRegionSelect);
                    }
                    });
           }
            
            function getCountries(subDimension,word,type){
            	 jq.ajax({
            		 type : "POST",
                     url : queryParams.url+"/GetCategoryCountForKlwines",
                     data : "&synonymword=" + encodeURIComponent(word) + "&subDimension="+subDimension+"&type="+type,
                     dataType : "json",
                     success:function(data) {
                    	 
                    	 var countrySelect="<option value='All'>All</option>";
                    	  jq.each(
                                  data,
                                  function(i, value) {
                                	 countrySelect+="<option value='"+value+"'>"+value+"</option>";
                                  });
                    	  jq("#winesCountry").html(countrySelect);
                    	  jq("#winessubRegion").html("<option value=all>All</option>");
                     }
                     });
            }
            
            
            this.getCategoryCountForKlwines=getCategoryCountForKlwines;
            function getCategoryCountForKlwines(subDimension,word,type,iscallProducts){
            	
            	jq(".inf-bw-checkboxText :checkbox").prop('checked', false);
            	totalProductsCountwithinCategory=0;
            	 jq.ajax({
            		 type : "POST",
                     url : queryParams.url+"/GetCategoryCountForKlwines",
                     data : "&synonymword=" + encodeURIComponent(word) + "&subDimension="+subDimension+"&type="+type+"&selectedCountry="+selectedCountry+"&selectedSubRegion="+selectedsubRegion,
                     dataType : "json",
                     async:"false",
                     success:function(data) {
                    	 	jq("#red").attr("count",data.redWinesCount);
                    	 	jq("#red").next().text("Red ("+data.redWinesCount +")");
                    	 	jq("#white").attr("count",data.whiteWinesCount);
                    	 	jq("#white").next().text("White ("+data.whiteWinesCount +")");
                    	 	jq("#others").attr("count",data.otherWinesCount);
                    	 	jq("#others").next().text("Others ("+data.otherWinesCount+")");
                    	 	totalProductsCountforCountry=data.redWinesCount+data.otherWinesCount+data.whiteWinesCount;
                    	 	if(iscallProducts){
                    	 		allKeysClick(selectedWord, 0, 0,totalProductsCountforCountry, this, null,
                                        selectedSubdimension,true);
                    	 	}
                     }
                     });
            }	
            
            this.allKeysClick=allKeysClick;
            function allKeysClick(words, reviewType, skip, totalCount, element, productId,
                            subDimension,isCategorySelected) {
            	if(words !="null"){
            		wordClicked = words;
            		selectedWord=words;
            	}else{
            		selectedWord="null";
            	}
                     // var slices = getUrlParams();
                    var entity = queryParams.entity;// slices[3]; // Gives the
													// Entity
                                                                                        // Name
                    var subproduct = queryParams.subProduct;// slices[4]; //
															// Gives the
                                                                                                    // SubProduct
																									// name
                    var showcomments = null;
                    if (showcomments == null) {
                            jq('.inf-bw-header_section #inf-bw-span_back').removeClass('inf-bw-span_back').addClass('inf-bw-span_back_hidden');
                            jq('.inf-bw-header_section #inf-bw-product_name').hide();
                            jq('.inf-bw-header_section #inf-bw-comment_info').hide();
                            jq('.inf-bw-review_container').height(globalHeight);
                            header = 2;

                    }
                    if (subDimension != null) {
                            header = 1;

                    }
                    if (productId != null) {

                            header = 1;
                            tagname = words;
                            if (words == "null")
                                    tagname = subDimension;
                            scrollPosForProducts = scrollPos;
                            getFeedsByIdForProducts(entity, subproduct, words, productId,
                                            totalCount, skip, subDimension, reviewType);
                            jq(".inf-bw-positive_review").show();
                            jq(".inf-bw-negative_review").show();
                    }

                    else {
                            tagname = words;
                            if (words == "null")
                                    tagname = subDimension;
                            header = 2;
                            if ((limitForProduct <= skipLimit))
                                    skipForProd = skip;
                            totalCountForProd = totalCount;
                            selectedSubdimension=subDimension;
                            getProducts(entity, subproduct, words, reviewType, skip,
                                            totalCount, showcomments, subDimension,isCategorySelected);
                    }
                    jq.each(jq('.inf-bw-review_container .inf-bw-review'), function(index, item) {
                            var wordToHighlight = jq(this).attr('word');
                            var sentenceToHighlight = jq(this).attr('scNo');
                            if(sentenceToHighlight !="-1"){
                            	jq(this).find('em:eq('+sentenceToHighlight+')').css('backgroundColor','#ff6');
                            }
                            else{
                            var myHilitor = new Inf_Hilitor(jq(this).attr('id'));
                            myHilitor.setMatchType("left");
                            myHilitor.apply(wordToHighlight);
                            }
                    });
                    /*
					 * if (words != null) { var n = words.split(","); var
					 * searchString = ""; var counter = 0; for ( var tot =
					 * n.length; counter < tot; counter++) { if ((tot - counter) !=
					 * 2) { var word = n[counter]; word = word.replace("[", "");
					 * 
					 * searchString = searchString + word + ","; } else {
					 * searchString = searchString + n[counter].replace("]",
					 * ""); } } //
					 * jq('.review_container').easymark('removeHighlight'); //
					 * jq('.review_container').easymark('highlight',
					 * searchString);
					 * 
					 * var myHilitor = new Hilitor("review_container");
					 * myHilitor.setMatchType("left");
					 * myHilitor.apply(searchString); }
					 */
                    if (skip == 0) {
                            var mode = element.className;
                            var dx = jq('.inf-bw-info_panel').width() - 30;
                            jq('.inf-bw-keys').removeClass('inf-bw-selected_key');
                            jq('.inf-bw-keys_block').removeClass('inf-bw-selected_key');
                    }
                            if (subDimension != null)
                                    jq(element).parent().addClass('inf-bw-selected_key');
                            else
                                    jq(element).parent().parent().addClass('inf-bw-selected_key');
                    if (header == 1 && skip == 0) {
                            jq('.inf-bw-header_section h3').empty().html(
                                            'Showing all user reviews associated with \'' + tagname + '\'');
                            jq(".inf-bw-userinfo").hide();
                            jq(".inf-bw-headerIcon").show();
                    } else if (header == 2 && skip == 0) {
                            jq('.inf-bw-header_section h3').empty().html(
                                            'Showing all products associated with \'' + tagname + '\'');
                            jq(".inf-bw-userinfo").show();
                            jq(".inf-bw-headerIcon").hide();
                    }
                    if (jq('.inf-bw-info_panel').hasClass('inf-bw-panel_close')) {
                            jq('.inf-bw-info_panel').addClass('inf-bw-panel_open');
                            jq('.inf-bw-info_panel').removeClass('inf-bw-panel_close');
                            jq('.inf-bw-info_panel').animate({
                                    right : '+=' + dx
                            }, 500, function() {
                                    // Animation complete.
                            });
                    }
                    jq.each(jq('.inf-bw-review_container .inf-bw-review'), function(index, item) {

                            jq(this).animate({
                                    height : '50px',
                                    opacity : '1'
                            }, 200, function() {
                                    jq(this).removeClass('hidden');
                                    jq(this).css({
                                            height : 'auto'
                                    });
                                    jq(".inf-bw-review_container").mCustomScrollbar("update");
                            });

                    });
                    jq.each(jq('.inf-bw-review_container .inf-bw-review_p'), function(index, item) {

                            jq(this).animate({
                                    height : '50px',
                                    opacity : '1'
                            }, 200, function() {
                                    jq(this).removeClass('hidden');
                                    jq(this).css({
                                            height : 'auto'
                                    });
                                    jq(".inf-bw-review_container").mCustomScrollbar("update");
                            });
                    });
            }

            function getFeedsByIdForProducts(entity, subproduct, word, productId, count,
                            skip, subDimension, reviewType) {
                    jq.ajax({               type : "POST",
                                            url : queryParams.url+"/getPostsFromMongo",
                                            data : "entity=" + entity + "&subproduct=" + subproduct
                                                            + "&word=" + encodeURIComponent(word) + "&reviewType="
                                                            + reviewType + "&skip=" + skip + "&limit=5"
                                                            + "&productId=" + productId + "&subDimension="
                                                            + encodeURIComponent(subDimension)+"&price="
                                                            +encodeURIComponent(widgetOpts.price),
                                            dataType : "json",
                                            success : function(data) {
                                                    jq(".inf-bw-review_container .mCSB_container #inf-bw-next").remove();
                                                    var dfs = InfFragBuilder(data);
                                                    skip = skip + 5;
                                                    if (skip == 5)
                                                            jq('.inf-bw-review_container .mCSB_container').empty();
                                                    jq('.inf-bw-review_container .mCSB_container').append(dfs);
                                                    if (skip < count)
                                                            jq('.inf-bw-review_container .mCSB_container')
                                                                            .append("<div id='inf-bw-next'><center><a><div class='blueBtn'  onClick=\"basicWidget.featuresWidget.showCommentsForProd('"
                                                                                                            + productId
                                                                                                            + "\','"
                                                                                                            + word
                                                                                                            + "\',"
                                                                                                            + skip
                                                                                                            + ","
                                                                                                            + reviewType
                                                                                                            + ","
                                                                                                            + count
                                                                                                            + ","
                                                                                                            + null
                                                                                                            + ","
                                                                                                            + "\'"
                                                                                                            + subDimension
                                                                                                            + "\'"
                                                                                                            + ")\"></div></a></center></div>");
                                            },
                                            async : false,
                                            complete : function() {
                                                    if (skip == 5) {
                                                            jq(".inf-bw-review_container").mCustomScrollbar("destroy");
                                                            jq(".inf-bw-review_container").mCustomScrollbar({
                                                                    scrollButtons : {
                                                                            enable : true
                                                                    },
                                                                    advanced : {
                                                                            updateOnContentResize : true,
                                                                            scrollInertia : 10
                                                                    },
                                                                    callbacks : {
                                                                    	whileScrolling : function() {
                                                                                    myCustomFn();
                                                                            }
                                                                    }
                                                            });
                                                    }
                                            },
                                            error : function(e18188) {
                                                    ('Error' + e);
                                            }

                                    });
            }
            
            /*This function makes an ajax call to fetch the products and displays it.*/
            this.getProducts=getProducts;
            function getProducts(entity, subproduct, word, reviewType, skip, totalCount,
                            showcomments, subDimension,isCategorySelected) {
            	var temp;
                    var category = parameters.category;
                  
                    //used only for klwines
                      if(!isCategorySelected && skip==0 && subproduct=="winesnew"){
                    	  selectedCountry="All";
                          selectedsubRegion="All";
                    	totalProductsCountforAllCategory=totalCount;
                    	getCountries(subDimension,word,"country");
                    	getCategoryCountForKlwines(subDimension,word,"catCount");
                    	categoryForklWines.splice(0,categoryForklWines.length);
                    }
                      
                    if (totalCount > 0) {
                            jq
                                            .ajax({
                                                    type : "POST",
                                                    url : queryParams.url+"/getPostsFromMongo",
                                                    data : "entity=" + entity + "&subproduct=" + subproduct
                                                                    + "&word=" + encodeURIComponent(word)
                                                                    + "&showcomments=" + showcomments + "&reviewType="
                                                                    + reviewType + "&skip=" + skip + "&limit="
                                                                    + limitForProduct + "&subDimension="
                                                                    + encodeURIComponent(subDimension) + "&category="
                                                                    + category+"&price="+encodeURIComponent(widgetOpts.price)
                                                                    +"&klwinesCategory="+categoryForklWines.toString()+"&selectedCountry="+selectedCountry+"&selectedSubRegion="+selectedsubRegion,
                                                    dataType : "json",
                                                    beforeSend : function() {
                                                    	//jq('.inf-bw-review_container .mCSB_container').html('<div class="inf-bw-loaders">').fadeIn();
                                                    },
                                                    success : function(data) {
                                                            var dfs = InfFragBuilder(data);
                                                            jq('.inf-bw-review_container .mCSB_container #inf-bw-next').remove();
                                                            if (skip == 0) {
                                                            	
                                                            	 jq('.inf-bw-review_container .mCSB_container').fadeOut(400, function(){
                                                            		 jq('.inf-bw-review_container .mCSB_container').empty().html(dfs).fadeIn();
                                                            		 skip = skip + skipLimit;
                                                                     temp = skip;
                                                                     if (limitForProduct > skipLimit) {
                                                                             skip = limitForProduct;
                                                                             limitForProduct = skipLimit;
                                                                     }

                                                                     if (skip <totalCount && subDimension != null) {
                                                                             jq('.inf-bw-review_container .mCSB_container')
                                                                                             .append(
                                                                                                             '<div id="inf-bw-next"><center><a><div class="blueBtn"  onClick=\'basicWidget.featuresWidget.allKeysClick("'
                                                                                                                             + word.replace("'","&apos;")
                                                                                                                             + '",'
                                                                                                                             + reviewType
                                                                                                                             + ','
                                                                                                                             + skip
                                                                                                                             + ','
                                                                                                                             + totalCount
                                                                                                                             + ',this,null,"'
                                                                                                                             + subDimension.replace("'","&apos;")
                                                                                                                             + '")\'/></div></a></center></div>');
                                                                     }
                                                                     if (temp == skipLimit) {
                                                                         setTimeout(function() {
                                                                             jq(".inf-bw-review_container")
                                                                                             .mCustomScrollbar("scrollTo",
                                                                                                             -(scrollPosForProducts));
                                                                             scrollPosForProducts = 0;
                                                                     }, 300);
                                                             }           
                                                            	});
                                                                   
                                                            } else {
                                                                    jq('.inf-bw-review_container .mCSB_container').append(dfs);
                                                                    skip = skip + skipLimit;
                                                                    temp = skip;
                                                                    if (limitForProduct > skipLimit) {
                                                                            skip = limitForProduct;
                                                                            limitForProduct = skipLimit;
                                                                    }

                                                                    if (skip <totalCount && subDimension != null) {
                                                                            jq('.inf-bw-review_container .mCSB_container')
                                                                                            .append(
                                                                                                            '<div id="inf-bw-next"><center><a><div class="blueBtn"  onClick=\'basicWidget.featuresWidget.allKeysClick("'
                                                                                                                            + word.replace("'","&apos;")
                                                                                                                            + '",'
                                                                                                                            + reviewType
                                                                                                                            + ','
                                                                                                                            + skip
                                                                                                                            + ','
                                                                                                                            + totalCount
                                                                                                                            + ',this,null,"'
                                                                                                                            + subDimension.replace("'","&apos;")
                                                                                                                            + '")\'/></div></a></center></div>');
                                                                    }
                                                            }
                                                           
                                                    },
                                                    async : false,
                                                    complete : function() { 
                                                    	
                                                    },
                                                    error : function(e18188) {
                                                            alert('Error' + e);
                                                    }
                                            });
                          
                          
                    } else {
                            jq('.inf-bw-review_container .mCSB_container')
                                            .html(
                                                            "<h3>No comments found in this category, please choose another comment type or product.</h3>");
                    }
            }

            function getUrlParams() {
                    var url = window.location.href;
                    url = url.replace("/Inferlytics/", "/");
                    return url.split("/");
            }

            
            /*This method is called when you click on a outer tile and displays the subcategories or inner tiles.*/
            this.getsubDimension=getsubDimension;
            function getsubDimension(featureName, TotalCount, element) {
            		subDimClicked = featureName ;
            		var totalCountlocal = TotalCount;
                    jq("#subcontainer").empty();
                    jq(".inf-bw-back").show();
                    jq('.inf-bw-review_container .mCSB_container').html('<div class="inf-bw-loaders">');
                    jq.each(
                                                    data,
                                                    function(i, value) {
                                                            if (value.name == featureName) {                                                            	
                                                            	totalCountlocal = value.size;
                                                            	var container="";
                                                                    jq
                                                                                    .each(
                                                                                                    value.children,
                                                                                                    function(k, subDimension) {
                                                                                                    	var showName;
                                                                                                    	if(queryParams.subProduct=="store")
                                                                                                    		showName="Mentions";
                                                                                                    		else
                                                                                                    			showName="Products";
                                                                                                    	
                                                                                                           container+="<div class='inf-bw-keys_block animated rotateIn all_block catall positive_block cata negative_block catb'>"
                                                                                                                                                            + "<ul onclick='basicWidget.featuresWidget.allKeysClick(\""
                                                                                                                                                            + subDimension.name.replace("'","&#39;")
                                                                                                                                                            + "\",0,0,"
                                                                                                                                                            + subDimension.size
                                                                                                                                                            + ",this,null,\""
                                                                                                                                                            + featureName.replace("'","&#39;")
                                                                                                                                                            + "\")'><li class='inf-bw-keyword_block_header'>"
                                                                                                                                                            + "<div class='inf-bw-front'><span>"
                                                                                                                                                            + subDimension.name
                                                                                                                                                            + "</span>"
                                                                                                                                                            + "<label class='lbl all_block'>"
                                                                                                                                                            + subDimension.size
                                                                                                                                                            + " "+showName
                                                                                                                                                            + "</label>"
                                                                                                                                                            
                                                                                                                                                            + "</div>"
                                                                                                                                            
                                                                                                                                                    + "</li></ul></div>";
                                                                                                    });
                                                                    jq("#subcontainer").append(container);
                                                                    
                                                                    loadfeatureeffect();
                                                            }
                                                    });
                    
                    allKeysClick('null', 0, 0, totalCountlocal, element, null, featureName);
                    

            }
this.backbuttonclick = backbuttonclick;
function backbuttonclick(){
	subDimClicked = null;
    wordClicked = null;
	addFeatures();
}

            this.addFeatures=addFeatures;
            function addFeatures() {
                    jq("#subcontainer").empty();
                    jq(".inf-bw-back").hide();

                    jq
                                    .each(data,
                                                    function(i, value) {
                                                            {
                                                                    var subCategories = "";

                                                                    jq.each(value.children, function(k, subCategory) {
                                                                            if (k < 2)
                                                                                    subCategories = subCategories
                                                                                                    + capitalise(subCategory.name)
                                                                                                    + ", ";
                                                                            else if (k == 3) {
                                                                                    subCategories = subCategories + "etc.";
                                                                            }
                                                                    });
                                                                    if(queryParams.subProduct=="store")
                                                                		showName="Mentions";
                                                                		else
                                                                			showName="Products";
                                                                    jq("#subcontainer")
                                                                                    .append(
                                                                                                    "<div class='inf-bw-keys_block animated rotateIn all_block catall positive_block cata negative_block catb'><ul onClick='basicWidget.featuresWidget.getsubDimension(\""
                                                                                                                    + value.name.replace("'","&apos;")
                                                                                                                    + "\","
                                                                                                                    + value.size
                                                                                                                    + ",this)'><li class='inf-bw-keyword_block_header'>        <div class='inf-bw-front'><span>"
                                                                                                                    + value.name
                                                                                                                    + "</span>"
                                                                                                                    + "<div class='inf-bw-subCategory'>"
                                                                                                                    + subCategories
                                                                                                                    + "</div>"
                                                                                                                    + "<label class='lbl all_block'>"
                                                                                                                    + value.size
                                                                                                                    + " "+showName
                                                                                                                    + "</label>"
                                                                                                                    +"</div></li></ul></div>");
                                                            }
                                                    });
                    loadfeatureeffect();
            }

            function loadfeatureeffect() {

                    // setup array of colors and a variable to store the current
                    // index

                    var colors = [ "inf-bw-metro-color"];
                    var classes = [ "cell2x2", "cell1x2", "cell2x1", "cell1x2", "cell1x1" ];
                    var widtharray = [ 140, 70, 140, 70, 70 ];
             var                    currColor = 0;
                    var currCell = 0;

                    jq('.inf-bw-keys_block').each(function(index, element) {

var                             w = jq(this).children().width();

                            /*
							 * w = jq(this).children().width(); h =
							 * jq(this).children().height();
							 * jq(this).css({'width':w+'px'});
							 * jq(this).css({'height':h+'px'});
							 * 
							 * jq(this).children().css({'width':w+'p_x'});
							 */
                            var currentclass = classes[currCell];
                            if (w > widtharray[currCell]) {
                                    currentclass = 'cell2x1';
                            }
                            jq(this).addClass(currentclass);// .addClass(colors[currColor]);
                            jq(this).children().addClass(currentclass).addClass(colors[0]);
                            // increment the current index
                            currColor++;
                            currCell++;
                            // if the next index is greater than then number of
                            // colors then reset to zero
                            if (currColor == colors.length) {
                                    currColor = 0;
                            }
                            if (currCell >= classes.length) {
                                    currCell = 4;
                            }
                            /*
							 * jq(this).children().css({ 'width' : w + 'px' });
							 */
                    });
                    
                    portfoliocall();
                 
                    setTimeout(function() {
                    	
                    jq(".inf-bw-data_container1").mCustomScrollbar("scrollTo", "top");
                    },500);
            }
            function portfoliocall() {

                    // alert();
                    // <!-- PORTFOLIO -->
                    jq('#subcontainer').portfolio({
                            // <!-- GRID SETTINGS -->
                            gridOffset : 42, // <!-- Manual Right Padding
												// Offset for
                            // 100% Width -->
                            cellWidth : 70, // <!-- The Width of one CELL in
											// PX-->
                            cellHeight : 70, // <!-- The Height of one CELL
												// in PX-->
                            cellPadding : 8, // <!-- Spaces Between the CELLS
												// -->
                            entryProPage : 50, // <!-- The Max. Amount of the
                            // Entries per Page, Rest made by
                            // Pagination -->

                            // <!-- CAPTION SETTING -->
                            captionOpacity : 85,

                            // <!-- FILTERING -->
                            filterList : "#feature_sorting" // <!-- Which
																// Filter is
																// used
                                                                                                        // for
																										// the
                    // Filtering / Pagination -->
                    // <!-- title:"#selected-filter-title", Which Div should be
					// used for
                    // showing the Selected Title of the Filter -->

                    });
                    jq(".inf-bw-data_container1").mCustomScrollbar("scrollTo", "top");
                    // Add Time out because the scrollbar needs to be
							// loaded oly
                                        // after
                    // the features are loaded.Else the scroll bar poiition will
					// not be
                    // accurate.

            }

            function loadkeywordeffect() {

                    // <!-- PORTFOLIO -->
                    jq('#keywordsubcontainer').portfolio({
                            // <!-- GRID SETTINGS -->
                            gridOffset : 30, // <!-- Manual Right Padding
												// Offset for
                            // 100% Width -->
                            cellWidth : 120, // <!-- The Width of one CELL in
												// PX-->
                            cellHeight : 30, // <!-- The Height of one CELL
												// in PX-->
                            cellPadding : 10, // <!-- Spaces Between the CELLS
												// -->
                            entryProPage : 50, // <!-- The Max. Amount of the
                            // Entries per Page, Rest made by
                            // Pagination -->

                            // <!-- CAPTION SETTING -->
                            captionOpacity : 85,

                            // <!-- FILTERING -->
                            filterList : "#keyword_sorting_div" // <!--
																	// Which
																	// Filter is
                                                                                                                // used
																												// for
                    // the Filtering / Pagination
                    // -->
                    // <!-- title:"#selected-filter-title", Which Div should be
					// used for
                    // showing the Selected Title of the Filter -->

                    });
            }

            function getUrlParameter(param) {
                    var pageUrl = window.location.search.substring(1);
                    var urlVars = pageUrl.split("&");
                    for ( var i = 0; i < urlVars.length; i++) {
                            var paramName = urlVars[i].split("=");
                            if (paramName[0] == param) {
                                    return paramName[1];
                            }
                    }
            }

            function setNullForProductId() {
                    jq('.inf-bw-header_section #inf-bw-product_name').hide();
                    jq('.inf-bw-header_section #inf-bw-comment_info').hide();
                    jq('.inf-bw-review_container').height(globalHeight);
                    header = 2;
            }

            this.showCommentsForProd=showCommentsForProd;
            function showCommentsForProd(productId, word, skip, reviewType, count,
                            productName, subDimension, element) {
            	isPositive=reviewType;
                    if (reviewType == 2 && element != null ) {
                            jq(".inf-bw-review_type #inf-bw-positiveCountno").text(jq(element).text());
                            jq(".inf-bw-review_type #inf-bw-negativeCountno").text(jq(element).next().text());
                            jq(".inf-bw-positive_review").attr("onclick", jq(element).attr("onclick"));
                            jq(".inf-bw-negative_review")
                                            .attr("onclick", jq(element).next().attr("onclick"));
                            if (jq(element).next().attr("onclick") == null) {
                                    jq(".inf-bw-negative_review").attr("onclick", "");
                            }

                            jq(".inf-bw-review_type #posarrow").show();
                            jq(".inf-bw-review_type #negarrow").hide();
                    } else if (reviewType == 3 && element != null) {
                            jq(".inf-bw-review_type #inf-bw-positiveCountno").text(jq(element).prev().text());
                            jq(".inf-bw-review_type #inf-bw-negativeCountno").text(jq(element).text());
                            jq(".inf-bw-positive_review")
                                            .attr("onclick", jq(element).prev().attr("onclick"));
                            jq(".inf-bw-negative_review").attr("onclick", jq(element).attr("onclick"));
                            if (jq(element).prev().attr("onclick") == null) {
                                    jq(".inf-bw-positive_review").attr("onclick", "");
                            }
                            jq(".inf-bw-review_type #posarrow").hide();
                            jq(".inf-bw-review_type #negarrow").show();
                    }
                    
                    if(parameters.brandName==="klwines" ||parameters.productName=="tescowinesnew"){
                    	jq(".inf-bw-review_type").hide();
                    }
                    
                    if (subDimension == "null") {

                            var goBackClick = "basicWidget.featuresWidget.allKeysClick(\"" + word + "\",1,0,"
                                            + totalCountForProd + ",this,null,'null')";
                            limitForProduct = skipForProd + skipLimit;
                            jq(".inf-bw-header_section #inf-bw-span_back").attr("onclick", goBackClick);
                            allKeysClick(word, reviewType, skip, count, jq('div.inf-bw-selected_key').find(
                                            '.inf-bw-keyword_header'), productId, subDimension);
                    } else {
                            var goBackClick = "basicWidget.featuresWidget.allKeysClick(\"" + word + "\",1,0,"
                                            + totalCountForProd + ",this,null,\"" + subDimension + "\")";
                            limitForProduct = skipForProd + skipLimit;
                            jq(".inf-bw-header_section #inf-bw-span_back").attr("onclick", goBackClick);
                            allKeysClick(word, reviewType, skip, count, jq('div.inf-bw-selected_key').find(
                                            '.inf-bw-keyword_block_header'), productId, subDimension);
                    }

                    jq('.inf-bw-header_section #inf-bw-span_back').removeClass('.inf-bw-header_section inf-bw-span_back_hidden').addClass('inf-bw-span_back');
                    if (productName != null) {
                            jq('.inf-bw-header_section #inf-bw-product_name').html(productName);
                            jq('.inf-bw-header_section #inf-bw-product_name').attr('title', productName);
                    }
                    jq('.inf-bw-header_section #inf-bw-product_name').show();
                    jq('.inf-bw-header_section #inf-bw-comment_info').show();
                    jq('.inf-bw-review_container').height(
                                    globalHeight - jq('.inf-bw-header_section #inf-bw-product_name').height() - 10);
            }
          
            function reviewCount(postId, isPositive) {
                    jq.ajax({
                            type : "POST",
                            url : queryParams.url+"/ReviewCount",
                            data : "postId=" + postId + "&isPositive=" + isPositive,
                            cache : false,
                            crossDomain : true,
                            success : function(data) {
                            }
                    });
            }
            
          function addEventToProductAnalytics() {
        	  
                   jq.ajax({
                        type : "POST",
                        url : queryParams.url+"/DoCaptureAnalytics",
                        data : "eventType=mainWidget" +"&callFrom=widget1"+"&entity="+parameters.brandName+"&subProduct="+parameters.productName,
                        cache : false,
                        crossDomain : true,
                        success : function(data) {
                        	
                        }
                });
          }
          
             
           
          		// register read reviews event in Analytics table
	          function addViewReviewEvent(word, postId, subdimension) {
	        	  // alert("5");
              var    datatosend = "eventType=readReview&word=" + word + "&postId=" + postId + "&SubDim="
                                      + subdimension +"&callFrom=widget1"+"&entity="+parameters.brandName+"&subProduct="+parameters.productName+"&reviewType="+isPositive;
              jq.ajax({
                      type : "POST",
                      url : queryParams.url+"/DoCaptureAnalytics",
                      data : datatosend,
                      cache : false,
                      crossDomain : true,
                      success : function(data) {

                      }
              });
         }

	     this.addProductDetailEvent=addProductDetailEvent;
	     function addProductDetailEvent(word, productId, subdimension) {
	    	// alert("6");
                  jq.ajax({
                          type : "POST",
                          url : queryParams.url+"/DoCaptureAnalytics",
                          data : "eventType=productDetail&word=" + word + "&productId=" + productId + "&subdimension="
                                          + subdimension  +"&callFrom=widget1"+"&entity="+parameters.brandName+"&subProduct="+parameters.productName,
                          cache : false,
                          crossDomain : true,
                          success : function(data) {
                        	        }
                  });
          }
          
            function myCustomFn() {
                    if (header == 2){
                            scrollPos = mcs.top;
                        }
            }
         
            function capitalise(string) {
                    return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
            }

            function stopWheel(e){
                if(!e){ /* IE7, IE8, Chrome, Safari */ 
                    e = window.event; 
                }
                if(e.preventDefault) { /* Chrome, Safari, Firefox */ 
                    e.preventDefault(); 
                } 
                e.returnValue = false; /* IE7, IE8 */
            }
            
    };
    
    
    /*
	 * This variable contains all the script for the product page to show the
	 * keywords.
	 */
    
    var ProductWidget= function(){
            
            /** ******* */
            /* productspage.js */
            /** ***** */
            var overlayPosition = 140; /*
										 * This variable stores the margin-left
										 * value for overlay This is multiplied
										 * with the position odf the keywords.
										 */
            var jq;
            var limit = 5; // Keeps the count of comments to be fetched.
            
            this.onProductLoad=onProductLoad;
            function onProductLoad(jquery) {
                    
                    jq=jquery;

                    jq(".inf-pw-review_container_product").mCustomScrollbar({
                            mouseWheel : true,
                            mouseWheelPixels : "auto",
                            scrollButtons : {
                                    enable : true
                            }
                    });

                    jq(".inf-pw-closeOverlay").click(function() {
                            jq("#inf-pw-posoverlay").hide();
                            jq("#inf-pw-negoverlay").hide();

                    });

                    jq('.inf-pw-review_container_product').on("click", ".inf-pw-review", function() {
                    	
                    	
                    	// add post click event for Analytics
                    	addpostReadClickEvent(this.id ,jq(this).attr("word"));
                    	
                            if (jq(this).find('.inf-pw-review_comment').hasClass('expand')) {

                                    var h = jq(this).find('.inf-pw-review_comment p').height();

                                    jq(this).find('.inf-pw-review_comment').removeClass('expand');
                                    jq(this).find('.inf-pw-review_comment').animate({
                                            'height' : h + 'px'
                                    }, {
                                            duration : 50,
                                            complete : function() {

                                            }
                                    }

                                    );

                            } else {
                                    jq(this).find('.inf-pw-review_comment').addClass('expand');

                                    jq(this).find('.inf-pw-review_comment').animate({
                                            'height' : '65px'
                                    }, {
                                            duration : 50,
                                            complete : function() {

                                            }
                                    });

                            }
                            /*
							 * jq(".review_container_product").mCustomScrollbar(
							 * "update");
							 */
                    });

            }

            this.getProductDetail=getProductDetail;
            function getProductDetail() {
                
                    jq("#inf-pw-keyWords").empty();
                    jq("#inf-pw-loader").hide();
                    jq("#inf-pw-productName").html(data.name);
                    jq("#inf-pw-productImg").attr("src", data.imgURL);
                    jq(".inf-pw-prodImage").show();
                    
                    jq.each(
                                    data.posKeywords,
                                    function(i, value) {
                                            value.keyWord = value.keyWord.replace(
                                                            "'", "&apos;");

                                            var posKeyWord = "<div class='inf-pw-posBlock' onclick='widget.productWidget.getPosComments("
                                                            + (i+1)
                                                            + ",\""
                                                            + value.keyWord
                                                            + "\","
                                                            + value.count
                                                            + ",0,2,this)' title='"
                                                            + value.keyWord
                                                            + " ("
                                                            + value.count
                                                            + ")"
                                                            + "'>"
                                                            + "<div class='inf-pw-nameText'>"
                                                            + value.keyWord
                                                            + " ("
                                                            + value.count
                                                            + ")</div>"
                                                            + "<div class='inf-pw-posBackground' ></div>"
                                                            + "</div>";
                                            if (i < 5)
                                                    jq("#inf-pw-poskeyWords")
                                                                    .append(posKeyWord);
                                            else
                                                    jq("#inf-pw-poskeywordsOverlay").append(
                                                                    posKeyWord);
                                            if (i == 5) {
                                                    jq("#inf-pw-poskeyWords")
                                                                    .append(
                                                                                    "<div class='inf-pw-showMore' id='inf-pw-posShowMore'>Show More</div>");
                                                    jq("#inf-pw-posShowMore")
                                                                    .click(
                                                                                    function() {
                                                                                            
                                                                                            
                                                                                            
                                                                                            if (jq(
                                                                                                            "#inf-pw-negShowMore")
                                                                                                            .hasClass(
                                                                                                                            'hide')) {

                                                                                                    jq(
                                                                                                                    "#inf-pw-negkeywordsOverlay")
                                                                                                                    .hide();
                                                                                                    jq(
                                                                                                                    "#inf-pw-negShowMore")
                                                                                                                    .removeClass(
                                                                                                                                    'hide');
                                                                                                    jq(
                                                                                                                    "#inf-pw-negShowMore")
                                                                                                                    .css(
                                                                                                                                    "background",
                                                                                                                                    "#FFF");
                                                                                                    jq("#inf-pw-negShowMore").css({"z-index":1,"box-shadow":""});
                                                                                            }

                                                                                            if (jq(this)
                                                                                                            .hasClass(
                                                                                                                            'hide')) {
                                                                                                    jq(
                                                                                                                    "#inf-pw-poskeywordsOverlay")
                                                                                                                    .hide();
                                                                                                    jq(this)
                                                                                                                    .removeClass(
                                                                                                                                    'hide');
                                                                                                    jq(this)
                                                                                                                    .css(
                                                                                                                                    "background",
                                                                                                                                    "#FFF");
                                                                                                    jq("#inf-pw-posShowMore").css({"z-index":1,"box-shadow":""});
                                                                                            } else {

                                                                                                    jq(this)
                                                                                                                    .addClass(
                                                                                                                                    'hide');
                                                                                    
                                                                                                    jq(
                                                                                                                    "#inf-pw-poskeywordsOverlay")
                                                                                                                    .show();
                                                                                                    jq("#inf-pw-posShowMore").css({"z-index":3,"box-shadow":"rgb(136, 136, 136) -1px -9px 17px -2px"});
                                                                                            }
                                                                                    });
                                            }

                                    });
                    jq
                                    .each(
                                                    data.negKeywords,
                                                    function(i, value) {
                                                            value.keyWord = value.keyWord.replace(
                                                                            "'", "&apos;");
                                                            var negKeyWord = "<div class='inf-pw-negBlock' onclick='widget.productWidget.getPosComments("
                                                                            + (i+1)
                                                                            + ",\""
                                                                            + value.keyWord
                                                                            + "\","
                                                                            + value.count
                                                                            + ",0,3,this)' title='"
                                                                            + value.keyWord
                                                                            + " ("
                                                                            + value.count
                                                                            + ")'"
                                                                            + ">"
                                                                            + "<div class='inf-pw-nameText' >"
                                                                            + value.keyWord
                                                                            + " ("
                                                                            + value.count
                                                                            + ")</div>"
                                                                            + "<div class='inf-pw-negBackground' ></div>"
                                                                            + "</div>";
                                                            if (i < 5)
                                                                    jq("#inf-pw-negkeyWords")
                                                                                    .append(negKeyWord);
                                                            else
                                                                    jq("#inf-pw-negkeywordsOverlay").append(
                                                                                    negKeyWord);

                                                            if (i == 5) {
                                                                    jq("#inf-pw-negkeyWords")
                                                                                    .append(
                                                                                                    "<div class='inf-pw-showMore' id='inf-pw-negShowMore'>Show More</div>");
                                                                    jq("#inf-pw-negShowMore")
                                                                                    .click(
                                                                                                    function() {
                                                                                                            jq("#inf-pw-negShowMore").css("z-index",3);
                                                                                                            jq("#inf-pw-negShowMore").css("box-shadow","rgb(136, 136, 136) -1px -9px 17px -2px");
                                                                                                            jq("#inf-pw-posShowMore").css("box-shadow","");
                                                                                                            jq("#inf-pw-posShowMore").css("z-index",1);
                                                                                                            
                                                                                                            if (jq(
                                                                                                                            "inf-pw-posShowMore")
                                                                                                                            .hasClass(
                                                                                                                                            'hide')) {
                                                                                                                    jq(
                                                                                                                                    "#inf-pw-poskeywordsOverlay")
                                                                                                                                    .hide();
                                                                                                                    jq(
                                                                                                                                    "#inf-pw-posShowMore")
                                                                                                                                    .removeClass(
                                                                                                                                                    'hide');
                                                                                                                    jq(
                                                                                                                                    "#inf-pw-posShowMore")
                                                                                                                                    .css(
                                                                                                                                                    "background",
                                                                                                                                                    "#FFF");
                                                                                                                    jq("#inf-pw-posShowMore").css({"z-index":1,"box-shadow":""});
                                                                                                            }

                                                                                                            if (jq(this)
                                                                                                                            .hasClass(
                                                                                                                                            'hide')) {
                                                                                                                    jq(
                                                                                                                                    "#inf-pw-negkeywordsOverlay")
                                                                                                                                    .hide();
                                                                                                                    jq(this)
                                                                                                                                    .removeClass(
                                                                                                                                                    'hide');
                                                                                                                    jq(this)
                                                                                                                                    .css(
                                                                                                                                                    "background",
                                                                                                                                                    "#FFF");
                                                                                                                    jq("#inf-pw-negShowMore").css({"z-index":1,"box-shadow":""});
                                                                                                            } else {

                                                                                                                    jq(this)
                                                                                                                                    .addClass(
                                                                                                                                                    'hide');
                                                                                                                    
                                                                                                                    jq(
                                                                                                                                    "#inf-pw-negkeywordsOverlay")
                                                                                                                                    .show();
                                                                                                                    jq("#inf-pw-negShowMore").css({"z-index":3,"box-shadow":"rgb(136, 136, 136) -1px -9px 17px -2px"});
                                                                                                            }
                                                                                                    });
                                                            }

                                                    });

            }
            
            function capitalise(string) {
                    return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
            }


            this.getPosComments=getPosComments;
            function getPosComments(id, keyword, count, skip, reviewType,element) {
            	var prodId=queryParams.ProductId;
            	if(jq(element).attr("class") != "inf-pw-blueBtn" 
            		&&  jq(element).attr("class") !="inf-mb-prd-blueBtn"){  // ignore
																			// when
																			// show
																			// more
																			// button
																			// is
																			// clicked
					 
               		 
            	}               
            	    if (skip == 0) {
                            jq("#inf-pw-posoverlay").hide();
                            jq("#inf-pw-negoverlay").hide();
                    }
                    var marginTop=((Math.floor(id/6))*45)+95;    /* Calculates the  Top margin position for the comments block.*/  
																	
                  id = id % 6;
                    var marginPos = (id * overlayPosition) + 245;
                    if (reviewType == 2) {
                            jq("#inf-pw-posoverlay").css("margin-left", marginPos + "px");
                            jq("#inf-pw-posoverlay").css("margin-top", marginTop + "px");
                            jq('.inf-pw-header').text(
                                            "Showing all positive user reviews associated with " + capitalise(keyword));
                    } else {
                            jq("#inf-pw-negoverlay").css("margin-left", marginPos + "px");
                            jq('.inf-pw-header').text(
                                            "Showing all negative user reviews associated with " + capitalise(keyword));
                    }
                    jq
                                    .ajax({
                                            type : "POST",
                                            url : queryParams.url+"/getPostsFromMongo",
                                            data : "entity=" + queryParams.entity + "&subproduct=" + queryParams.subProduct
                                                            + "&word=" + encodeURIComponent(keyword) + "&skip=" + skip + "&reviewType="
                                                            + reviewType + "&limit=" + limit + "&productId="
                                                            + queryParams.ProductId+"&price="+encodeURIComponent(widgetOpts.price),
                                            dataType : "json",
                                            success : function(data) {

                                                    jq(".inf-pw-next").remove();
                                                    var dfs = InfFragBuilder(data);
                                                    if (skip == 0) {
                                                            skip = skip + 5;
                                                            jq('.inf-pw-review_container_product .mCSB_container').empty();
                                                            jq('.inf-pw-review_container_product .mCSB_container').append(dfs);
                                                            if (skip < count)
                                                                    jq('.inf-pw-review_container_product .mCSB_container')
                                                                                    .append(
                                                                                                    "<div class='inf-pw-next'><center><a><div class='inf-pw-blueBtn'  onClick=\"widget.productWidget.getPosComments("
                                                                                                                    + id
                                                                                                                    + ",'"
                                                                                                                    + keyword
                                                                                                                    + "\',"
                                                                                                                    + count
                                                                                                                    + ","
                                                                                                                    + skip
                                                                                                                    + ","
                                                                                                                    + reviewType
                                                                                                                    + ", this"
                                                                                                                    + ")\"></div></a></center></div>");
                                                    }

                                                    else {

                                                            skip = skip + 5;
                                                            jq('.inf-pw-review_container_product .mCSB_container').append(dfs);
                                                            if (skip < count)
                                                                    jq('.inf-pw-review_container_product .mCSB_container')
                                                                                    .append(
                                                                                                    "<div class='inf-pw-next'><center><a><div class='inf-pw-blueBtn'   onClick=\"widget.productWidget.getPosComments("
                                                                                                                    + id
                                                                                                                    + ",'"
                                                                                                                    + keyword
                                                                                                                    + "\',"
                                                                                                                    + count
                                                                                                                    + ","
                                                                                                                    + skip
                                                                                                                    + ","
                                                                                                                    + reviewType
                                                                                                                    + ", this"
                                                                                                                    + ")\"></div></a></center></div>");
                                                    }
                                                    if (reviewType == 2)
                                                            jq('#inf-pw-posoverlay').show();

                                                    else
                                                            jq('#inf-pw-negoverlay').show();
                                                    jq(".inf-pw-review_container_product").mCustomScrollbar("update");

                                            },
                                            async : false,
                                            complete : function() {
                                                    if (reviewType == 2) {

                                                            jq.each(jq('.inf-pw-review_panel .inf-pw-review'),
                                                                            function(index, item) {
                                                                                    var wordToHighlight = jq(this).attr('word');
                                                                                    var myHilitor = new Inf_Hilitor(
                                                                                                    "inf-pw-posreview_container");
                                                                                    myHilitor.setMatchType("left");
                                                                                    myHilitor.apply(wordToHighlight);
                                                                            });

                                                    } else {

                                                            jq.each(jq('.inf-pw-review_panel .inf-pw-review'),
                                                                            function(index, item) {
                                                                                    var wordToHighlight = jq(this).attr('word');
                                                                                    var myHilitor = new Inf_Hilitor(
                                                                                                    "inf-pw-negreview_container");
                                                                                    myHilitor.setMatchType("left");
                                                                                    myHilitor.apply(wordToHighlight);
                                                                            });
                                                    }

                                                    if (skip == 5) {
                                                            jq(".inf-pw-review_container_product").mCustomScrollbar("destroy");
                                                            jq(".inf-pw-review_container_product").mCustomScrollbar({
                                                                    scrollButtons : {
                                                                            enable : true
                                                                    },
                                                                    advanced : {
                                                                            updateOnContentResize : true,
                                                                            scrollInertia : 10
                                                                    }
                                                            });
                                                    }
                                            },
                                            error : function(e) {
                                                    alert('Error' + e);
                                            }

                                    });

            }
            
                   
          // add post click Event
            function addpostReadClickEvent(postId,word){
            	// alert("2");
            	  jq.ajax({
                      type : "POST",
                      url : queryParams.url+"/DoCaptureAnalytics",
                      data : "eventType=readPost&word="+word + "&postId=" +postId +"&callFrom=otherWidget"+"&entity="+parameters.brandName+"&subProduct="+parameters.productName,
                      cache : false,
                      crossDomain : true,
                      success : function(data) {

                      }
              });
            };

            
    };
    
    /*
	 * This variable contains all the script for the product page to show the
	 * keywords(For nike and moltonbrown and Macys).
	 */
    var ClientProduct=function(){
              /** *************productdetail.js**************************** */


        var showPos = 0; /* 0 indicates positive overlay is close ,1 indicates it is open. */ 
                                                        
                                              
        var showNeg = 0; /* 0 indicates negative overlay is close ,1 indicates it is open.*/
                                                 
        var limit = 5;
      
        var KEYCODE_ESC = 27; /*Escape key keycode*/
        var jq;
        var keyWordsToDisplay=0;
        var skipposCountForKeywords=20; /* Keeps track of the skip value for positive keywords*/
        var skipnegCountForKeywords=20;	/* Keeps track of the skip value for negative keywords.*/
									
        var skipCount=20;   /*
							 * The toatal number of keywords needs to be loaded
							 * at once
							 */ 
        this.onloadMBproducts=onloadMBproducts;
        function onloadMBproducts(jquery) {
                				jq=jquery;
                                jq(window).scroll(function(){
                                          jq(".inf-bw-outer_wrapper").animate({ top :
                                                  window.pageYOffset+150}, 100);
                                          });
                               
                                jq(document)
                                                .keyup(
                                                                function(event) {

                                                                        var code = (event.keyCode ? event.keyCode
                                                                                        : event.which);
                                                                        if (code == KEYCODE_ESC) {
                                                                                jq(".inf-mb-prd-negoverlay").hide();
                                                                                jq(".inf-mb-prd-posoverlay").hide();
                                                                                jq(".inf-mb-prd-arrow").hide();
                                                                                if (showNeg == 1) {
                                                                                        jq(".inf-all-prd-keywords #inf-mb-prd-negShowmore").css(
                                                                                                        "background-position",
                                                                                                        "12px 10px");
                                                                                        jq(".inf-mb-prd-negoverlay").hide();
                                                                                        showNeg = 0;
                                                                                } else if (showPos == 1) {
                                                                                        jq(".inf-mb-prd-posoverlay").hide();
                                                                                        showPos = 0;
                                                                                        jq(".inf-all-prd-keywords #inf-mb-prd-posShowmore").css(
                                                                                                        "background-position",
                                                                                                        "12px 10px");
                                                                                }

                                                                        }

                                                                });
                                            
                                jq(".inf-all-prd-keywords #inf-mb-prd-posShowmore").click(
                                                function() {
                                                        jq(".inf-mb-prd-arrow").hide();
                                                        if (showPos == 0) {
                                                                jq(".inf-mb-prd-posoverlay").show();
                                                                showPos = 1;
                                                                jq(".inf-all-prd-keywords #inf-mb-prd-posShowmore").css(
                                                                                "background-position", "12px 18px");
                                                                jq(".inf-all-prd-keywords #inf-mb-prd-negoverlay").hide();
                                                                showNeg = 0;
                                                                jq(".inf-all-prd-keywords #inf-mb-prd-negShowmore").css(
                                                                                "background-position", "12px 10px");
                                                                jq(".inf-mb-prd-review_container").css("height","205px");
                                                                getProductDetails(data);
                                                                
                                                        } else {
                                                                jq(".inf-mb-prd-posoverlay").hide();
                                                                showPos = 0;
                                                                jq(".inf-all-prd-keywords #inf-mb-prd-posShowmore").css(
                                                                                "background-position", "12px 10px");
                                                        }
                                                        jq(".inf-mb-prd-goback").hide();
                                                        jq(".inf-mb-prd-comments-info ").hide();

                                                
                                                });

                                jq(".inf-all-prd-keywords #inf-mb-prd-negShowmore").click(
                                                function() {
                                                        jq(".inf-mb-prd-arrow").hide();
                                                        if (showNeg == 0) {
                                                                jq(".inf-mb-prd-negoverlay").show();
                                                                showNeg = 1;
                                                                jq(".inf-all-prd-keywords #inf-mb-prd-negShowmore").css(
                                                                                "background-position", "12px 18px");

                                                                jq(".inf-mb-prd-posoverlay").hide();
                                                                showPos = 0;
                                                                jq(".inf-all-prd-keywords #inf-mb-prd-posShowmore").css(
                                                                                "background-position", "12px 10px");
                                                                jq(".inf-mb-prd-review_container").css("height","205px");
                                                                getProductDetails(data);
                                                                
                                                        } else {
                                                                jq(".inf-all-prd-keywords #inf-mb-prd-negShowmore").css(
                                                                                "background-position", "12px 10px");
                                                                jq(".inf-mb-prd-negoverlay").hide();
                                                                showNeg = 0;
                                                        }
                                                        jq(".inf-mb-prd-goback").hide();
                                                        jq(".inf-mb-prd-comments-info ").hide();
                                                        
                                                });

                                jq('.inf-mb-prd-review_container').on("click", ".inf-mb-prd-review", function() {

                                        if (jq(this).find('.inf-mb-prd-review_comment').hasClass('expand')) {
                                          	  var h = jq(this).find('.inf-mb-prd-review_comment p').height() + 20;

                                              jq(this).find('.inf-mb-prd-review_comment').removeClass('expand');
                                              jq(this).find('.inf-mb-prd-review_comment').animate({
                                                      'height' : h + 'px'
                                              }, {
                                                      duration : 50,
                                                      complete : function() {
                                                      	 
                                                      }
                                              }

                                              );

                                            	if(queryParams.entity=="nike"){
                                            		
                                            		addNikePostReadClickEvent(this.id ,jq(this).attr("word"),queryParams.ProductId);
                                            	}
                                            	else{
                                            		// register review read
													// comments
                                            	    addpostReadClickEvent(this.id ,jq(this).attr("word"));
                                            	}
                                        		
                                        	    
                                              
                                        } else {
                                                jq(this).find('.inf-mb-prd-review_comment').addClass('expand');

                                                jq(this).find('.inf-mb-prd-review_comment').animate({
                                                        'height' : '58px'
                                                }, {
                                                        duration : 50,
                                                        complete : function() {
                                                        	
                                                        }
                                                });

                                        }
                                        	
                                       

                                });

                                jq(".inf-mb-prd-review_container").mCustomScrollbar({
                                        mouseWheel : true,
                                        mouseWheelPixels : "auto",
                                        scrollButtons : {
                                                enable : true
                                        },
                                        advanced : {
                                                updateOnContentResize : true
                                        }
                                });

                                jq(".inf-mb-prd-closebutton").click(
                                                function() {
                                                        jq(".inf-mb-prd-posoverlay").hide();
                                                        jq(".inf-mb-prd-arrow").hide();
                                                        jq(".inf-mb-prd-posoverlay").hide();
                                                        showPos = 0;
                                                        jq(".inf-all-prd-keywords #inf-mb-prd-posShowmore").css(
                                                                        "background-position", "12px 10px");
                                                        jq(".inf-mb-prd-negoverlay").hide();
                                                        jq(".inf-mb-prd-arrow").hide();
                                                        jq(".inf-mb-prd-negoverlay").hide();
                                                        showNeg = 0;
                                                        jq(".inf-all-prd-keywords #inf-mb-prd-negShowmore").css(
                                                                        "background-position", "12px 10px");
                                                        
                                                });

                                jq(".inf-mb-prd-goback").click(function() {
                                        jq(".inf-mb-prd-review_container").css("height","205px");
                                        getProductDetails(data);
                                        jq(".inf-mb-prd-goback").hide();
                                        jq(".inf-mb-prd-comments-info ").hide();
                                        
                                });

                        }
                        
        // );

        this.getProductDetails=getProductDetails;
        function getProductDetails(keywordsdata) {
                
                jq(".inf-all-prd-keywords #inf-mb-prd-poskeyWords").empty();
                jq(".inf-all-prd-keywords #inf-mb-prd-negkeyWords").empty();
                jq('.inf-mb-prd-review_container .mCSB_container').empty();
                jq("#inf-mb-prd-productImg").attr("src", keywordsdata.imgURL);
                jq(".inf-mb-prd-prodnameimg").text(keywordsdata.name);
                jq("#inf-mb-prd-price").html(keywordsdata.currency+keywordsdata.price);
                jq(".inf-prd-prodname").text(keywordsdata.name);
                jq('.inf-mb-prd-comments-header').text("Please click on any of the following product traits to see the associated review(s)");
                if (keywordsdata.posKeywords.length == 0) {
                        jq("#inf-mb-prd-poskeyWords").append(" No positive traits found");

                } else {
                        if(queryParams.entity=="moltonbrown"){
                        keyWordsToDisplay=3;
                        }else{
                                keyWordsToDisplay=5;}
                jq.each(
                                                keywordsdata.posKeywords,
                                                function(i, value) {
                                                        value.keyWord = value.keyWord.replace("'", "&apos;");
                                                        var posKeyWord = null;
                                                        if (i < keyWordsToDisplay) {
                                                                if (i != 0) {
                                                                        jq(".inf-all-prd-keywords  #inf-mb-prd-poskeyWords").append(", ");
                                                                }
                                                                var keyWordstoappend=buildKeywordsHTML("inf-mb-prd-poskeyword",value,i,2);
                                                                var jsonFormat="["+JSON.stringify(keyWordstoappend)+"]";
                                                                                                                                     
                                                             var keywordsHTML=InfFragBuilder(JSON.parse(jsonFormat));
                                                                                               
                                                                jq(".inf-all-prd-keywords  #inf-mb-prd-poskeyWords").append(keywordsHTML);
                                                        } else {

                                                                        if (i == keyWordsToDisplay) {
                                                                                jq(".inf-all-prd-keywords #inf-mb-prd-posShowmore").show();

                                                                }
                                                                posKeyWord = "<div class='inf-mb-prd-allkeyword' onclick='widget.clientProduct.getmbComments("
                                                                                + (i + 1)
                                                                                + ",\""
                                                                                + value.keyWord
                                                                                + "\","
                                                                                + value.count
                                                                                + ",0,2,this)' "
                                                                                + " title='"
                                                                                + value.keyWord
                                                                                + " ("
                                                                                + value.count
                                                                                + ")'"
                                                                                + ">"
                                                                                + value.keyWord
                                                                                + " ("
                                                                                + value.count
                                                                                + ")" + "</div>";

                                                                jq('.inf-mb-prd-posoverlay .inf-mb-prd-review_container .mCSB_container')
                                                                                .append(posKeyWord);

                                                        }

                                                });
                
                if(skipposCountForKeywords<keywordsdata.totposCount){
                	jq('.inf-mb-prd-posoverlay .inf-mb-prd-review_container .mCSB_container')
                	.append("<div class='inf-mb-prd-next'><center><a><div class='inf-mb-prd-blueBtn' onclick='widget.clientProduct.showMoreposKeyWords("+skipposCountForKeywords+",\"positive\")'></div></a></center></div>");
                }
        }
                
                if (keywordsdata.negKeywords.length === 0) {
                        jq("#inf-mb-prd-negkeyWords").append(" No negative traits found");
                } else {
                        
                        if(queryParams.entity=="moltonbrown"){
                        keyWordsToDisplay=3;
                        }else{
                                keyWordsToDisplay=5;
                              }
                jq
                                .each(
                                                keywordsdata.negKeywords,
                                                function(i, value) {
                                                        value.keyWord = value.keyWord.replace("'", "&apos;");
                                                        var negKeyWord = null;
                                                        if (i < keyWordsToDisplay) {
                                                                if (i != 0) {
                                                                        jq(".inf-all-prd-keywords #inf-mb-prd-negkeyWords").append(", ");
                                                                }
                                                                                                                           
                                                                var keyWordstoappend=buildKeywordsHTML("inf-mb-prd-negkeyword",value,i,3);
                                                                var jsonFormat="["+JSON.stringify(keyWordstoappend)+"]";
                                                                var keywordsHTML=InfFragBuilder(JSON.parse(jsonFormat));
                                                                                
                                                               jq(".inf-all-prd-keywords #inf-mb-prd-negkeyWords").append(keywordsHTML);
                                                        } else {
                                                                if (i == keyWordsToDisplay) {
                                                                        jq(".inf-all-prd-keywords #inf-mb-prd-negShowmore").show();
                                                                                
                                                                }
                                                                negKeyWord = "<div class='inf-mb-prd-allkeyword' onclick='widget.clientProduct.getmbComments("
                                                                                + (i + 1)
                                                                                + ",\""
                                                                                + value.keyWord
                                                                                + "\","
                                                                                + value.count
                                                                                + ",0,3,this)'"
                                                                                + " title='"
                                                                                + value.keyWord
                                                                                + " ("
                                                                                + value.count
                                                                                + ")'"
                                                                                + " >"
                                                                                + value.keyWord
                                                                                + " ("
                                                                                + value.count
                                                                                + ")" + "</div>";

                                                                jq('.inf-mb-prd-negoverlay .inf-mb-prd-review_container .mCSB_container')
                                                                                .append(negKeyWord);

                                                        }

                                                });
                if(skipnegCountForKeywords<keywordsdata.totnegCount){
                	jq('.inf-mb-prd-negoverlay .inf-mb-prd-review_container .mCSB_container')
                	.append("<div class='inf-mb-prd-next'><center><a><div class='inf-mb-prd-blueBtn' onclick='widget.clientProduct.showMorenegKeyWords("+skipnegCountForKeywords+",\"negative\")'></div></a></center></div>");
                }
        }

                registerEventsForComments();
        }

        
        
        this.showMoreposKeyWords=showMoreposKeyWords;
        function showMoreposKeyWords(skip,type){
        	   jq.ajax({
                   type : "POST",
                   url : queryParams.url+"/GetKeywords",
                   data : "entity=" + queryParams.entity + "&subProduct=" + queryParams.subProduct +"&skip=" + skip
                   			+"&ProductId=" + queryParams.ProductId+"&reviewType="+type,
                   dataType : "json",
                   success : function(serverdata) {
                	   
                	   skipposCountForKeywords+=skipCount;
                	   jq
                       .each(
                                       serverdata.posKeywords,
                                       function(i, value) {
                                    	   
                                    	   data.posKeywords.push(value);
                                    	   
                                       });
                	   getProductDetails(data);
                   }
        	   });
        }
        
        this.showMorenegKeyWords=showMorenegKeyWords;
        function showMorenegKeyWords(skip,type){
        	   jq.ajax({
                   type : "POST",
                   url : queryParams.url+"/GetKeywords",
                   data : "entity=" + queryParams.entity + "&subProduct=" + queryParams.subProduct +"&skip=" + skip
                   			+"&ProductId=" + queryParams.ProductId+"&reviewType="+type,
                   dataType : "json",
                   success : function(serverdata) {
                	   
                	   skipnegCountForKeywords+=skipCount;
                	   jq
                       .each(
                                       serverdata.negKeywords,
                                       function(i, value) {
                                    	   
                                    	   data.negKeywords.push(value);
                                    	   
                                       });
                	   getProductDetails(data);
                   }
        	   });
        }
        
        
        function buildKeywordsHTML(className,value,i,type){
        	  
            var keyWordstoappend={};
            keyWordstoappend.tagName="div";
            keyWordstoappend.className=className;
            keyWordstoappend.click="widget.clientProduct.getmbComments("
                    + (i + 1)
                    + ",'"
                    + value.keyWord
                    + "',"
                    + value.count
                    + ",0,"+ type+",this)";
            keyWordstoappend.textContent=value.keyWord  + " ("
            + value.count
            + ")";
        var arrow={};
        arrow.tagName="div";
        arrow.className="inf-mb-prd-arrow";
        

        var arrowImage={};
        arrowImage.tagName="div";
        arrowImage.className="inf-mb-prd-arrow-down";
        


        var arrowImageShadow={};
        arrowImageShadow.tagName="div";
        arrowImageShadow.className="inf-mb-prd-arrow-shadow";
        arrow.childNodes=[];
        arrow.childNodes[0]=arrowImage;
        arrow.childNodes[1]=arrowImageShadow;
        keyWordstoappend.childNodes=[];
        keyWordstoappend.childNodes[0]=arrow;

        	return keyWordstoappend;
        }
        
        
            function capitalise(string) {
                    return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
            }

      /* Get the comments for particular keyword. */  
        this.getmbComments=getmbComments;
        function getmbComments(id, keyword, count, skip, reviewType,element) {
        	var prodId=queryParams.ProductId;
                if (reviewType == 2) {
                        jq('.inf-mb-prd-comments-header').text(
                                        "All positive user reviews associated with '" + capitalise(keyword)+ "'");

                } else {

                        jq('.inf-mb-prd-comments-header').text(
                                        " All negative user reviews associated with '" + capitalise(keyword)+ "'");
                }
                
                jq.ajax({
                        type : "POST",
                        url : queryParams.url+"/getPostsFromMongo",
                        data : "entity=" + queryParams.entity + "&subproduct=" + queryParams.subProduct + "&word="
                                        + encodeURIComponent(keyword) + "&skip=" + skip + "&reviewType=" + reviewType
                                        + "&limit=" + limit + "&productId=" + prodId+"&mbProduct=true&price="+encodeURIComponent(widgetOpts.price),
                        dataType : "json",
                        success : function(data) {
                        		jq(".inf-mb-prd-next").remove();
                                var dfs = InfFragBuilder(data);
                        
                                if(skip==0)
                                	jq('.inf-mb-prd-review_container .mCSB_container').empty();    	
                                        skip = skip + 5;
                                      jq('.inf-mb-prd-review_container .mCSB_container').append(dfs);
                                        if (skip < count)
                                                jq('.inf-mb-prd-review_container .mCSB_container').append(
                                                                "<div class='inf-mb-prd-next'><center><a><div class='inf-mb-prd-blueBtn'  onClick=\"widget.clientProduct.getmbComments("
                                                                                + id + ",'" + keyword + "\'," + count + ","
                                                                                + skip + "," + reviewType+",this"
                                                                                + ")\"></div></a></center></div>");
                        
                                        jq(".inf-mb-prd-review_container").mCustomScrollbar("update");
                                  
                             
                                
                        },
                        async : false,
                        complete : function() {
                                if (reviewType == 2) {

                                        jq.each(jq('.inf-mb-prd-review_panel .inf-mb-prd-review'), function(index, item) {
                                                var wordToHighlight = jq(this).attr('word');
                                                var myHilitor = new Inf_Hilitor("inf-mb-prd-posreview_container");
                                                myHilitor.setMatchType("left");
                                                myHilitor.apply(wordToHighlight);
                                        });

                                } else {

                                        jq.each(jq('.inf-mb-prd-review_panel .inf-mb-prd-review'), function(index, item) {
                                                var wordToHighlight = jq(this).attr('word');
                                                var myHilitor = new Inf_Hilitor("inf-mb-prd-negreview_container");
                                                myHilitor.setMatchType("left");
                                                myHilitor.apply(wordToHighlight);
                                        });
                                }

                                if (skip == 5) {
                                        jq(".inf-mb-prd-review_container").mCustomScrollbar("destroy");
                                        
                                        jq(".inf-mb-prd-review_container").mCustomScrollbar({
                                                scrollButtons : {
                                                        enable : true
                                                },
                                                advanced : {
                                                        updateOnContentResize : true,
                                                        scrollInertia : 10
                                                }
                                        });
                                }
                        },
                        error : function(e) {
                                alert('Error' + e);
                        }

                });
                
                if(skip==5 && id>keyWordsToDisplay){
                        jq(".inf-mb-prd-goback").show();
                        jq(".inf-mb-prd-comments-info ").show();
                }
                jq(".inf-mb-prd-review_container").css("height","170px");
        }

        function registerEventsForComments() {
                
                jq(".inf-mb-prd-poskeyword").click(function() {
                        jq(".inf-mb-prd-posoverlay").show();
                        jq(".inf-mb-prd-arrow").hide();
                        jq(this).children(".inf-mb-prd-arrow").css("display", "inline");
                        jq(".inf-mb-prd-negoverlay").hide();
                        showNeg = 0;
                        jq(".inf-all-prd-keywords #inf-mb-prd-negShowmore").css("background-position", "12px 10px");
                        showPos = 0;
                        jq(".inf-all-prd-keywords #inf-mb-prd-posShowmore").css("background-position", "12px 10px");
                        jq(".inf-mb-prd-comments-info ").show();
                        jq(".inf-mb-prd-goback").hide();
                });

                jq(".inf-mb-prd-negkeyword").click(function() {
                        jq(".inf-mb-prd-negoverlay").show();
                        jq(".inf-mb-prd-arrow").hide();
                        jq(this).children(".inf-mb-prd-arrow").css("display", "inline");
                        jq(".inf-mb-prd-posoverlay").hide();
                        showNeg = 0;
                        jq(".inf-all-prd-keywords #inf-mb-prd-posShowmore").css("background-position", "12px 10px");

                        showPos = 0;
                        jq(".inf-all-prd-keywords #inf-mb-prd-negShowmore").css("background-position", "12px 10px");
                        jq(".inf-mb-prd-comments-info ").show();
                        jq(".inf-mb-prd-goback").hide();
                });
                

                
                

        }

              
      // add post click Event
        function addpostReadClickEvent(postId,word){

        	  jq.ajax({
                  type : "POST",
                  url : queryParams.url+"/DoCaptureAnalytics",
                  data : "eventType=readPost&word="+word + "&postId=" +postId +"&callFrom=otherWidget"+"&entity="+parameters.brandName+"&subProduct="+parameters.productName,
                  cache : false,
                  crossDomain : true,
                  success : function(data) {

                  }
          });
        };
        
        function addNikePostReadClickEvent(postId,word,productId){
        	
        	 jq.ajax({
                 type : "POST",
                 url : queryParams.url+"/DoCaptureAnalytics",
                 data : "eventType=readPostForNike&word="+word + "&postId=" +postId + "&productId=" +productId +"&callFrom=otherWidget"+"&entity="+parameters.brandName+"&subProduct="+parameters.productName,
                 cache : false,
                 crossDomain : true,
                 success : function(data) {

                 }
         });
        };
        
       

    };
    
    var clientProduct=new ClientProduct();
    this.clientProduct=clientProduct;
    
    var productWidget=new ProductWidget();
    this.productWidget=productWidget;
    
    var featuresWidget=new FeaturesWidget();
    this.featuresWidget=featuresWidget;
    
    
    /**
	 * @constructor
	 * @param {Object}
	 *            parameters widget parameters
	 */



   
    /**
	 * Defines the functionality of the widget
	 * 
	 * @param {Object}
	 *            jq - Local jquery variable
	 * @param {Object}
	 *            widgetOpts - container for all custom information
	 */

    function widgetMaker(jq, widgetOpts) {
        var widget, actions;
        actions = {
            'category-related-widget': function() {
                    data=null; // reset global variable
                    categoryRelatedProduct(jq, widgetOpts,true);
            },
            'particular-product-widget': function() {
                    data=null;  // reset global variable
              particularProduct(jq, widgetOpts);
            },
            'moltonbrown-product-widget': function() {
                    data=null;  // reset global variable
                    moltonbrownProduct(jq, widgetOpts);
          },
          'nike-product-widget': function() {
                  data=null;  // reset global variable
                  nikeProduct(jq, widgetOpts);
        },
        'macys-product-widget':function(){
        	data=null;  // reset global variable
            macysProduct(jq, widgetOpts);
        },
        'guitarcenter-product-widget':function(){
        	data=null;  // reset global variable
            guitarcenterProduct(jq, widgetOpts);
        },
        'klwines-product-widget':function(){
        	data=null;  // reset global variable
            klwinesProduct(jq, widgetOpts);
        },
        'oldnavy-product-widget':function(){
        	data=null;  // reset global variable
            oldnavyProduct(jq, widgetOpts);
        }
        };
        if (typeof actions[widgetOpts.widgetType] !== 'function') {
            throw new Error('Undefined Widget Type');
        }
        actions[widgetOpts.widgetType]();
    }

    function categoryRelatedProduct(jq, widgetOpts,onload) {

       // cssDynamicLoader(getStyle());
        constructWidget(jq, widgetOpts);
        

        function constructWidget(jq, widgetOpts) {
            var queryUrl;
            // ajax call
            queryUrl=constructUrl(jq, widgetOpts, '/ProductWidgetServlet');
            
            jq.ajax({
                    type:"POST",
                    dataType : "json",
                    url: queryUrl,
                success: function(json) {
                        if(json!=null){
                        	if(onload){
                        	var pricedetails = json.price;
                        	widgetOpts.maxprice=pricedetails.maxprice;
                        	widgetOpts.currency=pricedetails.currency;
                        	}
                        	data=json.features;
                        }
                        
                        
                        // dom manipulations - set initial content template
                        if(onload){
                            doDomManipulationsfirst(jq, widgetOpts);
                            }
                            else{
                            	// If goback is not present 
                            	if(! jq('.inf-bw-content_container .inf-bw-back').is(':visible')){
                            		//If Not Onload Just reload features
                            		featuresWidget.addFeatures();       
                            		//if products div is opened , click on the outer tile or close it.
                            		 if (jq('.inf-bw-container #inf-bw-info_panel').hasClass('inf-bw-panel_open')) {
                            			 if(subDimClicked!=null){
                            				 jq('#subcontainer .inf-bw-front :contains('+subDimClicked+')').click();
                            			 }
                            		 }
                            	}
                            	// If goback is present, get the subdimension clicked 
                            	// AND {If goback is present and products div is open,  }
                            	else{//{and products tab is open, get currently clicked subdimension}
                            		//if subdimension is clicked
                            		// if word is clicked.
                                    //Get the subdimendion clicked previously.
                            			if(wordClicked !=null && wordClicked!="null"){
                            				// load subdimensions
                            				featuresWidget.getsubDimension(subDimClicked,0,this);
                            				//or click the inner tile
                            				jq('#subcontainer .inf-bw-front :contains('+wordClicked+')').click();
                            			}else if(subDimClicked!=null){
                            				// get the outer tile, click on it.
                            				featuresWidget.addFeatures();
                            				//var element = 
                            					jq('#subcontainer .inf-bw-front :contains('+subDimClicked+')').click();
                            				//element.click();
                            				//allKeysClick('null', 0, 0, totalCountlocal, element, null, featureName);
                            			}
                            	//	}
                            	}
                            	
                            	
                            }
                        
                }
            });
          
        }



       

        function doDomManipulationsfirst(jq, widgetOpts) {
            var divToAppend = getDomToAppend();
            widgetOpts.containerDom.innerHTML = divToAppend;
            featuresWidget.onloadMethod(jq); // custom_script.js method call
        }

        function getDomToAppend() {
            return [
                '<div class="inf-bw-container">',
                '<div id=\'resizable\' class=\'ui-widget-content inf-bw-outer_wrapper\'>                                                                                       ',
                '                <div class=\'inf-bw-showcase_btn\'>                                                                                                                    ',
                '                        <div class=\'inf-bw-showcase_btn_icon\'>                                                                                                           ',
                '                                <p>Find Products Based On User Reviews</p>                                                                                                     ',
                '                        </div>                                                                                                                                             ',
                '                </div>                                                                                                                                                 ',
                '                <div id=\'inf-bw-info_panel\' class=\'inf-bw-info_panel inf-bw-panel_close\'>                                                                          ',
                '                        <div class=\'inf-bw-slidepanel\'></div>                                                                                                            ',
                '                        <div class=\'inf-bw-info_container\'>                                                                                                              ',
                '                                                                                                                                                              ',
                '                                <div class=\'inf-bw-header_section\'>                                                                                                          ',
                '                                        <h3></h3>                                                                                                                                  ',
                '                                        <div class=\'inf-bw-userinfo\'>Please click on the <span id="inf-bw-commentPosIcon" class=\'inf-bw-commentsIcon\'></span>     ',
                '     or  <span  id="inf-bw-commentNegIcon" class=\'inf-bw-commentsIcon\'/></span> to see the positive or negative comments for the product.</div>  ',
              
                '                                        <div id=\'inf-bw-product_name\' title=\'\'></div>                                                                                          ',
                '                                        <div id=\'inf-bw-comment_info\' title=\'\'>Please click anywhere on the comment to see the complete text.</div>                                                                                          ',
                '                                                <div class=\'inf-bw-headerIcon\'>                                                                                                         ',
                '                                                <span id=\'inf-bw-span_back\' class=\'inf-bw-span_back_hidden\'></span>                                                                ',
                '                                                <div class=\'inf-bw-review_type\'>                                                                                                     ',
                '                                                        <div class=\'inf-bw-positive_review\'>                                                                                             ',
                '                                                                <div id=\'inf-bw-positiveCountno\'></div>                                                                                      ',
                '                                                                <div id=\'posarrow\' class=\'inf-bw-inferlytics-arrow\'></div>                                                                 ',
                '                                                        </div>                                                                                                                             ',
                '                                                        <div class=\'inf-bw-negative_review\'>                                                                                             ',
                '                                                                <div id=\'inf-bw-negativeCountno\'></div>                                                                                      ',
                '                                                                <div id=\'negarrow\' class=\'inf-bw-inferlytics-arrow\'></div>                                                                 ',
                '                                                        </div>                                                                                                                             ',
                '                                                                                                                                                              ',
                '                                                </div>                                                                                                                                 ',
                '                                        </div>                                                                                                                                     ',
                '                                </div>                                                                                                                                         ',
                '                                                                                                                                                                               ',
                '                                                                                                                                                                               ',
                '                                <div class=\'inf-bw-review_container\' id=\'review_container\'></div>                                                                          ',
                '                        </div>                                                                                                                                             ',
                '                </div>                                                                                                                                                 ',
                '                <div class=\'inf-bw-content_panel\'>                                                                                                                   ',
                '                        <div class=\'inf-bw-header_panel\'>                                                                                                                ',
                '                                <div class=\'inf-bw-dragger\'></div>                                                                                                           ',
                '                                <div class=\'inf-bw-icon\'></div>                                                                                                              ',
                '                                <h1>Find Products Based On User Reviews</h1>                                                                                                   ',
                '                                <div class=\'inf-bw-minimize_panel\'></div>                                                                                                    ',
                '                        </div>                                                                                                                                             ',
                '                        <div class=\'inf-bw-content_container\'>                                                                                                           ',
                '                                <div id=\'inf-bw-tabs\' class=\'tabs\'>                                                                                                        ',
                '                                                                                                                                                              ',
                '                                        <ul class=\'inf-bw-tabs_panel\'>                                                                                                           ',
                '                                                <li><a class=\'selected inf-bw-feature\' title=\'By Features\'                                                                         ',
                '                                                        href=\'#tabs-1\'><span></span><label class=\'keyword\'><div></div></label></a></li>                                        ',
                '                                        </ul>                                                                                                                                      ',
                '                                                                                                                                                              ',
                '                                        <div id=\'tabs-1\' class=\'inf-bw-tab_container \'>                                                                                        ',
                '                                                                                                                                                              ',
                '                                                <div class=\'inf-bw-tab_header\'>                                                                                                      ',
                '                                                        <div class=\'inf-bw-infer-headerinfo\'>Click on a product trait to                                                                 ',
                '                                                                find related products.</div>                                                                                                   ',
                '                                                                                                                                                              ',
                '                                                </div>                                                                                                                                 ',
                '                                                <div class=\'inf-bw-back\' Onclick=\'basicWidget.featuresWidget.backbuttonclick()\'></div>                                                                               ',
                '                                                <div id=\'feature_sorting\' class=\'inf-bw-tab_content tab_content2 \'>                                                                ',
                '                                                        <div class=\'inf-bw-data_container1\'>                                                                                             ',
                '                                                                <div id=\'subcontainer\'                                                                                                       ',
                '                                                                        class=\'tp-portfolio theBigThemePunchGallery\'></div>                                                                      ',
                '                                                        </div>                                                                                                                             ',
                '                                                                                                                                                              ',
                '                                                </div>                                                                                                                                 ',
                '                                        </div>                                                                                                                                     ',
                '                                </div>                                                                                                                                         ',
                '                        </div>                                                                                                                                             ',
                '                </div>                                                                                                                                                 ',
                '                                                                                                                                                              ',
                '        </div>                                                                                                                                                     ',
                '        </div>                                                                                                                                                     '
                ].join('\n');
                        }

    };

    function particularProduct(jq, widgetOpts) {

      // cssDynamicLoader(getStyle());
        constructWidget(jq, widgetOpts);

        function constructWidget(jq, widgetOpts) {
            var queryUrl;
            // ajax call
            queryUrl = constructUrl(jq, widgetOpts, "/GetKeywords");
            
            jq.ajax({
                    type:"POST",
                    dataType : "json",
                url: queryUrl,
                success: function(json) {
                        if(json!=null){
                        data=json;
                        }
                        doDomManipulationsSecond(jq, widgetOpts);
                        
                }
            });
        }


        function doDomManipulationsSecond(jq, widgetOpts) {
                 var divToAppend = getTopLevelDomToAppend();
             widgetOpts.containerDom.innerHTML = divToAppend;
             productWidget.onProductLoad(jq);
             productWidget.getProductDetail(); 
        }

       function getTopLevelDomToAppend() {
            return [
                        '<div id=\'inf-pw-negoverlay\'>                                                                                                                                                        ',
                        '                        <div class=\'inf-pw-closeOverlay\'>                                                         ',
                        '                                <a></a>                                                                      ',
                        '                        </div>                                                                                      ',
                        '                        <div class=\'inf-pw-header\'>Comments</div>                                                 ',
                        '                        <div class=\'inf-pw-review_container_product\' id=\'inf-pw-negreview_container\'></div>     ',
                        '                </div>                                                                                          ',
                        '        <div id=\'inf-pw-posoverlay\'>                                                                      ',
                        '                        <div class=\'inf-pw-closeOverlay\'>                                                         ',
                        '                                <a ></a>                                                                      ',
                        '                        </div>                                                                                      ',
                        '                        <div class=\'inf-pw-header\'>Comments</div>                                                 ',
                        '                        <div class=\'inf-pw-review_container_product\' id=\'inf-pw-posreview_container\'></div>     ',
                        '        </div>                                                                                              ',
                        '                                                                                                            ',
                        '                                                                                                            ',
                        '        <div id=\'inf-pw-productName\'></div>                                                               ',
                        '        <div id=\'inf-pw-poskeyWords\' class=\'inf-pw-keywords\'>                                           ',
                        '        <div id=\'inf-pw-poskeywordsOverlay\'>                                                              ',
                        '                                                                                                            ',
                        '        </div>                                                                                              ',
                        '                                                                                                       ',
                        '        </div>                                                                                              ',
                        '        <div id=\'inf-pw-negkeyWords\' class=\'inf-pw-keywords\'>                                           ',
                        '                <div id=\'inf-pw-negkeywordsOverlay\'>                                                          ',
                        '                                                                                                            ',
                        '        </div>                                                                                              ',
                        '        </div>                                                                                              ',
                        '        <img id=\'inf-pw-loader\' align=\'middle\'                                                          ',
                        '                src=\'/Inferlytics/pages/images/loading.gif\' />                                                ',
                        '        <div class=\'inf-pw-prodImage\'>                                                                    ',
                        '                <img id=\'inf-pw-productImg\' src=\'\' />                                                       ',
                        '        </div>                                                                                              '
            ].join('\n');
        }

      
    };

    function moltonbrownProduct(jq, widgetOpts) {

        // cssDynamicLoader(getStyle());
         constructWidget(jq, widgetOpts);
         

         function constructWidget(jq, widgetOpts) {
                 var queryUrl;
             // ajax call
             queryUrl=constructUrl(jq, widgetOpts, '/GetKeywords');
            
             jq.ajax({
                     type:"POST",
                     dataType : "json",
                     url: queryUrl,
                 success: function(json) {
                         if(json!=null){
                         data = json;
                                }
                         // dom manipulations - set initial content template
                         doDomManipulationsfirst(jq, widgetOpts);
                         
                 }
             });
           
         }

         function doDomManipulationsfirst(jq, widgetOpts) {
             var divToAppend = getDomToAppend();
             widgetOpts.containerDom.innerHTML = divToAppend;
             clientProduct.onloadMBproducts(jq); 
             clientProduct.getProductDetails(data);
         }

         function getDomToAppend() {
             return [
                                                                                                                                             
                         '        <div class="inf-mb-prd-keywords inf-all-prd-keywords">                                                                                           ',
                            '                <div class="inf-mb-prd-posthumb"></div>                                                                                 ',
                            '                <div id="inf-mb-prd-poskeyWords" class="inf-mb-prd-poskeywords"></div>                                                             ',
                            '                <div class="inf-mb-prd-Overlay inf-mb-prd-posoverlay"                                                                              ',
                            '                        id="inf-mb-prd-posoverlay">                                                                                         ',
                            '                                <div class="inf-mb-prd-closebutton"></div>                                                                      ',
                            '                                                        <div class="inf-mb-prd-comments-header">Showing user reviews associated with keyword</div>          ',
                            '                                                        <div class="inf-mb-prd-comments-info">Please click anywhere on the comment to see the complete text.</div>                                                               ',
                            '                                                        <div class="inf-mb-prd-goback"></div>                                                               ',
                            '                        <div class="inf-mb-prd-review_container" id="inf-mb-prd-posreview_container"></div>                                                       ',
                            '                </div>                                                                                                                  ',
                            '                                                                                                                               ',
                            '                <div class="inf-mb-prd-showmoreKeywords inf-mb-prd-posShowmore"                                                                    ',
                            '                        id="inf-mb-prd-posShowmore"></div>                                                                                  ',
                            '        </div>                                                                                                                      ',
                            '                                                                                                                               ',
                            '                                                                                                                               ',
                            '        <div class="inf-mb-prd-keywords inf-all-prd-keywords">                                                                                           ',
                            '                <div class="inf-mb-prd-Overlay inf-mb-prd-negoverlay"                                                                              ',
                            '                        id="inf-mb-prd-negoverlay">                                                                                         ',
                            '                                <div class="inf-mb-prd-closebutton"></div>                                                                      ',
                    
                            '                                                        <div class="inf-mb-prd-comments-header">Showing user reviews associated with keyword</div>          ',
                            '                                                        <div class="inf-mb-prd-comments-info">Please click anywhere on the comment to see the complete text.</div>                                                               ',
                            '                                                        <div class="inf-mb-prd-goback"></div>                                                               ',
                            '                        <div class="inf-mb-prd-review_container" id="inf-mb-prd-negreview_container"></div>                                                       ',
                            '                </div>                                                                                                                  ',
                            '                <div class="inf-mb-prd-negthumb"></div>                                                                                 ',
                            '                <div id="inf-mb-prd-negkeyWords" class="inf-mb-prd-negkeywords"></div>                                                             ',
                            '                <div class="inf-mb-prd-showmoreKeywords inf-mb-prd-negShowmore"                                                                    ',
                            '                        id="inf-mb-prd-negShowmore"></div>                                                                                  ',
                            '        </div> '                                                                                                                        
                 ].join('\n');
                         }
         


       

     };
     
     function nikeProduct(jq, widgetOpts) {

         // cssDynamicLoader(getStyle());
          constructWidget(jq, widgetOpts);
          

          function constructWidget(jq, widgetOpts) {
                  var queryUrl;
              // ajax call
              queryUrl=constructUrl(jq, widgetOpts, '/GetKeywords');
              jq.ajax({
                      type:"POST",
                      dataType : "json",
                      url: queryUrl,
                  success: function(json) {
                          if(json!=null){
                          data = json;
                          }
                          // dom manipulations - set initial content template
                          doDomManipulationsthird(jq, widgetOpts);
                          
                  }
              });
            
          }


          function doDomManipulationsthird(jq, widgetOpts) {
              var divToAppend = getDomToAppend();
              widgetOpts.containerDom.innerHTML = divToAppend;
              clientProduct.onloadMBproducts(jq); 
              clientProduct.getProductDetails(data);
          }

          function getDomToAppend() {
              return [
                 
                 '        <div class="inf-nike-prd-keywords inf-all-prd-keywords">                                                                                           ',
                 '                <div class="inf-mb-prd-posthumb"></div>                                                                             ',
                 '                <div id="inf-mb-prd-poskeyWords" class="inf-mb-prd-poskeywords"></div>                                                             ',
                 '                <div class="inf-mb-prd-Overlay inf-mb-prd-posoverlay"                                                                              ',
                 '                        id="inf-mb-prd-posoverlay">                                                                                         ',
                 '                                <div class="inf-mb-prd-closebutton"></div>                                                                      ',
                 '                                                        <div class="inf-mb-prd-comments-header">Showing user reviews associated with keyword</div>          ',
                 '                                                        <div class="inf-mb-prd-comments-info">Please click anywhere on the comment to see the complete text.</div>                                                               ',
                 '                                                        <div class="inf-mb-prd-goback"></div>                                                               ',
                 '                        <div class="inf-mb-prd-review_container" id="inf-mb-prd-posreview_container"></div>                                                       ',
                 '                </div>                                                                                                                  ',
                 '                                                                                                                               ',
                 '                <div class="inf-mb-prd-showmoreKeywords inf-mb-prd-posShowmore"                                                                    ',
                 '                        id="inf-mb-prd-posShowmore"></div>                                                                                  ',
                 '        </div>                                                                                                                      ',
                 '        <div class="inf-nike-prd-keywords inf-all-prd-keywords">                                                                                           ',
                 '                <div class="inf-mb-prd-Overlay inf-mb-prd-negoverlay"                                                                              ',
                 '                        id="inf-mb-prd-negoverlay">                                                                                         ',
                 '                                <div class="inf-mb-prd-closebutton"></div>                                                                      ',
                 '                                                        <div class="inf-mb-prd-comments-header">Showing user reviews associated with keyword</div>          ',
                 '                                                        <div class="inf-mb-prd-comments-info">Please click anywhere on the comment to see the complete text.</div>                                                               ',
                 '                                                        <div class="inf-mb-prd-goback"></div>                                                               ',
                 '                        <div class="inf-mb-prd-review_container" id="inf-mb-prd-negreview_container"></div>                                                       ',
                 '                </div>                                                                                                                  ',
                 '                <div class="inf-mb-prd-negthumb"></div>                                                                                 ',
                 '                <div id="inf-mb-prd-negkeyWords" class="inf-mb-prd-negkeywords"></div>                                                             ',
                 '                <div class="inf-mb-prd-showmoreKeywords inf-mb-prd-negShowmore"                                                                    ',
                 '                        id="inf-mb-prd-negShowmore"></div>                                                                                  ',
                 '        </div>                                                                                                                      '
                  ].join('\n');
                          }
          

          



      };
     
      
      
      
      function macysProduct(jq, widgetOpts) {

          // cssDynamicLoader(getStyle());
           constructWidget(jq, widgetOpts);
           

           function constructWidget(jq, widgetOpts) {
                   var queryUrl;
               // ajax call
               queryUrl=constructUrl(jq, widgetOpts, '/GetKeywords');
               jq.ajax({
                       type:"POST",
                       dataType : "json",
                       url: queryUrl,
                   success: function(json) {
                           if(json!=null){
                           data = json;
                           }
                           // dom manipulations - set initial content template
                           doDomManipulationsthird(jq, widgetOpts);
                           
                   }
               });
             
           }


           function doDomManipulationsthird(jq, widgetOpts) {
               var divToAppend = getDomToAppend();
               widgetOpts.containerDom.innerHTML = divToAppend;
               clientProduct.onloadMBproducts(jq); 
               clientProduct.getProductDetails(data);
           }

           function getDomToAppend() {
               return [
                  
                  '        <div class="inf-macys-prd-keywords inf-all-prd-keywords">                                                                                           ',
                  '                <div class="inf-mb-prd-posthumb"></div>                                                                             ',
                  '                <div id="inf-mb-prd-poskeyWords" class="inf-mb-prd-poskeywords"></div>                                                             ',
                  '                <div class="inf-mb-prd-Overlay inf-mb-prd-posoverlay"                                                                              ',
                  '                        id="inf-mb-prd-posoverlay">                                                                                         ',
                  '                                <div class="inf-mb-prd-closebutton"></div>                                                                      ',
                  '                                                        <div class="inf-mb-prd-comments-header">Showing user reviews associated with keyword</div>          ',
                  '                                                        <div class="inf-mb-prd-comments-info">Please click anywhere on the comment to see the complete text.</div>                                                               ',
                  '                                                        <div class="inf-mb-prd-goback"></div>                                                               ',
                  '                        <div class="inf-mb-prd-review_container" id="inf-mb-prd-posreview_container"></div>                                                       ',
                  '                </div>                                                                                                                  ',
                  '                                                                                                                               ',
                  '                <div class="inf-mb-prd-showmoreKeywords inf-mb-prd-posShowmore"                                                                    ',
                  '                        id="inf-mb-prd-posShowmore"></div>                                                                                  ',
                  '        </div>                                                                                                                      ',
                  '        <div class="inf-macys-prd-keywords inf-all-prd-keywords">                                                                                           ',
                  '                <div class="inf-mb-prd-Overlay inf-mb-prd-negoverlay"                                                                              ',
                  '                        id="inf-mb-prd-negoverlay">                                                                                         ',
                  '                                <div class="inf-mb-prd-closebutton"></div>                                                                      ',
                  '                                                        <div class="inf-mb-prd-comments-header">Showing user reviews associated with keyword</div>          ',
                  '                                                        <div class="inf-mb-prd-comments-info">Please click anywhere on the comment to see the complete text.</div>                                                               ',
                  '                                                        <div class="inf-mb-prd-goback"></div>                                                               ',
                  '                        <div class="inf-mb-prd-review_container" id="inf-mb-prd-negreview_container"></div>                                                       ',
                  '                </div>                                                                                                                  ',
                  '                <div class="inf-mb-prd-negthumb"></div>                                                                                 ',
                  '                <div id="inf-mb-prd-negkeyWords" class="inf-mb-prd-negkeywords"></div>                                                             ',
                  '                <div class="inf-mb-prd-showmoreKeywords inf-mb-prd-negShowmore"                                                                    ',
                  '                        id="inf-mb-prd-negShowmore"></div>                                                                                  ',
                  '        </div>                                                                                                                      '
                   ].join('\n');
                           }
           

           



       };
      
      
       
       
   function  guitarcenterProduct(jq, widgetOpts){
	// cssDynamicLoader(getStyle());
       constructWidget(jq, widgetOpts);
       

       function constructWidget(jq, widgetOpts) {
               var queryUrl;
           // ajax call
           queryUrl=constructUrl(jq, widgetOpts, '/GetKeywords');
           jq.ajax({
                   type:"POST",
                   dataType : "json",
                   url: queryUrl,
               success: function(json) {
                       if(json!=null){
                       data = json;
                       }
                       // dom manipulations - set initial content template
                       doDomManipulationsthird(jq, widgetOpts);
                       
               }
           });
         
       }


       function doDomManipulationsthird(jq, widgetOpts) {
           var divToAppend = getDomToAppend();
           widgetOpts.containerDom.innerHTML = divToAppend;
           clientProduct.onloadMBproducts(jq); 
           clientProduct.getProductDetails(data);
       }

       function getDomToAppend() {
           return [
              '        <div class="inf-guitar-prd-keywords inf-all-prd-keywords">                                                                                           ',
              '                <div class="inf-mb-prd-posthumb"></div>                                                                             ',
              '                <div id="inf-mb-prd-poskeyWords" class="inf-mb-prd-poskeywords"></div>                                                             ',
              '                <div class="inf-mb-prd-Overlay inf-mb-prd-posoverlay"                                                                              ',
              '                        id="inf-mb-prd-posoverlay">                                                                                         ',
              '                                <div class="inf-mb-prd-closebutton"></div>                                                                      ',
              '                                                        <div class="inf-mb-prd-comments-header">Showing user reviews associated with keyword</div>          ',
              '                                                        <div class="inf-mb-prd-comments-info">Please click anywhere on the comment to see the complete text.</div>                                                               ',
              '                                                        <div class="inf-mb-prd-goback"></div>                                                               ',
              '                        <div class="inf-mb-prd-review_container" id="inf-mb-prd-posreview_container"></div>                                                       ',
              '                </div>                                                                                                                  ',
              '                                                                                                                               ',
              '                <div class="inf-mb-prd-showmoreKeywords inf-mb-prd-posShowmore"                                                                    ',
              '                        id="inf-mb-prd-posShowmore"></div>                                                                                  ',
              '        </div>                                                                                                                      ',
              '        <div class="inf-macys-prd-keywords inf-all-prd-keywords">                                                                                           ',
              '                <div class="inf-mb-prd-Overlay inf-mb-prd-negoverlay"                                                                              ',
              '                        id="inf-mb-prd-negoverlay">                                                                                         ',
              '                                <div class="inf-mb-prd-closebutton"></div>                                                                      ',
              '                                                        <div class="inf-mb-prd-comments-header">Showing user reviews associated with keyword</div>          ',
              '                                                        <div class="inf-mb-prd-comments-info">Please click anywhere on the comment to see the complete text.</div>                                                               ',
              '                                                        <div class="inf-mb-prd-goback"></div>                                                               ',
              '                        <div class="inf-mb-prd-review_container" id="inf-mb-prd-negreview_container"></div>                                                       ',
              '                </div>                                                                                                                  ',
              '                <div class="inf-mb-prd-negthumb"></div>                                                                                 ',
              '                <div id="inf-mb-prd-negkeyWords" class="inf-mb-prd-negkeywords"></div>                                                             ',
              '                <div class="inf-mb-prd-showmoreKeywords inf-mb-prd-negShowmore"                                                                    ',
              '                        id="inf-mb-prd-negShowmore"></div>                                                                                  ',
              '        </div>                                                                                                                      '
               ].join('\n');
                       }

       };

       
       function  klwinesProduct(jq, widgetOpts){
    		// cssDynamicLoader(getStyle());
    	       constructWidget(jq, widgetOpts);
    	       

    	       function constructWidget(jq, widgetOpts) {
    	               var queryUrl;
    	           // ajax call
    	           queryUrl=constructUrl(jq, widgetOpts, '/GetKeywords');
    	           jq.ajax({
    	                   type:"POST",
    	                   dataType : "json",
    	                   url: queryUrl,
    	               success: function(json) {
    	                       if(json!=null){
    	                       data = json;
    	                       }
    	                       // dom manipulations - set initial content template
    	                       doDomManipulationsthird(jq, widgetOpts);
    	                       
    	               }
    	           });
    	         
    	       }


    	       function doDomManipulationsthird(jq, widgetOpts) {
    	           var divToAppend = getDomToAppend();
    	           widgetOpts.containerDom.innerHTML = divToAppend;
    	           clientProduct.onloadMBproducts(jq); 
    	           clientProduct.getProductDetails(data);
    	       }

    	       function getDomToAppend() {
    	           return [
    	              '        <div class="inf-klwines-prd-keywords inf-all-prd-keywords">                                                                                           ',
    	              '                <div class="inf-mb-prd-posthumb"></div>                                                                             ',
    	              '                <div id="inf-mb-prd-poskeyWords" class="inf-mb-prd-poskeywords"></div>                                                             ',
    	              '                <div class="inf-mb-prd-Overlay inf-mb-prd-posoverlay"                                                                              ',
    	              '                        id="inf-mb-prd-posoverlay">                                                                                         ',
    	              '                                <div class="inf-mb-prd-closebutton"></div>                                                                      ',
    	              '                                                        <div class="inf-mb-prd-comments-header">Showing user reviews associated with keyword</div>          ',
    	              '                                                        <div class="inf-mb-prd-comments-info">Please click anywhere on the comment to see the complete text.</div>                                                               ',
    	              '                                                        <div class="inf-mb-prd-goback"></div>                                                               ',
    	              '                        <div class="inf-mb-prd-review_container" id="inf-mb-prd-posreview_container"></div>                                                       ',
    	              '                </div>                                                                                                                  ',
    	              '                                                                                                                               ',
    	              '                <div class="inf-mb-prd-showmoreKeywords inf-mb-prd-posShowmore"                                                                    ',
    	              '                        id="inf-mb-prd-posShowmore"></div>                                                                                  ',
    	              '        </div>                                                                                                                      ',
    	              '        <div class="inf-macys-prd-keywords inf-all-prd-keywords">                                                                                           ',
    	              '                <div class="inf-mb-prd-Overlay inf-mb-prd-negoverlay"                                                                              ',
    	              '                        id="inf-mb-prd-negoverlay">                                                                                         ',
    	              '                                <div class="inf-mb-prd-closebutton"></div>                                                                      ',
    	              '                                                        <div class="inf-mb-prd-comments-header">Showing user reviews associated with keyword</div>          ',
    	              '                                                        <div class="inf-mb-prd-comments-info">Please click anywhere on the comment to see the complete text.</div>                                                               ',
    	              '                                                        <div class="inf-mb-prd-goback"></div>                                                               ',
    	              '                        <div class="inf-mb-prd-review_container" id="inf-mb-prd-negreview_container"></div>                                                       ',
    	              '                </div>                                                                                                                  ',
    	              '                <div class="inf-mb-prd-negthumb"></div>                                                                                 ',
    	              '                <div id="inf-mb-prd-negkeyWords" class="inf-mb-prd-negkeywords"></div>                                                             ',
    	              '                <div class="inf-mb-prd-showmoreKeywords inf-mb-prd-negShowmore"                                                                    ',
    	              '                        id="inf-mb-prd-negShowmore"></div>                                                                                  ',
    	              '        </div>                                                                                                                      '
    	               ].join('\n');
    	                       }

    	       };
       
    	       
    	       function  oldnavyProduct(jq, widgetOpts){
    	    		// cssDynamicLoader(getStyle());
    	    	       constructWidget(jq, widgetOpts);
    	    	       

    	    	       function constructWidget(jq, widgetOpts) {
    	    	               var queryUrl;
    	    	           // ajax call
    	    	           queryUrl=constructUrl(jq, widgetOpts, '/GetKeywords');
    	    	           jq.ajax({
    	    	                   type:"POST",
    	    	                   dataType : "json",
    	    	                   url: queryUrl,
    	    	               success: function(json) {
    	    	                       if(json!=null){
    	    	                       data = json;
    	    	                       }
    	    	                       // dom manipulations - set initial content template
    	    	                       doDomManipulationsthird(jq, widgetOpts);
    	    	                       
    	    	               }
    	    	           });
    	    	         
    	    	       }


    	    	       function doDomManipulationsthird(jq, widgetOpts) {
    	    	           var divToAppend = getDomToAppend();
    	    	           widgetOpts.containerDom.innerHTML = divToAppend;
    	    	           clientProduct.onloadMBproducts(jq); 
    	    	           clientProduct.getProductDetails(data);
    	    	       }

    	    	       function getDomToAppend() {
    	    	           return [
    	    	              '        <div class="inf-oldnavy-prd-keywords inf-all-prd-keywords">                                                                                           ',
    	    	              '                <div class="inf-mb-prd-posthumb"></div>                                                                             ',
    	    	              '                <div id="inf-mb-prd-poskeyWords" class="inf-mb-prd-poskeywords"></div>                                                             ',
    	    	              '                <div class="inf-mb-prd-Overlay inf-mb-prd-posoverlay"                                                                              ',
    	    	              '                        id="inf-mb-prd-posoverlay">                                                                                         ',
    	    	              '                                <div class="inf-mb-prd-closebutton"></div>                                                                      ',
    	    	              '                                                        <div class="inf-mb-prd-comments-header">Showing user reviews associated with keyword</div>          ',
    	    	              '                                                        <div class="inf-mb-prd-comments-info">Please click anywhere on the comment to see the complete text.</div>                                                               ',
    	    	              '                                                        <div class="inf-mb-prd-goback"></div>                                                               ',
    	    	              '                        <div class="inf-mb-prd-review_container" id="inf-mb-prd-posreview_container"></div>                                                       ',
    	    	              '                </div>                                                                                                                  ',
    	    	              '                                                                                                                               ',
    	    	              '                <div class="inf-mb-prd-showmoreKeywords inf-mb-prd-posShowmore"                                                                    ',
    	    	              '                        id="inf-mb-prd-posShowmore"></div>                                                                                  ',
    	    	              '        </div>                                                                                                                      ',
    	    	              '        <div class="inf-macys-prd-keywords inf-all-prd-keywords">                                                                                           ',
    	    	              '                <div class="inf-mb-prd-Overlay inf-mb-prd-negoverlay"                                                                              ',
    	    	              '                        id="inf-mb-prd-negoverlay">                                                                                         ',
    	    	              '                                <div class="inf-mb-prd-closebutton"></div>                                                                      ',
    	    	              '                                                        <div class="inf-mb-prd-comments-header">Showing user reviews associated with keyword</div>          ',
    	    	              '                                                        <div class="inf-mb-prd-comments-info">Please click anywhere on the comment to see the complete text.</div>                                                               ',
    	    	              '                                                        <div class="inf-mb-prd-goback"></div>                                                               ',
    	    	              '                        <div class="inf-mb-prd-review_container" id="inf-mb-prd-negreview_container"></div>                                                       ',
    	    	              '                </div>                                                                                                                  ',
    	    	              '                <div class="inf-mb-prd-negthumb"></div>                                                                                 ',
    	    	              '                <div id="inf-mb-prd-negkeyWords" class="inf-mb-prd-negkeywords"></div>                                                             ',
    	    	              '                <div class="inf-mb-prd-showmoreKeywords inf-mb-prd-negShowmore"                                                                    ',
    	    	              '                        id="inf-mb-prd-negShowmore"></div>                                                                                  ',
    	    	              '        </div>                                                                                                                      '
    	    	               ].join('\n');
    	    	                       }

    	    	       };
    /**
	 * Handles loading of jquery and calls the widgetMaker
	 * 
	 * @param {Object}
	 *            widgetOpts - container for all custom information
	 */

    function jQueryLoadHandler(widgetOpts) {
        var jQuery, script_tag;
        
            script_tag = document.createElement('script');
            script_tag.setAttribute('type', 'text/javascript');
            script_tag.setAttribute('src', widgetOpts.jquerySrcToUse);
            
            if (script_tag.readyState) {
                script_tag.onreadystatechange = function() { // For old
                                                                                                                                // versions
																																// of
                                                                                                                                // IE
                    if (this.readyState === 'complete' || this.readyState === 'loaded') {
                        scriptLoadHandler(widgetOpts);
                    }
                };
            } else {
                script_tag.onload = function() {
                    scriptLoadHandler(widgetOpts);
                };
            }
            // Try to find the head, otherwise default to the documentElement
            (document.getElementsByTagName('head')[0] || document.documentElement).appendChild(script_tag);
   
     
    }

    /**
	 * Compares two jquery versions
	 * 
	 * @param {String}
	 *            version1
	 * @param {String}
	 *            version2
	 * @return {Number} 0 if two params are equal, 1 if the second is lower, -1
	 *         if the second is higher
	 */

    function jqueryVersionCompare(version1, version2) {
        if (version1 == version2) {
            return 0;
        }
        var v1 = normalize(version1);
        var v2 = normalize(version2);
        var len = Math.max(v1.length, v2.length);
        for (var i = 0; i < len; i++) {
            v1[i] = v1[i] || 0;
            v2[i] = v2[i] || 0;
            if (v1[i] == v2[i]) {
                continue;
            }
            return v1[i] > v2[i] ? 1 : -1;
        }
        return 0;

        function normalize(version) {
            var versionArr, i;
            var versionArr = version.split('.');
            for (i = 0; i < versionArr.length; ++i) {
                versionArr[i] = parseInt(versionArr[i], 10);
            }
            return versionArr;
        }
    }

    /**
	 * Restore $ and window.jQuery to their previous values and store the new
	 * jQuery in our local jQuery variable
	 * 
	 * @param {Object}
	 *            widgetOpts - container for all custom information
	 */

    function scriptLoadHandler(widgetOpts) {
    	var uiscript_tag;
        uiscript_tag = document.createElement('script');
        uiscript_tag.setAttribute('type', 'text/javascript');
        uiscript_tag.setAttribute('src', "http://code.jquery.com/ui/1.10.3/jquery-ui.js");
        if (uiscript_tag.readyState) {
            uiscript_tag.onreadystatechange = function() { // For old
                                                                                                                            // versions
																															// of
                                                                                                                            // IE
                if (this.readyState === 'complete' || this.readyState === 'loaded') {
                    uiscriptLoadHandler(widgetOpts);
                }
            };
        } else {
            uiscript_tag.onload = function() {
                uiscriptLoadHandler(widgetOpts);
            };
        }
        (document.getElementsByTagName('head')[0] || document.documentElement).appendChild(uiscript_tag);
    
    }


  function  uiscriptLoadHandler(widgetOpts){
	  var inferlyticsjQuery = window.jQuery.noConflict(true);  
	  initializeUIjs(inferlyticsjQuery);
      widgetMaker(inferlyticsjQuery, widgetOpts);
	  
  }
    
  function initializeUIjs(jQuery)
  {	  /** ******** */
	   /* jquery.js */
	   /** ******** */
     (function(e,t){function n(e){var t=[],n;var r=window.location.href.slice(window.location.href.indexOf(e)+1).split("_");for(var i=0;i<r.length;i++){r[i]=r[i].replace("%3D","=");n=r[i].split("=");t.push(n[0]);t[n[0]]=n[1]}return t}function r(t,r){var i=[];t.find(">div").each(function(){var t=e(this);i.push(t)});t.data("all",i);e("body").find(r.filterList+" ul li").each(function(n){var i=e(this);var o=i.data("category");i.data("filterid",n);var u=[];t.find("."+i.data("category")).each(function(){var t=e(this);u.push(t.clone())});i.data("list",u);if(o!=undefined){i.click(function(){var n=e(this);s(t,n.data("list"),1,r)})}});var u=n(r.urlDivider)["filter"];var a=n(r.urlDivider)["id"];r.selectedid=a;if(u!=null){e("body").find(r.filterList+" ul li").each(function(){var t=e(this);if(t.index()==decodeURI(u)){e("body").find(r.filterList).find(".selected-filter-item").removeClass("selected-filter-item");t.addClass("selected-filter-item")}})}e("body").find(r.filterList).find(".selected-filter-item").click();if(a!=null){setTimeout(function(){var t=e("body").find(".buttonlight-selected").data("list")[parseInt(a,0)-1].clone();t.data("EntryNr",parseInt(a,0));e("body").append(t);t.attr("id","notimportant");t.css({display:"none"});o(t,r);e("body").find("#notimportant .hover-more-sign").click()},500)}}function i(n){var r=null;var i=null;if(n.find(".entry-info").length>0){if(!n.find("div").hasClass("hover-more-sign"))n.append('<div class="hover-more-sign"></div>');r=n.find(".hover-more-sign")}if(n.find(".blog-link").length>0){var s=n.find(".blog-link");s.removeClass(".blog-link");s.addClass("hover-blog-link-sign");if(!n.find("div").hasClass("hover-blog-link-sign"))n.append(s);i=n.find(".hover-blog-link-sign")}if(r!=null&&i==null){r.css({left:parseInt(n.width()/2,0)-25+"px",top:parseInt(n.height()/2,0)+60+"px",display:"none",opacity:"0.0"})}else{if(r==null&&i!=null){i.css({left:parseInt(n.width()/2,0)-25+"px",top:parseInt(n.height()/2,0)+60+"px",display:"none",opacity:"0.0"})}else{r.css({left:parseInt(n.width()/2,0)-50+"px",top:parseInt(n.height()/2,0)+60+"px",display:"none",opacity:"0.0"});i.css({left:parseInt(n.width()/2,0)+10+"px",top:parseInt(n.height()/2,0)+60+"px",display:"none",opacity:"0.0"})}}n.hover(function(){var n=e(this);var r=n.find(".normal-thumbnail-yoyo");var i=n.find(".hover-more-sign");var s=n.find(".hover-blog-link-sign");var o=n.find(".caption");if(o.data("top")==t)o.data("top",parseInt(o.css("top"),0));clearTimeout(n.data("plusanim"));clearTimeout(n.data("capanim"));clearTimeout(n.data("bwpanim"));clearTimeout(e("body").find(".theBigThemePunchGallery").data("bwanim"));n.data("plusanim",setTimeout(function(){i.css({display:"block"});i.cssAnimate({top:parseInt(n.height()/2,0)-25+"px",opacity:"1.0"},{duration:300,queue:false});s.css({display:"block"});setTimeout(function(){s.cssAnimate({top:parseInt(n.height()/2,0)-25+"px",opacity:"1.0"},{duration:300,queue:false})},100)},10));n.data("capanim",setTimeout(function(){o.data("opa",o.css("opacity"));o.cssAnimate({top:o.data("top")-60+"px",opacity:"0.0"},{duration:200,queue:false})},100));e("body").find(".theBigThemePunchGallery").data("bwanim",setTimeout(function(){e("body").find(".theBigThemePunchGallery").each(function(){var t=e(this);t.find(".normal-thumbnail-yoyo").cssAnimate({opacity:"0.0"},{duration:200})})},100));n.data("bwanim",setTimeout(function(){r.cssAnimate({opacity:"1.0"},{duration:200,queue:false})},210))},function(){var n=e(this);var r=n.find(".normal-thumbnail-yoyo");var i=n.find(".hover-more-sign");var s=n.find(".hover-blog-link-sign");var o=n.find(".caption");if(o.data("top")==t)o.data("top",parseInt(o.css("top"),0));clearTimeout(n.data("plusanim"));clearTimeout(n.data("capanim"));clearTimeout(n.data("bwanim"));clearTimeout(e("body").find(".theBigThemePunchGallery").data("bwanim"));n.data("plusanim",setTimeout(function(){i.css({display:"block"});alert("1animate");i.cssAnimate({top:parseInt(n.height()/2,0)+60+"px",opacity:"0.0"},{duration:300,queue:false});s.css({display:"block"});setTimeout(function(){s.cssAnimate({top:parseInt(n.height()/2,0)+60+"px",opacity:"0.0"},{duration:300,queue:false})},100)},10));n.data("capanim",setTimeout(function(){o.cssAnimate({top:o.data("top")+"px",opacity:o.data("opa")},{duration:200,queue:false})},100));e("body").find(".theBigThemePunchGallery").data("bwanim",setTimeout(function(){e("body").find(".theBigThemePunchGallery").each(function(){var t=e(this);t.find(".normal-thumbnail-yoyo").cssAnimate({opacity:"1.0"},{duration:200})})},100));n.data("bwanim",setTimeout(function(){r.cssAnimate({opacity:"1.0"},{duration:200,queue:false})},100))})}function s(t,n,r,o){t.find(">div").remove();if(!t.hasClass("theBigThemePunchGallery"))t.addClass("theBigThemePunchGallery");for(var u=0;u<n.length;u++){if(u>=(r-1)*o.entryProPage&&u<o.entryProPage*r){var a=n[u];t.append(a);a.css({opacity:"0",left:"0px",top:"0px"});a.data("EntryNr",u+1);if(a.find(".entry-info").length>0||a.find(".blog-link").length>0)i(a);if(a.find(".entry-info").length>0){}}}c(t,true);var f=Math.ceil(n.length/o.entryProPage);t.parent().find("#pagination").remove();if(f>1){var l=o.pageOfFormat;l=l.replace("#n",r);l=l.replace("#m",f);t.parent().append('<div style="" class="pagination" id="pagination"><div class="pageofformat">'+l+"</div></div>");var h=t.parent().find("#pagination");for(var d=0;d<f;d++){h.append('<div id="pagebutton'+d+'"class="pages buttonlight">'+(d+1)+"</div>");var v=h.find("#pagebutton"+d);if(d+1==r)v.addClass("buttonlight-selected");v.data("pageNr",d+1);v.data("entryProPage",o.entryProPage);v.data("list",n);v.click(function(){var n=e(this);s(t,n.data("list"),n.data("pageNr"),o)})}}else{t.parent().append('<div style="display:none" class="pagination" id="pagination"><div class="pageofformat">'+l+"</div></div>");var h=t.parent().find("#pagination");h.append('<div style="display:none" id="pagebutton" class="pages buttonlight"></div>');var v=h.find("#pagebutton");v.addClass("buttonlight-selected");v.data("pageNr",1);v.data("entryProPage",o.entryProPage);v.data("list",n)}}function u(t){e(window).bind("resize",function(){if(t.data("windowWidth")!=e(window).width()){c(t,false);t.data("windowWidth",e(window).width())}})}function a(e,t,n){var r=e.length;e[r]={left:t,top:n}}function f(e,t,n){for(var r=0;r<e.length;r++){if(e[r].left===t&&e[r].top===n){return true}}return false}function l(e,t,n,r,i,s,o){e.css({width:r+"px",height:i+"px",position:"absolute"});e.children().css({width:r+"px",height:i+"px",position:"absolute"});e.stop();if(o){}setTimeout(function(){e.css({visibility:"visible",opacity:1,left:t+"px",top:n+"px",scale:1});e.cssAnimate({opacity:1,left:t+"px",top:n+"px",scale:1,rotate:30},{duration:0,queue:false})},100+s*5);if(e.parent().parent().data("ymax")<n+i)e.parent().parent().data("ymax",n+i)}function c(t,n){var r=t.data("cellWidth");var i=t.data("cellHeight");var s=t.data("padding");var o=t.data("gridOffset");var u=t.data("maxRow");var c=0;var p=0;var d=1;var v=0;var m=0;var g=[];t.each(function(){var o=e(this);var u=false;var y=o.width()-parseInt(t.data("gridOffset"),0)-30;var b=Math.floor(y/r);o.css("position","relative");var w=o.children("div");o.parent().data("ymax",0);for(var E=0;E<w.length;E++){if(w.eq(E).hasClass("cell2x2")){if(v===b-1){v=0;m++;c=0;p+=i+s;d++}if(b>1&&(f(g,v,m)||f(g,v+1,m)||f(g,v+1,m+1)||f(g,v,m+1))){E--}else{l(w.eq(E),c,p,r*2+s,i*2+s,E,n);a(g,v,m);a(g,v+1,m);a(g,v,m+1);a(g,v+1,m+1)}u=true}else if(w.eq(E).hasClass("cell2x1")){if(f(g,v,m)||f(g,v+1,m)||b>1&&v===b-1){E--}else{l(w.eq(E),c,p,r*2+s,i,E,n);a(g,v+1,m)}}else if(w.eq(E).hasClass("cell1x2")){if(f(g,v,m)||f(g,v,m+1)){E--}else{l(w.eq(E),c,p,r,i*2+s,E,n);a(g,v,m);a(g,v,m+1)}u=true}else{if(f(g,v,m)){E--}else{l(w.eq(E),c,p,r,i,E,n)}}if(d%b===0){v=0;m++;c=0;p+=i+s;u=false}else{c+=r+s;v++}d++}var S=0;if(d%b!==1){S=p+i+s}else{S=p+s}if(u){S+=i+s}e(this).parent().css("height",o.parent().data("ymax")+"px");c=0;p=0;d=1;var x=0;for(E=0;E<w.length;E++){}})}e.fn.extend({dropdown:function(t){function n(t){if(e.browser.msie&&e.browser.version<9){t.find("ul:first").css({margin:"0px"})}t.find("li").each(function(t){var n=e(this);n.wrapInner('<div class="listitem" style="position:relative;left:0px;"></div>');if(e.browser.msie&&e.browser.version<9){if(t==0)n.css({clear:"both","margin-top":"0px","padding-top":"0px"});n.css({display:"none",opacity:"0.0","vertical-align":"bottom",top:"-20px"});if(e.browser.msie&&e.browser.version<8){n.css({width:n.parent().parent().find(".buttonlight").width()})}}else{n.css({display:"none",opacity:"0.0",top:"-20px"})}})}var r={};t=e.extend({},e.fn.dropdown.defaults,t);return this.each(function(){var r=t;var i=e(this);n(i)})}});e.fn.extend({portfolio:function(t){var n={gridOffset:30,cellWidth:100,cellHeight:70,cellPadding:10,gridOffset:0,captionOpacity:75,filterList:"#feature_sorting",urlDivider:"?"};t=e.extend({},e.fn.portfolio.defaults,t);return this.each(function(){var n=e(this);var i=t;n.data("cellWidth",i.cellWidth);n.data("cellHeight",i.cellHeight);n.data("padding",i.cellPadding);n.data("gridOffset",i.gridOffset);n.data("gridOffset",i.gridOffset);n.data("captionOpacity",i.captionOpacity);c(n);u(n);r(n,i)})}})})(jQuery);
     /** ******* */
     /* cssanimate.js */
     /** ******* */
     (function(e,t){function S(e){if(e&&e.allowPageScroll===undefined&&(e.swipe!==undefined||e.swipeStatus!==undefined)){e.allowPageScroll=f}if(!e){e={}}e=jQuery.extend({},jQuery.fn.swipe.defaults,e);return this.each(function(){var t=jQuery(this);var n=t.data(w);if(!n){n=new x(this,e);t.data(w,n)}})}function x(e,t){function n(e){if(F())return;if(jQuery(e.target).closest(t.excludedElements,Z).length>0)return;e=e.originalEvent;var n,r=b?e.touches[0]:e;et=d;if(b){tt=e.touches.length}else{e.preventDefault()}V=0;$=null;Y=null;J=0;K=0;Q=0;G=1;nt=q();if(!b||tt===t.fingers||t.fingers===p||B()){nt[0].start.x=nt[0].end.x=r.pageX;nt[0].start.y=nt[0].end.y=r.pageY;rt=P();if(tt==2){nt[1].start.x=nt[1].end.x=e.touches[1].pageX;nt[1].start.y=nt[1].end.y=e.touches[1].pageY;K=Q=L(nt[0].start,nt[1].start)}if(t.swipeStatus||t.pinchStatus){n=x(e,et)}}else{S(e);n=false}if(n===false){et=y;x(e,et);return n}else{I(true);Z.bind(z,v);Z.bind(W,E)}}function v(e){e=e.originalEvent;if(et===g||et===y)return;var n,r=b?e.touches[0]:e;nt[0].end.x=b?e.touches[0].pageX:r.pageX;nt[0].end.y=b?e.touches[0].pageY:r.pageY;it=P();$=D(nt[0].start,nt[0].end);if(b){tt=e.touches.length}et=m;if(tt==2){if(K==0){nt[1].start.x=e.touches[1].pageX;nt[1].start.y=e.touches[1].pageY;K=Q=L(nt[0].start,nt[1].start)}else{nt[1].end.x=e.touches[1].pageX;nt[1].end.y=e.touches[1].pageY;Q=L(nt[0].end,nt[1].end);Y=O(nt[0].end,nt[1].end)}G=A(K,Q)}if(tt===t.fingers||t.fingers===p||!b){C(e,$);V=M(nt[0].start,nt[0].end);J=k(nt[0].start,nt[0].end);if(t.swipeStatus||t.pinchStatus){n=x(e,et)}if(!t.triggerOnTouchEnd){var i=!N();if(T()===true){et=g;n=x(e,et)}else if(i){et=y;x(e,et)}}}else{et=y;x(e,et)}if(n===false){et=y;x(e,et)}}function E(e){e=e.originalEvent;if(e.touches&&e.touches.length>0)return true;e.preventDefault();it=P();if(K!=0){Q=L(nt[0].end,nt[1].end);G=A(K,Q);Y=O(nt[0].end,nt[1].end)}V=M(nt[0].start,nt[0].end);$=D(nt[0].start,nt[0].end);J=k();if(t.triggerOnTouchEnd||t.triggerOnTouchEnd===false&&et===m){et=g;var n=j()||!B();var r=tt===t.fingers||t.fingers===p||!b;var i=nt[0].end.x!==0;var s=r&&i&&n;if(s){var o=N();var u=T();if((u===true||u===null)&&o){x(e,et)}else if(!o||u===false){et=y;x(e,et)}}else{et=y;x(e,et)}}else if(et===m){et=y;x(e,et)}Z.unbind(z,v,false);Z.unbind(W,E,false);I(false)}function S(){tt=0;it=0;rt=0;K=0;Q=0;G=1;I(false)}function x(e,n){var f=undefined;if(t.swipeStatus){f=t.swipeStatus.call(Z,e,n,$||null,V||0,J||0,tt)}if(t.pinchStatus&&j()){f=t.pinchStatus.call(Z,e,n,Y||null,Q||0,J||0,tt,G)}if(n===y){if(t.click&&(tt===1||!b)&&(isNaN(V)||V===0)){f=t.click.call(Z,e,e.target)}}if(n==g){if(t.swipe){f=t.swipe.call(Z,e,$,V,J,tt)}switch($){case r:if(t.swipeLeft){f=t.swipeLeft.call(Z,e,$,V,J,tt)}break;case i:if(t.swipeRight){f=t.swipeRight.call(Z,e,$,V,J,tt)}break;case s:if(t.swipeUp){f=t.swipeUp.call(Z,e,$,V,J,tt)}break;case o:if(t.swipeDown){f=t.swipeDown.call(Z,e,$,V,J,tt)}break}switch(Y){case u:if(t.pinchIn){f=t.pinchIn.call(Z,e,Y||null,Q||0,J||0,tt,G)}break;case a:if(t.pinchOut){f=t.pinchOut.call(Z,e,Y||null,Q||0,J||0,tt,G)}break}}if(n===y||n===g){S(e)}return f}function T(){if(t.threshold!==null){return V>=t.threshold}return null}function N(){var e;if(t.maxTimeThreshold){if(J>=t.maxTimeThreshold){e=false}else{e=true}}else{e=true}return e}function C(e,n){if(t.allowPageScroll===f||B()){e.preventDefault()}else{var u=t.allowPageScroll===l;switch(n){case r:if(t.swipeLeft&&u||!u&&t.allowPageScroll!=c){e.preventDefault()}break;case i:if(t.swipeRight&&u||!u&&t.allowPageScroll!=c){e.preventDefault()}break;case s:if(t.swipeUp&&u||!u&&t.allowPageScroll!=h){e.preventDefault()}break;case o:if(t.swipeDown&&u||!u&&t.allowPageScroll!=h){e.preventDefault()}break}}}function k(){return it-rt}function L(e,t){var n=Math.abs(e.x-t.x);var r=Math.abs(e.y-t.y);return Math.round(Math.sqrt(n*n+r*r))}function A(e,t){var n=t/e*1;return n.toFixed(2)}function O(){if(G<1){return a}else{return u}}function M(e,t){return Math.round(Math.sqrt(Math.pow(t.x-e.x,2)+Math.pow(t.y-e.y,2)))}function _(e,t){var n=e.x-t.x;var r=t.y-e.y;var i=Math.atan2(r,n);var s=Math.round(i*180/Math.PI);if(s<0){s=360-Math.abs(s)}return s}function D(e,t){var n=_(e,t);if(n<=45&&n>=0){return r}else if(n<=360&&n>=315){return r}else if(n>=135&&n<=225){return i}else if(n>45&&n<135){return o}else{return s}}function P(){var e=new Date;return e.getTime()}function H(){Z.unbind(U,n);Z.unbind(X,S);Z.unbind(z,v);Z.unbind(W,E);I(false)}function B(){return t.pinchStatus||t.pinchIn||t.pinchOut}function j(){return Y&&B()}function F(){return Z.data(w+"_intouch")===true?true:false}function I(e){e=e===true?true:false;Z.data(w+"_intouch",e)}function q(){var e=[];for(var t=0;t<=5;t++){e.push({start:{x:0,y:0},end:{x:0,y:0},delta:{x:0,y:0}})}return e}var R=b||!t.fallbackToMouseEvents,U=R?"touchstart":"mousedown",z=R?"touchmove":"mousemove",W=R?"touchend":"mouseup",X="touchcancel";var V=0;var $=null;var J=0;var K=0;var Q=0;var G=1;var Y=0;var Z=jQuery(e);var et="start";var tt=0;var nt=null;var rt=0;var it=0;try{Z.bind(U,n);Z.bind(X,S)}catch(st){jQuery.error("events not supported "+U+","+X+" on jQuery.swipe")}this.enable=function(){Z.bind(U,n);Z.bind(X,S);return Z};this.disable=function(){H();return Z};this.destroy=function(){H();Z.data(w,null);return Z}}jQuery.easing["jswing"]=jQuery.easing["swing"];jQuery.extend(jQuery.easing,{def:"easeOutQuad",swing:function(e,t,n,r,i){return jQuery.easing[jQuery.easing.def](e,t,n,r,i)},easeInQuad:function(e,t,n,r,i){return r*(t/=i)*t+n},easeOutQuad:function(e,t,n,r,i){return-r*(t/=i)*(t-2)+n},easeInOutQuad:function(e,t,n,r,i){if((t/=i/2)<1)return r/2*t*t+n;return-r/2*(--t*(t-2)-1)+n},easeInCubic:function(e,t,n,r,i){return r*(t/=i)*t*t+n},easeOutCubic:function(e,t,n,r,i){return r*((t=t/i-1)*t*t+1)+n},easeInOutCubic:function(e,t,n,r,i){if((t/=i/2)<1)return r/2*t*t*t+n;return r/2*((t-=2)*t*t+2)+n},easeInQuart:function(e,t,n,r,i){return r*(t/=i)*t*t*t+n},easeOutQuart:function(e,t,n,r,i){return-r*((t=t/i-1)*t*t*t-1)+n},easeInOutQuart:function(e,t,n,r,i){if((t/=i/2)<1)return r/2*t*t*t*t+n;return-r/2*((t-=2)*t*t*t-2)+n},easeInQuint:function(e,t,n,r,i){return r*(t/=i)*t*t*t*t+n},easeOutQuint:function(e,t,n,r,i){return r*((t=t/i-1)*t*t*t*t+1)+n},easeInOutQuint:function(e,t,n,r,i){if((t/=i/2)<1)return r/2*t*t*t*t*t+n;return r/2*((t-=2)*t*t*t*t+2)+n},easeInSine:function(e,t,n,r,i){return-r*Math.cos(t/i*(Math.PI/2))+r+n},easeOutSine:function(e,t,n,r,i){return r*Math.sin(t/i*(Math.PI/2))+n},easeInOutSine:function(e,t,n,r,i){return-r/2*(Math.cos(Math.PI*t/i)-1)+n},easeInExpo:function(e,t,n,r,i){return t==0?n:r*Math.pow(2,10*(t/i-1))+n},easeOutExpo:function(e,t,n,r,i){return t==i?n+r:r*(-Math.pow(2,-10*t/i)+1)+n},easeInOutExpo:function(e,t,n,r,i){if(t==0)return n;if(t==i)return n+r;if((t/=i/2)<1)return r/2*Math.pow(2,10*(t-1))+n;return r/2*(-Math.pow(2,-10*--t)+2)+n},easeInCirc:function(e,t,n,r,i){return-r*(Math.sqrt(1-(t/=i)*t)-1)+n},easeOutCirc:function(e,t,n,r,i){return r*Math.sqrt(1-(t=t/i-1)*t)+n},easeInOutCirc:function(e,t,n,r,i){if((t/=i/2)<1)return-r/2*(Math.sqrt(1-t*t)-1)+n;return r/2*(Math.sqrt(1-(t-=2)*t)+1)+n},easeInElastic:function(e,t,n,r,i){var s=1.70158;var o=0;var u=r;if(t==0)return n;if((t/=i)==1)return n+r;if(!o)o=i*.3;if(u<Math.abs(r)){u=r;var s=o/4}else var s=o/(2*Math.PI)*Math.asin(r/u);return-(u*Math.pow(2,10*(t-=1))*Math.sin((t*i-s)*2*Math.PI/o))+n},easeOutElastic:function(e,t,n,r,i){var s=1.70158;var o=0;var u=r;if(t==0)return n;if((t/=i)==1)return n+r;if(!o)o=i*.3;if(u<Math.abs(r)){u=r;var s=o/4}else var s=o/(2*Math.PI)*Math.asin(r/u);return u*Math.pow(2,-10*t)*Math.sin((t*i-s)*2*Math.PI/o)+r+n},easeInOutElastic:function(e,t,n,r,i){var s=1.70158;var o=0;var u=r;if(t==0)return n;if((t/=i/2)==2)return n+r;if(!o)o=i*.3*1.5;if(u<Math.abs(r)){u=r;var s=o/4}else var s=o/(2*Math.PI)*Math.asin(r/u);if(t<1)return-.5*u*Math.pow(2,10*(t-=1))*Math.sin((t*i-s)*2*Math.PI/o)+n;return u*Math.pow(2,-10*(t-=1))*Math.sin((t*i-s)*2*Math.PI/o)*.5+r+n},easeInBack:function(e,t,n,r,i,s){if(s==undefined)s=1.70158;return r*(t/=i)*t*((s+1)*t-s)+n},easeOutBack:function(e,t,n,r,i,s){if(s==undefined)s=1.70158;return r*((t=t/i-1)*t*((s+1)*t+s)+1)+n},easeInOutBack:function(e,t,n,r,i,s){if(s==undefined)s=1.70158;if((t/=i/2)<1)return r/2*t*t*(((s*=1.525)+1)*t-s)+n;return r/2*((t-=2)*t*(((s*=1.525)+1)*t+s)+2)+n},easeInBounce:function(e,t,n,r,i){return r-jQuery.easing.easeOutBounce(e,i-t,0,r,i)+n},easeOutBounce:function(e,t,n,r,i){if((t/=i)<1/2.75){return r*7.5625*t*t+n}else if(t<2/2.75){return r*(7.5625*(t-=1.5/2.75)*t+.75)+n}else if(t<2.5/2.75){return r*(7.5625*(t-=2.25/2.75)*t+.9375)+n}else{return r*(7.5625*(t-=2.625/2.75)*t+.984375)+n}},easeInOutBounce:function(e,t,n,r,i){if(t<i/2)return jQuery.easing.easeInBounce(e,t*2,0,r,i)*.5+n;return jQuery.easing.easeOutBounce(e,t*2-i,0,r,i)*.5+r*.5+n}});e.waitForImages={hasImageProperties:["backgroundImage","listStyleImage","borderImage","borderCornerImage"]};e.expr[":"].uncached=function(t){var n=document.createElement("img");n.src=t.src;return e(t).is('img[src!=""]')&&!n.complete};e.fn.waitForImages=function(t,n,r){if(e.isPlainObject(arguments[0])){n=t.each;r=t.waitForAll;t=t.finished}t=t||e.noop;n=n||e.noop;r=!!r;if(!e.isFunction(t)||!e.isFunction(n)){throw new TypeError("An invalid callback was supplied.")}return this.each(function(){var i=e(this),s=[];if(r){var o=e.waitForImages.hasImageProperties||[],u=/url\((['"]?)(.*?)\1\)/g;i.find("*").each(function(){var t=e(this);if(t.is("img:uncached")){s.push({src:t.attr("src"),element:t[0]})}e.each(o,function(e,n){var r=t.css(n);if(!r){return true}var i;while(i=u.exec(r)){s.push({src:i[2],element:t[0]})}})})}else{i.find("img:uncached").each(function(){s.push({src:this.src,element:this})})}var a=s.length,f=0;if(a==0){t.call(i[0])}e.each(s,function(r,s){var o=new Image;e(o).bind("load error",function(e){f++;n.call(s.element,f,a,e.type=="load");if(f==a){t.call(i[0]);return false}});o.src=s.src})})};var t=["Webkit","Moz","O","Ms","Khtml",""];var n=["borderRadius","boxShadow","userSelect","transformOrigin","transformStyle","transition","transitionDuration","transitionProperty","transitionTimingFunction","backgroundOrigin","backgroundSize","animation","filter","zoom","columns","perspective","perspectiveOrigin","appearance"];e.fn.cssSetQueue=function(t,n){v=this;var r=v.data("cssQueue")?v.data("cssQueue"):[];var i=v.data("cssCall")?v.data("cssCall"):[];var s=0;var o={};e.each(n,function(e,t){o[e]=t});while(1){if(!i[s]){i[s]=o.complete;break}s++}o.complete=s;r.push([t,o]);v.data({cssQueue:r,cssRunning:true,cssCall:i})};e.fn.cssRunQueue=function(){v=this;var e=v.data("cssQueue")?v.data("cssQueue"):[];if(e[0])v.cssEngine(e[0][0],e[0][1]);else v.data("cssRunning",false);e.shift();v.data("cssQueue",e)};e.cssMerge=function(t,n,r){e.each(n,function(n,i){e.each(r,function(e,r){t[r+n]=i})});return t};e.fn.cssAnimationData=function(e,t){var n=this;var r=n.data("cssAnimations");if(!r)r={};if(!r[e])r[e]=[];r[e].push(t);n.data("cssAnimations",r);return r[e]};e.fn.cssAnimationRemove=function(){var t=this;if(t.data("cssAnimations")!=undefined){var n=t.data("cssAnimations");var r=t.data("identity");e.each(n,function(e,t){n[e]=t.splice(r+1,1)});t.data("cssAnimations",n)}};e.css3D=function(n){e("body").data("cssPerspective",isFinite(n)?n:n?1e3:0).cssOriginal(e.cssMerge({},{TransformStyle:n?"preserve-3d":"flat"},t))};e.cssPropertySupporter=function(r){e.each(n,function(n,i){if(r[i])e.each(t,function(e,t){var n=i.substr(0,1);r[t+n[t?"toUpperCase":"toLowerCase"]()+i.substr(1)]=r[i]})});return r};e.cssAnimateSupport=function(){var n=false;e.each(t,function(e,t){n=document.body.style[t+"AnimationName"]!==undefined?true:n});return n};e.fn.cssEngine=function(n,r){function i(e){return String(e).replace(/([A-Z])/g,"-jQuery1").toLowerCase()}var s=this;var s=this;if(typeof r.complete=="number")s.data("cssCallIndex",r.complete);var o={linear:"linear",swing:"ease",easeIn:"ease-in",easeOut:"ease-out",easeInOut:"ease-in-out"};var u={};var a=e("body").data("cssPerspective");if(n.transform)e.each(t,function(e,t){var r=t+(t?"T":"t")+"ransform";var o=s.cssOriginal(i(r));var f=n.transform;if(!o||o=="none")u[r]="scale(1)";n[r]=(a&&!/perspective/gi.test(f)?"perspective("+a+") ":"")+f});n=e.cssPropertySupporter(n);var f=[];e.each(n,function(e,t){f.push(i(e))});var l=false;var c=[];var h=[];if(f!=undefined){for(var p=0;p<f.length;p++){c.push(String(r.duration/1e3)+"s");var d=o[r.easing];h.push(d?d:r.easing)}c=s.cssAnimationData("dur",c.join(", ")).join(", ");h=s.cssAnimationData("eas",h.join(", ")).join(", ");var v=s.cssAnimationData("prop",f.join(", "));s.data("identity",v.length-1);v=v.join(", ");var m={TransitionDuration:c,TransitionProperty:v,TransitionTimingFunction:h};var g={};g=e.cssMerge(g,m,t);var y=n;e.extend(g,n);if(g.display=="callbackHide")l=true;else if(g.display)u["display"]=g.display;s.cssOriginal(u)}setTimeout(function(){s.cssOriginal(g);var t=s.data("runningCSS");t=!t?y:e.extend(t,y);s.data("runningCSS",t);setTimeout(function(){s.data("cssCallIndex","a");if(l)s.cssOriginal("display","none");s.cssAnimationRemove();if(r.queue)s.cssRunQueue();if(typeof r.complete=="number"){s.data("cssCall")[r.complete].call(s);s.data("cssCall")[r.complete]=0}else r.complete.call(s)},r.duration)},0)};e.str2Speed=function(e){return isNaN(e)?e=="slow"?1e3:e=="fast"?200:600:e};e.fn.cssAnimate=function(t,n,r,i){var s=this;var o={duration:0,easing:"swing",complete:function(){},queue:true};var u={};u=typeof n=="object"?n:{duration:n};u[r?typeof r=="function"?"complete":"easing":0]=r;u[i?"complete":0]=i;u.duration=e.str2Speed(u.duration);e.extend(o,u);if(e.cssAnimateSupport()){s.each(function(n,r){r=e(r);if(o.queue){var i=!r.data("cssRunning");r.cssSetQueue(t,o);if(i)r.cssRunQueue()}else r.cssEngine(t,o)})}else s.animate(t,o);return s};e.cssPresetOptGen=function(e,t){var n={};n[e?typeof e=="function"?"complete":"easing":0]=e;n[t?"complete":0]=t;return n};e.fn.cssFadeTo=function(t,n,r,i){var s=this;opt=e.cssPresetOptGen(r,i);var o={opacity:n};opt.duration=t;if(e.cssAnimateSupport()){s.each(function(t,r){r=e(r);if(r.data("displayOriginal")!=r.cssOriginal("display")&&r.cssOriginal("display")!="none")r.data("displayOriginal",r.cssOriginal("display")?r.cssOriginal("display"):"block");else r.data("displayOriginal","block");o.display=n?r.data("displayOriginal"):"callbackHide";r.cssAnimate(o,opt)})}else s.fadeTo(t,opt);return s};e.fn.cssFadeOut=function(t,n,r){if(e.cssAnimateSupport()){if(!this.cssOriginal("opacity"))this.cssOriginal("opacity",1);this.cssFadeTo(t,0,n,r)}else this.fadeOut(t,n,r);return this};e.fn.cssFadeIn=function(t,n,r){if(e.cssAnimateSupport()){if(this.cssOriginal("opacity"))this.cssOriginal("opacity",0);this.cssFadeTo(t,1,n,r)}else this.fadeIn(t,n,r);return this};e.cssPx2Int=function(e){return e.split("p")[0]*1};e.fn.cssStop=function(){var n=this,r=0;n.data("cssAnimations",false).each(function(i,s){s=e(s);var o={TransitionDuration:"0s"};var u=s.data("runningCSS");var a={};if(u)e.each(u,function(t,n){n=isFinite(e.cssPx2Int(n))?e.cssPx2Int(n):n;var r=[0,1];var i={color:["#000","#fff"],background:["#000","#fff"],"float":["none","left"],clear:["none","left"],border:["none","0px solid #fff"],position:["absolute","relative"],family:["Arial","Helvetica"],display:["none","block"],visibility:["hidden","visible"],transform:["translate(0,0)","scale(1)"]};e.each(i,function(e,n){if((new RegExp(e,"gi")).test(t))r=n});a[t]=r[0]!=n?r[0]:r[1]});else u={};o=e.cssMerge(a,o,t);s.cssOriginal(o);setTimeout(function(){var t=e(n[r]);t.cssOriginal(u).data({runningCSS:{},cssAnimations:{},cssQueue:[],cssRunning:false});if(typeof t.data("cssCallIndex")=="number")t.data("cssCall")[t.data("cssCallIndex")].call(t);t.data("cssCall",[]);r++},0)});return n};e.fn.cssDelay=function(e){return this.cssAnimate({},e)};if(e.fn.cssOriginal!=undefined)e.fn.css=e.fn.cssOriginal;e.fn.cssOriginal=e.fn.css;var r="left",i="right",s="up",o="down",u="in",a="out",f="none",l="auto",c="horizontal",h="vertical",p="all",d="start",m="move",g="end",y="cancel",b="ontouchstart"in window,w="TouchSwipe";var E={fingers:1,threshold:75,maxTimeThreshold:null,swipe:null,swipeLeft:null,swipeRight:null,swipeUp:null,swipeDown:null,swipeStatus:null,pinchIn:null,pinchOut:null,pinchStatus:null,click:null,triggerOnTouchEnd:true,allowPageScroll:"auto",fallbackToMouseEvents:true,excludedElements:"button, input, select, textarea, a, .noSwipe"};jQuery.fn.swipe=function(e){var t=jQuery(this),n=t.data(w);if(n&&typeof e==="string"){if(n[e]){return n[e].apply(this,Array.prototype.slice.call(arguments,1))}else{jQuery.error("Method "+e+" does not exist on jQuery.swipe")}}else if(!n&&(typeof e==="object"||!e)){return S.apply(this,arguments)}return t};jQuery.fn.swipe.defaults=E;jQuery.fn.swipe.phases={PHASE_START:d,PHASE_MOVE:m,PHASE_END:g,PHASE_CANCEL:y};jQuery.fn.swipe.directions={LEFT:r,RIGHT:i,UP:s,DOWN:o,IN:u,OUT:a};jQuery.fn.swipe.pageScroll={NONE:f,HORIZONTAL:c,VERTICAL:h,AUTO:l};jQuery.fn.swipe.fingers={ONE:1,TWO:2,THREE:3,ALL:p}})(jQuery);
      /* Resizable.min.js */
     (function(e,t){function n(e){return parseInt(e,10)||0}function r(e){return!isNaN(parseInt(e,10))}e.widget("ui.resizable",e.ui.mouse,{version:"1.10.3",widgetEventPrefix:"resize",options:{alsoResize:false,animate:false,animateDuration:"slow",animateEasing:"swing",aspectRatio:false,autoHide:false,containment:false,ghost:false,grid:false,handles:"e,s,se",helper:false,maxHeight:null,maxWidth:null,minHeight:10,minWidth:10,zIndex:90,resize:null,start:null,stop:null},_create:function(){var t,n,r,i,s,o=this,u=this.options;this.element.addClass("ui-resizable");e.extend(this,{_aspectRatio:!!u.aspectRatio,aspectRatio:u.aspectRatio,originalElement:this.element,_proportionallyResizeElements:[],_helper:u.helper||u.ghost||u.animate?u.helper||"ui-resizable-helper":null});if(this.element[0].nodeName.match(/canvas|textarea|input|select|button|img/i)){this.element.wrap(e("<div class='ui-wrapper' style='overflow: hidden;'></div>").css({position:this.element.css("position"),width:this.element.outerWidth(),height:this.element.outerHeight(),top:this.element.css("top")}));this.element=this.element.parent().data("ui-resizable",this.element.data("ui-resizable"));this.elementIsWrapper=true;this.element.css({marginLeft:this.originalElement.css("marginLeft"),marginTop:this.originalElement.css("marginTop"),marginRight:this.originalElement.css("marginRight"),marginBottom:this.originalElement.css("marginBottom")});this.originalElement.css({marginLeft:0,marginTop:0,marginRight:0,marginBottom:0});this.originalResizeStyle=this.originalElement.css("resize");this.originalElement.css("resize","none");this._proportionallyResizeElements.push(this.originalElement.css({position:"static",zoom:1,display:"block"}));this.originalElement.css({margin:this.originalElement.css("margin")});this._proportionallyResize()}this.handles=u.handles||(!e(".ui-resizable-handle",this.element).length?"e,s,se":{n:".ui-resizable-n",e:".ui-resizable-e",s:".ui-resizable-s",w:".ui-resizable-w",se:".ui-resizable-se",sw:".ui-resizable-sw",ne:".ui-resizable-ne",nw:".ui-resizable-nw"});if(this.handles.constructor===String){if(this.handles==="all"){this.handles="n,e,s,w,se,sw,ne,nw"}t=this.handles.split(",");this.handles={};for(n=0;n<t.length;n++){r=e.trim(t[n]);s="ui-resizable-"+r;i=e("<div class='ui-resizable-handle "+s+"'></div>");i.css({zIndex:u.zIndex});if("se"===r){i.addClass("ui-icon ui-icon-gripsmall-diagonal-se")}this.handles[r]=".ui-resizable-"+r;this.element.append(i)}}this._renderAxis=function(t){var n,r,i,s;t=t||this.element;for(n in this.handles){if(this.handles[n].constructor===String){this.handles[n]=e(this.handles[n],this.element).show()}if(this.elementIsWrapper&&this.originalElement[0].nodeName.match(/textarea|input|select|button/i)){r=e(this.handles[n],this.element);s=/sw|ne|nw|se|n|s/.test(n)?r.outerHeight():r.outerWidth();i=["padding",/ne|nw|n/.test(n)?"Top":/se|sw|s/.test(n)?"Bottom":/^e$/.test(n)?"Right":"Left"].join("");t.css(i,s);this._proportionallyResize()}if(!e(this.handles[n]).length){continue}}};this._renderAxis(this.element);this._handles=e(".ui-resizable-handle",this.element).disableSelection();this._handles.mouseover(function(){if(!o.resizing){if(this.className){i=this.className.match(/ui-resizable-(se|sw|ne|nw|n|e|s|w)/i)}o.axis=i&&i[1]?i[1]:"se"}});if(u.autoHide){this._handles.hide();e(this.element).addClass("ui-resizable-autohide").mouseenter(function(){if(u.disabled){return}e(this).removeClass("ui-resizable-autohide");o._handles.show()}).mouseleave(function(){if(u.disabled){return}if(!o.resizing){e(this).addClass("ui-resizable-autohide");o._handles.hide()}})}this._mouseInit()},_destroy:function(){this._mouseDestroy();var t,n=function(t){e(t).removeClass("ui-resizable ui-resizable-disabled ui-resizable-resizing").removeData("resizable").removeData("ui-resizable").unbind(".resizable").find(".ui-resizable-handle").remove()};if(this.elementIsWrapper){n(this.element);t=this.element;this.originalElement.css({position:t.css("position"),width:t.outerWidth(),height:t.outerHeight(),top:t.css("top")}).insertAfter(t);t.remove()}this.originalElement.css("resize",this.originalResizeStyle);n(this.originalElement);return this},_mouseCapture:function(t){var n,r,i=false;for(n in this.handles){r=e(this.handles[n])[0];if(r===t.target||e.contains(r,t.target)){i=true}}return!this.options.disabled&&i},_mouseStart:function(t){var r,i,s,o=this.options,u=this.element.position(),a=this.element;this.resizing=true;if(/absolute/.test(a.css("position"))){a.css({position:"absolute",top:a.css("top")})}else if(a.is(".ui-draggable")){a.css({position:"absolute",top:u.top})}this._renderProxy();i=n(this.helper.css("top"));if(o.containment){r+=e(o.containment).scrollLeft()||0;i+=e(o.containment).scrollTop()||0}this.offset=this.helper.offset();this.position={left:r,top:i};this.size=this._helper?{width:a.outerWidth(),height:a.outerHeight()}:{width:a.width(),height:a.height()};this.originalSize=this._helper?{width:a.outerWidth(),height:a.outerHeight()}:{width:a.width(),height:a.height()};this.originalPosition={left:r,top:i};this.sizeDiff={width:a.outerWidth()-a.width(),height:a.outerHeight()-a.height()};this.originalMousePosition={left:t.pageX,top:t.pageY};this.aspectRatio=typeof o.aspectRatio==="number"?o.aspectRatio:this.originalSize.width/this.originalSize.height||1;s=e(".ui-resizable-"+this.axis).css("cursor");e("body").css("cursor",s==="auto"?this.axis+"-resize":s);a.addClass("ui-resizable-resizing");this._propagate("start",t);return true},_mouseDrag:function(t){var n,r=this.helper,i={},s=this.originalMousePosition,o=this.axis,u=this.position.top,a=this.position.left,f=this.size.width,l=this.size.height,c=t.pageX-s.left||0,h=t.pageY-s.top||0,p=this._change[o];if(!p){return false}n=p.apply(this,[t,c,h]);this._updateVirtualBoundaries(t.shiftKey);if(this._aspectRatio||t.shiftKey){n=this._updateRatio(n,t)}n=this._respectSize(n,t);this._updateCache(n);this._propagate("resize",t);if(this.position.top!==u){i.top=this.position.top+"px"}if(this.position.left!==a){i.left=this.position.left+"px"}if(this.size.width!==f){i.width=this.size.width+"px"}if(this.size.height!==l){i.height=this.size.height+"px"}r.css(i);if(!this._helper&&this._proportionallyResizeElements.length){this._proportionallyResize()}if(!e.isEmptyObject(i)){this._trigger("resize",t,this.ui())}return false},_mouseStop:function(t){this.resizing=false;var n,r,i,s,o,u,a,f=this.options,l=this;if(this._helper){n=this._proportionallyResizeElements;r=n.length&&/textarea/i.test(n[0].nodeName);i=r&&e.ui.hasScroll(n[0],"left")?0:l.sizeDiff.height;s=r?0:l.sizeDiff.width;o={width:l.helper.width()-s,height:l.helper.height()-i};u=parseInt(l.element.css("left"),10)+(l.position.left-l.originalPosition.left)||null;a=parseInt(l.element.css("top"),10)+(l.position.top-l.originalPosition.top)||null;if(!f.animate){this.element.css(e.extend(o,{top:a,left:u}))}l.helper.height(l.size.height);l.helper.width(l.size.width);if(this._helper&&!f.animate){this._proportionallyResize()}}e("body").css("cursor","auto");this.element.removeClass("ui-resizable-resizing");this._propagate("stop",t);if(this._helper){this.helper.remove()}return false},_updateVirtualBoundaries:function(e){var t,n,i,s,o,u=this.options;o={minWidth:r(u.minWidth)?u.minWidth:0,maxWidth:r(u.maxWidth)?u.maxWidth:Infinity,minHeight:r(u.minHeight)?u.minHeight:0,maxHeight:r(u.maxHeight)?u.maxHeight:Infinity};if(this._aspectRatio||e){t=o.minHeight*this.aspectRatio;i=o.minWidth/this.aspectRatio;n=o.maxHeight*this.aspectRatio;s=o.maxWidth/this.aspectRatio;if(t>o.minWidth){o.minWidth=t}if(i>o.minHeight){o.minHeight=i}if(n<o.maxWidth){o.maxWidth=n}if(s<o.maxHeight){o.maxHeight=s}}this._vBoundaries=o},_updateCache:function(e){this.offset=this.helper.offset();if(r(e.left)){this.position.left=e.left}if(r(e.top)){this.position.top=e.top}if(r(e.height)){this.size.height=e.height}if(r(e.width)){this.size.width=e.width}},_updateRatio:function(e){var t=this.position,n=this.size,i=this.axis;if(r(e.height)){e.width=e.height*this.aspectRatio}else if(r(e.width)){e.height=e.width/this.aspectRatio}if(i==="sw"){e.left=t.left+(n.width-e.width);e.top=null}if(i==="nw"){e.top=t.top+(n.height-e.height);e.left=t.left+(n.width-e.width)}return e},_respectSize:function(e){var t=this._vBoundaries,n=this.axis,i=r(e.width)&&t.maxWidth&&t.maxWidth<e.width,s=r(e.height)&&t.maxHeight&&t.maxHeight<e.height,o=r(e.width)&&t.minWidth&&t.minWidth>e.width,u=r(e.height)&&t.minHeight&&t.minHeight>e.height,a=this.originalPosition.left+this.originalSize.width,f=this.position.top+this.size.height,l=/sw|nw|w/.test(n),c=/nw|ne|n/.test(n);if(o){e.width=t.minWidth}if(u){e.height=t.minHeight}if(i){e.width=t.maxWidth}if(s){e.height=t.maxHeight}if(o&&l){e.left=a-t.minWidth}if(i&&l){e.left=a-t.maxWidth}if(u&&c){e.top=f-t.minHeight}if(s&&c){e.top=f-t.maxHeight}if(!e.width&&!e.height&&!e.left&&e.top){e.top=null}else if(!e.width&&!e.height&&!e.top&&e.left){e.left=null}return e},_proportionallyResize:function(){if(!this._proportionallyResizeElements.length){return}var e,t,n,r,i,s=this.helper||this.element;for(e=0;e<this._proportionallyResizeElements.length;e++){i=this._proportionallyResizeElements[e];if(!this.borderDif){this.borderDif=[];n=[i.css("borderTopWidth"),i.css("borderRightWidth"),i.css("borderBottomWidth"),i.css("borderLeftWidth")];r=[i.css("paddingTop"),i.css("paddingRight"),i.css("paddingBottom"),i.css("paddingLeft")];for(t=0;t<n.length;t++){this.borderDif[t]=(parseInt(n[t],10)||0)+(parseInt(r[t],10)||0)}}i.css({height:s.height()-this.borderDif[0]-this.borderDif[2]||0,width:s.width()-this.borderDif[1]-this.borderDif[3]||0})}},_renderProxy:function(){var t=this.element,n=this.options;this.elementOffset=t.offset();if(this._helper){this.helper=this.helper||e("<div style='overflow:hidden;'></div>");this.helper.addClass(this._helper).css({width:this.element.outerWidth()-1,height:this.element.outerHeight()-1,position:"absolute",left:this.elementOffset.left+"px",top:this.elementOffset.top+"px",zIndex:++n.zIndex});this.helper.appendTo("body").disableSelection()}else{this.helper=this.element}},_change:{e:function(e,t){return{width:this.originalSize.width+t}},w:function(e,t){var n=this.originalSize,r=this.originalPosition;return{left:r.left+t,width:n.width-t}},n:function(e,t,n){var r=this.originalSize,i=this.originalPosition;return{top:i.top+n,height:r.height-n}},s:function(e,t,n){return{height:this.originalSize.height+n}},se:function(t,n,r){return e.extend(this._change.s.apply(this,arguments),this._change.e.apply(this,[t,n,r]))},sw:function(t,n,r){return e.extend(this._change.s.apply(this,arguments),this._change.w.apply(this,[t,n,r]))},ne:function(t,n,r){return e.extend(this._change.n.apply(this,arguments),this._change.e.apply(this,[t,n,r]))},nw:function(t,n,r){return e.extend(this._change.n.apply(this,arguments),this._change.w.apply(this,[t,n,r]))}},_propagate:function(t,n){e.ui.plugin.call(this,t,[n,this.ui()]);t!=="resize"&&this._trigger(t,n,this.ui())},plugins:{},ui:function(){return{originalElement:this.originalElement,element:this.element,helper:this.helper,position:this.position,size:this.size,originalSize:this.originalSize,originalPosition:this.originalPosition}}});e.ui.plugin.add("resizable","animate",{stop:function(t){var n=e(this).data("ui-resizable"),r=n.options,i=n._proportionallyResizeElements,s=i.length&&/textarea/i.test(i[0].nodeName),o=s&&e.ui.hasScroll(i[0],"left")?0:n.sizeDiff.height,u=s?0:n.sizeDiff.width,a={width:n.size.width-u,height:n.size.height-o},f=parseInt(n.element.css("left"),10)+(n.position.left-n.originalPosition.left)||null,l=parseInt(n.element.css("top"),10)+(n.position.top-n.originalPosition.top)||null;n.element.animate(e.extend(a,l&&f?{top:l,left:f}:{}),{duration:r.animateDuration,easing:r.animateEasing,step:function(){var r={width:parseInt(n.element.css("width"),10),height:parseInt(n.element.css("height"),10),top:parseInt(n.element.css("top"),10),left:parseInt(n.element.css("left"),10)};if(i&&i.length){e(i[0]).css({width:r.width,height:r.height})}n._updateCache(r);n._propagate("resize",t)}})}});e.ui.plugin.add("resizable","containment",{start:function(){var t,r,i,s,o,u,a,f=e(this).data("ui-resizable"),l=f.options,c=f.element,h=l.containment,p=h instanceof e?h.get(0):/parent/.test(h)?c.parent().get(0):h;if(!p){return}f.containerElement=e(p);if(/document/.test(h)||h===document){f.containerOffset={left:0,top:0};f.containerPosition={left:0,top:0};f.parentData={element:e(document),left:0,top:0,width:e(document).width(),height:e(document).height()||document.body.parentNode.scrollHeight}}else{t=e(p);r=[];e(["Top","Right","Left","Bottom"]).each(function(e,i){r[e]=n(t.css("padding"+i))});f.containerOffset=t.offset();f.containerPosition=t.position();f.containerSize={height:t.innerHeight()-r[3],width:t.innerWidth()-r[1]};i=f.containerOffset;s=f.containerSize.height;o=f.containerSize.width;u=e.ui.hasScroll(p,"left")?p.scrollWidth:o;a=e.ui.hasScroll(p)?p.scrollHeight:s;f.parentData={element:p,left:i.left,top:i.top,width:u,height:a}}},resize:function(t){var n,r,i,s,o=e(this).data("ui-resizable"),u=o.options,a=o.containerOffset,f=o.position,l=o._aspectRatio||t.shiftKey,c={top:0,left:0},h=o.containerElement;if(h[0]!==document&&/static/.test(h.css("position"))){c=a}if(f.left<(o._helper?a.left:0)){o.size.width=o.size.width+(o._helper?o.position.left-a.left:o.position.left-c.left);if(l){o.size.height=o.size.width/o.aspectRatio}o.position.left=u.helper?a.left:0}if(f.top<(o._helper?a.top:0)){o.size.height=o.size.height+(o._helper?o.position.top-a.top:o.position.top);if(l){o.size.width=o.size.height*o.aspectRatio}o.position.top=o._helper?a.top:0}o.offset.left=o.parentData.left+o.position.left;o.offset.top=o.parentData.top+o.position.top;n=Math.abs((o._helper?o.offset.left-c.left:o.offset.left-c.left)+o.sizeDiff.width);r=Math.abs((o._helper?o.offset.top-c.top:o.offset.top-a.top)+o.sizeDiff.height);i=o.containerElement.get(0)===o.element.parent().get(0);s=/relative|absolute/.test(o.containerElement.css("position"));if(i&&s){n-=o.parentData.left}if(n+o.size.width>=o.parentData.width){o.size.width=o.parentData.width-n;if(l){o.size.height=o.size.width/o.aspectRatio}}if(r+o.size.height>=o.parentData.height){o.size.height=o.parentData.height-r;if(l){o.size.width=o.size.height*o.aspectRatio}}},stop:function(){var t=e(this).data("ui-resizable"),n=t.options,r=t.containerOffset,i=t.containerPosition,s=t.containerElement,o=e(t.helper),u=o.offset(),a=o.outerWidth()-t.sizeDiff.width,f=o.outerHeight()-t.sizeDiff.height;if(t._helper&&!n.animate&&/relative/.test(s.css("position"))){e(this).css({left:u.left-i.left-r.left,width:a,height:f})}if(t._helper&&!n.animate&&/static/.test(s.css("position"))){e(this).css({left:u.left-i.left-r.left,width:a,height:f})}}});e.ui.plugin.add("resizable","alsoResize",{start:function(){var t=e(this).data("ui-resizable"),n=t.options,r=function(t){e(t).each(function(){var t=e(this);t.data("ui-resizable-alsoresize",{width:parseInt(t.width(),10),height:parseInt(t.height(),10),left:parseInt(t.css("left"),10),top:parseInt(t.css("top"),10)})})};if(typeof n.alsoResize==="object"&&!n.alsoResize.parentNode){if(n.alsoResize.length){n.alsoResize=n.alsoResize[0];r(n.alsoResize)}else{e.each(n.alsoResize,function(e){r(e)})}}else{r(n.alsoResize)}},resize:function(t,n){var r=e(this).data("ui-resizable"),i=r.options,s=r.originalSize,o=r.originalPosition,u={height:r.size.height-s.height||0,width:r.size.width-s.width||0,top:r.position.top-o.top||0,left:r.position.left-o.left||0},a=function(t,r){e(t).each(function(){var t=e(this),i=e(this).data("ui-resizable-alsoresize"),s={},o=r&&r.length?r:t.parents(n.originalElement[0]).length?["width","height"]:["width","height","top","left"];e.each(o,function(e,t){var n=(i[t]||0)+(u[t]||0);if(n&&n>=0){s[t]=n||null}});t.css(s)})};if(typeof i.alsoResize==="object"&&!i.alsoResize.nodeType){e.each(i.alsoResize,function(e,t){a(e,t)})}else{a(i.alsoResize)}},stop:function(){e(this).removeData("resizable-alsoresize")}});e.ui.plugin.add("resizable","ghost",{start:function(){var t=e(this).data("ui-resizable"),n=t.options,r=t.size;t.ghost=t.originalElement.clone();t.ghost.css({opacity:.25,display:"block",position:"relative",height:r.height,width:r.width,margin:0,left:0,top:0}).addClass("ui-resizable-ghost").addClass(typeof n.ghost==="string"?n.ghost:"");t.ghost.appendTo(t.helper)},resize:function(){var t=e(this).data("ui-resizable");if(t.ghost){t.ghost.css({position:"relative",height:t.size.height,width:t.size.width})}},stop:function(){var t=e(this).data("ui-resizable");if(t.ghost&&t.helper){t.helper.get(0).removeChild(t.ghost.get(0))}}});e.ui.plugin.add("resizable","grid",{resize:function(){var t=e(this).data("ui-resizable"),n=t.options,r=t.size,i=t.originalSize,s=t.originalPosition,o=t.axis,u=typeof n.grid==="number"?[n.grid,n.grid]:n.grid,a=u[0]||1,f=u[1]||1,l=Math.round((r.width-i.width)/a)*a,c=Math.round((r.height-i.height)/f)*f,h=i.width+l,p=i.height+c,d=n.maxWidth&&n.maxWidth<h,v=n.maxHeight&&n.maxHeight<p,m=n.minWidth&&n.minWidth>h,g=n.minHeight&&n.minHeight>p;n.grid=u;if(m){h=h+a}if(g){p=p+f}if(d){h=h-a}if(v){p=p-f}if(/^(se|s|e)$/.test(o)){t.size.width=h;t.size.height=p}else if(/^(ne)$/.test(o)){t.size.width=h;t.size.height=p;t.position.top=s.top-c}else if(/^(sw)$/.test(o)){t.size.width=h;t.size.height=p;t.position.left=s.left-l}else{t.size.width=h;t.size.height=p;t.position.top=s.top-c;t.position.left=s.left-l}}})})(jQuery);
      /* Jquery mouse wheel.min.js */
     (function(a){function d(b){var c=b||window.event,d=[].slice.call(arguments,1),e=0,f=!0,g=0,h=0;return b=a.event.fix(c),b.type="mousewheel",c.wheelDelta&&(e=c.wheelDelta/120),c.detail&&(e=-c.detail/3),h=e,c.axis!==undefined&&c.axis===c.HORIZONTAL_AXIS&&(h=0,g=-1*e),c.wheelDeltaY!==undefined&&(h=c.wheelDeltaY/120),c.wheelDeltaX!==undefined&&(g=-1*c.wheelDeltaX/120),d.unshift(b,e,g,h),(a.event.dispatch||a.event.handle).apply(this,d)}var b=["DOMMouseScroll","mousewheel"];if(a.event.fixHooks)for(var c=b.length;c;)a.event.fixHooks[b[--c]]=a.event.mouseHooks;a.event.special.mousewheel={setup:function(){if(this.addEventListener)for(var a=b.length;a;)this.addEventListener(b[--a],d,!1);else this.onmousewheel=d},teardown:function(){if(this.removeEventListener)for(var a=b.length;a;)this.removeEventListener(b[--a],d,!1);else this.onmousewheel=null}},a.fn.extend({mousewheel:function(a){return a?this.bind("mousewheel",a):this.trigger("mousewheel")},unmousewheel:function(a){return this.unbind("mousewheel",a)}})})(jQuery);
     /** ******** */
    /* jquery.mCustomScrollbar.min.js */
     /** ******** */
     (function(e){var t={init:function(t){var n={set_width:false,set_height:false,horizontalScroll:false,scrollInertia:950,mouseWheel:true,mouseWheelPixels:"auto",autoDraggerLength:true,autoHideScrollbar:false,snapAmount:null,snapOffset:0,scrollButtons:{enable:false,scrollType:"continuous",scrollSpeed:"auto",scrollAmount:40},advanced:{updateOnBrowserResize:true,updateOnContentResize:false,autoExpandHorizontalScroll:false,autoScrollOnFocus:true,normalizeMouseWheelDelta:false},contentTouchScroll:true,callbacks:{onScrollStart:function(){},onScroll:function(){},onTotalScroll:function(){},onTotalScrollBack:function(){},onTotalScrollOffset:0,onTotalScrollBackOffset:0,whileScrolling:function(){}},theme:"light"},t=e.extend(true,n,t);return this.each(function(){var n=e(this);if(t.set_width){n.css("width",t.set_width)}if(t.set_height){n.css("height",t.set_height)}if(!e(document).data("mCustomScrollbar-index")){e(document).data("mCustomScrollbar-index","1")}else{var r=parseInt(e(document).data("mCustomScrollbar-index"));e(document).data("mCustomScrollbar-index",r+1)}n.wrapInner("<div class='mCustomScrollBox mCS-"+t.theme+"' id='mCSB_"+e(document).data("mCustomScrollbar-index")+"' style='position:relative; height:100%; overflow:hidden; max-width:100%;' />").addClass("mCustomScrollbar _mCS_"+e(document).data("mCustomScrollbar-index"));var i=n.children(".mCustomScrollBox");if(t.horizontalScroll){i.addClass("mCSB_horizontal").wrapInner("<div class='mCSB_h_wrapper' style='position:relative; left:0; width:999999px;' />");var s=i.children(".mCSB_h_wrapper");s.wrapInner("<div class='mCSB_container' style='position:absolute; left:0;' />").children(".mCSB_container").css({width:s.children().outerWidth(),position:"relative"}).unwrap()}else{i.wrapInner("<div class='mCSB_container' style='position:relative; top:0;' />")}var o=i.children(".mCSB_container");if(e.support.touch){o.addClass("mCS_touch")}o.after("<div class='mCSB_scrollTools' style='position:absolute;'><div class='mCSB_draggerContainer'><div class='mCSB_dragger' style='position:absolute;' oncontextmenu='return false;'><div class='mCSB_dragger_bar' style='position:relative;'></div></div><div class='mCSB_draggerRail'></div></div></div>");var u=i.children(".mCSB_scrollTools"),a=u.children(".mCSB_draggerContainer"),f=a.children(".mCSB_dragger");if(t.horizontalScroll){f.data("minDraggerWidth",f.width())}else{f.data("minDraggerHeight",f.height())}if(t.scrollButtons.enable){if(t.horizontalScroll){u.prepend("<a class='mCSB_buttonLeft' oncontextmenu='return false;'></a>").append("<a class='mCSB_buttonRight' oncontextmenu='return false;'></a>")}else{u.prepend("<a class='mCSB_buttonUp' oncontextmenu='return false;'></a>").append("<a class='mCSB_buttonDown' oncontextmenu='return false;'></a>")}}i.bind("scroll",function(){if(!n.is(".mCS_disabled")){i.scrollTop(0).scrollLeft(0)}});n.data({mCS_Init:true,mCustomScrollbarIndex:e(document).data("mCustomScrollbar-index"),horizontalScroll:t.horizontalScroll,scrollInertia:t.scrollInertia,scrollEasing:"mcsEaseOut",mouseWheel:t.mouseWheel,mouseWheelPixels:t.mouseWheelPixels,autoDraggerLength:t.autoDraggerLength,autoHideScrollbar:t.autoHideScrollbar,snapAmount:t.snapAmount,snapOffset:t.snapOffset,scrollButtons_enable:t.scrollButtons.enable,scrollButtons_scrollType:t.scrollButtons.scrollType,scrollButtons_scrollSpeed:t.scrollButtons.scrollSpeed,scrollButtons_scrollAmount:t.scrollButtons.scrollAmount,autoExpandHorizontalScroll:t.advanced.autoExpandHorizontalScroll,autoScrollOnFocus:t.advanced.autoScrollOnFocus,normalizeMouseWheelDelta:t.advanced.normalizeMouseWheelDelta,contentTouchScroll:t.contentTouchScroll,onScrollStart_Callback:t.callbacks.onScrollStart,onScroll_Callback:t.callbacks.onScroll,onTotalScroll_Callback:t.callbacks.onTotalScroll,onTotalScrollBack_Callback:t.callbacks.onTotalScrollBack,onTotalScroll_Offset:t.callbacks.onTotalScrollOffset,onTotalScrollBack_Offset:t.callbacks.onTotalScrollBackOffset,whileScrolling_Callback:t.callbacks.whileScrolling,bindEvent_scrollbar_drag:false,bindEvent_content_touch:false,bindEvent_scrollbar_click:false,bindEvent_mousewheel:false,bindEvent_buttonsContinuous_y:false,bindEvent_buttonsContinuous_x:false,bindEvent_buttonsPixels_y:false,bindEvent_buttonsPixels_x:false,bindEvent_focusin:false,bindEvent_autoHideScrollbar:false,mCSB_buttonScrollRight:false,mCSB_buttonScrollLeft:false,mCSB_buttonScrollDown:false,mCSB_buttonScrollUp:false});if(t.horizontalScroll){if(n.css("max-width")!=="none"){if(!t.advanced.updateOnContentResize){t.advanced.updateOnContentResize=true}}}else{if(n.css("max-height")!=="none"){var l=false,h=parseInt(n.css("max-height"));if(n.css("max-height").indexOf("%")>=0){l=h,h=n.parent().height()*l/100}n.css("overflow","hidden");i.css("max-height",h)}}n.mCustomScrollbar("update");if(t.advanced.updateOnBrowserResize){var p,d=e(window).width(),v=e(window).height();e(window).bind("resize."+n.data("mCustomScrollbarIndex"),function(){if(p){clearTimeout(p)}p=setTimeout(function(){if(!n.is(".mCS_disabled")&&!n.is(".mCS_destroyed")){var t=e(window).width(),r=e(window).height();if(d!==t||v!==r){if(n.css("max-height")!=="none"&&l){i.css("max-height",n.parent().height()*l/100)}n.mCustomScrollbar("update");d=t;v=r}}},150)})}if(t.advanced.updateOnContentResize){var m;if(t.horizontalScroll){var g=o.outerWidth()}else{var g=o.outerHeight()}m=setInterval(function(){if(t.horizontalScroll){if(t.advanced.autoExpandHorizontalScroll){o.css({position:"absolute",width:"auto"}).wrap("<div class='mCSB_h_wrapper' style='position:relative; left:0; width:999999px;' />").css({width:o.outerWidth(),position:"relative"}).unwrap()}var e=o.outerWidth()}else{var e=o.outerHeight()}if(e!=g){n.mCustomScrollbar("update");g=e}},300)}})},update:function(){var t=e(this),n=t.children(".mCustomScrollBox"),r=n.children(".mCSB_container");r.removeClass("mCS_no_scrollbar");t.removeClass("mCS_disabled mCS_destroyed");n.scrollTop(0).scrollLeft(0);var i=n.children(".mCSB_scrollTools"),s=i.children(".mCSB_draggerContainer"),o=s.children(".mCSB_dragger");if(t.data("horizontalScroll")){var u=i.children(".mCSB_buttonLeft"),a=i.children(".mCSB_buttonRight"),f=n.width();if(t.data("autoExpandHorizontalScroll")){r.css({position:"absolute",width:"auto"}).wrap("<div class='mCSB_h_wrapper' style='position:relative; left:0; width:999999px;' />").css({width:r.outerWidth(),position:"relative"}).unwrap()}var l=r.outerWidth()}else{var h=i.children(".mCSB_buttonUp"),p=i.children(".mCSB_buttonDown"),d=n.height(),v=r.outerHeight()}if(v>d&&!t.data("horizontalScroll")){i.css("display","block");var m=s.height();if(t.data("autoDraggerLength")){var g=Math.round(d/v*m),y=o.data("minDraggerHeight");if(g<=y){o.css({height:y})}else{if(g>=m-10){var b=m-10;o.css({height:b})}else{o.css({height:g})}}o.children(".mCSB_dragger_bar").css({"line-height":o.height()+"px"})}var w=o.height(),E=(v-d)/(m-w);t.data("scrollAmount",E).mCustomScrollbar("scrolling",n,r,s,o,h,p,u,a);var S=Math.abs(r.position().top);t.mCustomScrollbar("scrollTo",S,{scrollInertia:0,trigger:"internal"})}else{if(l>f&&t.data("horizontalScroll")){i.css("display","block");var x=s.width();if(t.data("autoDraggerLength")){var T=Math.round(f/l*x),N=o.data("minDraggerWidth");if(T<=N){o.css({width:N})}else{if(T>=x-10){var C=x-10;o.css({width:C})}else{o.css({width:T})}}}var k=o.width(),E=(l-f)/(x-k);t.data("scrollAmount",E).mCustomScrollbar("scrolling",n,r,s,o,h,p,u,a);var S=Math.abs(r.position().left);t.mCustomScrollbar("scrollTo",S,{scrollInertia:0,trigger:"internal"})}else{n.unbind("mousewheel focusin");if(t.data("horizontalScroll")){o.add(r).css("left",0)}else{o.add(r).css("top",0)}i.css("display","none");r.addClass("mCS_no_scrollbar");t.data({bindEvent_mousewheel:false,bindEvent_focusin:false})}}},scrolling:function(t,r,i,s,o,u,a,f){function v(e,t,n,r){if(l.data("horizontalScroll")){l.mCustomScrollbar("scrollTo",s.position().left-t+r,{moveDragger:true,trigger:"internal"})}else{l.mCustomScrollbar("scrollTo",s.position().top-e+n,{moveDragger:true,trigger:"internal"})}}var l=e(this);if(!l.data("bindEvent_scrollbar_drag")){var h,p;if(e.support.msPointer){s.bind("MSPointerDown",function(t){t.preventDefault();l.data({on_drag:true});s.addClass("mCSB_dragger_onDrag");var n=e(this),r=n.offset(),i=t.originalEvent.pageX-r.left,o=t.originalEvent.pageY-r.top;if(i<n.width()&&i>0&&o<n.height()&&o>0){h=o;p=i}});e(document).bind("MSPointerMove."+l.data("mCustomScrollbarIndex"),function(e){e.preventDefault();if(l.data("on_drag")){var t=s,n=t.offset(),r=e.originalEvent.pageX-n.left,i=e.originalEvent.pageY-n.top;v(h,p,i,r)}}).bind("MSPointerUp."+l.data("mCustomScrollbarIndex"),function(e){l.data({on_drag:false});s.removeClass("mCSB_dragger_onDrag")})}else{s.bind("mousedown touchstart",function(t){t.preventDefault();t.stopImmediatePropagation();var n=e(this),r=n.offset(),i,o;if(t.type==="touchstart"){var u=t.originalEvent.touches[0]||t.originalEvent.changedTouches[0];i=u.pageX-r.left;o=u.pageY-r.top}else{l.data({on_drag:true});s.addClass("mCSB_dragger_onDrag");i=t.pageX-r.left;o=t.pageY-r.top}if(i<n.width()&&i>0&&o<n.height()&&o>0){h=o;p=i}}).bind("touchmove",function(t){t.preventDefault();t.stopImmediatePropagation();var n=t.originalEvent.touches[0]||t.originalEvent.changedTouches[0],r=e(this),i=r.offset(),s=n.pageX-i.left,o=n.pageY-i.top;v(h,p,o,s)});e(document).bind("mousemove."+l.data("mCustomScrollbarIndex"),function(e){if(l.data("on_drag")){var t=s,n=t.offset(),r=e.pageX-n.left,i=e.pageY-n.top;v(h,p,i,r)}}).bind("mouseup."+l.data("mCustomScrollbarIndex"),function(e){l.data({on_drag:false});s.removeClass("mCSB_dragger_onDrag")})}l.data({bindEvent_scrollbar_drag:true})}if(e.support.touch&&l.data("contentTouchScroll")){if(!l.data("bindEvent_content_touch")){var m,g,y,b,w,E,S;r.bind("touchstart",function(t){t.stopImmediatePropagation();m=t.originalEvent.touches[0]||t.originalEvent.changedTouches[0];g=e(this);y=g.offset();w=m.pageX-y.left;b=m.pageY-y.top;E=b;S=w});r.bind("touchmove",function(t){t.preventDefault();t.stopImmediatePropagation();m=t.originalEvent.touches[0]||t.originalEvent.changedTouches[0];g=e(this).parent();y=g.offset();w=m.pageX-y.left;b=m.pageY-y.top;if(l.data("horizontalScroll")){l.mCustomScrollbar("scrollTo",S-w,{trigger:"internal"})}else{l.mCustomScrollbar("scrollTo",E-b,{trigger:"internal"})}})}}if(!l.data("bindEvent_scrollbar_click")){i.bind("click",function(t){var n=(t.pageY-i.offset().top)*l.data("scrollAmount"),r=e(t.target);if(l.data("horizontalScroll")){n=(t.pageX-i.offset().left)*l.data("scrollAmount")}if(r.hasClass("mCSB_draggerContainer")||r.hasClass("mCSB_draggerRail")){l.mCustomScrollbar("scrollTo",n,{trigger:"internal",scrollEasing:"draggerRailEase"})}});l.data({bindEvent_scrollbar_click:true})}if(l.data("mouseWheel")){if(!l.data("bindEvent_mousewheel")){t.bind("mousewheel",function(e,t){var n,o=l.data("mouseWheelPixels"),u=Math.abs(r.position().top),a=s.position().top,f=i.height()-s.height();if(l.data("normalizeMouseWheelDelta")){if(t<0){t=-1}else{t=1}}if(o==="auto"){o=100+Math.round(l.data("scrollAmount")/2)}if(l.data("horizontalScroll")){a=s.position().left;f=i.width()-s.width();u=Math.abs(r.position().left)}if(t>0&&a!==0||t<0&&a!==f){e.preventDefault();e.stopImmediatePropagation()}n=u-t*o;l.mCustomScrollbar("scrollTo",n,{trigger:"internal"})});l.data({bindEvent_mousewheel:true})}}if(l.data("scrollButtons_enable")){if(l.data("scrollButtons_scrollType")==="pixels"){if(l.data("horizontalScroll")){f.add(a).unbind("mousedown touchstart MSPointerDown mouseup MSPointerUp mouseout MSPointerOut touchend",T,N);l.data({bindEvent_buttonsContinuous_x:false});if(!l.data("bindEvent_buttonsPixels_x")){f.bind("click",function(e){e.preventDefault();x(Math.abs(r.position().left)+l.data("scrollButtons_scrollAmount"))});a.bind("click",function(e){e.preventDefault();x(Math.abs(r.position().left)-l.data("scrollButtons_scrollAmount"))});l.data({bindEvent_buttonsPixels_x:true})}}else{u.add(o).unbind("mousedown touchstart MSPointerDown mouseup MSPointerUp mouseout MSPointerOut touchend",T,N);l.data({bindEvent_buttonsContinuous_y:false});if(!l.data("bindEvent_buttonsPixels_y")){u.bind("click",function(e){e.preventDefault();x(Math.abs(r.position().top)+l.data("scrollButtons_scrollAmount"))});o.bind("click",function(e){e.preventDefault();x(Math.abs(r.position().top)-l.data("scrollButtons_scrollAmount"))});l.data({bindEvent_buttonsPixels_y:true})}}function x(e){if(!s.data("preventAction")){s.data("preventAction",true);l.mCustomScrollbar("scrollTo",e,{trigger:"internal"})}}}else{if(l.data("horizontalScroll")){f.add(a).unbind("click");l.data({bindEvent_buttonsPixels_x:false});if(!l.data("bindEvent_buttonsContinuous_x")){f.bind("mousedown touchstart MSPointerDown",function(e){e.preventDefault();var t=L();l.data({mCSB_buttonScrollRight:setInterval(function(){l.mCustomScrollbar("scrollTo",Math.abs(r.position().left)+t,{trigger:"internal",scrollEasing:"easeOutCirc"})},17)})});var T=function(e){e.preventDefault();clearInterval(l.data("mCSB_buttonScrollRight"))};f.bind("mouseup touchend MSPointerUp mouseout MSPointerOut",T);a.bind("mousedown touchstart MSPointerDown",function(e){e.preventDefault();var t=L();l.data({mCSB_buttonScrollLeft:setInterval(function(){l.mCustomScrollbar("scrollTo",Math.abs(r.position().left)-t,{trigger:"internal",scrollEasing:"easeOutCirc"})},17)})});var N=function(e){e.preventDefault();clearInterval(l.data("mCSB_buttonScrollLeft"))};a.bind("mouseup touchend MSPointerUp mouseout MSPointerOut",N);l.data({bindEvent_buttonsContinuous_x:true})}}else{u.add(o).unbind("click");l.data({bindEvent_buttonsPixels_y:false});if(!l.data("bindEvent_buttonsContinuous_y")){u.bind("mousedown touchstart MSPointerDown",function(e){e.preventDefault();var t=L();l.data({mCSB_buttonScrollDown:setInterval(function(){l.mCustomScrollbar("scrollTo",Math.abs(r.position().top)+t,{trigger:"internal",scrollEasing:"easeOutCirc"})},17)})});var C=function(e){e.preventDefault();clearInterval(l.data("mCSB_buttonScrollDown"))};u.bind("mouseup touchend MSPointerUp mouseout MSPointerOut",C);o.bind("mousedown touchstart MSPointerDown",function(e){e.preventDefault();var t=L();l.data({mCSB_buttonScrollUp:setInterval(function(){l.mCustomScrollbar("scrollTo",Math.abs(r.position().top)-t,{trigger:"internal",scrollEasing:"easeOutCirc"})},17)})});var k=function(e){e.preventDefault();clearInterval(l.data("mCSB_buttonScrollUp"))};o.bind("mouseup touchend MSPointerUp mouseout MSPointerOut",k);l.data({bindEvent_buttonsContinuous_y:true})}}function L(){var e=l.data("scrollButtons_scrollSpeed");if(l.data("scrollButtons_scrollSpeed")==="auto"){e=Math.round((l.data("scrollInertia")+100)/40)}return e}}}if(l.data("autoScrollOnFocus")){if(!l.data("bindEvent_focusin")){t.bind("focusin",function(){t.scrollTop(0).scrollLeft(0);var n=e(document.activeElement);if(n.is("input,textarea,select,button,a[tabindex],area,object")){var i=r.position().top,s=n.position().top,o=t.height()-n.outerHeight();if(l.data("horizontalScroll")){i=r.position().left;s=n.position().left;o=t.width()-n.outerWidth()}if(i+s<0||i+s>o){l.mCustomScrollbar("scrollTo",s,{trigger:"internal"})}}});l.data({bindEvent_focusin:true})}}if(l.data("autoHideScrollbar")){if(!l.data("bindEvent_autoHideScrollbar")){t.bind("mouseenter",function(e){t.addClass("mCS-mouse-over");n.showScrollbar.call(t.children(".mCSB_scrollTools"))}).bind("mouseleave touchend",function(e){t.removeClass("mCS-mouse-over");if(e.type==="mouseleave"){n.hideScrollbar.call(t.children(".mCSB_scrollTools"))}});l.data({bindEvent_autoHideScrollbar:true})}}},scrollTo:function(t,r){function E(e){this.mcs={top:a.position().top,left:a.position().left,draggerTop:h.position().top,draggerLeft:h.position().left,topPct:Math.round(100*Math.abs(a.position().top)/Math.abs(a.outerHeight()-u.height())),leftPct:Math.round(100*Math.abs(a.position().left)/Math.abs(a.outerWidth()-u.width()))};switch(e){case"onScrollStart":i.data("mCS_tweenRunning",true).data("onScrollStart_Callback").call(i,this.mcs);break;case"whileScrolling":i.data("whileScrolling_Callback").call(i,this.mcs);break;case"onScroll":i.data("onScroll_Callback").call(i,this.mcs);break;case"onTotalScrollBack":i.data("onTotalScrollBack_Callback").call(i,this.mcs);break;case"onTotalScroll":i.data("onTotalScroll_Callback").call(i,this.mcs);break}}var i=e(this),s={moveDragger:false,trigger:"external",callbacks:true,scrollInertia:i.data("scrollInertia"),scrollEasing:i.data("scrollEasing")},r=e.extend(s,r),o,u=i.children(".mCustomScrollBox"),a=u.children(".mCSB_container"),f=u.children(".mCSB_scrollTools"),l=f.children(".mCSB_draggerContainer"),h=l.children(".mCSB_dragger"),p=draggerSpeed=r.scrollInertia,v,m,g,y;if(!a.hasClass("mCS_no_scrollbar")){i.data({mCS_trigger:r.trigger});if(i.data("mCS_Init")){r.callbacks=false}if(t||t===0){if(typeof t==="number"){if(r.moveDragger){o=t;if(i.data("horizontalScroll")){t=h.position().left*i.data("scrollAmount")}else{t=h.position().top*i.data("scrollAmount")}draggerSpeed=0}else{o=t/i.data("scrollAmount")}}else{if(typeof t==="string"){var b;if(t==="top"){b=0}else{if(t==="bottom"&&!i.data("horizontalScroll")){b=a.outerHeight()-u.height()}else{if(t==="left"){b=0}else{if(t==="right"&&i.data("horizontalScroll")){b=a.outerWidth()-u.width()}else{if(t==="first"){b=i.find(".mCSB_container").find(":first")}else{if(t==="last"){b=i.find(".mCSB_container").find(":last")}else{b=i.find(t)}}}}}}if(b.length===1){if(i.data("horizontalScroll")){t=b.position().left}else{t=b.position().top}o=t/i.data("scrollAmount")}else{o=t=b}}}if(i.data("horizontalScroll")){if(i.data("onTotalScrollBack_Offset")){m=-i.data("onTotalScrollBack_Offset")}if(i.data("onTotalScroll_Offset")){y=u.width()-a.outerWidth()+i.data("onTotalScroll_Offset")}if(o<0){o=t=0;clearInterval(i.data("mCSB_buttonScrollLeft"));if(!m){v=true}}else{if(o>=l.width()-h.width()){o=l.width()-h.width();t=u.width()-a.outerWidth();clearInterval(i.data("mCSB_buttonScrollRight"));if(!y){g=true}}else{t=-t}}var w=i.data("snapAmount");if(w){t=Math.round(t/w)*w-i.data("snapOffset")}n.mTweenAxis.call(this,h[0],"left",Math.round(o),draggerSpeed,r.scrollEasing);n.mTweenAxis.call(this,a[0],"left",Math.round(t),p,r.scrollEasing,{onStart:function(){if(r.callbacks&&!i.data("mCS_tweenRunning")){E("onScrollStart")}if(i.data("autoHideScrollbar")){n.showScrollbar.call(f)}},onUpdate:function(){if(r.callbacks){E("whileScrolling")}},onComplete:function(){if(r.callbacks){E("onScroll");if(v||m&&a.position().left>=m){E("onTotalScrollBack")}if(g||y&&a.position().left<=y){E("onTotalScroll")}}h.data("preventAction",false);i.data("mCS_tweenRunning",false);if(i.data("autoHideScrollbar")){if(!u.hasClass("mCS-mouse-over")){n.hideScrollbar.call(f)}}}})}else{if(i.data("onTotalScrollBack_Offset")){m=-i.data("onTotalScrollBack_Offset")}if(i.data("onTotalScroll_Offset")){y=u.height()-a.outerHeight()+i.data("onTotalScroll_Offset")}if(o<0){o=t=0;clearInterval(i.data("mCSB_buttonScrollUp"));if(!m){v=true}}else{if(o>=l.height()-h.height()){o=l.height()-h.height();t=u.height()-a.outerHeight();clearInterval(i.data("mCSB_buttonScrollDown"));if(!y){g=true}}else{t=-t}}var w=i.data("snapAmount");if(w){t=Math.round(t/w)*w-i.data("snapOffset")}n.mTweenAxis.call(this,h[0],"top",Math.round(o),draggerSpeed,r.scrollEasing);n.mTweenAxis.call(this,a[0],"top",Math.round(t),p,r.scrollEasing,{onStart:function(){if(r.callbacks&&!i.data("mCS_tweenRunning")){E("onScrollStart")}if(i.data("autoHideScrollbar")){n.showScrollbar.call(f)}},onUpdate:function(){if(r.callbacks){E("whileScrolling")}},onComplete:function(){if(r.callbacks){E("onScroll");if(v||m&&a.position().top>=m){E("onTotalScrollBack")}if(g||y&&a.position().top<=y){E("onTotalScroll")}}h.data("preventAction",false);i.data("mCS_tweenRunning",false);if(i.data("autoHideScrollbar")){if(!u.hasClass("mCS-mouse-over")){n.hideScrollbar.call(f)}}}})}if(i.data("mCS_Init")){i.data({mCS_Init:false})}}}},stop:function(){var t=e(this),r=t.children().children(".mCSB_container"),i=t.children().children().children().children(".mCSB_dragger");n.mTweenAxisStop.call(this,r[0]);n.mTweenAxisStop.call(this,i[0])},disable:function(t){var n=e(this),r=n.children(".mCustomScrollBox"),i=r.children(".mCSB_container"),s=r.children(".mCSB_scrollTools"),o=s.children().children(".mCSB_dragger");r.unbind("mousewheel focusin mouseenter mouseleave touchend");i.unbind("touchstart touchmove");if(t){if(n.data("horizontalScroll")){o.add(i).css("left",0)}else{o.add(i).css("top",0)}}s.css("display","none");i.addClass("mCS_no_scrollbar");n.data({bindEvent_mousewheel:false,bindEvent_focusin:false,bindEvent_content_touch:false,bindEvent_autoHideScrollbar:false}).addClass("mCS_disabled")},destroy:function(){var t=e(this);t.removeClass("mCustomScrollbar _mCS_"+t.data("mCustomScrollbarIndex")).addClass("mCS_destroyed").children().children(".mCSB_container").unwrap().children().unwrap().siblings(".mCSB_scrollTools").remove();e(document).unbind("mousemove."+t.data("mCustomScrollbarIndex")+" mouseup."+t.data("mCustomScrollbarIndex")+" MSPointerMove."+t.data("mCustomScrollbarIndex")+" MSPointerUp."+t.data("mCustomScrollbarIndex"));e(window).unbind("resize."+t.data("mCustomScrollbarIndex"))}},n={showScrollbar:function(){this.stop().animate({opacity:1},"fast")},hideScrollbar:function(){this.stop().animate({opacity:0},"fast")},mTweenAxis:function(e,t,n,r,i,s){function v(){if(window.performance&&window.performance.now){return window.performance.now()}else{if(window.performance&&window.performance.webkitNow){return window.performance.webkitNow()}else{if(Date.now){return Date.now()}else{return(new Date).getTime()}}}}function m(){if(!c){o.call()}c=v()-f;g();if(c>=e._time){e._time=c>e._time?c+l-(c-e._time):c+l-1;if(e._time<c+1){e._time=c+1}}if(e._time<r){e._id=_request(m)}else{a.call()}}function g(){if(r>0){e.currVal=w(e._time,h,d,r,i);p[t]=Math.round(e.currVal)+"px"}else{p[t]=n+"px"}u.call()}function y(){l=1e3/60;e._time=c+l;_request=!window.requestAnimationFrame?function(e){g();return setTimeout(e,.01)}:window.requestAnimationFrame;e._id=_request(m)}function b(){if(e._id==null){return}if(!window.requestAnimationFrame){clearTimeout(e._id)}else{window.cancelAnimationFrame(e._id)}e._id=null}function w(e,t,n,r,i){switch(i){case"linear":return n*e/r+t;break;case"easeOutQuad":e/=r;return-n*e*(e-2)+t;break;case"easeInOutQuad":e/=r/2;if(e<1){return n/2*e*e+t}e--;return-n/2*(e*(e-2)-1)+t;break;case"easeOutCubic":e/=r;e--;return n*(e*e*e+1)+t;break;case"easeOutQuart":e/=r;e--;return-n*(e*e*e*e-1)+t;break;case"easeOutQuint":e/=r;e--;return n*(e*e*e*e*e+1)+t;break;case"easeOutCirc":e/=r;e--;return n*Math.sqrt(1-e*e)+t;break;case"easeOutSine":return n*Math.sin(e/r*(Math.PI/2))+t;break;case"easeOutExpo":return n*(-Math.pow(2,-10*e/r)+1)+t;break;case"mcsEaseOut":var s=(e/=r)*e,o=s*e;return t+n*(.499999999999997*o*s+ -2.5*s*s+5.5*o+ -6.5*s+4*e);break;case"draggerRailEase":e/=r/2;if(e<1){return n/2*e*e*e+t}e-=2;return n/2*(e*e*e+2)+t;break}}var s=s||{},o=s.onStart||function(){},u=s.onUpdate||function(){},a=s.onComplete||function(){};var f=v(),l,c=0,h=e.offsetTop,p=e.style;if(t==="left"){h=e.offsetLeft}var d=n-h;b();y()},mTweenAxisStop:function(e){if(e._id==null){return}if(!window.requestAnimationFrame){clearTimeout(e._id)}else{window.cancelAnimationFrame(e._id)}e._id=null},rafPolyfill:function(){var e=["ms","moz","webkit","o"],t=e.length;while(--t>-1&&!window.requestAnimationFrame){window.requestAnimationFrame=window[e[t]+"RequestAnimationFrame"];window.cancelAnimationFrame=window[e[t]+"CancelAnimationFrame"]||window[e[t]+"CancelRequestAnimationFrame"]}}};n.rafPolyfill.call();e.support.touch=!!("ontouchstart"in window);e.support.msPointer=window.navigator.msPointerEnabled;var r="https:"==document.location.protocol?"https:":"http:";e.event.special.mousewheel||document.write("");e.fn.mCustomScrollbar=function(n){if(t[n]){return t[n].apply(this,Array.prototype.slice.call(arguments,1))}else{if(typeof n==="object"||!n){return t.init.apply(this,arguments)}else{e.error("Method "+n+" does not exist")}}}})(jQuery);

  }
    
    

    /**
	 * Retrieves venue based on event id
	 * 
	 * @param {Object}
	 *            jq - Local jquery variable
	 * @param {Number}
	 *            eventVenueId - Event's Venue Id
	 * @param {Array}
	 *            venuesToSearch - Array of venues
	 * @return {Object} venue - First Matched Venue Object By Id
	 */

    function getVenueForEvent(jq, eventVenueId, venuesToSearch) {
        var match = jq.grep(venuesToSearch, function(venue) {
            return venue.id === eventVenueId;
        });
        return match ? match[0] : null;
    }

    /**
	 * Constructs the request url of the api
	 * 
	 * @param {Object}
	 *            jq - Local Jquery Variable
	 * @param {Object}
	 *            widgetOpts - container for all custom information
	 * @return {String} requestUrl with serialized query params.
	 */

    function constructUrl(jq, widgetOpts,servletName) {
        var queryUrl,  serializedQueryParam;
        queryUrl = widgetOpts.partnerBaseUrl+servletName;
        if (widgetOpts.brandName) {
            queryParams.entity = widgetOpts.brandName;
        }
        if (widgetOpts.productName) {
            queryParams.subProduct = widgetOpts.productName;
        }
        if (widgetOpts.productId) {
            queryParams.ProductId = widgetOpts.productId;
        }
        if(widgetOpts.partnerBaseUrl){
                queryParams.url=widgetOpts.partnerBaseUrl;
        }
        if(widgetOpts.category){
                queryParams.category=widgetOpts.category;
        } 
        if(widgetOpts.price){
            queryParams.price=widgetOpts.price;
        }
        queryParams.skip=0;
        // this is required to tell jquery to not serialize the queryparams with
                // [] for arrays
        jq.ajaxSetup({
            traditional: true
        });
        serializedQueryParam = jq.param(queryParams);
        jq.ajaxSetup({
            traditional: false
        });
        queryUrl += '?' + serializedQueryParam;
        return queryUrl;
    }

    function isPositiveInteger(str) {
        var n = ~~Number(str);
        return String(n).valueOf() === String(str).valueOf() && n >= 0;
    }
    
      
};



