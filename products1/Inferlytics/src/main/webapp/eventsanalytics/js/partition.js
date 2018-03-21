

var margin = {top: 40, right: 10, bottom: 10, left: 10},
width = 300 - margin.left - margin.right,
height = 450 - margin.top - margin.bottom;

var color = d3.scale.category10();

var treemap = d3.layout.treemap()
.size([width, height])
.sticky(true)
.sort(function(a,b) {
    return a.value - b.value;
})
.value(function(d) { return d.size +20; });

var div = d3.select("#chartbody").append("div")
.style("position", "relative")
.style("width", (width + margin.left + margin.right) + "px")
.style("height", (height + margin.top + margin.bottom) + "px")
// .style("left", margin.left + "px")
.style("top", margin.top + "px");

var plotchart = function(json) {
	root = $.parseJSON(JSON.stringify(json));
  	var node = div.datum(root).selectAll(".node")
      .data(treemap)
      .enter().append("div")
      .attr("class", "node")
      .call(position)
      .style("background", function(d) { return d.children ? null : "#999999"; })
      .text(function(d) { return d.children ? null : d.name; })
  	  .on("click", topicClick)
  	  .on("mousemove", mousemove)
      .on("mouseout", mouseout);
  	
  d3.selectAll("input").on("change", function change() {
    var value = this.value === "count"
        ? function() { return 1; }
        : function(d) { return d.size +20; };

    node
        .data(treemap.value(value).nodes)
        .transition()
        .duration(1500)
        .call(position);
    
  });
  };

  var mousemove = function(d) {
	  var xPosition = d3.event.pageX + 5;
	  var yPosition = d3.event.pageY + 5;

	  d3.select("#tooltip")
	    .style("left", xPosition + "px")
	    .style("top", yPosition + "px");
	  d3.select("#tooltip #heading")
	    .text(d.name);
	  d3.select("#tooltip #mentions")
	    .text(d.size+ " mentions");
	  d3.select("#tooltip").classed("hidden", false);
	};

	var mouseout = function() {
	  d3.select("#tooltip").classed("hidden", true);
	};

	
function position() {
this.style("left", function(d) { return d.x + "px"; })
  .style("top", function(d) { return d.y + "px"; })
  .style("width", function(d) { return Math.max(0, d.dx - 1) + "px"; })
  .style("height", function(d) { return Math.max(0, d.dy - 1) + "px"; });
}

function fontSize(d,i) {
	if(d.children == null){
	var size = d.dx/5;
	var words = d.name.split(' ');
	var word = words[0];
	var width = d.dx;
	var height = d.dy;
	d3.select(this).style("font-size", size + "px").text(word);
/*
 * while(((d3.select(this).getBBox().width >= width) ||
 * (d3.select(this).getBBox().height >= height)) && (size > 12)) { size--;
 * d3.select(this).style("font-size", size + "px"); this.firstChild.data = word; }
 */
	}
}
	function wordWrap(d, i){
		if(d.children == null){
	var words = d.name.split(' ');
	var line = new Array();
	var length = 0;
	var text = "";
	var width = d.dx;
	var height = d.dy;
	var word;
	do {
	   word = words.shift();
	   line.push(word);
	   if (words.length)
	     this.firstChild.data = line.join(' ') + " " + words[0]; 
	   else
	     this.firstChild.data = line.join(' ');
	   length = this.getBBox().width;
	   if (length < width && words.length) {
	     ;
	   }
	   else {
	     text = line.join(' ');
	     this.firstChild.data = text;
	     if (this.getBBox().width > width) { 
	       text = d3.select(this).select(function() {return this.lastChild;}).text();
	       text = text + "...";
	       d3.select(this).select(function() {return this.lastChild;}).text(text);
	       d3.select(this).classed("wordwrapped", true);
	       break;
	    }
	    else
	      ;

	  if (text != '') {
	    d3.select(this).append("svg:tspan")
	    .attr("x", 0)
	    .attr("dx", "0.15em")
	    .attr("dy", "0.9em")
	    .text(text).style("opacity", 
	    	    function(d){
	        bounds = this.getBBox();
	        return((bounds.height < d.h -textMargin) && 
	               (bounds.width < d.w-textMargin)    ) ? 1:0;
	      });
	  }
	  else
	     ;

	  if(this.getBBox().height > height && words.length) {
	     text = d3.select(this).select(function() {return this.lastChild;}).text();
	     text = text + "...";
	     d3.select(this).select(function() {return this.lastChild;}).text(text);
	     d3.select(this).classed("wordwrapped", true);

	     break;
	  }
	  else
	     ;

	  line = new Array();
	    }
	  } while (words.length);
	  // this.firstChild.data = '';
		}
	} 


