# README - Login/Register REST API

## Model Layer
### In this layer, we will create User and Role JPA entities and establish MANY-to-MANY relationships between them. JPA annotations are used to establish MANY-to-MANY relationships between User and Role entities.
### Here are JPA Entities that are used in the login/register system:

#### - UserJPA

This is where we specify the details of the users table that will be created in the database. We also specify the details of the columns that will be created in the table.

###### ```@Data``` is a convenient shortcut annotation that bundles the features of ```@ToString```, ```@EqualsAndHashCode```, ```@Getter``` / ```@Setter``` and ```@RequiredArgsConstructor``` together: In other words, ```@Data``` generates all the boilerplate that is normally associated with simple POJOs (Plain Old Java Objects) and beans: getters for all fields, setters for all non-final fields, and appropriate toString, equals and hashCode implementations that involve the fields of the class, and a constructor that initializes all final fields, as well as all non-final fields with no initializer that have been marked with @NonNull, in order to ensure the field is never null.
###### ```@Entity``` -> This annotation is used to specify that the class is an entity and is mapped to a database table.

    You define the entity with an annotation @Entity
    It is necessary a primary key (PK) with @ID
    The value of this PK is generated automatically with this annotation @GeneratedValue with params AUTO and IDENTITY
    @Table to define explicitly the name of the table.
    @Column to define the name of the column.

###### ```@Table``` -> This annotation is used to specify the details of the table that will be created in the database.

#### - RoleJPA

This is where we specify the details of the roles table that will be created in the database. We also specify the details of the columns that will be created in the table.


## Repository Layer
### A repository is a mechanism for encapsulating storage, retrieval, and search behavior which emulates a collection of objects.
### Here are Repositories that are used in the login/register system:

#### - UserRepository

This is where we define the methods that will be used to perform CRUD operations on the users table in the database.

### - RoleRepository

This is where we define the methods that will be used to perform CRUD operations on the roles table in the database.

## Service Layer
### The service layer is the place where we write the logic of loading user details by name or email from the database.
#### There is a ```CustomUserDetailsService``` which implements ```UserDetailsService``` interface. This interface has a method called ```loadUserByUsername()``` which is used to load user details by username. We will override this method to load user details from the database.
#### Spring Security uses the ```UserDetailsService``` interface, which contains the ```loadUserByUsername(String username)``` method to lookup UserDetails for a given username.
#### The ```UserDetails``` interface represents an authenticated user object and Spring Security provides an out-of-the-box implementation of `org.springframework.security.core.userdetails.User.` 

## Spring Security Configuration

### Spring Security is a powerful and highly customizable authentication and access-control framework. It is the de-facto standard for securing Spring-based applications.
#### We are using the Spring security provided `BCryptPasswordEncoder` to encode the password. The password is encoded using the BCrypt hashing function.

## Payload Layer
### Here are created DTO classes to transfer data/payload between the client and the server.
#### -Login
#### -Register

## Controller Layer
### Here are created REST API endpoints to handle the login and register requests.
##### The `@Autowired` annotation provides more fine-grained control over where and how autowiring should be accomplished. The `@Autowired` annotation can be used to autowire bean on the setter method just like `@Required` annotation, constructor, a property or methods with arbitrary names and/or multiple arguments.
##### `@RequestMapping`, `@GetMapping` and `@PostMapping` are Spring annotations that are used to map web requests to Spring Controller methods.