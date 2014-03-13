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
    
    var msg = jQuery(".registration-confirmation-messages").html();
    if (msg != "") {
        jQuery(".newsletter-confirmation-msg").show();
    }
    


});

    function multiSiteForms(groupId){
        $j('.multi-site-form').each(function (index) {
            action = $j(this).attr('action')+"&doAsGroupId="+groupId;
            $j(this).attr('action',action);
        });
    }
    
    
var $ = jQuery;