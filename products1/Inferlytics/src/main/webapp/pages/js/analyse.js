$(document).click(function(e) {
	if (!$(e.target).is('.tagPopUp, .openDiv  *,.tagPopUp *')) {
		$('.tagPopUp').hide();
	}
	return;
});

function openDiv(id, feedbackFlag) {
	if (feedbackFlag == true) {
		selectRadio(id);
	}
	$("#" + id).show();
};

function giveFeedback(divfeedback, id, sentiment) {
	var feedback = getCheckedValue(divfeedback);
	//alert(feedback);
	if (feedback!=-1 && feedback != sentiment && sentiment != null) {
		$.ajax({
			type : "POST",
			url : "DoImproveSentimentAnalysis",
			data : "submitType=0&feedback=" + feedback + "&feedId=" + id,
			success : function(response) {
				$('.tagPopUp').hide();
			},
			complete : function() {

			},

			error : function(e) {
				alert('Error' + e);
			}

		});
	}
};
function AddPattern(word, pattern, score) {
	alert(word);
	alert(pattern);
	alert(score);

	$.ajax({
		type : "POST",
		url : "DoImproveSentimentAnalysis",
		data : "submitType=1&pattern=" + pattern + "&word=" + word + "&score="
				+ score,
		success : function(response) {
			$('.tagPopUp').hide();
		},
		complete : function() {

		},

		error : function(e) {
			alert('Error' + e);
		}

	});
};
function AddNegation(negation) {

	$.ajax({
		type : "POST",
		url : "DoImproveSentimentAnalysis",
		data : "submitType=2&negation=" + negation,
		success : function(response) {
			$('.tagPopUp').hide();
		},
		complete : function() {

		},

		error : function(e) {
			alert('Error' + e);
		}

	});
};
function AddModifier(modifier, score) {

	$.ajax({
		type : "POST",
		url : "DoImproveSentimentAnalysis",
		data : "submitType=3&modifier=" + modifier + "&score=" + score,
		success : function(response) {
			$('.tagPopUp').hide();
		},
		complete : function() {

		},

		error : function(e) {
			alert('Error' + e);
		}

	});
};
function selectRadio(div) {
	$("#" + div + " :radio").each(function() {
		var name = this.name;
		if (name.length > 0) {
			var i = name.indexOf(':');
			if (i != -1) {
				var value = name.charAt(i - 1);
				if (value == this.value) {

					this.checked = true;
				}

			}
		}
	});
};
function getCheckedValue(div) {
	var value = -1;
	$("#" + div + " :radio:checked").each(function() {
		 value = this.value;
	
	});
	return value;
};

function closeDiv(id) {
	$('.tagPopUp').hide();
}

function fetchdataFromTripAdvisor(){

	$.ajax({
		type : "POST",
		url : "DoSentimentAnalysisFromTripAdvisor",
		success : function(response) {
			
		},
		complete : function() {

		},

		error : function(e) {
			alert('Error' + e);
		}

	});

}

/*function	plotcharts(){
	var brand = document.getElementById('brandname').value;
	alert(brand);
	var frm = document.getElementById('sentiForm');
	if(frm) {
		frm.action = 'PlotChart';
		alert(frm.action);
		//document.getElementById("sentiForm").submit();
		document.forms["sentiForm"].submit();
		
	}


}*/