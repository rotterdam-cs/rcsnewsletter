$j = jQuery.noConflict();

jQuery(document).ready(function() {                
    $j("#unregister-link").click(function() {
       $j('.unregister-button').toggle();
       $j('.register-button').toggle();
       $j('#unregister-link').toggle();
       $j('#register-link').toggle();
       $j('.infohidden').toggle();
    });

    $j("#register-link").click(function() {
       $j('.unregister-button').toggle();
       $j('.register-button').toggle();
       $j('#unregister-link').toggle();
       $j('#register-link').toggle();
       $j('.infohidden').toggle();
    });
    
    var activationkey = $j(".activationkey").val();
    $j(".activationkey").hide();
    if (activationkey != "") {
       $j('.registration-sucess').show();
       
//       $j('.register-button').hide();
//       $j('#unregister-link').hide();
//       $j('#register-link').hide();
//       $j('.infohidden').hide();
//       $j('.infonohidden').hide();
    }
    
});