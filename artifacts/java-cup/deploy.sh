#!/bin/bash

mvn deploy:deploy-file -Durl=sftp://web.sourceforge.net:/home/project-web/emustudio/htdocs/repository -DrepositoryId=emustudio-repository -Dfile=./java-cup-0.11a.jar -DpomFile=./pom.xml

