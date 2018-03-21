// JavaScript Document
var featurejsonurl;
var header=1;    // 1 indicates the header should be comments else header
					// would be products
$(document)
		.ready(
				function() {
			

					$(this).removeClass('nosearch');
					$('.searchmsg').remove();

					$('.data_container ul').removeClass('highlighted_key');
				
					var entity = getUrlParameter('entity');
					if(entity!=null){
						var url =  'url(pages/images/ecomwidget/'+entity+') no-repeat';
						$(".page").css('background', url);
					}
					
					var subProduct = getUrlParameter('subProduct');
					featurejsonurl = "Files/"+entity+"_"+subProduct+".json";				
				
					
					$(".data_container").mCustomScrollbar({

						scrollButtons : {
							enable : true
						}
					});
					$(".data_container1").mCustomScrollbar({
						scrollButtons : {
							enable : true
						}
					});
					$(".review_container").mCustomScrollbar({
						scrollButtons : {
							enable : true
						},
					advanced:{
				        updateOnContentResize: true
				    }
					});
					$('.tabs_panel li').click(function() {
						$(".data_container").mCustomScrollbar("update");
						$(".data_container1").mCustomScrollbar("update");
					});

					$('#span_back').click(function(){
						  $('#span_back').removeClass('span_back').addClass('span_back_hidden');
						  $('div.selected_key').find('.keyword_header').click();	
						 
					});
					
					$('#feature_sorting li a').click(
							function() {
								$(this).parent().click();
								mode = $(this).attr('href');

								$('#feature_sorting li ').removeClass(
										'selected');
								$(this).parent().addClass('selected');
								mode = mode.replace('#', '');
								$.each($('.data_container1 .keys_block '),
										function(index, item) {

											$(this).removeClass('hide_div');

										});
								$.each($('.data_container1 .front .lbl '),
										function(index, item) {
											$(this).removeClass('hide_label');
										});
								$.each($('.data_container1 .keys_block'),
										function(index, item) {
									// $(this).addClass('animated rotateIn');
											if (!$(this).hasClass(mode)) {

												$(this).addClass('hide_div');

											}
										});
								$.each($('.data_container1 .front .lbl '),
										function(index, item) {
											if (!$(this).hasClass(mode)) {
												$(this).addClass('hide_label');
											}
										});
								
/*
 * $.each($('.data_container1 .keys_block'), function(index, item) {
 * 
 * $(this).animate({ height : '50px', opacity : '1' }, 200, function() {
 * $(this).removeClass('hidden'); $(this).css({ height : 'auto', }); //
 * $(".review_container").mCustomScrollbar("update"); });
 * 
 * });
 */
								
								$(".data_container1")
										.mCustomScrollbar("update");
								
								
								return false;
							});
				$('#keyword_sorting li a')
							.click(
									function() {
										
										// $(this).parent().click();
										
										mode = $(this).attr('href');
										$('#keyword_sorting li').removeClass(
												'selected');
										$(this).parent().addClass('selected');
										mode = mode.replace('#', '');
									
										$.each(
														$('.data_container .keys '),
														function(index, item) {
															// $(this).removeClass('animated
															// rotateIn');
															$(this).removeClass(
															'hide_div');
															$(this).find('.front .lbl').each(function() {

																 $(this)
																 .removeClass(
																 'hide_label'); 
																if (!$(this)
																		.hasClass(
																				mode)) {
																	$(this)
																			.addClass(
																					'hide_label');
																}
															
															});	
															
														// $(this).each($('.front
														// .lbl'),function(index,itemlbl){});
															if (!$(this)
																	.hasClass(
																			mode)) {
																$(this)
																		.addClass(
																				'hide_div');

															}
															else{
																
													          // $(this).addClass('animated
																// rotateIn');
															}
														});
										
										$(".data_container").mCustomScrollbar(
												"update");
										return false;
									});
					
					
					
				    
					
					/*
					 * $('.keys_block .keyword_block_header') .click( function()
					 * {}); $('.keys_block .keyword_count .keyword_positive')
					 * .click( function() {}); $('.keys_block .keyword_count
					 * .keyword_negative') .click( function() {}); $('.keys
					 * .keyword_header').click( ); $('.keys .keyword_count
					 * .keyword_positive') .click( function() {}); $('.keys
					 * .keyword_count .keyword_negative') .click( function()
					 * {});
					 */

					$('.slidepanel').click(function() {
						if ($('.info_panel').hasClass('panel_close')) {
							w = $('#resizable').width();
							$('.info_panel').addClass('panel_open');
							$('.info_panel').removeClass('panel_close');
							dx = $('.info_panel').width() - 30;
							$('.info_panel').animate({
								right : '+=' + dx
							}, 500, function() {
								// Animation complete.
							});
						} else {
							$('.info_panel').removeClass('panel_open');
							$('.info_panel').addClass('panel_close');
							dx = $('.info_panel').width() - 30;
							$('.info_panel').animate({
								right : '-=' + dx
							}, 500, function() {
								// Animation complete.
							});
						}
					});
					$('.showcase_btn').click(function() {
						
						current = $(this);
						$('.popup_overlay').css({
							'width' : $(window).width(),
							'height' : $(window).height()
						}).show();
						$(this).animate({
							right : '0px'
						}, 500, function() {
							$(this).hide();
							width = $('#resizable').outerWidth();
							if (!$(this).hasClass('open')) {
								$('.outer_wrapper').animate({
									right : '-1px'
								}, 500, function() {
									$('.info_panel').animate({
										right : '180px'
									}, 500);
									$('.icon').addClass('open');
								});
							}
						});
						addFeatures();
						loadfeatureeffect();
					});
					$('.minimize_panel').click(function() {
						if ($('#info_panel').hasClass('panel_open')) {
							$('#info_panel').animate({
								left : 'auto',
								right : '150px',
								width : '400px'
							}, 500, function() {
							});
						}
						$('.info_panel').animate({
							left : 'auto',
							right : '150px'
						}, 500, function() {
							if ($('.info_panel').hasClass('panel_open')) {
								$('.info_panel').removeClass('panel_open');
								$('.info_panel').addClass('panel_close');
							}
							$('.outer_wrapper').css('left', 'auto');
							// $('.outer_wrapper').animate({left:'auto',height:'400px',width:'400px',right:'-1px',top:'150px'},
							// 500,function(){});
							width = $('#resizable').outerWidth() + 2;
							$('.outer_wrapper').animate({
								right : '-' + width + 'px',
								left : ' 0px'
							}, 500, function() {
								$('.popup_overlay').hide();
								$('.icon').removeClass('open');
								$('.showcase_btn').show();
								$('.showcase_btn').animate({
									right : '100px'
								}, 500, function() {
								});
							});

						});

					});
					$('.checkbox')
							.click(
									function() {
										mode = $(this).parent().attr('class');

										mode = mode.replace('_', '');
										
										if ($(this).hasClass('select_type')) {
											$(this).removeClass('select_type');
											$(this).addClass('unselect_type');
											$
													.each(
															$('.review_container .review'),
															function(index,
																	item) {
																if ($(this)
																		.hasClass(
																				mode)) {
																	$(this)
																			.parent()
																			.animate(
																					{
																						height : '0px',
																						opacity : '0'
																					},
																					500,
																					function() {
																						$(
																								this)
																								.addClass(
																										'hidden');
																						$(
																								".review_container")
																								.mCustomScrollbar(
																										"update");
																					});
																}
															});
										} else {
											$(this)
													.removeClass(
															'unselect_type');
											$(this).addClass('select_type');
											$
													.each(
															$('.review_container .review'),
															function(index,
																	item) {
																if ($(this)
																		.hasClass(
																				mode)) {
																	$(this)
																			.parent()
																			.animate(
																					{
										
																						height : '85px',
																						opacity : '1'
																					},
																					500,
																					function() {
																						$(
																								this)
																								.removeClass(
																										'hidden');
																						$(
																								this)
																								.css(
																										{
																											height : 'auto'
																										});
																						$(
																								".review_container")
																								.mCustomScrollbar(
																										"update");
																					});
																}
															});
										}

									});

					$('#submit_key')
							.click(
									function() {
										$('.data_container ul').removeClass(
												'highlighted_key');
										found = false;
										$('.searchmsg').remove();
										$
												.each(
														$('.data_container ul'),
														function(index, item) {
															$match = '';
															// console.log($(item).find('.front
															// span').text());
															// $match =
															// $(item).find('.front
															// span').text().match($.trim($('#submit_key').val()));
															$match = $(item)
																	.find(
																			'.front span')
																	.text();

															if ($match == $
																	.trim($(
																			'#search_key')
																			.val())) {
																// alert("No
																// results
																// found");
																$(item)
																		.addClass(
																				'highlighted_key');
																$('#search_key')
																		.removeClass(
																				'nosearch');
																found = true;
															} else {
																$(item)
																		.parent()
																		.addClass(
																				'hidesearch');
																$(
																		".data_container")
																		.mCustomScrollbar(
																				"update");

															}

														});
										if (!found) {
											$('#search_key').addClass(
													'nosearch');
											$('.searchmsg').remove();
											$('.clear_search').show();
											$('.data_container .mCSB_container')
													.append(
															'<p class="searchmsg">No result found.</p>');
										}
										return false;
									});
					$('#search_key').focus(function() {
						$(this).removeClass('nosearch');
						$('.searchmsg').remove();

						$('.data_container ul').removeClass('highlighted_key');
					});
					$("#search_key")
							.keypress(
									function(e) {
										if (e.keyCode == 13) {
											e.preventDefault();
											$('.searchmsg').remove();
											$('.data_container ul')
													.removeClass(
															'highlighted_key');
											found = false;
											$
													.each(
															$('.data_container ul'),
															function(index,
																	item) {
																$match = '';
																// console.log($(item).find('.front
																// span').text());
																// $match =
																// $(item).find('.front
																// span').text().match($.trim($('#submit_key').val()));
																$match = $(item)
																		.find(
																				'.front span')
																		.text();

																if ($match == $
																		.trim($(
																				'#search_key')
																				.val())) {
																	// alert("No
																	// results
																	// found");
																	$(item)
																			.addClass(
																					'highlighted_key');
																	$(item)
																			.parent()
																			.removeClass(
																					'hidesearch');
																	$(
																			'#search_key')
																			.removeClass(
																					'nosearch');
																	found = true;
																} else {
																	$(item)
																			.parent()
																			.addClass(
																					'hidesearch');
																	$(
																			".data_container")
																			.mCustomScrollbar(
																					"update");

																}

															});
											if (!found) {
												$('#search_key').addClass(
														'nosearch');
												$('.searchmsg').remove();
												$('.clear_search').show();
												$(
														'.data_container .mCSB_container')
														.append(
																'<p class="searchmsg">No result found.</p>');
											}

										}
									});
					$('.clear_search').click(function() {
						$('#search_key').removeClass('nosearch');
						$('#search_key').val('');
						$('.searchmsg').remove();
						$('.clear_search').hide();
						$('.data_container .keys').removeClass('hidesearch');
						$('.data_container ul').removeClass('highlighted_key');
						$(".data_container").mCustomScrollbar("update");
					});
					$('#search_key').focusout(
							function() {
								if ($.trim($('#search_key').val())) {
									$('.clear_search').show();
									$('.searchmsg').remove();
									$('.data_container .keys').removeClass(
											'hidesearch');
									$(".data_container").mCustomScrollbar(
											"update");
								} else {
									$('.clear_search').hide();
									$('#search_key').removeClass('nosearch');
									$('.searchmsg').remove();
									$('.data_container ul').removeClass(
											'highlighted_key');
									$('.data_container .keys').removeClass(
											'hidesearch');
								}
							});

					$('.review_container').on(
							"click",
							".review",
							function() {
								if ($(this).find('.review_comment').hasClass(
										'expand')) {
									h = $(this).find('.review_comment p')
											.height();
									$(this).find('.review_comment').animate({
										'height' : h + 'px'
									}, 100);
									$(this).find('.review_comment')
											.removeClass('expand');
									
								} else {
									$(this).find('.review_comment').animate({
										'height' : '34px'
									}, 100);
									$(this).find('.review_comment').addClass(
											'expand');
									
								}
								/*
								 * $(".review_container").mCustomScrollbar(
								 * "update");
								 */
							});
				
				
					$(".keys")
							.hover(
									function() {
										dw = -14;
										dh = -14;
										w = $(this).width() + 28;
										h = $(this).height()+28;
										$(this).children().addClass('hover');
										$(this)
												.children()
												.css(
														{
															'-moz-transform' : 'rotateY(0deg) translate('
																	+ dw
																	+ 'px, '
																	+ dh
																	+ 'px) ',
															'-webkit-transform' : 'rotateY(0deg) translate('
																	+ dw
																	+ 'px, '
																	+ dh
																	+ 'px) ',
															'-o-transform' : 'rotateY(0deg) translate('
																	+ dw
																	+ 'px, '
																	+ dh
																	+ 'px) ',
															'transform' : 'rotateY(0deg) translate('
																	+ dw
																	+ 'px, '
																	+ dh
																	+ 'px)',
															'width' : w + 'px',
															'height' : h + 'px',
															'z-index' : '1000',

														});
									},
									function() {
										$(this).children().removeClass('hover');
										w = $(this).width();
										h = $(this).height();
										$(this)
												.children()
												.css(
														{
															'transform' : 'rotateY(-180deg) translate(0px, 0px)',
															'-moz-transform' : 'rotateY(-180deg) translate(0px, 0px)',
															'-webkit-transform' : 'rotateY(-180deg) translate(0px, 0px)',
															'-o-transform' : 'rotateY(-180deg) translate(0px, 0px)',
															'width' : w + 'px',
															'height' : h + 'px',
															'z-index' : '10',

														});
									});
					
					$(".data_container1").on("hover",".keys_block",function(e) {
						  if(e.type == "mouseenter") {

								dw = -14;
								dh = -14;
								w = $(this).width() +24;
								var spancount = $(this).find('.keyword_count span').length;
								h = (($(this).find('.keyword_count span').height() * spancount)) +$(this).find('span').height()-4;
								$(this).children().addClass('hover');
								
								$(this)
										.children()
										.css(
												{
													'-moz-transform' : 'rotateY(0deg) translate('
															+ dw
															+ 'px, '
															+ dh
															+ 'px)',
													'-webkit-transform' : 'rotateY(0deg) translate('
															+ dw
															+ 'px, '
															+ dh
															+ 'px)',
													'-o-transform' : 'rotateY(0deg) translate('
															+ dw
															+ 'px, '
															+ dh
															+ 'px)',
													'transform' : 'rotateY(0deg) translate('
															+ dw
															+ 'px, '
															+ dh
															+ 'px)',
													'width' : w + 'px',
													'height' : h + 'px',
													'overflow':'none !important'
												// 'z-index' : '1000'

												});
								$(this)	.css({
									'z-index' : '1000'

								});
								
							
						  }
						  else if (e.type == "mouseleave") {

								
								$(this).children().removeClass('hover');
								w = $(this).width();										
								h = $(this).height();
								$(this)
										.children()
										.css(
												{
													'-moz-transform' : 'rotateY(-180deg) translate(0px, 0px)',
													'-webkit-transform' : 'rotateY(-180deg) translate(0px, 0px)',
													'-o-transform' : 'rotateY(-180deg) translate(0px, 0px)',
													'transform' : 'rotateY(-180deg) translate(0px, 0px)',
													'width' : w + 'px',
													'height' : h + 'px',
													'overflow' : 'hidden'
													// 'z-index' : '10'

												});
								$(this)	.css({
									'z-index' : '10'

								});
							
						  }
						});
					
					
					 $('.viewProduct').on({
							
						 mouseenter: function() {			
						 $(this).find('.pop-up').show();
						 						
						 },
						 mouseleave: function() {
						 $(this).find('.pop-up').hide();
						 }
						 });
					 

							 $('.review_container').on("hover", ".viewProduct",
							function(e) {
								if (e.type == "mouseenter") {
									$(this).find('.pop-up').show();

								} else if (e.type == "mouseleave") {

									$(this).find('.pop-up').hide();
								}
							});

				/*
				 * $(".keys_block") .hover( function() {}, function() {});
				 */
					

				});


function getUrlVars()
{
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}

function getWords(subDimension)
{
	var words="[";
	
    $.ajax({
        
        url: featurejsonurl,                   //add the Url here
        dataType: "json",
        cache:false,
        crossDomain : true,
        async:false,
 	    success: function(data) {
    	   
 	$.each( data, function( i, value ) {

 		if(value.name==subDimension){
 		
 			$.each( value.children, function( k, loopword ){
 				words=words+loopword.name+",";
 		   
 });	
 	}
 });
 	words=words+"]";

 	 		},
 		
 		error:function(xhr, status, error) {
             alert(error+status+xhr);
         }
          });
  
return words;
	}

function allKeysClick( words, reviewType, skip, totalCount, element,productId, subDimension) {
	
	// Method call to populate comments part in 'info_panel' div
	var getParameters = getUrlVars();
	var entity=getParameters["entity"];
	var subproduct=getParameters["subProduct"];
	
	var subproducts=subproduct.split("#");
	 
	var showcomments=getParameters["showcomments"];
	if(showcomments==null){
		 $('#span_back').removeClass('span_back').addClass('span_back_hidden');
		 header=2;
		 }
	if(subDimension!=null)
		{
		header=1;
		}
	 if(productId!=null){
		 header=1;
		 getFeedsByIdForProducts(entity, subproducts[0], words, productId);
		
	 }
   
	 else {
	
		getFeedsById(entity,subproducts[0],words,reviewType,skip,totalCount,showcomments,subDimension);
		if(words==null||words=="null")
			{
		words=getWords(subDimension);
			}
		if(reviewType==1){
			$(".positive_review").show();
			 $(".negative_review").show();
		}
		else if(reviewType==2)
			{
			$(".positive_review").show();
			 $(".negative_review").hide();
			}
		else{
			 $(".positive_review").hide();
			 $(".negative_review").show();
			}
	}


	if(words !=null){
		var n=words.split(",");
		var searchString ="";
		var counter=0;
		for(var tot = n.length; counter < tot; counter++){
			 if((tot - counter) != 1){
				 var word = n[counter];
				 word = word.replace("[","");
			 searchString = searchString+word+",";
			 }
			 else{
				 searchString = searchString+n[counter].replace("]",""); 
			 }
		 }
		// $('.review_container').easymark('removeHighlight');
		// $('.review_container').easymark('highlight', searchString);
		    var myHilitor = new Hilitor("review_container");
		    myHilitor.setMatchType("left");
			myHilitor.apply(searchString);
		
	}
	
   if(skip==0){
	mode = element.className;
	tagname = "";
	dx = $('.info_panel').width() - 30;
	$('.keys').removeClass('selected_key');
	$('.keys_block').removeClass('selected_key');
   }
	if(mode == 'keyword_positive'){
		
		$(element).parent().parent().parent().addClass('selected_key');
		$('.negative_review .checkbox').removeClass('unselect_type').addClass(
		'unselect_type');
		$('.positive_review .checkbox').removeClass('unselect_type').addClass(
		'select_type');
		tagname = $(element).parent().parent().find('.back span').text();
	}else if(mode == 'keyword_negative'){
	
		$(element).parent().parent().parent().addClass('selected_key');
		$('.negative_review .checkbox').removeClass('unselect_type').addClass(
		'select_type');
		$('.positive_review .checkbox').removeClass('unselect_type').addClass(
		'unselect_type');
		tagname = $(element).parent().parent().find('.back span').text();
	}
	else{

	$(element).parent().parent().addClass('selected_key');
		$('.negative_review .checkbox').removeClass('unselect_type').addClass(
			'select_type');
	$('.positive_review .checkbox').removeClass('unselect_type').addClass(
			'select_type');
	tagname = $(element).find('.back span').text();
	}
	if(header==1 && skip==0){
	$('.header_section h3').empty().html(
			'Showing all user reviews with \''
					+ tagname + '\'');
	}
	else if(header==2 && skip==0)
		{
		$('.header_section h3').empty().html(
				'Showing all products with \''
						+ tagname + '\'');
		
		}
	if ($('.info_panel').hasClass('panel_close')) {
		$('.info_panel').addClass('panel_open');
		$('.info_panel').removeClass('panel_close');
		$('.info_panel').animate({
			right : '+=' + dx
		}, 500, function() {
			// Animation complete.
		});
	}
	$.each($('.review_container .review'), function(index, item) {

		$(this).animate({
			height : '50px',
			opacity : '1'
		}, 200, function() {
			$(this).removeClass('hidden');
			$(this).css({
				height : 'auto',
			});
			$(".review_container").mCustomScrollbar("update");
		});

	});
	
	$.each($('.review_container .review_p'), function(index, item) {

		$(this).animate({
			height : '50px',
			opacity : '1'
		}, 200, function() {
			$(this).removeClass('hidden');
			$(this).css({
				height : 'auto',
			});
			$(".review_container").mCustomScrollbar("update");
		});

	});

}


function getFeedsByIdForProducts(entity, subproduct, word, productId) {
	
	$.ajax({
		type : "POST",
		url : "getPostsFromMongo",
		data : "entity=" + entity + "&subproduct=" + subproduct + "&word=" + word  + "&reviewType=1"+"&skip=0"+"&limit=5"+"&productId="+productId,
		dataType : "json",
		success : function(data) {
           
         
			var dfs = FragBuilder(data);
			$('.review_container .mCSB_container').empty();
			
			$('.review_container .mCSB_container').append(dfs);
                       
		},
		async:   false,
		complete : function() {

		},
		error : function(e18188) {
			alert('Error' + e);
		}

	});

}

function getFeedsById(entity, subproduct, word, reviewType, skip, totalCount, showcomments , subDimension) {
	$('#loading').show();
	$.ajax({
		type : "POST",
		url : "getPostsFromMongo",
		data : "entity=" + entity + "&subproduct=" + subproduct + "&word=" + word + "&showcomments="+ showcomments + "&reviewType="+reviewType+"&skip="+skip+"&limit=5&subDimension="+subDimension,
		dataType : "json",
		success : function(data) {
       
			var dfs = FragBuilder(data);
			$('#loading').remove();
			$('#next').remove();

			if(skip==0){
			$('.review_container .mCSB_container').html(dfs);
			}
			else{
				
			$('.review_container .mCSB_container').append(dfs);
			}
		     skip=skip+5;
			if(skip<totalCount && subDimension!=null){
				$('.review_container .mCSB_container').append("<div id='loading'><center><img src='pages/images/loading.gif' alt='loading' width='50' height='5	0'></center></div>");
				 $('#loading').hide();
				 $('.review_container .mCSB_container').append("<div id='next'><center><a href='#'><input type='button' value='Show more' class='blueBtn'  onClick=\"allKeysClick('"+word+"\'," +reviewType+ "," + skip +","+ totalCount+",this,null,\'"+subDimension+"\')\"/></a></center></div>");
				
			}
			else if(skip<totalCount){
			$('.review_container .mCSB_container').append("<div id='loading'><center><img src='pages/images/loading.gif' alt='loading' width='100' height='100'></center></div>");
			 $('#loading').hide();
			 $('.review_container .mCSB_container').append("<div id='next'><center><a href='#'><input type='button' value='Show more' class='blueBtn'  onClick=\"allKeysClick('"+word+"\'," +reviewType+ "," + skip +","+ totalCount+",this)\"/></a></center></div>");
			
			}
                       
		},
		async:   false,
		complete : function() {

		},
		error : function(e18188) {
			alert('Error' + e);
		}

	});

}

 function addFeatures(){
	 
 $("#subcontainer").empty();
 $("#back").hide();
    $.ajax({
      
       url: featurejsonurl,                   // add the Url here
       dataType: "json",
	   cache:false,
           crossDomain : true,
	    success: function(data) {

	$.each( data, function( k, subDimension ){
	
			 $("#subcontainer").append("" +
			 		"<div class='keys_block animated rotateIn all_block catall positive_block cata negative_block catb'>" +
			 		"<ul><li class='keyword_block_header' onclick='allKeysClick(null,1,0,"+subDimension.size +",this,null,\""+subDimension.name+"\")';>" +
			 		"<div class='front'><span>"+subDimension.name+"</span>" +
			 				"<label class='lbl all_block'>"+subDimension.size+"</label>" +
			 						"<label class='lbl positive_block hide_label'>"+subDimension.posCount+"</label>" +
			 						"<label class='lbl negative_block hide_label'>"+subDimension.negCount+"</label></div>" +
			 						"<div class='back'><span ><center>"+subDimension.name+"</center></span>" +
			 								"<label>58287</label></div></li><li class='keyword_count'>" +
			 								"<span class='keyword_positive'  onclick='allKeysClick(null,2,0,"+subDimension.posCount +",this,null,\""+subDimension.name+"\")';><label>"+subDimension.posCount+"</label></span>" +
			 										"<span class='keyword_negative'  onclick='allKeysClick(null,3,0,"+subDimension.negCount +",this,null,\""+subDimension.name+"\")';><label>"+subDimension.negCount+"</label></span></li></ul></div>");
});		
		loadfeatureeffect();
		},
		
		error:function(xhr, status, error) {
            alert(error+status+xhr);
        }

         });
 }

/*function addFeatures(){
	$("#subcontainer").empty();
$("#back").hide();
 $.ajax({
      
       url: featurejsonurl,          // Please add the URL here
       dataType: "json",
	   cache:false,
           crossDomain : true,
	    success: function(data) {
			$.each( data, function( i, value ) {
				{
				 $("#subcontainer").append("<div class='keys_block animated rotateIn all_block catall positive_block cata negative_block catb'><ul><li class='keyword_block_header'>	<div class='front'><span>"+value.name+"</span><label class='lbl all_block'>"+value.size+"</label><label class='lbl positive_block hide_label'>"+value.posCount+"</label><label class='lbl negative_block hide_label'>"+value.negCount+"</label></div><div class='back'><span onClick='getsubDimension(\""+value.name+"\")'><center>"+value.name+"</center></span><label>58287</label></div></li><li class='keyword_count'><span class='keyword_positive_nohover'><label onClick='getsubDimension(\""+value.name+"\")'>"+value.posCount+"</label></span><span class='keyword_negative_nohover'><label class='nomask' onClick='getsubDimension(\""+value.name+"\")'>"+value.negCount+"</div></span></li></ul></div>");
			  	}
			});
			  loadfeatureeffect(); 
		},
		
		error:function(xhr, status, error) {
           // alert(error+status+xhr);
        }



         });
		





}*/

function loadfeatureeffect(){
	
	// setup array of colors and a variable to store the current
	// index

    var colors = ["metro-red", "metro-orange", "metro-violet", "metro-green","metro-blue","metro-turquoise","metro-yellow","metro-pink"];
    var classes = ["cell2x2", "cell2x2", "cell2x2", "cell2x2","cell2x2"];
    var widtharray = [140,140,140,140,140];
        currColor   = 0;
        currCell   = 0;
        alert();
	$('.keys_block').each(function(index, element) {
		
		w = $(this).children().width();
		/*
		 * w = $(this).children().width(); h = $(this).children().height();
		 * $(this).css({'width':w+'px'}); $(this).css({'height':h+'px'});
		 * 
		 * $(this).children().css({'width':w+'p_x'});
		 */
		var currentclass = classes[currCell];
			if(w > widtharray[currCell]){
				currentclass = 'cell2x2';
			}
			$(this).addClass(currentclass);// .addClass(colors[currColor]);
			$(this).children().addClass(currentclass).addClass(colors[currColor]);
			// increment the current index
			currColor++;
			currCell++;
		        // if the next index is greater than then number of
				// colors then reset to zero
		        if (currColor == colors.length) {
		        	currColor = 0;
		        }
		        if (currCell >= classes.length ) {
		        	currCell = 4;
		        }
			/*
			 * $(this).children().css({ 'width' : w + 'px' });
			 */
		});
	
	
 portfoliocall();
 $(".data_container1")
	.mCustomScrollbar("update");

}     
  function portfoliocall(){

	
	  // alert();
	// <!-- PORTFOLIO -->
	     $('#subcontainer').portfolio({    
	       // <!-- GRID SETTINGS -->
	         gridOffset:30,            // <!-- Manual Right Padding Offset for
										// 100% Width -->
	         cellWidth:70,            // <!-- The Width of one CELL in PX-->
	         cellHeight:70,             // <!-- The Height of one CELL in PX-->
	         cellPadding:10,             // <!-- Spaces Between the CELLS -->
	         entryProPage:50,             // <!-- The Max. Amount of the
											// Entries per Page, Rest made by
											// Pagination -->
	                
	      // <!-- CAPTION SETTING -->
	    captionOpacity:85,
	                
	   // <!-- FILTERING -->
	    filterList:"#feature_sorting",    // <!-- Which Filter is used for the
											// Filtering / Pagination -->
	     // <!-- title:"#selected-filter-title", Which Div should be used for
			// showing the Selected Title of the Filter -->
	                   
	    
	})  ;

   }       
  
  function loadkeywordeffect(){

	// <!-- PORTFOLIO -->
	     $('#keywordsubcontainer').portfolio({    
	       // <!-- GRID SETTINGS -->
	         gridOffset:30,            // <!-- Manual Right Padding Offset for
										// 100% Width -->
	         cellWidth:120,            // <!-- The Width of one CELL in PX-->
	         cellHeight:30,             // <!-- The Height of one CELL in PX-->
	         cellPadding:10,             // <!-- Spaces Between the CELLS -->
	         entryProPage:50,             // <!-- The Max. Amount of the
											// Entries per Page, Rest made by
											// Pagination -->
	                
	      // <!-- CAPTION SETTING -->
	    captionOpacity:85,
	                
	   // <!-- FILTERING -->
	    filterList:"#keyword_sorting_div",    // <!-- Which Filter is used for
												// the Filtering / Pagination
												// -->
	     // <!-- title:"#selected-filter-title", Which Div should be used for
			// showing the Selected Title of the Filter -->
	                    
	 
	})  ;
  }
                                                                            
 function getUrlParameter(param){
	 var pageUrl = window.location.search.substring(1);
	 var urlVars = pageUrl.split("&");
	 for(var i=0; i<urlVars.length;i++){
		 var paramName = urlVars[i].split("=");
		 if(paramName[0]==param){
			 return paramName[1];
		 }
	 }
 }
 
 function setNullForProductId(){	
	
	 $('#span_back').removeClass('span_back').addClass('span_back_hidden');
	header=2;
 }
 
 function showCommentsForProd(productId,word){
	  allKeysClick( word, 1, 0, 50,  $('div.selected_key').find('.keyword_header'), productId);
	  $('#span_back').removeClass('span_back_hidden').addClass('span_back');
	  
 }
 function reviewCount(postId,isPositive)
 {
	  $.ajax({
	      type: "POST",
	       url: "ReviewCount",   
	       data:"postId="+postId+"&isPositive="+isPositive,
	       cache:false,
	       crossDomain : true,
		   success: function(data) {
		  }
		    });
	 
 }
 
 
