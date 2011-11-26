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
});