 $( function() {
    run();

function run(){
var interval = setInterval(fun, 1000);
};


function fun(){
var task_id =parseInt( $(".task_id").text());
$.ajax({
   "url":"/status/?task_id="+task_id,
   "type":"get",
   "dataType":"json",
   "success":function(data){
    console.log(data);
    }
});


};

});
