var w = 800,
    h = 500,
    x = d3.scale.linear().range([0, w]),
    y = d3.scale.linear().range([0, h]),
    color = d3.scale.category20c();

       var vis = d3.select("#chartbody").append("div")
    .attr("class", "chart")
    .style("width", w + "px")
    .style("height", h + "px")
  .append("svg:svg")
    .attr("width", w)
    .attr("height", h);

var partition = d3.layout.partition()
    .value(function(d) { return d.size; });

 function plotchart(json) {
	root = $.parseJSON(json);
	var g = vis.selectAll("g")
      .data(partition.nodes(root))
    .enter().append("svg:g")
      .attr("transform", function(d) { return "translate(" + x(d.y) + "," + y(d.x) + ")"; })      
      .on("click", click);

  var kx = w / root.dx,
      ky = h / 1;

  g.append("svg:rect")
      .attr("width", root.dy * kx)
      .attr("height", function(d) { return d.dx * ky; })
      .attr("class", function(d) { return d.children ? "parent" : "child"; })
  .style("fill", function(d) {
	  if (d.color!= undefined){
		  return d.color;
	  }	  
	  return color((d.children ? d : d.parent).name); 
	  }
  );

  g.append("svg:text")
      .attr("transform", transform)
      .attr("dy", ".35em")
      .style("opacity", function(d) { return d.dx * ky > 12 ? 1 : 0; })
      .text(function(d) { return d.name; });
/*
  d3.select(window)
      .on("click", function() { click(root); });*/

  function click(d) {
	if (!d.children) {
		if(d.postIds == undefined) {
			//$('#container').html("Empty Node");
		} else {
			getFeedsById(d.postIds,d.name,d.color,d.words,d.parent.parent.name);
		}
		return ; 
	}
    $('.topComments').hide('slow');
    kx = (d.y ? w - 40 : w) / (1 - d.y);
    ky = h / d.dx;
    x.domain([d.y, 1]).range([d.y ? 40 : 0, w]);
    y.domain([d.x, d.x + d.dx]);

    var t = g.transition()
        .duration(d3.event.altKey ? 7500 : 750)
        .attr("transform", function(d) { return "translate(" + x(d.y) + "," + y(d.x) + ")"; });

    t.select("rect")
        .attr("width", d.dy * kx)
        .attr("height", function(d) { return d.dx * ky; });

    t.select("text")
        .attr("transform", transform)
        .style("opacity", function(d) { return d.dx * ky > 12 ? 1 : 0; });

    d3.event.stopPropagation();
  }

  function transform(d) {
    return "translate(8," + d.dx * ky / 2 + ")";
  }
}

function getFeedsById(postIds,name,color,words,dimension) {
	$('.topComments').hide();
	$.ajax({
		type : "POST",
		url : "getPostsFromMongo",
		data : "postIds=" + postIds,
		dataType: "json",
		success : function(data) {
			var json =	data;
			if(json !=null){
			var count = json.length;
			 $('#firstComment').html('');
			 $('#secondComment').html('');
			 $('#thirdComment').html('');
			 $('#fourthComment').html('');
			 $('#fifthComment').html('');
			 $('#sixthComment').html('');
			
			if(count != 0)
			$('.topComments').show('slow');
			 $('#totalCount').html(name);
			 for(var i=0; i < count; i++){				
				 if(i==0){							
				 $('#firstComment').append(json[i]);
				 $('#firstComment').css( { 'background-color':  color });
				 }
				 else if(i==1){
					 $('#secondComment').append(json[i]);
					 $('#secondComment').css( { 'background-color':  color });
					 }
				 else if(i==2){
					 $('#thirdComment').append(json[i]);
					 $('#thirdComment').css( { 'background-color':  color });
					 }
				 else if(i==3){
					 $('#fourthComment').append(json[i]);
					 $('#fourthComment').css( { 'background-color':  color });
					 }
				 else if(i==4){
					 $('#fifthComment').append(json[i]);
					 $('#fifthComment').css( { 'background-color':  color });
					 }
				 else if(i==5){
					 $('#sixthComment').append(json[i]);
					 $('#sixthComment').css( { 'background-color':  color });
					 }	                            
	            }  
			 var counter=0;
			 var searchString ="";
			/* $('li').each(function() {
				
			        var sentences = $(this)
			            .text()
			            .replace(/([^.!?]*[^.!?\s][.!?]['"]?)(\s|$)/g, 
			                     '<span class="sentence">$1</span>$2');
			        if(sentences.substring(1,5) !="span"){
			        	//alert(sentences);
			        	sentences="<span class='sentence'>"+sentences+"</span>";
			        	
			        }
			        
			      $(this).html(sentences);
			        
			    });*/
			 for(var tot = words.length; counter < tot; counter++){
				 if((tot - counter) != 1){
				 searchString = searchString+words[counter]+" ";
				 }
				 else{
					 searchString = searchString+words[counter]; 
				 }
			 }
			
				 $('.topComments').easymark('removeHighlight');
				  if(words.length && dimension!="Relations"){
					  //alert(searchString);
				    $('.topComments').easymark('highlight', searchString);
				  } 
				
			 
			}
		},
		complete : function() {
		
		},
		error : function(e) {
			//alert('Error' + e);
		}

	});
};

