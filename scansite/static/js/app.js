 $( function() {
    submit = $('#submit');
    $.ajax({
    "url":"/status",
    "type":"get",
    "dataType":"json",
    "success":function(data){
    console.log(data);
    }

    });

    submit.click(function(event){
        name = $('.task_name').val()
        choice=$('#id_choice').val();
        target=$('.target').val();
        deep = $('.deep_input').val();
        crsf = $.cookie('csrftoken');
        console.log(crsf);
        console.log(choice);
        console.log(target);
        $.post("",
            {'name':name,'target':target,'choice':choice, 'deep':deep,'csrfmiddlewaretoken': $.cookie('csrftoken') },
            function(data,status){
                console.log(data);
                var obj = jQuery.parseJSON(  data );
                if (obj.flag){
                    alert("success of create task!");
                    var taskid = obj.task_id
                    window.location.replace("/status/"+taskid);
                }else{
                    alert("error of task created!")
                }

            });
        event.stopPropagation();
        event.preventDefault();
    });

});
