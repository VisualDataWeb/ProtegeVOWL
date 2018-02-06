ProtégéVOWL 
===========

Source code of the VOWL plugin for Protégé.

Note that ProtégéVOWL has some knwon has some known bugs and does not implement all visual elements defined in the VOWL specification.
A more complete implementation of VOWL is provided by the web application [WebVOWL] (https://github.com/VisualDataWeb/WebVOWL).

See http://vowl.visualdataweb.org for more information on ProtégéVOWL and WebVOWL.

## Developer Setup

### Requirements
Java JDK is required for the ProtégéVOWL build.

### Eclipse Setup
There are several options if you want to use Eclipse as IDE. You need to select the 
``Import`` within Eclipse and select the ``Maven``, ``Existing Maven Projects`` option and select the ProtégéVOWL Git Repository. Eclipse will automatically suggest to import ``pom.xml``
 
### Maven Build
Some steps are required to build ProtégéVOWL with Maven. If you use Eclipse you need to run the ``pom.xml`` as Maven build and you may need to select JDK as Alternate JRE.
In addition you need to add the goals ``clean compile package``. You will find the compiled packaged within the target folder.

### Copy to Protege
To copy the build result to your Protégé installation you need to add ``install`` to your maven goals.
In addition you have to add the parameter ``protege.home`` which leads to your Protégé installation.
If you remove the ``install`` goal the build result is not copied to Protégé.