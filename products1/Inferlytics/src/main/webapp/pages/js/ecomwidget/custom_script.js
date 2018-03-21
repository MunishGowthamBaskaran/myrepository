	// JavaScript Document
	var globalHeight = 300; /*This is to set the review container height.Changes
					when resize is done.*/
	var calledProduct = false;
	var featurejsonurl;
	var skipLimit = 6; /* This keeps track of the number of products to be shown at once or
	   to load at once.*/
	var tagname; /* Keeps track of the Word that was clicked.*/
	var header = 1; /*
	 * 1 indicates the header should be (reviews) else header should
	 * be (products).
	 */
	var scrollPos = 0; /*Keeps track of the scroll position*/
	var scrollPosForProducts = 0;
	
	var limitForProduct = 6; /* Keep track of limt globally*/
	var skipForProd; /* Keep track of the skip Count globally*/
	var totalCountForProd; /*
	 * Keep track of the Total Count of products globally
	 * for the selected keyword.
	 */
	$(document)
			.ready(
					function() {
	
						/*	 $(window).scroll(function(){
								  
								  
								  $(".outer_wrapper").css({ "top" :
									  (150+window.pageYOffset)});
											  
								  	
								  });
						 */
						img1 = new Image(); // Preload the image
						img1.src = "../../pages/images/ecomwidget/showmorehover.png";
	
						$('#product_name').hide();
						var slices = getUrlParams();
	
						var entity = slices[3]; // Gives the Entity Name to display the background image
	
						if (entity != null) {
							var url = 'url(/Inferlytics/pages/images/ecomwidget/'
									+ entity // URL OF THE BACKGROUND IMAGE
									+ ') no-repeat';
							$(".page").css('background', url);
						}
	
						$(".data_container1").mCustomScrollbar({
							scrollButtons : {
								enable : true
							}
						});
						
						$('.header_section').hover(function() {
						    $(document).bind('mousewheel DOMMouseScroll',function(){ 
						        stopWheel(); 
						    });
						}, function() {
						    $(document).unbind('mousewheel DOMMouseScroll');
						});


						
						$(".review_container").mCustomScrollbar({
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
						$('.tabs_panel li').click(function() {
							$(".data_container1").mCustomScrollbar("update");
						});
						$('#span_back').click(
								function() {
									$(this).removeClass('span_back').addClass(
											'span_back_hidden');
									$('#product_name').hide();
									$('.review_container').height(globalHeight);
	
								});
	
						$(".n").hover(function() {
							$(this).append($("<span> ***</span>"));
						}, function() {
							$(this).find("span:last").remove();
						});
	
						$('.slidepanel').click(function() {
							if ($('.info_panel').hasClass('panel_open')) {
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
											right : $('#resizable').width() - 470
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
									right : '0px',
									width : "498px"
								}, 500, function() {
								});
							}
							$('.info_panel').animate({
								left : 'auto',
								right : '0px'
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
										right : '110px'
									}, 500, function() {
									});
								});
	
							});
	
						});
	
						$('.review_container')
								.on(
										"click",
										".review",
										function() {
											if ($(this).find('.review_comment')
													.hasClass('expand')) {
												if (!calledProduct) {
													var word = $('div.selected_key')
															.find('.keyword_header')
															.find('.back span')
															.text();
													if (word == "") {
														word = $('div.selected_key')
																.find(
																		'.keyword_block_header')
																.find('.back span')
																.text();
	
													}
													getClientDetailsForComments(
															word, $(this)
																	.attr('id'), $(
																	this).attr(
																	'subDim'));
												} else {
													calledProduct = false;
												}
	
												h = $(this).find(
														'.review_comment p')
														.height();
	
												$(this).find('.review_comment')
														.removeClass('expand');
												$(this)
														.find('.review_comment')
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
																							$(
																									".review_container")
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
												$(this).find('.review_comment')
														.addClass('expand');
	
												$(this)
														.find('.review_comment')
														.animate(
																{
																	'height' : '54px'
																},
																{
																	duration : 50,
																	complete : function() {
																		var timerId = null;
																		timerId = window
																				.setInterval(
																						function() {
																							$(
																									".review_container")
																									.mCustomScrollbar(
																											"update");
																							window
																									.clearInterval(timerId);
																						},
																						50);
	
																	}
																});
	
											}
	
										});
	
					});
	
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
	
	function allKeysClick(words, reviewType, skip, totalCount, element, productId,
			subDimension) {
		var slices = getUrlParams();
		var entity = slices[3]; // Gives the Entity Name
		var subproduct = slices[4]; // Gives the SubProduct name
		var subproducts = subproduct.split("#");// Remove #symbol from the URL if
		// present
		var showcomments = null;
		if (showcomments == null) {
			$('#span_back').removeClass('span_back').addClass('span_back_hidden');
			$('#product_name').hide();
			$('.review_container').height(globalHeight);
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
			getFeedsByIdForProducts(entity, subproducts[0], words, productId,
					totalCount, skip, subDimension, reviewType);
			$(".positive_review").show();
			$(".negative_review").show();
	
		}
	
		else {
	
			tagname = words;
			if (words == "null")
				tagname = subDimension;
			header = 2;
			if ((limitForProduct <= skipLimit)) {
				skipForProd = skip;
			}
			totalCountForProd = totalCount;
			getProducts(entity, subproducts[0], words, reviewType, skip,
					totalCount, showcomments, subDimension);
	
		}
		$.each($('.review_container .review'), function(index, item) {
			var wordToHighlight = $(this).attr('word');
			var myHilitor = new Hilitor($(this).attr('id'));
			myHilitor.setMatchType("left");
			myHilitor.apply(wordToHighlight);
		});
		/*
		 * if (words != null) { var n = words.split(","); var searchString = ""; var
		 * counter = 0; for ( var tot = n.length; counter < tot; counter++) { if
		 * ((tot - counter) != 2) { var word = n[counter]; word = word.replace("[",
		 * "");
		 * 
		 * searchString = searchString + word + ","; } else { searchString =
		 * searchString + n[counter].replace("]", ""); } } //
		 * $('.review_container').easymark('removeHighlight'); //
		 * $('.review_container').easymark('highlight', searchString);
		 * 
		 * var myHilitor = new Hilitor("review_container");
		 * myHilitor.setMatchType("left"); myHilitor.apply(searchString); }
		 */
		if (skip == 0) {
			mode = element.className;
			dx = $('.info_panel').width() - 30;
			$('.keys').removeClass('selected_key');
			$('.keys_block').removeClass('selected_key');
		}
	
		if (subDimension != null)
			$(element).parent().addClass('selected_key');
		else
			$(element).parent().parent().addClass('selected_key');
	
		if (header == 1 && skip == 0) {
			$('.header_section h3').empty().html(
					'Showing all user reviews associated with \'' + tagname + '\'');
			$(".userinfo").hide();
			$("#headerIcon").show();
		} else if (header == 2 && skip == 0) {
			$('.header_section h3').empty().html(
					'Showing all products associated with \'' + tagname + '\'');
			$(".userinfo").show();
			$("#headerIcon").hide();
	
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
	
	function getFeedsByIdForProducts(entity, subproduct, word, productId, count,
			skip, subDimension, reviewType) {
	
		$
				.ajax({
					type : "POST",
					url : "/Inferlytics/getPostsFromMongo",
					data : "entity=" + entity + "&subproduct=" + subproduct
							+ "&word=" + encodeURIComponent(word) + "&reviewType="
							+ reviewType + "&skip=" + skip + "&limit=5"
							+ "&productId=" + productId + "&subDimension="
							+ encodeURIComponent(subDimension),
					dataType : "json",
					success : function(data) {
	
						$("#next").remove();
						var dfs = FragBuilder(data);
	
						skip = skip + 5;
						if (skip == 5)
							$('.review_container .mCSB_container').empty();
						$('.review_container .mCSB_container').append(dfs);
						if (skip < count)
							$('.review_container .mCSB_container')
									.append(
											"<div id='next'><center><a href='#'><div class='blueBtn'  onClick=\"showCommentsForProd('"
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
							$(".review_container").mCustomScrollbar("destroy");
							$(".review_container").mCustomScrollbar({
								scrollButtons : {
									enable : true
								},
								advanced : {
									updateOnContentResize : true,
									scrollInertia : 10
								},
								callbacks : {
									onScroll : function() {
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
	
	function getProducts(entity, subproduct, word, reviewType, skip, totalCount,
			showcomments, subDimension) {
	
		var slices = getUrlParams();
		var category = slices[5]; // Get the category from the URL
		if (category != null && category != "" && category != "#") {
			category = category.replace("#", "");
		}
		if (category == "" || category == "#") {
			category = "undefined";
		}
		if (totalCount > 0) {
			$
					.ajax({
						type : "POST",
						url : "/Inferlytics/getPostsFromMongo",
						data : "entity=" + entity + "&subproduct=" + subproduct
								+ "&word=" + encodeURIComponent(word)
								+ "&showcomments=" + showcomments + "&reviewType="
								+ reviewType + "&skip=" + skip + "&limit="
								+ limitForProduct + "&subDimension="
								+ encodeURIComponent(subDimension) + "&category="
								+ category,
						dataType : "json",
						beforeSend : function() {
	
						},
						success : function(data) {
							var dfs = FragBuilder(data);
							$('#next').remove();
	
							if (skip == 0) {
								$('.review_container .mCSB_container').html(dfs);
							} else {
								$('.review_container .mCSB_container').append(dfs);
							}
							skip = skip + skipLimit;
							var temp = skip; //to check whether skip is 6 so that we can update the scroll bar.
	
							if (limitForProduct > skipLimit) { //This is true only whe user comes back from the commnets section to products.So that he comes back to where he left from.    
								skip = limitForProduct;
								limitForProduct = skipLimit;
							}
	
							if (skip < totalCount && subDimension != null) {
								$('.review_container .mCSB_container')
										.append(
												"<div id='next'><center><a href='#'><div class='blueBtn'  onClick=\"allKeysClick('"
														+ word
														+ "\',"
														+ reviewType
														+ ","
														+ skip
														+ ","
														+ totalCount
														+ ",this,null,\'"
														+ subDimension
														+ "\')\"/></div></a></center></div>");
	
							}
							if (temp == skipLimit) {
								setTimeout(function() {
									$(".review_container").mCustomScrollbar(
											"scrollTo", -(scrollPosForProducts));
									scrollPosForProducts = 0;
								}, 300);
	
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
			$('.review_container .mCSB_container')
					.html(
							"<h3>No comments found in this category, please choose another comment type or product.</h3>");
		}
	
	}
	
	function getUrlParams() {
		var url = window.location.href;
		url = url.replace("/Inferlytics/", "/");
		return url.split("/");
	}
	
	function getsubDimension(featureName, TotalCount, element) {
	
		$("#subcontainer").empty();
		$("#back").show();
	
		$
				.each(
						featureJSON,
						function(i, value) {
							if (value.name == featureName) {
								$
										.each(
												value.children,
												function(k, subDimension) {
													$("#subcontainer")
															.append(
																	""
																			+ "<div class='keys_block animated rotateIn all_block catall positive_block cata negative_block catb'>"
																			+ "<ul onclick='allKeysClick(\""
																			+ subDimension.name
																			+ "\",0,0,"
																			+ subDimension.size
																			+ ",this,null,\""
																			+ featureName
																			+ "\")'><li class='keyword_block_header'>"
																			+ "<div class='front'><span>"
																			+ subDimension.name
																			+ "</span>"
																			+ "<label class='lbl all_block'>"
																			+ subDimension.size
																			+ " Products"
																			+ "</label>"
																			+ "</div>"
																			+ "</li></ul></div>");
												});
							}
						});
		allKeysClick("null", 0, 0, TotalCount, element, null, featureName);
		loadfeatureeffect();
	
	}
	
	function addFeatures() {
		$("#subcontainer").empty();
		$("#back").hide();
	
		$
				.each(
						featureJSON,
						function(i, value) {
							{
								var subCategories = "";
	
								$.each(value.children, function(k, subCategory) {
									if (k < 2)
										subCategories = subCategories
												+ capitalise(subCategory.name)
												+ ", ";
									else if (k == 3) {
										subCategories = subCategories + "etc.";
									}
	
								});
	
								$("#subcontainer")
										.append(
												"<div class='keys_block animated rotateIn all_block catall positive_block cata negative_block catb'><ul onClick='getsubDimension(\""
														+ value.name
														+ "\","
														+ value.size
														+ ",this)'><li class='keyword_block_header'>	<div class='front'><span>"
														+ value.name
														+ "</span>"
														+ "<div class='subCategory'>"
														+ subCategories
														+ "</div>"
														+ "<label class='lbl all_block'>"
														+ value.size
														+ " Products"
														+ "</label>"
														+ "</div></li></ul></div>");
							}
						});
		loadfeatureeffect();
	
	}
	
	function loadfeatureeffect() {
	
		// setup array of colors and a variable to store the current
		// index
	
		var colors = [ "metro-color"];
		var classes = [ "cell2x2", "cell1x2", "cell2x1", "cell1x2", "cell1x1" ];
		var widtharray = [ 140, 70, 140, 70, 70 ];
		currColor = 0;
		currCell = 0;
	
		$('.keys_block').each(function(index, element) {
	
			w = $(this).children().width();
	
			/*
			 * w = $(this).children().width(); h = $(this).children().height();
			 * $(this).css({'width':w+'px'}); $(this).css({'height':h+'px'});
			 * 
			 * $(this).children().css({'width':w+'p_x'});
			 */
			var currentclass = classes[currCell];
			if (w > widtharray[currCell]) {
				currentclass = 'cell2x1';
			}
			$(this).addClass(currentclass);// .addClass(colors[currColor]);
			$(this).children().addClass(currentclass).addClass(colors[0]);
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
			 * $(this).children().css({ 'width' : w + 'px' });
			 */
		});
	
		portfoliocall();
		$(".data_container1").mCustomScrollbar("update");
	
	}
	function portfoliocall() {
	
		// alert();
		// <!-- PORTFOLIO -->
		$('#subcontainer').portfolio({
			// <!-- GRID SETTINGS -->
			gridOffset : 20, // <!-- Manual Right Padding Offset for
			// 100% Width -->
			cellWidth : 70, // <!-- The Width of one CELL in PX-->
			cellHeight : 70, // <!-- The Height of one CELL in PX-->
			cellPadding : 8, // <!-- Spaces Between the CELLS -->
			entryProPage : 50, // <!-- The Max. Amount of the
			// Entries per Page, Rest made by
			// Pagination -->
	
			// <!-- CAPTION SETTING -->
			captionOpacity : 85,
	
			// <!-- FILTERING -->
			filterList : "#feature_sorting", // <!-- Which Filter is used for the
		// Filtering / Pagination -->
		// <!-- title:"#selected-filter-title", Which Div should be used for
		// showing the Selected Title of the Filter -->
	
		});
		setTimeout(function() {
			$(".data_container1").mCustomScrollbar("scrollTo", "top");
		}, 30); // Add Time out because the scrollbar needs to be loaded oly after
		// the features are loaded.Else the scroll bar poiition will not be
		// accurate.
	
	}
	
	function loadkeywordeffect() {
	
		// <!-- PORTFOLIO -->
		$('#keywordsubcontainer').portfolio({
			// <!-- GRID SETTINGS -->
			gridOffset : 30, // <!-- Manual Right Padding Offset for
			// 100% Width -->
			cellWidth : 120, // <!-- The Width of one CELL in PX-->
			cellHeight : 30, // <!-- The Height of one CELL in PX-->
			cellPadding : 10, // <!-- Spaces Between the CELLS -->
			entryProPage : 50, // <!-- The Max. Amount of the
			// Entries per Page, Rest made by
			// Pagination -->
	
			// <!-- CAPTION SETTING -->
			captionOpacity : 85,
	
			// <!-- FILTERING -->
			filterList : "#keyword_sorting_div", // <!-- Which Filter is used for
		// the Filtering / Pagination
		// -->
		// <!-- title:"#selected-filter-title", Which Div should be used for
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
	
		$('#product_name').hide();
		$('.review_container').height(globalHeight);
		header = 2;
	}
	
	function showCommentsForProd(productId, word, skip, reviewType, count,
			productName, subDimension, element) {
		if (reviewType == 2 && element != null) {
			$("#positiveCountno").text($(element).text());
			$("#negativeCountno").text($(element).next().text());
			$(".positive_review").attr("onclick", $(element).attr("onclick"));
			$(".negative_review")
					.attr("onclick", $(element).next().attr("onclick"));
			if ($(element).next().attr("onclick") == null) {
				$(".negative_review").attr("onclick", "");
			}
	
			$("#posarrow").show();
			$("#negarrow").hide();
		} else if (reviewType == 3 && element != null) {
			$("#positiveCountno").text($(element).prev().text());
			$("#negativeCountno").text($(element).text());
			$(".positive_review")
					.attr("onclick", $(element).prev().attr("onclick"));
			$(".negative_review").attr("onclick", $(element).attr("onclick"));
			if ($(element).prev().attr("onclick") == null) {
				$(".positive_review").attr("onclick", "");
			}
			$("#posarrow").hide();
			$("#negarrow").show();
		}
	
		if (subDimension == "null") {
	
			var goBackClick = "allKeysClick(\'" + word + "\',1,0,"
					+ totalCountForProd + ",this,null,'null')";
			limitForProduct = skipForProd + 6;
			$("#span_back").attr("onclick", goBackClick);
			allKeysClick(word, reviewType, skip, count, $('div.selected_key').find(
					'.keyword_header'), productId, subDimension);
		} else {
			var goBackClick = "allKeysClick(\'" + word + "\',1,0,"
					+ totalCountForProd + ",this,null,\'" + subDimension + "\')";
			limitForProduct = skipForProd + 6;
			$("#span_back").attr("onclick", goBackClick);
			allKeysClick(word, reviewType, skip, count, $('div.selected_key').find(
					'.keyword_block_header'), productId, subDimension);
		}
	
		$('#span_back').removeClass('span_back_hidden').addClass('span_back');
		if (productName != null) {
			$('#product_name').html(productName);
			$('#product_name').attr('title', productName);
		}
		$('#product_name').show();
		$('.review_container').height(
				globalHeight - $('#product_name').height() - 10);
	
	}
	function reviewCount(postId, isPositive) {
		$.ajax({
			type : "POST",
			url : "/Inferlytics/ReviewCount",
			data : "postId=" + postId + "&isPositive=" + isPositive,
			cache : false,
			crossDomain : true,
			success : function(data) {
	
			}
		});
	
	}
	
	function getClientDetails(word, productId) {
	
		$.ajax({
			type : "POST",
			url : "/Inferlytics/DoCaptureAnalytics",
			data : "word=" + word + "&productId=" + productId + "&isKeyword=true",
			cache : false,
			crossDomain : true,
			success : function(data) {
	
			}
		});
	}
	
	function getClientDetailsForFeatures(word, productId, subdimension) {
		calledProduct = true;
		$.ajax({
			type : "POST",
			url : "/Inferlytics/DoCaptureAnalytics",
			data : "word=" + word + "&productId=" + productId + "&subdimension="
					+ subdimension + "&isKeyword=false",
			cache : false,
			crossDomain : true,
			success : function(data) {
	
			}
		});
	}
	
	function getClientDetailsForComments(word, postId, subdimension) {
	
		if (subdimension == null) {
			isKeyword = true;
			datatosend = "word=" + word + "&postId=" + postId + "&isKeyword="
					+ isKeyword;
		} else {
			isKeyword = false;
			datatosend = "word=" + word + "&postId=" + postId + "&subdimension="
					+ subdimension + "&isKeyword=" + isKeyword;
		}
		$.ajax({
			type : "POST",
			url : "/Inferlytics/DoCaptureAnalytics",
			data : datatosend,
			cache : false,
			crossDomain : true,
			success : function(data) {
	
			}
		});
	}
	
	function myCustomFn() {
		if (header == 2)
			scrollPos = mcs.top;
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