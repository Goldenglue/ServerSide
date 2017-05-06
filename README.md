# ServerSide
Server side of client-server uni application


Commands: 
-dc - disconnect from server                                                          
-sobjc - cannot be executed directly from server, identifies that client sent object  
-sobjs - send someVector to server in JSON format                                     
-clrc - cannot be executed directly from server, identifies that client requested to clear storage of objects on server                
-clrs - clears storage of objects on client                                           
-vecsc - cannot be executed directly from server, identifies that client requested size of objects storage on server                
-vecss - requests size of objects storage on client                                  
-gobjc - cannot be executed directly from server, identifies that client requested object with given number ex:-gobjs3                  
-gobjs - request of object with given number ex: -gobjc3                              
-robj - cannot be executed directly from server, identifies that client sent requested object by command -gobjc                        

However, if you do try to use commands that shouldn't be used from server, you'll get an error cause no protection yet.
