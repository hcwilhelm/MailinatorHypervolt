# Hypervolt Malinator

This is my solution to the Hypervolt tech task [Mailinator Clone](https://github.com/fauna/exercises/blob/main/backend.md)

## Tech Stack

I choosed to use ZIO 2.0 as the main framework to implement this task.

- ZIO 2.0
- ZIO-HTTP
- ZIO-JSON
- ZIO-QUILL (with H2 in memory DB)

The server is running fully concurrent any race conditions are handled by the transactional nature of H2 in-memory DB running. Also a
message garbage collector is running concurrently and deletes messages which are older than the retention time.

## Running test

```sbt test```

## Running the server

```sbt run```

## Calling endpoints

I am using [HTTPIE](https://httpie.io/) here in the examples but of course feel free to use ```curl```
I assume the server is started up and ready to receive requests.

### Health Check

``http GET localhost:8080/health``

### Create random Mailbox

``http POST localhost:8080/mailboxes``

### Create named Mailbox

``echo -n '"foobar"' | http POST localhost:8080/mailboxes``

### Query Mailboxes 

``http GET localhost:8080/mailboxes``

### Mailbox

```http DELETE localhost:8080/mailboxes/foobar```

### Create Message

``http POST localhost:8080/mailboxes/foobar/messages sender="FooSender" subject="WTF" message="A very long story"``

### Query all Messages for a mailbox

``http GET localhost:8080/mailboxes/foobar/messages``

### Query a Messages my Id 

``http GET localhost:8080/mailboxes/foobar/messages/50a055ac-f81b-48ef-b69c-ddb7365020bf``

### Delete a Message my Id

``http DELETE localhost:8080/mailboxes/foobar/messages/08e5a936-6fe7-419b-a35d-594b4be2bd6a``


## Things to improve 

- Logging
- Configuration loading implemented as a ZIO Service ( application.conf )
- Implement proper typed error handling
- Implement Pagination
- Implement a complete test suite ( tests are missing for MessageRepo, MailinatorApp and GarbageCollector)
- Implement Benchmarks
- Proper API documentation using OpenAPI ( formally knows as Swagger)
- Lots of other things :P

## Obstacles 

As I am not so experienced in using Quill, although I like the idea of compile time generated queries, 
I should have used Doobie or maybe ZIO-SQL ;(

## Sideffects 

I might have discovered a Quill bug and they asked me to open a ticket for that. I will do that in the next 
couple of days to support the opensource community with my feedback. 


