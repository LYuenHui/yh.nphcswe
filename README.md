# yh.nphcswe

## Background
--------------------
Language : Java  
Database : embedded H2 Database  
Framework : SpringBoot


## **BUILD**
----------
1.) Clone the repo
````
git clone https://github.com/LYuenHui/yh.nphcswe.git
````
2.) Go into yh.nphcswe folder  
3.) Command to start application
````
mvn spring-boot:run
````

## **APIs**
----------

Function : Upload CSV   
Method :Post   
URL : http://localhost:8080/users/upload 

Function : Create   
Method :Post   
URL : http://localhost:8080/users

Function : Fetch all employees with salary range filter  
Method :Get   
URL : http://localhost:8080/users?minSalary=300&maxSalary=500

Function : Get Employees    
Method :Get   
URL : http://localhost:8080/users/{id}

Function : Update Employees    
Method :Put/Patch   
URL : http://localhost:8080/users/{id}

Function : Delete Employees  
Method :Delete   
URL : http://localhost:8080/users/{id}






