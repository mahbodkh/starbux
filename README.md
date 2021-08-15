# Getting Started

### Features:

* Springboot.
* H2.
* Caching.
* Flyway Migration.
* Slf4j for logs.  
* Lombok.
* Swagger.
* Dockerfile.
* Deploy Heroku 
* All services are decoupled design [`SOLID`]
* All services are injected with maximum capacity beans.
* _Ready for monitoring. [actuator, prometheus, influxDb]_

### Start:

The following guides illustrate how to use some features concretely:

Run:
`$ mvn spring-boot:run`

url: `http://localhost` Port: `8008`
Heroku: `https://bestseller-starbux-stage.herokuapp.com`

###Swagger:
Address: 
`https://bestseller-starbux-stage.herokuapp.com/swagger-ui.html`

### Docker:

`docker-compose -f src/main/docker/docker-compose.yml up -d`


### APIs doc:

| Http Method   | Path                                    | Description                 |
|-------------	|---------------------------------------- |---------------------------	|
| GET           | /v1/user/{id}/                          | Load user by {id}           |
| POST          | /v1/user/admin/create/                  | Create user by admin        |
| GET           | /v1/user/admin/all/                     | Load all users by admin     |
| PUT           | /v1/user/admin/{id}/edit/               | Edit user by {id} by admin    |
| PUT           | /v1/user/admin/{id}/delete/             | Delete safe user by admin        |
| DELETE        | /v1/user/admin/{id}/delete/             | Delete completely by admin    |
| PUT           | /v1/user/admin/{id}/ban/                | Ban the user by admin        |
| PUT           | /v1/user/admin/{id}/frozen/             | Frozen the user by admin        |
|            	|                                         |                             	|
| GET           | /v1/product/{id}/                       | Load the product by user        |
| GET           | /v1/product/all/                        | Load All product by user     |
| POST          | /v1/product/admin/create/               | Create product by admin        |
| PUT           | /v1/product/admin/{id}/edit/            | Edit product by admin        |
| DELETE        | /v1/product/admin/{id}/delete/          | Delete completely by admin    |
|               |                                         |                               |
| POST          | /v1/cart/create/{id}/user/              | Create by user {id}      |
| GET           | /v1/cart/{id}/                          | Load by cart {id}         |
| PUT           | /v1/cart/admin/{id}/user/               | Load by user {id}       |
| PUT           | /v1/cart/admin/{id}/delete/             | Delete cart by cart {id}       |
|               |                                         |                               |
| GET           | /v1/order/{id}/user/                    | Load by user {id}         |
| GET           | /v1/order/create/{id}/user/             | Create by user {id}     |
| GET           | /v1/order/admin/{id}/delete/            | Delete by order {id}  |
|               |                                         |                               |
| GET           | /v1/report/admin/product/top/           | Report by top side product         |
| GET           | /v1/report/admin/                       | Load all the currencies       |
|               |                                         |                               |
|               |                                         |                               |



