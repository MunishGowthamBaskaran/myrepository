var a = '<div class="popOverBox">Your Text Here</div>';
$(function() {
	getProducts(0,1,223);

});

function getProducts(skip,pageNo,totCount) {
	$.ajax({
				type : "POST",
		url : "/Inferlytics/YahooConnector",
		async : true,
		data : "&product=nordstromboots&skip="+skip+"&limit=16",
		dataType : "json",
		success : function(returnData) {
		$("#productsList").html(FragBuilder(returnData));
		

		$('.inf-mb-prd-poskeyword').click(function(e) {
			e.stopPropagation();
		});
		$('.inf-mb-prd-negkeyword').click(function(e) {
			e.stopPropagation();
		});

		$(document).click(
				function(e) {
					if (($('.inf-mb-prd-poskeyword').has(e.target).length == 0)
							|| $(e.target).is('.close')) {
						$('.inf-mb-prd-poskeyword').popover('hide');
					}

					if (($('.inf-mb-prd-negkeyword').has(e.target).length == 0)
							|| $(e.target).is('.close')) {
						$('.inf-mb-prd-negkeyword').popover('hide');
					}
				});
		}
	
		
	
	});
	$("#pgNo").text(pageNo);
	skip=skip+16;
	pageNo++;
	if(skip<totCount){
		$("#nextPage").attr("onclick","getProducts("+skip+","+pageNo+","+totCount+")");
	}
	
}

function getCommentsPos(word,prodId,totCount) {

	/*
	 * Destroy the existing popover and add anew one.This would hide the opened
	 * popover and pops up a new one
	 */
	$.ajax({
		type : "POST",
		url : "/Inferlytics/getPostsFromMongo",
		async : false,
		data : "entity=nodstorm&subproduct=womenshoes&word="+word+"&skip=0&reviewType=2&limit=5&productId="+prodId+"&mbProduct=true&price=undefined",
		dataType : "json",
		success : function(returnData) {
			$('.inf-mb-prd-poskeyword').popover('destroy');
			$('.inf-mb-prd-negkeyword').popover('destroy');
			var div = '<div class="inf-mb-prd-review_container" id="inf-mb-prd-posreview_container">';

			/* Register a popover */
			$(".inf-mb-prd-poskeyword").popover({
				placement : 'bottom', // top, bottom, left or right
				title : 'Showing Reviews for \''+word.toUpperCase()+'\'' ,
				html : 'true',
				container : 'body',
				content : div 
			});
			
			/*
			 * Register the events and add custom scrollbar when the popover is
			 * displayed.
			 */
			
			$('.inf-mb-prd-poskeyword').on(
					'shown.bs.popover',
					function() {
						$('.popover').click(function(e) {
							e.stopPropagation();
						});
						$(".inf-mb-prd-review_container").mCustomScrollbar({
							mouseWheel : true,
							mouseWheelPixels : "auto",
							scrollButtons : {
								enable : true
							},
							advanced : {
								updateOnContentResize : true
							}
						});
					
						$(".inf-mb-prd-review_container .mCSB_container").html(FragBuilder(returnData));
						if(totCount>5){
							$(".inf-mb-prd-review_container .mCSB_container").append('<div id="inf-bw-next" onclick="appendComments(\''+word+'\',\''+prodId+'\',5,'+totCount+',2)"><center><a><div class="blueBtn"></div></a></center></div>');
						}
						
						highlight();
						$('.inf-mb-prd-review_container').on(
								"click",
								".inf-mb-prd-review",
								function() {
									if ($(this).find('.inf-mb-prd-review_comment')
											.hasClass('expand')) {
										var h = $(this).find(
												'.inf-mb-prd-review_comment p')
												.height() + 20;
										$(this).find('.inf-mb-prd-review_comment')
												.removeClass('expand');
										$(this).find('.inf-mb-prd-review_comment')
												.animate({
													'height' : h + 'px'
												}, {
													duration : 50,
													complete : function() {
													}
												});

									} else {
										$(this).find('.inf-mb-prd-review_comment')
												.addClass('expand');
										$(this).find('.inf-mb-prd-review_comment')
												.animate({
													'height' : '58px'
												}, {
													duration : 50,
													complete : function() {
													}
												});
									}
								});
					});

		
			
			
		}
	});

	


}

function getCommentsNeg(word,prodId,totCount) {

	
	$.ajax({
		type : "POST",
		url : "/Inferlytics/getPostsFromMongo",
		async : false,
		data : "entity=nodstorm&subproduct=womenshoes&word="+word+"&skip=0&reviewType=3&limit=5&productId="+prodId+"&mbProduct=true&price=undefined",
		dataType : "json",
		success : function(returnData) {
			$('.inf-mb-prd-poskeyword').popover('destroy');
			$('.inf-mb-prd-negkeyword').popover('destroy');
			var div = '<div class="inf-mb-prd-review_container" id="inf-mb-prd-negreview_container">';
			$(".inf-mb-prd-negkeyword").popover({
				placement : 'bottom', // top, bottom, left or right
				title : 'Showing Reviews for \''+word.toUpperCase()+'\'' ,
				html : 'true',
				container : 'body',
				content : div
			});
		
			/*
			 * Register the events and add custom scrollbar when the popover is
			 * displayed.
			 */
			
			$('.inf-mb-prd-negkeyword').on(
					'shown.bs.popover',
					function() {
						$('.popover').click(function(e) {
							e.stopPropagation();
						});
						$(".inf-mb-prd-review_container").mCustomScrollbar({
							mouseWheel : true,
							mouseWheelPixels : "auto",
							scrollButtons : {
								enable : true
							},
							advanced : {
								updateOnContentResize : true
							}
						});
					
						$(".inf-mb-prd-review_container .mCSB_container").html(FragBuilder(returnData));
					if(totCount>5){
						$(".inf-mb-prd-review_container .mCSB_container").append('<div id="inf-bw-next" onclick="appendComments(\''+word+'\',\''+prodId+'\',5,'+totCount+',2)"><center><a><div class="blueBtn"></div></a></center></div>');
					}
						highlight();
						$('.inf-mb-prd-review_container').on(
								"click",
								".inf-mb-prd-review",
								function() {
									if ($(this).find('.inf-mb-prd-review_comment')
											.hasClass('expand')) {
										var h = $(this).find(
												'.inf-mb-prd-review_comment p')
												.height() + 20;
										$(this).find('.inf-mb-prd-review_comment')
												.removeClass('expand');
										$(this).find('.inf-mb-prd-review_comment')
												.animate({
													'height' : h + 'px'
												}, {
													duration : 50,
													complete : function() {
													}
												});

									} else {
										$(this).find('.inf-mb-prd-review_comment')
												.addClass('expand');
										$(this).find('.inf-mb-prd-review_comment')
												.animate({
													'height' : '58px'
												}, {
													duration : 50,
													complete : function() {
													}
												});
									}
								});
					});
		}
	});
	
	
}


function highlight(){
	  $.each($('.inf-mb-prd-review_container .inf-mb-prd-review'), function(index, item) {
          var wordToHighlight = $(this).attr('word');
          var sentenceToHighlight = $(this).attr('scNo');
          if(sentenceToHighlight !="-1"){
          	$(this).find('em:eq('+sentenceToHighlight+')').css('backgroundColor','#ff6');
          }
          else{
          var myHilitor = new Hilitor($(this).attr('id'));
          myHilitor.setMatchType("left");
          myHilitor.apply(wordToHighlight);
          }
  });
	
}
	
function appendComments(word,prodId,skip,totCount,reviewType){
	
	$.ajax({
		type : "POST",
		url : "/Inferlytics/getPostsFromMongo",
		async : false,
		data : "entity=nodstorm&subproduct=womenshoes&word="+word+"&skip="+skip+"&reviewType="+reviewType+"&limit=5&productId="+prodId+"&mbProduct=true&price=undefined",
		dataType : "json",
		success : function(returnData) {
			$("#inf-bw-next").remove();
			$(".inf-mb-prd-review_container .mCSB_container").append(FragBuilder(returnData));
			
			skip=skip+5;
			if(skip<totCount){
				$(".inf-mb-prd-review_container .mCSB_container").append('<div id="inf-bw-next" onclick="appendComments(\''+word+'\',\''+prodId+'\','+skip+','+totCount+','+reviewType+')"><center><a><div class="blueBtn"></div></a></center></div>');
			}
			highlight();
		}});
	
	
}	  
	  

