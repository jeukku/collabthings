CollabThings
============

Collabthings is a platform to store, share in a designs of machines and constructions, scripts and instructions how those are manufactured and maintained. Everything piece is linked together and everything is automatically shared through waazdoh -network. You can take any design, edit and publish it.

The idea is to define how to build for example a hair dryer, what parts are needed and how they are put together. What tools and what kind of robot is needed to assembly it. If somebody wants to make a better design or update the software that is needed, anyone gets those updates. If you want to create something else using some of the parts of hair dryer, that is great because we already know how to build those parts and how to use them.

The platform is designed so that, it should handle cities as well as small parts. With some work you should be able to take a city and zoom in to some bolt somewhere.

https://www.youtube.com/watch?v=1R0Mu75DU3Q


Trello
======

https://trello.com/b/A0jQVmXl/collabthings

Development Environment
=======================

Install Virtualbox, maven, eclipse and vagrant.

Clone git repositories as Eclipse projects:

development branches:
- git@github.com:CollabThings/waazdoh.service.git
- git@github.com:CollabThings/org.collabthings.web.git
- git@github.com:CollabThings/collabthings.git
- git@github.com:CollabThings/collabthings.ogl.git
- git@github.com:CollabThings/collabthings.swt.git

run "mvn package" in everey project.

master branch:
- git@github.com:CollabThings/collabthings.env.git

run "vagrant up" in collabthings.env/vagrant. This takes about 10min.

checkout that service is running http://192.168.33.20:8080/service/admin/basicchecks
checkout that website is runnung http://192.168.33.20:8080/

run Application org.collabthings.swt.AppLauncher with parameters -Dwaazdoh.prefix=ct_swt_test -Dservice.url=http://192.168.33.20:8080/service
