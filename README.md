# yh.nphcswe

## Background
Language : Java  
Database : embedded H2 Database  
Framework : SpringBoot


## **Build and Run**
1.) Clone the repo
````
git clone https://github.com/LYuenHui/yh.nphcswe.git
```` 
2.) Command to start application
````
mvn spring-boot:run
````

## **APIs**

**Upload API**   
POST http://localhost:8080/users/upload 

**Create API**  
POST http://localhost:8080/users

**Fetch all API**  
GET http://localhost:8080/users  
Parameter :  
  -minSalary(decimal), default value = 0.
  -maxSalary(decimal), default value = 4000.

**Get API**    
GET http://localhost:8080/users/{id}

**Update API**    
PUT/PATCH http://localhost:8080/users/{id}

**Delete API**    
DELETE http://localhost:8080/users/{id}






