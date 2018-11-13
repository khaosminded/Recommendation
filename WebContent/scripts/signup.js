(function() {  
  
  function init() {
    // register event listeners
    document.querySelector('#submit-btn').addEventListener('click', signup);
  }
  
function signup() {
    var username = document.querySelector('#username').value;
    var password = document.querySelector('#password').value;
    password = md5(username + md5(password));
    var firstname = document.querySelector('#first-name').value;
    var lastname = document.querySelector('#last-name').value;
    // The request parameters
    var url = './signup';
    var req = JSON.stringify({
      user_id : username,
      password : password,
      first_name : firstname,
      last_name : lastname
    });
    //create request
    var xhr = new XMLHttpRequest();
    xhr.open("POST", url, true);

    xhr.onload = function() {
      if (xhr.status === 200) {
    	  signupSuccess(xhr);
      } else {
        signupError(xhr);
      }
    };

    xhr.onerror = function() {
      console.error("The request couldn't be completed.");
      signupError(xhr);
    };

    
  xhr.setRequestHeader("Content-Type",
                       "application/json;charset=utf-8");
  xhr.send(req);
    
  }

  function signupError(response) {
	  var output = JSON.parse(xhr.responseText);
    document.querySelector('#signup-error').innerHTML = output.status;
  }
  function signupSuccess(response) {
	  var output = JSON.parse(response.responseText);
	  document.querySelector('#signup-error').innerHTML = output.status;
	  if (output.status === "OK") {
		//redirect to login
		  window.location.href="index.html"; 
	  }
  }
  
  init();
})()




