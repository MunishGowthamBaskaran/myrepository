var FragBuilder = (function() {
	var e = function(e, t) {
		for ( var n in t) {
			e.style[n] = t[n]
		}
	};
	var t = function(e, t) {
		for ( var n in t) {
			e.onclick = n
		}
	};
	var n = function(t) {
		var r = document.createDocumentFragment();
		for ( var i in t) {
			if (!("tagName" in t[i]) && "textContent" in t[i]) {
				r.appendChild(document.createTextNode(t[i]["textContent"]))
			} else if ("tagName" in t[i]) {
				var s = document.createElement(t[i].tagName);
				delete t[i].tagName;
				for ( var o in t[i]) {
					var u = t[i][o];
					switch (o) {
					case "textContent":
						s.appendChild(document.createTextNode(u));
						break;
					case "style":
						e(s, u);
						break;
					case "click":
						s.setAttribute("onclick", u);
						break;
					case "childNodes":
						s.appendChild(n(u));
						break;
					case "src":
						s.src = u;
						break;
					case "href":
						s.setAttribute("href", u);
						s.setAttribute("target", "_blank");
						break;
					case "id":
						s.setAttribute("id", u);
						break;
					case "subDim":
						s.setAttribute("subDim", u);
						break;
					case "word":
						s.setAttribute("word", u);
						break;
					case "scNo":
						s.setAttribute("scNo", u);
						break;
					default:
						if (o in s) {
							s[o] = u
						}
						break
					}
				}
				r.appendChild(s)
			} else {
				throw "Error: Malformed JSON Fragment"
			}
		}
		return r
	};
	var r = function(e) {
		var t = document.createElement("div"), n = document
				.createDocumentFragment();
		t.innerHTML = e;
		while (t.hasChildNodes()) {
			n.appendChild(t.firstChild)
		}
		return n
	};
	return function(e) {
		if (typeof e === "string") {
			return r(e)
		} else {
			return n(e)
		}
	}
}());
