(function(e, t) {
	function n(e) {
		var t = [], n;
		var r = window.location.href.slice(window.location.href.indexOf(e) + 1)
				.split("_");
		for ( var i = 0; i < r.length; i++) {
			r[i] = r[i].replace("%3D", "=");
			n = r[i].split("=");
			t.push(n[0]);
			t[n[0]] = n[1]
		}
		return t
	}
	function r(t, r) {
		var i = [];
		t.find(">div").each(function() {
			var t = e(this);
			i.push(t)
		});
		t.data("all", i);
		e("body").find(r.filterList + " ul li").each(function(n) {
			var i = e(this);
			var o = i.data("category");
			i.data("filterid", n);
			var u = [];
			t.find("." + i.data("category")).each(function() {
				var t = e(this);
				u.push(t.clone())
			});
			i.data("list", u);
			if (o != undefined) {
				i.click(function() {
					var n = e(this);
					s(t, n.data("list"), 1, r)
				})
			}
		});
		var u = n(r.urlDivider)["filter"];
		var a = n(r.urlDivider)["id"];
		r.selectedid = a;
		if (u != null) {
			e("body").find(r.filterList + " ul li").each(
					function() {
						var t = e(this);
						if (t.index() == decodeURI(u)) {
							e("body").find(r.filterList).find(
									".selected-filter-item").removeClass(
									"selected-filter-item");
							t.addClass("selected-filter-item")
						}
					})
		}
		e("body").find(r.filterList).find(".selected-filter-item").click();
		if (a != null) {
			setTimeout(
					function() {
						var t = e("body").find(".buttonlight-selected").data(
								"list")[parseInt(a, 0) - 1].clone();
						t.data("EntryNr", parseInt(a, 0));
						e("body").append(t);
						t.attr("id", "notimportant");
						t.css({
							display : "none"
						});
						o(t, r);
						e("body").find("#notimportant .hover-more-sign")
								.click()
					}, 500)
		}
	}
	function i(n) {
		var r = null;
		var i = null;
		if (n.find(".entry-info").length > 0) {
			if (!n.find("div").hasClass("hover-more-sign"))
				n.append('<div class="hover-more-sign"></div>');
			r = n.find(".hover-more-sign")
		}
		if (n.find(".blog-link").length > 0) {
			var s = n.find(".blog-link");
			s.removeClass(".blog-link");
			s.addClass("hover-blog-link-sign");
			if (!n.find("div").hasClass("hover-blog-link-sign"))
				n.append(s);
			i = n.find(".hover-blog-link-sign")
		}
		if (r != null && i == null) {
			r.css({
				left : parseInt(n.width() / 2, 0) - 25 + "px",
				top : parseInt(n.height() / 2, 0) + 60 + "px",
				display : "none",
				opacity : "0.0"
			})
		} else {
			if (r == null && i != null) {
				i.css({
					left : parseInt(n.width() / 2, 0) - 25 + "px",
					top : parseInt(n.height() / 2, 0) + 60 + "px",
					display : "none",
					opacity : "0.0"
				})
			} else {
				r.css({
					left : parseInt(n.width() / 2, 0) - 50 + "px",
					top : parseInt(n.height() / 2, 0) + 60 + "px",
					display : "none",
					opacity : "0.0"
				});
				i.css({
					left : parseInt(n.width() / 2, 0) + 10 + "px",
					top : parseInt(n.height() / 2, 0) + 60 + "px",
					display : "none",
					opacity : "0.0"
				})
			}
		}
		n.hover(function() {
			var n = e(this);
			var r = n.find(".normal-thumbnail-yoyo");
			var i = n.find(".hover-more-sign");
			var s = n.find(".hover-blog-link-sign");
			var o = n.find(".caption");
			if (o.data("top") == t)
				o.data("top", parseInt(o.css("top"), 0));
			clearTimeout(n.data("plusanim"));
			clearTimeout(n.data("capanim"));
			clearTimeout(n.data("bwpanim"));
			clearTimeout(e("body").find(".theBigThemePunchGallery").data(
					"bwanim"));
			n.data("plusanim", setTimeout(function() {
				i.css({
					display : "block"
				});
				i.cssAnimate({
					top : parseInt(n.height() / 2, 0) - 25 + "px",
					opacity : "1.0"
				}, {
					duration : 300,
					queue : false
				});
				s.css({
					display : "block"
				});
				setTimeout(function() {
					s.cssAnimate({
						top : parseInt(n.height() / 2, 0) - 25 + "px",
						opacity : "1.0"
					}, {
						duration : 300,
						queue : false
					})
				}, 100)
			}, 10));
			n.data("capanim", setTimeout(function() {
				o.data("opa", o.css("opacity"));
				o.cssAnimate({
					top : o.data("top") - 60 + "px",
					opacity : "0.0"
				}, {
					duration : 200,
					queue : false
				})
			}, 100));
			e("body").find(".theBigThemePunchGallery").data(
					"bwanim",
					setTimeout(function() {
						e("body").find(".theBigThemePunchGallery").each(
								function() {
									var t = e(this);
									t.find(".normal-thumbnail-yoyo")
											.cssAnimate({
												opacity : "0.0"
											}, {
												duration : 200
											})
								})
					}, 100));
			n.data("bwanim", setTimeout(function() {
				r.cssAnimate({
					opacity : "1.0"
				}, {
					duration : 200,
					queue : false
				})
			}, 210))
		}, function() {
			var n = e(this);
			var r = n.find(".normal-thumbnail-yoyo");
			var i = n.find(".hover-more-sign");
			var s = n.find(".hover-blog-link-sign");
			var o = n.find(".caption");
			if (o.data("top") == t)
				o.data("top", parseInt(o.css("top"), 0));
			clearTimeout(n.data("plusanim"));
			clearTimeout(n.data("capanim"));
			clearTimeout(n.data("bwanim"));
			clearTimeout(e("body").find(".theBigThemePunchGallery").data(
					"bwanim"));
			n.data("plusanim", setTimeout(function() {
				i.css({
					display : "block"
				});
				alert("1animate");
				i.cssAnimate({
					top : parseInt(n.height() / 2, 0) + 60 + "px",
					opacity : "0.0"
				}, {
					duration : 300,
					queue : false
				});
				s.css({
					display : "block"
				});
				setTimeout(function() {
					s.cssAnimate({
						top : parseInt(n.height() / 2, 0) + 60 + "px",
						opacity : "0.0"
					}, {
						duration : 300,
						queue : false
					})
				}, 100)
			}, 10));
			n.data("capanim", setTimeout(function() {
				o.cssAnimate({
					top : o.data("top") + "px",
					opacity : o.data("opa")
				}, {
					duration : 200,
					queue : false
				})
			}, 100));
			e("body").find(".theBigThemePunchGallery").data(
					"bwanim",
					setTimeout(function() {
						e("body").find(".theBigThemePunchGallery").each(
								function() {
									var t = e(this);
									t.find(".normal-thumbnail-yoyo")
											.cssAnimate({
												opacity : "1.0"
											}, {
												duration : 200
											})
								})
					}, 100));
			n.data("bwanim", setTimeout(function() {
				r.cssAnimate({
					opacity : "1.0"
				}, {
					duration : 200,
					queue : false
				})
			}, 100))
		})
	}
	function s(t, n, r, o) {
		t.find(">div").remove();
		if (!t.hasClass("theBigThemePunchGallery"))
			t.addClass("theBigThemePunchGallery");
		for ( var u = 0; u < n.length; u++) {
			if (u >= (r - 1) * o.entryProPage && u < o.entryProPage * r) {
				var a = n[u];
				t.append(a);
				a.css({
					opacity : "0",
					left : "0px",
					top : "0px"
				});
				a.data("EntryNr", u + 1);
				if (a.find(".entry-info").length > 0
						|| a.find(".blog-link").length > 0)
					i(a);
				if (a.find(".entry-info").length > 0) {
				}
			}
		}
		c(t, true);
		var f = Math.ceil(n.length / o.entryProPage);
		t.parent().find("#pagination").remove();
		if (f > 1) {
			var l = o.pageOfFormat;
			l = l.replace("#n", r);
			l = l.replace("#m", f);
			t.parent().append(
					'<div style="" class="pagination" id="pagination"><div class="pageofformat">'
							+ l + "</div></div>");
			var h = t.parent().find("#pagination");
			for ( var d = 0; d < f; d++) {
				h.append('<div id="pagebutton' + d
						+ '"class="pages buttonlight">' + (d + 1) + "</div>");
				var v = h.find("#pagebutton" + d);
				if (d + 1 == r)
					v.addClass("buttonlight-selected");
				v.data("pageNr", d + 1);
				v.data("entryProPage", o.entryProPage);
				v.data("list", n);
				v.click(function() {
					var n = e(this);
					s(t, n.data("list"), n.data("pageNr"), o)
				})
			}
		} else {
			t
					.parent()
					.append(
							'<div style="display:none" class="pagination" id="pagination"><div class="pageofformat">'
									+ l + "</div></div>");
			var h = t.parent().find("#pagination");
			h
					.append('<div style="display:none" id="pagebutton" class="pages buttonlight"></div>');
			var v = h.find("#pagebutton");
			v.addClass("buttonlight-selected");
			v.data("pageNr", 1);
			v.data("entryProPage", o.entryProPage);
			v.data("list", n)
		}
	}
	function u(t) {
		e(window).bind("resize", function() {
			if (t.data("windowWidth") != e(window).width()) {
				c(t, false);
				t.data("windowWidth", e(window).width())
			}
		})
	}
	function a(e, t, n) {
		var r = e.length;
		e[r] = {
			left : t,
			top : n
		}
	}
	function f(e, t, n) {
		for ( var r = 0; r < e.length; r++) {
			if (e[r].left === t && e[r].top === n) {
				return true
			}
		}
		return false
	}
	function l(e, t, n, r, i, s, o) {
		e.css({
			width : r + "px",
			height : i + "px",
			position : "absolute"
		});
		e.children().css({
			width : r + "px",
			height : i + "px",
			position : "absolute"
		});
		e.stop();
		if (o) {
		}
		setTimeout(function() {
			e.css({
				visibility : "visible",
				opacity : 1,
				left : t + "px",
				top : n + "px",
				scale : 1
			});
			e.cssAnimate({
				opacity : 1,
				left : t + "px",
				top : n + "px",
				scale : 1,
				rotate : 30
			}, {
				duration : 0,
				queue : false
			})
		}, 100 + s * 5);
		if (e.parent().parent().data("ymax") < n + i)
			e.parent().parent().data("ymax", n + i)
	}
	function c(t, n) {
		var r = t.data("cellWidth");
		var i = t.data("cellHeight");
		var s = t.data("padding");
		var o = t.data("gridOffset");
		var u = t.data("maxRow");
		var c = 0;
		var p = 0;
		var d = 1;
		var v = 0;
		var m = 0;
		var g = [];
		t.each(function() {
			var o = e(this);
			var u = false;
			var y = o.width() - parseInt(t.data("gridOffset"), 0) - 30;
			var b = Math.floor(y / r);
			o.css("position", "relative");
			var w = o.children("div");
			o.parent().data("ymax", 0);
			for ( var E = 0; E < w.length; E++) {
				if (w.eq(E).hasClass("cell2x2")) {
					if (v === b - 1) {
						v = 0;
						m++;
						c = 0;
						p += i + s;
						d++
					}
					if (b > 1
							&& (f(g, v, m) || f(g, v + 1, m)
									|| f(g, v + 1, m + 1) || f(g, v, m + 1))) {
						E--
					} else {
						l(w.eq(E), c, p, r * 2 + s, i * 2 + s, E, n);
						a(g, v, m);
						a(g, v + 1, m);
						a(g, v, m + 1);
						a(g, v + 1, m + 1)
					}
					u = true
				} else if (w.eq(E).hasClass("cell2x1")) {
					if (f(g, v, m) || f(g, v + 1, m) || b > 1 && v === b - 1) {
						E--
					} else {
						l(w.eq(E), c, p, r * 2 + s, i, E, n);
						a(g, v + 1, m)
					}
				} else if (w.eq(E).hasClass("cell1x2")) {
					if (f(g, v, m) || f(g, v, m + 1)) {
						E--
					} else {
						l(w.eq(E), c, p, r, i * 2 + s, E, n);
						a(g, v, m);
						a(g, v, m + 1)
					}
					u = true
				} else {
					if (f(g, v, m)) {
						E--
					} else {
						l(w.eq(E), c, p, r, i, E, n)
					}
				}
				if (d % b === 0) {
					v = 0;
					m++;
					c = 0;
					p += i + s;
					u = false
				} else {
					c += r + s;
					v++
				}
				d++
			}
			var S = 0;
			if (d % b !== 1) {
				S = p + i + s
			} else {
				S = p + s
			}
			if (u) {
				S += i + s
			}
			e(this).parent().css("height", o.parent().data("ymax") + "px");
			c = 0;
			p = 0;
			d = 1;
			var x = 0;
			for (E = 0; E < w.length; E++) {
			}
		})
	}
	e.fn
			.extend({
				dropdown : function(t) {
					function n(t) {
						if (e.browser.msie && e.browser.version < 9) {
							t.find("ul:first").css({
								margin : "0px"
							})
						}
						t
								.find("li")
								.each(
										function(t) {
											var n = e(this);
											n
													.wrapInner('<div class="listitem" style="position:relative;left:0px;"></div>');
											if (e.browser.msie
													&& e.browser.version < 9) {
												if (t == 0)
													n.css({
														clear : "both",
														"margin-top" : "0px",
														"padding-top" : "0px"
													});
												n
														.css({
															display : "none",
															opacity : "0.0",
															"vertical-align" : "bottom",
															top : "-20px"
														});
												if (e.browser.msie
														&& e.browser.version < 8) {
													n
															.css({
																width : n
																		.parent()
																		.parent()
																		.find(
																				".buttonlight")
																		.width()
															})
												}
											} else {
												n.css({
													display : "none",
													opacity : "0.0",
													top : "-20px"
												})
											}
										})
					}
					var r = {};
					t = e.extend({}, e.fn.dropdown.defaults, t);
					return this.each(function() {
						var r = t;
						var i = e(this);
						n(i)
					})
				}
			});
	e.fn.extend({
		portfolio : function(t) {
			var n = {
				gridOffset : 30,
				cellWidth : 100,
				cellHeight : 70,
				cellPadding : 10,
				gridOffset : 0,
				captionOpacity : 75,
				filterList : "#feature_sorting",
				urlDivider : "?"
			};
			t = e.extend({}, e.fn.portfolio.defaults, t);
			return this.each(function() {
				var n = e(this);
				var i = t;
				n.data("cellWidth", i.cellWidth);
				n.data("cellHeight", i.cellHeight);
				n.data("padding", i.cellPadding);
				n.data("gridOffset", i.gridOffset);
				n.data("gridOffset", i.gridOffset);
				n.data("captionOpacity", i.captionOpacity);
				c(n);
				u(n);
				r(n, i)
			})
		}
	})
})(jQuery);