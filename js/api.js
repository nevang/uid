$(document).ready(function(){
  var isStable, apiLink

  isStable = function(v) {
    return /^[\d\.\-]+$/i.test(v)
  }

  apiLink = function(v) {
    return 'api/' + (isStable(v) ? v : 'latest') + '/gr/jkl/uid/package.html'
  }

  $.get('releases.xml', function(data) {
    $(data).find('release').each(function(){
      var v, h, a
      v = $(this).find('version').text()
      h = $('<li />')
      h.append('<a href="' + apiLink(v) + '">ScalaDoc for uid ' + v + '</a>')
      a = isStable(v) ? '#stable-release' : '#snapshot-release'
      h.appendTo($(a))
    })
  }, 'xml')
})
