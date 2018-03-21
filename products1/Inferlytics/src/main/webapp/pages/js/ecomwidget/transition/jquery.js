/**
 * jquery.freshline.portfolio - jQuery Plugin for portfolio gallery
 * @version: 1.5 (16.12.12)
 * @requires jQuery v1.4 or later
 * @author themepunch
 * All Rights Reserved, use only in freshline Templates or when Plugin bought at Envato !
 **/
(function (e, t) {
	 function n(e) {
	        var t = [],
	            n;
	        var r = window.location.href.slice(window.location.href.indexOf(e) + 1).split("_");
	        for (var i = 0; i < r.length; i++) {
	            r[i] = r[i].replace("%3D", "=");
	            n = r[i].split("=");
	            t.push(n[0]);
	            t[n[0]] = n[1];
	        }
	        return t;
	    }

    function r(t, r) {
        var i = [];
        t.find(">div").each(function () {
            var t = e(this);
            i.push(t);
        });
        t.data("all", i);
        e("body").find(r.filterList + " ul li").each(function (n) {
            var i = e(this);
            var category= i.data("category");
            
            i.data("filterid", n);
            var s = [];
            t.find("." + i.data("category")).each(function () {
                var t = e(this);
                s.push(t.clone());
            });
            i.data("list", s);
            if(category != undefined){
            i.click(function () {
                var n = e(this);

                u(t, n.data("list"), 1, r);

/*                setTimeout(function () {
                    e("body").find(r.backgroundHolder).tpbackground({
                        slideshow: r.backgroundSlideshow,
                        callback: "true",
                        cat: n.data("category")
                    });
                    e("body").find(r.title).html(n.html());
                    e("body").find(r.title).cssAnimate({
                        opacity: "1.0"
                    }, {
                        duration: 500,
                        queue: false
                    })
                }, 500)*/
            });
            }
        });
        var s = n(r.urlDivider)["filter"];
        var a = n(r.urlDivider)["id"];
        r.selectedid = a;
        if (s != null) {
            e("body").find(r.filterList + " ul li").each(function () {
                var t = e(this);
                if (t.index() == decodeURI(s)) {
                    e("body").find(r.filterList).find(".selected-filter-item").removeClass("selected-filter-item");
                    t.addClass("selected-filter-item")
                }
            })
        }
        e("body").find(r.filterList).find(".selected-filter-item").click();
        if (a != null) {
            setTimeout(function () {
                var t = e("body").find(".buttonlight-selected").data("list")[parseInt(a, 0) - 1].clone();
                t.data("EntryNr", parseInt(a, 0));
                e("body").append(t);
                t.attr("id", "notimportant");
                t.css({
                    display: "none"
                });
                o(t, r);
                e("body").find("#notimportant .hover-more-sign").click()
            }, 500)
        }
    }

    function s(n) {
        var r = null;
        var i = null;
        if (n.find(".entry-info").length > 0) {
            if (!n.find("div").hasClass("hover-more-sign")) n.append('<div class="hover-more-sign"></div>');
            r = n.find(".hover-more-sign")
        }
        if (n.find(".blog-link").length > 0) {
            var s = n.find(".blog-link");
            s.removeClass(".blog-link");
            s.addClass("hover-blog-link-sign");
            if (!n.find("div").hasClass("hover-blog-link-sign")) n.append(s);
            i = n.find(".hover-blog-link-sign")
        }
        if (r != null && i == null) {
            r.css({
                left: parseInt(n.width() / 2, 0) - 25 + "px",
                top: parseInt(n.height() / 2, 0) + 60 + "px",
                display: "none",
                opacity: "0.0"
            })
        } else {
            if (r == null && i != null) {
                i.css({
                    left: parseInt(n.width() / 2, 0) - 25 + "px",
                    top: parseInt(n.height() / 2, 0) + 60 + "px",
                    display: "none",
                    opacity: "0.0"
                })
            } else {
                r.css({
                    left: parseInt(n.width() / 2, 0) - 50 + "px",
                    top: parseInt(n.height() / 2, 0) + 60 + "px",
                    display: "none",
                    opacity: "0.0"
                });
                i.css({
                    left: parseInt(n.width() / 2, 0) + 10 + "px",
                    top: parseInt(n.height() / 2, 0) + 60 + "px",
                    display: "none",
                    opacity: "0.0"
                })
            }
        }
        n.hover(function () {
            var n = e(this);
            var r = n.find(".normal-thumbnail-yoyo");
            var i = n.find(".hover-more-sign");
            var s = n.find(".hover-blog-link-sign");
            var o = n.find(".caption");
            if (o.data("top") == t) o.data("top", parseInt(o.css("top"), 0));
            clearTimeout(n.data("plusanim"));
            clearTimeout(n.data("capanim"));
            clearTimeout(n.data("bwpanim"));
            clearTimeout(e("body").find(".theBigThemePunchGallery").data("bwanim"));
            n.data("plusanim", setTimeout(function () {
                i.css({
                    display: "block"
                });
                i.cssAnimate({
                    top: parseInt(n.height() / 2, 0) - 25 + "px",
                    opacity: "1.0"
                }, {
                    duration: 300,
                    queue: false
                });
                s.css({
                    display: "block"
                });
                setTimeout(function () {
                    s.cssAnimate({
                        top: parseInt(n.height() / 2, 0) - 25 + "px",
                        opacity: "1.0"
                    }, {
                        duration: 300,
                        queue: false
                    })
                }, 100)
            }, 10));
            n.data("capanim", setTimeout(function () {
                o.data("opa", o.css("opacity"));
                o.cssAnimate({
                    top: o.data("top") - 60 + "px",
                    opacity: "0.0"
                }, {
                    duration: 200,
                    queue: false
                })
            }, 100));
            e("body").find(".theBigThemePunchGallery").data("bwanim", setTimeout(function () {
                e("body").find(".theBigThemePunchGallery").each(function () {
                    var t = e(this);
                    t.find(".normal-thumbnail-yoyo").cssAnimate({
                        opacity: "0.0"
                    }, {
                        duration: 200
                    })
                })
            }, 100));
            n.data("bwanim", setTimeout(function () {
                r.cssAnimate({
                    opacity: "1.0"
                }, {
                    duration: 200,
                    queue: false
                })
            }, 210))
        }, function () {
            var n = e(this);
            var r = n.find(".normal-thumbnail-yoyo");
            var i = n.find(".hover-more-sign");
            var s = n.find(".hover-blog-link-sign");
            var o = n.find(".caption");
            if (o.data("top") == t) o.data("top", parseInt(o.css("top"), 0));
            clearTimeout(n.data("plusanim"));
            clearTimeout(n.data("capanim"));
            clearTimeout(n.data("bwanim"));
            clearTimeout(e("body").find(".theBigThemePunchGallery").data("bwanim"));
            n.data("plusanim", setTimeout(function () {
                i.css({
                    display: "block"
                });
                alert("1animate");
                i.cssAnimate({
                    top: parseInt(n.height() / 2, 0) + 60 + "px",
                    opacity: "0.0"
                }, {
                    duration: 300,
                    queue: false
                });
                s.css({
                    display: "block"
                });
                setTimeout(function () {
                    s.cssAnimate({
                        top: parseInt(n.height() / 2, 0) + 60 + "px",
                        opacity: "0.0"
                    }, {
                        duration: 300,
                        queue: false
                    })
                }, 100)
            }, 10));
            n.data("capanim", setTimeout(function () {
                o.cssAnimate({
                    top: o.data("top") + "px",
                    opacity: o.data("opa")
                }, {
                    duration: 200,
                    queue: false
                })
            }, 100));
            e("body").find(".theBigThemePunchGallery").data("bwanim", setTimeout(function () {
                e("body").find(".theBigThemePunchGallery").each(function () {
                    var t = e(this);
                    t.find(".normal-thumbnail-yoyo").cssAnimate({
                        opacity: "1.0"
                    }, {
                        duration: 200
                    })
                })
            }, 100));
            n.data("bwanim", setTimeout(function () {
                r.cssAnimate({
                    opacity: "1.0"
                }, {
                    duration: 200,
                    queue: false
                })
            }, 100))
        })
    }

    function u(t, n, r, a) {
        t.find(">div").remove();
        if (!t.hasClass("theBigThemePunchGallery")) t.addClass("theBigThemePunchGallery");
        for (var f = 0; f < n.length; f++) {
            if (f >= (r - 1) * a.entryProPage && f < a.entryProPage * r) {
                var l = n[f];
                t.append(l);
                l.css({
                    opacity: "0",
                    left: "0px",
                    top: "0px"
                });
                l.data("EntryNr", f + 1);
               // i(l.find(".thumbnails"));
                if (l.find(".entry-info").length > 0 || l.find(".blog-link").length > 0) s(l);
                if (l.find(".entry-info").length > 0) {
                   // o(l, a)
                }
            }
        }
        p(t, true);
        var c = Math.ceil(n.length / a.entryProPage);
        t.parent().find("#pagination").remove();
        if (c > 1) {
            var h = a.pageOfFormat;
            h = h.replace("#n", r);
            h = h.replace("#m", c);
            t.parent().append('<div style="" class="pagination" id="pagination"><div class="pageofformat">' + h + "</div></div>");
            var d = t.parent().find("#pagination");
            for (var v = 0; v < c; v++) {
                d.append('<div id="pagebutton' + v + '"class="pages buttonlight">' + (v + 1) + "</div>");
                var m = d.find("#pagebutton" + v);
                if (v + 1 == r) m.addClass("buttonlight-selected");
                m.data("pageNr", v + 1);
                m.data("entryProPage", a.entryProPage);
                m.data("list", n);
                m.click(function () {
                    var n = e(this);
                    u(t, n.data("list"), n.data("pageNr"), a)
                })
            }
        } else {
            t.parent().append('<div style="display:none" class="pagination" id="pagination"><div class="pageofformat">' + h + "</div></div>");
            var d = t.parent().find("#pagination");
            d.append('<div style="display:none" id="pagebutton" class="pages buttonlight"></div>');
            var m = d.find("#pagebutton");
            m.addClass("buttonlight-selected");
            m.data("pageNr", 1);
            m.data("entryProPage", a.entryProPage);
            m.data("list", n)
        }
    }


    function f(t) {
        e(window).bind("resize", function () {
            if (t.data("windowWidth") != e(window).width()) {
                p(t, false);
                t.data("windowWidth", e(window).width());
            }
        })
    }

    function l(e, t, n) {
        var r = e.length;
        e[r] = {
            left: t,
            top: n
        }
    }

    function c(e, t, n) {
        for (var r = 0; r < e.length; r++) {
            if (e[r].left === t && e[r].top === n) {
                return true
            }
        }
        return false
    }

    function h(t, n, r, i, s, o, u) {
        t.css({
            width: i + "px",
            height: s + "px",
            position: "absolute"
        });
        t.children().css({
            width: i + "px",
            height: s + "px",
            position: "absolute"
        });
        t.stop();
        if (u) {
/*            t.cssAnimate({
                left: n + "px",
                opacity: 0,
                top: r + 20 + "px",
                scale: 1,
                rotate: 30
            }, {
                duration: 1,
                queue: false
            });*/
           /* if (e.browser.msie && e.browser.version < 9) {
                t.css({
                    visibility: "hidden"
                })
            }*/
        }
        setTimeout(function () {
            t.css({
                visibility: "visible",
                	opacity: 1,
                    left: n + "px",
                    top: r + "px",
                    scale: 1,
            });
            t.cssAnimate({
                opacity: 1,
                left: n + "px",
                top: r + "px",
                scale: 1,
                rotate: 30
            }, {
                duration: 0,
                queue: false
            })
        }, 100 + o * 5);
        if (t.parent().parent().data("ymax") < r + s) t.parent().parent().data("ymax", r + s)
    }

    function p(t, n) {
        var r = t.data("cellWidth");
        var i = t.data("cellHeight");
        var s = t.data("padding");
        var o = t.data("gridOffset");
        var u = t.data("maxRow");
        var a = 0;
        var f = 0;
        var p = 1;
        var d = 0;
        var v = 0;
        var m = [];
        t.each(function () {
            var o = e(this);
            var u = false;
            var g = o.width() - parseInt(t.data("gridOffset"), 0)-30;
            var b = Math.floor(g / r);
            o.css("position", "relative");
            var w = o.children("div");
            o.parent().data("ymax", 0);
            for (var E = 0; E < w.length; E++) {
                if (w.eq(E).hasClass("cell2x2")) {
                    if (d === b - 1) {
                        d = 0;
                        v++;
                        a = 0;
                        f += i + s;
                        p++
                    }
                    if (b > 1 && (c(m, d, v) || c(m, d + 1, v) || c(m, d + 1, v + 1) || c(m, d, v + 1))) {
                        E--
                    } else {
                        h(w.eq(E), a, f, r * 2 + s, i * 2 + s, E, n);
                        l(m, d, v);
                        l(m, d + 1, v);
                        l(m, d, v + 1);
                        l(m, d + 1, v + 1)
                    }
                    u = true
                } else if (w.eq(E).hasClass("cell2x1")) {
                    if (c(m, d, v) || c(m, d + 1, v) || b > 1 && d === b - 1) {
                        E--
                    } else {
                        h(w.eq(E), a, f, r * 2 + s, i, E, n);
                        l(m, d + 1, v)
                    }
                } else if (w.eq(E).hasClass("cell1x2")) {
                    if (c(m, d, v) || c(m, d, v + 1)) {
                        E--
                    } else {
                        h(w.eq(E), a, f, r, i * 2 + s, E, n);
                        l(m, d, v);
                        l(m, d, v + 1)
                    }
                    u = true
                } else {
                    if (c(m, d, v)) {
                        E--
                    } else {
                        h(w.eq(E), a, f, r, i, E, n)
                    }
                } if (p % b === 0) {
                    d = 0;
                    v++;
                    a = 0;
                    f += i + s;
                    u = false
                } else {
                    a += r + s;
                    d++
                }
                p++
            }
            var S = 0;
            if (p % b !== 1) {
                S = f + i + s
            } else {
                S = f + s
            } if (u) {
                S += i + s
            }
            e(this).parent().css("height", o.parent().data("ymax") + "px");
            a = 0;
            f = 0;
            p = 1;
            var T = 0;
            for (E = 0; E < w.length; E++) {}
        })
    }

    e.fn.extend({
        dropdown: function (t) {
            function r(t) {
                if (e.browser.msie && e.browser.version < 9) {
                    t.find("ul:first").css({
                        margin: "0px"
                    })
                }
                t.find("li").each(function (t) {
                    var n = e(this);
                    n.wrapInner('<div class="listitem" style="position:relative;left:0px;"></div>');
                    if (e.browser.msie && e.browser.version < 9) {
                        if (t == 0) n.css({
                            clear: "both",
                            "margin-top": "0px",
                            "padding-top": "0px"
                        });
                        n.css({
                            display: "none",
                            opacity: "0.0",
                            "vertical-align": "bottom",
                            top: "-20px"
                        });
                        if (e.browser.msie && e.browser.version < 8) {
                            n.css({
                                width: n.parent().parent().find(".buttonlight").width()
                            })
                        }
                    } else {
                        n.css({
                            display: "none",
                            opacity: "0.0",
                            top: "-20px"
                        })
                    }
                });

            }
            var n = {};
            t = e.extend({}, e.fn.dropdown.defaults, t);
            return this.each(function () {
                var n = t;
                var i = e(this);
                r(i)
            })
        }
    });

    e.fn.extend({
        portfolio: function (t) {
            var n = {
                gridOffset: 30,
                cellWidth: 100,
                cellHeight: 70,
                cellPadding: 10,
                gridOffset: 0,
                captionOpacity: 75,
               
                filterList: "#feature_sorting",
              
                urlDivider: "?",
               
            };
            t = e.extend({}, e.fn.portfolio.defaults, t);
            return this.each(function () {
                var n = e(this);
                var i = t;
                n.data("cellWidth", i.cellWidth);
                n.data("cellHeight", i.cellHeight);
                n.data("padding", i.cellPadding);
                n.data("gridOffset", i.gridOffset);
                n.data("gridOffset", i.gridOffset);
                n.data("captionOpacity", i.captionOpacity);
                p(n);
                f(n);
             //   e("body").find(i.filterList).dropdown({});
                r(n, i);
/*                e("body").find(i.backgroundHolder).tpbackground({
                    slideshow: i.backgroundSlideshow,
                    callback: "false",
                    cat: ""
                });*/

            });
        }
    });
})(jQuery);