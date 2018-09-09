## Digital Notice Board
Multipurpose and multiplatform GPU accelerated electronic notice board with a page to upload notice board images and an authorization system.  

There are 2 components to the system – Upload system and Client Application. 

### Upload System  
  Upload system has an image upload page and an admin page deployed in Tomcat server written in Java/Jersey to maintain the notice board images.
  - **Upload Screen**: Upload screen is open to everybody in the network and can upload any image that needs to be put on the notice board.  Screen captures name of the uploader, a description about the image and the image file which will be sent to the authorizer screen. 
  - **Admin Screen**: Authorizer screen will display a login form to login with admin username/password.  Once logged in, admin can see all the images currently available in the system with a “Delete” button to delete from getting displayed on notice board and an “Authorize” button which will be displayed only if the image is not authorized already.  Only Admin authorized images will be displayed in the notice board.

### Client Application
  The client system is the actual notice board getting displayed.  It is written using libGDX for GPU acceleration and multiplatform compatibility.  It will display all the authorized images as a slideshow with transition animations.
