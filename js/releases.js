$(document).ready(function(){
  var isStable, depedency, sbtCode, loadCSS, resolver = 'resolvers += Opts.resolver.sonatypeSnapshots';

  loadCSS = function(href) {
    var cssLink = $("<link>");
    $("head").append(cssLink); //IE hack: append before setting href
    cssLink.attr({
      rel:  "stylesheet",
      type: "text/css",
      href: href
    });
  };

  isStable = function(v) {
    return /^[\d\.\-]+$/i.test(v);
  };

  depedency = function(v) {
    return 'libraryDependencies += "gr.jkl" %% "uid" % "' + v + '"';
  };

  sbtCode = function(v) {
    return '<pre><code class="prettyprint lang-scala">' + 
      (isStable(v) ? '' : resolver + '\n\n') + depedency(v) + '</code></pre>';
  };

  loadCSS('css/prettify.css');

  $.ajaxSetup({
    cache: true
  });

  $.getScript('js/prettify/prettify.js', function(){
    $.getScript('js/prettify/lang-scala.js', function(){
      prettyPrint();
    })
  });

  $.get('releases.xml', function(data) {
    $(data).find('release').each(function(){
      var r, v, so, sv, h, a;
      r = $(this);
      v = r.find('version').text();
      so = $.makeArray(r.find('value'));
      sp = $.map(so, function(o){
        return '<em>' + $(o).text() + '</em>';
      });
      h = $('<div />');
      h.append('<h2>uid ' + v + '</h2>');
      h.append('<p>uid ' + v + ' is built for Scala ' + sp.join(', ') + 
        '. In order to use uid ' + v + ', include the following in your sbt build:</p>');
      h.append(sbtCode(v));
      a = isStable(v) ? '#stables' : '#snapshots';
      h.appendTo($(a));
    });
  }, 'xml');
});
