var FragBuilder = (function() {
    var applyStyles = function(element, style_object) {
        for (var prop in style_object) {
            element.style[prop] = style_object[prop];
        }
    };
    
    var setonclick = function(element, style_object) {
        for (var value in style_object) {
            element.onclick = value;
        }
    };
    var generateFragmentFromJSON = function(json) {
        var tree = document.createDocumentFragment();
           json.forEach(function(obj) {
            if (!('tagName' in obj) && 'textContent' in obj) {
                tree.appendChild(document.createTextNode(obj['textContent']));
            } else if ('tagName' in obj) {
                var el = document.createElement(obj.tagName);
                delete obj.tagName;
                for (part in obj) {
                    var val = obj[part];
                    switch (part) {
                    case ('textContent'):
                        el.appendChild(document.createTextNode(val));
                        break;
                    case ('style'):
                        applyStyles(el, val);
                        break;
                    case ('click'):
                    	el.setAttribute("onclick",val);   
                        break;
                    case ('childNodes'):
                        el.appendChild(generateFragmentFromJSON(val));
                        break;
                    case ('src'):
                    	el.src = val;
                    	break;
                    case ('href'):
                    	el.setAttribute("href",val);  
                    	el.setAttribute("target","_blank");  
                        break;
                    case ('id'):
                    	el.setAttribute("id",val);  
                    	break;
                    case ('subDim'):
                    	el.setAttribute("subDim",val);  
                    	break;
                    case ('word'):
                    	el.setAttribute("word",val);  
                    	break;
                    default:
                        if (part in el) {
                            el[part] = val;
                        }
                        break;
                    }
                }
                tree.appendChild(el);
            } else {
                throw "Error: Malformed JSON Fragment";
            }
        });
        return tree;
    };
    var generateFragmentFromString = function(HTMLstring) {
        var div = document.createElement("div"),
            tree = document.createDocumentFragment();
        div.innerHTML = HTMLstring;
        while (div.hasChildNodes()) {
            tree.appendChild(div.firstChild);
        }
        return tree;
    };
    return function(fragment) {
        if( typeof fragment === 'string' ) {
            return generateFragmentFromString( fragment );
        } else {
            return generateFragmentFromJSON( fragment );
        }
    };
}());
