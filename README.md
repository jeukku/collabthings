CollabThings
============

Collabthings is a platform to store, share in a designs of machines and constructions, scripts and instructions how those are manufactured and maintained. Everything piece is linked together and everything is automatically shared through waazdoh -network. You can take any design, edit and publish it.

The idea is to define how to build for example a hair dryer, what parts are needed and how they are put together. What tools and what kind of robot is needed to assembly it. If somebody wants to make a better design or update the software that is needed, anyone gets those updates. If you want to create something else using some of the parts of hair dryer, that is great because we already know how to build those parts and how to use them.

The platform is designed so that, it should handle cities as well as small parts. With some work you should be able to take a city and zoom in to some bolt somewhere.

https://www.youtube.com/watch?v=1R0Mu75DU3Q

Development
===========

Run waazdoh and waazdoh.wab services locally:
 - git clone git@github.com:jeukku/waazdoh.service.git
 - git clone git@github.com:jeukku/waazdoh.web.git
 - mvn appengine:devserver in both paths
 - UI: git clone git@github.com:jeukku/collabthings.swt.git and run org.collabthings.swt.AppLauncher with variable -Dservice.url=http://localhost:18099/waazdoh  

