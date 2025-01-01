Document Variables
================================
_CONTEXT-ROOT_=https://localhost:12100/
_ENV_=local|dev

API
================================
Swagger UI
--------------------------------
_CONTEXT-ROOT_/swagger-ui.html

Swagger JSON
--------------------------------
_CONTEXT-ROOT_/v2/api-docs


Build
================================
`mvn -Plocal clean package`

Local Development
================================
1. switch maven profile to 'local': `mvn -Plocal`
1. see Build
1. either start with spring-boot maven plugin `mvn -Plocal clean spring-boot:run`
1. or spring boot app `LoginApplication` in eclipse/jidea with program arguments `--spring.config.name=application,infra,passwords`

Local Run
================================
`java -jar ocp-leadtool-be-*.jar`


Install
================================
1. Perform any necessary backup of your installation destination directory.
1. Delete old `cart-*.jar` from destination.
1. Copy new `cart-*.jar` from distribution (Nexus) to destination.
1. Extract with overwrite enabled the configuration package `cart-_ENV_.zip` to destination.


Run
================================
1. Change working directory to installation destination.
1. Start application: `./bin/start.sh`
1. Stop application: `./bin/stop.sh`
