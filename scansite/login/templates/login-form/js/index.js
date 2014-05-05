$( document ).ready(function() {
  var userInput = $('#username-input'),
      userWrap = $('.username-wrap'),
      user = $('#name');
      login = $('.password');

  userInput.keyup(function (event) {
      if (event.which == 13) {
        var name = $(this).val();
        user.text(name);
        userInput.addClass('hidden');
         userWrap.addClass('hidden');
        user.removeClass('hidden');
        user.parent().addClass('pw-active');
        $('.password').focus();
	$('.password').text("");
        return false; 
      }
  });


  login.click(function(event){
      var name = userInput.val();
        if (name.length == 0){
	return true;
	}
        user.text(name);
        userInput.addClass('hidden');
         userWrap.addClass('hidden');
        user.removeClass('hidden');
        user.parent().addClass('pw-active');
        $('.password').focus();
        return false; 
        
  });
  login.keyup(function (event) {
        if (event.which == 13) {
	if  ($('.password').val().length > 0)
	{
	    $('#form1').submit();
	    return false;
	}
	}
      
      });
  user.click(function (event) {
        user.addClass('hidden');
        user.parent().removeClass('pw-active'); 
        userInput.removeClass('hidden');
     userWrap.removeClass('hidden');
  });
});
